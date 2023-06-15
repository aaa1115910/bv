package dev.aaa1115910.biliapi.http.util

import dev.aaa1115910.biliapi.http.BiliHttpApi
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.plugin
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.parameter
import io.ktor.client.request.setBody
import io.ktor.http.HttpMethod
import io.ktor.http.Parameters
import io.ktor.http.encodedPath
import io.ktor.http.plus
import java.net.URLEncoder
import java.security.MessageDigest

private const val APP_KEY = "dfca71928277209b"
private const val APP_SEC = "b5475a8825547a4fc26c7d518eaaa02e"
private val mixinKeyEncTab = listOf(
    46, 47, 18, 2, 53, 8, 23, 32, 15, 50, 10, 31, 58, 3, 45, 35, 27, 43, 5, 49,
    33, 9, 42, 19, 29, 28, 14, 39, 12, 38, 41, 13, 37, 48, 7, 16, 24, 55, 40, 61,
    26, 17, 0, 1, 60, 51, 30, 4, 22, 25, 54, 21, 56, 59, 6, 63, 57, 62, 11, 36,
    20, 34, 44, 52
)

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

suspend fun HttpRequestBuilder.encWbi() {
    val getMixinKey: (orig: String) -> String = { orig ->
        val mixinKey = mixinKeyEncTab.fold("") { s, i -> s + orig[i] }
        mixinKey.substring(0, 32)
    }

    if (BiliHttpApi.wbiImgKey == null || BiliHttpApi.wbiSubKey == null) BiliHttpApi.updateWbi()
    require(BiliHttpApi.wbiImgKey != null && BiliHttpApi.wbiSubKey != null) { "Wbi keys can't be null!" }
    val mixinKey = getMixinKey(BiliHttpApi.wbiImgKey + BiliHttpApi.wbiSubKey)

    val wts = (System.currentTimeMillis() / 1000).toInt()
    parameter("wts", wts)

    val sortedParams = url.encodedParameters.entries()
        .associate { it.key to it.value.first() }
        .toSortedMap()
        .map { (key, value) -> "$key=$value" }
        .joinToString("&")

    val wRid = MessageDigest.getInstance("MD5").digest((sortedParams + mixinKey).toByteArray())
        .joinToString("") { "%02x".format(it) }
    parameter("w_rid", wRid)
}

fun HttpClient.encApiSign() = plugin(HttpSend)
    .intercept { request ->
        when (request.method) {
            HttpMethod.Get -> {
                val isWebRequest = request.headers.contains("Cookie")
                val isWbiRequest = request.url.encodedPath.contains("wbi")
                if (isWebRequest && isWbiRequest) {
                    println("Enc wbi for get request: ${request.url}")
                    request.encWbi()
                }
            }

            HttpMethod.Post -> {
                val parameters = (request.body as FormDataContent).formData
                val isParametersContainKeywords = parameters.contains("access_key")
                val isPathContainKeywords = request.url.encodedPath.contains("passport")
                if (isParametersContainKeywords || isPathContainKeywords) {
                    println("Enc app sign for post request: ${request.url}")
                    request.encAppPost()
                }
            }
        }
        execute(request)
    }

