package dev.aaa1115910.bv.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.Player
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.kuaishou.akdanmaku.DanmakuConfig
import dev.aaa1115910.bv.component.DanmakuPlayerCompose
import dev.aaa1115910.bv.component.controllers.VideoPlayerController
import dev.aaa1115910.bv.component.controllers.info.VideoPlayerInfoData
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.viewmodel.PlayerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.koin.androidx.compose.koinViewModel
import java.util.Timer
import java.util.TimerTask

@Composable
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
fun VideoPlayerScreen(
    modifier: Modifier = Modifier,
    playerViewModel: PlayerViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val logger = KotlinLogging.logger { }

    val videoPlayer = playerViewModel.player!!
    val danmakuPlayer = playerViewModel.danmakuPlayer!!

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        logger.info { "Request focus on controller" }
        focusRequester.requestFocus()
        //focusRequester.captureFocus()
    }

    var infoData by remember {
        mutableStateOf(
            VideoPlayerInfoData(
                totalDuration = 0,
                currentTime = 0,
                bufferedPercentage = 0,
                resolutionWidth = 0,
                resolutionHeight = 0,
                codec = "null"
            )
        )
    }

    val danmakuConfig by remember {
        mutableStateOf(
            DanmakuConfig(
                visibility = playerViewModel.currentDanmakuEnabled,
                textSizeScale = playerViewModel.currentDanmakuSize.scale * 2,
                alpha = playerViewModel.currentDanmakuTransparency.transparency
            )
        )
    }

    val updateSeek: () -> Unit = {
        infoData = VideoPlayerInfoData(
            totalDuration = videoPlayer.duration.coerceAtLeast(0L),
            currentTime = videoPlayer.currentPosition.coerceAtLeast(0L),
            bufferedPercentage = videoPlayer.bufferedPercentage,
            resolutionWidth = videoPlayer.videoSize.width,
            resolutionHeight = videoPlayer.videoSize.height,
            codec = videoPlayer.videoFormat?.codecs ?: "null"
        )
    }

    //定时刷新进度条
    DisposableEffect(Unit) {
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                scope.launch {
                    updateSeek()
                }
            }

        }, 500, 500)
        onDispose {
            timer.cancel()
        }
    }

    DisposableEffect(Unit) {
        //exo player listener
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                //缓冲完成，并非播放/暂停
                if (playbackState == Player.STATE_READY) {
                    danmakuPlayer.start(danmakuConfig)
                    danmakuPlayer.seekTo(videoPlayer.currentPosition)

                    playerViewModel.showBuffering = false
                } else {
                    danmakuPlayer.pause()
                    if (playbackState == Player.STATE_BUFFERING) {
                        playerViewModel.showBuffering = true
                    }

                    //隐藏左下角日志
                    scope.launch(Dispatchers.Default) {
                        delay(3_000)
                        playerViewModel.showLogs = false
                    }
                }
            }

            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                if (playWhenReady) {
                    logger.info { "Start danmaku" }
                    danmakuPlayer.updateConfig(danmakuConfig)
                } else {
                    logger.info { "Pause danmaku" }
                    danmakuPlayer.pause()
                }
            }

            //进度条回退
            override fun onSeekBackIncrementChanged(seekBackIncrementMs: Long) {
                danmakuPlayer.seekTo(videoPlayer.currentPosition)
            }

            //进度条快进
            override fun onSeekForwardIncrementChanged(seekForwardIncrementMs: Long) {
                danmakuPlayer.seekTo(videoPlayer.currentPosition)
            }
        }

        val analyticsListener = object : AnalyticsListener {
            override fun onBandwidthEstimate(
                eventTime: AnalyticsListener.EventTime,
                totalLoadTimeMs: Int,
                totalBytesLoaded: Long,
                bitrateEstimate: Long
            ) {
                println("----$totalLoadTimeMs----$totalBytesLoaded----${bitrateEstimate / 1024}KB/s----")
            }
        }

        videoPlayer.addListener(listener)
        videoPlayer.addAnalyticsListener(analyticsListener)

        //release exo video player
        onDispose {
            videoPlayer.removeListener(listener)
            videoPlayer.release()
        }
    }

    LaunchedEffect(playerViewModel.danmakuData) {
        logger.info { "Update danmaku data" }
        danmakuPlayer.updateData(playerViewModel.danmakuData)
    }

    VideoPlayerController(
        modifier = modifier
            //.focusable()
            .focusRequester(focusRequester)
            .fillMaxSize(),
        infoData = infoData,

        resolutionMap = playerViewModel.availableQuality,
        availableVideoCodec = playerViewModel.availableVideoCodec,
        currentResolution = playerViewModel.currentQuality,
        currentVideoCodec = playerViewModel.currentVideoCodec,
        currentDanmakuEnabled = playerViewModel.currentDanmakuEnabled,
        currentDanmakuSize = playerViewModel.currentDanmakuSize,
        currentDanmakuTransparency = playerViewModel.currentDanmakuTransparency,

        buffering = playerViewModel.showBuffering,
        isPlaying = playerViewModel.player?.isPlaying == true,
        bufferSpeed = "playerViewModel.player?.",

        showLogs = playerViewModel.showLogs,
        logs = playerViewModel.logs,

        title = playerViewModel.title,

        onChooseResolution = { qualityId ->
            playerViewModel.currentQuality = qualityId
            videoPlayer.pause()
            val current = videoPlayer.currentPosition
            scope.launch(Dispatchers.Default) {
                playerViewModel.playQuality(qualityId)
                withContext(Dispatchers.Main) {
                    videoPlayer.seekTo(current)
                    videoPlayer.play()
                }
            }
        },
        onChooseVideoCodec = { videoCodec ->
            playerViewModel.currentVideoCodec = videoCodec
            videoPlayer.pause()
            val current = videoPlayer.currentPosition
            scope.launch(Dispatchers.Default) {
                playerViewModel.playQuality(playerViewModel.currentQuality, videoCodec)
                withContext(Dispatchers.Main) {
                    videoPlayer.seekTo(current)
                    videoPlayer.play()
                }
            }
        },
        onSwitchDanmaku = { enable ->
            Prefs.defaultDanmakuEnabled = enable
            playerViewModel.currentDanmakuEnabled = enable
            danmakuConfig.visibility = enable
            danmakuPlayer.updateConfig(danmakuConfig)
        },
        onDanmakuSizeChange = { size ->
            Prefs.defaultDanmakuSize = size.ordinal
            playerViewModel.currentDanmakuSize = size
            danmakuConfig.textSizeScale = size.scale * 2
            danmakuPlayer.updateConfig(danmakuConfig)
        },
        onDanmakuTransparencyChange = { transparency ->
            Prefs.defaultDanmakuTransparency = transparency.ordinal
            playerViewModel.currentDanmakuTransparency = transparency
            danmakuConfig.alpha = transparency.transparency
            danmakuPlayer.updateConfig(danmakuConfig)
        },
        onSeekBack = {
            playerViewModel.player?.seekBack()
            playerViewModel.player?.play()
        },
        onSeekForward = {
            playerViewModel.player?.seekForward()
            playerViewModel.player?.play()
        },
        onPlay = {
            playerViewModel.player?.play()
            playerViewModel.danmakuPlayer?.start()
        },
        onPause = {
            playerViewModel.player?.pause()
        },
        requestFocus = {
            focusRequester.requestFocus()
        }
    ) {
        Box(
            modifier = Modifier.background(Color.Black)
        ) {
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center),
                factory = {
                    PlayerView(context).apply {
                        player = videoPlayer
                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                        useController = false
                    }
                }
            )

            DanmakuPlayerCompose(
                modifier = Modifier.align(Alignment.TopCenter),
                danmakuPlayer = danmakuPlayer
            )
        }
    }
}