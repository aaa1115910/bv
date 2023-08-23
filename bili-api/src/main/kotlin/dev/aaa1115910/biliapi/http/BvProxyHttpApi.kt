package dev.aaa1115910.biliapi.http

import bilibili.pgc.gateway.player.v2.PlayURLGrpcKt
import bilibili.pgc.gateway.player.v2.PlayViewReply
import bilibili.pgc.gateway.player.v2.PlayViewReq
import dev.aaa1115910.biliapi.grpc.utils.generateGrpcProxyHeaders
import dev.aaa1115910.biliapi.http.entity.BiliResponse
import dev.aaa1115910.biliapi.http.entity.video.PlayUrlData
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
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.toByteArray
import io.ktor.utils.io.ByteReadChannel
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object BvProxyHttpApi {
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
                    if (endPoint == "127.0.0.1") {
                        //local debug
                        port = 8080
                    } else {
                        protocol = URLProtocol.HTTPS
                    }
                }
            }
        }.apply {
            encApiSign()
        }
    }

    suspend fun getPgcVideoPlayUrl(
        av: Int? = null,
        bv: String? = null,
        epid: Int? = null,
        cid: Int? = null,
        qn: Int? = null,
        fnval: Int? = null,
        fnver: Int? = null,
        fourk: Int? = null,
        session: String? = null,
        supportMultiAudio: Boolean? = null,
        drmTechType: Int? = null,
        fromClient: String? = null,
        sessData: String? = null
    ): BiliResponse<PlayUrlData> = client?.get("/pgc/player/web/playurl") {
        require(av != null || bv != null) { "av and bv cannot be null at the same time" }
        require(epid != null || cid != null) { "epid and cid cannot be null at the same time" }
        av?.let { parameter("avid", it) }
        bv?.let { parameter("bvid", it) }
        epid?.let { parameter("ep_id", it) }
        cid?.let { parameter("cid", it) }
        qn?.let { parameter("qn", it) }
        fnval?.let { parameter("fnval", it) }
        fnver?.let { parameter("fnver", it) }
        fourk?.let { parameter("fourk", it) }
        session?.let { parameter("session", it) }
        supportMultiAudio?.let { parameter("support_multi_audio", it) }
        drmTechType?.let { parameter("drm_tech_type", it) }
        fromClient?.let { parameter("from_client", it) }
        sessData?.let { header("Cookie", "SESSDATA=$sessData;") }
        //必须得加上 referer 才能通过账号身份验证
        header("referer", "https://www.bilibili.com")
    }?.body() ?: throw IllegalStateException("no proxy server")

    suspend fun pgcPlayView(
        playViewReq: PlayViewReq,
        accessKey: String? = null,
        buvid: String? = null
    ): PlayViewReply {
        val response = client!!.post(PlayURLGrpcKt.playViewMethod.fullMethodName) {
            generateGrpcProxyHeaders(accessKey ?: "", buvid ?: "")
            setBody(ByteReadChannel(playViewReq.toByteArray()))
        }
        when (response.status) {
            HttpStatusCode.OK -> {
                val responseBytes = response.bodyAsChannel().toByteArray()
                return PlayViewReply.parseFrom(responseBytes)
            }

            else -> {
                val responseText = response.bodyAsText()
                val biliResponse = Json.parseToJsonElement(responseText).jsonObject
                throw IllegalStateException(biliResponse["message"]!!.jsonPrimitive.content)
            }
        }
    }
}