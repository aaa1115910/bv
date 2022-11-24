package dev.aaa1115910.bv

import android.util.Xml
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsChannel
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import org.xmlpull.v1.XmlPullParser

object BiliApi {
    private var endPoint: String = ""
    private lateinit var client: HttpClient

    private val logger = KotlinLogging.logger { }

    init {
        createClient()
    }

    private fun createClient() {
        client = HttpClient(OkHttp) {
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
        }
    }

    /**
     * 获取热门视频列表
     */
    suspend fun getPopularVideoData(
        pageNumber: Int = 1,
        pageSize: Int = 20
    ): PopularVideosResponse = client.get("https://api.bilibili.com/x/web-interface/popular") {
        parameter("pn", pageNumber)
        parameter("ps", pageSize)
    }.body()

    /**
     * 获取视频详细信息
     */
    suspend fun getVideoInfo(
        av: Int? = null,
        bv: String? = null
    ): VideoInfoResponse = client.get("https://api.bilibili.com/x/web-interface/view") {
        parameter("aid", av)
        parameter("bvid", bv)
    }.body()

    /**
     * 获取视频流
     */
    suspend fun getVideoPlayUrl(
        av: Int? = null,
        bv: Int? = null,
        cid: Int,
        qn: Int? = null,
        fnval: Int? = null,
        fnver: Int? = null,
        fourk: Int? = 0,
        session: String? = null,
        otype: String = "json",
        type: String = "",
        platform: String = "oc"
    ): PlayUrlResponse = client.get("https://api.bilibili.com/x/player/playurl") {
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
    }.body()

    suspend fun getDanmakuXml(
        cid: Int
    ): DanmakuResponse {
        val xmlChannel = client.get("https://api.bilibili.com/x/v1/dm/list.so") {
            parameter("oid", cid)
        }.bodyAsChannel()

        val xml = Xml.newPullParser()
        xml.setInput(xmlChannel.toInputStream().reader())

        var chatserver = ""
        var chatId = 0
        var maxLimit = 0
        var state = 0
        var realName = 0
        var source = ""

        val data = mutableListOf<DanmakuData>()

        var eventType = xml.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    when (xml.name) {
                        "chatserver" -> {
                            xml.next()
                            chatserver = xml.text
                        }

                        "chatid" -> {
                            xml.next()
                            chatId = xml.text.toInt()
                        }

                        "maxlimit" -> {
                            xml.next()
                            maxLimit = xml.text.toInt()
                        }

                        "state" -> {
                            xml.next()
                            state = xml.text.toInt()
                        }

                        "real_name" -> {
                            xml.next()
                            realName = xml.text.toInt()
                        }

                        "source" -> {
                            xml.next()
                            source = xml.text
                        }

                        "d" -> {
                            val p = xml.getAttributeValue(0)
                            xml.next()
                            val text = xml.text
                            data.add(DanmakuData.fromString(p, text))
                        }
                    }
                }
            }
            eventType = xml.next();
        }

        val response = DanmakuResponse(chatserver, chatId, maxLimit, state, realName, source, data)
        return response
    }

    /*
    suspend fun getDanmakuXml(
        cid: Int
    ) :String{
        val url="https://api.bilibili.com/x/v1/dm/list.so?oid=$cid"
        val okhttp=OkHttpClient()
        val call = okhttp.newCall(Request.Builder().url(url).build())
        val body= call.execute().body!!.string()
        val xmlFormat = XML {
            unknownChildHandler = UnknownChildHandler { _, _, _, _, _ -> emptyList() }
        }
        val a = xmlFormat.decodeFromString(DanmakuResponse.serializer(), body)

        return a.toString()
    }

     */
}

fun main() {
    runBlocking {
        val data = BiliApi.getDanmakuXml(cid = 267714)
        println(data)
    }
}

@Serializable
data class PopularVideosResponse(
    val code: Int,
    val message: String,
    val ttl: Int,
    val data: PopularVideoData
)

@Serializable
data class VideoInfoResponse(
    val code: Int,
    val message: String,
    val ttl: Int,
    val data: VideoInfo
)

@Serializable
data class PopularVideoData(
    val list: List<VideoInfo>,
    @SerialName("no_more")
    val noMore: Boolean
)

