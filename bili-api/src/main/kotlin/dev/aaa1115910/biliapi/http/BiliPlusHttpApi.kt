package dev.aaa1115910.biliapi.http

import dev.aaa1115910.biliapi.http.entity.biliplus.View
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
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object BiliPlusHttpApi {
    private var endPoint: String = "www.biliplus.com"
    private lateinit var client: HttpClient

    private val json = Json {
        coerceInputValues = true
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    init {
        createClient()
    }

    private fun createClient() {
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
        }
    }

    suspend fun view(
        aid: Int,
        update: Boolean = true,
        accessKey: String? = null
    ): View = client.get("/api/view") {
        parameter("id", aid)
        parameter("update", update)
        accessKey?.let { parameter("access_key", it) }
    }.body()

    suspend fun getSeasonIdByAvid(
        aid: Int
    ): Int? {
        return runCatching {
            view(aid).bangumi?.seasonId?.toInt()
        }.getOrDefault(null)
    }
}