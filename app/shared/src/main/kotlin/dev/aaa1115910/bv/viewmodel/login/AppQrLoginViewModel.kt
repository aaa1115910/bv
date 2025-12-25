package dev.aaa1115910.bv.viewmodel.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.aaa1115910.biliapi.entity.login.QrLoginState
import dev.aaa1115910.biliapi.repositories.LoginRepository
import dev.aaa1115910.bv.BVApp
import dev.aaa1115910.bv.entity.AuthData
import dev.aaa1115910.bv.repository.UserRepository
import dev.aaa1115910.bv.util.BlacklistUtil
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.fError
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.timeTask
import dev.aaa1115910.bv.util.toast
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.annotation.KoinViewModel
import java.util.Timer

@KoinViewModel
class AppQrLoginViewModel(
    private val userRepository: UserRepository,
    private val loginRepository: LoginRepository
) : ViewModel() {
    var state by mutableStateOf(QrLoginState.Ready)
    private val logger = KotlinLogging.logger { }
    var loginUrl by mutableStateOf("")
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
                runCatching { timer.cancel() }
                timer = timeTask(1000, 1000, "check qr login result") {
                    viewModelScope.launch(Dispatchers.IO) {
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

                    timer.cancel()
                    BlacklistUtil.checkUid(Prefs.uid)
                    userRepository.addUser(authData)
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
}
