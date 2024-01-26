package dev.aaa1115910.bv.player.mobile.component

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import dev.aaa1115910.bv.player.AbstractVideoPlayer
import dev.aaa1115910.bv.player.BvVideoPlayer
import dev.aaa1115910.bv.player.VideoPlayerListener
import dev.aaa1115910.bv.player.mobile.component.controller.BvPlayerController
import dev.aaa1115910.bv.player.mobile.util.LocalMobileVideoPlayerData
import kotlinx.coroutines.delay

@Composable
fun BvPlayer(
    modifier: Modifier = Modifier,
    isFullScreen: Boolean,
    onEnterFullScreen: () -> Unit,
    onExitFullScreen: () -> Unit,
    onBack: () -> Unit,
    onChangeResolution: (Int) -> Unit,
    onToggleDanmaku: (Boolean) -> Unit,
    onEnabledDanmakuTypesChange: (List<DanmakuType>) -> Unit,
    onDanmakuOpacityChange: (Float) -> Unit,
    onDanmakuScaleChange: (Float) -> Unit,
    onDanmakuAreaChange: (Float) -> Unit,
    videoPlayer: AbstractVideoPlayer,
    danmakuPlayer: DanmakuPlayer?
) {
    val mobileVideoPlayerData = LocalMobileVideoPlayerData.current
    var isPlaying by rememberSaveable { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    var isBuffering by remember { mutableStateOf(false) }
    var exception by remember { mutableStateOf<Exception?>(null) }

    var enabledDanmaku by rememberSaveable { mutableStateOf(mobileVideoPlayerData.enabledDanmaku) }
    val typeFilter by remember { mutableStateOf(TypeFilter()) }
    var danmakuConfig by remember { mutableStateOf(DanmakuConfig()) }

    var currentTime by remember { mutableStateOf(0L) }
    var totalTime by remember { mutableStateOf(0L) }
    var currentSeekPosition by remember { mutableStateOf(0f) }
    var bufferedSeekPosition by remember { mutableStateOf(0f) }

    // 直接调用 danmakuPlayer 会始终为 null
    var mDanmakuPlayer: DanmakuPlayer? by remember { mutableStateOf(null) }

    var aspectRatio by remember { mutableStateOf(16 / 9f) }

    val updatePosition = {
        currentTime = videoPlayer.currentPosition
        totalTime = videoPlayer.duration
        currentSeekPosition = videoPlayer.currentPosition / videoPlayer.duration.toFloat()
        bufferedSeekPosition = videoPlayer.bufferedPercentage / 100f
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
        updateEnabledDanmakuTypeFilter(mobileVideoPlayerData.currentDanmakuTypes)
        danmakuConfig = danmakuConfig.copy(
            retainerPolicy = RETAINER_BILIBILI,
            textSizeScale = mobileVideoPlayerData.currentDanmakuScale,
            dataFilter = listOf(typeFilter)
        )
        danmakuConfig.updateFilter()
        mDanmakuPlayer?.updateConfig(danmakuConfig)
    }

    val updateDanmakuConfigTypeFilter: () -> Unit = {
        updateEnabledDanmakuTypeFilter(mobileVideoPlayerData.currentDanmakuTypes)
        danmakuConfig.updateFilter()
        mDanmakuPlayer?.updateConfig(danmakuConfig)
    }

    val toggleDanmakuEnabled: (Boolean) -> Unit = { enabled ->
        updateEnabledDanmakuTypeFilter(if (enabled) mobileVideoPlayerData.currentDanmakuTypes else listOf())
        danmakuConfig.updateFilter()
        mDanmakuPlayer?.updateConfig(danmakuConfig)
    }

    val updateDanmakuConfig: () -> Unit = {
        danmakuConfig = danmakuConfig.copy(
            retainerPolicy = RETAINER_BILIBILI,
            textSizeScale = mobileVideoPlayerData.currentDanmakuScale,
        )
        mDanmakuPlayer?.updateConfig(danmakuConfig)
    }

    val updateVideoAspectRatio: () -> Unit = {
        val aspectRatioValue = videoPlayer.videoWidth / videoPlayer.videoHeight.toFloat()
        aspectRatio = if (aspectRatioValue > 0) aspectRatioValue else 16 / 9f
    }

    val videoPlayerListener = object : VideoPlayerListener {
        override fun onError(error: Exception) {
            println("onError: $error")
            isError = true
            exception = error.cause as Exception?
        }

        override fun onReady() {
            println("onReady")
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
            println("onPlay")
            //logger.info { "onPlay" }
            println("start danmaku player, ${danmakuPlayer != null}")
            mDanmakuPlayer?.start()
            isPlaying = true
            isBuffering = false

            //if (playerViewModel.lastPlayed > 0 && hideBackToHistoryTimer == null) {
            //    showBackToHistory = true
            //    hideBackToHistoryTimer = countDownTimer(5000, 1000, "hideBackToHistoryTimer") {
            //        showBackToHistory = false
            //        hideBackToHistoryTimer = null
            //        playerViewModel.lastPlayed = 0
            //    }
            //}
        }

        override fun onPause() {
            println("onPause")
            println("pause danmaku player 1")
            mDanmakuPlayer?.pause()
            isPlaying = false
        }

        override fun onBuffering() {
            println("onBuffering")
            isBuffering = true
            println("pause danmaku player 2")
            mDanmakuPlayer?.pause()
        }

        override fun onEnd() {
            println("onEnd")
            println("pause danmaku player 3")
            mDanmakuPlayer?.pause()
            isPlaying = false
            //if (!Prefs.incognitoMode) sendHeartbeat()

            //val videoListIndex = playerViewModel.availableVideoList.indexOfFirst {
            //    it.cid == playerViewModel.currentCid
            //}
            //if (videoListIndex + 1 < playerViewModel.availableVideoList.size) {
            //    val nextVideo = playerViewModel.availableVideoList[videoListIndex + 1]
            //    logger.info { "Play next video: $nextVideo" }
            //    playerViewModel.partTitle = nextVideo.title
            //    playerViewModel.loadPlayUrl(
            //        avid = nextVideo.aid,
            //        cid = nextVideo.cid,
            //        epid = nextVideo.epid,
            //        seasonId = nextVideo.seasonId,
            //        continuePlayNext = true
            //    )
            //}
        }

        override fun onIdle() {
            println("onIdle")
            println("pause danmaku player 4")
            mDanmakuPlayer?.pause()
        }

        override fun onSeekBack(seekBackIncrementMs: Long) {
            mDanmakuPlayer?.seekTo(currentTime)
        }

        override fun onSeekForward(seekForwardIncrementMs: Long) {
            mDanmakuPlayer?.seekTo(currentTime)
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

    BvPlayerController(
        modifier = modifier,
        isPlaying = isPlaying,
        isFullScreen = isFullScreen,
        currentTime = currentTime,
        totalTime = totalTime,
        currentSeekPosition = currentSeekPosition,
        bufferedSeekPosition = bufferedSeekPosition,
        currentResolutionCode = mobileVideoPlayerData.currentResolutionCode,
        currentSpeed = mobileVideoPlayerData.currentSpeed,
        availableResolutionMap = mobileVideoPlayerData.availableResolutionMap,
        enabledDanmaku = enabledDanmaku,
        enabledDanmakuTypes = mobileVideoPlayerData.currentDanmakuTypes,
        danmakuOpacity = mobileVideoPlayerData.currentDanmakuOpacity,
        danmakuScale = mobileVideoPlayerData.currentDanmakuScale,
        danmakuArea = mobileVideoPlayerData.currentDanmakuArea,
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
            enabledDanmaku = !enabledDanmaku
            toggleDanmakuEnabled(enabledDanmaku)
            onToggleDanmaku(enabledDanmaku)
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
        //Media3VideoPlayer(videoPlayer = videoPlayer)
        BvVideoPlayer(
            modifier = Modifier
                .aspectRatio(aspectRatio)
                .align(Alignment.Center),
            videoPlayer = videoPlayer, playerListener = videoPlayerListener
        )
        AkDanmakuPlayer(
            modifier = Modifier
                .alpha(mobileVideoPlayerData.currentDanmakuOpacity)
                .fillMaxHeight(mobileVideoPlayerData.currentDanmakuArea),
            danmakuPlayer = mDanmakuPlayer
        )
    }
}

enum class DanmakuType {
    All, Top, Rolling, Bottom;
}