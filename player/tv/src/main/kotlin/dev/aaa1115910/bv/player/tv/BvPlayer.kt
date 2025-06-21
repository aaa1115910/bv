package dev.aaa1115910.bv.player.tv

import android.os.CountDownTimer
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import dev.aaa1115910.bv.player.AkDanmakuPlayer
import dev.aaa1115910.bv.player.BvVideoPlayer
import dev.aaa1115910.bv.player.VideoPlayerListener
import dev.aaa1115910.bv.player.entity.Audio
import dev.aaa1115910.bv.player.entity.DanmakuType
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerClockData
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerConfigData
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerDanmakuMasksData
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerDebugInfoData
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerHistoryData
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerLoadStateData
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerLogsData
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerSeekData
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerStateData
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerVideoInfoData
import dev.aaa1115910.bv.player.entity.RequestState
import dev.aaa1115910.bv.player.entity.Resolution
import dev.aaa1115910.bv.player.entity.VideoAspectRatio
import dev.aaa1115910.bv.player.entity.VideoCodec
import dev.aaa1115910.bv.player.entity.VideoListItem
import dev.aaa1115910.bv.player.entity.VideoPlayerClockData
import dev.aaa1115910.bv.player.entity.VideoPlayerDebugInfoData
import dev.aaa1115910.bv.player.entity.VideoPlayerSeekData
import dev.aaa1115910.bv.player.entity.VideoPlayerStateData
import dev.aaa1115910.bv.player.tv.controller.VideoPlayerController
import dev.aaa1115910.bv.player.util.danmakuMask
import dev.aaa1115910.bv.util.countDownTimer
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.formatHourMinSec
import dev.aaa1115910.bv.util.ifElse
import dev.aaa1115910.bv.util.timeTask
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
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
    onSendHeartbeat: suspend (Int) -> Unit,
    onClearBackToHistoryData: () -> Unit,
    onLoadNextVideo: () -> Unit,
    onExit: () -> Unit,
    onLoadNewVideo: (VideoListItem) -> Unit,
    onResolutionChange: (Resolution, afterChange: suspend () -> Unit) -> Unit,
    onCodecChange: (VideoCodec, afterChange: suspend () -> Unit) -> Unit,
    onAspectRatioChange: (VideoAspectRatio) -> Unit,
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
) {
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

    var duration by remember { mutableLongStateOf(0L) }
    var bufferedPercentage by remember { mutableStateOf(0) }
    var currentVideoAspectRatio by remember { mutableStateOf(VideoAspectRatio.Default) }
    var currentPosition by remember { mutableLongStateOf(0L) }
    //var currentPlaySpeed by remember { mutableFloatStateOf(Prefs.defaultPlaySpeed) }
    var aspectRatioValue by remember { mutableFloatStateOf(16f / 9f) }
    val aspectRatio by animateFloatAsState(
        targetValue = aspectRatioValue,
        animationSpec = spring(
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "aspectRatio",
    )
    var lastPlayed by remember { mutableLongStateOf(0L) }
    var defaultAspectRatio by remember { mutableFloatStateOf(16 / 9f) }

    var clock: Triple<Int, Int, Int> by remember { mutableStateOf(Triple(0, 0, 0)) }

    var hideLogsTimer: CountDownTimer? by remember { mutableStateOf(null) }
    var clockRefreshTimer: CountDownTimer? by remember { mutableStateOf(null) }
    var hideBackToHistoryTimer: CountDownTimer? by remember { mutableStateOf(null) }

    var currentDanmakuMaskFrame: DanmakuMaskFrame? by remember { mutableStateOf(null) }

    val updateSeek = {
        currentPosition = videoPlayer.currentPosition.coerceAtLeast(0L)
        duration = videoPlayer.duration.coerceAtLeast(0L)
        bufferedPercentage = videoPlayer.bufferedPercentage
    }

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
            dataFilter = listOf(typeFilter)
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
                //播放完后上报的时间应为 -1
                if (currentTime >= totalTime) -1 else currentTime
            }
            onSendHeartbeat(time)
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
            showBackToHistory = true
            hideBackToHistoryTimer = countDownTimer(5000, 1000, "hideBackToHistoryTimer") {
                showBackToHistory = false
                hideBackToHistoryTimer = null
                //playerViewModel.lastPlayed = 0
                onClearBackToHistoryData()
            }
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
            logger.info { "Reset default play speed: ${videoPlayerConfigData.currentVideoSpeed}" }
            videoPlayer.speed = videoPlayerConfigData.currentVideoSpeed
            mDanmakuPlayer?.updatePlaySpeed(videoPlayerConfigData.currentVideoSpeed)
        }

        override fun onPlay() {
            logger.info { "onPlay" }
            mDanmakuPlayer?.start()
            logger.info { "danmakuplayer null? ${danmakuPlayer == null}" }
            isPlaying = true
            isBuffering = false
            updateBackToHistory()
        }

        override fun onPause() {
            logger.info { "onPause" }
            mDanmakuPlayer?.pause()
            isPlaying = false
        }

        override fun onBuffering() {
            logger.info { "onBuffering" }
            isBuffering = true
            mDanmakuPlayer?.pause()
        }

        override fun onEnd() {
            logger.info { "onEnd" }
            mDanmakuPlayer?.pause()
            isPlaying = false
            if (!videoPlayerConfigData.incognitoMode) sendHeartbeat()

            onLoadNextVideo()
        }

        override fun onIdle() {
            //TODO("Not yet implemented")
        }

        override fun onSeekBack(seekBackIncrementMs: Long) {
            mDanmakuPlayer?.seekTo(currentPosition)
        }

        override fun onSeekForward(seekForwardIncrementMs: Long) {
            mDanmakuPlayer?.seekTo(currentPosition)
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(danmakuPlayer) {
        logger.debug { "update mDanmakuPlayer" }
        mDanmakuPlayer = danmakuPlayer
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

        val updateMask: suspend () -> Unit = {
            val currentPosition = currentPosition
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
            val currentPosition = currentPosition
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
                clock = Triple(hour, minute, second)
            }
        )
        onDispose {
            clockRefreshTimer?.cancel()
        }
    }

    CompositionLocalProvider(
        LocalVideoPlayerSeekData provides VideoPlayerSeekData(
            duration = duration,
            position = currentPosition,
            bufferedPercentage = bufferedPercentage
        ),
        LocalVideoPlayerClockData provides VideoPlayerClockData(
            hour = clock.first,
            minute = clock.second,
            second = clock.third
        ),
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
            onPlaySpeedChange = { speed ->
                logger.info { "Set default play speed: $speed" }
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
            onRequestFocus = { focusRequester.requestFocus() },
        ) {
            LaunchedEffect(Unit) {
                videoPlayer.setOptions()
            }

            BvVideoPlayer(
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(aspectRatio)
                    .align(Alignment.Center),
                videoPlayer = videoPlayer,
                playerListener = videoPlayerListener,
            )

            AkDanmakuPlayer(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .fillMaxHeight(videoPlayerConfigData.currentDanmakuArea)
                    .fillMaxHeight()
                    .alpha(videoPlayerConfigData.currentDanmakuOpacity)
                    .ifElse(
                        { videoPlayerConfigData.currentDanmakuMask },
                        Modifier.danmakuMask(currentDanmakuMaskFrame)
                    ),
                danmakuPlayer = mDanmakuPlayer
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
