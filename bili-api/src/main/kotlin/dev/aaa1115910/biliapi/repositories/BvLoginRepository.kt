package dev.aaa1115910.biliapi.repositories

import dev.aaa1115910.biliapi.entity.login.Captcha
import dev.aaa1115910.biliapi.entity.login.QrLoginData
import dev.aaa1115910.biliapi.entity.login.QrLoginResult
import dev.aaa1115910.biliapi.entity.login.QrLoginState
import dev.aaa1115910.biliapi.entity.login.WebCookies
import dev.aaa1115910.biliapi.http.BiliPassportHttpApi
import io.ktor.util.date.toJvmDate
import java.util.UUID

class BvLoginRepository {
    /**
     * 请求扫码登录的二维码，仅支持 Http 接口使用
     */
    suspend fun requestQrLogin(): QrLoginData {
        val response = BiliPassportHttpApi.getQRUrl().getResponseData()
        return QrLoginData(
            url = response.url,
            key = response.qrcodeKey
        )
    }

    /**
     * 检查扫码登录情况
     *
     * @param qrcodeKey 二维码内容
     */
    suspend fun checkQrLoginState(qrcodeKey: String): QrLoginResult {
        val (response, cookies) = BiliPassportHttpApi.loginWithQR(qrcodeKey)
        val responseData = response.getResponseData()
        var resultCookies: WebCookies? = null
        val resultState = when (responseData.code) {
            0 -> {
                resultCookies = WebCookies(
                    dedeUserId = cookies.find { it.name == "DedeUserID" }?.value?.toLong()
                        ?: throw IllegalArgumentException("Cookie DedeUserID not found"),
                    dedeUserIdCkMd5 = cookies.find { it.name == "DedeUserID__ckMd5" }?.value
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
                QrLoginState.Success
            }

            86101 -> QrLoginState.WaitingForScan
            86090 -> QrLoginState.WaitingForConfirm
            86038 -> QrLoginState.Expired
            else -> QrLoginState.Unknown
        }
        return QrLoginResult(resultState, resultCookies)
    }

    /**
     * 申请 captcha 验证码
     */
    suspend fun getCaptcha(): Captcha {
        val captchaData = BiliPassportHttpApi.getCaptcha().getResponseData()
        return Captcha(
            token = captchaData.token,
            challenge = captchaData.geetest.challenge,
            gt = captchaData.geetest.gt
        )
    }

    fun generateLoginSessionId() = UUID.randomUUID().toString().replace("-", "")

    suspend fun requestSms(
        phone: Long,
        loginSessionId: String,
        buvid: String,
        recaptchaToken: String? = null,
        geetestChallenge: String? = null,
        geetestValidate: String? = null
    ): SendSmsResult {
        val response = BiliPassportHttpApi.sendSms(
            cid = 86,
            tel = phone,
            loginSessionId = loginSessionId,
            recaptchaToken = recaptchaToken,
            geeChallenge = geetestChallenge,
            geeValidate = geetestValidate,
            geeSeccode = "$geetestValidate|jordan",
            channel = "bili",
            buvid = buvid,
            statistics = """{"appId":1,"platform":3,"version":"7.27.0","abtest":""}""",
            ts = System.currentTimeMillis() / 1000
        )
        if (response.code == 0 && response.data != null) {
            return if (response.data.captchaKey != "") {
                SendSmsResult(
                    state = SendSmsState.Success,
                    captchaKey = response.data.captchaKey
                )
            } else {
                SendSmsResult(
                    state = SendSmsState.RecaptchaRequire,
                    recaptchaUrl = response.data.recaptchaUrl
                )
            }
        } else {
            throw IllegalStateException(response.message)
        }
    }
}

data class SendSmsResult(
    val state: SendSmsState,
    val captchaKey: String? = null,
    val recaptchaUrl: String? = null
)

enum class SendSmsState {
    Error,
    Success,
    RecaptchaRequire
}