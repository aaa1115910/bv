package dev.aaa1115910.biliapi

import dev.aaa1115910.biliapi.entity.BiliResponse
import dev.aaa1115910.biliapi.entity.BiliResponseWithoutData
import dev.aaa1115910.biliapi.entity.danmaku.DanmakuData
import dev.aaa1115910.biliapi.entity.danmaku.DanmakuResponse
import dev.aaa1115910.biliapi.entity.dynamic.DynamicData
import dev.aaa1115910.biliapi.entity.history.HistoryData
import dev.aaa1115910.biliapi.entity.season.SeasonData
import dev.aaa1115910.biliapi.entity.user.MyInfoData
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
import dev.aaa1115910.biliapi.entity.video.VideoInfo
import dev.aaa1115910.biliapi.entity.video.VideoMoreInfo
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.BrowserUserAgent
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
                host = "api.bilibili.com"
            }
        }
    }

    /**
     * ????????????????????????
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
     * ????????????????????????
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
     * ???????????????
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
     * ?????????????????????
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
        //??????????????? referer ??????????????????????????????
        header("referer", "https://www.bilibili.com")
    }.body()

    /**
     * ??????[cid]??????????????????
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
     * ??????????????????
     *
     * @param type ????????????????????? all:?????? video:???????????? pgc:???????????? article?????????
     * @param offset ?????????2?????????????????????????????????????????????????????????offset
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
     * ????????????[uid]???????????????
     */
    suspend fun getUserInfo(
        uid: Long,
        sessData: String = ""
    ): BiliResponse<UserInfoData> = client.get("/x/space/acc/info") {
        parameter("mid", uid)
        header("Cookie", "SESSDATA=$sessData;")
    }.body()


    /**
     * ????????????[uid]???????????????
     *
     * @param uid ??????id
     * @param photo ??????????????????????????????
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
     * ??????[sessData]????????????????????????
     */
    suspend fun getUserSelfInfo(
        sessData: String = ""
    ): BiliResponse<MyInfoData> = client.get("/x/space/myinfo") {
        header("Cookie", "SESSDATA=$sessData;")
    }.body()

    /**
     * ?????????????????????id[max]???????????????[viewAt]????????????
     *
     * @param business ?????? ????????????
     * @param pageSize ????????????
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
     * ???????????????[avid]???[bvid]???????????????????????????
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
     * ???????????????[mediaId]????????????
     */
    suspend fun getFavoriteFolderInfo(
        mediaId: Long,
        sessData: String = ""
    ): BiliResponse<FavoriteFolderInfo> = client.get("/x/v3/fav/folder/info") {
        parameter("media_id", mediaId)
        header("Cookie", "SESSDATA=$sessData;")
    }.body()

    /**
     * ????????????[mid]????????????????????????
     *
     * @param type ?????????????????? ??????????????? 0????????? 2???????????????
     * @param rid ????????????id ???????????????????????????avid
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
     * ???????????????[mediaId]???????????????
     *
     * @param tid ??????tid ????????????????????? 0???????????????
     * @param keyword ???????????????
     * @param order ???????????? ???????????????:mtime ????????????: view ??????????????????pubtime
     * @param type ???????????? 0???????????????????????????media_id??? 1??????????????????
     * @param pageSize ???????????? ????????????1-20
     * @param pageNumber ?????? ?????????1
     * @param platform ???????????? ??????web??????????????????????????????
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
     * ???????????????[mediaId]???????????????id
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
     * ????????????????????????
     *
     * @param aid ??????avid avid???bvid????????????
     * @param bvid ??????bvid avid???bvid????????????
     * @param cid ??????cid ???????????????P
     * @param epid ??????epid
     * @param sid ??????ssid
     * @param mid ????????????mid
     * @param playedTime ?????????????????? ???????????? ?????????0
     * @param realtime ?????????????????? ????????????
     * @param startTs ?????????????????? ?????????
     * @param type ???????????? 3??????????????? 4????????? 10?????????
     * @param subType ??????????????? ???type=4?????????????????? 1????????? 2????????? 3???????????? 4????????? 5???????????? 7?????????
     * @param dt 2
     * @param playType ???????????? 0???????????? 1??????????????? 2????????? 3???????????????
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
     * ????????????[avid]???[cid]???????????????????????????????????????
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
     * ?????????[avid]???[bvid]??????????????????
     *
     * @param like ????????????
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
     * ????????????[avid]???[bvid]???????????????
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
     * ?????????[avid]???[bvid]??????????????????
     *
     * @param like ??????????????????
     * @param multiply ????????????
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
     * ????????????[avid]???[bvid]???????????????
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
     * ?????????[avid]?????????[addMediaIds]??????[delMediaIds]??????
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
     * ????????????[avid]???????????????
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
     * ????????????[mid]????????????
     *
     * @param order ???????????? ?????????pubdate ???????????????pubdate ???????????????click ???????????????stow
     * @param tid ?????????????????? ?????????0 0???????????????????????? ??????tid?????????????????????
     * @param keyword ??????????????? ??????????????????????????????UP???????????????
     * @param pageNumber ??????
     * @param pageSize ???????????? ??????1?????????50
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
     * ????????????[seasonId]???[epId]???????????????????????? ss24439 ep234533?????????????????????
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
        //??????????????? referer ??????????????????????????????
        header("referer", "https://www.bilibili.com")
    }.body()
}