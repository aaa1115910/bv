package dev.aaa1115910.biliapi.http.entity.season

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement

@Serializable
data class AppSeasonData(
    @SerialName("activity_entrance")
    val activityEntrance: List<ActivityEntrance> = emptyList(),
    val actor: Actor,
    val alias: String,
    @SerialName("all_buttons")
    val allButtons: AllButtons,
    @SerialName("all_up_infos")
    val allUpInfos: Map<String, UpInfo> = emptyMap(),
    val areas: List<Area> = emptyList(),
    val badge: String,
    val badgeInfo: Episode.BadgeInfo? = null,
    @SerialName("channel_entrance")
    val channelEntrance: List<ChannelEntrance> = emptyList(),
    val cover: String,
    val detail: String,
    @SerialName("dynamic_subtitle")
    val dynamicSubtitle: String,
    @SerialName("earphone_conf")
    val earphoneConf: EarphoneConf,
    @SerialName("enable_vt")
    val enableVt: Boolean,
    val evaluate: String,
    @SerialName("icon_font")
    val iconFont: IconFont,
    val link: String,
    @SerialName("media_badge_info")
    val mediaBadgeInfo: Episode.BadgeInfo,
    @SerialName("media_id")
    val mediaId: Int,
    val mode: Int,
    val modules: List<Module> = emptyList(),
    @SerialName("new_ep")
    val newEp: NewEP,
    @SerialName("new_keep_activity_material")
    val newKeepActivityMaterial: NewKeepActivityMaterial? = null,
    @SerialName("origin_name")
    val originName: String? = null,
    val payment: Payment,
    @SerialName("play_strategy")
    val playStrategy: PlayStrategy? = null,
    @SerialName("player_icon")
    val playerIcon: PlayerIcon? = null,
    val premieres: JsonArray,
    @SerialName("producer_title")
    val producerTitle: String? = null,
    val publish: Publish,
    val rating: Rating? = null,
    val record: String,
    @SerialName("refine_cover")
    val refineCover: String,
    val reserve: Reserve,
    val right: SeasonRights? = null,
    @SerialName("season_id")
    val seasonId: Int,
    @SerialName("season_title")
    val seasonTitle: String,
    val series: Series,
    @SerialName("share_copy")
    val shareCopy: String,
    @SerialName("share_url")
    val shareUrl: String,
    @SerialName("short_link")
    val shortLink: String,
    @SerialName("show_season_type")
    val showSeasonType: Int,
    @SerialName("square_cover")
    val squareCover: String,
    val staff: Staff,
    val stat: Stat,
    val status: Int,
    val styles: List<Style> = emptyList(),
    val subtitle: String,
    //@SerialName("test_switch")
    //val testSwitch:TestSwitch
    val title: String,
    val total: Int,
    val type: Int,
    @SerialName("type_desc")
    val typeDesc: String,
    @SerialName("type_name")
    val typeName: String,
    @SerialName("user_status")
    val userStatus: UserStatus,
    @SerialName("user_thumbup")
    val userThumbup: UserThumbup? = null
) {
    @Serializable
    data class ActivityEntrance(
        @SerialName("activity_cover")
        val activityCover: String,
        @SerialName("activity_link")
        val activityLink: String,
        @SerialName("activity_subtitle")
        val activitySubtitle: String,
        @SerialName("activity_title")
        val activityTitle: String,
        @SerialName("activity_type")
        val activityType: Int,
        val report: Report,
        @SerialName("word_tag")
        val wordTag: String
    ) {
        @Serializable
        data class Report(
            @SerialName("click_event_id")
            val clickEventId: String,
            val extends: Extends,
            @SerialName("show_event_id")
            val showEventId: String
        ) {
            @Serializable
            data class Extends(
                @SerialName("season_type")
                val seasonType: Int,
                val link: String,
                @SerialName("season_id")
                val seasonId: Int,
                @SerialName("adsense_id")
                val adsenseId: Int,
                @SerialName("ep_id")
                val epId: Int,
                @SerialName("pre_wtgt_id")
                val preWtgtId: String,
            )
        }
    }

    @Serializable
    data class Actor(
        val info: String,
        val title: String
    )

    @Serializable
    data class AllButtons(
        @SerialName("watch_formal")
        val watchFormal: String
    )

    @Serializable
    data class UpInfo(
        val avatar: String,
        val follower: Int,
        @SerialName("is_follow")
        private val _isFollow: Int,
        val isFollow: Boolean = _isFollow == 1,
        val mid: Long,
        val uname: String,
        @SerialName("verify_type")
        val verifyType: Int,
        @SerialName("verify_type2")
        val verifyType2: Int,
        @SerialName("vip_label")
        val vipLabel: VipLabel
    ) {
        /**
         * 大会员标签
         *
         * 暂时还没遇到十年/百年大会员的标签
         *
         * @param labelTheme 无会员为“”，大会员为“vip”，年度大会员为“annual_vip”
         * @param path
         * @param text 无会员为“”，大会员为“大会员”，年度大会员为“年度大会员”
         */
        @Serializable
        data class VipLabel(
            @SerialName("label_theme")
            val labelTheme: String,
            val path: String,
            val text: String
        )
    }

    @Serializable
    data class Area(
        val id: Int,
        val name: String
    )

    @Serializable
    data class ChannelEntrance(
        @SerialName("bubble_text")
        val bubbleText: String,
        val link: String,
        val name: String,
        @SerialName("tag_report")
        val tagReport: TagReport
    ) {
        @Serializable
        data class TagReport(
            @SerialName("tag_type")
            val tagType: String,
            @SerialName("tag_type_name")
            val tagTypeName: String,
            @SerialName("version_style")
            val versionStyle: String
        )
    }

    @Serializable
    data class EarphoneConf(
        @SerialName("sp_phones")
        val spPhones: JsonArray
    )

    @Serializable
    data class IconFont(
        val name: String,
        val text: String
    )

    /**
     * 板块，从上到下排列，包含 Tab，选集，PV等
     *
     * @param data 板块内容
     * @param id 板块id
     * @param moduleStyle
     * @param more
     * @param report
     * @param style 板块样式 season/positive/section
     * @param title 板块标题
     */
    @Serializable
    data class Module(
        val data: ModuleData,
        val id: Int,
        @SerialName("module_style")
        val moduleStyle: ModuleStyle,
        val more: String = "",
        val report: Report? = null,
        val style: String,
        val title: String
    ) {
        /**
         * 板块内容，当内容为“seasons”时仅包含seasons，当内容为“episodes”时仅包含episodes，当内容为“section”时包含除seasons以外的所有内容
         */
        @Serializable
        data class ModuleData(
            val attr: Int = 0,
            @SerialName("episode_id")
            val episodeId: Int? = null,
            @SerialName("episode_ids")
            val episodeIds: JsonArray? = null,
            val seasons: List<OtherSeason> = emptyList(),
            val episodes: List<Episode> = emptyList(),
            val id: Int? = null,
            val more: String? = null,
            @SerialName("split_text")
            val splitText: String? = null,
            val title: String? = null,
            val type: Int? = null,
            val type2: Int? = null
        )

        @Serializable
        data class ModuleStyle(
            val hidden: Int = 0,
            val line: Int
        )

        @Serializable
        data class Report(
            @SerialName("season_id")
            val seasonId: String,
            @SerialName("season_type")
            val seasonType: String,
            @SerialName("sec_title")
            val secTitle: String,
            @SerialName("section_id")
            val sectionId: String,
            @SerialName("section_type")
            val sectionType: String
        )
    }

    @Serializable
    data class NewKeepActivityMaterial(
        val activityId: Int
    )

    @Serializable
    data class Payment(
        val dialog: JsonElement,
        @SerialName("pay_tip")
        val payTip: JsonElement? = null,
        @SerialName("pay_type")
        val payType: PayType,
        val price: String,
        @SerialName("report_type")
        val reportType: Int,
        @SerialName("tv_price")
        val tvPrice: String,
        @SerialName("vip_discount_price")
        val vipDiscountPrice: String,
        @SerialName("vip_promotion")
        val vipPromotion: String
    ) {
        @Serializable
        data class PayType(
            @SerialName("allow_ticket")
            val allowTicket: Int
        )
    }

    @Serializable
    data class PlayStrategy(
        @SerialName("auto_play_toast")
        val autoPlayToast: String,
        @SerialName("recommend_show_strategy")
        val recommendShowStrategy: Int,
        val strategies: List<String> = emptyList()
    )

    @Serializable
    data class PlayerIcon(
        val ctime: Int,
        @SerialName("drag_data")
        val dragData: JsonElement? = null,
        val hash1: String,
        val hash2: String,
        @SerialName("no_drag_data")
        val noDragData: JsonElement? = null,
        val url1: String,
        val url2: String
    )

    @Serializable
    data class Reserve(
        val episodes: JsonElement,
        val tip: String
    )

    @Serializable
    data class Series(
        @SerialName("display_type")
        val displayType: Int,
        @SerialName("series_id")
        val seriesId: Int,
        @SerialName("series_title")
        val seriesTitle: String
    )

    @Serializable
    data class Staff(
        val info: String,
        val title: String
    )

    @Serializable
    data class Stat(
        val coin: Int? = null,
        val danmaku: Int? = null,
        val favorite: Int,
        val favorites: Int,
        val followers: String,
        val likes: Int,
        val play: String,
        val reply: Int,
        val share: Int,
        val views: Long,
        val vt: Int
    )

    /**
     * 剧集类别，例如“游戏改”、“战斗”,"奇幻","热血"
     */
    @Serializable
    data class Style(
        val id: Int,
        val name: String,
        val url: String
    )

    /**
     * 用户信息
     *
     * @param follow 是否追剧
     * @param followBubble
     * @param followStatus
     * @param pay
     * @param pay_for
     * @param progress 上次播放进度，仅登录时存在
     * @param sponsor
     * @param vipInfo 用户会员信息，仅登录时存在
     */
    @Serializable
    data class UserStatus(
        val follow: Int,
        @SerialName("follow_bubble")
        val followBubble: Int,
        @SerialName("follow_status")
        val followStatus: Int,
        val pay: Int,
        @SerialName("pay_for")
        val payFor: Int,
        val progress: Progress? = null,
        val review: Review? = null,
        val sponsor: Int,
        val vip: Int,
        @SerialName("vip_frozen")
        val vipFrozen: Int
    ) {
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

        @Serializable
        data class Review(
            @SerialName("article_url")
            val articleUrl: String,
            @SerialName("is_open")
            val isOpen: Int,
            val score: Int
        )
    }

    @Serializable
    data class UserThumbup(
        @SerialName("url_image_ani")
        val urlImageAni: String,
        @SerialName("url_image_ani_cut")
        val urlImageAniCut: String,
        @SerialName("url_image_bright")
        val urlImageBright: String,
        @SerialName("url_image_dim")
        val urlImageDim: String,
    )
}

