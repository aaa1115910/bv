package dev.aaa1115910.biliapi.http.util

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.setBody
import io.ktor.http.Parameters
import io.ktor.http.plus
import java.net.URLEncoder
import java.security.MessageDigest

private const val APP_KEY = "dfca71928277209b"
private const val APP_SEC = "b5475a8825547a4fc26c7d518eaaa02e"

fun HttpRequestBuilder.encAppPost() {
    var parameters = (body as FormDataContent).formData
    parameters += Parameters.build { append("appkey", APP_KEY) }

    val sortedParams = parameters.entries()
        .associate { it.key to it.value.first() }
        .toSortedMap()
        .map { (key, value) -> "$key=${URLEncoder.encode(value, "utf-8")}" }
        .joinToString("&")

    val sign = MessageDigest.getInstance("MD5").digest((sortedParams + APP_SEC).toByteArray())
        .joinToString("") { "%02x".format(it) }

    parameters += Parameters.build { append("sign", sign) }
    setBody(FormDataContent(parameters))
    println("sign: $sign")
}