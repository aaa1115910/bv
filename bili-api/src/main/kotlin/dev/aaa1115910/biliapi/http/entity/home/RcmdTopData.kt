package dev.aaa1115910.biliapi.http.entity.home


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class RcmdTopData(
    @SerialName("business_card")
    val businessCard: JsonElement? = null,
    @SerialName("floor_info")
    val floorInfo: JsonElement? = null,
    val item: List<RcmdItem>,
    val mid: Long,
    @SerialName("preload_expose_pct")
    val preloadExposePct: Double,
    @SerialName("preload_floor_expose_pct")
    val preloadFloorExposePct: Double,
    @SerialName("side_bar_column")
    val sideBarColumn: List<SideBarColumn>? = emptyList(),
    @SerialName("user_feature")
    val userFeature: JsonElement? = null
) {
    @Serializable
    data class RcmdItem(
        @SerialName("av_feature")
        val avFeature: JsonElement? = null,
        @SerialName("business_info")
        val businessInfo: BusinessInfo?,
        val bvid: String,
        val cid: Long,
        val duration: Int,
        @SerialName("enable_vt")
        val enableVt: Int,
        val goto: String,
        val id: Long,
        @SerialName("is_followed")
        val isFollowed: Int,
        @SerialName("is_stock")
        val isStock: Int,
        @SerialName("ogv_info")
        val ogvInfo: JsonElement? = null,
        val owner: Owner? = null,
        val pic: String,
        val pos: Int,
        val pubdate: Int,
        @SerialName("rcmd_reason")
        val rcmdReason: RcmdReason? = null,
        @SerialName("room_info")
        val roomInfo: JsonElement? = null,
        @SerialName("show_info")
        val showInfo: Int,
        val stat: Stat? = null,
        val title: String,
        @SerialName("track_id")
        val trackId: String,
        val uri: String
    ) {
        @Serializable
        data class BusinessInfo(
            @SerialName("activity_type")
            val activityType: Int,
            @SerialName("ad_cb")
            val adCb: String,
            @SerialName("ad_desc")
            val adDesc: String,
            @SerialName("adver_name")
            val adverName: String,
            val agency: String,
            val area: Int,
            @SerialName("asg_id")
            val asgId: Int,
            @SerialName("business_mark")
            val businessMark: BusinessMark,
            @SerialName("card_type")
            val cardType: Int,
            @SerialName("cm_mark")
            val cmMark: Int,
            @SerialName("contract_id")
            val contractId: String,
            @SerialName("creative_id")
            val creativeId: Int,
            @SerialName("creative_type")
            val creativeType: Int,
            val epid: Int,
            val id: Int,
            @SerialName("inline")
            val `inline`: Inline,
            val intro: String,
            @SerialName("is_ad")
            val isAd: Boolean,
            @SerialName("is_ad_loc")
            val isAdLoc: Boolean,
            val label: String,
            val litpic: String,
            val mid: String,
            val name: String,
            @SerialName("null_frame")
            val nullFrame: Boolean,
            val operater: String,
            val pic: String,
            @SerialName("pic_main_color")
            val picMainColor: String,
            @SerialName("pos_num")
            val posNum: Int,
            @SerialName("request_id")
            val requestId: String,
            @SerialName("res_id")
            val resId: Int,
            @SerialName("server_type")
            val serverType: Int,
            @SerialName("src_id")
            val srcId: Int,
            val stime: Int,
            val style: Int,
            @SerialName("sub_title")
            val subTitle: String,
            val title: String,
            val url: String
        ) {
            @Serializable
            data class BusinessMark(
                @SerialName("bg_border_color")
                val bgBorderColor: String,
                @SerialName("bg_color")
                val bgColor: String,
                @SerialName("bg_color_night")
                val bgColorNight: String,
                @SerialName("border_color")
                val borderColor: String,
                @SerialName("border_color_night")
                val borderColorNight: String,
                @SerialName("img_height")
                val imgHeight: Int,
                @SerialName("img_url")
                val imgUrl: String,
                @SerialName("img_width")
                val imgWidth: Int,
                val text: String,
                @SerialName("text_color")
                val textColor: String,
                @SerialName("text_color_night")
                val textColorNight: String,
                val type: Int
            )

            @Serializable
            data class Inline(
                @SerialName("inline_barrage_switch")
                val inlineBarrageSwitch: Int,
                @SerialName("inline_type")
                val inlineType: Int,
                @SerialName("inline_url")
                val inlineUrl: String,
                @SerialName("inline_use_same")
                val inlineUseSame: Int
            )
        }

        @Serializable
        data class Owner(
            val face: String,
            val mid: Long,
            val name: String
        )

        @Serializable
        data class RcmdReason(
            val content: String? = null,
            @SerialName("reason_type")
            val reasonType: Int
        )

        @Serializable
        data class Stat(
            val danmaku: Int,
            val like: Int,
            val view: Int,
            val vt: Int
        )
    }

    @Serializable
    data class SideBarColumn(
        @SerialName("av_feature")
        val avFeature: JsonElement? = null,
        @SerialName("card_type")
        val cardType: String,
        @SerialName("card_type_en")
        val cardTypeEn: String,
        val comic: JsonElement? = null,
        val cover: String,
        val duration: Int,
        @SerialName("enable_vt")
        val enableVt: Int,
        val goto: String,
        @SerialName("horizontal_cover_16_10")
        val horizontalCover1610: String? = null,
        @SerialName("horizontal_cover_16_9")
        val horizontalCover169: String? = null,
        val id: Int,
        @SerialName("is_finish")
        val isFinish: Int,
        @SerialName("is_play")
        val isPlay: Int,
        @SerialName("is_rec")
        val isRec: Int,
        @SerialName("is_started")
        val isStarted: Int,
        @SerialName("new_ep")
        val newEp: NewEp,
        val pos: Int,
        val producer: List<Producer>,
        @SerialName("room_info")
        val roomInfo: JsonElement? = null,
        val source: String,
        val stats: Stats,
        val styles: List<String> = emptyList(),
        @SerialName("sub_title")
        val subTitle: String,
        val title: String,
        @SerialName("track_id")
        val trackId: String,
        val url: String
    ) {
        @Serializable
        data class NewEp(
            val cover: String,
            @SerialName("day_of_week")
            val dayOfWeek: Int,
            val duration: Int,
            val id: Int,
            @SerialName("index_show")
            val indexShow: String? = null,
            @SerialName("long_title")
            val longTitle: String? = null,
            @SerialName("pub_time")
            val pubTime: String?,
            val title: String
        )

        @Serializable
        data class Producer(
            @SerialName("is_contribute")
            val isContribute: Int,
            val mid: Long,
            val name: String,
            val type: Int
        )

        @Serializable
        data class Stats(
            val coin: Int,
            val danmaku: Int,
            val favorite: Int,
            val follow: Int,
            val likes: Int,
            val reply: Int,
            @SerialName("series_follow")
            val seriesFollow: Int? = null,
            @SerialName("series_view")
            val seriesView: Int? = null,
            val view: Int
        )
    }
}