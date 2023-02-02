package dev.aaa1115910.biliapi

import dev.aaa1115910.biliapi.entity.BiliResponse
import dev.aaa1115910.biliapi.entity.BiliResponseWithoutData
import dev.aaa1115910.biliapi.entity.danmaku.DanmakuData
import dev.aaa1115910.biliapi.entity.danmaku.DanmakuResponse
import dev.aaa1115910.biliapi.entity.dynamic.DynamicData
import dev.aaa1115910.biliapi.entity.history.HistoryData
import dev.aaa1115910.biliapi.entity.search.HotwordResponse
import dev.aaa1115910.biliapi.entity.search.KeywordSuggest
import dev.aaa1115910.biliapi.entity.search.SearchResultData
import dev.aaa1115910.biliapi.entity.season.SeasonFollowData
import dev.aaa1115910.biliapi.entity.season.SeasonData
import dev.aaa1115910.biliapi.entity.user.FollowAction
import dev.aaa1115910.biliapi.entity.user.FollowActionSource
import dev.aaa1115910.biliapi.entity.user.UserFollowData
import dev.aaa1115910.biliapi.entity.user.MyInfoData
import dev.aaa1115910.biliapi.entity.user.RelationData
import dev.aaa1115910.biliapi.entity.user.RelationStat
import dev.aaa1115910.biliapi.entity.user.SpaceVideoData
import dev.aaa1115910.biliapi.entity.user.UserCardData
import dev.aaa1115910.biliapi.entity.user.UserInfoData
import dev.aaa1115910.biliapi.entity.user.favorite.FavoriteFolderInfo
import dev.aaa1115910.biliapi.entity.user.favorite.FavoriteFolderInfoListData
import dev.aaa1115910.biliapi.entity.user.favorite.FavoriteItemIdListResponse
import dev.aaa1115910.biliapi.entity.user.favorite.UserFavoriteFoldersData
import dev.aaa1115910.biliapi.entity.video.AddCoin
import dev.aaa1115910.biliapi.entity.video.SetVideoFavorite
import dev.aaa1115910.biliapi.entity.video.CheckVideoFavoured
import dev.aaa1115910.biliapi.entity.video.CheckSentCoin
import dev.aaa1115910.biliapi.entity.video.PlayUrlData
import dev.aaa1115910.biliapi.entity.video.PopularVideoData
import dev.aaa1115910.biliapi.entity.video.RelatedVideosResponse
import dev.aaa1115910.biliapi.entity.video.Tag
import dev.aaa1115910.biliapi.entity.video.Timeline
import dev.aaa1115910.biliapi.entity.video.TimelineType
import dev.aaa1115910.biliapi.entity.video.VideoInfo
import dev.aaa1115910.biliapi.entity.video.VideoMoreInfo
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
import io.ktor.http.Parameters
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import mu.KotlinLogging
import javax.xml.parsers.DocumentBuilderFactory

