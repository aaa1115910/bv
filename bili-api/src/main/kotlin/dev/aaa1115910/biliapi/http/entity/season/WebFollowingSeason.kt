package dev.aaa1115910.biliapi.http.entity.season

import dev.aaa1115910.biliapi.http.entity.video.VideoStat
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement

/**
 * 追剧
 */
@Serializable
data class FollowingSeasonWebData(
    val list: List<WebFollowingSeason>,
    @SerialName("pn")
    val pageNumber: Int,
    @SerialName("ps")
    val pageSize: Int,
    val total: Int
)

@Serializable
data class FollowingSeasonAppData(
    @SerialName("follow_list")
    val followList: List<AppFollowingSeason> = emptyList(),
    @SerialName("has_next")
    private val _hasNext: Int,
    val hasNext: Boolean = _hasNext == 1,
    val series: JsonElement? = null,
    val total: Int,
    @SerialName("vip_tip")
    val vipTip: JsonArray? = null,
    val want: JsonArray? = null,
    val watched: JsonArray? = null
)

@Serializable
data class WebFollowingSeason(
    val badge: String,
    @SerialName("badge_ep")
    val badgeEp: String,
    @SerialName("badge_info")
    val badgeInfo: BadgeInfo,
    @SerialName("badge_infos")
    val badgeInfos: BadgeInfos? = null,
    @SerialName("badge_type")
    val badgeType: Int,
    @SerialName("both_follow")
    val bothFollow: Boolean,
    @SerialName("can_watch")
    val canWatch: Int,
    val cover: String,
    val evaluate: String,
    @SerialName("first_ep")
    val firstEp: Int,
    @SerialName("first_ep_info")
    val firstEpInfo: EpInfo,
    @SerialName("follow_status")
    val followStatus: Int,
    @SerialName("formal_ep_count")
    val formalEpCount: Int? = null,
    @SerialName("horizontal_cover_16_10")
    val horizontalCover1610: String? = null,
    @SerialName("horizontal_cover_16_9")
    val horizontalCover169: String? = null,
    @SerialName("is_finish")
    val isFinish: Int,
    @SerialName("is_new")
    val isNew: Int,
    @SerialName("is_play")
    val isPlay: Int,
    @SerialName("is_started")
    val isStarted: Int,
    @SerialName("media_attr")
    val mediaAttr: Int,
    @SerialName("media_id")
    val mediaId: Int,
    val mode: Int,
    @SerialName("new_ep")
    val newEp: EpInfo,
    val producers: List<Producer> = emptyList(),
    val progress: String,
    val publish: Publish,
    @SerialName("renewal_time")
    val renewalTime: String? = null,
    val rights: Rights,
    @SerialName("season_attr")
    val seasonAttr: Int,
    @SerialName("season_id")
    val seasonId: Int,
    @SerialName("season_status")
    val seasonStatus: Int,
    @SerialName("season_title")
    val seasonTitle: String,
    @SerialName("season_type")
    val seasonType: Int,
    @SerialName("season_type_name")
    val seasonTypeName: String,
    @SerialName("season_version")
    val seasonVersion: String? = null,
    val section: List<Section>,
    val series: Series? = null,
    @SerialName("short_url")
    val shortUrl: String,
    @SerialName("square_cover")
    val squareCover: String,
    val stat: VideoStat,
    val styles: List<String> = emptyList(),
    val subtitle: String,
    @SerialName("subtitle_14")
    val subtitle14: String? = null,
    val summary: String,
    val title: String,
    @SerialName("total_count")
    val totalCount: Int,
    val url: String,
    @SerialName("viewable_crowd_type")
    val viewableCrowdType: Int? = null
) {
    @Serializable
    data class BadgeInfo(
        @SerialName("bg_color")
        val bgColor: String,
        @SerialName("bg_color_night")
        val bgColorNight: String,
        val img: String? = null,
        @SerialName("multi_img")
        val multiImg: MultiImg,
        val text: String? = null,
    ) {
        @Serializable
        data class MultiImg(
            val color: String,
            @SerialName("medium_remind")
            val mediumRemind: String
        )
    }

    @Serializable
    data class BadgeInfos(
        @SerialName("vip_or_pay")
        val vipOrPay: BadgeInfo? = null
    )

    @Serializable
    data class EpInfo(
        val cover: String? = null,
        val duration: Int? = null,
        val id: Int? = null,
        @SerialName("index_show")
        val indexShow: String? = null,
        @SerialName("long_title")
        val longTitle: String? = null,
        @SerialName("pub_time")
        val pubTime: String? = null,
        val title: String? = null
    )

    @Serializable
    data class Producer(
        @SerialName("is_contribute")
        val isContribute: Int? = null,
        val mid: Long,
        val type: Int
    )

    @Serializable
    data class Publish(
        @SerialName("pub_time")
        val pubTime: String,
        @SerialName("pub_time_show")
        val pubTimeShow: String,
        @SerialName("release_date")
        val releaseDate: String,
        @SerialName("release_date_show")
        val releaseDateShow: String
    )

    @Serializable
    data class Rights(
        @SerialName("demand_end_time")
        val demandEndTime: JsonElement? = null,
        @SerialName("is_selection")
        val isSelection: Int,
        @SerialName("selection_style")
        val selectionStyle: Int
    )

    @Serializable
    data class Section(
        @SerialName("ban_area_show")
        val banAreaShow: Int? = null,
        val copyright: String,
        @SerialName("episode_ids")
        val episodeIds: List<Int>,
        @SerialName("limit_group")
        val limitGroup: Int,
        @SerialName("season_id")
        val seasonId: Int,
        @SerialName("section_id")
        val sectionId: Int,
        @SerialName("watch_platform")
        val watchPlatform: Int
    )

    @Serializable
    data class Series(
        @SerialName("new_season_id")
        val newSeasonId: Int? = null,
        @SerialName("season_count")
        val seasonCount: Int? = null,
        @SerialName("series_id")
        val seriesId: Int? = null,
        @SerialName("series_ord")
        val seriesOrd: Int? = null,
        val title: String? = null
    )
}

