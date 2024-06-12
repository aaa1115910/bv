package dev.aaa1115910.bv.screen

import android.app.Activity
import android.os.CountDownTimer
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.tv.material3.Text
import com.kuaishou.akdanmaku.DanmakuConfig
import com.kuaishou.akdanmaku.data.DanmakuItemData
import com.kuaishou.akdanmaku.ecs.component.filter.TypeFilter
import com.kuaishou.akdanmaku.ext.RETAINER_BILIBILI
import dev.aaa1115910.biliapi.entity.danmaku.DanmakuMaskFrame
import dev.aaa1115910.biliapi.entity.video.VideoShot
import dev.aaa1115910.bv.component.DanmakuPlayerCompose
import dev.aaa1115910.bv.component.controllers.LocalVideoPlayerControllerData
import dev.aaa1115910.bv.component.controllers.VideoPlayerControllerData
import dev.aaa1115910.bv.component.controllers.info.VideoPlayerInfoData
import dev.aaa1115910.bv.component.controllers2.DanmakuType
import dev.aaa1115910.bv.component.controllers2.VideoPlayerController
import dev.aaa1115910.bv.component.ifElse
import dev.aaa1115910.bv.entity.VideoAspectRatio
import dev.aaa1115910.bv.entity.proxy.ProxyArea
import dev.aaa1115910.bv.player.BvVideoPlayer
import dev.aaa1115910.bv.player.VideoPlayerListener
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.countDownTimer
import dev.aaa1115910.bv.util.danmakuMask
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.formatMinSec
import dev.aaa1115910.bv.util.swapList
import dev.aaa1115910.bv.util.timeTask
import dev.aaa1115910.bv.viewmodel.RequestState
import dev.aaa1115910.bv.viewmodel.VideoPlayerV3ViewModel
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel
import java.util.Calendar
import java.util.Timer
import kotlin.math.max

