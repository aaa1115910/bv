package dev.aaa1115910.biliapi.repositories

import dev.aaa1115910.biliapi.entity.login.Captcha
import dev.aaa1115910.biliapi.entity.login.QrLoginData
import dev.aaa1115910.biliapi.entity.login.QrLoginResult
import dev.aaa1115910.biliapi.entity.login.QrLoginState
import dev.aaa1115910.biliapi.entity.login.SmsLoginResult
import dev.aaa1115910.biliapi.entity.login.WebCookies
import dev.aaa1115910.biliapi.http.BiliPassportHttpApi
import io.ktor.util.date.toJvmDate
import java.util.Date
import java.util.UUID

class LoginRepository {
    /**
     * 请求扫码登录的二维码，仅支持 Http 接口使用
     */
    suspend fun requestWebQrLogin(): QrLoginData {
        val response = BiliPassportHttpApi.getWebQRUrl().getResponseData()
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
    suspend fun checkWebQrLoginState(qrcodeKey: String): QrLoginResult {
        val (response, cookies) = BiliPassportHttpApi.loginWithWebQR(qrcodeKey)
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
        return QrLoginResult(
            state = resultState,
            accessToken = null,
            refreshToken = null,
            cookies = resultCookies
        )
    }

    /**
     * 请求扫码登录的二维码，支持 Http+gRPC 接口使用
     */
    suspend fun requestAppQrLogin(): QrLoginData {
        val response = BiliPassportHttpApi.getAppQRUrl(
            localId = "0",
            ts = (System.currentTimeMillis() / 1000).toInt(),
            mobiApp = "android_hd"
        ).getResponseData()
        return QrLoginData(
            url = response.url,
            key = response.authCode
        )
    }

    /**
     * 检查扫码登录情况
     *
     * @param authCode 二维码内容
     */
    suspend fun checkAppQrLoginState(authCode: String): QrLoginResult {
        val response = BiliPassportHttpApi.loginWithAppQR(
            authCode = authCode,
            localId = "0",
            ts = (System.currentTimeMillis() / 1000).toInt(),
        )
        println(response)
        var resultCookies: WebCookies? = null
        val resultState = when (response.code) {
            0 -> {
                resultCookies = WebCookies(
                    dedeUserId = response.getResponseData().cookieInfo.cookies.find { it.name == "DedeUserID" }?.value?.toLong()
                        ?: throw IllegalArgumentException("Cookie DedeUserID not found"),
                    dedeUserIdCkMd5 = response.getResponseData().cookieInfo.cookies.find { it.name == "DedeUserID__ckMd5" }?.value
                        ?: throw IllegalArgumentException("Cookie DedeUserID__ckMd5 not found"),
                    sid = response.getResponseData().cookieInfo.cookies.find { it.name == "sid" }?.value
                        ?: throw IllegalArgumentException("Cookie sid not found"),
                    biliJct = response.getResponseData().cookieInfo.cookies.find { it.name == "bili_jct" }?.value
                        ?: throw IllegalArgumentException("Cookie bili_jct not found"),
                    sessData = response.getResponseData().cookieInfo.cookies.find { it.name == "SESSDATA" }?.value
                        ?: throw IllegalArgumentException("Cookie SESSDATA not found"),
                    expiredDate = Date(response.getResponseData().cookieInfo.cookies.first().expires * 1000L)
                )
                QrLoginState.Success
            }

            86039 -> QrLoginState.WaitingForScan
            86090 -> QrLoginState.WaitingForConfirm
            86038 -> QrLoginState.Expired
            else -> QrLoginState.Unknown
        }
        return QrLoginResult(
            state = resultState,
            accessToken = response.data?.accessToken,
            refreshToken = response.data?.refreshToken,
            cookies = resultCookies
        )
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

    /**
     * 请求验证码
     */
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
        return if (response.code == 0 && response.data != null) {
            if (response.data.captchaKey != "") {
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
            SendSmsResult(
                state = SendSmsState.Error,
                message = response.message
            )
        }
    }

    /**
     * 验证码登录
     */
    suspend fun loginWithSms(
        phone: Long,
        loginSessionId: String,
        code: Int,
        captchaKey: String
    ): SmsLoginResult {
        val response = BiliPassportHttpApi.loginWithSms(
            cid = 86,
            tel = phone,
            loginSessionId = loginSessionId,
            code = code,
            captchaKey = captchaKey
        ).getResponseData()
        return SmsLoginResult.fromSmsLoginResponse(response)
    }
}

data class SendSmsResult(
    val state: SendSmsState,
    val message: String = "",
    val captchaKey: String? = null,
    val recaptchaUrl: String? = null
)

enum class SendSmsState {
    Ready,
    Error,
    Success,
    RecaptchaRequire
}