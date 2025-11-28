package dev.aaa1115910.biliapi.http

import dev.aaa1115910.biliapi.http.entity.BiliResponse
import dev.aaa1115910.biliapi.http.entity.live.DanmuInfoData
import dev.aaa1115910.biliapi.http.entity.live.HistoryDanmaku
import dev.aaa1115910.biliapi.http.entity.live.RoomPlayInfoData
import dev.aaa1115910.biliapi.http.plugins.BiliUserAgent
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object BiliLiveHttpApi {
    private var endPoint: String = ""
    private lateinit var client: HttpClient
    private val logger = KotlinLogging.logger { }

    init {
        createClient()
    }

    private fun createClient() {
        client = HttpClient(OkHttp) {
            BiliUserAgent()
            install(ContentNegotiation) {
                json(Json {
                    coerceInputValues = true
                    ignoreUnknownKeys = true
                    prettyPrint = true
                })
            }
            install(ContentEncoding) {
                deflate(1.0F)
                gzip(0.9F)
            }
            defaultRequest {
                url {
                    host = "api.live.bilibili.com"
                    protocol = URLProtocol.HTTPS
                }
            }
        }
    }

    /**
     * 获取直播间[roomId]的弹幕连接地址等信息，例如 token
     */
    suspend fun getLiveDanmuInfo(roomId: Int): BiliResponse<DanmuInfoData> =
        client.get("/xlive/web-room/v1/index/getDanmuInfo") {
            parameter("id", roomId)
        }.body()

    /**
     * 获取直播间[roomId]的信息
     */
    suspend fun getLiveRoomPlayInfo(roomId: Int): BiliResponse<RoomPlayInfoData> =
        client.get("/xlive/web-room/v1/index/getRoomPlayInfo") {
            parameter("room_id", roomId)
        }.body()

    /**
     * 获取直播间[roomId]的历史弹幕
     */
    suspend fun getLiveDanmuHistory(roomId: Int): BiliResponse<HistoryDanmaku> =
        client.get("/xlive/web-room/v1/dM/gethistory") {
            parameter("roomid", roomId)
        }.body()

}