/**
 * 视频详细信息
 *
 * @param bvid 稿件bvid
 * @param aid 稿件avid
 * @param videos 稿件分P总数 默认为1
 * @param tid 分区tid
 * @param tname 子分区名称
 * @param copyright 视频类型 1：原创 2：转载
 * @param pic 稿件封面图片url
 * @param title 稿件标题
 * @param pubdate 稿件发布时间 秒级时间戳
 * @param ctime 用户投稿时间 秒级时间戳
 * @param desc 视频简介
 * @param state 视频状态
 * @param duration 稿件总时长(所有分P) 单位为秒
 * @param forward 撞车视频跳转avid 仅撞车视频存在此字段
 * @param missionId 稿件参与的活动id
 * @param redirectUrl 稿重定向url	仅番剧或影视视频存在此字段，用于番剧&影视的av/bv->ep
 * @param rights 视频属性标志
 * @param owner 视频UP主信息
 * @param stat 视频状态数
 * @param dynamic 视频同步发布的的动态的文字内容
 * @param cid 视频1P cid
 * @param dimension 视频1P分辨率
 * @param premiere null
 * @param teenageMode
 * @param isChargeableSeason
 * @param isStory
 * @param noCache
 * @param pages 视频分P列表
 * @param subtitle 视频CC字幕信息
 * @param staff 合作成员列表 非合作视频无此项
 * @param isSeasonDisplay
 * @param userGarb 用户装扮信息
 * @param honorReply
 * @param likeIcon
 * @param shortLink
 * @param shortLinkV2
 * @param firstFrame
 * @param pubLocation
 * @param seasonType
 * @param isOgv
 * @param ogvInfo
 *
 */
@Serializable
data class VideoInfo(
    val bvid: String,
    val aid: Int,
    val videos: Int,
    val tid: Int,
    val tname: String,
    val copyright: Int,
    val pic: String,
    val title: String,
    val pubdate: Int,
    val ctime: Int,
    val desc: String,
    val state: Int,
    val duration: Int,
    val forward: Int? = null,
    @SerialName("mission_id")
    val missionId: Int? = null,
    @SerialName("redirect_url")
    val redirectUrl: String? = null,
    val rights: VideoRights,
    val owner: VideoOwner,
    val stat: VideoStat,
    val dynamic: String,
    val cid: Int,
    val dimension: Dimension,
    val premiere: String? = null,
    @SerialName("teenage_mode")
    val teenageMode: Int = 0,
    @SerialName("is_chargeable_season")
    val isChargeableSeason: Boolean = false,
    @SerialName("is_story")
    val isStory: Boolean = false,
    @SerialName("no_cache")
    val noCache: Boolean = false,
    val pages: List<VideoPage> = emptyList(),
    val subtitle: Subtitle? = null,
    val staff: List<Staff> = emptyList(),
    @SerialName("is_season_display")
    val isSeasonDisplay: Boolean = false,
    @SerialName("user_garb")
    val userGarb: UserGarb? = null,
    @SerialName("honor_reply")
    val honorReply: HonorReply? = null,
    @SerialName("like_icon")
    val likeIcon: String? = null,
    @SerialName("short_link")
    val shortLink: String? = null,
    @SerialName("short_link_v2")
    val shortLinkV2: String? = null,
    @SerialName("first_frame")
    val firstFrame: String? = null,
    @SerialName("pub_location")
    val pubLocation: String? = null,
    @SerialName("season_type")
    val seasonType: Int? = null,
    @SerialName("is_ogv")
    val isOgv: Boolean = false,
    @SerialName("ogv_info")
    val ogvInfo: String? = null
)

/**
 * 视频属性标志
 *
 * @param bp 是否允许承包
 * @param elec 是否支持充电
 * @param download 是否允许下载
 * @param movie 是否电影
 * @param pay 是否PGC付费
 * @param hd5 是否有高码率
 * @param noReprint 是否显示“禁止转载”标志
 * @param autoplay 是否自动播放
 * @param ugcPay 是否UGC付费
 * @param isCooperation 是否为联合投稿
 * @param ugcPayPreview
 * @param noBackground
 * @param cleanMode
 * @param isSteinGate 是否为互动视频
 * @param is360 是否为全景视频
 * @param noShare
 * @param arcPay
 * @param payFreeWatch
 */
