package dev.aaa1115910.bv.player.tv

import android.os.CountDownTimer
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.tv.material3.Text
import com.kuaishou.akdanmaku.DanmakuConfig
import com.kuaishou.akdanmaku.data.DanmakuItemData
import com.kuaishou.akdanmaku.ecs.component.filter.TypeFilter
import com.kuaishou.akdanmaku.ext.RETAINER_BILIBILI
import com.kuaishou.akdanmaku.ui.DanmakuPlayer
import dev.aaa1115910.biliapi.entity.danmaku.DanmakuMaskFrame
import dev.aaa1115910.biliapi.entity.video.Subtitle
import dev.aaa1115910.bv.player.AbstractVideoPlayer
import dev.aaa1115910.bv.player.BvVideoPlayer
import dev.aaa1115910.bv.player.VideoPlayerListener
import dev.aaa1115910.bv.player.entity.Audio
import dev.aaa1115910.bv.player.entity.DanmakuType
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerClockState
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerConfigData
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerDanmakuMasksData
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerDebugInfoData
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerHistoryData
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerLoadStateData
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerLogsData
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerSeekState
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerStateData
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerVideoInfoData
import dev.aaa1115910.bv.player.entity.RequestState
import dev.aaa1115910.bv.player.entity.Resolution
import dev.aaa1115910.bv.player.entity.VideoAspectRatio
import dev.aaa1115910.bv.player.entity.VideoCodec
import dev.aaa1115910.bv.player.entity.VideoListItem
import dev.aaa1115910.bv.player.entity.VideoRotation
import dev.aaa1115910.bv.player.entity.VideoPlayerClockState
import dev.aaa1115910.bv.player.entity.VideoPlayerDebugInfoData
import dev.aaa1115910.bv.player.entity.VideoPlayerSeekState
import dev.aaa1115910.bv.player.entity.VideoPlayerStateData
import dev.aaa1115910.bv.player.tv.controller.VideoPlayerController
import dev.aaa1115910.bv.util.countDownTimer
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.formatHourMinSec
import dev.aaa1115910.bv.util.requestFocus
import dev.aaa1115910.bv.util.timeTask
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Timer
import kotlin.math.max

