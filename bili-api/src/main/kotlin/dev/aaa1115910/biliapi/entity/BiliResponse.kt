package dev.aaa1115910.biliapi.entity

import kotlinx.serialization.Serializable
import kotlin.jvm.Throws

@Serializable
data class BiliResponse<T>(
    val code: Int,
    val message: String,
    val ttl: Int? = null,
    val data: T? = null,
    val result: T? = null
) {
    fun isError() = !isSuccess()
    fun isSuccess() = code == 0

    @Throws()
    fun getResponseData(): T {
        check(isSuccess()) { message }
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