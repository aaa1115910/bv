package dev.aaa1115910.biliapi

import dev.aaa1115910.biliapi.entity.BiliResponse
import dev.aaa1115910.biliapi.entity.danmaku.DanmakuData
import dev.aaa1115910.biliapi.entity.danmaku.DanmakuResponse
import dev.aaa1115910.biliapi.entity.dynamic.DynamicData
import dev.aaa1115910.biliapi.entity.history.HistoryData
import dev.aaa1115910.biliapi.entity.user.MyInfoData
import dev.aaa1115910.biliapi.entity.user.UserCardData
import dev.aaa1115910.biliapi.entity.user.UserInfoData
import dev.aaa1115910.biliapi.entity.video.PlayUrlData
import dev.aaa1115910.biliapi.entity.video.PopularVideoData
import dev.aaa1115910.biliapi.entity.video.RelatedVideosResponse
import dev.aaa1115910.biliapi.entity.video.VideoInfo
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.BrowserUserAgent
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsChannel
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import javax.xml.parsers.DocumentBuilderFactory

object BiliApi {
    private var endPoint: String = ""
    private lateinit var client: HttpClient
    private val logger = KotlinLogging.logger { }

    init {
        createClient()
    }

    private fun createClient() {
        client = HttpClient(OkHttp) {
            BrowserUserAgent()
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                })
            }
            install(ContentEncoding) {
                deflate(1.0F)
                gzip(0.9F)
            }
            defaultRequest {
                host = "api.bilibili.com"
            }
        }
    }

    /**
     * 获取热门视频列表
     */
    suspend fun getPopularVideoData(
        pageNumber: Int = 1,
        pageSize: Int = 20,
        sessData: String = ""
    ): BiliResponse<PopularVideoData> = client.get("/x/web-interface/popular") {
        parameter("pn", pageNumber)
        parameter("ps", pageSize)
        header("Cookie", "SESSDATA=$sessData;")
    }.body()

    /**
     * 获取视频详细信息
     */
    suspend fun getVideoInfo(
        av: Int? = null,
        bv: String? = null,
        sessData: String = ""
    ): BiliResponse<VideoInfo> = client.get("/x/web-interface/view") {
        parameter("aid", av)
        parameter("bvid", bv)
        header("Cookie", "SESSDATA=$sessData;")
    }.body()

    /**
     * 获取视频流
     */
    suspend fun getVideoPlayUrl(
        av: Int? = null,
        bv: String? = null,
        cid: Int,
        qn: Int? = null,
        fnval: Int? = null,
        fnver: Int? = null,
        fourk: Int? = 0,
        session: String? = null,
        otype: String = "json",
        type: String = "",
        platform: String = "oc",
        sessData: String = ""
    ): BiliResponse<PlayUrlData> = client.get("/x/player/playurl") {
        parameter("avid", av)
        parameter("bvid", bv)
        parameter("cid", cid)
        parameter("qn", qn)
        parameter("fnval", fnval)
        parameter("fnver", fnver)
        parameter("fourk", fourk)
        parameter("session", session)
        parameter("otype", otype)
        parameter("type", type)
        parameter("platform", platform)
        header("Cookie", "SESSDATA=$sessData;")
    }.body()

    /**
     * 通过[cid]获取视频弹幕
     */
    suspend fun getDanmakuXml(
        cid: Int,
        sessData: String = ""
    ): DanmakuResponse {
        val xmlChannel = client.get("/x/v1/dm/list.so") {
            parameter("oid", cid)
            header("Cookie", "SESSDATA=$sessData;")
        }.bodyAsChannel()

        val dbFactory = DocumentBuilderFactory.newInstance()
        val dBuilder = dbFactory.newDocumentBuilder()
        val doc = withContext(Dispatchers.IO) {
            dBuilder.parse(xmlChannel.toInputStream())
        }
        doc.documentElement.normalize()

        val chatServer = doc.getElementsByTagName("chatserver").item(0).textContent
        val chatId = doc.getElementsByTagName("chatid").item(0).textContent.toInt()
        val maxLimit = doc.getElementsByTagName("maxlimit").item(0).textContent.toInt()
        val state = doc.getElementsByTagName("state").item(0).textContent.toInt()
        val realName = doc.getElementsByTagName("real_name").item(0).textContent.toInt()
        val source = doc.getElementsByTagName("source").item(0).textContent

        val data = mutableListOf<DanmakuData>()
        val danmakuNodes = doc.getElementsByTagName("d")

        for (i in 0 until danmakuNodes.length) {
            val danmakuNode = danmakuNodes.item(i)
            val p = danmakuNode.attributes.item(0).textContent
            val text = danmakuNode.textContent
            data.add(DanmakuData.fromString(p, text))
        }

        return DanmakuResponse(chatServer, chatId, maxLimit, state, realName, source, data)
    }

    /**
     * 获取动态列表
     *
     * @param type 返回数据额类型 all:全部 video:视频投稿 pgc:追番追剧 article：专栏
     * @param offset 请求第2页及其之后时填写，填写上一次请求获得的offset
     */
    suspend fun getDynamicList(
        timezoneOffset: Int = -480,
        type: String = "all",
        page: Int = 1,
        offset: String? = null,
        sessData: String = ""
    ): BiliResponse<DynamicData> = client.get("/x/polymer/web-dynamic/v1/feed/all") {
        parameter("timezone_offset", timezoneOffset)
        parameter("type", type)
        parameter("page", page)
        offset?.let { parameter("offset", offset) }
        header("Cookie", "SESSDATA=$sessData;")
    }.body()

    /**
     * 获取用户[uid]的详细信息
     */
    suspend fun getUserInfo(
        uid: Long,
        sessData: String = ""
    ): BiliResponse<UserInfoData> = client.get("/x/space/acc/info") {
        parameter("mid", uid)
        header("Cookie", "SESSDATA=$sessData;")
    }.body()


    /**
     * 获取用户[uid]的卡片信息
     *
     * @param uid 用户id
     * @param photo 是否请求用户主页头图
     */
    suspend fun getUserCardInfo(
        uid: Long,
        photo: Boolean = false,
        sessData: String = ""
    ): BiliResponse<UserCardData> = client.get("/x/web-interface/card") {
        parameter("mid", uid)
        parameter("photo", photo)
        header("Cookie", "SESSDATA=$sessData;")
    }.body()

    /**
     * 通过[sessData]获取用户个人信息
     */
    suspend fun getUserSelfInfo(
        sessData: String = ""
    ): BiliResponse<MyInfoData> = client.get("/x/space/myinfo") {
        header("Cookie", "SESSDATA=$sessData;")
    }.body()

    /**
     * 获取截止至目标id[max]和目标时间[viewAt]历史记录
     *
     * @param business 分类 貌似无效
     * @param pageSize 页面大小
     */
    suspend fun getHistories(
        max: Long = 0,
        business: String = "",
        viewAt: Long = 0,
        pageSize: Int = 20,
        sessData: String = ""
    ): BiliResponse<HistoryData> = client.get("/x/web-interface/history/cursor") {
        parameter("max", max)
        parameter("business", business)
        parameter("viewAt", viewAt)
        parameter("pageSize", pageSize)
        header("Cookie", "SESSDATA=$sessData;")
    }.body()

    suspend fun getRelatedVideos(
        avid: Long? = null,
        bvid: String? = null
    ): RelatedVideosResponse = client.get("/x/web-interface/archive/related") {
        check(avid != null || bvid != null) { "avid and bvid cannot be null at the same time" }
        parameter("aid", avid)
        parameter("bvid", bvid)
    }.body()
}