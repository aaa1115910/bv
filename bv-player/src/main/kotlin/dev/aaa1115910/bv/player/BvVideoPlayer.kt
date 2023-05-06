package dev.aaa1115910.bv.player

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import dev.aaa1115910.bv.player.impl.exo.ExoMediaPlayer
import dev.aaa1115910.bv.player.impl.vlc.VlcMediaPlayer
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.util.VLCVideoLayout

@OptIn(UnstableApi::class)
@Composable
fun BvVideoPlayer(
    modifier: Modifier = Modifier,
    videoPlayer: AbstractVideoPlayer,
    playerListener: VideoPlayerListener
) {
    DisposableEffect(Unit) {
        videoPlayer.setPlayerEventListener(playerListener)

        onDispose {
            videoPlayer.release()
        }
    }

    when (videoPlayer) {
        is ExoMediaPlayer -> {
            var videoPlayerView: PlayerView? by remember { mutableStateOf(null) }
            AndroidView(
                modifier = modifier.fillMaxSize(),
                factory = { ctx ->
                    videoPlayerView = PlayerView(ctx).apply {
                        player = videoPlayer.mPlayer
                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                        useController = false
                    }
                    videoPlayerView!!
                }
            )
        }

        is VlcMediaPlayer -> {
            var videoPlayerView: VLCVideoLayout? by remember { mutableStateOf(null) }
            AndroidView(
                modifier = modifier.fillMaxSize(),
                factory = { ctx ->
                    videoPlayerView = VLCVideoLayout(ctx).apply {
                        videoPlayer.mPlayer?.attachViews(this, null, true, false)
                        videoPlayer.mPlayer?.videoScale = MediaPlayer.ScaleType.SURFACE_FILL
                    }
                    videoPlayerView!!
                }
            )
        }
    }
}
