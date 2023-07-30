package dev.aaa1115910.biliapi.http.entity.login.sms

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 发送验证码结果
 */
@Serializable
data class SendSmsResponse(
    @SerialName("captcha_key")
    val captchaKey: String,
    @SerialName("recaptcha_url")
    val recaptchaUrl: String
)
