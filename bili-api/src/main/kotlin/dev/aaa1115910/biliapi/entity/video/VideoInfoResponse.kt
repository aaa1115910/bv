package dev.aaa1115910.biliapi.entity.video

import kotlinx.serialization.Serializable


@Serializable
data class VideoInfoResponse(
    val code: Int,
    val message: String,
    val ttl: Int,
    val data: VideoInfo
)