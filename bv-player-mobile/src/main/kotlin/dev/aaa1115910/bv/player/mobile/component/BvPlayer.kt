package dev.aaa1115910.bv.player.mobile.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.media3.exoplayer.ExoPlayer
import dev.aaa1115910.bv.player.mobile.component.controller.BvPlayerController

@Composable
fun BvPlayer(
    modifier: Modifier = Modifier,
    isLandscape: Boolean,
    onEnterFullScreen: () -> Unit,
    onExitFullScreen: () -> Unit,
    videoPlayer: ExoPlayer
) {
    BvPlayerController(
        modifier = modifier,
        isLandscape = isLandscape,
        onEnterFullScreen = onEnterFullScreen,
        onExitFullScreen = onExitFullScreen
    ) {
        BvVideoPlayer(videoPlayer = videoPlayer)
    }
}