@Serializable
data class VideoRights(
    val bp: Int,
    val elec: Int,
    val download: Int,
    val movie: Int,
    val pay: Int,
    val hd5: Int,
    @SerialName("no_reprint")
    val noReprint: Int,
    val autoplay: Int,
    @SerialName("ugc_pay")
    val ugcPay: Int,
    @SerialName("is_cooperation")
    val isCooperation: Int,
    @SerialName("ugc_pay_preview")
    val ugcPayPreview: Int,
    @SerialName("no_background")
    val noBackground: Int,
    @SerialName("clean_mode")
    val cleanMode: Int? = null,
    @SerialName("is_stein_gate")
    val isSteinGate: Int? = null,
    @SerialName("is_360")
    val is360: Int? = null,
    @SerialName("no_share")
    val noShare: Int? = null,
    @SerialName("arc_pay")
    val arcPay: Int,
    @SerialName("pay_free_watch")
    val payFreeWatch: Int? = null
)

/**
 * 视频作者
 *
 * @param mid UP主mid
 * @param name UP主昵称
 * @param face UP主头像
 */
@Serializable
data class VideoOwner(
    val mid: Long,
    val name: String,
    val face: String
)

/**
 * 视频数据
 *
 * @param aid 稿件avid
 * @param view 播放数
 * @param danmaku 弹幕数
 * @param reply 评论数
 * @param favorite 收藏数
 * @param coin 投币数
 * @param share 分享数
 * @param nowRank 当前排名
 * @param hisRank 历史最高排行
 * @param like 获赞数
 * @param dislike 点踩数	恒为0
 * @param evaluation 视频评分
 * @param argueMsg 警告/争议提示信息
 */
@Serializable
data class VideoStat(
    val aid: Int,
    val view: Int,
    val danmaku: Int,
    val reply: Int,
    val favorite: Int,
    val coin: Int,
    val share: Int,
    @SerialName("now_rank")
    val nowRank: Int,
    @SerialName("his_rank")
    val hisRank: Int,
    val like: Int,
    val dislike: Int,
    val evaluation: String = "",
    @SerialName("argue_msg")
    val argueMsg: String = ""
)

/**
 * 分辨率
 *
 * @param width 当前分P 宽度
 * @param height 当前分P 高度
 * @param rotate 是否将宽高对换 0：正常,1：对换
 */
@Serializable
data class Dimension(
    val width: Int,
    val height: Int,
    val rotate: Int
)

/**
 * 视频分P
 *
 * @param cid 分P cid
 * @param page 分P序号 从1开始
 * @param from 视频来源 vupload：普通上传（B站）
 * @param hunan：芒果TV
 * @param qq：腾讯
 * @param part 分P标题
 * @param duration 分P持续时间 单位为秒
 * @param vid 站外视频vid	仅站外视频有效
 * @param weblink 站外视频跳转url 仅站外视频有效
 * @param dimension 当前分P分辨率 部分较老视频无分辨率值
 */
@Serializable
data class VideoPage(
    val cid: Int,
    val page: Int,
    val from: String,
    val part: String,
    val duration: Int,
    val vid: String,
    val weblink: String,
    val dimension: Dimension
)

/**
 * 字幕信息
 * @param allowSubmit 是否允许提交字幕
 * @param list 字幕列表
 */
@Serializable
data class Subtitle(
    val allowSubmit: Boolean = false,
    val list: List<SubtitleListItem> = emptyList()
)

/**
 * 字幕列表项
 *
 * @param id 字幕id
 * @param lan 字幕语言
 * @param lanDoc 字幕语言名称
 * @param isLock 是否锁定
 * @param authorMid 字幕上传者mid
 * @param subtitleUrl json格式字幕文件url
 * @param author 字幕上传者信息
 */
@Serializable
data class SubtitleListItem(
    val id: Long,
    val lan: String,
    @SerialName("lan_doc")
    val lanDoc: String,
    @SerialName("is_lock")
    val isLock: Boolean,
    @SerialName("author_mid")
    val authorMid: Int? = null,
    @SerialName("subtitle_url")
    val subtitleUrl: String,
    val author: SubtitleAuthor
)

