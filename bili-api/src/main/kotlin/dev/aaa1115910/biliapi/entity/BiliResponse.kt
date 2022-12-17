package dev.aaa1115910.biliapi.entity

import kotlinx.serialization.Serializable
import kotlin.jvm.Throws

@Serializable
data class BiliResponse<T>(
    val code: Int,
    val message: String,
    val ttl: Int,
    val data: T? = null
) {
    fun isError() = !isSuccess()
    fun isSuccess() = code == 0

    @Throws()
    fun getResponseData(): T {
        check(isSuccess()) { message }
        check(data != null) { "response data is null" }
        return data
    }
}

@Serializable
data class BiliResponseWithoutData(
    val code: Int,
    val message: String,
    val ttl: Int
)