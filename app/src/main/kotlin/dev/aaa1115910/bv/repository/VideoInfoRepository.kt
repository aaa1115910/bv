package dev.aaa1115910.bv.repository

class VideoInfoRepository {
    val videoList = mutableListOf<VideoListItem>()
}

data class VideoListItem(
    val aid: Int,
    val cid: Int,
    val title: String,
    val index: Int,
    val isEpisode: Boolean
)