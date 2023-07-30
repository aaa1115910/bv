package dev.aaa1115910.biliapi.entity.login

import dev.aaa1115910.biliapi.http.entity.login.sms.SmsLoginResponse
import java.util.Date

data class SmsLoginResult(
    val status: Int,
    val message: String,
    val accessToken: String,
    val refreshToken: String,
    val sessData: String,
    val biliJct: String,
    val dedeUserId: Long,
    val dedeUserIdCkMd5: String,
    val sid: String,
    val expiredDate: Date
) {
    companion object {
        fun fromSmsLoginResponse(smsLoginResponse: SmsLoginResponse) = SmsLoginResult(
            status = smsLoginResponse.status,
            message = smsLoginResponse.message,
            accessToken = smsLoginResponse.tokenInfo!!.accessToken,
            refreshToken = smsLoginResponse.tokenInfo.refreshToken,
            sessData = smsLoginResponse.cookieInfo!!.cookies.find { it.name == "SESSDATA" }?.value
                ?: "",
            biliJct = smsLoginResponse.cookieInfo.cookies.find { it.name == "bili_jct" }?.value
                ?: "",
            dedeUserId = smsLoginResponse.cookieInfo.cookies.find { it.name == "DedeUserID" }?.value?.toLongOrNull()
                ?: 0,
            dedeUserIdCkMd5 = smsLoginResponse.cookieInfo.cookies.find { it.name == "DedeUserID__ckMd5" }?.value
                ?: "",
            sid = smsLoginResponse.cookieInfo.cookies.find { it.name == "sid" }?.value ?: "",
            expiredDate = Date(System.currentTimeMillis() + smsLoginResponse.tokenInfo.expiresIn * 1000L)
        )
    }
}