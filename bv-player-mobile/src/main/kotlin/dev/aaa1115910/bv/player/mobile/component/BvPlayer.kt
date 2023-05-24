package dev.aaa1115910.bv.player.mobile.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.media3.exoplayer.ExoPlayer
import dev.aaa1115910.bv.player.mobile.component.controller.BvPlayerController
import kotlinx.coroutines.delay

@Composable
fun BvPlayer(
    modifier: Modifier = Modifier,
    isLandscape: Boolean,
    onEnterFullScreen: () -> Unit,
    onExitFullScreen: () -> Unit,
    videoPlayer: ExoPlayer
) {
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

    BvPlayerController(
        modifier = modifier,
        isLandscape = isLandscape,
        currentTime = currentTime,
        totalTime = totalTime,
        currentSeekPosition = currentSeekPosition,
        bufferedSeekPosition = bufferedSeekPosition,
        onEnterFullScreen = onEnterFullScreen,
        onExitFullScreen = onExitFullScreen,
        onSeekToPosition = videoPlayer::seekTo,
        onSeekMove = { videoPlayer.seekTo(videoPlayer.currentPosition + it) }
    ) {
        BvVideoPlayer(videoPlayer = videoPlayer)
    }
}