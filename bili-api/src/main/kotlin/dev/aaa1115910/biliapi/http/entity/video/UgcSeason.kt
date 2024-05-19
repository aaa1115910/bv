package dev.aaa1115910.biliapi.http.entity.video

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * 合集信息
 *
 * @param id 合集 id
 * @param title 合集标题
 * @param cover 合集封面
 * @param mid up主 uid
 * @param intro
 * @param signState
 * @param attribute
 * @param sections 合集分集
 */
@Serializable
data class UgcSeason(
    val id: Int,
    val title: String,
    val cover: String,
    val mid: Long,
    val intro: String,
    @SerialName("sign_state")
    val signState: Int,
    val attribute: Int,
    val sections: List<Section>
) {
    @Serializable
    data class Section(
        @SerialName("season_id")
        val seasonId: Int,
        val id: Int,
        val title: String,
        val type: Int,
        val episodes: List<Episode>
    ) {
        @Serializable
        data class Episode(
            @SerialName("season_id")
            val seasonId: Int,
            @SerialName("section_id")
            val sectionId: Int,
            val id: Int,
            val aid: Long,
            val cid: Long,
            val title: String,
            val attribute: Int,
            val arc: Arc,
            val page: VideoPage,
            val bvid: String
        ) {
            @Serializable
            data class Arc(
                val aid: Long,
                val videos: Int,
                @SerialName("type_id")
                val typeId: Int,
                @SerialName("type_name")
                val typeName: String,
                val copyright: Int,
                val pic: String,
                val title: String,
                @SerialName("pubdate")
                val pubDate: Int,
                val ctime: Int,
                val desc: String,
                val state: Int,
                val duration: Int,
                val rights: VideoRights,
                //val author: Author,
                val stat: VideoStat,
                val dynamic: String,
                //val dimension: Dimension,
                @SerialName("desc_v2")
                val descV2: JsonElement? = null,
                @SerialName("is_chargeable_season")
                val isChargeableSeason: Boolean,
                @SerialName("is_blooper")
                val isBlooper: Boolean
            ) {
                @Serializable
                data class Author(
                    val mid: Long,
                    val name: String,
                    val face: String
                )
            }
        }
    }
}
