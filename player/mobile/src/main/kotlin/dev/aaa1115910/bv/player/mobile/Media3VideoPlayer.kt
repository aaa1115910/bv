package dev.aaa1115910.bv.player.mobile

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MergingMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView

@OptIn(UnstableApi::class)
@Composable
fun Media3VideoPlayer(
    modifier: Modifier = Modifier,
    videoPlayer: ExoPlayer
) {
    var videoPlayerView: PlayerView? by remember { mutableStateOf(null) }
    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { ctx ->
            videoPlayerView = PlayerView(ctx).apply {
                player = videoPlayer
                player?.playWhenReady = true
                //resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                useController = false
            }
            videoPlayerView!!
        }
    )
}

@OptIn(UnstableApi::class)
fun ExoPlayer.playUrl(videoUrl: String?, audioUrl: String?) {
    val videoMediaSource = videoUrl?.let {
        ProgressiveMediaSource.Factory(Media3VideoPlayerConfig.dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(it))
    }
    val audioMediaSource = audioUrl?.let {
        ProgressiveMediaSource.Factory(Media3VideoPlayerConfig.dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(it))
    }

    val mediaSources = listOfNotNull(videoMediaSource, audioMediaSource)
    setMediaSource(MergingMediaSource(*mediaSources.toTypedArray()))
}

@UnstableApi
object Media3VideoPlayerConfig {
    private const val userAgent =
        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36"
    private val header = mapOf("referer" to "https://www.bilibili.com")

    val dataSourceFactory = DefaultHttpDataSource.Factory().apply {
        setUserAgent(userAgent)
        setDefaultRequestProperties(header)
    }
}