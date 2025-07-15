package dev.aaa1115910.biliapi.http

import com.tfowl.ktor.client.plugins.JsoupPlugin
import dev.aaa1115910.biliapi.entity.pgc.PgcType
import dev.aaa1115910.biliapi.http.entity.BiliResponse
import dev.aaa1115910.biliapi.http.entity.BiliResponseWithoutData
import dev.aaa1115910.biliapi.http.entity.danmaku.DanmakuData
import dev.aaa1115910.biliapi.http.entity.danmaku.DanmakuResponse
import dev.aaa1115910.biliapi.http.entity.dynamic.DynamicData
import dev.aaa1115910.biliapi.http.entity.dynamic.DynamicDetailData
import dev.aaa1115910.biliapi.http.entity.history.HistoryData
import dev.aaa1115910.biliapi.http.entity.home.RcmdIndexData
import dev.aaa1115910.biliapi.http.entity.home.RcmdTopData
import dev.aaa1115910.biliapi.http.entity.index.IndexResultData
import dev.aaa1115910.biliapi.http.entity.pgc.PgcFeedData
import dev.aaa1115910.biliapi.http.entity.pgc.PgcFeedV3Data
import dev.aaa1115910.biliapi.http.entity.pgc.PgcWebInitialStateData
import dev.aaa1115910.biliapi.http.entity.region.RegionBanner
import dev.aaa1115910.biliapi.http.entity.region.RegionDynamic
import dev.aaa1115910.biliapi.http.entity.region.RegionDynamicList
import dev.aaa1115910.biliapi.http.entity.region.RegionFeedRcmd
import dev.aaa1115910.biliapi.http.entity.region.RegionLocs
import dev.aaa1115910.biliapi.http.entity.reply.CommentData
import dev.aaa1115910.biliapi.http.entity.reply.CommentReplyData
import dev.aaa1115910.biliapi.http.entity.search.AppSearchSquareData
import dev.aaa1115910.biliapi.http.entity.search.KeywordSuggest
import dev.aaa1115910.biliapi.http.entity.search.SearchResultData
import dev.aaa1115910.biliapi.http.entity.search.SearchTendingData
import dev.aaa1115910.biliapi.http.entity.search.WebSearchSquareData
import dev.aaa1115910.biliapi.http.entity.season.AppSeasonData
import dev.aaa1115910.biliapi.http.entity.season.FollowingSeasonAppData
import dev.aaa1115910.biliapi.http.entity.season.FollowingSeasonWebData
import dev.aaa1115910.biliapi.http.entity.season.SeasonFollowData
import dev.aaa1115910.biliapi.http.entity.season.WebSeasonData
import dev.aaa1115910.biliapi.http.entity.toview.ToViewData
import dev.aaa1115910.biliapi.http.entity.user.AppSpaceVideoData
import dev.aaa1115910.biliapi.http.entity.user.FollowAction
import dev.aaa1115910.biliapi.http.entity.user.FollowActionSource
import dev.aaa1115910.biliapi.http.entity.user.MyInfoData
import dev.aaa1115910.biliapi.http.entity.user.RelationData
import dev.aaa1115910.biliapi.http.entity.user.RelationStat
import dev.aaa1115910.biliapi.http.entity.user.UserCardData
import dev.aaa1115910.biliapi.http.entity.user.UserFollowData
import dev.aaa1115910.biliapi.http.entity.user.UserInfoData
import dev.aaa1115910.biliapi.http.entity.user.WebSpaceVideoData
import dev.aaa1115910.biliapi.http.entity.user.favorite.FavoriteFolderInfo
import dev.aaa1115910.biliapi.http.entity.user.favorite.FavoriteFolderInfoListData
import dev.aaa1115910.biliapi.http.entity.user.favorite.FavoriteItemIdListResponse
import dev.aaa1115910.biliapi.http.entity.user.favorite.UserFavoriteFoldersData
import dev.aaa1115910.biliapi.http.entity.user.garb.Equip
import dev.aaa1115910.biliapi.http.entity.user.garb.EquipPart
import dev.aaa1115910.biliapi.http.entity.video.AddCoin
import dev.aaa1115910.biliapi.http.entity.video.CheckSentCoin
import dev.aaa1115910.biliapi.http.entity.video.CheckVideoFavoured
import dev.aaa1115910.biliapi.http.entity.video.PlayUrlData
import dev.aaa1115910.biliapi.http.entity.video.PlayUrlV2Data
import dev.aaa1115910.biliapi.http.entity.video.PopularVideoData
import dev.aaa1115910.biliapi.http.entity.video.RelatedVideosResponse
import dev.aaa1115910.biliapi.http.entity.video.SetVideoFavorite
import dev.aaa1115910.biliapi.http.entity.video.Tag
import dev.aaa1115910.biliapi.http.entity.video.TagDetail
import dev.aaa1115910.biliapi.http.entity.video.TagTopVideosResponse
import dev.aaa1115910.biliapi.http.entity.video.Timeline
import dev.aaa1115910.biliapi.http.entity.video.TimelineAppData
import dev.aaa1115910.biliapi.http.entity.video.VideoDetail
import dev.aaa1115910.biliapi.http.entity.video.VideoInfo
import dev.aaa1115910.biliapi.http.entity.video.VideoMoreInfo
import dev.aaa1115910.biliapi.http.entity.video.VideoShot
import dev.aaa1115910.biliapi.http.entity.web.NavResponseData
import dev.aaa1115910.biliapi.http.util.BiliAppConf
import dev.aaa1115910.biliapi.http.util.encApiSign
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.BrowserUserAgent
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.readRawBytes
import io.ktor.http.Parameters
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.InternalAPI
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import org.jsoup.nodes.Document
import javax.xml.parsers.DocumentBuilderFactory

@Suppress("SpellCheckingInspection")
object BiliHttpApi {
    private var endPoint: String = "api.bilibili.com"
    private lateinit var client: HttpClient

