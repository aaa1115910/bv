package dev.aaa1115910.biliapi.http.entity.search

import dev.aaa1115910.biliapi.http.entity.user.OfficialVerify
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonElement

@Serializable
sealed class SearchResultItem

/**
 * 活动(activity)
 */
@Serializable
data class SearchActivityResult(
    val status: Int,
    val author: String,
    val url: String,
    val title: String,
    val cover: String,
    val pos: Int,
    @SerialName("card_type")
    val cardType: Int,
    val state: Int,
    val position: Int,
    val corner: String,
    @SerialName("card_value")
    val cardValue: String,
    val type: String,
    val id: Int,
    val desc: String
) : SearchResultItem()

/**
 * 专栏(article)
 */
@Serializable
data class SearchArticleResult(
    @SerialName("pub_time")
    val pubTime: Int,
    val like: Int,
    val title: String,
    @SerialName("rank_offset")
    val rankOffset: Int,
    val mid: Long,
    @SerialName("image_urls")
    val imageUrls: List<String>,
    @SerialName("template_id")
    val templateId: Int,
    @SerialName("category_id")
    val categoryId: Int,
    @SerialName("sub_type")
    val subType: Int,
    val version: String,
    val view: Int,
    val reply: Int,
    @SerialName("rank_index")
    val rankIndex: Int,
    val desc: String,
    @SerialName("rank_score")
    val rankScore: Int,
    val type: String,
    val id: Int,
    @SerialName("category_name")
    val categoryName: String
) : SearchResultItem()

/**
 * 用户(bili_user)
 */
@Serializable
data class SearchBiliUserResult(
    val type: String,
    val mid: Long,
    val uname: String,
    val usign: String,
    val fans: Int,
    val videos: Int,
    val upic: String,
    @SerialName("face_nft")
    val faceNft: Int,
    @SerialName("face_nft_type")
    val faceNftType: Int,
    @SerialName("verify_info")
    val verifyInfo: String,
    val level: Int,
    val gender: Int,
    @SerialName("is_upuser")
    val isUpUser: Int,
    @SerialName("is_live")
    val isLive: Int,
    @SerialName("room_id")
    val roomId: Int,
    val res: List<JsonElement>,
    @SerialName("official_verify")
    val officialVerify: OfficialVerify,
    @SerialName("hit_columns")
    val hitColumns: List<String>,
    @SerialName("is_senior_member")
    val isSeniorMember: Int
) : SearchResultItem()

/**
 * 番剧(media_bangumi) 影视(mdeia_ft)
 *
 * @param type 结果类型 media_bangumi：番剧 media_ft：影视
 * @param mediaId 剧集mdid
 * @param title 剧集标题 关键字用xml标签<em class="keyword">标注
 * @param orgTitle 剧集原名 关键字用xml标签<em class="keyword">标注 可为空
 * @param mediaType 剧集类型 1：番剧 2：电影 3：纪录片 4：国创 5：电视剧 7：综艺
 * @param cv 声优
 * @param staff 制作组
 * @param seasonId 剧集ssid
 * @param isAvid
 * @param hitColumns 关键字匹配类型
 * @param hitEpids 关键字匹配分集标题的分集epid 多个用,分隔
 * @param seasonType 剧集类型	1：番剧 2：电影 3：纪录片 4：国创 5：电视剧 7：综艺
 * @param seasonTypeName 剧集类型文字
 * @param selectionStyle 分集选择按钮风格 horizontal：横排式 grid：按钮式
 * @param epSize 结果匹配的分集数
 * @param url 剧集重定向url
 * @param buttonText 观看按钮文字
 * @param isFollow 是否追番 需要登录(SESSDATA) 未登录则恒为0 0：否 1：是
 * @param isSelection
 * @param eps 结果匹配的分集信息
 * @param badges 剧集标志信息
 * @param cover 剧集封面url
 * @param areas 地区
 * @param styles 风格
 * @param gotoUrl 剧集重定向url
 * @param desc 简介
 * @param pubTime 开播时间 时间戳
 * @param mediaMode
 * @param fixPubTimeStr 开播时间重写信息 优先级高于[pubTime] 可为空
 * @param mediaScore 评分信息
 * @param displayInfo 剧集标志信息
 * @param pgcSeasonId 剧集ssid
 * @param corner 角标有无 2：无 13：有
 * @param indexShow
 */
