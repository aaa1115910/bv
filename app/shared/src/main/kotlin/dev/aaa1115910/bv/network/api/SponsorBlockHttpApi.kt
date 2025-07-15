package dev.aaa1115910.bv.network.api

import dev.aaa1115910.bv.BuildConfig
import dev.aaa1115910.bv.player.entity.sponsorblock.SegmentItem
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object SponsorBlockHttpApi {
    private const val BASE_URL = "https://bsbsb.top/api/"
    private const val EXT_VERSION = BuildConfig.VERSION_NAME
    private const val ORIGIN = BuildConfig.APPLICATION_ID

    private val client = HttpClient(OkHttp) {
        expectSuccess = false // Handle API errors manually by checking status code if needed
        defaultRequest {
            url(BASE_URL)
            header("Origin", ORIGIN)
            header("X-Ext-Version", EXT_VERSION)
        }
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
                isLenient = true
            })
        }
    }

    suspend fun getSkipSegments(
        videoID: String, // BVID
        cid: Long,
        categories: List<String>? = null,
    ): Result<List<SegmentItem>> {
        return try {
            val response = client.get("skipSegments") {
                parameter("videoID", videoID)
                parameter("cid", cid)
                categories?.forEach { category ->
                    parameter("category", category)
                }
            }
            when (response.status) {
                HttpStatusCode.OK -> Result.success(response.body())
                HttpStatusCode.NotFound -> Result.success(emptyList()) // 404 is a valid "no data" response
                else -> {
                    val errorMsg =
                        "SponsorBlock API Error: ${response.status} - ${response.bodyAsText()}"
                    println(errorMsg)
                    Result.failure(Exception(errorMsg))
                }
            }
        } catch (e: ClientRequestException) {
            // Handle specific Ktor client exceptions, e.g. 4xx/5xx that cause exceptions before body is read
            val errorMsg =
                "SponsorBlock API ClientRequestException: ${e.response.status} - ${e.message}"
            println(errorMsg)
            Result.failure(Exception(errorMsg, e))
        } catch (e: ServerResponseException) {
            val errorMsg =
                "SponsorBlock API ServerResponseException: ${e.response.status} - ${e.message}"
            println(errorMsg)
            Result.failure(Exception(errorMsg, e))
        } catch (e: kotlinx.serialization.SerializationException) {
            val errorMsg = "SponsorBlock API SerializationException: ${e.message}"
            println(errorMsg)
            Result.failure(Exception(errorMsg, e))
        } catch (e: Exception) { // Catch-all for other exceptions like network issues
            val errorMsg =
                "Error fetching skip segments: ${e.javaClass.simpleName} - ${e.localizedMessage}"
            println(errorMsg)
            Result.failure(Exception(errorMsg, e))
        }
    }
}