    private val json = Json {
        coerceInputValues = true
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    var wbiImgKey: String? = null
    var wbiSubKey: String? = null
    private var wbiLastRefreshDate = 0L

    init {
        createClient()
        CoroutineScope(Dispatchers.IO).launch {
            updateWbi()
        }
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
            install(JsoupPlugin)
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
        sessData: String? = null
    ): BiliResponse<VideoInfo> = client.get("/x/web-interface/view") {
        parameter("aid", av)
        parameter("bvid", bv)
        sessData?.let { header("Cookie", "SESSDATA=$sessData;") }
    }.body()

    /**
     * 获取视频超详细信息
     */
    suspend fun getVideoDetail(
        av: Long? = null,
        bv: String? = null,
        sessData: String? = null
    ): BiliResponse<VideoDetail> = client.get("/x/web-interface/view/detail") {
        parameter("aid", av)
        parameter("bvid", bv)
        sessData?.let { header("Cookie", "SESSDATA=$sessData;") }
    }.body()

    /**
     * 获取视频流
     */
    suspend fun getVideoPlayUrl(
        av: Long? = null,
        bv: String? = null,
        cid: Long,
        qn: Int? = null,
        fnval: Int? = null,
        fnver: Int? = null,
        fourk: Int? = 0,
        session: String? = null,
        otype: String = "json",
        type: String = "",
        platform: String = "oc",
        sessData: String? = null
    ): BiliResponse<PlayUrlData> = client.get("/x/player/playurl") {
        require(av != null || bv != null) { "av and bv cannot be null at the same time" }
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
        sessData?.let { header("Cookie", "SESSDATA=$sessData;") }
    }.body()

    /**
     * 获取剧集视频流
     */
    suspend fun getPgcVideoPlayUrl(
        av: Long? = null,
        bv: String? = null,
        epid: Int? = null,
        cid: Long? = null,
        qn: Int? = null,
        fnval: Int? = null,
        fnver: Int? = null,
        fourk: Int? = null,
        session: String? = null,
        supportMultiAudio: Boolean? = null,
        drmTechType: Int? = null,
        fromClient: String? = null,
        sessData: String? = null
    ): BiliResponse<PlayUrlData> = client.get("/pgc/player/web/playurl") {
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
    }.body()

    /**
     * 获取剧集视频流 v2
     */
    suspend fun getPgcVideoPlayUrlV2(
        av: Long? = null,
        bv: String? = null,
        epid: Int? = null,
        cid: Long? = null,
        qn: Int? = null,
        fnval: Int? = null,
        fnver: Int? = null,
        fourk: Int? = null,
        session: String? = null,
        supportMultiAudio: Boolean? = null,
        drmTechType: Int? = null,
        fromClient: String? = null,
        sessData: String? = null
    ): BiliResponse<PlayUrlV2Data> = client.get("/pgc/player/web/v2/playurl") {
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
    }.body()

    /**
     * 通过[cid]获取视频弹幕
     */
    suspend fun getDanmakuXml(
        cid: Long,
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
        val chatId = doc.getElementsByTagName("chatid").item(0).textContent.toLong()
        val maxLimit = doc.getElementsByTagName("maxlimit").item(0).textContent.toInt()
        val state = doc.getElementsByTagName("state").item(0).textContent.toInt()
        val realName = doc.getElementsByTagName("real_name").item(0).textContent.toInt()
        val source = runCatching {
            doc.getElementsByTagName("source").item(0).textContent
        }.getOrDefault("")

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
     * 获取动态详情
     *
     * @param id 动态id
     */
    suspend fun getDynamicDetail(
        timezoneOffset: Int = -480,
        id: String,
        sessData: String = ""
    ): BiliResponse<DynamicDetailData> = client.get("/x/polymer/web-dynamic/v1/detail") {
        parameter("timezone_offset", timezoneOffset)
        parameter("id", id)
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
        parameter("view_at", viewAt)
        parameter("ps", pageSize)
        header("Cookie", "SESSDATA=$sessData;")
    }.body()

    /**
     * 获取稍后再看列表
     */

    suspend fun getToView(
        // max: Long = 0,
        // business: String = "",
        // viewAt: Long = 0,
        // pageSize: Int = 20,
        sessData: String = ""
    ): BiliResponse<ToViewData> = client.get("/x/v2/history/toview") {
        // parameter("max", max)
        // parameter("business", business)
        // parameter("view_at", viewAt)
        // parameter("ps", pageSize)
        header("Cookie", "SESSDATA=$sessData;")
    }.body()

    /**
     * 获取与视频[avid]或[bvid]有关的相关推荐视频
     */
    suspend fun getRelatedVideos(
        avid: Long? = null,
        bvid: String? = null
    ): RelatedVideosResponse = client.get("/x/web-interface/archive/related") {
        require(avid != null || bvid != null) { "avid and bvid cannot be null at the same time" }
        parameter("aid", avid)
        parameter("bvid", bvid)
    }.body()

    /**
     * 获取收藏夹[mediaId]的元数据
     */
    suspend fun getFavoriteFolderInfo(
        mediaId: Long,
        accessKey: String? = null,
        sessData: String? = null
    ): BiliResponse<FavoriteFolderInfo> = client.get("/x/v3/fav/folder/info") {
        checkToken(accessKey, sessData)
        parameter("media_id", mediaId)
        accessKey?.let { parameter("access_key", it) }
        sessData?.let { header("Cookie", "SESSDATA=$it;") }
    }.body()

    /**
     * 获取用户[mid]的所有收藏夹信息
     *
     * @param type 目标内容属性 默认为全部 0：全部 2：视频稿件
     * @param rid 目标内容id 视频稿件：视频稿件avid
     */
    suspend fun getAllFavoriteFoldersInfo(
        mid: Long,
        type: Int = 0,
        rid: Long? = null,
        accessKey: String? = null,
        sessData: String? = null
    ): BiliResponse<UserFavoriteFoldersData> = client.get("/x/v3/fav/folder/created/list-all") {
        checkToken(accessKey, sessData)
        parameter("up_mid", mid)
        parameter("type", type)
        parameter("rid", rid)
        accessKey?.let { parameter("access_key", it) }
        sessData?.let { header("Cookie", "SESSDATA=$it;") }
    }.body()

    /**
     * 获取收藏夹[mediaId]的详细内容
     *
     * @param tid 分区tid 默认为全部分区 0：全部分区
     * @param keyword 搜索关键字
     * @param order 排序方式 按收藏时间:mtime 按播放量: view 按投稿时间：pubtime
     * @param type 查询范围 0：当前收藏夹（对应media_id） 1：全部收藏夹
     * @param pageSize 每页数量 定义域：1-20
     * @param pageNumber 页码 默认为1
     * @param platform 平台标识 可为web（影响内容列表类型）
     */
    suspend fun getFavoriteList(
        mediaId: Long,
        tid: Int = 0,
        keyword: String? = null,
        order: String? = null,
        type: Int = 0,
        pageSize: Int = 20,
        pageNumber: Int = 1,
        platform: String? = null,
        accessKey: String? = null,
        sessData: String? = null
    ): BiliResponse<FavoriteFolderInfoListData> = client.get("/x/v3/fav/resource/list") {
        checkToken(accessKey, sessData)
        parameter("media_id", mediaId)
        parameter("tid", tid)
        parameter("keyword", keyword)
        parameter("order", order)
        parameter("type", type)
        parameter("ps", pageSize)
        parameter("pn", pageNumber)
        parameter("platform", platform)
        accessKey?.let { parameter("access_key", it) }
        sessData?.let { header("Cookie", "SESSDATA=$it;") }
    }.body()

    /**
     * 获取收藏夹[mediaId]的全部内容id
     */
    suspend fun getFavoriteIdList(
        mediaId: Long,
        platform: String? = null,
        accessKey: String? = null,
        sessData: String? = null
    ): FavoriteItemIdListResponse = client.get("/x/v3/fav/resource/ids") {
        checkToken(accessKey, sessData)
        parameter("media_id", mediaId)
        parameter("platform", platform)
        accessKey?.let { parameter("access_key", it) }
        sessData?.let { header("Cookie", "SESSDATA=$it;") }
    }.body()

    /**
     * 上报视频播放心跳
     *
     * @param avid 稿件avid avid与bvid任选一个
     * @param bvid 稿件bvid avid与bvid任选一个
     * @param cid 视频cid 用于识别分P
     * @param epid 番剧epid
     * @param sid 番剧ssid
     * @param mid 当前用户mid
     * @param playedTime 视频播放进度 单位为秒 默认为0
     * @param realtime 总计播放时间 单位为秒
     * @param startTs 开始播放时刻 时间戳
     * @param type 视频类型 3：投稿视频 4：剧集 10：课程
     * @param subType 剧集副类型 当type=4时本参数有效 1：番剧 2：电影 3：纪录片 4：国创 5：电视剧 7：综艺
     * @param dt 2
     * @param playType 播放动作 0：播放中 1：开始播放 2：暂停 3：继续播放
     * @param csrf bili_jct
     * @param sessData SESSDATA
     */
    suspend fun sendHeartbeat(
        avid: Long? = null,
        bvid: String? = null,
        cid: Long? = null,
        epid: Int? = null,
        sid: Int? = null,
        mid: Long? = null,
        playedTime: Int? = null,
        realtime: Int? = null,
        startTs: Long? = null,
        type: Int? = null,
        subType: Int? = null,
        dt: Int? = null,
        playType: Int? = null,
        csrf: String? = null,
        sessData: String
    ): String = client.post("/x/click-interface/web/heartbeat") {
        require(avid != null || bvid != null) { "avid and bvid cannot be null at the same time" }
        setBody(
            FormDataContent(
                Parameters.build {
                    avid?.let { append("aid", "$it") }
                    bvid?.let { append("bvid", it) }
                    cid?.let { append("cid", "$it") }
                    epid?.let { append("epid", "$it") }
                    sid?.let { append("sid", "$it") }
                    mid?.let { append("mid", "$it") }
                    playedTime?.let { append("played_time", "$it") }
                    realtime?.let { append("realtime", "$it") }
                    startTs?.let { append("start_ts", "$it") }
                    type?.let { append("type", "$it") }
                    subType?.let { append("sub_type", "$it") }
                    dt?.let { append("dt", "$it") }
                    playType?.let { append("play_type", "$it") }
                    csrf?.let { append("csrf", it) }
                }
            ))
        header("Cookie", "SESSDATA=$sessData;")
    }.bodyAsText()

    suspend fun sendHeartbeat(
        avid: Long? = null,
        bvid: String? = null,
        cid: Long? = null,
        epid: Int? = null,
        sid: Int? = null,
        mid: Long? = null,
        playedTime: Int? = null,
        realtime: Int? = null,
        startTs: Long? = null,
        type: Int? = null,
        subType: Int? = null,
        dt: Int? = null,
        playType: Int? = null,
        accessKey: String? = null
    ): String = client.post("/x/v2/history/report") {
        require(avid != null || bvid != null) { "avid and bvid cannot be null at the same time" }
        setBody(
            FormDataContent(
                Parameters.build {
                    avid?.let { append("aid", "$it") }
                    bvid?.let { append("bvid", it) }
                    cid?.let { append("cid", "$it") }
                    epid?.let { append("epid", "$it") }
                    sid?.let { append("sid", "$it") }
                    mid?.let { append("mid", "$it") }
                    playedTime?.let { append("progress", "$it") }
                    realtime?.let { append("realtime", "$it") }
                    startTs?.let { append("start_ts", "$it") }
                    type?.let { append("type", "$it") }
                    subType?.let { append("sub_type", "$it") }
                    dt?.let { append("dt", "$it") }
                    playType?.let { append("play_type", "$it") }
                    accessKey?.let { append("access_key", it) }
                }
            ))
    }.bodyAsText()

    /**
     * 获取视频[avid]的[cid]视频更多信息，例如播放进度
     */
    suspend fun getVideoMoreInfo(
        avid: Long,
        cid: Long,
        sessData: String
    ): BiliResponse<VideoMoreInfo> = client.get("/x/player/wbi/v2") {
        parameter("aid", avid)
        parameter("cid", cid)
        header("Cookie", "SESSDATA=$sessData;")
    }.body()

    /**
     * 为视频[avid]或[bvid]点赞或取消赞
     *
     * @param like 是否点赞
     * @param csrf bili_jct
     * @param sessData SESSDATA
     */
    suspend fun sendVideoLike(
        avid: Long? = null,
        bvid: String? = null,
        like: Boolean = true,
        csrf: String,
        sessData: String
    ): Pair<Boolean, String> {
        val response = client.post("/x/web-interface/archive/like") {
            require(avid != null || bvid != null) { "avid and bvid cannot be null at the same time" }
            setBody(
                FormDataContent(
                    Parameters.build {
                        avid?.let { append("aid", "$it") }
                        bvid?.let { append("bvid", it) }
                        append("like", "${if (like) 1 else 2}")
                        append("csrf", csrf)
                    }
                ))
            header("Cookie", "SESSDATA=$sessData;")
        }.body<BiliResponseWithoutData>()
        return Pair(response.code == 0, response.message)
    }

    /**
     * 检查视频[avid]或[bvid]是否已点赞
     */
    suspend fun checkVideoLiked(
        avid: Long? = null,
        bvid: String? = null,
        sessData: String
    ): Boolean {
        val response = client.get("/x/web-interface/archive/has/like") {
            require(avid != null || bvid != null) { "avid and bvid cannot be null at the same time" }
            avid?.let { parameter("aid", it) }
            bvid?.let { parameter("bvid", it) }
            header("Cookie", "SESSDATA=$sessData;")
        }.body<BiliResponse<Int>>()
        return runCatching {
            response.getResponseData() == 1
        }.getOrDefault(false)
    }

    /**
     * 为视频[avid]或[bvid]点赞或取消赞
     *
     * @param like 是否顺便点赞
     * @param multiply 投币数量
     * @param csrf bili_jct
     * @param sessData SESSDATA
     */
    suspend fun sendVideoCoin(
        avid: Long? = null,
        bvid: String? = null,
        multiply: Int = 1,
        like: Boolean = false,
        csrf: String,
        sessData: String
    ): Pair<Boolean, String> {
        require(avid != null || bvid != null) { "avid and bvid cannot be null at the same time" }
        val response = client.post("/x/web-interface/coin/add") {
            setBody(
                FormDataContent(
                    Parameters.build {
                        avid?.let { append("aid", "$it") }
                        bvid?.let { append("bvid", it) }
                        append("multiply", "$multiply")
                        append("select_like", "${if (like) 1 else 0}")
                        append("csrf", csrf)
                    }
                ))
            header("Cookie", "SESSDATA=$sessData;")
        }.body<BiliResponse<AddCoin>>()
        return Pair(response.code == 0, response.message)
    }

    /**
     * 检查视频[avid]或[bvid]是否已投币
     */
    suspend fun checkVideoSentCoin(
        avid: Long? = null,
        bvid: String? = null,
        sessData: String
    ): Boolean {
        val response = client.get("/x/web-interface/archive/coins") {
            require(avid != null || bvid != null) { "avid and bvid cannot be null at the same time" }
            avid?.let { parameter("aid", it) }
            bvid?.let { parameter("bvid", it) }
            header("Cookie", "SESSDATA=$sessData;")
        }.body<BiliResponse<CheckSentCoin>>()
        return runCatching {
            response.getResponseData().multiply != 0
        }.getOrDefault(false)
    }

    /**
     * 为视频[avid]添加到[addMediaIds]或从[delMediaIds]移除
     */
    suspend fun setVideoToFavorite(
        avid: Long,
        type: Int = 2,
        addMediaIds: List<Long> = listOf(),
        delMediaIds: List<Long> = listOf(),
        accessKey: String? = null,
        csrf: String? = null,
        sessData: String? = null
    ) {
        checkToken(accessKey, sessData)
        val response = client.post("/x/v3/fav/resource/deal") {
            require(addMediaIds.isNotEmpty() || delMediaIds.isNotEmpty()) {
                "addMediaIds and delMediaIds cannot be empty at the same time"
            }
            setBody(
                FormDataContent(
                    Parameters.build {
                        append("rid", "$avid")
                        append("type", "$type")
                        append("add_media_ids", addMediaIds.joinToString(separator = ","))
                        append("del_media_ids", delMediaIds.joinToString(separator = ","))
                        csrf?.let { append("csrf", it) }
                        accessKey?.let { append("access_key", it) }
                    }
                ))
            sessData?.let { header("Cookie", "SESSDATA=$it;") }
        }.body<BiliResponse<SetVideoFavorite>>()
        check(response.code == 0) { response.message }
    }

    /**
     * 检查视频[avid]是否已收藏
     */
    suspend fun checkVideoFavoured(
        avid: Long,
        accessKey: String? = null,
        sessData: String? = null
    ): Boolean {
        checkToken(accessKey, sessData)
        val response = client.get("/x/v2/fav/video/favoured") {
            parameter("aid", avid)
            accessKey?.let { parameter("access_key", it) }
            sessData?.let { header("Cookie", "SESSDATA=$it;") }
        }.body<BiliResponse<CheckVideoFavoured>>()
        return runCatching {
            response.getResponseData().favoured
        }.getOrDefault(false)
    }

    /**
     * 获取用户[mid]投稿视频
     *
     * @param order 排序方式 默认为pubdate 最新发布：pubdate 最多播放：click 最多收藏：stow
     * @param tid 筛选目标分区 默认为0 0：不进行分区筛选 分区tid为所筛选的分区
     * @param keyword 关键词筛选 用于使用关键词搜索该UP主视频稿件
     * @param pageNumber 页码
     * @param pageSize 每页项数 最小1，最大50
     */
    suspend fun getWebUserSpaceVideos(
        mid: Long,
        order: String = "pubdate",
        tid: Int = 0,
        keyword: String? = null,
        pageNumber: Int = 1,
        pageSize: Int = 30,
        sessData: String
    ): BiliResponse<WebSpaceVideoData> = client.get("/x/space/wbi/arc/search") {
        parameter("mid", mid)
        parameter("order", order)
        parameter("tid", tid)
        keyword?.let { parameter("keyword", it) }
        parameter("pn", pageNumber)
        parameter("ps", pageSize)
        // 风控
        parameter("dm_img_list", "[]")
        parameter("dm_img_str", "V2ViR0wgMS4wIChPcGVuR0wgRVMgMi4wIENocm9taXVtKQ")
        parameter("dm_cover_img_str", "V2ViR0wgMS4wIChPcGVuR0wgRVMgMi4wIENocm9taXVtKQ")
        header("Cookie", "SESSDATA=$sessData;")
        header("referer", "https://space.bilibili.com")
    }.body()

    suspend fun getAppUserSpaceVideos(
        mid: Long,
        lastAvid: Long,
        order: String = "pubdate",
        accessKey: String
    ): BiliResponse<AppSpaceVideoData> =
        client.get("https://app.bilibili.com/x/v2/space/archive/cursor") {
            parameter("vmid", mid)
            parameter("aid", lastAvid)
            parameter("order", order)
            parameter("access_key", accessKey)
        }.body()

    /**
     * 获取剧集[seasonId]或[epId]的详细信息 (Web)，例如 ss24439 ep234533，传参仅需数字
     */
    suspend fun getWebSeasonInfo(
        seasonId: Int? = null,
        epId: Int? = null,
        sessData: String = ""
    ): BiliResponse<WebSeasonData> = client.get("/pgc/view/web/season") {
        require(seasonId != null || epId != null) { "seasonId and epId cannot be null at the same time" }
        seasonId?.let { parameter("season_id", it) }
        epId?.let { parameter("ep_id", it) }
        header("Cookie", "SESSDATA=$sessData;")
        //必须得加上 referer 才能通过账号身份验证
        header("referer", "https://www.bilibili.com")
    }.body()

    /**
     * 获取剧集[seasonId]或[epId]的详细信息 (App)，例如 ss24439 ep234533，传参仅需数字
     */
    suspend fun getAppSeasonInfo(
        seasonId: Int? = null,
        epId: Int? = null,
        mobiApp: String,
        adExtra: String? = null,
        autoPlay: Int? = null,
        build: Int? = null,
        cLocale: String? = null,
        channel: String? = null,
        disableRcmd: Int? = null,
        fromAv: String? = null,
        fromSpmid: String? = null,
        isShowAllSeries: Int? = null,
        platform: String? = null,
        sLocale: String? = null,
        spmid: String? = null,
        statistics: String? = null,
        trackPath: String? = null,
        trackid: String? = null,
        ts: Int? = null,
        accessKey: String? = ""
    ): BiliResponse<AppSeasonData> = client.get("/pgc/view/v2/app/season") {
        require(seasonId != null || epId != null) { "seasonId and epId cannot be null at the same time" }
        seasonId?.let { parameter("season_id", it) }
        epId?.let { parameter("ep_id", it) }
        parameter("mobi_app", mobiApp)
        adExtra?.let { parameter("ad_extra", it) }
        autoPlay?.let { parameter("auto_play", it) }
        build?.let { parameter("build", it) }
        cLocale?.let { parameter("c_locale", it) }
        channel?.let { parameter("channel", it) }
        disableRcmd?.let { parameter("disable_rcmd", it) }
        fromAv?.let { parameter("from_av", it) }
        fromSpmid?.let { parameter("from_spmid", it) }
        isShowAllSeries?.let { parameter("is_show_all_series", it) }
        platform?.let { parameter("platform", it) }
        sLocale?.let { parameter("s_locale", it) }
        spmid?.let { parameter("spmid", it) }
        statistics?.let { parameter("statistics", it) }
        trackPath?.let { parameter("track_path", it) }
        trackid?.let { parameter("trackid", it) }
        ts?.let { parameter("ts", it) }
        accessKey?.let { parameter("access_key", accessKey) }
    }.body()

    /**
     * 添加番剧[seasonId]的追番
     */
    suspend fun addSeasonFollow(
        seasonId: Int,
        csrf: String,
        sessData: String
    ): BiliResponse<SeasonFollowData> = client.post("/pgc/web/follow/add") {
        setBody(
            FormDataContent(
                Parameters.build {
                    append("season_id", "$seasonId")
                    append("csrf", csrf)
                }
            ))
        header("Cookie", "SESSDATA=$sessData;")
        //必须得加上 referer 才能通过账号身份验证
        header("referer", "https://www.bilibili.com")
    }.body()

    /**
     * 添加番剧[seasonId]的追番
     */
    suspend fun addSeasonFollow(
        seasonId: Int,
        accessKey: String
    ): BiliResponse<SeasonFollowData> = client.post("/pgc/app/follow/add") {
        setBody(
            FormDataContent(
                Parameters.build {
                    append("season_id", "$seasonId")
                    append("access_key", accessKey)
                }
            ))
    }.body()

    /**
     * 取消番剧[seasonId]的追番
     */
    suspend fun delSeasonFollow(
        seasonId: Int,
        csrf: String,
        sessData: String
    ): BiliResponse<SeasonFollowData> = client.post("/pgc/web/follow/del") {
        setBody(
            FormDataContent(
                Parameters.build {
                    append("season_id", "$seasonId")
                    append("csrf", csrf)
                }
            ))
        header("Cookie", "SESSDATA=$sessData;")
        //必须得加上 referer 才能通过账号身份验证
        header("referer", "https://www.bilibili.com")
    }.body()

    /**
     * 取消番剧[seasonId]的追番
     */
    suspend fun delSeasonFollow(
        seasonId: Int,
        accessKey: String
    ): BiliResponse<SeasonFollowData> = client.post("/pgc/app/follow/del") {
        setBody(
            FormDataContent(
                Parameters.build {
                    append("season_id", "$seasonId")
                    append("access_key", accessKey)
                }
            ))
    }.body()

    /**
     * 单独获取剧集[seasonId]的用户信息[WebSeasonData.UserStatus]
     */
    suspend fun getSeasonUserStatus(
        seasonId: Int,
        sessData: String
    ): BiliResponse<WebSeasonData.UserStatus> = client.get("/pgc/view/web/season/user/status") {
        parameter("season_id", seasonId)
        header("Cookie", "SESSDATA=$sessData;")
        //必须得加上 referer 才能通过账号身份验证
        header("referer", "https://www.bilibili.com")
    }.body()

    /**
     * 获取视频[avid]/[bvid]的视频标签[Tag]
     */
    suspend fun getVideoTags(
        avid: Long? = null,
        bvid: String? = null,
        sessData: String = ""
    ): BiliResponse<List<Tag>> = client.get("/x/tag/archive/tags") {
        require(avid != null || bvid != null) { "avid and bvid cannot be null at the same time" }
        avid?.let { parameter("aid", it) }
        bvid?.let { parameter("bvid", it) }
        header("Cookie", "SESSDATA=$sessData;")
    }.body()

    /**
     * 获取视频标签[tagId]的详细信息，包含相关标签和最新视频
     */
    suspend fun getTagDetail(
        tagId: Int,
        pageNumber: Int,
        pageSize: Int
    ): BiliResponse<TagDetail> = client.get("/x/tag/detail") {
        parameter("tag_id", tagId)
        parameter("pn", pageNumber)
        parameter("ps", pageSize)
    }.body()

    /**
     * 获取视频标签[tagId]的最热门的视频列表
     */
    suspend fun getTagTopVideos(
        tagId: Int,
        pageNumber: Int,
        pageSize: Int
    ): TagTopVideosResponse = client.get("/x/web-interface/tag/top") {
        parameter("tid", tagId)
        parameter("pn", pageNumber)
        parameter("ps", pageSize)
    }.body()

    /**
     * 获取剧集更新时间表
     *
     * @param type 番剧: 1 影视（貌似只有少数几个纪录片）: 3, 国创: 4
     */
    suspend fun getTimeline(
        type: Int,
        before: Int,
        after: Int
    ): BiliResponse<List<Timeline>> = client.get("/pgc/web/timeline") {
        require(before in 0..7) { "before must in [0,7]" }
        require(after in 0..7) { "after must in [0,7]" }
        parameter("types", type)
        parameter("before", before)
        parameter("after", after)
    }.body()

    /**
     * 获取剧集更新时间表
     *
     * @param filterType 全部: 0 番剧: 1 我的追番: 2 国创: 3
     */
    suspend fun getTimeline(
        filterType: Int,
    ): BiliResponse<TimelineAppData> = client.get("/pgc/app/timeline") {
        parameter("filter_type", filterType)
        parameter("access_key", "")
    }.body()

    /**
     * 获取用户[mid]的关注列表，对于其他用户只能访问前5页
     */
    suspend fun getUserFollow(
        mid: Long,
        orderType: String? = null,
        pageSize: Int = 50,
        pageNumber: Int = 1,
        accessKey: String? = null,
        sessData: String? = null
    ): BiliResponse<UserFollowData> = client.get("/x/relation/followings") {
        checkToken(accessKey, sessData)
        parameter("vmid", mid)
        orderType?.let { parameter("order_type", orderType) }
        parameter("ps", pageSize)
        parameter("pn", pageNumber)
        sessData?.let { header("Cookie", "SESSDATA=$sessData;") }
        accessKey?.let { parameter("access_key", accessKey) }
    }.body()

    /**
     * 更改与用户[mid]之间的相互关系[action]
     */
    suspend fun modifyFollow(
        mid: Long,
        action: FollowAction,
        actionSource: FollowActionSource,
        accessKey: String? = null,
        csrf: String? = null,
        sessData: String? = null
    ): BiliResponseWithoutData = client.post("/x/relation/modify") {
        checkToken(accessKey, sessData)
        setBody(
            FormDataContent(
                Parameters.build {
                    append("fid", "$mid")
                    append("act", "${action.id}")
                    append("re_src", "${actionSource.id}")
                    accessKey?.let { append("access_key", accessKey) }
                    csrf?.let { append("csrf", csrf) }
                }
            ))
        sessData?.let { header("Cookie", "SESSDATA=$sessData;") }
    }.body()

    /**
     * 获取与用户[mid]的相互关系[RelationData]
     *
     * 有两个api，响应相同
     * - https://api.bilibili.com/x/space/acc/relation
     * - https://api.bilibili.com/x/web-interface/relation
     */
    suspend fun getRelations(
        mid: Long,
        accessKey: String? = null,
        sessData: String? = null
    ): BiliResponse<RelationData> = client.get("/x/space/wbi/acc/relation") {
        checkToken(accessKey, sessData)
        parameter("mid", mid)
        accessKey?.let { parameter("access_key", accessKey) }
        sessData?.let { header("Cookie", "SESSDATA=$sessData;") }
    }.body()

    /**
     * 获取用户[mid]的关系统计（关注数，粉丝数，黑名单数）
     */
    suspend fun getRelationStat(
        mid: Long,
        accessKey: String? = null,
        sessData: String? = null
    ): BiliResponse<RelationStat> = client.get("x/relation/stat") {
        parameter("vmid", mid)
        accessKey?.let { parameter("access_key", accessKey) }
        sessData?.let { header("Cookie", "SESSDATA=$sessData;") }
    }.body()

    /**
     * 获取搜索提示（Web）
     *
     * @param limit 返回数量
     * @param platform 平台标识
     */
    suspend fun getWebSearchSquare(
        limit: Int = 10,
        platform: String? = null
    ): BiliResponse<WebSearchSquareData> =
        client.get("/x/web-interface/wbi/search/square") {
            parameter("limit", limit)
            platform?.let { parameter("platform", platform) }
        }.body()

    /**
     * 获取搜索提示（App）
     *
     * @param limit 返回数量，上限仅为 10
     * @param platform 平台标识
     */
    suspend fun getAppSearchSquare(
        limit: Int = 10,
        platform: String? = null,
        //accessKey: String = ""
    ): BiliResponse<List<AppSearchSquareData>> =
        client.get("https://app.bilibili.com/x/v2/search/square") {
            parameter("limit", limit)
            platform?.let { parameter("platform", platform) }
            parameter("build", BiliAppConf.APP_BUILD_CODE)
            //parameter("access_key", accessKey)
        }.body()

    /**
     * 获取搜索趋势（App）
     *
     * @param limit 返回数量
     */
    suspend fun getSearchTrendRank(
        limit: Int = 10
    ): BiliResponse<SearchTendingData> =
        client.get("https://app.bilibili.com/x/v2/search/trending/ranking") {
            parameter("limit", limit)
            //platform?.let { parameter("platform", platform) }
            //parameter("build", BiliAppConf.APP_BUILD_CODE)
        }.body()

    /**
     * 获取搜索关键词建议
     *
     * 如果请求不带 [mainVer]，那返回的响应将只会包含 result，但不便于数据处理
     *
     * 如果请求中包含了 [highlight]，在返回的结果中 [KeywordSuggest.Result.tag] 的 name 会包含高亮的 html 标签
     */
    @OptIn(InternalAPI::class)
    suspend fun getKeywordSuggest(
        term: String,
        mainVer: String = "v1",
        highlight: String? = null,
        buvid: String
    ): KeywordSuggest {
        // 需手动解析 json，因为返回的 Content-Type 为 null，会导致 Ktor 抛出异常
        // io.ktor.client.call.NoTransformationFoundException: Expected response body of the type 'class dev.aaa1115910.biliapi.http.entity.search.KeywordSuggest (Kotlin reflection is not available)' but was 'class io.ktor.utils.io.ByteBufferChannel (Kotlin reflection is not available)'
        // In response from `https://s.search.bilibili.com/main/suggest?term=xxx`
        // Response status `200 `
        // Response header `ContentType: null`
        // Request header `Accept: application/json`
        val responseText = client.get("https://s.search.bilibili.com/main/suggest") {
            parameter("term", term)
            parameter("main_ver", mainVer)
            highlight?.let { parameter("highlight", it) }
            parameter("buvid", buvid)
        }.readRawBytes().toString(Charsets.UTF_8)
        val keywordSuggest = json.decodeFromString<KeywordSuggest>(responseText)
        val result = json.decodeFromJsonElement<KeywordSuggest.Result>(keywordSuggest.result!!)
        keywordSuggest.suggests.addAll(result.tag)
        return keywordSuggest
    }

    /**
     * 综合搜索与[keyword]相关的结果
     */
    suspend fun searchAll(
        keyword: String,
        page: Int = 1,
        tid: Int? = null,
        order: String? = null,
        duration: Int? = null,
        buvid3: String? = null
    ): BiliResponse<SearchResultData> = client.get("/x/web-interface/wbi/search/all/v2") {
        parameter("keyword", keyword)
        parameter("page", page)
        tid?.let { parameter("tids", it) }
        order?.let { parameter("order", it) }
        duration?.let { parameter("duration", it) }
        header("Cookie", "buvid3=$buvid3;")
    }.body()

    /**
     * 分类搜索与[keyword]相关的[type]类型的相关结果
     */
    suspend fun searchType(
        keyword: String,
        type: String,
        page: Int = 1,
        tid: Int? = null,
        order: String? = null,
        duration: Int? = null,
        buvid3: String? = null
    ): BiliResponse<SearchResultData> = client.get("/x/web-interface/wbi/search/type") {
        parameter("keyword", keyword)
        parameter("search_type", type)
        parameter("page", page)
        tid?.let { parameter("tids", it) }
        order?.let { parameter("order", it) }
        duration?.let { parameter("duration", it) }
        header("Cookie", "buvid3=$buvid3;")
    }.body()

    /** 获取番剧首页数据 */
    suspend fun getPgcWebInitialStateData(pgcType: PgcType): PgcWebInitialStateData {
        val path = pgcType.name.lowercase()
        val htmlDocuments = client.get("https://www.bilibili.com/$path").body<Document>()

        val dataScriptTagContent = htmlDocuments.body().select("script").find {
            it.html().contains("__INITIAL_STATE__")
        }?.html() ?: throw IllegalStateException("initial state data cannot be null")
        val dataJson =
            dataScriptTagContent.split("__INITIAL_STATE__=", ";(function()")[1]
        val initinalData = runCatching {
            json.decodeFromString<PgcWebInitialStateData>(dataJson)
        }.onFailure {
            println("parse initial state data failed: ${it.stackTraceToString()}")
        }.getOrNull() ?: throw IllegalStateException("parse initial state data failed")
        return initinalData
    }

    /**
     * 获取 PGC 猜你喜欢
     *
     * 返回数据的前几条内包含每小时更新的分类排行榜
     */
    suspend fun getPgcFeedV3(
        name: String = "anime",
        cursor: Int = 0
    ): BiliResponse<PgcFeedV3Data> = client.get("/pgc/page/web/v3/feed") {
        parameter("name", name)
        parameter("coursor", cursor)
    }.body()

    /**
     * 获取 PGC 猜你喜欢
     */
    suspend fun getPgcFeed(
        name: String = "movie",
        cursor: Int = 0
    ): BiliResponse<PgcFeedData> = client.get("/pgc/page/web/feed") {
        parameter("name", name)
        parameter("coursor", cursor)
        parameter("new_cursor_status", true)
    }.body()


    /**
     * 获取用户[mid]的追剧列表
     *
     * @param type 追剧类型
     * @param status 追剧状态
     * @param pageNumber 页码
     * @param pageSize 每页数量 [1, 30]
     * @param mid 用户id
     */
    suspend fun getFollowingSeasons(
        type: Int,
        status: Int,
        pageNumber: Int = 1,
        pageSize: Int = 15,
        mid: Long,
        sessData: String? = ""
    ): BiliResponse<FollowingSeasonWebData> = client.get("/x/space/bangumi/follow/list") {
        parameter("type", type)
        parameter("follow_status", status)
        parameter("pn", pageNumber)
        parameter("ps", pageSize)
        parameter("vmid", mid)
        header("Cookie", "SESSDATA=$sessData;")
    }.body()

    /**
     * 获取用户的追剧列表
     *
     * @param type 追剧类型
     * @param status 追剧状态
     * @param pageNumber 页码
     * @param pageSize 每页数量 [1, 30]
     * @param build App build code
     */
    suspend fun getFollowingSeasons(
        type: String,
        status: Int,
        pageNumber: Int = 1,
        pageSize: Int = 15,
        build: Int,
        accessKey: String
    ): BiliResponse<FollowingSeasonAppData> = client.get("/pgc/app/follow/v2/$type") {
        parameter("status", status)
        parameter("pn", pageNumber)
        parameter("ps", pageSize)
        parameter("build", build)
        parameter("access_key", accessKey)
    }.body()

    /**
     * 获取导航栏用户信息
     *
     * 内含 wbi keys
     */
    suspend fun getWebInterfaceNav(): BiliResponse<NavResponseData> =
        client.get("/x/web-interface/nav").body()

    /**
     * 更新 wbi keys
     */
    suspend fun updateWbi() {
        val needToUpdate =
            wbiImgKey == null || wbiSubKey == null || System.currentTimeMillis() - wbiLastRefreshDate < 2 * 60 * 60 * 1000L
        if (!needToUpdate) {
            println("Skip update wbi keys")
            return
        }

        println("Updating wbi keys...")
        runCatching {
            val wbiData = getWebInterfaceNav().data!!.wbiImg
            wbiImgKey = wbiData.getImgKey()
            wbiSubKey = wbiData.getSubKey()
            wbiLastRefreshDate = System.currentTimeMillis()
        }.onSuccess {
            println("Update wbi data success")
        }.onFailure {
            println("Update wbi data failed: ${it.stackTraceToString()}")
        }
    }

    /**
     * 获取首页视频推荐列表（Web）
     */
    suspend fun getFeedRcmd(
        freshType: Int = 4,
        pageSize: Int = 30,
        idx: Int = 1,
        sessData: String? = null
    ): BiliResponse<RcmdTopData> = client.get("/x/web-interface/wbi/index/top/feed/rcmd") {
        parameter("fresh_type", freshType)
        parameter("ps", pageSize)
        parameter("fresh_idx", idx)
        parameter("fresh_idx_1h", idx)
        sessData?.let { header("Cookie", "SESSDATA=$it;") }
    }.body()

    /**
     * 获取首页视频推荐列表（App）
     */
    suspend fun getFeedIndex(
        idx: Int = 0,
        accessKey: String? = null,
    ): BiliResponse<RcmdIndexData> =
        client.get("https://app.bilibili.com/x/v2/feed/index") {
            parameter("idx", idx)
            accessKey?.let { parameter("access_key", it) }
        }.body()

    private suspend fun seasonIndexResult(
        seasonIndexType: SeasonIndexType,
        order: Int? = null,
        seasonVersion: Int? = null,
        spokenLanguageType: Int? = null,
        area: Int? = null,
        isFinish: Int? = null,
        copyright: Int? = null,
        seasonStatus: Int? = null,
        seasonMonth: Int? = null,
        year: String? = null,
        releaseDate: String? = null,
        styleId: Int? = null,
        producerId: Int? = null,
        sort: Int? = null,
        page: Int? = null,
        pagesize: Int? = null,
        type: Int? = null
    ): BiliResponse<IndexResultData> = client.get("/pgc/season/index/result") {
        parameter("st", seasonIndexType.id)
        order?.let { parameter("order", it) }
        seasonVersion?.let { parameter("season_version", it) }
        spokenLanguageType?.let { parameter("spoken_language_type", it) }
        area?.let { parameter("area", it) }
        isFinish?.let { parameter("is_finish", it) }
        copyright?.let { parameter("copyright", it) }
        seasonStatus?.let { parameter("season_status", it) }
        seasonMonth?.let { parameter("season_month", it) }
        year?.let { parameter("year", it) }
        releaseDate?.let { parameter("release_date", it) }
        styleId?.let { parameter("style_id", it) }
        producerId?.let { parameter("producer_id", it) }
        sort?.let { parameter("sort", it) }
        page?.let { parameter("page", it) }
        parameter("season_type", seasonIndexType.id)
        pagesize?.let { parameter("pagesize", it) }
        type?.let { parameter("type", it) }
    }.body()

    suspend fun seasonIndexAnimeResult(
        order: Int = 0,
        seasonVersion: Int = -1,
        spokenLanguageType: Int = -1,
        area: Int = -1,
        isFinish: Int = -1,
        copyright: Int = -1,
        seasonStatus: Int = -1,
        seasonMonth: Int = -1,
        year: String = "-1",
        styleId: Int = -1,
        sort: Int = 0,
        page: Int = 1,
        pagesize: Int = 20,
        type: Int = 1
    ) = seasonIndexResult(
        seasonIndexType = SeasonIndexType.Anime,
        order = order,
        seasonVersion = seasonVersion,
        spokenLanguageType = spokenLanguageType,
        area = area,
        isFinish = isFinish,
        copyright = copyright,
        seasonStatus = seasonStatus,
        seasonMonth = seasonMonth,
        year = year,
        styleId = styleId,
        sort = sort,
        page = page,
        pagesize = pagesize,
        type = type
    )

    suspend fun seasonIndexGuochuangResult(
        order: Int = 0,
        seasonVersion: Int = -1,
        isFinish: Int = -1,
        copyright: Int = -1,
        seasonStatus: Int = -1,
        year: String = "-1",
        styleId: Int = -1,
        sort: Int = 0,
        page: Int = 1,
        pagesize: Int = 20,
        type: Int = 1
    ) = seasonIndexResult(
        seasonIndexType = SeasonIndexType.Guochuang,
        order = order,
        seasonVersion = seasonVersion,
        isFinish = isFinish,
        copyright = copyright,
        seasonStatus = seasonStatus,
        year = year,
        styleId = styleId,
        sort = sort,
        page = page,
        pagesize = pagesize,
        type = type
    )

    suspend fun seasonIndexVarietyResult(
        order: Int = 0,
        seasonStatus: Int = -1,
        styleId: Int = -1,
        sort: Int = 0,
        page: Int = 1,
        pagesize: Int = 20,
        type: Int = 1
    ) = seasonIndexResult(
        seasonIndexType = SeasonIndexType.Variety,
        order = order,
        seasonStatus = seasonStatus,
        styleId = styleId,
        sort = sort,
        page = page,
        pagesize = pagesize,
        type = type
    )

    suspend fun seasonIndexMovieResult(
        order: Int = 0,
        area: Int = -1,
        styleId: Int = -1,
        releaseDate: String = "-1",
        seasonStatus: Int = -1,
        sort: Int = 0,
        page: Int = 1,
        pagesize: Int = 20,
        type: Int = 1
    ) = seasonIndexResult(
        seasonIndexType = SeasonIndexType.Movie,
        order = order,
        area = area,
        styleId = styleId,
        releaseDate = releaseDate,
        seasonStatus = seasonStatus,
        sort = sort,
        page = page,
        pagesize = pagesize,
        type = type
    )

    suspend fun seasonIndexTvResult(
        order: Int = 0,
        area: Int = -1,
        styleId: Int = -1,
        releaseDate: String = "-1",
        seasonStatus: Int = -1,
        sort: Int = 0,
        page: Int = 1,
        pagesize: Int = 20,
        type: Int = 1
    ) = seasonIndexResult(
        seasonIndexType = SeasonIndexType.Tv,
        order = order,
        area = area,
        styleId = styleId,
        releaseDate = releaseDate,
        seasonStatus = seasonStatus,
        sort = sort,
        page = page,
        pagesize = pagesize,
        type = type
    )

    suspend fun seasonIndexDocumentaryResult(
        order: Int = 0,
        area: Int = -1,
        styleId: Int = -1,
        producerId: Int = -1,
        releaseDate: String = "-1",
        seasonStatus: Int = -1,
        sort: Int = 0,
        page: Int = 1,
        pagesize: Int = 20,
        type: Int = 1
    ) = seasonIndexResult(
        seasonIndexType = SeasonIndexType.Documentary,
        order = order,
        area = area,
        styleId = styleId,
        producerId = producerId,
        releaseDate = releaseDate,
        seasonStatus = seasonStatus,
        sort = sort,
        page = page,
        pagesize = pagesize,
        type = type
    )

    suspend fun download(url: String): ByteArray {
        return client.get(url).readRawBytes()
    }

    suspend fun getWebVideoShot(
        aid: Long? = null,
        bvid: String? = null,
        cid: Long? = null,
        needJsonArrayIndex: Boolean = false
    ): BiliResponse<VideoShot> = client.get("/x/player/videoshot") {
        require(aid != null || bvid != null) { "av and bv cannot be null at the same time" }
        aid?.let { parameter("aid", it) }
        bvid?.let { parameter("bvid", it) }
        cid?.let { parameter("cid", it) }
        parameter("index", if (needJsonArrayIndex) 1 else 0)
    }.body()

    suspend fun getAppVideoShot(
        aid: Long,
        cid: Long
    ): BiliResponse<VideoShot> = client.get("https://app.bilibili.com/x/v2/view/video/shot") {
        parameter("aid", aid)
        parameter("cid", cid)
        parameter("ts", 0)
    }.body()

    suspend fun getUserEquippedGarb(
        part: EquipPart,
        sessData: String
    ): BiliResponse<Equip> = client.get("/x/garb/user/equip") {
        parameter("part", part.value)
        header("Cookie", "SESSDATA=$sessData;")
    }.body()

    /**
     * 获取分区动态（App），包含顶部轮播图，大卡片活动推广位，和视频列表第一页
     */
    suspend fun getRegionDynamic(
        rid: Int,
        accessKey: String
    ): BiliResponse<RegionDynamic> = client.get("https://app.bilibili.com/x/v2/region/dynamic") {
        parameter("access_key", accessKey)
        parameter("build", BiliAppConf.APP_BUILD_CODE)
        parameter("rid", rid)
    }.body()

    /**
     * 获取分区视频列表（App）,用于[getRegionDynamic]加载数据后下滑加载更多数据
     */
    suspend fun getRegionDynamicList(
        rid: Int,
        ctime: Long = 0,
        accessKey: String
    ): BiliResponse<RegionDynamicList> =
        client.get("https://app.bilibili.com/x/v2/region/dynamic/list") {
            parameter("access_key", accessKey)
            parameter("build", BiliAppConf.APP_BUILD_CODE)
            parameter("rid", rid)
            parameter("ctime", ctime)
            parameter("pull", "false")
        }.body()

    //

    /**
     * 获取分区内各种插入的banner，例如顶部轮播图，还有插入的广告横幅（Web）
     *
     * id:
     * 4973  动画  douga
     * 4991  游戏  game
     * 5004  鬼畜  kichiku
     * 4979  音乐  music
     * 4985  舞蹈  dance
     * 5008  影视  cinephile
     * 5007  娱乐  ent
     * 4997  知识  knowledge
     * 4998  科技  tech
     * 5005  资讯  information
     * 5002  美食  food
     * 5001  生活  life
     * 5000  汽车  car
     * 5006  时尚  fashion
     * 4999  运动  sports
     * 5003  动物圈 animal
     */
    suspend fun getLocs(
        ids: List<Int>,
        sessData: String? = null
    ): RegionLocs = client.get("/x/web-show/res/locs") {
        parameter("ids", ids.joinToString(","))
        sessData?.let { header("Cookie", "SESSDATA=$it;") }
    }.body()

    /**
     * 获取评论
     *
     * @param type 评论类型
     * @param oid 评论区id
     * @param mode 评论排序方式 默认为 3， 0 3：仅按热度 1：按热度+按时间 2：仅按时间
     * @param paginationStr 分页参数
     */
    suspend fun getComments(
        type: Long,
        oid: Long,
        mode: Int = 3,
        paginationStr: String = """{"offset":""}""",
        //webLocation: Int = 1815875,
        sessData: String? = null,
        buvid3: String? = null
    ): BiliResponse<CommentData> =
        client.get("/x/v2/reply/wbi/main") {
            parameter("type", type)
            parameter("oid", oid)
            parameter("mode", mode)
            parameter("pagination_str", paginationStr)
            //parameter("web_location", webLocation)
            sessData?.let { header("Cookie", "SESSDATA=$sessData;buvid3=$buvid3;") }
        }.body()

    suspend fun getCommentReplies(
        oid: Long,
        type: Long,
        root: Long,
        pageSize: Int = 10,
        pageNumber: Int = 1
    ): BiliResponse<CommentReplyData> = client.get("/x/v2/reply/reply") {
        parameter("oid", oid)
        parameter("type", type)
        parameter("root", root)
        parameter("ps", pageSize)
        parameter("pn", pageNumber)
    }.body()

    suspend fun getSeasonIdByAvid(
        avid: Long
    ): Int? {
        return runCatching {
            val data = getPgcVideoPlayUrlV2(av = avid).getResponseData()
            data.playViewBusinessInfo.seasonInfo.seasonId
        }.getOrNull()
    }

    suspend fun getAidCidByEpid(
        epid: Int
    ): Pair<Long, Long>? {
        return runCatching {
            val data = getPgcVideoPlayUrlV2(epid = epid).getResponseData()
            data.playViewBusinessInfo.episodeInfo.aid to data.playViewBusinessInfo.episodeInfo.cid
        }.getOrNull()
    }

    /**
     * 获取 UGC 分区轮播图
     */
    suspend fun getRegionBanner(
        regionId: Int
    ): BiliResponse<RegionBanner> = client.get("/x/web-show/region/banner") {
        parameter("region_id", regionId)
    }.body()

    /**
     * 获取 UGC 分区推荐视频
     *
     * @param displayId 页数
     * @param requestCnt 每页数量
     * @param fromRegion 分区id
     */
    suspend fun getRegionFeedRcmd(
        displayId: Int,
        requestCnt: Int = 15,
        fromRegion: Int,
        device: String = "web",
        plat: Int = 30,
        sessData: String? = null
    ): BiliResponse<RegionFeedRcmd> = client.get("/x/web-interface/region/feed/rcmd") {
        parameter("display_id", displayId)
        parameter("request_cnt", requestCnt)
        parameter("from_region", fromRegion)
        parameter("device", device)
        parameter("plat", plat)
        sessData?.let { header("Cookie", "SESSDATA=$it;") }
    }.body()
}

enum class SeasonIndexType(val id: Int) {
    Anime(1), Movie(2), Documentary(3), Guochuang(4), Tv(5), Variety(7);

    companion object {
        fun fromId(id: Int) = entries.first { it.id == id }
    }
}

private fun checkToken(accessKey: String?, sessData: String?) {
    require(accessKey != null || sessData != null) { "accessKey and sessData cannot be null at the same time" }
}