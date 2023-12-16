package dev.aaa1115910.bv.player.mobile.component

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.kuaishou.akdanmaku.DanmakuConfig
import com.kuaishou.akdanmaku.data.DanmakuItemData
import com.kuaishou.akdanmaku.ecs.component.filter.TypeFilter
import com.kuaishou.akdanmaku.ext.RETAINER_BILIBILI
import com.kuaishou.akdanmaku.ui.DanmakuPlayer
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
    videoPlayer: ExoPlayer,
    danmakuPlayer: DanmakuPlayer
) {
    val mobileVideoPlayerData = LocalMobileVideoPlayerData.current
    var isPlaying by rememberSaveable { mutableStateOf(false) }

    var enabledDanmaku by rememberSaveable { mutableStateOf(mobileVideoPlayerData.enabledDanmaku) }
    val typeFilter by remember { mutableStateOf(TypeFilter()) }
    var danmakuConfig by remember { mutableStateOf(DanmakuConfig()) }

    var currentTime by remember { mutableStateOf(0L) }
    var totalTime by remember { mutableStateOf(0L) }
    var currentSeekPosition by remember { mutableStateOf(0f) }
    var bufferedSeekPosition by remember { mutableStateOf(0f) }

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
        danmakuPlayer.updateConfig(danmakuConfig)
    }

    val updateDanmakuConfigTypeFilter: () -> Unit = {
        updateEnabledDanmakuTypeFilter(mobileVideoPlayerData.currentDanmakuTypes)
        danmakuConfig.updateFilter()
        danmakuPlayer.updateConfig(danmakuConfig)
    }

    val toggleDanmakuEnabled: (Boolean) -> Unit = { enabled ->
        updateEnabledDanmakuTypeFilter(if (enabled) mobileVideoPlayerData.currentDanmakuTypes else listOf())
        danmakuConfig.updateFilter()
        danmakuPlayer.updateConfig(danmakuConfig)
    }

    val updateDanmakuConfig: () -> Unit = {
        danmakuConfig = danmakuConfig.copy(
            retainerPolicy = RETAINER_BILIBILI,
            textSizeScale = mobileVideoPlayerData.currentDanmakuScale,
        )
        danmakuPlayer.updateConfig(danmakuConfig)
    }

    LaunchedEffect(Unit) {
        while (true) {
            updatePosition()
            delay(200)
        }
    }

    SideEffect {
        videoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
                if (isPlaying) {
                    danmakuPlayer.start()
                } else {
                    danmakuPlayer.pause()
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_READY -> {
                        initDanmakuConfig()
                        danmakuPlayer.seekTo(videoPlayer.currentPosition)
                        if (!isPlaying) danmakuPlayer.pause()
                    }

                    Player.STATE_ENDED -> danmakuPlayer.pause()
                    Player.STATE_IDLE -> {}
                    Player.STATE_BUFFERING -> danmakuPlayer.pause()
                    else -> danmakuPlayer.pause()
                }
            }

            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                if (playWhenReady) {
                    danmakuPlayer.start()
                } else {
                    danmakuPlayer.pause()
                }
            }
        })
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
        onPlay = { videoPlayer.play() },
        onPause = { videoPlayer.pause() },
        onSeekToPosition = videoPlayer::seekTo,
        onChangeResolution = onChangeResolution,
        onChangeSpeed = videoPlayer::setPlaybackSpeed,
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
        Media3VideoPlayer(videoPlayer = videoPlayer)
        AkDanmakuPlayer(
            modifier = Modifier
                .alpha(mobileVideoPlayerData.currentDanmakuOpacity)
                .fillMaxHeight(mobileVideoPlayerData.currentDanmakuArea),
            danmakuPlayer = danmakuPlayer
        )
    }
}

enum class DanmakuType {
    All, Top, Rolling, Bottom;
}