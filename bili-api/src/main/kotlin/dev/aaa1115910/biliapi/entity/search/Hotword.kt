package dev.aaa1115910.biliapi.entity.search

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class HotwordData(
    val trending: Trending
) {
    @Serializable
    data class Trending(
        val title: String,
        @SerialName("trackid")
        val trackId: String,
        val list: List<Hotword>,
        @SerialName("top_list")
        val topList: JsonElement? = null
    )
}

/**
 * 热门关键词
 *
 * @param keyword 关键词
 * @param showName 完整关键词
 * @param icon 图标 url
 * @param uri
 * @param goto
 */
@Serializable
data class Hotword(
    val keyword: String,
    @SerialName("show_name")
    val showName: String,
    val icon: String,
    val uri: String,
    val goto: String
)