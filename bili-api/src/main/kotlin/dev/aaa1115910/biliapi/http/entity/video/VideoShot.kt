package dev.aaa1115910.biliapi.http.entity.video

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VideoShot(
    @SerialName("pvdata")
    val pvData: String,
    @SerialName("img_x_len")
    val imgXLen: Int,
    @SerialName("img_y_len")
    val imgYLen: Int,
    @SerialName("img_x_size")
    val imgXSize: Int,
    @SerialName("img_y_size")
    val imgYSize: Int,
    val image: List<String> = emptyList(),
    val index: List<UShort>? = null
)
