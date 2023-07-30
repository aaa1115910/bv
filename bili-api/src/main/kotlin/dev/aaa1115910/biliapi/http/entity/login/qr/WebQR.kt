package dev.aaa1115910.biliapi.http.entity.login.qr

import io.ktor.http.Cookie
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class RequestWebQRData(
    val url: String,
    @SerialName("qrcode_key")
    val qrcodeKey: String
)


@Serializable
data class QRLoginResponse(
    val code: Int,
    val message: String,
    val ttl: Int,
    val data: WebQRLoginData,
    @Transient
    var cookies: List<Cookie> = emptyList()
)

@Serializable
data class WebQRLoginData(
    val url: String,
    @SerialName("refresh_token")
    val refreshToken: String,
    val timestamp: Long,
    val code: Int,
    val message: String
)