@Composable
fun VideoPlayerV3Screen(
    modifier: Modifier = Modifier,
    playerViewModel: VideoPlayerV3ViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val videoPlayer = playerViewModel.videoPlayer!!
    val logger = KotlinLogging.logger { }

    val focusRequester = remember { FocusRequester() }

    var infoData by remember {
        mutableStateOf(
            VideoPlayerInfoData(
                totalDuration = 0,
                currentTime = 0,
                bufferedPercentage = 0,
                resolutionWidth = 0,
                resolutionHeight = 0,
                codec = "null"
            )
        )
    }
    var debugInfo by remember { mutableStateOf("") }
    var showLogs by remember { mutableStateOf(false) }
    var showBackToHistory by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(true) }
    var isBuffering by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    var exception: Exception? by remember { mutableStateOf(null) }
    var proxyArea by remember { mutableStateOf(ProxyArea.MainLand) }

    val typeFilter by remember { mutableStateOf(TypeFilter()) }
    var danmakuConfig by remember { mutableStateOf(DanmakuConfig()) }

    var currentVideoAspectRatio by remember { mutableStateOf(VideoAspectRatio.Default) }
    var currentPosition by remember { mutableLongStateOf(0L) }
    var currentPlaySpeed by remember { mutableFloatStateOf(Prefs.defaultPlaySpeed) }
    var aspectRatio by remember { mutableFloatStateOf(16f / 9f) }

    var clock: Triple<Int, Int, Int> by remember { mutableStateOf(Triple(0, 0, 0)) }

    var hideLogsTimer: CountDownTimer? by remember { mutableStateOf(null) }
    var clockRefreshTimer: CountDownTimer? by remember { mutableStateOf(null) }
    var hideBackToHistoryTimer: CountDownTimer? by remember { mutableStateOf(null) }

    var currentDanmakuMaskFrame: DanmakuMaskFrame? by remember { mutableStateOf(null) }

    val updateSeek: () -> Unit = {
        currentPosition = videoPlayer.currentPosition.coerceAtLeast(0L)
        infoData = VideoPlayerInfoData(
            totalDuration = videoPlayer.duration.coerceAtLeast(0L),
            currentTime = videoPlayer.currentPosition.coerceAtLeast(0L),
            bufferedPercentage = videoPlayer.bufferedPercentage,
            resolutionWidth = videoPlayer.videoWidth,
            resolutionHeight = videoPlayer.videoHeight,
            codec = ""//videoPlayer.videoFormat?.sampleMimeType ?: "null"
        )
        debugInfo = videoPlayer.debugInfo
    }

    val initDanmakuConfig: () -> Unit = {
        val danmakuTypes = playerViewModel.currentDanmakuTypes
        if (!danmakuTypes.contains(DanmakuType.All)) {
            val types = DanmakuType.entries.toMutableList()
            types.remove(DanmakuType.All)
            types.removeAll(danmakuTypes)
            val filterTypes = types.mapNotNull {
                when (it) {
                    DanmakuType.Rolling -> DanmakuItemData.DANMAKU_MODE_ROLLING
                    DanmakuType.Top -> DanmakuItemData.DANMAKU_MODE_CENTER_TOP
                    DanmakuType.Bottom -> DanmakuItemData.DANMAKU_MODE_CENTER_BOTTOM
                    else -> null
                }
            }
            filterTypes.forEach { typeFilter.addFilterItem(it) }
        }
        danmakuConfig = danmakuConfig.copy(
            retainerPolicy = RETAINER_BILIBILI,
            textSizeScale = playerViewModel.currentDanmakuScale,
            dataFilter = listOf(typeFilter)
        )
        danmakuConfig.updateFilter()
        logger.info { "Init danmaku config: $danmakuConfig" }
        playerViewModel.danmakuPlayer?.updateConfig(danmakuConfig)
    }

    val updateDanmakuConfigTypeFilter: () -> Unit = {
        val danmakuTypes = playerViewModel.currentDanmakuTypes
        typeFilter.clear()
        if (!danmakuTypes.contains(DanmakuType.All)) {
            val types = DanmakuType.entries.toMutableList()
            types.remove(DanmakuType.All)
            types.removeAll(danmakuTypes)
            val filterTypes = types.mapNotNull {
                when (it) {
                    DanmakuType.Rolling -> DanmakuItemData.DANMAKU_MODE_ROLLING
                    DanmakuType.Top -> DanmakuItemData.DANMAKU_MODE_CENTER_TOP
                    DanmakuType.Bottom -> DanmakuItemData.DANMAKU_MODE_CENTER_BOTTOM
                    else -> null
                }
            }
            filterTypes.forEach { typeFilter.addFilterItem(it) }
        }
        logger.info { "Update danmaku type filters: ${typeFilter.filterSet}" }
        danmakuConfig.updateFilter()
        playerViewModel.danmakuPlayer?.updateConfig(danmakuConfig)
    }

    val updateDanmakuConfig: () -> Unit = {
        danmakuConfig = danmakuConfig.copy(
            retainerPolicy = RETAINER_BILIBILI,
            textSizeScale = playerViewModel.currentDanmakuScale,
        )
        logger.info { "Update danmaku config: $danmakuConfig" }
        playerViewModel.danmakuPlayer?.updateConfig(danmakuConfig)
    }

    val updateVideoAspectRatio: () -> Unit = {
        aspectRatio = when (currentVideoAspectRatio) {
            VideoAspectRatio.Default -> {
                val aspectRatioValue =
                    playerViewModel.currentVideoWidth / playerViewModel.currentVideoHeight.toFloat()
                if (aspectRatioValue > 0) aspectRatioValue else 16 / 9f
            }

            VideoAspectRatio.FourToThree -> 4 / 3f
            VideoAspectRatio.SixteenToNine -> 16 / 9f
        }
        logger.info { "Update video player aspectRatio: $aspectRatio" }
    }

    val sendHeartbeat: () -> Unit = {
        scope.launch(Dispatchers.IO) {
            val time = withContext(Dispatchers.Main) {
                val currentTime = (videoPlayer.currentPosition.coerceAtLeast(0L) / 1000).toInt()
                val totalTime = (videoPlayer.duration.coerceAtLeast(0L) / 1000).toInt()
                //播放完后上报的时间应为 -1
                if (currentTime >= totalTime) -1 else currentTime
            }
            playerViewModel.uploadHistory(time)
        }
    }

    val videoPlayerListener = object : VideoPlayerListener {
        override fun onError(error: Exception) {
            logger.info { "onError: $error" }
            isError = true
            exception = error.cause as Exception?
        }

        override fun onReady() {
            logger.info { "onReady" }
            isError = false
            exception = null
            initDanmakuConfig()
            updateVideoAspectRatio()

            //reset default play speed
            logger.info { "Reset default play speed: $currentPlaySpeed" }
            videoPlayer.speed = currentPlaySpeed
            playerViewModel.danmakuPlayer?.updatePlaySpeed(currentPlaySpeed)
        }

        override fun onPlay() {
            logger.info { "onPlay" }
            playerViewModel.danmakuPlayer?.start()
            isPlaying = true
            isBuffering = false

            if (playerViewModel.lastPlayed > 0 && hideBackToHistoryTimer == null) {
                showBackToHistory = true
                hideBackToHistoryTimer = countDownTimer(5000, 1000, "hideBackToHistoryTimer") {
                    showBackToHistory = false
                    hideBackToHistoryTimer = null
                    playerViewModel.lastPlayed = 0
                }
            }
        }

        override fun onPause() {
            logger.info { "onPause" }
            playerViewModel.danmakuPlayer?.pause()
            isPlaying = false
        }

        override fun onBuffering() {
            logger.info { "onBuffering" }
            isBuffering = true
            playerViewModel.danmakuPlayer?.pause()
        }

        override fun onEnd() {
            logger.info { "onEnd" }
            playerViewModel.danmakuPlayer?.pause()
            isPlaying = false
            if (!Prefs.incognitoMode) sendHeartbeat()

            val videoListIndex = playerViewModel.availableVideoList.indexOfFirst {
                it.cid == playerViewModel.currentCid
            }
            if (videoListIndex + 1 < playerViewModel.availableVideoList.size) {
                val nextVideo = playerViewModel.availableVideoList[videoListIndex + 1]
                logger.info { "Play next video: $nextVideo" }
                playerViewModel.partTitle = nextVideo.title
                playerViewModel.loadPlayUrl(
                    avid = nextVideo.aid,
                    cid = nextVideo.cid,
                    epid = nextVideo.epid,
                    seasonId = nextVideo.seasonId,
                    continuePlayNext = true
                )
            }
        }

        override fun onSeekBack(seekBackIncrementMs: Long) {
            playerViewModel.danmakuPlayer?.seekTo(currentPosition)
        }

        override fun onSeekForward(seekForwardIncrementMs: Long) {
            playerViewModel.danmakuPlayer?.seekTo(currentPosition)
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(playerViewModel.loadState) {
        when (playerViewModel.loadState) {
            RequestState.Ready -> {}
            RequestState.Doing -> {}
            RequestState.Done -> {}
            RequestState.Success -> {}
            RequestState.Failed -> {
                exception = Exception(playerViewModel.errorMessage)
                isError = true
            }
        }
    }

    DisposableEffect(Unit) {
        val updateSeekTimer = timeTask(0, 100, "updateSeekTimer", false) {
            scope.launch { updateSeek() }
        }
        onDispose {
            updateSeekTimer.cancel()
        }
    }

    DisposableEffect(Unit) {
        var updateSeekTimer: Timer? = null
        var resetTimer: ((Long) -> Unit)? = null

        val updateMask: () -> Unit = {
            currentDanmakuMaskFrame = playerViewModel.danmakuMasks.firstOrNull {
                currentPosition in it.range
            }?.frames?.firstOrNull { currentPosition in it.range }

            if (currentDanmakuMaskFrame != null) {
                resetTimer?.invoke(
                    max(currentDanmakuMaskFrame!!.range.last - currentPosition + 3, 20)
                )
            } else {
                resetTimer?.invoke(2000)
            }
        }

        val timerTask: () -> Unit = {
            scope.launch {
                if (playerViewModel.danmakuMasks.isNotEmpty()) {
                    if (currentDanmakuMaskFrame == null) {
                        //当前无蒙版
                        updateMask()
                    } else if (currentPosition !in currentDanmakuMaskFrame!!.range) {
                        //当前蒙版过期
                        updateMask()
                    } else {
                        //正常情况下不会在未过期时运行到此代码块，除非是卡顿等情况
                        if (isPlaying) {
                            //重新计时
                            val delay =
                                max(currentDanmakuMaskFrame!!.range.last - currentPosition + 3, 20)
                            resetTimer?.invoke(delay)
                        } else {
                            //暂停中。。。
                            resetTimer?.invoke(2000)
                        }
                    }
                } else {
                    //定期检查是否有蒙版
                    currentDanmakuMaskFrame = null
                    resetTimer?.invoke(2000)
                }
            }
        }

        resetTimer = { delay ->
            updateSeekTimer = timeTask(delay, "updateDanmakuMask", false) {
                timerTask()
            }
        }
        resetTimer.invoke(0)

        onDispose {
            updateSeekTimer?.cancel()
        }
    }

    DisposableEffect(Unit) {
        var sendHeartbeatTimer: Timer? = null
        if (!Prefs.incognitoMode) {
            sendHeartbeatTimer = timeTask(
                delay = 5000,
                period = 15000,
                tag = "sendHeartbeatTimer"
            ) {
                scope.launch(Dispatchers.Main) {
                    if (videoPlayer.isPlaying) sendHeartbeat()
                }
            }
        }
        onDispose {
            if (!Prefs.incognitoMode) {
                sendHeartbeat()
                sendHeartbeatTimer?.cancel()
            }
        }
    }


    LaunchedEffect(playerViewModel.lastChangedLog) {
        hideLogsTimer?.cancel()
        showLogs = true
        hideLogsTimer = countDownTimer(3000, 1000, "hideLogsTimer") {
            showLogs = false
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            videoPlayer.release()
        }
    }

    DisposableEffect(Unit) {
        clockRefreshTimer = countDownTimer(
            millisInFuture = Long.MAX_VALUE,
            countDownInterval = 1000,
            tag = "clockRefreshTimer",
            showLogs = false,
            onTick = {
                val calendar = Calendar.getInstance()
                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                val minute = calendar.get(Calendar.MINUTE)
                val second = calendar.get(Calendar.SECOND)
                clock = Triple(hour, minute, second)
            }
        )
        onDispose {
            clockRefreshTimer?.cancel()
        }
    }

    CompositionLocalProvider(
        LocalVideoPlayerControllerData provides VideoPlayerControllerData(
            debugInfo = debugInfo,
            infoData = infoData,
            resolutionMap = playerViewModel.availableQuality,
            availableVideoCodec = playerViewModel.availableVideoCodec,
            availableAudio = playerViewModel.availableAudio,
            availableSubtitleTracks = playerViewModel.availableSubtitle,
            availableVideoList = playerViewModel.availableVideoList,
            currentVideoCid = playerViewModel.currentCid,
            currentResolution = playerViewModel.currentQuality,
            currentVideoCodec = playerViewModel.currentVideoCodec,
            currentVideoAspectRatio = currentVideoAspectRatio,
            currentVideoSpeed = currentPlaySpeed,
            currentAudio = playerViewModel.currentAudio,
            currentDanmakuEnabled = playerViewModel.currentDanmakuEnabled,
            currentDanmakuEnabledList = playerViewModel.currentDanmakuTypes,
            currentDanmakuScale = playerViewModel.currentDanmakuScale,
            currentDanmakuOpacity = playerViewModel.currentDanmakuOpacity,
            currentDanmakuArea = playerViewModel.currentDanmakuArea,
            currentSubtitleId = playerViewModel.currentSubtitleId,
            currentSubtitleData = playerViewModel.currentSubtitleData,
            currentSubtitleFontSize = playerViewModel.currentSubtitleFontSize,
            currentSubtitleBackgroundOpacity = playerViewModel.currentSubtitleBackgroundOpacity,
            currentSubtitleBottomPadding = playerViewModel.currentSubtitleBottomPadding,
            currentPosition = currentPosition,
            lastPlayed = playerViewModel.lastPlayed,
            title = playerViewModel.title,
            secondTitle = playerViewModel.partTitle,
            isPlaying = isPlaying,
            isBuffering = isBuffering,
            isError = isError,
            exception = exception,
            clock = clock,
            showBackToHistory = showBackToHistory,
            needPay = playerViewModel.needPay,
            epid = playerViewModel.epid,
            videoShot = playerViewModel.videoShot
        )
    ) {
        VideoPlayerController(
            modifier = modifier
                .focusRequester(focusRequester),
            videoPlayer = playerViewModel.videoPlayer!!,

            idleIcon = playerViewModel.playerIconIdle,
            movingIcon = playerViewModel.playerIconMoving,

            onPlay = { videoPlayer.start() },
            onPause = {
                videoPlayer.pause()
                if (!Prefs.incognitoMode) sendHeartbeat()
            },
            onExit = {
                if (!Prefs.incognitoMode) sendHeartbeat()
                (context as Activity).finish()
            },
            onGoTime = {
                videoPlayer.seekTo(it)
                playerViewModel.danmakuPlayer?.seekTo(it)
                // akdanmaku 会在跳转后立即播放，如果需要缓冲则会导致弹幕不同步
                playerViewModel.danmakuPlayer?.pause()
            },
            onBackToHistory = {
                val time = playerViewModel.lastPlayed.toLong()
                logger.fInfo { "Back to history: ${time.formatMinSec()}" }
                videoPlayer.seekTo(time)
                playerViewModel.danmakuPlayer?.seekTo(time)
                // akdanmaku 会在跳转后立即播放，如果需要缓冲则会导致弹幕不同步
                playerViewModel.danmakuPlayer?.pause()
                playerViewModel.lastPlayed = 0
                showBackToHistory = false
                hideBackToHistoryTimer?.cancel()
                hideBackToHistoryTimer = null
            },
            onPlayNewVideo = {
                if (!Prefs.incognitoMode) sendHeartbeat()
                playerViewModel.partTitle = it.title
                playerViewModel.loadPlayUrl(
                    avid = it.aid,
                    cid = it.cid,
                    epid = it.epid,
                    seasonId = it.seasonId,
                    continuePlayNext = true
                )
            },

            onResolutionChange = { qualityId ->
                playerViewModel.currentQuality = qualityId
                videoPlayer.pause()
                val current = videoPlayer.currentPosition
                scope.launch(Dispatchers.Default) {
                    playerViewModel.updateAvailableCodec()
                    playerViewModel.playQuality(qualityId)
                    withContext(Dispatchers.Main) {
                        videoPlayer.seekTo(current)
                        videoPlayer.start()
                    }
                }
            },
            onCodecChange = { videoCodec ->
                playerViewModel.currentVideoCodec = videoCodec
                videoPlayer.pause()
                val current = videoPlayer.currentPosition
                scope.launch(Dispatchers.Default) {
                    playerViewModel.playQuality(
                        playerViewModel.currentQuality,
                        playerViewModel.currentVideoCodec
                    )
                    withContext(Dispatchers.Main) {
                        videoPlayer.seekTo(current)
                        videoPlayer.start()
                    }
                }
            },
            onAspectRatioChange = { aspectRadio ->
                currentVideoAspectRatio = aspectRadio
                updateVideoAspectRatio()
            },
            onPlaySpeedChange = { speed ->
                logger.info { "Set default play speed: $speed" }
                Prefs.defaultPlaySpeed = speed
                currentPlaySpeed = speed
                videoPlayer.speed = speed
                playerViewModel.danmakuPlayer?.updatePlaySpeed(speed)
            },
            onAudioChange = { audio ->
                playerViewModel.currentAudio = audio
                videoPlayer.pause()
                val current = videoPlayer.currentPosition
                scope.launch(Dispatchers.IO) {
                    playerViewModel.updateAvailableCodec()
                    playerViewModel.playQuality(audio = audio)
                    withContext(Dispatchers.Main) {
                        videoPlayer.seekTo(current)
                        videoPlayer.start()
                    }
                }
            },
            onDanmakuSwitchChange = { enabledDanmakuTypes ->
                logger.info { "On enabled danmaku type change: $enabledDanmakuTypes" }
                Prefs.defaultDanmakuTypes = enabledDanmakuTypes
                playerViewModel.currentDanmakuTypes.swapList(enabledDanmakuTypes)
                updateDanmakuConfigTypeFilter()
            },
            onDanmakuSizeChange = { scale ->
                logger.info { "On danmaku scale change: $scale" }
                Prefs.defaultDanmakuScale = scale
                playerViewModel.currentDanmakuScale = scale
                updateDanmakuConfig()
            },
            onDanmakuOpacityChange = { opacity ->
                logger.info { "On danmaku opacity change: $opacity" }
                Prefs.defaultDanmakuOpacity = opacity
                playerViewModel.currentDanmakuOpacity = opacity
            },
            onDanmakuAreaChange = { area ->
                logger.info { "On danmaku area change: $area" }
                Prefs.defaultDanmakuArea = area
                playerViewModel.currentDanmakuArea = area
            },
            onSubtitleChange = { subtitle ->
                playerViewModel.loadSubtitle(subtitle.id)
            },
            onSubtitleSizeChange = { size ->
                logger.info { "On subtitle font size change: $size" }
                Prefs.defaultSubtitleFontSize = size
                playerViewModel.currentSubtitleFontSize = size
            },
            onSubtitleBackgroundOpacityChange = { opacity ->
                logger.info { "On subtitle background opacity change: $opacity" }
                Prefs.defaultSubtitleBackgroundOpacity = opacity
                playerViewModel.currentSubtitleBackgroundOpacity = opacity
            },
            onSubtitleBottomPadding = { padding ->
                logger.info { "On subtitle bottom padding change: $padding" }
                Prefs.defaultSubtitleBottomPadding = padding
                playerViewModel.currentSubtitleBottomPadding = padding
            },
            onRequestFocus = { focusRequester.requestFocus() },
        ) {
            Box(
                modifier = Modifier.background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                LaunchedEffect(Unit) {
                    videoPlayer.setOptions()
                }

                BvVideoPlayer(
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(aspectRatio),
                    videoPlayer = videoPlayer,
                    playerListener = videoPlayerListener,
                    isVerticalVideo = playerViewModel.isVerticalVideo
                )

                DanmakuPlayerCompose(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .fillMaxHeight(playerViewModel.currentDanmakuArea)
                        // 在之前版本中，设置 DanmakuConfig 透明度后，更改其它弹幕设置后，可能会导致弹幕透明度
                        // 突然变成完全不透明一瞬间，因此这次新版选择直接在此处设置透明度
                        .alpha(playerViewModel.currentDanmakuOpacity)
                        .ifElse(
                            { Prefs.enableWebmark },
                            Modifier.danmakuMask(currentDanmakuMaskFrame)
                        ),
                    danmakuPlayer = playerViewModel.danmakuPlayer
                )

                if (showLogs) {
                    Column(
                        modifier = Modifier.align(Alignment.BottomStart)
                    ) {
                        Text(text = playerViewModel.logs)
                    }
                }
            }
        }
    }
}