@Composable
fun BvPlayer(
    modifier: Modifier = Modifier,
    videoPlayer: AbstractVideoPlayer,
    danmakuPlayer: DanmakuPlayer?,
    playerSeekForwardStep: Int = 10,
    playerSeekBackwardStep: Int = 5,
    showBottomProgressBar: Boolean = false,
    useTextureViewFixPortraitVideo: Boolean = false,
    onSendHeartbeat: suspend (Int) -> Unit,
    onClearBackToHistoryData: () -> Unit,
    onLoadNextVideo: () -> Unit,
    onExit: () -> Unit,
    onLoadNewVideo: (VideoListItem) -> Unit,
    onResolutionChange: (Resolution, afterChange: suspend () -> Unit) -> Unit,
    onCodecChange: (VideoCodec, afterChange: suspend () -> Unit) -> Unit,
    onAspectRatioChange: (VideoAspectRatio) -> Unit,
    onRotationChange: (VideoRotation) -> Unit,
    onPlaySpeedChange: (Float) -> Unit,
    onAudioChange: (Audio, afterChange: suspend () -> Unit) -> Unit,
    onDanmakuSwitchChange: (List<DanmakuType>) -> Unit,
    onDanmakuSizeChange: (Float) -> Unit,
    onDanmakuOpacityChange: (Float) -> Unit,
    onDanmakuAreaChange: (Float) -> Unit,
    onDanmakuMaskChange: (Boolean) -> Unit,
    onSubtitleChange: (Subtitle) -> Unit,
    onSubtitleSizeChange: (TextUnit) -> Unit,
    onSubtitleBackgroundOpacityChange: (Float) -> Unit,
    onSubtitleBottomPadding: (Dp) -> Unit,
    onToggleRelatedVideos: (Boolean) -> Unit = {},
    onOpenUpSpace: () -> Unit = {},
    onShowDanmakuChange: (Boolean) -> Unit = {},
    onLoopPlayModeChange: (Boolean) -> Unit = {},
    onRefreshVideo: () -> Unit = {},
    userActionContent: @Composable (
        modifier: Modifier,
        focusMap: Map<String, FocusRequester>,
        onFocus: (String) -> Unit,
        onPauseAutoHide: (Boolean) -> Unit
    ) -> Unit = { _, _, _, _ -> }
) {
//    // 调试重组次数: AtomicInteger，不被 Compose 追踪，只记录真实由外部状态引起的重组次数。
//    val recomposeCounter = remember { java.util.concurrent.atomic.AtomicInteger(0) }
//    SideEffect {
//        val value = recomposeCounter.incrementAndGet()
//        println("Recompose(BvPlayer): $value")
//    }

    val scope = rememberCoroutineScope()
    val logger = KotlinLogging.logger("BvPlayer")
    //val tvVideoPlayerData = LocalTvVideoPlayerData.current
    val videoPlayerConfigData = LocalVideoPlayerConfigData.current
    val videoPlayerDanmakuMaskData = LocalVideoPlayerDanmakuMasksData.current
    val videoPlayerHistoryData = LocalVideoPlayerHistoryData.current
    val videoPlayerLoadStateData = LocalVideoPlayerLoadStateData.current
    val videoPlayerLogsData = LocalVideoPlayerLogsData.current
    val videoPlayerVideoInfoData = LocalVideoPlayerVideoInfoData.current

    val focusRequester = remember { FocusRequester() }
//    println("isLoop: ${videoPlayerConfigData.isLoop}, showDanmaku: ${videoPlayerConfigData.showDanmaku}")

    // 直接调用 danmakuPlayer 会始终为 null
    var mDanmakuPlayer: DanmakuPlayer? by remember { mutableStateOf(null) }

    var showLogs by remember { mutableStateOf(false) }
    var showBackToHistory by remember { mutableStateOf(false) }
    var isPlaying by rememberSaveable { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    var isBuffering by remember { mutableStateOf(false) }
    var exception by remember { mutableStateOf<Exception?>(null) }
    //var proxyArea by remember { mutableStateOf(ProxyArea.MainLand) }

    val typeFilter by remember { mutableStateOf(TypeFilter()) }
    var danmakuConfig by remember { mutableStateOf(DanmakuConfig()) }

    val seekState = remember { VideoPlayerSeekState() }
    var currentVideoAspectRatio by remember { mutableStateOf(videoPlayerConfigData.currentVideoAspectRatio) }
    var currentVideoRotation by remember { mutableStateOf(videoPlayerConfigData.currentVideoRotation) }
    var currentPlaySpeed by remember { mutableFloatStateOf(videoPlayerConfigData.currentVideoSpeed) }
    var aspectRatioValue by remember { mutableFloatStateOf(16f / 9f) }
    var lastPlayed by remember { mutableLongStateOf(0L) }
    var defaultAspectRatio by remember { mutableFloatStateOf(16 / 9f) }
    var showInfoProvider: () -> Boolean by remember { mutableStateOf({ false }) }

    val clockState = remember { VideoPlayerClockState() }

    var hideLogsTimer: CountDownTimer? by remember { mutableStateOf(null) }
    var clockRefreshTimer: CountDownTimer? by remember { mutableStateOf(null) }
    var hideBackToHistoryTimer: CountDownTimer? by remember { mutableStateOf(null) }

    var currentDanmakuMaskFrame: DanmakuMaskFrame? by remember { mutableStateOf(null) }

    // 独立弹幕层句柄（Stable），父级重组频率降低
    val danmakuLayerHandle = remember { DanmakuLayerHandle() }

    val initDanmakuConfig: () -> Unit = {
        val danmakuTypes = videoPlayerConfigData.currentDanmakuEnabledList
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
            textSizeScale = videoPlayerConfigData.currentDanmakuScale,
            dataFilter = listOf(typeFilter),
            visibility = videoPlayerConfigData.showDanmaku
        )
        danmakuConfig.updateFilter()
        logger.info { "Init danmaku config: $danmakuConfig" }
        mDanmakuPlayer?.updateConfig(danmakuConfig)
    }

    val updateDanmakuConfigTypeFilter: () -> Unit = {
        val danmakuTypes = videoPlayerConfigData.currentDanmakuEnabledList
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
        mDanmakuPlayer?.updateConfig(danmakuConfig)
    }

    val updateDanmakuConfig: () -> Unit = {
        danmakuConfig = danmakuConfig.copy(
            retainerPolicy = RETAINER_BILIBILI,
            textSizeScale = videoPlayerConfigData.currentDanmakuScale,
        )
        logger.info { "Update danmaku config: $danmakuConfig" }
        mDanmakuPlayer?.updateConfig(danmakuConfig)
    }

    val updateVideoAspectRatio: () -> Unit = {
        aspectRatioValue = when (currentVideoAspectRatio) {
            VideoAspectRatio.Default -> defaultAspectRatio
            VideoAspectRatio.FourToThree -> 4 / 3f
            VideoAspectRatio.SixteenToNine -> 16 / 9f
        }
        logger.info { "Update video player aspectRatio: $aspectRatioValue" }
    }

    val sendHeartbeat: () -> Unit = {
        scope.launch(Dispatchers.IO) {
            val time = withContext(Dispatchers.Main) {
                val currentTime = (videoPlayer.currentPosition.coerceAtLeast(0L) / 1000).toInt()
                val totalTime = (videoPlayer.duration.coerceAtLeast(0L) / 1000).toInt()

                if (totalTime == 0) {
                    -2 // 无法正常博凡
                } else if (currentTime >= totalTime - 1) {
                    -1 // 播放完后上报的时间应为 -1
                } else {
                    currentTime // 播放中上报当前时间
                }
            }
            if (time > -2) {
                onSendHeartbeat(time)
            }
        }
    }

    // updateBackToHistory() 中使用 videoPlayerHistoryData.lastPlayed 无法获取到新值
    LaunchedEffect(videoPlayerHistoryData.lastPlayed) {
        lastPlayed = videoPlayerHistoryData.lastPlayed.toLong()
    }

    LaunchedEffect(videoPlayerVideoInfoData.width, videoPlayerVideoInfoData.height) {
        val newAspectRatio =
            videoPlayerVideoInfoData.width / videoPlayerVideoInfoData.height.toFloat()
        defaultAspectRatio = newAspectRatio.takeIf { it > 0 } ?: (16 / 9f)
        updateVideoAspectRatio()
    }

    val updateBackToHistory: () -> Unit = {
        // 此处使用 videoPlayerHistoryData.lastPlayed 无法获取到新值
        //if (videoPlayerHistoryData.lastPlayed > 0 && hideBackToHistoryTimer == null) {
        if (lastPlayed > 0 && hideBackToHistoryTimer == null) {
            logger.info { "show showBackToHistory: ${videoPlayerHistoryData.lastPlayed}" }
            scope.launch(Dispatchers.Main) {
                showBackToHistory = true
                hideBackToHistoryTimer = countDownTimer(5000, 1000, "hideBackToHistoryTimer") {
                    scope.launch(Dispatchers.Main) {
                        showBackToHistory = false
                        hideBackToHistoryTimer = null
                        //playerViewModel.lastPlayed = 0
                        onClearBackToHistoryData()
                    }
                }
            }
        }
    }

    val videoPlayerListener = object : VideoPlayerListener {
        override fun onError(error: Exception) {
            logger.info { "onError: $error" }
            scope.launch(Dispatchers.Main) {
                isError = true
                exception = error.cause as Exception?
            }
        }

        override fun onReady() {
            logger.info { "onReady" }
            scope.launch(Dispatchers.Main) {
                isError = false
                exception = null
                initDanmakuConfig()
                updateVideoAspectRatio()

                //reset default play speed
                onPlaySpeedChange(currentPlaySpeed)
                logger.info { "Reset default play speed: $currentPlaySpeed" }
                videoPlayer.speed = currentPlaySpeed
                mDanmakuPlayer?.updatePlaySpeed(currentPlaySpeed)
            }
        }

        override fun onPlay() {
            logger.info { "onPlay" }
            mDanmakuPlayer?.start()
            scope.launch(Dispatchers.Main) {
                isPlaying = true
                isBuffering = false
                updateBackToHistory()
            }
        }

        override fun onPause() {
            logger.info { "onPause" }
            mDanmakuPlayer?.pause()
            scope.launch(Dispatchers.Main) {
                isPlaying = false
            }
        }

        override fun onBuffering() {
            logger.info { "onBuffering" }
            scope.launch(Dispatchers.Main) {
                isBuffering = true
            }
            mDanmakuPlayer?.pause()
        }

        override fun onEnd() {
            if (videoPlayerConfigData.showRelatedVideos) {
                logger.info { "onEnd: show related videos, skip auto next" }
                scope.launch(Dispatchers.Main) {
                    isPlaying = false
                }
                return
            }

            if (videoPlayerConfigData.isLoop) {
                logger.info { "onEnd: replay" }
                scope.launch(Dispatchers.Main) {
                    videoPlayer.seekTo(0)
                    mDanmakuPlayer?.seekTo(0)
                    mDanmakuPlayer?.pause()
                    videoPlayer.start()
                }
                return
            }

            logger.info { "onEnd" }
            mDanmakuPlayer?.pause()
            scope.launch(Dispatchers.Main) {
                isPlaying = false
                if (!videoPlayerConfigData.incognitoMode) sendHeartbeat()
                // 当控制信息面板显示时不自动播放下一集
                if (!showInfoProvider()) {
                    onLoadNextVideo()
                } else {
                    logger.info { "Skip auto next because info panel visible" }
                }
            }
        }

        override fun onIdle() {
            //TODO("Not yet implemented")
        }

        override fun onSeekBack(seekBackIncrementMs: Long) {
            mDanmakuPlayer?.seekTo(seekState.position)
            mDanmakuPlayer?.pause()
        }

        override fun onSeekForward(seekForwardIncrementMs: Long) {
            mDanmakuPlayer?.seekTo(seekState.position)
            mDanmakuPlayer?.pause()
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus(scope)
    }

    LaunchedEffect(videoPlayerConfigData.isLoop, videoPlayerConfigData.showDanmaku) {
        videoPlayer.setPlayerEventListener(videoPlayerListener)
    }

    LaunchedEffect(danmakuPlayer) {
        logger.debug { "update mDanmakuPlayer" }
        mDanmakuPlayer = danmakuPlayer
        danmakuLayerHandle.updateDanmakuPlayer(danmakuPlayer)
    }

    LaunchedEffect(videoPlayerLoadStateData.loadState) {
        when (videoPlayerLoadStateData.loadState) {
            RequestState.Ready -> {}
            RequestState.Doing -> {}
            RequestState.Done -> {}
            RequestState.Success -> {}
            RequestState.Failed -> {
                exception = Exception(videoPlayerLoadStateData.errorMessage)
                isError = true
            }
        }
    }

    LaunchedEffect(videoPlayer) {
        while (currentCoroutineContext().isActive) {
            val pos = videoPlayer.currentPosition.coerceAtLeast(0L)
            val dur = videoPlayer.duration.coerceAtLeast(0L)
            val buf = videoPlayer.bufferedPercentage
            if (seekState.position != pos) seekState.position = pos
            if (seekState.duration != dur) seekState.duration = dur
            if (seekState.bufferedPercentage != buf) seekState.bufferedPercentage = buf
            delay(200)
        }
    }

    DisposableEffect(Unit) {
        var updateSeekTimer: Timer? = null
        var resetTimer: ((Long) -> Unit)? = null

        val updateMask: suspend () -> Unit = {
            val currentPosition = seekState.position
            val danmakuMasks = videoPlayerDanmakuMaskData.danmakuMasks.firstOrNull {
                currentPosition in it.range
            }?.frames?.firstOrNull { currentPosition in it.range }
            withContext(Dispatchers.Main) { currentDanmakuMaskFrame = danmakuMasks }

            if (currentDanmakuMaskFrame != null) {
                resetTimer?.invoke(
                    max(currentDanmakuMaskFrame!!.range.last - currentPosition + 3, 20)
                )
            } else {
                resetTimer?.invoke(2000)
            }
        }

        val timerTask: () -> Unit = {
            val currentPosition = seekState.position
            scope.launch(Dispatchers.IO) {
                if (videoPlayerDanmakuMaskData.danmakuMasks.isNotEmpty()) {
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
                    withContext(Dispatchers.Main) { currentDanmakuMaskFrame = null }
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
        if (!videoPlayerConfigData.incognitoMode) {
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
            if (!videoPlayerConfigData.incognitoMode) {
                sendHeartbeat()
                sendHeartbeatTimer?.cancel()
            }
        }
    }

    LaunchedEffect(videoPlayerLogsData.logs) {
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
                if (clockState.hour != hour) clockState.hour = hour
                if (clockState.minute != minute) clockState.minute = minute
                if (clockState.second != second) clockState.second = second
            }
        )
        onDispose { clockRefreshTimer?.cancel() }
    }

    CompositionLocalProvider(
        LocalVideoPlayerSeekState provides seekState,
        LocalVideoPlayerClockState provides clockState,
        //LocalVideoPlayerHistoryData provides LocalVideoPlayerHistoryData.current.copy(
        //    showBackToHistory = showBackToHistory
        //),
        //LocalVideoPlayerHistoryData provides VideoPlayerHistoryData(
        //    lastPlayed = videoPlayerHistoryData.lastPlayed,
        //    showBackToHistory = showBackToHistory
        //),
        LocalVideoPlayerStateData provides VideoPlayerStateData(
            isPlaying = isPlaying,
            isBuffering = isBuffering,
            isError = isError,
            exception = exception,
            showBackToHistory = showBackToHistory
        ),
        LocalVideoPlayerDebugInfoData provides VideoPlayerDebugInfoData(
            debugInfo = videoPlayer.debugInfo
        ),
    ) {
        VideoPlayerController(
            modifier = modifier
                .focusRequester(focusRequester),
            videoPlayer = videoPlayer,
            playerSeekForwardStep = playerSeekForwardStep,
            playerSeekBackwardStep = playerSeekBackwardStep,
            showBottomProgressBar = showBottomProgressBar,
            showRelatedVideos = videoPlayerConfigData.showRelatedVideos,
            onToggleRelatedVideos = onToggleRelatedVideos,
            registerShowInfoProvider = { provider -> showInfoProvider = provider },

            onPlay = { videoPlayer.start() },
            onPause = {
                videoPlayer.pause()
                if (!videoPlayerConfigData.incognitoMode) sendHeartbeat()
            },
            onExit = {
                if (!videoPlayerConfigData.incognitoMode) sendHeartbeat()
                onExit()
            },
            onGoTime = {
                videoPlayer.seekTo(it)
                mDanmakuPlayer?.seekTo(it)
                // akdanmaku 会在跳转后立即播放，如果需要缓冲则会导致弹幕不同步
                mDanmakuPlayer?.pause()
            },
            onBackToHistory = {
                val time = videoPlayerHistoryData.lastPlayed.toLong()
                logger.fInfo { "Back to history: ${time.formatHourMinSec()}" }
                videoPlayer.seekTo(time)
                mDanmakuPlayer?.seekTo(time)
                // akdanmaku 会在跳转后立即播放，如果需要缓冲则会导致弹幕不同步
                mDanmakuPlayer?.pause()
                //playerViewModel.lastPlayed = 0
                onClearBackToHistoryData()
                showBackToHistory = false
                hideBackToHistoryTimer?.cancel()
                hideBackToHistoryTimer = null
            },
            onPlayNewVideo = {
                if (!videoPlayerConfigData.incognitoMode) sendHeartbeat()
                //playerViewModel.partTitle = it.title
                //playerViewModel.loadPlayUrl(
                //    avid = it.aid,
                //    cid = it.cid,
                //    epid = it.epid,
                //    seasonId = it.seasonId,
                //    continuePlayNext = true
                //)
                onLoadNewVideo(it)
            },
            onResolutionChange = { resolution ->
                videoPlayer.pause()
                val current = videoPlayer.currentPosition
                onResolutionChange(resolution) {
                    //scope.launch(Dispatchers.Default) {
                    //    playerViewModel.updateAvailableCodec()
                    //    playerViewModel.playQuality(qualityId)
                    withContext(Dispatchers.Main) {
                        videoPlayer.seekTo(current)
                        videoPlayer.start()
                    }
                    //}
                }
                //playerViewModel.currentQuality = qualityId
            },
            onCodecChange = { videoCodec ->
                videoPlayer.pause()
                val current = videoPlayer.currentPosition
                onCodecChange(videoCodec) {
                    withContext(Dispatchers.Main) {
                        videoPlayer.seekTo(current)
                        videoPlayer.start()
                    }
                }
            },
            onAspectRatioChange = { aspectRadio ->
                currentVideoAspectRatio = aspectRadio
                onAspectRatioChange(currentVideoAspectRatio)
                updateVideoAspectRatio()
            },
            onRotationChange = { rotation ->
//                if (videoPlayerConfigData.currentResolution > Resolution.R1080P60) {
//                    // 4k及以上的视频旋转后画面很卡、hdr、杜比世界的视频旋转后色彩和对比度不对， 所以先切换到<=R1080P60
//                    val tempList =
//                        videoPlayerConfigData.availableResolutions.sortedByDescending { it.code }
//                    val currentQuality = tempList.firstOrNull { it.code <= Resolution.R1080P60.code }
//                        ?: tempList.last()
//                    if (videoPlayerConfigData.currentResolution != currentQuality) {
//                        videoPlayer.pause()
//                        val current = videoPlayer.currentPosition
//                        onResolutionChange(currentQuality) {
//                            withContext(Dispatchers.Main) {
//                                videoPlayer.seekTo(current)
//                                videoPlayer.start()
//                            }
//                        }
//                    }
//                }

                currentVideoRotation = rotation
                onRotationChange(rotation)
            },
            onPlaySpeedChange = { speed ->
                logger.info { "Set default play speed: $speed" }
                currentPlaySpeed = speed
                onPlaySpeedChange(speed)
                videoPlayer.speed = speed
                mDanmakuPlayer?.updatePlaySpeed(speed)
            },
            onAudioChange = { audio ->
                videoPlayer.pause()
                val current = videoPlayer.currentPosition
                onAudioChange(audio) {
                    withContext(Dispatchers.Main) {
                        videoPlayer.seekTo(current)
                        videoPlayer.start()
                    }
                }
            },
            onDanmakuSwitchChange = { enabledDanmakuTypes ->
                logger.info { "On enabled danmaku type change: $enabledDanmakuTypes" }
                onDanmakuSwitchChange(enabledDanmakuTypes)
                updateDanmakuConfigTypeFilter()
            },
            onDanmakuSizeChange = { scale ->
                logger.info { "On danmaku scale change: $scale" }
                onDanmakuSizeChange(scale)
                updateDanmakuConfig()
            },
            onDanmakuOpacityChange = { opacity ->
                logger.info { "On danmaku opacity change: $opacity" }
                onDanmakuOpacityChange(opacity)
            },
            onDanmakuAreaChange = { area ->
                logger.info { "On danmaku area change: $area" }
                onDanmakuAreaChange(area)
            },
            onDanmakuMaskChange = { mask ->
                logger.info { "On danmaku mask change: $mask" }
                onDanmakuMaskChange(mask)
            },
            onSubtitleChange = { subtitle ->
                onSubtitleChange(subtitle)
            },
            onSubtitleSizeChange = { size ->
                logger.info { "On subtitle font size change: $size" }
                onSubtitleSizeChange(size)
            },
            onSubtitleBackgroundOpacityChange = { opacity ->
                logger.info { "On subtitle background opacity change: $opacity" }
                onSubtitleBackgroundOpacityChange(opacity)
            },
            onSubtitleBottomPadding = { padding ->
                logger.info { "On subtitle bottom padding change: $padding" }
                onSubtitleBottomPadding(padding)
            },
            onRequestFocus = { focusRequester.requestFocus(scope) },
            onOpenUpSpace = onOpenUpSpace,
            onRefreshVideo = onRefreshVideo,
            onOpenDanmaku = {
                onShowDanmakuChange(true)
                videoPlayerConfigData.showDanmaku = true
                danmakuConfig = danmakuConfig.copy(visibility = true)
                danmakuConfig.updateVisibility()
                logger.info { "Update danmaku config: $danmakuConfig" }
                mDanmakuPlayer?.updateConfig(danmakuConfig)
            },
            onHideDanmaku = {
                onShowDanmakuChange(false)
                videoPlayerConfigData.showDanmaku = false
                danmakuConfig = danmakuConfig.copy(visibility = false)
                danmakuConfig.updateVisibility()
                logger.info { "Update danmaku config: $danmakuConfig" }
                mDanmakuPlayer?.updateConfig(danmakuConfig)
            },
            onLoopPlayModeChange = {
                videoPlayerConfigData.isLoop = it
                onLoopPlayModeChange(it)
            },
            userActionContent = userActionContent
        ) {
            LaunchedEffect(Unit) {
                videoPlayer.setOptions()
            }

            // 将弹幕层副作用独立到子树，保证父级其它状态变化不导致 handle 以外的重组
            DanmakuLayerSideEffects(
                danmakuLayerHandle = danmakuLayerHandle,
                area = videoPlayerConfigData.currentDanmakuArea,
                opacity = videoPlayerConfigData.currentDanmakuOpacity,
                visible = videoPlayerConfigData.showDanmaku,
                maskFrame = currentDanmakuMaskFrame.takeIf { videoPlayerConfigData.currentDanmakuMask }
            )

            BvVideoPlayer(
                modifier = Modifier
//                    .fillMaxHeight()
                    .aspectRatio(aspectRatioValue)
                    .align(Alignment.Center),
                videoPlayer = videoPlayer,
                playerListener = videoPlayerListener,
                rotationDegrees = currentVideoRotation.degrees,
                danmakuPlayer = danmakuPlayer,
                forceUseTextureView = useTextureViewFixPortraitVideo
            )

            DanmakuLayer(
                modifier = Modifier.align(Alignment.TopCenter),
                handle = danmakuLayerHandle
            )

            if (showLogs) {
                Column(
                    modifier = Modifier.align(Alignment.BottomStart)
                ) {
                    Text(text = videoPlayerLogsData.logs)
                }
            }
        }
    }
}

// 同步弹幕层 UI 相关的独立副作用（区域/透明度/蒙版/可见性）
@Composable
private fun DanmakuLayerSideEffects(
    danmakuLayerHandle: DanmakuLayerHandle,
    area: Float,
    opacity: Float,
    visible: Boolean,
    maskFrame: DanmakuMaskFrame?,
) {
    LaunchedEffect(area, opacity, visible, maskFrame) {
        danmakuLayerHandle.update(
            area = area,
            opacity = opacity,
            mask = maskFrame,
            visible = visible,
        )
    }
}
