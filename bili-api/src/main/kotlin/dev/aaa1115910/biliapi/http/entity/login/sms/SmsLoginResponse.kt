package dev.aaa1115910.biliapi.http.entity.login.sms

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SmsLoginResponse(
    val status: Int,
    val message: String,
    val url: String,
    @SerialName("token_info")
    val tokenInfo: TokenInfo? = null,
    @SerialName("cookie_info")
    val cookieInfo: CookieInfo? = null,
    val sso: List<String> = emptyList(),
    @SerialName("is_new")
    val isNew: Boolean,
    @SerialName("is_tourist")
    val isTourist: Boolean
) {
    @Serializable
    data class TokenInfo(
        val mid: Long,
        @SerialName("expires_in")
        val expiresIn: Int,
        @SerialName("access_token")
        val accessToken: String,
        @SerialName("refresh_token")
        val refreshToken: String
    )

    @Serializable
    data class CookieInfo(
        val cookies: List<Cookie>,
        val domains: List<String>
    ) {
        @Serializable
        data class Cookie(
            var name: String,
            var value: String,
            @SerialName("http_only")
            var httpOnly: Int,
            val expires: Int,
            var secure: Int,
            @SerialName("same_site")
            val sameSite: Int
        )
    }
}
