package dev.aaa1115910.biliapi.http.entity.video

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class VideoShot(
    @SerialName("pvdata")
    val pvData: String? = null,
    @SerialName("img_x_len")
    val imgXLen: Int = 10,
    @SerialName("img_y_len")
    val imgYLen: Int = 10,
    @SerialName("img_x_size")
    val imgXSize: Int = 0,
    @SerialName("img_y_size")
    val imgYSize: Int = 0,
    val image: List<String> = emptyList(),
    val index: List<UShort>? = null,
    @SerialName("video_shots")
    var videoShots: JsonElement? = null,
    var indexs: JsonElement? = null
)
