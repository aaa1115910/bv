package dev.aaa1115910.bv.sponsorblock.net

import dev.aaa1115910.bv.sponsorblock.entity.Video
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.security.MessageDigest

class SponsorBlockClient {
    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            }, contentType = ContentType.Any)
        }
    }

    private fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    suspend fun getSkipSegments(bvid: String, cid: Long, vararg categories: String): List<Video> {
        val hashPrefix = sha256(bvid).substring(0, 4)
        return client.get("https://bsbsb.top/api/skipSegments/$hashPrefix") {
            header("x-ext-version", "0.5.0")
            url {
                parameters.append("categories", "[\"${categories.joinToString("\",\"")}\"]")
                parameters.append("cid", cid.toString())
            }
        }.body()
    }
}
