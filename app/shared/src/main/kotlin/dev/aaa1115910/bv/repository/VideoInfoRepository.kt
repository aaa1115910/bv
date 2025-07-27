package dev.aaa1115910.bv.repository

import dev.aaa1115910.bv.entity.carddata.VideoCardData
import dev.aaa1115910.bv.player.entity.VideoListItem
import org.koin.core.annotation.Single

@Single
class VideoInfoRepository {
    val videoList = mutableListOf<VideoListItem>()
    val relatedVideos = mutableListOf<VideoCardData>()
}
