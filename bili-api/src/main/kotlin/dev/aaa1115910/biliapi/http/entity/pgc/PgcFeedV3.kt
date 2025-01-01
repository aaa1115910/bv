package dev.aaa1115910.biliapi.http.entity.pgc

import dev.aaa1115910.biliapi.http.entity.web.Hover
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray

@Serializable
data class PgcFeedV3Data(
    @Suppress("SpellCheckingInspection")
    var coursor: Int,
    @SerialName("has_next")
    val hasNext: Boolean,
    val items: List<FeedItem>
) {
    @Serializable
    data class FeedItem(
        @SerialName("rank_id")
        val rankId: Int,
        @SerialName("sub_items")
        val subItems: List<FeedSubItem>,
        val text: JsonArray? = null
    ) {
        @Serializable
        data class FeedSubItem(
            @SerialName("card_style")
            val cardStyle: String,
            val cover: String,
            @SerialName("episode_id")
            val episodeId: Int? = null,
            val evaluate: String? = null,
            val hover: Hover? = null,
            val inline: Inline? = null,
            val link: String? = null,
            @SerialName("rank_id")
            val rankId: Int,
            val rating: String? = null,
            @SerialName("rating_count")
            val ratingCount: Int? = null,
            val report: Report,
            @SerialName("season_id")
            val seasonId: Int? = null,
            @SerialName("season_type")
            val seasonType: Int? = null,
            val stat: Stat? = null,
            @SerialName("sub_items")
            val subItems: List<FeedSubItem>? = null,
            @SerialName("sub_title")
            val subTitle: String,
            val text: JsonArray? = null,
            val title: String,
            val userStatus: UserStatus? = null
        ) {
            @Serializable
            data class Inline(
                @SerialName("end_time")
                val endTime: Int? = null,
                @SerialName("ep_id")
                val epId: Int,
                @SerialName("first_ep")
                val firstEp: Int,
                @SerialName("material_no")
                val materialNo: String? = null,
                val scene: Int,
                @SerialName("start_time")
                val startTime: Int? = null
            )

            @Serializable
            data class Report(
                @SerialName("first_ep")
                val firstEp: Int? = null,
                val scene: Int? = null
            )

            @Serializable
            data class Stat(
                val danmaku: Int,
                val duration: Int,
                val view: Long
            )

            @Serializable
            data class UserStatus(
                val follow: Int
            )
        }
    }
}