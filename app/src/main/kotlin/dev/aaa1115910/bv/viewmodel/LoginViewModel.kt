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
import dev.aaa1115910.biliapi.http.BiliPassportHttpApi
import dev.aaa1115910.bv.BVApp
import dev.aaa1115910.bv.repository.UserRepository
import dev.aaa1115910.bv.util.fError
import dev.aaa1115910.bv.util.fInfo
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
    private var loginUrl by mutableStateOf("")
    var qrImage by mutableStateOf(ImageBitmap(1, 1, ImageBitmapConfig.Argb8888))
    private var key = ""

    private var timer = Timer()

    fun requestQRCode() {
        state = LoginState.Ready
        logger.fInfo { "Request login qr code" }
        viewModelScope.launch(Dispatchers.Default) {
            runCatching {
                state = LoginState.RequestingQRCode
                val responseData = BiliPassportHttpApi.getQRUrl().getResponseData()
                loginUrl = responseData.url
                key = responseData.qrcodeKey
                logger.fInfo { "Get login request code url" }
                logger.info { responseData.url }
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
                logger.fError { "Get login request code url failed: ${it.stackTraceToString()}" }
                timer.cancel()
            }
        }
    }

    suspend fun checkLoginResult() {
        logger.fInfo { "Check for login result" }
        runCatching {
            val (response, cookies) = BiliPassportHttpApi.loginWithQR(key)
            val responseData = response.getResponseData()
            when (responseData.code) {
                0 -> {
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
                    logger.fInfo { "Login success" }
                    state = LoginState.LoginSuccess
                    timer.cancel()
                }

                86101 -> {
                    logger.fInfo { "Waiting to scan" }
                    state = LoginState.WaitToScan
                }

                86090 -> {
                    logger.fInfo { "Waiting to confirm" }
                    state = LoginState.WaitToConfirm
                }

                86038 -> {
                    logger.fInfo { "QR expired" }
                    state = LoginState.QRExpired
                    timer.cancel()
                }

                else -> {
                    logger.fInfo { "Get unknown response code: $response" }
                }
            }

        }.onFailure {
            if (it is CancellationException) {
                logger.fInfo { "Timer job cancelled" }
                return@onFailure
            }
            withContext(Dispatchers.Main) {
                it.message?.toast(BVApp.context)
            }
            state = LoginState.Error
            logger.fError { "Check qr state failed: ${it.stackTraceToString()}" }
        }
    }

    private fun generateQRImage() {
        val output = ByteArrayOutputStream()
        QRCode(loginUrl).render().writeImage(output)
        val input = ByteArrayInputStream(output.toByteArray())
        qrImage = BitmapFactory.decodeStream(input).asImageBitmap()
        logger.fInfo { "Generated qr image" }
    }
}

enum class LoginState {
    Ready, RequestingQRCode, WaitToScan, WaitToConfirm, QRExpired, LoginSuccess, Error
}