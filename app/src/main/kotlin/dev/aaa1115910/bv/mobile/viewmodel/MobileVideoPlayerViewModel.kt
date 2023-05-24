package dev.aaa1115910.bv.mobile.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.media3.exoplayer.ExoPlayer
import com.kuaishou.akdanmaku.ui.DanmakuPlayer
import dev.aaa1115910.biliapi.BiliApi
import dev.aaa1115910.biliapi.entity.video.Dash
import dev.aaa1115910.biliapi.entity.video.PlayUrlData
import dev.aaa1115910.biliapi.entity.video.UgcSeason
import dev.aaa1115910.biliapi.entity.video.VideoInfo
import dev.aaa1115910.biliapi.entity.video.VideoMoreInfo
import dev.aaa1115910.biliapi.entity.video.VideoPage
import dev.aaa1115910.bilisubtitle.entity.SubtitleItem
import dev.aaa1115910.bv.BVApp
import dev.aaa1115910.bv.component.controllers2.DanmakuType
import dev.aaa1115910.bv.entity.Audio
import dev.aaa1115910.bv.entity.VideoCodec
import dev.aaa1115910.bv.player.mobile.component.playUrl
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.fException
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.swapList
import dev.aaa1115910.bv.util.swapMap
import dev.aaa1115910.bv.util.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mu.KotlinLogging

class MobileVideoPlayerViewModel : ViewModel() {
    private val logger = KotlinLogging.logger {}
    var videoPlayer: ExoPlayer? by mutableStateOf(null)
    var danmakuPlayer: DanmakuPlayer? by mutableStateOf(null)

    var avid: Int = 0
    var videoInfo: VideoInfo? by mutableStateOf(null)
    private var playUrlResponse: PlayUrlData? by mutableStateOf(null)
    var dashData: Dash? = null

    var availableQuality = mutableStateMapOf<Int, String>()
    var availableVideoCodec = mutableStateListOf<VideoCodec>()
    var availableSubtitle = mutableStateListOf<VideoMoreInfo.SubtitleItem>()
    var availableAudio = mutableStateListOf<Audio>()

    var currentQuality by mutableStateOf(Prefs.defaultQuality)
    var currentVideoCodec by mutableStateOf(Prefs.defaultVideoCodec)
    var currentAudio by mutableStateOf(Prefs.defaultAudio)
    var currentDanmakuScale by mutableStateOf(Prefs.defaultDanmakuScale)
    var currentDanmakuOpacity by mutableStateOf(Prefs.defaultDanmakuOpacity)
    var currentDanmakuEnabled by mutableStateOf(Prefs.defaultDanmakuEnabled)
    val currentDanmakuTypes = mutableStateListOf<DanmakuType>().apply {
        addAll(Prefs.defaultDanmakuTypes)
    }
    var currentDanmakuArea by mutableStateOf(Prefs.defaultDanmakuArea)
    var currentSubtitleId by mutableStateOf(-1L)
    var currentSubtitleData = mutableStateListOf<SubtitleItem>()
    var currentSubtitleFontSize by mutableStateOf(Prefs.defaultSubtitleFontSize)
    var currentSubtitleBackgroundOpacity by mutableStateOf(Prefs.defaultSubtitleBackgroundOpacity)
    var currentSubtitleBottomPadding by mutableStateOf(Prefs.defaultSubtitleBottomPadding)

    suspend fun updateVideoInfo(avid: Int) {
        this.avid = avid
        runCatching {
            val response = BiliApi.getVideoInfo(av = avid, sessData = Prefs.sessData)
            videoInfo = response.getResponseData()
        }.onFailure {
            withContext(Dispatchers.Main) {
                "视频详细信息加载失败：${it.localizedMessage}".toast(BVApp.context)
            }
            logger.fInfo { "Get video info failed: ${it.stackTraceToString()}" }
        }
    }

    suspend fun playFirstPartVideo() {
        val video = videoInfo?.pages?.first()?:videoInfo?.ugcSeason?.sections?.first()
        println(video)
        require(video != null) { "没有可播放视频" }
        loadPlayUrl(
            avid = avid,
            cid = if (video is VideoPage) video.cid else (video as UgcSeason.Section).id
        )
    }

    private suspend fun loadPlayUrl(
        avid: Int,
        cid: Int,
        fnval: Int = 4048,
        qn: Int = 80,
        fnver: Int = 0,
        fourk: Int = 0
    ) {

        runCatching {
            val responseData = BiliApi.getVideoPlayUrl(
                av = avid,
                cid = cid,
                fnval = fnval,
                qn = qn,
                fnver = fnver,
                fourk = fourk,
                sessData = Prefs.sessData
            ).getResponseData()

            playUrlResponse = responseData
            logger.fInfo { "Load play url response success" }
            //logger.info { "Play url response: $responseData" }

            //读取清晰度
            val resolutionMap = mutableMapOf<Int, String>()
            responseData.dash?.video?.forEach {
                if (!resolutionMap.containsKey(it.id)) {
                    val index = responseData.acceptQuality.indexOf(it.id)
                    resolutionMap[it.id] = responseData.acceptDescription[index]
                }
            }

            logger.fInfo { "Video available resolution: $resolutionMap" }
            availableQuality.swapMap(resolutionMap)

            //读取音频
            val audioList = mutableListOf<Audio>()
            responseData.dash?.audio?.forEach {
                Audio.fromCode(it.id)?.let { audio ->
                    if (!audioList.contains(audio)) audioList.add(audio)
                }
            }
            responseData.dash?.dolby?.audio?.forEach {
                Audio.fromCode(it.id)?.let { audio ->
                    if (!audioList.contains(audio)) audioList.add(audio)
                }
            }
            responseData.dash?.flac?.audio?.let {
                Audio.fromCode(it.id)?.let { audio ->
                    if (!audioList.contains(audio)) audioList.add(audio)
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

            dashData = responseData.dash!!

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
        val currentResolutionInfo =
            playUrlResponse!!.supportFormats.find { it.quality == currentQuality }
        val codecList = currentResolutionInfo!!.codecs!!
            .mapNotNull { VideoCodec.fromCodecString(it) }
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

        val videoItem = dashData!!.video.find { it.id == qn && it.codecs.startsWith(codec.prefix) }
        val videoUrl = videoItem?.baseUrl ?: dashData!!.video[0].baseUrl

        val audioItem = dashData?.audio?.find { it.id == audio.code }
            ?: dashData?.dolby?.audio?.find { it.id == audio.code }
            ?: dashData?.flac?.audio?.let { if (it.id == audio.code) dashData?.flac?.audio else null }
            ?: dashData?.audio?.minByOrNull { it.codecs }
        val audioUrl = audioItem?.baseUrl ?: dashData?.audio?.first()?.baseUrl

        logger.fInfo { "Select audio: $audioItem" }
        //addLogs("音频编码：${(Audio.fromCode(audioItem?.id ?: 0))?.getDisplayName(BVApp.context) ?: "未知"}")

        //currentVideoHeight = videoItem?.height ?: 0
        //currentVideoWidth = videoItem?.width ?: 0

        withContext(Dispatchers.Main) {
            videoPlayer!!.playUrl(videoUrl, audioUrl)
            videoPlayer!!.prepare()
            // showBuffering = true
        }
    }
}