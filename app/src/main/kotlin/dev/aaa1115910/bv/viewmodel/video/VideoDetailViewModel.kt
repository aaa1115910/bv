package dev.aaa1115910.bv.viewmodel.video

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dev.aaa1115910.biliapi.entity.video.VideoDetail
import dev.aaa1115910.biliapi.repositories.VideoDetailRepository
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.fInfo
import mu.KotlinLogging

class VideoDetailViewModel(
    private val videoDetailRepository: VideoDetailRepository
) : ViewModel() {
    private val logger = KotlinLogging.logger { }
    var state by mutableStateOf(VideoInfoState.Loading)
    var videoDetail: VideoDetail? by mutableStateOf(null)

    suspend fun loadDetail(aid: Int) {
        logger.fInfo { "Load detail: [avid=$aid, preferApiType=${Prefs.apiType.name}]" }
        state = VideoInfoState.Loading
        runCatching {
            videoDetail = videoDetailRepository.getVideoDetail(
                aid = aid,
                preferApiType = Prefs.apiType
            )
        }.onFailure {
            state = VideoInfoState.Error
        }.onSuccess {
            state = VideoInfoState.Success
            //logger.info { "Video detail loaded: $$videoDetail" }
            logger.fInfo { "Relate video count: ${videoDetail!!.relateVideos.size}" }
        }
    }
}

enum class VideoInfoState {
    Loading,
    Success,
    Error
}