/**
 * 字幕作者
 *
 * @param mid 字幕上传者mid
 * @param name 字幕上传者昵称
 * @param sex 字幕上传者性别 男 女 保密
 * @param face 字幕上传者头像url
 * @param sign 字幕上传者签名
 * @param rank 10000 作用尚不明确
 * @param birthday 0 作用尚不明确
 * @param isFakeAccount 0 作用尚不明确
 * @param isDeleted 0 作用尚不明确
 */
@Serializable
data class SubtitleAuthor(
    val mid: Int,
    val name: String,
    val sex: String,
    val face: String,
    val sign: String,
    val rank: Int,
    val birthday: Int,
    @SerialName("is_fake_account")
    val isFakeAccount: Int,
    @SerialName("is_deleted")
    val isDeleted: Int
)

/**
 * 创作者个人信息
 *
 * @param mid 成员mid
 * @param title 成员名称
 * @param name 成员昵称
 * @param face 成员头像url
 * @param vip 成员大会员状态
 * @param official 成员认证信息
 * @param follower 成员粉丝数
 */
@Serializable
data class Staff(
    val mid: Int,
    val title: String,
    val name: String,
    val face: String,
    val vip: Vip,
    val official: Official,
    val follower: Int
)

/**
 * 会员信息
 *
 * @param type 成员会员类型 0：无 1：月会员 2：年会员
 * @param status 会员状态 0：无 1：有
 * @param themeType num 0
 */
@Serializable
data class Vip(
    val type: Int,
    val status: Int,
    @SerialName("theme_type")
    val themeType: Int
)

/**
 * 认证信息
 *
 * @param role 成员认证级别 0：无 1 2 7：个人认证 3 4 5 6：机构认证
 * @param title 成员认证名 无为空
 * @param desc  成员认证备注 无为空
 * @param type 成员认证类型 -1：无 0：有
 */
@Serializable
data class Official(
    val role: Int,
    val title: String,
    val desc: String,
    val type: Int
)

/**
 * 用户头像挂件
 *
 * @param urlImageAniCut
 */
@Serializable
data class UserGarb(
    @SerialName("url_image_ani_cut")
    val urlImageAniCut: String
)

/**
 * 推荐理由
 *
 * @param honor
 */
@Serializable
data class HonorReply(
    val honor: List<HonorReplyItem> = emptyList()
)

/**
 * 推荐信息
 *
 * @param aid 当前稿件aid
 * @param type 2：第?期每周必看 3：全站排行榜最高第?名 4：热门
 * @param desc 描述
 * @param weeklyRecommendNum
 */
@Serializable
data class HonorReplyItem(
    val aid: Int,
    val type: Int,
    val desc: String,
    @SerialName("weekly_recommend_num")
    val weeklyRecommendNum: Int
)

@Serializable
enum class VideoQuality(val qn: Int, val displayName: String) {
    Q260P(6, "240P 极速"),
    Q360P(16, "360P 流畅"),
    Q480P(32, "480P 清晰"),
    Q720P(64, ""),
    Q720P60(74, ""),
    Q1080P(80, ""),
    Q1080PPlus(112, ""),
    Q1080P60(116, ""),
    Q4K(120, ""),
    HDR(125, ""),
    Dolby(126, ""),
    Q8K(127, "")
}

@Serializable
data class PlayUrlResponse(
    val code: Int,
    val message: String,
    val ttl: Int,
    val data: PlayUrlData? = null
)

/**
 * @param from local 作用尚不明确
 * @param result suee 作用尚不明确
 * @param message 空 作用尚不明确
 * @param quality 当前的视频分辨率代码 值含义见上表
 * @param format 视频格式
 * @param timeLength 视频长度（毫秒值） 单位为毫秒 不同分辨率/格式可能有略微差异
 * @param acceptFormat 视频支持的全部格式 每项用,分隔
 * @param acceptDescription 视频支持的分辨率列表
 * @param acceptQuality 视频支持的分辨率代码列表 值含义见上表
 * @param videoCodecId 默认选择视频流的编码id 见视频编码代码
 * @param seekParam 固定值：start 作用尚不明确
 * @param seekType offset（dash、flv） second（mp4） 作用尚不明确
 * @param durl 视频分段 注：仅flv/mp4存在此项
 * @param dash dash音视频流信息 注：仅dash存在此项
 * @param supportFormats 支持格式的详细信息
 * @param high_format null
 * @param lastPlayTime 上次播放进度	毫秒值
 * @param lastPlayCid 上次播放分p的cid
 */
