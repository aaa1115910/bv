package dev.aaa1115910.bv.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import dev.aaa1115910.bv.player.AbstractVideoPlayer
import dev.aaa1115910.bv.player.BvVideoPlayer
import dev.aaa1115910.bv.player.VideoPlayerListener
import dev.aaa1115910.bv.player.VideoPlayerOptions
import dev.aaa1115910.bv.player.impl.exo.ExoPlayerFactory

private const val videoUrl = ""
private const val audioUrl = ""

private val options = VideoPlayerOptions(
    userAgent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36",
    referer = "https://www.bilibili.com"
)

private val videoPlayerListener = object : VideoPlayerListener {
    override fun onError(error: Exception) {
        println("onError: $error")
        //TODO("Not yet implemented")
    }

    override fun onReady() {
        println("onReady")
        //TODO("Not yet implemented")
    }

    override fun onPlay() {
        println("onPlay")
        //TODO("Not yet implemented")
    }

    override fun onPause() {
        println("onPause")
        //TODO("Not yet implemented")
    }

    override fun onBuffering() {
        println("onBuffering")
        //TODO("Not yet implemented")
    }

    override fun onEnd() {
        println("onEnd")
        //TODO("Not yet implemented")
    }

    override fun onIdle() {
        println("onIdle")
        //TODO("Not yet implemented")
    }

    override fun onSeekBack(seekBackIncrementMs: Long) {
        //TODO("Not yet implemented")
    }

    override fun onSeekForward(seekForwardIncrementMs: Long) {
        //TODO("Not yet implemented")
    }
}

@Preview
@Composable
fun BvVideoPlayerExoPreview() {
    val context = LocalContext.current
    val exoPlayer by remember { mutableStateOf(ExoPlayerFactory().create(context, options)) }

    BvVideoPlayerPreview(exoPlayer)
}

@Composable
fun BvVideoPlayerPreview(
    player: AbstractVideoPlayer
) {
    LaunchedEffect(Unit) {
        player.setOptions()
        player.playUrl(videoUrl, audioUrl)
        player.prepare()
    }

    BvVideoPlayer(
        modifier = Modifier.fillMaxSize(),
        videoPlayer = player,
        playerListener = videoPlayerListener
    )
}