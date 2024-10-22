package dev.aaa1115910.biliapi.http.entity.pgc

import dev.aaa1115910.biliapi.http.entity.web.Hover
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement

/**
 * PGC 首页 ssr 数据
 */
@Serializable
data class PgcWebInitialStateData(
    val modules: Modules,
) {
    /**
     * @param banner 轮播图
     * @param index 索引
     * @param ext 时间表
     */
    @Suppress("KDocUnresolvedReference")
    @Serializable
    data class Modules(
        val banner: Banner,
        //val index: Index,
        //val ext:Ext,
    ) {
        @Serializable
        data class Banner(
            val title: String,
            val spmid: String,
            val size: Int,
            val style: String,
            val headers: JsonArray,
            val items: List<BannerItem>,
            val wids: JsonArray,
            @SerialName("module_id")
            val moduleId: Int
        ) {
            @Serializable
            data class BannerItem(
                val rating: String? = null,
                val title: String,
                val cover: String,
                val link: String,
                val evaluate: String? = null,
                val report: JsonElement? = null,
                val hover: Hover? = null,
                val stat: Stat? = null,
                val values: JsonArray? = null,
                @SerialName("season_id")
                val seasonId: Int? = null,
                @SerialName("season_type")
                val seasonType: Int? = null,
                @SerialName("rating_count")
                val ratingCount: Int? = null,
                @SerialName("episode_id")
                val episodeId: Int? = null,
                @SerialName("big_cover")
                val bigCover: String? = null,
                @SerialName("play_btn")
                val playBtn: Int? = null,
                @SerialName("play_title")
                val playTitle: String? = null,
                @SerialName("rank_id")
                val rankId: Int,
                @SerialName("user_status")
                val userStatus: UserStatus? = null,
                @SerialName("date_ts")
                val dateTs: Int? = null,
                @SerialName("day_of_week")
                val dayOfWeek: Int? = null,
                @SerialName("is_today")
                val isToday: Int? = null,
                @SerialName("is_latest")
                val isLatest: Int? = null,
                val id: String,
                @SerialName("showReportData")
                val showReportData: ShowReportData,
                // 当前获取到的 json 中未包含 webpcover 和 webpbigcover
                //@SerialName("webpcover")
                //val webpCover: String,
                //@SerialName("webpbigcover")
                //val webpBigCover: String
            ) {

                @Serializable
                data class Stat(
                    val view: Long
                )

                @Serializable
                data class UserStatus(
                    val follow: Int
                )

                @Serializable
                data class ShowReportData(
                    @SerialName("module_type")
                    val moduleType: String,
                    @SerialName("module_id")
                    val moduleId: Int,
                    @SerialName("ep_id")
                    val epId: Int? = null,
                    @SerialName("season_id")
                    val seasonId: Int? = null
                )
            }
        }
    }
}