@Serializable
data class PlayUrlData(
    val from: String,
    val result: String,
    val message: String,
    val quality: Int,
    val format: String,
    @SerialName("timelength")
    val timeLength: Int,
    @SerialName("accept_format")
    val acceptFormat: String,
    @SerialName("accept_description")
    val acceptDescription: List<String> = emptyList(),
    @SerialName("accept_quality")
    val acceptQuality: List<Int> = emptyList(),
    @SerialName("video_codecid")
    val videoCodecId: Int,
    @SerialName("seek_param")
    val seekParam: String,
    @SerialName("seek_type")
    val seekType: String,
    val durl: List<Durl> = emptyList(),
    val dash: Dash? = null,
    @SerialName("support_formats")
    val supportFormats: List<SupportFormat> = emptyList(),
    @SerialName("last_play_time")
    val lastPlayTime: Int,
    @SerialName("last_play_cid")
    val lastPlayCid: Int
)

/**
 * 视频播放地址
 *
 * @param order 视频分段序号 某些视频会分为多个片段（从1顺序增长）
 * @param length 视频长度 单位为毫秒
 * @param size 视频大小 单位为Byte
 * @param ahead 空 作用尚不明确
 * @param vhead 空 作用尚不明确
 * @param url 视频流url 注：url内容存在转义符 有效时间为120min
 * @param backupUrl 备用视频流
 */
@Serializable
data class Durl(
    val order: Int,
    val length: Int,
    val size: Int,
    val ahead: String,
    val vhead: String,
    val url: String,
    @SerialName("backup_url")
    val backupUrl: List<String> = emptyList()
)

//TODO
@Serializable
data class Dash(
    val duration: Int,
    val minBufferTime: Float,
    val video: List<DashData> = emptyList(),
    val audio: List<DashData> = emptyList()
)

@Serializable
data class DashData(
    val id: Int,
    val baseUrl: String,
    val backupUrl: List<String> = emptyList(),
    val mimeType: String,
    val codecs: String,
    val width: Int,
    val height: Int,
    val frameRate: String,
    val sar: String,
    val startWithSap: Int,
    @SerialName("segment_base")
    val segmentBase: SegmentBase,
    @SerialName("codecid")
    val codecId: Int
)

@Serializable
data class SegmentBase(
    val initialization: String,
    @SerialName("index_range")
    val indexRange: String
)

/**
 * 支持的视频格式
 *
 * @param quality 视频清晰度代码
 * @param format 视频格式
 * @param newDescription 格式描述
 * @param displayDesc 格式描述
 * @param superScript (?)
 * @param codecs 可用编码格式列表
 */
@Serializable
data class SupportFormat(
    val quality: Int,
    val format: String,
    @SerialName("new_description")
    val newDescription: String,
    @SerialName("display_desc")
    val displayDesc: String,
    @SerialName("superscript")
    val superScript: String,
    val codecs: List<String>? = emptyList()
)

data class DanmakuResponse(
    val chatserver: String,
    val chatId: Int,
    val maxLimit: Int,
    val state: Int,
    val realName: Int,
    val source: String,
    val data: List<DanmakuData> = emptyList()
)

data class DanmakuData(
    val time: Float,
    val type: Int,
    val size: Int,
    val color: Int,
    val timestamp: Int,
    val pool: Int,
    val midHash: String,
    val dmid: Long,
    val level: Int,
    val text: String
) {
    companion object {
        fun fromString(p: String, text: String): DanmakuData {
            val data = p.split(",")
            return DanmakuData(
                time = data[0].toFloat(),
                type = data[1].toInt(),
                size = data[2].toInt(),
                color = data[3].toInt(),
                timestamp = data[4].toInt(),
                pool = data[5].toInt(),
                midHash = data[6],
                dmid = data[7].toLong(),
                level = data[8].toInt(),
                text = text
            )
        }
    }
}