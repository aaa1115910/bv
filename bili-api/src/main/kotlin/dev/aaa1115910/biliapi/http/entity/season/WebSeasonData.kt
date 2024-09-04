package dev.aaa1115910.biliapi.http.entity.season

import dev.aaa1115910.biliapi.http.entity.user.Pendant
import dev.aaa1115910.biliapi.http.entity.user.Vip
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 剧集信息
 *
 * @param activity 参与的活动
 * @param alias
 * @param bkgCover 网页背景图片url 无则为空
 * @param cover 剧集封面图片url
 * @param episodes 正片剧集列表
 * @param evaluate 简介
 * @param jpTitle 空 作用尚不明确
 * @param link 简介页面url
 * @param mediaId 剧集mdid
 * @param mode 2 作用尚不明确
 * @param newEp obj	更新信息
 * @param payment 会员&付费信息 若无相关内容则无此项
 * @param positive
 * @param publish 发布信息
 * @param rating 评分信息 若无相关内容则无此项
 * @param record 备案号 无则为空
 * @param rights 属性标志信息
 * @param seasonId 番剧ssid
 * @param seasonTitle 剧集标题
 * @param seasons 同系列所有季信息
 * @param section 花絮、PV、番外等非正片内容 若无相关内容则无此项
 * @param series 系列信息
 * @param shareCopy 《{标题}》+{备注}
 * @param shareSubTitle 备注
 * @param shareUrl 番剧播放页面url
 * @param show 网页全屏标志
 * @param squareCover 方形封面图片url
 * @param stat 状态数
 * @param status
 * @param style 剧集风格
 * @param subtitle 剧集副标题
 * @param title 剧集标题
 * @param total 总计正片集数 未完结：大多为-1 已完结：正整数
 * @param type 剧集类型 1：番剧 2：电影 3：纪录片 4：国创 5：电视剧 7：综艺
 * @param upInfo UP主信息 若无相关内容则无此项
 * @param userStatus 用户信息
 */
