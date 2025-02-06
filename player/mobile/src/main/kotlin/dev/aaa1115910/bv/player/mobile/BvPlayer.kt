package dev.aaa1115910.bv.player.mobile

import android.os.CountDownTimer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.kuaishou.akdanmaku.DanmakuConfig
import com.kuaishou.akdanmaku.data.DanmakuItemData
import com.kuaishou.akdanmaku.ecs.component.filter.TypeFilter
import com.kuaishou.akdanmaku.ext.RETAINER_BILIBILI
import com.kuaishou.akdanmaku.ui.DanmakuPlayer
import dev.aaa1115910.biliapi.entity.danmaku.DanmakuMaskFrame
import dev.aaa1115910.bv.player.AbstractVideoPlayer
import dev.aaa1115910.bv.player.AkDanmakuPlayer
import dev.aaa1115910.bv.player.BvVideoPlayer
import dev.aaa1115910.bv.player.VideoPlayerListener
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
import dev.aaa1115910.bv.player.entity.VideoAspectRatio
import dev.aaa1115910.bv.player.entity.VideoPlayerClockData
import dev.aaa1115910.bv.player.entity.VideoPlayerDebugInfoData
import dev.aaa1115910.bv.player.entity.VideoPlayerSeekData
import dev.aaa1115910.bv.player.entity.VideoPlayerStateData
import dev.aaa1115910.bv.player.mobile.controller.BvPlayerController
import dev.aaa1115910.bv.util.countDownTimer
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.delay
import java.util.Calendar

