package dev.aaa1115910.bilisubtitle.entity

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class BiliSubtitle constructor(
    @SerialName("font_size")
    val fontSize: Float? = null,
    @SerialName("font_color")
    val fontColor: String? = null,
    @SerialName("background_alpha")
    val backgroundAlpha: Float? = null,
    @SerialName("background_color")
    val backgroundColor: String? = null,
    // AI字幕返回的属性是大写的（Stroke），非AI字幕是小写的（stroke）
    // 兼容大小写写法，序列化输出使用小写 stroke
    @SerialName("Stroke")
    @JsonNames("stroke")
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
    val music: Float? = null,
    val version: String? = null // 自动翻译字幕特有属性
)