@Serializable
data class WebSeasonData(
    val activity: Activity,
    val alias: String,
    @SerialName("bkg_cover")
    val bkgCover: String,
    val cover: String,
    val episodes: List<Episode> = emptyList(),
    val evaluate: String,
    @SerialName("jp_title")
    val jpTitle: String,
    val link: String,
    @SerialName("media_id")
    val mediaId: Int,
    val mode: Int,
    @SerialName("new_ep")
    val newEp: NewEP,
    val payment: Payment? = null,
    val positive: Positive,
    val publish: Publish,
    val rating: Rating? = null,
    val record: String,
    val rights: SeasonRights,
    @SerialName("season_id")
    val seasonId: Int,
    @SerialName("season_title")
    val seasonTitle: String,
    val seasons: List<OtherSeason> = emptyList(),
    val section: List<SeasonSection> = emptyList(),
    val series: Series,
    @SerialName("share_copy")
    val shareCopy: String,
    @SerialName("share_sub_title")
    val shareSubTitle: String,
    @SerialName("share_url")
    val shareUrl: String,
    val show: Show,
    @SerialName("square_cover")
    val squareCover: String,
    val stat: SeasonStat,
    val status: Int,
    val styles: List<String> = emptyList(),
    val subtitle: String,
    val title: String,
    val total: Int,
    val type: Int,
    @SerialName("up_info")
    val upInfo: UpInfo? = null,
    @SerialName("user_status")
    val userStatus: UserStatus

) {
    /**
     * 当前推广活动
     *
     * @param headBgUrl
     * @param id 活动id
     * @param title 活动标题
     */
    @Serializable
    data class Activity(
        @SerialName("head_bg_url")
        val headBgUrl: String,
        val id: Int,
        val title: String
    )

    /**
     * 会员&付费信息
     *
     * @param discount 当前折扣 例如 100 代表 100% 不打折
     * @param payType
     * @param price 价格 例如 5.0
     * @param promotion
     * @param tip
     * @param vipDiscount 会员折扣 例如 50 代表 50%
     * @param vipFirstPromotion
     * @param vipPromotion
     */
    @Serializable
    data class Payment(
        val discount: Int,
        @SerialName("pay_type")
        val payType: PayType,
        val price: String,
        val promotion: String,
        val tip: String? = "",
        @SerialName("vip_discount")
        val vipDiscount: Int,
        @SerialName("vip_first_promotion")
        val vipFirstPromotion: String,
        @SerialName("vip_promotion")
        val vipPromotion: String
    ) {
        /**
         * 支付方式
         *
         * @param allowDiscount 允许使用折扣
         * @param allowPack
         * @param allowTicket
         * @param allowTimeLimit
         * @param allowVipDiscount 允许使用会员折扣
         * @param forbidBb 拒绝使用 B 币支付
         */
        @Serializable
        data class PayType(
            @SerialName("allow_discount")
            val allowDiscount: Int,
            @SerialName("allow_pack")
            val allowPack: Int,
            @SerialName("allow_ticket")
            val allowTicket: Int,
            @SerialName("allow_time_limit")
            val allowTimeLimit: Int,
            @SerialName("allow_vip_discount")
            val allowVipDiscount: Int,
            @SerialName("forbid_bb")
            val forbidBb: Int
        )
    }

    /**
     * @param id
     * @param title
     */
    @Serializable
    data class Positive(
        val id: Int,
        val title: String
    )

    /**
     * 系列信息
     *
     * @param displayType
     * @param seriesId 系列id
     * @param seriesTitle 系列名
     */
    @Serializable
    data class Series(
        @SerialName("display_type")
        val displayType: Int,
        @SerialName("series_id")
        val seriesId: Int,
        @SerialName("series_title")
        val seriesTitle: String
    )

    /**
     * 网页全屏标志
     *
     * @param wideScreen 是否全屏	0：正常  1：全屏
     */
    @Serializable
    data class Show(
        @SerialName("wide_screen")
        val wideScreen: Int
    )

    /**
     * 剧集状态数
     *
     * @param coins 投币数
     * @param danmakus 弹幕数
     * @param favorites 收藏数
     * @param likes 点赞数
     * @param reply 评论数
     * @param share 分享数
     * @param views 播放数
     */
    @Serializable
    data class SeasonStat(
        val coins: Int,
        val danmakus: Int,
        val favorites: Int,
        val likes: Int,
        val reply: Int,
        val share: Int,
        val views: Long
    )

    /**
     * UP主信息
     *
     * @param avatar 头像图片url
     * @param avatarSubscriptUrl
     * @param follower 粉丝数
     * @param isFollow 0
     * @param mid UP主mid
     * @param nicknameColor
     * @param pendant  头像框
     * @param themeType 0
     * @param uname UP主昵称
     * @param verifyType
     * @param vipLabel
     * @param vipStatus
     * @param vipType
     */
    @Serializable
    data class UpInfo(
        val avatar: String,
        @SerialName("avatar_subscript_url")
        val avatarSubscriptUrl: String,
        val follower: Int,
        @SerialName("is_follow")
        val isFollow: Int,
        val mid: Long,
        @SerialName("nickname_color")
        val nicknameColor: String,
        val pendant: Pendant,
        @SerialName("theme_type")
        val themeType: Int,
        val uname: String,
        @SerialName("verify_type")
        val verifyType: Int,
        @SerialName("vip_label")
        val vipLabel: Vip.Label,
        @SerialName("vip_status")
        val vipStatus: Int,
        @SerialName("vip_type")
        val vipType: Int
    )

    /**
     * 用户信息
     *
     * @param areaLimit
     * @param banAreaShow
     * @param dialog 开通大会员提示文案，一般只在单独获取用户状态时且用户非会员时出现
     * @param follow 是否追剧
     * @param followStatus
     * @param login 是否已登录
     * @param pay
     * @param payPackPaid
     * @param progress 上次播放进度，仅登录时存在
     * @param sponsor
     * @param vipInfo 用户会员信息，仅登录时存在
     */
    @Serializable
    data class UserStatus(
        @SerialName("area_limit")
        val areaLimit: Int,
        @SerialName("ban_area_show")
        val banAreaShow: Int,
        val dialog: Dialog? = null,
        val follow: Int,
        @SerialName("follow_status")
        val followStatus: Int,
        val login: Int,
        val pay: Int,
        @SerialName("pay_pack_paid")
        val payPackPaid: Int,
        val progress: Progress? = null,
        val sponsor: Int,
        @SerialName("vip_info")
        val vipInfo: VipInfo? = null
    ) {

        /**
         * 开通大会员按钮文案
         */
        @Serializable
        data class Dialog(
            @SerialName("btn_right")
            val btnRight: BtnRight,
            val desc: String,
            val title: String
        ) {
            @Serializable
            data class BtnRight(
                val title: String,
                val type: String
            )
        }

        /**
         * 上次播放进度
         *
         * @param lastEpId 上次播放剧集epid
         * @param lastEpIndex 上次播放剧集标题 [Episode.title]
         * @param lastTime 上次播放时间
         */
        @Serializable
        data class Progress(
            @SerialName("last_ep_id")
            val lastEpId: Int,
            @SerialName("last_ep_index")
            val lastEpIndex: String,
            @SerialName("last_time")
            val lastTime: Int
        )

        /**
         * 用户会员信息
         *
         * @param dueDate 会员到期时间
         * @param status
         * @param type
         */
        @Serializable
        data class VipInfo(
            @SerialName("due_date")
            val dueDate: Long,
            val status: Int,
            val type: Int
        )
    }
}

/**
 * 同系列其它季
 *
 * @param badge 标签，例如“独家”
 * @param badgeInfo
 * @param badgeType
 * @param cover 剧集封面图片url
 * @param horizontalCover 横向封面图片url，仅 App
 * @param link 剧集链接，仅 App
 * @param mediaId 剧集mdid，仅 Web
 * @param newEp  更新信息
 * @param report 仅 App
 * @param resource 仅 App
 * @param seasonId
 * @param seasonTitle 剧集短标题，用于 Tab 处显示
 * @param seasonType
 * @param stat 仅 Web
 * @param title 剧集完整长标题，仅 App
 */
