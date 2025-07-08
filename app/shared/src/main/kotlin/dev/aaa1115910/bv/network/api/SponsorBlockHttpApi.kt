package dev.aaa1115910.bv.network.api

import dev.aaa1115910.bv.entity.sponsorblock.SegmentItem
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import java.security.MessageDigest
import dev.aaa1115910.bv.BuildConfig // Import BuildConfig

object SponsorBlockHttpApi {
    private const val BASE_URL = "https://bsbsb.top/api/"
    private val EXT_VERSION = BuildConfig.SPONSOR_BLOCK_EXT_VERSION
    private val ORIGIN = BuildConfig.SPONSOR_BLOCK_API_ORIGIN

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
                    val errorMsg = "SponsorBlock API Error: ${response.status} - ${response.bodyAsText()}"
                    println(errorMsg)
                    Result.failure(Exception(errorMsg))
                }
            }
        } catch (e: ClientRequestException) {
            // Handle specific Ktor client exceptions, e.g. 4xx/5xx that cause exceptions before body is read
            val errorMsg = "SponsorBlock API ClientRequestException: ${e.response.status} - ${e.message}"
            println(errorMsg)
            Result.failure(Exception(errorMsg, e))
        } catch (e: ServerResponseException) {
            val errorMsg = "SponsorBlock API ServerResponseException: ${e.response.status} - ${e.message}"
            println(errorMsg)
            Result.failure(Exception(errorMsg, e))
        } catch (e: kotlinx.serialization.SerializationException) {
            val errorMsg = "SponsorBlock API SerializationException: ${e.message}"
            println(errorMsg)
            Result.failure(Exception(errorMsg, e))
        }
        catch (e: Exception) { // Catch-all for other exceptions like network issues
            val errorMsg = "Error fetching skip segments: ${e.javaClass.simpleName} - ${e.localizedMessage}"
            println(errorMsg)
            Result.failure(Exception(errorMsg, e))
        }
    }
}
