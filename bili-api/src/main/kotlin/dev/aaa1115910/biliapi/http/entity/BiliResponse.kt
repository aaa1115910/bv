package dev.aaa1115910.biliapi.http.entity

import kotlinx.serialization.Serializable

/**
 * @param code 0：成功 -101：账号未登录 -400：参数错误 -401：非法访问 -403：访问权限不足
 */
@Serializable
data class BiliResponse<T>(
    val code: Int,
    val message: String,
    val ttl: Int? = null,
    val data: T? = null,
    val result: T? = null
) {
    @Throws()
    fun getResponseData(): T {
        when (code) {
            0 -> {}
            -101 -> throw AuthFailureException(message)
            -352 -> throw RiskControlException(message)
            else -> throw IllegalStateException(message)
        }
        check(data != null || result != null) { "response data and result are both null" }
        data?.let { return it }
        result?.let { return it }
        error("response data and result are both null, and code should not run here")
    }
}

@Serializable
data class BiliResponseWithoutData(
    val code: Int,
    val message: String,
    val ttl: Int
)

@Suppress("unused")
class AuthFailureException : RuntimeException {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}

@Suppress("unused")
class RiskControlException : RuntimeException {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}