@Composable
fun BvPlayer(
    modifier: Modifier = Modifier,
    isFullScreen: Boolean,
    onEnterFullScreen: () -> Unit,
    onExitFullScreen: () -> Unit,
    onBack: () -> Unit,
    onClearBackToHistoryData: () -> Unit,
    onChangeResolution: (Int) -> Unit,
    onToggleDanmaku: (Boolean) -> Unit,
    onEnabledDanmakuTypesChange: (List<DanmakuType>) -> Unit,
    onDanmakuOpacityChange: (Float) -> Unit,
    onDanmakuScaleChange: (Float) -> Unit,
    onDanmakuAreaChange: (Float) -> Unit,
    videoPlayer: AbstractVideoPlayer,
    danmakuPlayer: DanmakuPlayer?
) {
    val logger = KotlinLogging.logger("BvPlayer")
    // 直接调用 danmakuPlayer 会始终为 null
    var mDanmakuPlayer: DanmakuPlayer? by remember { mutableStateOf(null) }

    val videoPlayerConfigData = LocalVideoPlayerConfigData.current
    val videoPlayerDanmakuMaskData = LocalVideoPlayerDanmakuMasksData.current
    val videoPlayerHistoryData = LocalVideoPlayerHistoryData.current
    val videoPlayerLoadStateData = LocalVideoPlayerLoadStateData.current
    val videoPlayerLogsData = LocalVideoPlayerLogsData.current
    val videoPlayerVideoInfoData = LocalVideoPlayerVideoInfoData.current

    var showLogs by remember { mutableStateOf(false) }
    var showBackToHistory by remember { mutableStateOf(false) }
    var isPlaying by rememberSaveable { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    var isBuffering by remember { mutableStateOf(false) }
    var exception by remember { mutableStateOf<Exception?>(null) }

    val typeFilter by remember { mutableStateOf(TypeFilter()) }
    var danmakuConfig by remember { mutableStateOf(DanmakuConfig()) }

    var duration by remember { mutableLongStateOf(0L) }
    var bufferedPercentage by remember { mutableStateOf(0) }
    var currentVideoAspectRatio by remember { mutableStateOf(VideoAspectRatio.Default) }
    var currentPosition by remember { mutableLongStateOf(0L) }
    //var currentPlaySpeed by remember { mutableFloatStateOf(Prefs.defaultPlaySpeed) }
    var aspectRatio by remember { mutableFloatStateOf(16f / 9f) }
    var lastPlayed by remember { mutableLongStateOf(0L) }

    var clock: Triple<Int, Int, Int> by remember { mutableStateOf(Triple(0, 0, 0)) }

    var hideLogsTimer: CountDownTimer? by remember { mutableStateOf(null) }
    var clockRefreshTimer: CountDownTimer? by remember { mutableStateOf(null) }
    var hideBackToHistoryTimer: CountDownTimer? by remember { mutableStateOf(null) }

    var currentDanmakuMaskFrame: DanmakuMaskFrame? by remember { mutableStateOf(null) }


    val updatePosition = {
        currentPosition = videoPlayer.currentPosition
        duration = videoPlayer.duration
        bufferedPercentage = videoPlayer.bufferedPercentage
    }

    val updateEnabledDanmakuTypeFilter: (List<DanmakuType>) -> Unit = { danmakuTypes ->
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
    }


    val initDanmakuConfig: () -> Unit = {
        updateEnabledDanmakuTypeFilter(videoPlayerConfigData.currentDanmakuEnabledList)
        danmakuConfig = danmakuConfig.copy(
            retainerPolicy = RETAINER_BILIBILI,
            textSizeScale = videoPlayerConfigData.currentDanmakuScale,
            dataFilter = listOf(typeFilter)
        )
        danmakuConfig.updateFilter()
        mDanmakuPlayer?.updateConfig(danmakuConfig)
    }

    val updateDanmakuConfigTypeFilter: () -> Unit = {
        updateEnabledDanmakuTypeFilter(videoPlayerConfigData.currentDanmakuEnabledList)
        danmakuConfig.updateFilter()
        mDanmakuPlayer?.updateConfig(danmakuConfig)
    }

    val toggleDanmakuEnabled: (Boolean) -> Unit = { enabled ->
        updateEnabledDanmakuTypeFilter(if (enabled) videoPlayerConfigData.currentDanmakuEnabledList else listOf())
        danmakuConfig.updateFilter()
        mDanmakuPlayer?.updateConfig(danmakuConfig)
    }

    val updateDanmakuConfig: () -> Unit = {
        danmakuConfig = danmakuConfig.copy(
            retainerPolicy = RETAINER_BILIBILI,
            textSizeScale = videoPlayerConfigData.currentDanmakuScale,
        )
        mDanmakuPlayer?.updateConfig(danmakuConfig)
    }

    val updateVideoAspectRatio: () -> Unit = {
        val aspectRatioValue = videoPlayer.videoWidth / videoPlayer.videoHeight.toFloat()
        aspectRatio = if (aspectRatioValue > 0) aspectRatioValue else 16 / 9f
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
            println("onError: $error")
            isError = true
            exception = error.cause as Exception?
        }

        override fun onReady() {
            logger.info { "onReady" }
            isError = false
            exception = null
            initDanmakuConfig()

            updateVideoAspectRatio()
            videoPlayer.start()

            //reset default play speed
            //logger.info { "Reset default play speed: $currentPlaySpeed" }
            //videoPlayer.speed = currentPlaySpeed
            //playerViewModel.danmakuPlayer?.updatePlaySpeed(currentPlaySpeed)
        }

        override fun onPlay() {
            logger.info { "onPlay" }
            mDanmakuPlayer?.start()
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
            //if (!Prefs.incognitoMode) sendHeartbeat()

            //onLoadNextVideo()
        }

        override fun onIdle() {
            logger.info { "onIdle" }
            mDanmakuPlayer?.pause()
        }

        override fun onSeekBack(seekBackIncrementMs: Long) {
            mDanmakuPlayer?.seekTo(currentPosition)
        }

        override fun onSeekForward(seekForwardIncrementMs: Long) {
            mDanmakuPlayer?.seekTo(currentPosition)
        }

    }

    LaunchedEffect(Unit) {
        while (true) {
            updatePosition()
            delay(200)
        }
    }

    LaunchedEffect(danmakuPlayer) {
        mDanmakuPlayer = danmakuPlayer
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
        BvPlayerController(
            modifier = modifier,
            isFullScreen = isFullScreen,
            onEnterFullScreen = onEnterFullScreen,
            onExitFullScreen = onExitFullScreen,
            onBack = onBack,
            onPlay = { videoPlayer.start() },
            onPause = { videoPlayer.pause() },
            onSeekToPosition = { position ->
                mDanmakuPlayer?.seekTo(position)
                mDanmakuPlayer?.pause()
                videoPlayer.seekTo(position)
            },
            onChangeResolution = onChangeResolution,
            onChangeSpeed = { videoPlayer.speed = it },
            onToggleDanmaku = {
                //toggleDanmakuEnabled(videoPlayerConfigData.currentDanmakuEnabled)
                onToggleDanmaku(videoPlayerConfigData.currentDanmakuEnabled)
            },
            onEnabledDanmakuTypesChange = { enabledDanmakuTypes ->
                onEnabledDanmakuTypesChange(enabledDanmakuTypes)
                updateDanmakuConfigTypeFilter()
            },
            onDanmakuOpacityChange = onDanmakuOpacityChange,
            onDanmakuScaleChange = { scale ->
                onDanmakuScaleChange(scale)
                updateDanmakuConfig()
            },
            onDanmakuAreaChange = onDanmakuAreaChange
        ) {
            BvVideoPlayer(
                modifier = Modifier
                    .aspectRatio(aspectRatio)
                    .align(Alignment.Center),
                videoPlayer = videoPlayer, playerListener = videoPlayerListener
            )
            if (videoPlayerConfigData.currentDanmakuEnabled) {
                AkDanmakuPlayer(
                    modifier = Modifier
                        .alpha(videoPlayerConfigData.currentDanmakuOpacity)
                        .fillMaxHeight(videoPlayerConfigData.currentDanmakuArea),
                    danmakuPlayer = mDanmakuPlayer
                )
            }
        }
    }
}
