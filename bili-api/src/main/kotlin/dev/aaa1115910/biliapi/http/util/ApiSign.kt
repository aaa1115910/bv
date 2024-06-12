package dev.aaa1115910.biliapi.http.util

import dev.aaa1115910.biliapi.http.BiliHttpApi
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.plugin
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.parameter
import io.ktor.client.request.setBody
import io.ktor.client.utils.EmptyContent
import io.ktor.http.HttpMethod
import io.ktor.http.Parameters
import io.ktor.http.URLBuilder
import io.ktor.http.clone
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

fun HttpRequestBuilder.encAppGet() {
    parameter("appkey", APP_KEY)

    val sortedParams = url.encodedParameters.entries()
        .associate { it.key to it.value.first() }
        .toSortedMap()
        .also {
            url.parameters.clear()
            it.entries.forEach { (key, value) -> parameter(key, value) }
        }

    val sortedParamsString = sortedParams
        .map { (key, value) -> "$key=$value" }
        .joinToString("&")

    val sign = MessageDigest.getInstance("MD5").digest((sortedParamsString + APP_SEC).toByteArray())
        .joinToString("") { "%02x".format(it) }

    parameter("sign", sign)
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
        // skip when using grpc proxy
        if (request.url.encodedPath.startsWith("bilibili.")) {
            return@intercept execute(request)
        }

        val getUrlWithoutAccessToken: (URLBuilder) -> String = { urlBuilder ->
            urlBuilder.clone().apply {
                if (parameters.contains("access_key") && !parameters["access_key"].isNullOrBlank()) {
                    parameters["access_key"] = "HIDDEN_ACCESS_TOKEN"
                }
            }.toString()
        }

        when (request.method) {
            // app 端如果既用到了 wbi get 接口，也用到了 token 去请求，那是先计算 wbi sign 还是 app sign？
            // 目前看来需要计算 wbi sign 的接口之前忘记计算 app sign 都通过校验了🤯
            HttpMethod.Get -> {
                val isWbiRequest = request.url.encodedPath.contains("wbi")
                val isAppRequest =
                    request.url.parameters.contains("access_key") || request.url.host == "app.bilibili.com"
                if (isWbiRequest) {
                    println("Enc wbi for get request: ${getUrlWithoutAccessToken(request.url)}")
                    request.encWbi()
                } else if (isAppRequest) {
                    println("Enc app sign for get request: ${getUrlWithoutAccessToken(request.url)}")
                    request.encAppGet()
                    println(getUrlWithoutAccessToken(request.url))
                }
            }

            HttpMethod.Post -> {
                if (request.body is EmptyContent) return@intercept execute(request)
                val parameters = (request.body as FormDataContent).formData
                val isParametersContainKeywords = parameters.contains("access_key")
                val isPathContainKeywords = request.url.encodedPath.contains("passport")
                if (isParametersContainKeywords || isPathContainKeywords) {
                    println("Enc app sign for post request: ${getUrlWithoutAccessToken(request.url)}")
                    request.encAppPost()
                }
            }
        }
        execute(request)
    }

