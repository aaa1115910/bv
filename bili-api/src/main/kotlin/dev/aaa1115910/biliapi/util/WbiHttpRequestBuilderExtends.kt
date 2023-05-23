package dev.aaa1115910.biliapi.util

import dev.aaa1115910.biliapi.BiliApi
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.parameter
import java.security.MessageDigest

suspend fun HttpRequestBuilder.encWbi() {
    val getMixinKey: (orig: String) -> String = { orig ->
        val mixinKeyEncTab = listOf(
            46, 47, 18, 2, 53, 8, 23, 32, 15, 50, 10, 31, 58, 3, 45, 35, 27, 43, 5, 49,
            33, 9, 42, 19, 29, 28, 14, 39, 12, 38, 41, 13, 37, 48, 7, 16, 24, 55, 40, 61,
            26, 17, 0, 1, 60, 51, 30, 4, 22, 25, 54, 21, 56, 59, 6, 63, 57, 62, 11, 36,
            20, 34, 44, 52
        )
        val mixinKey = mixinKeyEncTab.fold("") { s, i -> s + orig[i] }
        mixinKey.substring(0, 32)
    }

    if (BiliApi.wbiImgKey == null || BiliApi.wbiSubKey == null) BiliApi.updateWbi()
    require(BiliApi.wbiImgKey != null && BiliApi.wbiSubKey != null) { "Wbi keys can't be null!" }
    val mixinKey = getMixinKey(BiliApi.wbiImgKey + BiliApi.wbiSubKey)

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
