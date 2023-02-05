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
import dev.aaa1115910.bv.player.BvVideoPlayer
import dev.aaa1115910.bv.player.VideoPlayerListener
import dev.aaa1115910.bv.player.impl.exo.ExoPlayerFactory
import dev.aaa1115910.bv.player.impl.vlc.VlcPlayerFactory

private const val videoUrl = ""
private const val audioUrl = ""

private val videoPlayerListener = object : VideoPlayerListener {
    override fun onError(error: String) {
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
    val exoPlayer by remember { mutableStateOf(ExoPlayerFactory().create(context)) }

    LaunchedEffect(Unit) {
        //exoPlayer.initPlayer()
        exoPlayer.setOptions()
        exoPlayer.setHeader(mapOf("referer" to "https://www.bilibili.com"))
        exoPlayer.playUrl(videoUrl, audioUrl)
        exoPlayer.prepare()
    }

    BvVideoPlayer(
        modifier = Modifier.fillMaxSize(),
        videoPlayer = exoPlayer,
        playerListener = videoPlayerListener
    )
}

@Preview
@Composable
fun BvVideoPlayerVlcPreview() {
    val context = LocalContext.current
    val vldPlayer by remember { mutableStateOf(VlcPlayerFactory().create(context)) }

    LaunchedEffect(Unit) {
        //vldPlayer.initPlayer()
        vldPlayer.setOptions()
        vldPlayer.setHeader(mapOf("referer" to "https://www.bilibili.com"))
        vldPlayer.playUrl(videoUrl, audioUrl)
        vldPlayer.prepare()
    }

    BvVideoPlayer(
        modifier = Modifier.fillMaxSize(),
        videoPlayer = vldPlayer,
        playerListener = videoPlayerListener
    )
}