package dev.aaa1115910.bv.util

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import mu.KotlinLogging

object NetworkUtil {
    private lateinit var client: HttpClient
    private const val LOC_CHECK_URL = "https://www.cloudflare.com/cdn-cgi/trace"
    private val logger = KotlinLogging.logger { }
    var networkCheckResult: Map<String, String> = emptyMap()

    init {
        createClient()
    }

    private fun createClient() {
        client = HttpClient(OkHttp) {
            install(HttpRequestRetry) {
                retryOnException(maxRetries = 3)
            }
        }
    }

    suspend fun isMainlandChina(): Boolean {
        return runCatching {
            val result = client.get(LOC_CHECK_URL).bodyAsText()
            logger.info { "Network result:\n$result" }

            networkCheckResult = result
                .lines()
                .filter { it != "" }
                .associate {
                    val splits = it.split("=")
                    splits[0] to splits[1]
                }

            require(networkCheckResult["loc"] != "CNN") { "BV doesn't support use in mainland China" }

            false
        }.getOrDefault(true)
    }
}
