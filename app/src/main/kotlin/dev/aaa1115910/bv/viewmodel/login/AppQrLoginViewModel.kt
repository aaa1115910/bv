package dev.aaa1115910.bv.viewmodel.login

import android.graphics.BitmapFactory
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.aaa1115910.biliapi.entity.login.QrLoginState
import dev.aaa1115910.biliapi.repositories.LoginRepository
import dev.aaa1115910.bv.BVApp
import dev.aaa1115910.bv.entity.AuthData
import dev.aaa1115910.bv.repository.UserRepository
import dev.aaa1115910.bv.util.fError
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.timeTask
import dev.aaa1115910.bv.util.toast
import io.github.g0dkar.qrcode.QRCode
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.Timer

class AppQrLoginViewModel(
    private val userRepository: UserRepository,
    private val loginRepository: LoginRepository
) : ViewModel() {
    var state by mutableStateOf(QrLoginState.Ready)
    private val logger = KotlinLogging.logger { }
    private var loginUrl by mutableStateOf("")
    var qrImage by mutableStateOf(ImageBitmap(1, 1, ImageBitmapConfig.Argb8888))
    private var key = ""

    private var timer = Timer()

    fun requestQRCode() {
        state = QrLoginState.Ready
        logger.fInfo { "Request login qr code" }
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                withContext(Dispatchers.Main) { state = QrLoginState.RequestingQRCode }
                val qrLoginData = loginRepository.requestAppQrLogin()
                loginUrl = qrLoginData.url
                key = qrLoginData.key
                logger.fInfo { "Get login request code url" }
                logger.info { qrLoginData.url }
                withContext(Dispatchers.Main) { generateQRImage() }
                runCatching { timer.cancel() }
                timer = timeTask(1000, 1000, "check qr login result") {
                    viewModelScope.launch {
                        checkLoginResult()
                    }
                }
            }.onFailure {
                withContext(Dispatchers.Main) {
                    it.message?.toast(BVApp.context)
                    state = QrLoginState.Error
                }
                logger.fError { "Get login request code url failed: ${it.stackTraceToString()}" }
                timer.cancel()
            }
        }
    }

    fun cancelCheckLoginResultTimer() {
        timer.cancel()
    }

    private suspend fun checkLoginResult() {
        logger.fInfo { "Check for login result" }
        runCatching {
            val qrLoginResult = loginRepository.checkAppQrLoginState(key)
            withContext(Dispatchers.Main) { state = qrLoginResult.state }
            when (state) {
                QrLoginState.WaitingForScan -> {
                    logger.fInfo { "Waiting to scan" }
                }

                QrLoginState.WaitingForConfirm -> {
                    logger.fInfo { "Waiting to confirm" }
                }

                QrLoginState.Expired -> {
                    logger.fInfo { "QR expired" }
                    timer.cancel()
                }

                QrLoginState.Success -> {
                    logger.fInfo { "Login success" }

                    val authData = AuthData(
                        uid = qrLoginResult.cookies!!.dedeUserId,
                        uidCkMd5 = qrLoginResult.cookies!!.dedeUserIdCkMd5,
                        sid = qrLoginResult.cookies!!.sid,
                        biliJct = qrLoginResult.cookies!!.biliJct,
                        sessData = qrLoginResult.cookies!!.sessData,
                        tokenExpiredData = qrLoginResult.cookies!!.expiredDate.time,
                        accessToken = qrLoginResult.accessToken!!,
                        refreshToken = qrLoginResult.refreshToken!!
                    )
                    userRepository.addUser(authData)

                    timer.cancel()
                }

                else -> {
                    logger.fInfo { "This state should not be here: $state" }
                }
            }
        }.onFailure {
            if (it is CancellationException) {
                logger.fInfo { "Timer job cancelled" }
                return@onFailure
            }
            withContext(Dispatchers.Main) {
                it.message?.toast(BVApp.context)
                state = QrLoginState.Error
            }
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
