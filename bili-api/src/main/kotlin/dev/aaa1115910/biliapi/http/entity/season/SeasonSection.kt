package dev.aaa1115910.biliapi.http.entity.season

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray

/**
 * 板块 花絮、PV、番外等非正片内容
 *
 * @param attr
 * @param episodeId
 * @param episodeIds
 * @param episodes 板块内容
 * @param id 板块id
 * @param title 板块标题
 * @param type
 */
@Serializable
data class SeasonSection(
    val attr: Int,
    @SerialName("episode_id")
    val episodeId: Int,
    @SerialName("episode_ids")
    val episodeIds: JsonArray? = null,
    val episodes: List<Episode> = emptyList(),
    val id: Int,
    val title: String,
    val type: Int
)
