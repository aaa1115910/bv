package dev.aaa1115910.bv.mobile.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.media3.exoplayer.ExoPlayer
import com.kuaishou.akdanmaku.data.DanmakuItemData
import com.kuaishou.akdanmaku.render.SimpleRenderer
import com.kuaishou.akdanmaku.ui.DanmakuPlayer
import dev.aaa1115910.biliapi.entity.ApiType
import dev.aaa1115910.biliapi.entity.CodeType
import dev.aaa1115910.biliapi.entity.PlayData
import dev.aaa1115910.biliapi.entity.reply.Comment
import dev.aaa1115910.biliapi.entity.reply.CommentPage
import dev.aaa1115910.biliapi.entity.reply.CommentSort
import dev.aaa1115910.biliapi.entity.video.Subtitle
import dev.aaa1115910.biliapi.entity.video.VideoDetail
import dev.aaa1115910.biliapi.entity.video.VideoPage
import dev.aaa1115910.biliapi.entity.video.season.Section
import dev.aaa1115910.biliapi.http.BiliHttpApi
import dev.aaa1115910.biliapi.repositories.VideoDetailRepository
import dev.aaa1115910.biliapi.repositories.VideoPlayRepository
import dev.aaa1115910.bilisubtitle.entity.SubtitleItem
import dev.aaa1115910.bv.BVApp
import dev.aaa1115910.bv.entity.Audio
import dev.aaa1115910.bv.entity.Resolution
import dev.aaa1115910.bv.entity.VideoCodec
import dev.aaa1115910.bv.player.AbstractVideoPlayer
import dev.aaa1115910.bv.player.mobile.component.DanmakuType
import dev.aaa1115910.bv.player.mobile.component.playUrl
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.fException
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.fWarn
import dev.aaa1115910.bv.util.swapList
import dev.aaa1115910.bv.util.swapMap
import dev.aaa1115910.bv.util.toast
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class MobileVideoPlayerViewModel(
    private val videoDetailRepository: VideoDetailRepository,
    private val videoPlayRepository: VideoPlayRepository
) : ViewModel() {
    private val logger = KotlinLogging.logger {}

    var videoPlayer: AbstractVideoPlayer? by mutableStateOf(null)
    var danmakuPlayer: DanmakuPlayer? by mutableStateOf(null)

    var avid: Long = 0
    var videoDetail: VideoDetail? by mutableStateOf(null)
    private var playData: PlayData? by mutableStateOf(null)
    var danmakuData = mutableStateListOf<DanmakuItemData>()
    var comments = mutableStateListOf<Comment>()

    var hasMoreComments = true
    var refreshingComments by mutableStateOf(false)
    var updatingComments = false
    var nextCommentPage = CommentPage()
    var commentSort by mutableStateOf(CommentSort.Hot)
    var rpid by mutableLongStateOf(0L)
    var rpCount by mutableIntStateOf(0)

    var availableQuality = mutableStateMapOf<Int, String>()
    var availableVideoCodec = mutableStateListOf<VideoCodec>()
    var availableSubtitle = mutableStateListOf<Subtitle>()
    var availableAudio = mutableStateListOf<Audio>()

    var currentQuality by mutableStateOf(Prefs.defaultQuality)
    var currentVideoCodec by mutableStateOf(Prefs.defaultVideoCodec)
    var currentAudio by mutableStateOf(Prefs.defaultAudio)
    var currentDanmakuScale by mutableStateOf(Prefs.defaultDanmakuScale)
    var currentDanmakuOpacity by mutableStateOf(Prefs.defaultDanmakuOpacity)
    var currentDanmakuEnabled by mutableStateOf(Prefs.defaultDanmakuEnabled)
    val currentDanmakuTypes = mutableStateListOf<DanmakuType>().apply {
        addAll(Prefs.defaultDanmakuTypes.map {
            when (it) {
                dev.aaa1115910.bv.component.controllers2.DanmakuType.All -> DanmakuType.All
                dev.aaa1115910.bv.component.controllers2.DanmakuType.Rolling -> DanmakuType.Rolling
                dev.aaa1115910.bv.component.controllers2.DanmakuType.Top -> DanmakuType.Top
                dev.aaa1115910.bv.component.controllers2.DanmakuType.Bottom -> DanmakuType.Bottom
            }
        })
    }
    var currentDanmakuArea by mutableStateOf(Prefs.defaultDanmakuArea)
    var currentSubtitleId by mutableStateOf(-1L)
    var currentSubtitleData = mutableStateListOf<SubtitleItem>()
    var currentSubtitleFontSize by mutableStateOf(Prefs.defaultSubtitleFontSize)
    var currentSubtitleBackgroundOpacity by mutableStateOf(Prefs.defaultSubtitleBackgroundOpacity)
    var currentSubtitleBottomPadding by mutableStateOf(Prefs.defaultSubtitleBottomPadding)

    suspend fun updateVideoInfo(avid: Long) {
        this.avid = avid
        runCatching {
            videoDetail = videoDetailRepository.getVideoDetail(
                aid = avid,
                preferApiType = Prefs.apiType
            )
        }.onFailure {
            withContext(Dispatchers.Main) {
                "视频详细信息加载失败：${it.localizedMessage}".toast(BVApp.context)
            }
            logger.fInfo { "Get video info failed: ${it.stackTraceToString()}" }
        }
    }

    suspend fun playFirstPartVideo() {
        val video = videoDetail?.pages?.first() ?: videoDetail?.ugcSeason?.sections?.first()
        println(video)
        require(video != null) { "没有可播放视频" }
        releaseDanmakuPlayer()
        reInitDanmakuPlayer()
        val cid = if (video is VideoPage) video.cid else (video as Section).id
        loadPlayUrl(
            avid = avid,
            cid = cid
        )
        loadDanmaku(cid)
    }

    private suspend fun loadPlayUrl(
        avid: Long,
        cid: Long,
        preferApiType: ApiType = Prefs.apiType
    ) {

        runCatching {
            val playData = videoPlayRepository.getPlayData(
                aid = avid,
                cid = cid,
                preferApiType = preferApiType
            )

            this.playData = playData
            logger.fInfo { "Load play url response success" }
            //logger.info { "Play url response: $responseData" }

            //读取清晰度
            val resolutionMap = mutableMapOf<Int, String>()
            playData.dashVideos.forEach {
                if (!resolutionMap.containsKey(it.quality)) {
                    val name = Resolution.fromCode(it.quality)?.getShortDisplayName(BVApp.context)
                        ?: "Unknown"
                    resolutionMap[it.quality] = name
                }
            }

            logger.fInfo { "Video available resolution: $resolutionMap" }
            availableQuality.swapMap(resolutionMap)

            //读取音频
            val audioList = mutableListOf<Audio>()
            playData.dashAudios.forEach {
                Audio.fromCode(it.codecId)?.let { audio ->
                    if (!audioList.contains(audio)) audioList.add(audio)
                }
            }
            playData.dolby?.let {
                Audio.fromCode(it.codecId)?.let { audio ->
                    audioList.add(audio)
                }
            }
            playData.flac?.let {
                Audio.fromCode(it.codecId)?.let { audio ->
                    audioList.add(audio)
                }
            }

            logger.fInfo { "Video available audio: $audioList" }
            availableAudio.swapList(audioList)

            //先确认最终所选清晰度
            val existDefaultResolution =
                availableQuality.keys.find { it == Prefs.defaultQuality } != null

            if (!existDefaultResolution) {
                val tempList = resolutionMap.keys.sorted()
                currentQuality = tempList.first()
                tempList.forEach {
                    if (it <= Prefs.defaultQuality) {
                        currentQuality = it
                    }
                }
            }

            //确认最终所选音质
            val existDefaultAudio = availableAudio.contains(Prefs.defaultAudio)
            if (!existDefaultAudio) {
                currentAudio = when {
                    availableAudio.contains(Audio.A192K) -> Audio.A192K
                    availableAudio.contains(Audio.A132K) -> Audio.A132K
                    availableAudio.contains(Audio.A64K) -> Audio.A64K
                    else -> availableAudio.first()
                }
            }

            //再确认最终所选视频编码
            updateAvailableCodec()

            playQuality(qn = currentQuality, codec = currentVideoCodec)

        }.onFailure {
            //addLogs("加载视频地址失败：${it.localizedMessage}")
            //errorMessage = it.stackTraceToString()
            //loadState = RequestState.Failed
            logger.fException(it) { "Load video failed" }
        }.onSuccess {
            //addLogs("加载视频地址成功")
            //loadState = RequestState.Success
            logger.fInfo { "Load play url success" }
        }
    }


    fun updateAvailableCodec() {
        if (Prefs.apiType == ApiType.App) return
        val supportedCodec = playData!!.codec
        val codecList =
            supportedCodec[currentQuality]!!.mapNotNull { VideoCodec.fromCodecString(it) }

        availableVideoCodec.swapList(codecList)
        logger.fInfo { "Video available codec: ${availableVideoCodec.toList()}" }

        logger.fInfo { "Default codec: $currentVideoCodec" }
        currentVideoCodec = if (codecList.contains(Prefs.defaultVideoCodec)) {
            Prefs.defaultVideoCodec
        } else {
            codecList.minByOrNull { it.ordinal }!!
        }
        logger.fInfo { "Select codec: $currentVideoCodec" }
    }

    suspend fun playQuality(
        qn: Int = currentQuality,
        codec: VideoCodec = currentVideoCodec,
        audio: Audio = currentAudio
    ) {
        logger.fInfo { "Select resolution: $qn, codec: $codec, audio: $audio" }
        //addLogs("播放清晰度：${availableQuality[qn]}, 视频编码：${codec.getDisplayName(BVApp.context)}")

        val videoItem = playData!!.dashVideos.find {
            when (Prefs.apiType) {
                ApiType.Web -> it.quality == qn && it.codecs!!.startsWith(codec.prefix)
                ApiType.App -> it.quality == qn
            }
        }
        val videoUrl = videoItem?.baseUrl ?: playData!!.dashVideos.first().baseUrl

        val audioItem = playData!!.dashAudios.find { it.codecId == audio.code }
            ?: playData!!.dolby.takeIf { it?.codecId == audio.code }
            ?: playData!!.flac.takeIf { it?.codecId == audio.code }
            ?: playData!!.dashAudios.minByOrNull { it.codecId }
        val audioUrl = audioItem?.baseUrl ?: playData!!.dashAudios.first().baseUrl

        logger.fInfo { "Select audio: $audioItem" }
        //addLogs("音频编码：${(Audio.fromCode(audioItem?.id ?: 0))?.getDisplayName(BVApp.context) ?: "未知"}")

        //currentVideoHeight = videoItem?.height ?: 0
        //currentVideoWidth = videoItem?.width ?: 0

        withContext(Dispatchers.Main) {
            logger.info { "Video url: $videoUrl" }
            logger.info { "Audio url: $audioUrl" }
            videoPlayer!!.playUrl(videoUrl, audioUrl)
            videoPlayer!!.prepare()
            // showBuffering = true
        }
    }

    suspend fun loadMoreComment() {
        if (updatingComments) return
        updatingComments = true
        if (!hasMoreComments) {
            updatingComments = false
            delay(300)
            refreshingComments = false
            return
        }
        logger.fInfo { "Load more comment, page=$nextCommentPage" }
        runCatching {
            val commentsData = videoDetailRepository.getComments(
                aid = avid,
                page = nextCommentPage,
                sort = commentSort,
                preferApiType = Prefs.apiType
            )
            nextCommentPage = commentsData.nextPage
            hasMoreComments = commentsData.hasNext
            comments.addAll(commentsData.comments)
        }.onFailure {
            logger.fException(it) { "Load more comment failed" }
            withContext(Dispatchers.Main) {
                "加载评论失败：${it.localizedMessage}".toast(BVApp.context)
            }
        }
        updatingComments = false
        delay(300)
        refreshingComments = false
    }

    private suspend fun loadDanmaku(cid: Long) {
        runCatching {
            val danmakuXmlData = BiliHttpApi.getDanmakuXml(cid = cid, sessData = Prefs.sessData)

            danmakuData.clear()
            danmakuData.addAll(danmakuXmlData.data.map {
                DanmakuItemData(
                    danmakuId = it.dmid,
                    position = (it.time * 1000).toLong(),
                    content = it.text,
                    mode = when (it.type) {
                        4 -> DanmakuItemData.DANMAKU_MODE_CENTER_TOP
                        5 -> DanmakuItemData.DANMAKU_MODE_CENTER_BOTTOM
                        else -> DanmakuItemData.DANMAKU_MODE_ROLLING
                    },
                    textSize = it.size,
                    textColor = Color(it.color).toArgb()
                )
            })
            danmakuPlayer?.updateData(danmakuData)
        }.onFailure {
            logger.fWarn { "Load danmaku filed: ${it.stackTraceToString()}" }
        }.onSuccess {
            logger.fInfo { "Load danmaku success, size=${danmakuData.size}" }
        }
    }

    suspend fun switchCommentSort(newSort: CommentSort) {
        logger.fInfo { "Switch comment sort to ${newSort.name}" }
        commentSort = newSort
        refreshComments()
    }

    suspend fun refreshComments() {
        refreshingComments = true
        logger.fInfo { "refresh comment" }
        nextCommentPage = CommentPage()
        hasMoreComments = true
        comments.clear()
        loadMoreComment()
    }

    private suspend fun releaseDanmakuPlayer() = withContext(Dispatchers.Main) {
        println("release danmaku player")
        danmakuPlayer?.release()
    }

    private suspend fun reInitDanmakuPlayer() = withContext(Dispatchers.Main) {
        println("set new danmaku player")
        danmakuPlayer = DanmakuPlayer(SimpleRenderer())
    }
}