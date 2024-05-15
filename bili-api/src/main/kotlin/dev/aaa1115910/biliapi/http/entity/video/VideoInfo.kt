package dev.aaa1115910.biliapi.http.entity.video

import dev.aaa1115910.biliapi.http.entity.subtitle.Subtitle
import dev.aaa1115910.biliapi.http.entity.user.Staff
import dev.aaa1115910.biliapi.http.entity.user.UserGarb
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonPrimitive

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
 * @param premiere 首播状态
 * @param teenageMode
 * @param isChargeableSeason
 * @param isStory
 * @param noCache
 * @param pages 视频分P列表
 * @param subtitle 视频CC字幕信息
 * @param staff 合作成员列表 非合作视频无此项
 * @param ugcSeason 合集信息
 * @param isSeasonDisplay 是否为合集
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
 * @param rcmdReason 热门推荐理由
 */
@Serializable
data class VideoInfo(
    val bvid: String,
    val aid: Long,
    val videos: Int,
    val tid: Int,
    val tname: String,
    val copyright: Int,
    val pic: String,
    val title: String,
    val pubdate: Int,
    val ctime: Int = 0,
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
    val cid: Long,
    val dimension: Dimension,
    val premiere: Premiere? = null,
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
    @SerialName("ugc_season")
    val ugcSeason: UgcSeason? = null,
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
    val ogvInfo: String? = null,
    @SerialName("rcmd_reason")
    private val _rcmdReason: JsonElement? = null,
    var rcmdReason: RcmdReason? = null,
) {
    init {
        rcmdReason = if (_rcmdReason == null) {
            null
        } else if (_rcmdReason is JsonObject) {
            Json.decodeFromJsonElement<RcmdReason>(_rcmdReason)
        } else {
            val reason = _rcmdReason.jsonPrimitive.content
            if (reason == "") null else RcmdReason(content = reason, cornerMark = 0)
        }
    }

    @Serializable
    data class RcmdReason(
        val content: String,
        @SerialName("corner_mark")
        val cornerMark: Int
    )
}

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
    val noBackground: Int? = null,
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
    val aid: Long = 0,
    val view: Int = 0,
    val danmaku: Int = 0,
    val reply: Int = 0,
    val favorite: Int = 0,
    val coin: Int = 0,
    val share: Int = 0,
    @SerialName("now_rank")
    val nowRank: Int = 0,
    @SerialName("his_rank")
    val hisRank: Int = 0,
    val like: Int = 0,
    val dislike: Int = 0,
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

@Serializable
data class Premiere(
    val state: Int,
    @SerialName("start_time")
    val startTime: Long,
    @SerialName("room_id")
    val roomId: Int
)

/**
 * 视频分P
 *
 * @param cid 分P cid
 * @param page 分P序号 从1开始
 * @param from 视频来源 vupload：普通上传（B站） hunan：芒果TV qq：腾讯
 * @param part 分P标题
 * @param duration 分P持续时间 单位为秒
 * @param vid 站外视频vid	仅站外视频有效
 * @param weblink 站外视频跳转url 仅站外视频有效
 * @param dimension 当前分P分辨率 部分较老视频无分辨率值
 */
@Serializable
data class VideoPage(
    val cid: Long,
    val page: Int,
    val from: String,
    val part: String,
    val duration: Int,
    val vid: String,
    val weblink: String,
    val dimension: Dimension
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
    val aid: Long,
    val type: Int,
    val desc: String,
    @SerialName("weekly_recommend_num")
    val weeklyRecommendNum: Int
)