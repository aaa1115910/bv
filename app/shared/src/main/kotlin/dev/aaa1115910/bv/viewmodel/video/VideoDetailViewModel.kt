package dev.aaa1115910.bv.viewmodel.video

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dev.aaa1115910.biliapi.entity.video.VideoDetail
import dev.aaa1115910.biliapi.repositories.VideoDetailRepository
import dev.aaa1115910.bv.entity.carddata.VideoCardData
import dev.aaa1115910.bv.player.entity.VideoListItem
import dev.aaa1115910.bv.player.entity.VideoListPart
import dev.aaa1115910.bv.player.entity.VideoListUgcEpisode
import dev.aaa1115910.bv.player.entity.VideoListUgcEpisodeTitle
import dev.aaa1115910.bv.repository.VideoInfoRepository
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.swapListWithMainContext
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class VideoDetailViewModel(
    private val videoDetailRepository: VideoDetailRepository,
    private val videoInfoRepository: VideoInfoRepository
) : ViewModel() {
    private val logger = KotlinLogging.logger { }
    var state by mutableStateOf(VideoInfoState.Loading)
    var videoDetail: VideoDetail? by mutableStateOf(null)

    var relatedVideos = mutableStateListOf<VideoCardData>()

    suspend fun loadDetail(aid: Long, fromPgcSeason: Boolean = false, withoutUserActions: Boolean = false) {
        logger.fInfo { "Load detail: [avid=$aid, preferApiType=${Prefs.apiType.name}]" }
        state = VideoInfoState.Loading
        runCatching {
            val videoDetailData = videoDetailRepository.getVideoDetail(
                aid = aid,
                preferApiType = Prefs.apiType,
                withoutUserActions = withoutUserActions
            )
            withContext(Dispatchers.Main) { videoDetail = videoDetailData }
            if (!fromPgcSeason) updateVideoList(aid)
        }.onFailure {
            state = VideoInfoState.Error
            logger.fInfo { "Load video av$aid failed: ${it.stackTraceToString()}" }
        }.onSuccess {
            state = VideoInfoState.Success
            logger.fInfo { "Load video av$aid success" }

            updateRelatedVideos()
        }.getOrThrow()
    }

    suspend fun loadDetailOnlyUpdateHistory(aid: Long) {
        logger.fInfo { "Load detail only update history: [avid=$aid, preferApiType=${Prefs.apiType.name}]" }
        runCatching {
            val historyData = videoDetailRepository.getVideoDetail(
                aid = aid,
                preferApiType = Prefs.apiType,
                withoutUserActions = true
            ).history
            withContext(Dispatchers.Main) { videoDetail?.history = historyData }
        }.onFailure {
            logger.fInfo { "Load video av$aid only update history failed: ${it.stackTraceToString()}" }
        }.onSuccess {
            logger.fInfo { "Load video av$aid only update history success: ${videoDetail?.history}" }
        }
    }

    private suspend fun updateRelatedVideos() {
        logger.fInfo { "Start update relate video" }
        val relateVideoCardDataList = videoDetail?.relatedVideos?.map {
            VideoCardData(
                avid = it.aid,
                title = it.title,
                cover = it.cover,
                upName = it.author?.name ?: "",
                time = it.duration * 1000L,
                play = it.view,
                danmaku = it.danmaku,
                jumpToSeason = it.jumpToSeason,
                epId = it.epid,
                pubTime = it.pubTime
            )
        } ?: emptyList()
        relatedVideos.swapListWithMainContext(relateVideoCardDataList)
        logger.fInfo { "Update ${relateVideoCardDataList.size} relate videos" }
    }

    private fun updateVideoList(aid: Long) {
        if (videoDetail?.ugcSeason != null) {
            updateUgcSeasonSectionVideoList(0)
        } else {
            val partVideoList =
                videoDetail!!.pages.mapIndexed { index, videoPage ->
                    VideoListPart(
                        aid = aid,
                        cid = videoPage.cid,
                        title = videoDetail!!.title,
                        partTitle = videoPage.title,
                        index = index,
                    )
                }
            videoInfoRepository.videoList.clear()
            videoInfoRepository.videoList.addAll(partVideoList)
        }
    }

    fun updateUgcSeasonSectionVideoList(sectionIndex: Int) {
        val partVideoList = mutableListOf<VideoListItem>()
        videoDetail!!.ugcSeason!!.sections[sectionIndex].episodes.mapIndexed { epIndex, episode ->
            if (episode.pages.size == 1) {
                episode.pages.mapIndexed { pageInd, videoPage ->
                    partVideoList.add(
                        VideoListUgcEpisode(
                            aid = episode.aid,
                            cid = videoPage.cid,
                            title = episode.title,
                            partTitle = "",
                            index = epIndex
                        )
                    )
                }
            } else {
                partVideoList.add(
                    VideoListUgcEpisodeTitle(
                        title = episode.title,
                        index = epIndex,
                    )
                )
                episode.pages.mapIndexed { pageIndex, videoPage ->
                    partVideoList.add(
                        VideoListPart(
                            aid = episode.aid,
                            cid = videoPage.cid,
                            title = episode.title,
                            partTitle = videoPage.title,
                            index = pageIndex,
                        )
                    )
                }
            }
        }
        videoInfoRepository.videoList.clear()
        videoInfoRepository.videoList.addAll(partVideoList)
    }
}

enum class VideoInfoState {
    Loading,
    Success,
    Error
}