package dev.aaa1115910.biliapi.entity.login.qr

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestQRData(
    val url: String,
    @SerialName("qrcode_key")
    val qrcodeKey: String
)
