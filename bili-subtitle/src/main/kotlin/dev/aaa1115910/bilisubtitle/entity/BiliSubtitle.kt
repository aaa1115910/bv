package dev.aaa1115910.bilisubtitle.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BiliSubtitle(
    @SerialName("font_size")
    val fontSize: Float,
    @SerialName("font_color")
    val fontColor: String,
    @SerialName("background_alpha")
    val backgroundAlpha: Float,
    @SerialName("background_color")
    val backgroundColor: String,
    @SerialName("Stroke")
    val stroke: String,
    val body: List<BiliSubtitleItem> = emptyList()
)

@Serializable
data class BiliSubtitleItem(
    val from: Float,
    val to: Float,
    val location: Int,
    val content: String
)