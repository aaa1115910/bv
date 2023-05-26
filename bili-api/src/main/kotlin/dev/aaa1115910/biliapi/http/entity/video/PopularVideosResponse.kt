package dev.aaa1115910.biliapi.http.entity.video

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PopularVideoData(
    val list: List<VideoInfo>,
    @SerialName("no_more")
    val noMore: Boolean
)