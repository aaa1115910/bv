package dev.aaa1115910.bv.viewmodel.login

import android.webkit.JavascriptInterface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.aaa1115910.biliapi.http.util.generateBuvid
import dev.aaa1115910.biliapi.repositories.BvLoginRepository
import dev.aaa1115910.biliapi.repositories.SendSmsState
import dev.aaa1115910.bv.BVApp
import dev.aaa1115910.bv.repository.UserRepository
import dev.aaa1115910.bv.util.fDebug
import dev.aaa1115910.bv.util.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import java.net.URL

class SmsLoginViewModel(
    private val userRepository: UserRepository,
    private val bvLoginRepository: BvLoginRepository
) : ViewModel() {
    private val logger = KotlinLogging.logger { }
    var sendSmsState by mutableStateOf(SendSmsState.Ready)
    var recaptchaUrl by mutableStateOf("")

    private var phone: Long = 0
    private val loginSessionId = bvLoginRepository.generateLoginSessionId()
    private var recaptchaToken: String? = null
    private var geetestChallenge: String? = null
    private var geetestValidate: String? = null
    private var geetestGt: String? = null
    private val buvid = generateBuvid()
    private var captchaKey: String? = null

    suspend fun sendSms(phone: Long) {
        this.phone = phone
        logger.info { "Send sms to $phone" }
        runCatching {
            val sendSmsResult = bvLoginRepository.requestSms(
                phone, loginSessionId, buvid, recaptchaToken, geetestChallenge, geetestValidate
            )
            when (sendSmsResult.state) {
                SendSmsState.Ready -> {
                    logger.info { "this state should be here: $sendSmsState" }
                    sendSmsState = sendSmsResult.state
                }

                SendSmsState.Error -> {
                    logger.warn { "Send sms failed: ${sendSmsResult.message}" }
                    sendSmsState = sendSmsResult.state
                    withContext(Dispatchers.Main) {
                        "发送短信失败：${sendSmsResult.message}".toast(BVApp.context)
                    }
                }

                SendSmsState.Success -> {
                    logger.info { "Send sms success" }
                    sendSmsState = sendSmsResult.state
                    captchaKey = sendSmsResult.captchaKey
                    withContext(Dispatchers.Main) {
                        "验证码已发送".toast(BVApp.context)
                    }
                }

                SendSmsState.RecaptchaRequire -> {
                    logger.info { "Require manual recaptcha" }
                    logger.info { "recaptcha url: ${sendSmsResult.recaptchaUrl}" }
                    recaptchaUrl = sendSmsResult.recaptchaUrl ?: ""

                    URL(sendSmsResult.recaptchaUrl).query.split("&").forEach {
                        val (key, value) = it.split("=")
                        when (key) {
                            "recaptcha_token" -> recaptchaToken = value
                            "gee_gt" -> geetestGt = value
                            "gee_challenge" -> geetestChallenge = value
                        }
                    }

                    logger.info { "recaptchaToken: $recaptchaToken" }
                    logger.info { "geetestGt: $geetestGt" }
                    logger.info { "geetestChallenge: $geetestChallenge" }
                    sendSmsState = sendSmsResult.state
                }
            }
        }.onFailure {
            logger.warn { "Send sms failed: ${it.stackTraceToString()}" }
            withContext(Dispatchers.Main) {
                "发送短信失败：${it.message}".toast(BVApp.context)
            }
        }
    }

    @JavascriptInterface
    fun parseCaptchaResult(arg: String) {
        logger.info { "Get geetest result: $arg" }
        val geetestResult = Json.decodeFromString<GeetestResult>(arg)
        geetestChallenge = geetestResult.geetestChallenge
        geetestValidate = geetestResult.geetestValidate
        sendSmsState = SendSmsState.Ready
        viewModelScope.launch(Dispatchers.IO) { sendSms(phone) }
    }

    suspend fun loginWithSms(code: Int, onSuccess: () -> Unit) {
        logger.info { "Login with sms code: $code" }
        runCatching {
            val loginResult = bvLoginRepository.loginWithSms(
                phone = phone,
                loginSessionId = loginSessionId,
                code = code,
                captchaKey = captchaKey!!
            )
            if (loginResult.status == 0) {
                userRepository.setCookies(
                    uid = loginResult.dedeUserId,
                    uidCkMd5 = loginResult.dedeUserIdCkMd5,
                    sid = loginResult.sid,
                    sessData = loginResult.sessData,
                    biliJct = loginResult.biliJct,
                    expiredDate = loginResult.expiredDate
                )
                userRepository.setAppToken(
                    accessToken = loginResult.accessToken,
                    refreshToken = loginResult.refreshToken
                )
                withContext(Dispatchers.Main) {
                    "登录成功".toast(BVApp.context)
                }
                logger.info { "Login with sms success" }
                logger.fDebug { "$loginResult" }
            } else {
                logger.warn { "Login with sms return a unknown response: [status=${loginResult.status}, message=${loginResult.message}]" }
                withContext(Dispatchers.Main) {
                    "未知情况：${loginResult.message}".toast(BVApp.context)
                }
            }
        }.onFailure {
            logger.warn { "Login with sms failed: ${it.stackTraceToString()}" }
            withContext(Dispatchers.Main) {
                "短信登录失败：${it.message}".toast(BVApp.context)
            }
        }
    }
}

@Serializable
data class GeetestResult(
    @SerialName("geetest_challenge")
    val geetestChallenge: String,
    @SerialName("geetest_validate")
    val geetestValidate: String,
    @SerialName("geetest_seccode")
    val geetestSeccode: String
)