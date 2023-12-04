package dev.aaa1115910.biliapi.http.entity.index

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IndexResultData(
    @SerialName("has_next")
    val hasNext: Int,
    val list: List<IndexResultItem>,
    val num: Int,
    val size: Int,
    val total: Int
) {
    @Serializable
    data class IndexResultItem(
        val badge: String,
        @SerialName("badge_info")
        val badgeInfo: BadgeInfo,
        @SerialName("badge_type")
        val badgeType: Int,
        val cover: String,
        @SerialName("first_ep")
        val firstEp: FirstEp,
        @SerialName("index_show")
        val indexShow: String,
        @SerialName("is_finish")
        val isFinish: Int,
        val link: String,
        @SerialName("media_id")
        val mediaId: Int,
        val order: String,
        @SerialName("order_type")
        val orderType: String,
        val score: String,
        @SerialName("season_id")
        val seasonId: Int,
        @SerialName("season_status")
        val seasonStatus: Int,
        @SerialName("season_type")
        val seasonType: Int,
        val subTitle: String,
        val title: String,
        @SerialName("title_icon")
        val titleIcon: String
    ) {
        @Serializable
        data class BadgeInfo(
            @SerialName("bg_color")
            val bgColor: String,
            @SerialName("bg_color_night")
            val bgColorNight: String,
            val text: String
        )

        @Serializable
        data class FirstEp(
            val cover: String,
            @SerialName("ep_id")
            val epId: Int
        )
    }
}