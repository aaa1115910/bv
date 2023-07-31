package dev.aaa1115910.biliapi.entity.login

import java.util.Date

/**
 * 获取扫码登录的二维码
 *
 * @param url 二维码内容
 * @param key 用于查询扫码登录结果
 */
data class QrLoginData(
    val url: String,
    val key: String
)

/**
 * 扫码登录结果
 *
 * @param state 登录结果状态
 * @param cookies 登录成功的 cookies
 */
data class QrLoginResult(
    val state: QrLoginState,
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val cookies: WebCookies? = null
)

enum class QrLoginState {
    Ready,
    RequestingQRCode,
    WaitingForScan,
    WaitingForConfirm,
    Expired,
    Success,
    Error,
    Unknown
}

data class WebCookies(
    val dedeUserId: Long,
    val dedeUserIdCkMd5: String,
    val sid: String,
    val biliJct: String,
    val sessData: String,
    val expiredDate: Date
)