@Serializable
data class SearchMediaResult(
    val type: String,
    @SerialName("media_id")
    val mediaId: Int,
    val title: String,
    @SerialName("org_title")
    val orgTitle: String,
    @SerialName("media_type")
    val mediaType: Int,
    val cv: String,
    val staff: String,
    @SerialName("season_id")
    val seasonId: Int,
    @SerialName("is_avid")
    val isAvid: Boolean,
    @SerialName("hit_columns")
    val hitColumns: List<String>? = null,
    @SerialName("hit_epids")
    val hitEpids: String,
    @SerialName("season_type")
    val seasonType: Int,
    @SerialName("season_type_name")
    val seasonTypeName: String,
    @SerialName("selection_style")
    val selectionStyle: String,
    @SerialName("ep_size")
    val epSize: Int,
    val url: String,
    @SerialName("button_text")
    val buttonText: String,
    @SerialName("is_follow")
    val isFollow: Int,
    @SerialName("is_selection")
    val isSelection: Int,
    val eps: List<SearchMediaEpisode>? = null,
    val badges: List<Badge>? = null,
    val cover: String,
    val areas: String,
    val styles: String,
    @SerialName("goto_url")
    val gotoUrl: String,
    val desc: String,
    @SerialName("pubtime")
    val pubTime: Int,
    @SerialName("media_mode")
    val mediaMode: Int,
    @SerialName("fix_pubtime_str")
    val fixPubTimeStr: String,
    @SerialName("media_score")
    val mediaScore: MediaScore,
    @SerialName("display_info")
    val displayInfo: List<Badge>? = null,
    @SerialName("pgc_season_id")
    val pgcSeasonId: Int,
    val corner: Int,
    @SerialName("index_show")
    val indexShow: String
) : SearchResultItem() {

    /**
     * 分集信息
     *
     * @param id 分集epid
     * @param cover 分集封面url
     * @param title 完整标题
     * @param url 分集重定向url
     * @param releaseDate
     * @param badges 分集标志
     * @param indexTitle 短标题
     * @param longTitle 单集标题
     */
    @Serializable
    data class SearchMediaEpisode(
        val id: Int,
        val cover: String,
        val title: String,
        val url: String,
        @SerialName("release_date")
        val releaseDate: String,
        val badges: List<Badge>? = null,
        @SerialName("index_title")
        val indexTitle: String,
        @SerialName("long_title")
        val longTitle: String
    )

    /**
     * 评分信息
     *
     * @param score 评分
     * @param userCount 总计评分人数
     */
    @Serializable
    data class MediaScore(
        val score: Float,
        @SerialName("user_count")
        val userCount: Int
    )

    /**
     * @param text 剧集标志
     * @param textColor 文字颜色
     * @param textColorNight 夜间文字颜色
     * @param bgColor 背景颜色
     * @param bgColorNight 夜间背景颜色
     * @param borderColor 背景颜色
     * @param borderColorNight 夜间背景颜色
     * @param bgStyle
     */
    @Serializable
    data class Badge(
        val text: String,
        @SerialName("text_color")
        val textColor: String,
        @SerialName("text_color_night")
        val textColorNight: String,
        @SerialName("bg_color")
        val bgColor: String,
        @SerialName("bg_color_night")
        val bgColorNight: String,
        @SerialName("border_color")
        val borderColor: String,
        @SerialName("border_color_night")
        val borderColorNight: String,
        @SerialName("bg_style")
        val bgStyle: Int
    )
}

/**
 * 话题(topic)
 *
 * @param type 结果类型 固定为topic
 * @param description 简介
 * @param pubDate 发布时间 时间戳
 * @param title 标题
 * @param favourite
 * @param hitColumns 关键字匹配类型
 * @param review
 * @param rankOffset 搜索结果排名值
 * @param cover 话题封面url
 * @param update 上传时间 时间戳
 * @param mid UP主mid
 * @param click
 * @param tpType
 * @param keyword
 * @param tpId 话题id
 * @param rankIndex
 * @param author UP主昵称
 * @param arcUrl 话题页面重定向url
 * @param rankScore 结果排序量化值
 */
