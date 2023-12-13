package dev.aaa1115910.bv.player.mobile.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
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
    videoPlayer: ExoPlayer,
    danmakuPlayer: DanmakuPlayer
) {
    val mobileVideoPlayerData = LocalMobileVideoPlayerData.current
    var isPlaying by rememberSaveable { mutableStateOf(false) }

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
        onEnterFullScreen = onEnterFullScreen,
        onExitFullScreen = onExitFullScreen,
        onBack = onBack,
        onPlay = { videoPlayer.play() },
        onPause = { videoPlayer.pause() },
        onSeekToPosition = videoPlayer::seekTo,
        onChangeResolution = onChangeResolution,
        onChangeSpeed = videoPlayer::setPlaybackSpeed
    ) {
        Media3VideoPlayer(videoPlayer = videoPlayer)
        AkDanmakuPlayer(
            danmakuPlayer = danmakuPlayer
        )
    }
}