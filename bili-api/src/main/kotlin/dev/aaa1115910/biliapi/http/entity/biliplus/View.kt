package dev.aaa1115910.biliapi.http.entity.biliplus


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class View(
    val aid: Long,
    val author: String,
    val bangumi: Bangumi? = null,
    val coins: Int,
    val created: Int,
    @SerialName("created_at")
    val createdAt: String,
    val description: String,
    val favorites: Int,
    val id: Int,
    @SerialName("lastupdate")
    val lastUpdate: String,
    @SerialName("lastupdatets")
    val lastUpdatets: Int,
    val list: List<ListItem>,
    val mid: Long,
    val pic: String,
    val play: Int,
    val review: Int,
    val tag: String,
    val tid: Int,
    val title: String,
    val typename: String,
    @SerialName("v2_app_api")
    val v2AppApi: V2AppApi,
    val ver: Int,
    @SerialName("video_review")
    val videoReview: Int
) {
    @Serializable
    data class Bangumi(
        val cover: String,
        @SerialName("is_finish")
        val isFinish: String,
        @SerialName("is_jump")
        val isJump: Int,
        @SerialName("newest_ep_id")
        val newestEpId: String,
        @SerialName("newest_ep_index")
        val newestEpIndex: String,
        @SerialName("ogv_play_url")
        val ogvPlayUrl: String,
        @SerialName("season_id")
        val seasonId: String,
        val title: String,
        @SerialName("total_count")
        val totalCount: String,
        val weekday: String
    )

    @Serializable
    data class ListItem(
        val cid: Long,
        val page: Int,
        val part: String,
        val type: String,
        val vid: String
    )

    @Serializable
    data class V2AppApi(
        val aid: Long,
        val bvid: String,
        val cid: Long,
        @SerialName("cm_config")
        val cmConfig: CmConfig,
        val config: Config,
        val copyright: Int,
        val ctime: Int,
        @SerialName("DagwUser")
        val dagwUser: List<JsonElement>,
        @SerialName("DataCenterInfo")
        val dataCenterInfo: String,
        val desc: String,
        val dimension: Dimension,
        @SerialName("dm_seg")
        val dmSeg: Int,
        val duration: Int,
        val `dynamic`: String,
        @SerialName("InteractLabel")
        val interactLabel: String,
        @SerialName("like_custom")
        val likeCustom: LikeCustom,
        @SerialName("LiveOrderText")
        val liveOrderText: String,
        val owner: Owner,
        @SerialName("owner_ext")
        val ownerExt: OwnerExt,
        val pages: List<Page>,
        val paster: Paster? = null,
        val pic: String,
        @SerialName("play_param")
        val playParam: Int,
        @SerialName("PlayToast")
        val playToast: JsonElement? = null,
        @SerialName("premiere_resource")
        val premiereResource: JsonElement? = null,
        @SerialName("pub_location")
        val pubLocation: String? = null,
        val pubdate: Int,
        @SerialName("redirect_url")
        val redirectUrl: String? = null,
        @SerialName("RejectPage")
        val rejectPage: JsonElement? = null,
        val rights: Rights,
        val season: Season? = null,
        @SerialName("share_subtitle")
        val shareSubtitle: String? = null,
        @SerialName("short_link")
        val shortLink: String,
        @SerialName("short_link_v2")
        val shortLinkV2: String,
        val stat: Stat,
        val state: Int,
        @SerialName("t_icon")
        val tIcon: TIcon,
        @SerialName("TabModule")
        val tabModule: JsonElement? = null,
        val tag: List<Tag>,
        val tid: Int,
        val title: String,
        val tname: String,
        @SerialName("up_from_v2")
        val upFromV2: Int? = null,
        val videos: Int,
        @SerialName("vt_display")
        val vtDisplay: String
    ) {
        @Serializable
        data class CmConfig(
            @SerialName("ads_control")
            val adsControl: AdsControl
        ) {
            @Serializable
            data class AdsControl(
                @SerialName("has_danmu")
                val hasDanmu: Int,
                @SerialName("under_player_scroller_seconds")
                val underPlayerScrollerSeconds: Int
            )
        }

        @Serializable
        data class Config(
            @SerialName("abtest_small_window")
            val abtestSmallWindow: String,
            @SerialName("feed_has_next")
            val feedHasNext: Boolean,
            @SerialName("feed_style")
            val feedStyle: String,
            @SerialName("has_guide")
            val hasGuide: Boolean,
            @SerialName("is_absolute_time")
            val isAbsoluteTime: Boolean,
            @SerialName("local_play")
            val localPlay: Int,
            @SerialName("rec_three_point_style")
            val recThreePointStyle: Int,
            @SerialName("relates_title")
            val relatesTitle: String,
            @SerialName("share_style")
            val shareStyle: Int,
            @SerialName("valid_show_m")
            val validShowM: Int,
            @SerialName("valid_show_n")
            val validShowN: Int
        )

        @Serializable
        data class Dimension(
            val height: Int,
            val rotate: Int,
            val width: Int
        )

        @Serializable
        data class LikeCustom(
            @SerialName("full_to_half_progress")
            val fullToHalfProgress: Int,
            @SerialName("like_switch")
            val likeSwitch: Boolean,
            @SerialName("non_full_progress")
            val nonFullProgress: Int,
            @SerialName("update_count")
            val updateCount: Int
        )

        @Serializable
        data class Owner(
            val face: String,
            val mid: Int,
            val name: String
        )

        @Serializable
        data class OwnerExt(
            @SerialName("arc_count")
            val arcCount: String,
            val assists: JsonElement? = null,
            val fans: Int,
            @SerialName("nft_face_icon")
            val nftFaceIcon: JsonElement? = null,
            @SerialName("official_verify")
            val officialVerify: OfficialVerify,
            val vip: Vip
        ) {
            @Serializable
            data class OfficialVerify(
                val desc: String,
                val type: Int
            )

            @Serializable
            data class Vip(
                val accessStatus: Int,
                val dueRemark: String,
                val label: Label,
                val themeType: Int,
                val vipDueDate: Long,
                val vipStatus: Int,
                val vipStatusWarn: String,
                val vipType: Int
            ) {
                @Serializable
                data class Label(
                    @SerialName("bg_color")
                    val bgColor: String,
                    @SerialName("bg_style")
                    val bgStyle: Int,
                    @SerialName("border_color")
                    val borderColor: String,
                    @SerialName("img_label_uri_hans")
                    val imgLabelUriHans: String,
                    @SerialName("img_label_uri_hans_static")
                    val imgLabelUriHansStatic: String,
                    @SerialName("img_label_uri_hant")
                    val imgLabelUriHant: String,
                    @SerialName("img_label_uri_hant_static")
                    val imgLabelUriHantStatic: String,
                    @SerialName("label_theme")
                    val labelTheme: String,
                    val path: String,
                    val text: String,
                    @SerialName("text_color")
                    val textColor: String,
                    @SerialName("use_img_label")
                    val useImgLabel: Boolean
                )
            }
        }

        @Serializable
        data class Page(
            val cid: Long,
            val dimension: Dimension? = null,
            val dmlink: String? = null,
            @SerialName("download_subtitle")
            val downloadSubtitle: String? = null,
            @SerialName("download_title")
            val downloadTitle: String? = null,
            val duration: Int? = null,
            val from: String,
            val page: Int,
            val part: String,
            val vid: String,
            val weblink: String? = null
        ) {
            @Serializable
            data class Dimension(
                val height: Int,
                val rotate: Int,
                val width: Int
            )
        }

        @Serializable
        data class Paster(
            val aid: Long,
            @SerialName("allow_jump")
            val allowJump: Int,
            val cid: Long,
            val duration: Int,
            val type: Int,
            val url: String
        )

        @Serializable
        data class Rights(
            @SerialName("arc_pay")
            val arcPay: Int,
            val autoplay: Int,
            val bp: Int,
            val download: Int,
            val elec: Int,
            val hd5: Int,
            @SerialName("is_cooperation")
            val isCooperation: Int,
            val movie: Int,
            @SerialName("no_background")
            val noBackground: Int,
            @SerialName("no_reprint")
            val noReprint: Int,
            val pay: Int,
            @SerialName("pay_free_watch")
            val payFreeWatch: Int,
            @SerialName("ugc_pay")
            val ugcPay: Int,
            @SerialName("ugc_pay_preview")
            val ugcPayPreview: Int
        )

        @Serializable
        data class Season(
            val cover: String,
            @SerialName("is_finish")
            val isFinish: String,
            @SerialName("is_jump")
            val isJump: Int,
            @SerialName("newest_ep_id")
            val newestEpId: String,
            @SerialName("newest_ep_index")
            val newestEpIndex: String,
            @SerialName("ogv_play_url")
            val ogvPlayUrl: String,
            @SerialName("season_id")
            val seasonId: String,
            val title: String,
            @SerialName("total_count")
            val totalCount: String,
            val weekday: String
        )

        @Serializable
        data class Stat(
            val aid: Long,
            val coin: Int,
            val danmaku: Int,
            val dislike: Int,
            val favorite: Int,
            @SerialName("his_rank")
            val hisRank: Int,
            val like: Int,
            @SerialName("now_rank")
            val nowRank: Int,
            val reply: Int,
            val share: Int,
            val view: Int,
            val vt: Int,
            val vv: Int
        )

        @Serializable
        data class TIcon(
            val act: Act,
            val new: New
        ) {
            @Serializable
            data class Act(
                val icon: String
            )

            @Serializable
            data class New(
                val icon: String
            )
        }

        @Serializable
        data class Tag(
            val attribute: Int,
            val cover: String,
            val hated: Int,
            val hates: Int,
            @SerialName("is_activity")
            val isActivity: Int,
            val liked: Int,
            val likes: Int,
            @SerialName("tag_id")
            val tagId: Int,
            @SerialName("tag_name")
            val tagName: String,
            @SerialName("tag_type")
            val tagType: String,
            val uri: String
        )
    }
}