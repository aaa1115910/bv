package dev.aaa1115910.biliapi.http

import dev.aaa1115910.biliapi.http.entity.BiliResponse
import dev.aaa1115910.biliapi.http.entity.login.CaptchaData
import dev.aaa1115910.biliapi.http.entity.login.qr.AppQRDataRequest
import dev.aaa1115910.biliapi.http.entity.login.qr.AppQRLoginData
import dev.aaa1115910.biliapi.http.entity.login.qr.RequestWebQRData
import dev.aaa1115910.biliapi.http.entity.login.qr.WebQRLoginData
import dev.aaa1115910.biliapi.http.entity.login.sms.SendSmsResponse
import dev.aaa1115910.biliapi.http.entity.login.sms.SmsLoginResponse
import dev.aaa1115910.biliapi.http.plugins.BiliUserAgent
import dev.aaa1115910.biliapi.http.util.encApiSign
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Cookie
import io.ktor.http.Parameters
import io.ktor.http.URLProtocol
import io.ktor.http.setCookie
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object BiliPassportHttpApi {
    private lateinit var client: HttpClient

    init {
        createClient()
    }

    private fun createClient() {
        client = HttpClient(OkHttp) {
            BiliUserAgent()
            install(ContentNegotiation) {
                json(Json {
                    coerceInputValues = true
                    ignoreUnknownKeys = true
                    prettyPrint = true
                })
            }
            install(ContentEncoding) {
                deflate(1.0F)
                gzip(0.9F)
            }
            defaultRequest {
                url {
                    host = "passport.bilibili.com"
                    protocol = URLProtocol.HTTPS
                }
            }
        }.apply {
            encApiSign()
        }
    }

    /**
     * 申请二维码（Web）
     */
    suspend fun getWebQRUrl(): BiliResponse<RequestWebQRData> =
        client.get("/x/passport-login/web/qrcode/generate").body()

    /**
     * 使用[qrcodeKey]进行二维码登录
     */
    suspend fun loginWithWebQR(qrcodeKey: String): Pair<BiliResponse<WebQRLoginData>, List<Cookie>> {
        val loginResponse = client.get("/x/passport-login/web/qrcode/poll") {
            parameter("qrcode_key", qrcodeKey)
        }
        return Pair(loginResponse.body(), loginResponse.setCookie())
    }

    /**
     * 申请二维码（App）
     */
    suspend fun getAppQRUrl(
        localId: String? = null,
        ts: Int,
        mobiApp: String? = null
    ): BiliResponse<AppQRDataRequest> =
        client.post("/x/passport-tv-login/qrcode/auth_code") {
            setBody(FormDataContent(
                Parameters.build {
                    localId?.let { append("local_id", it) }
                    append("ts", "$ts")
                    mobiApp?.let { append("mobi_app", it) }
                }
            ))
        }.body()


    /**
     * 使用[authCode]进行二维码登录
     */
    suspend fun loginWithAppQR(
        authCode: String,
        localId: String? = null,
        ts: Int
    ): BiliResponse<AppQRLoginData> =
        client.post("/x/passport-tv-login/qrcode/poll") {
            setBody(FormDataContent(
                Parameters.build {
                    append("auth_code", authCode)
                    localId?.let { append("local_id", it) }
                    append("ts", "$ts")
                }
            ))
        }.body()

    /**
     * 申请 captcha 验证码
     *
     * @param source 获取来源 已知：main_web
     */
    suspend fun getCaptcha(
        source: String? = null
    ): BiliResponse<CaptchaData> =
        client.get("/x/passport-login/captcha") {
            source?.let { parameter("source", it) }
        }.body()

    /**
     * 发送短信验证码
     *
     * @param cid 国际冠字码
     * @param tel 手机号码
     * @param loginSessionId 登录标识 uuid去掉'-'后得到
     * @param channel 一般固定值为"bili"
     * @param buvid
     * @param statistics 一般固定为{"appId":1,"platform":3,"version":"7.27.0","abtest":""}
     */
    suspend fun sendSms(
        cid: Long,
        tel: Long,
        loginSessionId: String,
        recaptchaToken: String? = null,
        geeChallenge: String? = null,
        geeValidate: String? = null,
        geeSeccode: String? = null,
        channel: String,
        buvid: String,
        statistics: String,
        ts: Long
    ): BiliResponse<SendSmsResponse> = client.post("/x/passport-login/sms/send") {
        setBody(FormDataContent(
            Parameters.build {
                append("cid", "$cid")
                append("tel", "$tel")
                append("login_session_id", loginSessionId)
                recaptchaToken?.let { append("recaptcha_token", it) }
                geeChallenge?.let { append("gee_challenge", it) }
                geeValidate?.let { append("gee_validate", it) }
                geeSeccode?.let { append("gee_seccode", it) }
                append("channel", channel)
                append("buvid", buvid)
                append("statistics", statistics)
                append("ts", "$ts")
            }
        ))
    }.body()

    suspend fun loginWithSms(
        cid: Long,
        tel: Long,
        loginSessionId: String,
        code: Int,
        captchaKey: String
    ): BiliResponse<SmsLoginResponse> = client.post("/x/passport-login/login/sms") {
        setBody(FormDataContent(
            Parameters.build {
                append("cid", "$cid")
                append("tel", "$tel")
                append("login_session_id", loginSessionId)
                append("code", "$code")
                append("captcha_key", captchaKey)
                append("ts", "0")
            }
        ))
    }.body()
}