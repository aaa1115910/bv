package dev.aaa1115910.biliapi.http.entity.region


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegionDynamicList(
    @SerialName("cbottom")
    val cBottom: Long,
    @SerialName("ctop")
    val cTop: Long,
    val new: List<Item>
) {
    @Serializable
    data class Item(
        val cover: String,
        @SerialName("cover_left_icon_1")
        val coverLeftIcon1: Int,
        @SerialName("cover_left_text_1")
        val coverLeftText1: String,
        val danmaku: Int? = null,
        val duration: Int,
        val face: String,
        val favourite: Int? = null,
        val goto: String,
        val like: Int? = null,
        val name: String,
        val param: String,
        val play: Int? = null,
        @SerialName("pubdate")
        val pubDate: Int,
        val reply: Int? = null,
        val rid: Int,
        @SerialName("rname")
        val rName: String,
        val title: String,
        val uri: String
    )
}