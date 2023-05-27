package dev.aaa1115910.biliapi

import dev.aaa1115910.biliapi.entity.BiliResponse
import dev.aaa1115910.biliapi.entity.login.qr.QRLoginData
import dev.aaa1115910.biliapi.entity.login.qr.RequestQRData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.BrowserUserAgent
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.Cookie
import io.ktor.http.URLProtocol
import io.ktor.http.setCookie
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object BiliPassportApi {
    private lateinit var client: HttpClient

    init {
        createClient()
    }

    private fun createClient() {
        client = HttpClient(OkHttp) {
            BrowserUserAgent()
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
        }
    }

    /**
     * 申请二维码（Web）
     */
    suspend fun getQRUrl(): BiliResponse<RequestQRData> =
        client.get("/x/passport-login/web/qrcode/generate").body()

    /**
     * 使用[qrcodeKey]进行二维码登录
     */
    suspend fun loginWithQR(qrcodeKey: String): Pair<BiliResponse<QRLoginData>, List<Cookie>> {
        val loginResponse = client.get("/x/passport-login/web/qrcode/poll") {
            parameter("qrcode_key", qrcodeKey)
        }
        return Pair(loginResponse.body(), loginResponse.setCookie())
    }

}