@Serializable
data class OtherSeason(
    val badge: String,
    @SerialName("badge_info")
    val badgeInfo: Episode.BadgeInfo,
    @SerialName("badge_type")
    val badgeType: Int,
    val cover: String,
    @SerialName("horizontal_cover")
    val horizontalCover: String? = null,
    val link: String? = null,
    @SerialName("media_id")
    val mediaId: Int? = null,
    @SerialName("new_ep")
    val newEp: NewEP,
    val report: Report? = null,
    val resource: String? = null,
    @SerialName("season_id")
    val seasonId: Int,
    @SerialName("season_title")
    val seasonTitle: String,
    @SerialName("season_type")
    val seasonType: Int? = null,
    val stat: Stat? = null,
    val title: String? = null
) {
    /**
     * 剧集数据信息
     *
     * @param favorites 单季追剧人数
     * @param seriesFollow 系列追剧人数
     * @param views 播放次数
     */
    @Serializable
    data class Stat(
        val favorites: Int,
        @SerialName("series_follow")
        val seriesFollow: Int,
        val views: Long
    )

    @Serializable
    data class Report(
        @SerialName("display_type")
        val displayType: String,
        @SerialName("season_id")
        val seasonId: String,
        @SerialName("season_type")
        val seasonType: String,
        @SerialName("version_style")
        val versionStyle: String
    )
}

/**
 * 更新信息
 *
 * @param cover 封面
 * @param desc 更新备注
 * @param id 最新一话epid
 * @param indexShow 集数
 * @param _isNew 是否最新发布 0：否 1：是
 * @param title 最新一话标题
 */
@Serializable
data class NewEP(
    val cover: String = "",
    val desc: String = "",
    val id: Int,
    @SerialName("index_show")
    val indexShow: String = "",
    @SerialName("is_new")
    private val _isNew: Int = 0,
    val title: String = ""
) {
    val isNew = _isNew == 1
}

/**
 * 发布信息
 *
 * @param _isFinish 完结状态 0：未完结 1：已完结
 * @param _isStarted 是否发布 0：未发布 1：已发布
 * @param pubTime 发布时间 YYYY-MM-DDD hh:mm:ss
 * @param pubTimeShow 发布时间文字介绍
 * @param releaseDateShow 发布日期文字介绍，仅 App 端，例如 "2020年8月15日开播"
 * @param timeLengthShow 应该可能是更新时间显示，仅 App 端，例如 "已完结，全1话"
 * @param unknowPubDate 0 作用尚不明确
 * @param updateInfoDesc 更新信息，仅 App 端，例如 "更已完结，全1话"
 * @param weekday 0 作用尚不明确
 */
@Serializable
data class Publish(
    @SerialName("is_finish")
    private val _isFinish: Int,
    @SerialName("is_started")
    private val _isStarted: Int,
    @SerialName("pub_time")
    val pubTime: String,
    @SerialName("pub_time_show")
    val pubTimeShow: String,
    @SerialName("release_date_show")
    val releaseDateShow: String? = null,
    @SerialName("time_length_show")
    val timeLengthShow: String? = null,
    @SerialName("unknow_pub_date")
    val unknowPubDate: Int,
    @SerialName("update_info_desc")
    val updateInfoDesc: String? = null,
    val weekday: Int
) {
    val isFinish = _isFinish == 1
    val isStarted = _isStarted == 1
}

/**
 * 评分信息
 *
 * @param count 总计评分人数
 * @param score 评分
 */
@Serializable
data class Rating(
    val count: Int,
    val score: Float
)

/**
 * 属性标志信息
 *
 * @param allowBp
 * @param allowBpRank
 * @param allowDownload
 * @param allowReview
 * @param areaLimit
 * @param banAreaShow
 * @param canWatch
 * @param copyright 版权标志 bilibili：授权 dujia：独家
 * @param copyrightName 版权名称，仅 App 端，例如 “独家”
 * @param forbidPre
 * @param isCoverShow
 * @param isPreview
 * @param onlyVipDownload
 * @param resource
 * @param watchPlatform
 */
@Serializable
data class SeasonRights(
    @SerialName("allow_bp")
    val allowBp: Int,
    @SerialName("allow_bp_rank")
    val allowBpRank: Int,
    @SerialName("allow_download")
    val allowDownload: Int,
    @SerialName("allow_review")
    val allowReview: Int,
    @SerialName("area_limit")
    val areaLimit: Int,
    @SerialName("ban_area_show")
    val banAreaShow: Int,
    @SerialName("can_watch")
    val canWatch: Int,
    val copyright: String,
    @SerialName("copyright_name")
    val copyrightName: String? = null,
    @SerialName("forbid_pre")
    val forbidPre: Int,
    @SerialName("is_cover_show")
    val isCoverShow: Int,
    @SerialName("is_preview")
    val isPreview: Int,
    @SerialName("only_vip_download")
    val onlyVipDownload: Int,
    val resource: String,
    @SerialName("watch_platform")
    val watchPlatform: Int
)