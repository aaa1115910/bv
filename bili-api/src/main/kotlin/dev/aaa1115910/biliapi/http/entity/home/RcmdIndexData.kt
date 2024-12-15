package dev.aaa1115910.biliapi.http.entity.home


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class RcmdIndexData(
    val config: Config,
    @SerialName("interest_choose")
    val interestChoose: JsonElement? = null,
    val items: List<RcmdItem>
) {
    @Serializable
    data class Config(
        @SerialName("auto_refresh_by_behavior")
        val autoRefreshByBehavior: Int? = null,
        @SerialName("auto_refresh_time")
        val autoRefreshTime: Int,
        @SerialName("auto_refresh_time_by_active")
        val autoRefreshTimeByActive: Int,
        @SerialName("auto_refresh_time_by_appear")
        val autoRefreshTimeByAppear: Int,
        @SerialName("auto_refresh_time_by_behavior")
        val autoRefreshTimeByBehavior: Int? = null,
        @SerialName("autoplay_card")
        val autoplayCard: Int,
        @SerialName("card_density_exp")
        val cardDensityExp: Int,
        val column: Int,
        @SerialName("enable_rcmd_guide")
        val enableRcmdGuide: Boolean,
        @SerialName("feed_clean_abtest")
        val feedCleanAbtest: Int,
        @SerialName("history_cache_size")
        val historyCacheSize: Int? = null,
        @SerialName("home_transfer_test")
        val homeTransferTest: Int,
        @SerialName("inline_sound")
        val inlineSound: Int,
        @SerialName("is_back_to_homepage")
        val isBackToHomepage: Boolean,
        @SerialName("show_inline_danmaku")
        val showInlineDanmaku: Int,
        @SerialName("single_autoplay_flag")
        val singleAutoplayFlag: Int? = null,
        @SerialName("small_cover_wh_ratio")
        val smallCoverWhRatio: Double? = null,
        @SerialName("space_enlarge_exp")
        val spaceEnlargeExp: Int? = null,
        @SerialName("story_mode_v2_guide_exp")
        val storyModeV2GuideExp: Int,
        val toast: JsonElement,
        @SerialName("trigger_loadmore_left_line_num")
        val triggerLoadmoreLeftLineNum: Int? = null,
        @SerialName("video_mode")
        val videoMode: Int? = null,
        @SerialName("visible_area")
        val visibleArea: Int
    )

    @Serializable
    data class RcmdItem(
        //@SerialName("ad_info")
        //val adInfo: AdInfo,
        val args: Args,
        @SerialName("can_play")
        val canPlay: Int? = null,
        @SerialName("card_goto")
        val cardGoto: String,
        @SerialName("card_type")
        val cardType: String,
        val cover: String? = null,
        @SerialName("cover_left_1_content_description")
        val coverLeft1ContentDescription: String? = null,
        @SerialName("cover_left_2_content_description")
        val coverLeft2ContentDescription: String? = null,
        @SerialName("cover_left_icon_1")
        val coverLeftIcon1: Int? = null,
        @SerialName("cover_left_icon_2")
        val coverLeftIcon2: Int? = null,
        @SerialName("cover_left_text_1")
        val coverLeftText1: String? = null,
        @SerialName("cover_left_text_2")
        val coverLeftText2: String? = null,
        @SerialName("cover_right_content_description")
        val coverRightContentDescription: String? = null,
        @SerialName("cover_right_text")
        val coverRightText: String? = null,
        val desc: String? = null,
        @SerialName("desc_button")
        val descButton: DescButton? = null,
        @SerialName("ff_cover")
        val ffCover: String? = null,
        val goto: String? = null,
        @SerialName("goto_icon")
        val gotoIcon: GotoIcon? = null,
        val idx: Int,
        @SerialName("official_icon")
        val officialIcon: Int? = null,
        @SerialName("param")
        val `param`: String? = null,
        @SerialName("player_args")
        val playerArgs: PlayerArgs? = null,
        @SerialName("rcmd_reason")
        val rcmdReason: String? = null,
        @SerialName("rcmd_reason_style")
        val rcmdReasonStyle: RcmdReasonStyle? = null,
        @SerialName("report_flow_data")
        val reportFlowData: String? = null,
        @SerialName("talk_back")
        val talkBack: String? = null,
        @SerialName("three_point")
        val threePoint: ThreePoint? = null,
        @SerialName("three_point_v2")
        val threePointV2: List<ThreePointV2>,
        val title: String? = null,
        @SerialName("track_id")
        val trackId: String? = null,
        val uri: String? = null
    ) {
        @Serializable
        data class Args(
            val aid: Long? = null,
            val rid: Int? = null,
            val rname: String? = null,
            val tid: Int? = null,
            val tname: String? = null,
            @SerialName("up_id")
            val upId: Long? = null,
            @SerialName("up_name")
            val upName: String? = null
        )

        @Serializable
        data class DescButton(
            val event: String,
            val text: String,
            val type: Int,
            val uri: String? = null
        )

        @Serializable
        data class GotoIcon(
            @SerialName("icon_height")
            val iconHeight: Int,
            @SerialName("icon_night_url")
            val iconNightUrl: String,
            @SerialName("icon_url")
            val iconUrl: String,
            @SerialName("icon_width")
            val iconWidth: Int
        )

        @Serializable
        data class PlayerArgs(
            val aid: Long,
            val cid: Long,
            val duration: Int,
            val type: String
        )

        @Serializable
        data class RcmdReasonStyle(
            @SerialName("bg_color")
            val bgColor: String,
            @SerialName("bg_color_night")
            val bgColorNight: String,
            @SerialName("bg_style")
            val bgStyle: Int,
            @SerialName("border_color")
            val borderColor: String,
            @SerialName("border_color_night")
            val borderColorNight: String,
            val text: String,
            @SerialName("text_color")
            val textColor: String,
            @SerialName("text_color_night")
            val textColorNight: String
        )

        @Serializable
        data class ThreePoint(
            @SerialName("dislike_reasons")
            val dislikeReasons: List<DislikeReason>,
            val feedbacks: List<Feedback>? = null,
            @SerialName("watch_later")
            val watchLater: Int? = null
        ) {
            @Serializable
            data class DislikeReason(
                val id: Int,
                val name: String,
                val toast: String
            )

            @Serializable
            data class Feedback(
                val id: Int,
                val name: String,
                val toast: String
            )
        }

        @Serializable
        data class ThreePointV2(
            val icon: String? = null,
            val reasons: List<Reason> = emptyList(),
            val subtitle: String? = null,
            val title: String? = null,
            val type: String
        ) {
            @Serializable
            data class Reason(
                val id: Int,
                val name: String,
                val toast: String
            )
        }
    }
}