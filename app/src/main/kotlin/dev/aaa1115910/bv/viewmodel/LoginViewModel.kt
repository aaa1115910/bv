package dev.aaa1115910.bv.viewmodel

import android.graphics.BitmapFactory
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.aaa1115910.biliapi.BiliPassportApi
import dev.aaa1115910.bv.BVApp
import dev.aaa1115910.bv.repository.UserRepository
import dev.aaa1115910.bv.util.toast
import io.github.g0dkar.qrcode.QRCode
import io.ktor.util.date.toJvmDate
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.Timer
import java.util.TimerTask


class LoginViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    var state by mutableStateOf(LoginState.Ready)
    private val logger = KotlinLogging.logger { }
    var loginUrl by mutableStateOf("")
    val isLogin get() = userRepository.isLogin
    var qrImage by mutableStateOf(ImageBitmap(1, 1, ImageBitmapConfig.Argb8888))
    private var key = ""

    private var timer = Timer()

    fun requestQRCode() {
        state = LoginState.Ready
        logger.info { "Request login qr code" }
        viewModelScope.launch(Dispatchers.Default) {
            runCatching {
                state = LoginState.RequestingQRCode
                val response = BiliPassportApi.getQRUrl()
                loginUrl = response.data.url
                key = response.data.qrcodeKey
                logger.info { "Get login request code url: ${response.data.url}" }
                generateQRImage()
                runCatching { timer.cancel() }
                timer = Timer()
                timer.schedule(object : TimerTask() {
                    override fun run() {
                        viewModelScope.launch {
                            checkLoginResult()
                        }
                    }
                }, 1000, 1000)
            }.onFailure {
                withContext(Dispatchers.Main) {
                    it.message?.toast(BVApp.context)
                }
                state = LoginState.Error
                logger.error { "Get login request code url failed: ${it.stackTraceToString()}" }
                timer.cancel()
            }
        }
    }

    suspend fun checkLoginResult() {
        logger.info { "Check for login result" }
        runCatching {
            val response = BiliPassportApi.loginWithQR(key)
            require(response.code == 0) { "Check qr state failed" }
            when (response.data.code) {
                0 -> {
                    val cookies = response.cookies
                    userRepository.setLoginData(
                        uid = cookies.find { it.name == "DedeUserID" }?.value?.toLong()
                            ?: throw IllegalArgumentException("Cookie DedeUserID not found"),
                        uidCkMd5 = cookies.find { it.name == "DedeUserID__ckMd5" }?.value
                            ?: throw IllegalArgumentException("Cookie DedeUserID__ckMd5 not found"),
                        sid = cookies.find { it.name == "sid" }?.value
                            ?: throw IllegalArgumentException("Cookie sid not found"),
                        biliJct = cookies.find { it.name == "bili_jct" }?.value
                            ?: throw IllegalArgumentException("Cookie bili_jct not found"),
                        sessData = cookies.find { it.name == "SESSDATA" }?.value
                            ?: throw IllegalArgumentException("Cookie SESSDATA not found"),
                        expiredDate = cookies.first().expires?.toJvmDate()
                            ?: throw IllegalArgumentException("Cookie expires date not found")
                    )
                    state = LoginState.LoginSuccess
                }

                86101 -> {
                    logger.info { "Waiting to scan" }
                    state = LoginState.WaitToScan
                }

                86090 -> {
                    logger.info { "Waiting to confirm" }
                    state = LoginState.WaitToConfirm
                }

                86038 -> {
                    logger.info { "QR expired" }
                    state = LoginState.QRExpired
                    timer.cancel()
                }

                else -> {
                    logger.info { "Get unknown response code: $response" }
                }
            }

        }.onFailure {
            if (it is CancellationException) {
                logger.info { "Timer job cancelled" }
                return@onFailure
            }
            withContext(Dispatchers.Main) {
                it.message?.toast(BVApp.context)
            }
            state = LoginState.Error
            logger.error { "Check qr state failed: ${it.stackTraceToString()}" }
        }
    }

    private fun generateQRImage() {
        val output = ByteArrayOutputStream()
        QRCode(loginUrl).render().writeImage(output)
        val input = ByteArrayInputStream(output.toByteArray())
        qrImage = BitmapFactory.decodeStream(input).asImageBitmap()
        logger.info { "Generated qr image" }
    }
}

enum class LoginState {
    Ready, RequestingQRCode, WaitToScan, WaitToConfirm, QRExpired, LoginSuccess, Error
}