package dev.aaa1115910.biliapi.http

import dev.aaa1115910.biliapi.http.entity.BiliResponse
import dev.aaa1115910.biliapi.http.entity.proxy.ProxyAppPlayUrlData
import dev.aaa1115910.biliapi.http.entity.proxy.ProxyWebPlayUrlData
import dev.aaa1115910.biliapi.http.util.encApiSign
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.BrowserUserAgent
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive

object ProxyHttpApi {
    private var client: HttpClient? = null

    private val json = Json {
        coerceInputValues = true
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    fun createClient(endPoint: String) {
        client = HttpClient(OkHttp) {
            BrowserUserAgent()
            install(ContentNegotiation) {
                json(json)
            }
            install(ContentEncoding) {
                deflate(1.0F)
                gzip(0.9F)
            }
            install(HttpRequestRetry) {
                retryOnException(maxRetries = 2)
            }
            defaultRequest {
                url {
                    host = endPoint
                    protocol = URLProtocol.HTTPS
                }
            }
        }.apply {
            encApiSign()
        }
    }

    suspend fun getWebPgcVideoPlayUrl(
        cid: Int,
        epid: Int,
        qn: Int? = null,
        fnval: Int? = null,
        fnver: Int? = null,
        fourk: Int? = null,
        accessKey: String? = null,
        area: String
    ): BiliResponse<ProxyWebPlayUrlData> = client?.get("/pgc/player/web/playurl") {
        parameter("ep_id", epid)
        parameter("cid", cid)
        qn?.let { parameter("qn", it) }
        fnval?.let { parameter("fnval", it) }
        fnver?.let { parameter("fnver", it) }
        fourk?.let { parameter("fourk", it) }
        accessKey?.let { parameter("access_key", it) }
        parameter("area", area)
    }?.body() ?: throw IllegalStateException("no proxy server")

    suspend fun getAppPgcVideoPlayUrl(
        cid: Int,
        epid: Int,
        qn: Int? = null,
        fnval: Int? = null,
        fnver: Int? = null,
        fourk: Int? = null,
        accessKey: String? = null,
        area: String
    ): BiliResponse<ProxyAppPlayUrlData> {
        val responseString = client?.get("/pgc/player/api/playurl") {
            parameter("ep_id", epid)
            parameter("cid", cid)
            qn?.let { parameter("qn", it) }
            fnval?.let { parameter("fnval", it) }
            fnver?.let { parameter("fnver", it) }
            fourk?.let { parameter("fourk", it) }
            accessKey?.let { parameter("access_key", it) }
            parameter("area", area)
            parameter("build", 1001310)
            parameter("mobi_app", "bstar_a")
            parameter("platform", "android")
            parameter("ts", System.currentTimeMillis() / 1000)
        }?.bodyAsText() ?: throw IllegalStateException("no proxy server")
        val jsonObject = json.decodeFromString<JsonObject>(responseString)

        return if (jsonObject.size == 2) {
            BiliResponse(
                code = jsonObject["code"]!!.jsonPrimitive.int,
                message = jsonObject["message"]!!.jsonPrimitive.content,
                data = null
            )
        } else {
            BiliResponse(
                code = 0,
                message = "success",
                data = json.decodeFromString<ProxyAppPlayUrlData>(responseString)
            )
        }
    }
}