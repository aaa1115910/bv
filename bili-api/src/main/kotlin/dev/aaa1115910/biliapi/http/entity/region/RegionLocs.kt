package dev.aaa1115910.biliapi.http.entity.region


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class RegionLocs(
    @SerialName("ads_control")
    val adsControl: AdsControl,
    val code: Int,
    val count: Int,
    val data: Map<String, List<LocData>>,
    val live: JsonObject? = null,
    val message: String
) {
    @Serializable
    data class AdsControl(
        @SerialName("has_danmu")
        val hasDanmu: Int,
        @SerialName("has_live_booking_ad")
        val hasLiveBookingAd: Boolean,
        @SerialName("under_player_scroller_seconds")
        val underPlayerScrollerSeconds: Int
    )

    @Serializable
    data class LocData(
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
        val businessMark: JsonObject? = null,
        @SerialName("card_type")
        val cardType: Int,
        @SerialName("click_urls")
        val clickUrls: JsonObject? = null,
        @SerialName("cm_mark")
        val cmMark: Int,
        @SerialName("contract_id")
        val contractId: String,
        @SerialName("creative_type")
        val creativeType: Int,
        @SerialName("epid")
        val epId: Int,
        @SerialName("feedback_panel")
        val feedbackPanel: JsonObject? = null,
        val id: Int,
        val inline: Inline,
        val intro: String,
        @SerialName("is_ad_loc")
        val isAdLoc: Boolean,
        @SerialName("jump_target")
        val jumpTarget: Int,
        val label: String,
        @SerialName("litpic")
        val litPic: String,
        val mid: String,
        val name: String,
        @SerialName("null_frame")
        val nullFrame: Boolean,
        @SerialName("operater")
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
        val room: JsonObject? = null,
        @SerialName("sales_type")
        val salesType: Int,
        val season: JsonObject? = null,
        @SerialName("server_type")
        val serverType: Int,
        @SerialName("show_urls")
        val showUrls: JsonObject? = null,
        @SerialName("src_id")
        val srcId: Int,
        @SerialName("stime")
        val sTime: Int,
        val style: Int,
        @SerialName("sub_title")
        val subTitle: String,
        val title: String,
        @SerialName("track_id")
        val trackId: String,
        val url: String
    ) {
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
}