object BiliApi {
    private var endPoint: String = ""
    private lateinit var client: HttpClient
    private val logger = KotlinLogging.logger { }

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
        header("Cookie", "SESSDATA=$sessData;")
    }.body()

    /**
     * 获取剧集视频流
     */
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
        sessData: String = ""
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
        header("Cookie", "SESSDATA=$sessData;")
        //必须得加上 referer 才能通过账号身份验证
        header("referer", "https://www.bilibili.com")
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
        sessData: String = ""
    ): BiliResponse<FavoriteFolderInfo> = client.get("/x/v3/fav/folder/info") {
        parameter("media_id", mediaId)
        header("Cookie", "SESSDATA=$sessData;")
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
        rid: Int? = null,
        sessData: String = ""
    ): BiliResponse<UserFavoriteFoldersData> = client.get("/x/v3/fav/folder/created/list-all") {
        parameter("up_mid", mid)
        parameter("type", type)
        parameter("rid", rid)
        header("Cookie", "SESSDATA=$sessData;")
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
        sessData: String = ""
    ): BiliResponse<FavoriteFolderInfoListData> = client.get("/x/v3/fav/resource/list") {
        parameter("media_id", mediaId)
        parameter("tid", tid)
        parameter("keyword", keyword)
        parameter("order", order)
        parameter("type", type)
        parameter("ps", pageSize)
        parameter("pn", pageNumber)
        parameter("platform", platform)
        header("Cookie", "SESSDATA=$sessData;")
    }.body()

    /**
     * 获取收藏夹[mediaId]的全部内容id
     */
    suspend fun getFavoriteIdList(
        mediaId: Long,
        platform: String? = null,
        sessData: String = ""
    ): FavoriteItemIdListResponse = client.get("/x/v3/fav/resource/ids") {
        parameter("media_id", mediaId)
        parameter("platform", platform)
        header("Cookie", "SESSDATA=$sessData;")
    }.body()

    /**
     * 上报视频播放心跳
     *
     * @param aid 稿件avid avid与bvid任选一个
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
        cid: Int? = null,
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
        setBody(FormDataContent(
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

    /**
     * 获取视频[avid]的[cid]视频更多信息，例如播放进度
     */
    suspend fun getVideoMoreInfo(
        avid: Int,
        cid: Int,
        sessData: String
    ): BiliResponse<VideoMoreInfo> = client.get("/x/player/v2") {
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
        avid: Int? = null,
        bvid: String? = null,
        like: Boolean = true,
        csrf: String,
        sessData: String
    ): Pair<Boolean, String> {
        val response = client.post("/x/web-interface/archive/like") {
            require(avid != null || bvid != null) { "avid and bvid cannot be null at the same time" }
            setBody(FormDataContent(
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
        avid: Int? = null,
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
        avid: Int? = null,
        bvid: String? = null,
        multiply: Int = 1,
        like: Boolean = false,
        csrf: String,
        sessData: String
    ): Pair<Boolean, String> {
        require(avid != null || bvid != null) { "avid and bvid cannot be null at the same time" }
        val response = client.post("/x/web-interface/coin/add") {
            setBody(FormDataContent(
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
        avid: Int? = null,
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
        avid: Int,
        type: Int = 2,
        addMediaIds: List<Long> = listOf(),
        delMediaIds: List<Long> = listOf(),
        csrf: String,
        sessData: String
    ): Pair<Boolean, String> {
        val response = client.post("/x/v3/fav/resource/deal") {
            require(addMediaIds.isNotEmpty() || delMediaIds.isNotEmpty()) {
                "addMediaIds and delMediaIds cannot be empty at the same time"
            }
            setBody(FormDataContent(
                Parameters.build {
                    append("rid", "$avid")
                    append("type", "$type")
                    val regex = """ |\[|]""".toRegex()
                    append("add_media_ids", "${addMediaIds.toString().replace(regex, "")}")
                    append("del_media_ids", "${delMediaIds.toString().replace(regex, "")}")
                    append("csrf", csrf)
                }
            ))
            header("Cookie", "SESSDATA=$sessData;")
        }.body<BiliResponse<SetVideoFavorite>>()
        return Pair(response.code == 0, response.message)
    }

    /**
     * 检查视频[avid]是否已收藏
     */
    suspend fun checkVideoFavoured(
        avid: Int,
        sessData: String
    ): Boolean {
        val response = client.get("/x/v2/fav/video/favoured") {
            parameter("aid", avid)
            header("Cookie", "SESSDATA=$sessData;")
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
    suspend fun getUserSpaceVideos(
        mid: Long,
        order: String = "pubdate",
        tid: Int = 0,
        keyword: String? = null,
        pageNumber: Int = 1,
        pageSize: Int = 30,
        sessData: String
    ): BiliResponse<SpaceVideoData> = client.get("/x/space/arc/search") {
        parameter("mid", mid)
        parameter("order", order)
        parameter("tid", tid)
        keyword?.let { parameter("keyword", it) }
        parameter("pn", pageNumber)
        parameter("ps", pageSize)
        header("Cookie", "SESSDATA=$sessData;")
    }.body()

    /**
     * 获取剧集[seasonId]或[epId]的详细信息，例如 ss24439 ep234533，传参仅需数字
     */
    suspend fun getSeasonInfo(
        seasonId: Int? = null,
        epId: Int? = null,
        sessData: String = ""
    ): BiliResponse<SeasonData> = client.get("/pgc/view/web/season") {
        require(seasonId != null || epId != null) { "seasonId and epId cannot be null at the same time" }
        seasonId?.let { parameter("season_id", it) }
        epId?.let { parameter("ep_id", it) }
        header("Cookie", "SESSDATA=$sessData;")
        //必须得加上 referer 才能通过账号身份验证
        header("referer", "https://www.bilibili.com")
    }.body()

    /**
     * 添加番剧[seasonId]的追番
     */
    suspend fun addSeasonFollow(
        seasonId: Int,
        csrf: String,
        sessData: String
    ): BiliResponse<SeasonFollowData> = client.post("/pgc/web/follow/add") {
        parameter("season_id", seasonId)
        parameter("csrf", csrf)
        header("Cookie", "SESSDATA=$sessData;")
        //必须得加上 referer 才能通过账号身份验证
        header("referer", "https://www.bilibili.com")
    }.body()

    /**
     * 取消番剧[seasonId]的追番
     */
    suspend fun delSeasonFollow(
        seasonId: Int,
        csrf: String,
        sessData: String
    ): BiliResponse<SeasonFollowData> = client.post("/pgc/web/follow/del") {
        parameter("season_id", seasonId)
        parameter("csrf", csrf)
        header("Cookie", "SESSDATA=$sessData;")
        //必须得加上 referer 才能通过账号身份验证
        header("referer", "https://www.bilibili.com")
    }.body()

    /**
     * 单独获取剧集[seasonId]的用户信息[SeasonData.UserStatus]
     */
    suspend fun getSeasonUserStatus(
        seasonId: Int,
        sessData: String
    ): BiliResponse<SeasonData.UserStatus> = client.get("/pgc/view/web/season/user/status") {
        parameter("season_id", seasonId)
        header("Cookie", "SESSDATA=$sessData;")
        //必须得加上 referer 才能通过账号身份验证
        header("referer", "https://www.bilibili.com")
    }.body()

    /**
     * 获取视频[avid]/[bvid]的视频标签[Tag]
     */
    suspend fun getVideoTags(
        avid: Int? = null,
        bvid: String? = null,
        sessData: String = ""
    ): BiliResponse<List<Tag>> = client.get("/x/tag/archive/tags") {
        require(avid != null || bvid != null) { "avid and bvid cannot be null at the same time" }
        avid?.let { parameter("aid", it) }
        bvid?.let { parameter("bvid", it) }
        header("Cookie", "SESSDATA=$sessData;")
    }.body()

    /**
     * 获取剧集更新时间表
     */
    suspend fun getTimeline(
        type: TimelineType,
        before: Int,
        after: Int
    ): BiliResponse<List<Timeline>> = client.get("/pgc/web/timeline") {
        require(before in 0..7) { "before must in [0,7]" }
        require(after in 0..7) { "after must in [0,7]" }
        parameter("types", type.id)
        parameter("before", before)
        parameter("after", after)
    }.body()

    /**
     * 获取用户[mid]的关注列表，对于其他用户只能访问前5页
     */
    suspend fun getUserFollow(
        mid: Long,
        orderType: String? = null,
        pageSize: Int = 50,
        pageNumber: Int = 1,
        sessData: String = ""
    ): BiliResponse<UserFollowData> = client.get("/x/relation/followings") {
        parameter("vmid", mid)
        orderType?.let { parameter("order_type", orderType) }
        parameter("ps", pageSize)
        parameter("pn", pageNumber)
        header("Cookie", "SESSDATA=$sessData;")
    }.body()

    /**
     * 更改与用户[mid]之间的相互关系[action]
     */
    suspend fun modifyFollow(
        mid: Long,
        action: FollowAction,
        actionSource: FollowActionSource,
        csrf: String,
        sessData: String = ""
    ): BiliResponseWithoutData = client.post("/x/relation/modify") {
        setBody(FormDataContent(
            Parameters.build {
                append("fid", "$mid")
                append("act", "${action.id}")
                append("re_src", "${actionSource.id}")
                append("csrf", csrf)
            }
        ))
        header("Cookie", "SESSDATA=$sessData;")
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
        sessData: String = ""
    ): BiliResponse<RelationData> = client.get("/x/space/acc/relation") {
        parameter("mid", mid)
        header("Cookie", "SESSDATA=$sessData;")
    }.body()

    /**
     * 获取用户[mid]的关系统计（关注数，粉丝数，黑名单数）
     */
    suspend fun getRelationStat(
        mid: Long
    ): BiliResponse<RelationStat> = client.get("x/relation/stat") {
        parameter("vmid", mid)
    }.body()

    /**
     * 获取搜索热词
     */
    suspend fun getHotwords(): HotwordResponse =
        client.get("https://s.search.bilibili.com/main/hotword").body()

    /**
     * 获取搜索关键词建议
     *
     * 如果请求不带 [mainVer]，那返回的响应将只会包含 result，但不便于数据处理
     *
     * 如果请求中包含了 [highlight]，在返回的结果中 [KeywordSuggest.Result.tag] 的 name 会包含高亮的 html 标签
     */
    suspend fun getKeywordSuggest(
        term: String,
        mainVer: String = "v1",
        highlight: String? = null
    ): KeywordSuggest {
        val response: KeywordSuggest = client.get("https://s.search.bilibili.com/main/suggest") {
            parameter("term", term)
            parameter("main_ver", mainVer)
            highlight?.let { parameter("highlight", it) }
        }.body()
        if (response.code == 0) {
            runCatching {
                val result = json.decodeFromJsonElement<KeywordSuggest.Result>(response.result!!)
                response.suggests.addAll(result.tag)
            }
        }
        return response
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
    ): BiliResponse<SearchResultData> = client.get("/x/web-interface/wbi/search/all/v2") {
        parameter("keyword", keyword)
        parameter("page", page)
        tid?.let { parameter("tids", it) }
        order?.let { parameter("order", it) }
        duration?.let { parameter("duration", it) }
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
    ): BiliResponse<SearchResultData> = client.get("/x/web-interface/wbi/search/type") {
        parameter("keyword", keyword)
        parameter("search_type", type)
        parameter("page", page)
        tid?.let { parameter("tids", it) }
        order?.let { parameter("order", it) }
        duration?.let { parameter("duration", it) }
    }.body()
}