package dev.aaa1115910.bilisubtitle.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BiliSubtitle(
    @SerialName("font_size")
    val fontSize: Float? = null,
    @SerialName("font_color")
    val fontColor: String? = null,
    @SerialName("background_alpha")
    val backgroundAlpha: Float? = null,
    @SerialName("background_color")
    val backgroundColor: String? = null,
    @SerialName("Stroke")
    val stroke: String? = null,
    val type: String? = null,
    val lang: String? = null,
    val version: String? = null,
    val body: List<BiliSubtitleItem> = emptyList()
)

@Serializable
data class BiliSubtitleItem(
    val from: Float,
    val to: Float,
    val sid: Int? = null,
    val location: Int,
    val content: String,
    val music: Float? = null
)