package dev.aaa1115910.biliapi.entity.login.qr

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QRLoginResponse(
    val code: Int,
    val message: String,
    val ttl: Int,
    val data: QRLoginData
)

@Serializable
data class QRLoginData(
    val url: String,
    @SerialName("refresh_token")
    val refreshToken: String,
    val timestamp: Long,
    val code: Int,
    val message: String
)