@Serializable
data class SearchTopicResult(
    val description: String,
    @SerialName("pubdate")
    val pubDate: Int,
    val title: String,
    val favourite: Int,
    @SerialName("hit_columns")
    val hitColumns: List<String>,
    val review: Int,
    @SerialName("rank_offset")
    val rankOffset: Int,
    val cover: String,
    val update: Int,
    val mid: Long,
    val click: Int,
    @SerialName("tp_type")
    val tpType: Int,
    val keyword: String,
    @SerialName("tp_id")
    val tpId: Int,
    @SerialName("rank_index")
    val rankIndex: Int,
    val author: String,
    val type: String,
    @SerialName("arcurl")
    val arcUrl: String,
    @SerialName("rank_score")
    val rankScore: Int
) : SearchResultItem()

/**
 * 视频(video)
 *
 * @param type 结果类型 固定为video
 * @param id 稿件avid
 * @param author UP主昵称
 * @param mid UP主mid
 * @param typeId 视频分区tid
 * @param typeName 视频子分区名
 * @param arcUrl 视频重定向url
 * @param aid 稿件avid
 * @param bvid 稿件bvid
 * @param title 视频标题 关键字用xml标签<em class="keyword">标注
 * @param description 视频简介
 * @param arcRank
 * @param pic 视频封面url
 * @param play 视频播放量
 * @param videoReview 视频弹幕量
 * @param favorites 视频收藏数
 * @param tag 视频TAG 每项TAG用,分隔
 * @param review 视频评论数
 * @param pubDate 视频投稿时间 时间戳
 * @param sendDate 视频发布时间 时间戳
 * @param duration 视频时长 HH:MM
 * @param badgePay
 * @param hitColumns 关键字匹配类型
 * @param viewType
 * @param isPay
 * @param isUnionVideo 是否为合作视频 0：否 1：是
 * @param recTags
 * @param newRecTags
 * @param rankScore 结果排序量化值
 * @param like
 * @param upic
 * @param corner
 * @param cover
 * @param desc
 * @param url
 * @param recReason
 * @param danmaku
 * @param bizData
 * @param isChargeVideo
 */
@Serializable
data class SearchVideoResult(
    val type: String,
    val id: Long,
    val author: String,
    val mid: Long,
    @SerialName("typeid")
    val typeId: String,
    @SerialName("typename")
    val typeName: String,
    @SerialName("arcurl")
    val arcUrl: String,
    val aid: Long,
    val bvid: String,
    val title: String,
    val description: String,
    @SerialName("arcrank")
    val arcRank: String,
    val pic: String,
    val play: Int,
    @SerialName("video_review")
    val videoReview: Int,
    val favorites: Int,
    val tag: String,
    val review: Int,
    @SerialName("pubdate")
    val pubDate: Int,
    @SerialName("senddate")
    val sendDate: Int,
    val duration: String,
    @SerialName("badgepay")
    val badgePay: Boolean,
    @SerialName("hit_columns")
    val hitColumns: List<String>,
    @SerialName("view_type")
    val viewType: String,
    @SerialName("is_pay")
    val isPay: Int,
    @SerialName("is_union_video")
    val isUnionVideo: Int,
    @SerialName("rec_tags")
    val recTags: JsonElement? = null,
    @SerialName("new_rec_tags")
    val newRecTags: List<JsonElement>,
    @SerialName("rank_score")
    val rankScore: Int,
    val like: Int,
    val upic: String,
    val corner: String,
    val cover: String,
    val desc: String,
    val url: String,
    @SerialName("rec_reason")
    val recReason: String,
    val danmaku: Int,
    @SerialName("biz_data")
    val bizData: JsonElement? = null,
    @SerialName("is_charge_video")
    val isChargeVideo: Int = 0,
    val vt: Int = 0,
    @SerialName("enable_vt")
    private val _enableVt: Int = 0,
    @Transient
    val enableVt: Boolean = _enableVt == 1,
    @SerialName("vt_display")
    val vtDisplay: String,
    val subtitle: String,
    @SerialName("episode_count_text")
    val episodeCountText: String,
    @SerialName("release_status")
    val releaseStatus: Int,
    @SerialName("is_intervene")
    val isIntervene: Int
) : SearchResultItem()