@Serializable
data class AppFollowingSeason(
    val areas: List<Area> = emptyList(),
    val badge: String,
    @SerialName("badge_info")
    val badgeInfo: BadgeInfo,
    @SerialName("badge_type")
    val badgeType: Int,
    @SerialName("can_watch")
    val canWatch: Int,
    val cover: String,
    val follow: Int,
    @SerialName("is_finish")
    private val _isFinish: Int,
    val isFinish: Boolean = _isFinish == 1,
    val movable: Int,
    val mtime: Int,
    @SerialName("new_ep")
    val newEp: NewEp,
    val progress: Progress? = null,
    @SerialName("season_id")
    val seasonId: Int,
    @SerialName("season_type")
    val seasonType: Int,
    @SerialName("season_type_name")
    val seasonTypeName: String,
    val series: Series,
    @SerialName("square_cover")
    val squareCover: String,
    val title: String,
    val url: String
) {
    @Serializable
    data class Area(
        val id: Int,
        val name: String
    )

    @Serializable
    data class BadgeInfo(
        @SerialName("bg_color")
        val bgColor: String,
        @SerialName("bg_color_night")
        val bgColorNight: String,
        val img: String? = null,
        val text: String
    )

    @Serializable
    data class NewEp(
        val cover: String,
        val duration: Int,
        val id: Int,
        @SerialName("index_show")
        val indexShow: String,
        @SerialName("is_new")
        private val _isNew: Int,
        val isNew: Boolean = _isNew == 1
    )

    @Serializable
    data class Progress(
        @SerialName("index_show")
        val indexShow: String,
        @SerialName("last_ep_id")
        val lastEpId: Int,
        @SerialName("last_time")
        val lastTime: Int,
    )

    @Serializable
    data class Series(
        val count: Int,
        val id: Int,
        val title: String
    )
}

/*
enum class FollowingSeasonType(val id: Int) {
    Bangumi(id = 1), FilmAndTelevision(id = 2)
}

enum class FollowingSeasonStatus(val id: Int) {
    All(id = 0), Want(id = 1), Watching(id = 2), Watched(id = 3)
}
*/
