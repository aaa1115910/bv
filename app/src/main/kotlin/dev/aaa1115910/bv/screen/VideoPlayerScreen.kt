package dev.aaa1115910.bv.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.Player
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.kuaishou.akdanmaku.DanmakuConfig
import dev.aaa1115910.bv.component.DanmakuPlayerCompose
import dev.aaa1115910.bv.component.controllers.VideoPlayerController
import dev.aaa1115910.bv.component.controllers.info.VideoPlayerInfoData
import dev.aaa1115910.bv.entity.VideoAspectRatio
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.requestFocus
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
    val scope = rememberCoroutineScope()
    val logger = KotlinLogging.logger { }

    val videoPlayer = playerViewModel.player!!

    var videoPlayerView: PlayerView? by remember { mutableStateOf(null) }
    var videoPlayerHeight by remember { mutableStateOf(0.dp) }
    var videoPlayerWidth by remember { mutableStateOf(0.dp) }
    var usingDefaultAspectRatio by remember { mutableStateOf(true) }
    var currentVideoAspectRatio by remember { mutableStateOf(VideoAspectRatio.Default) }
    var currentPosition by remember { mutableStateOf(0L) }

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        logger.fInfo { "Request focus on controller" }
        focusRequester.requestFocus(scope)
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
        currentPosition = videoPlayer.currentPosition.coerceAtLeast(0L)
        infoData = VideoPlayerInfoData(
            totalDuration = videoPlayer.duration.coerceAtLeast(0L),
            currentTime = videoPlayer.currentPosition.coerceAtLeast(0L),
            bufferedPercentage = videoPlayer.bufferedPercentage,
            resolutionWidth = videoPlayer.videoSize.width,
            resolutionHeight = videoPlayer.videoSize.height,
            codec = videoPlayer.videoFormat?.sampleMimeType ?: "null"
        )
    }

    val sendHeartbeat: () -> Unit = {
        scope.launch(Dispatchers.Default) {
            val time = withContext(Dispatchers.Main) {
                val currentTime = (videoPlayer.currentPosition.coerceAtLeast(0L) / 1000).toInt()
                val totalTime = (videoPlayer.duration.coerceAtLeast(0L) / 1000).toInt()
                //播放完后上报的时间应为 -1
                if (currentTime >= totalTime) -1 else currentTime
            }
            playerViewModel.uploadHistory(time)
        }
    }

    //定时刷新进度条
    DisposableEffect(Unit) {
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                scope.launch {
                    updateSeek()

                    //播放一段时间后隐藏跳转历史记录
                    if (playerViewModel.lastPlayed != 0 && infoData.currentTime > 3000) {
                        playerViewModel.lastPlayed = 0
                    }
                }
            }

        }, 0, 100)
        onDispose {
            timer.cancel()
        }
    }

    //播放记录上报
    DisposableEffect(Unit) {
        val timer = Timer()
        if (!Prefs.incognitoMode) {
            timer.schedule(object : TimerTask() {
                override fun run() {
                    scope.launch(Dispatchers.Main) {
                        if (videoPlayer.isPlaying) sendHeartbeat()
                    }
                }
            }, 5000, 15000)
        }
        onDispose {
            if (!Prefs.incognitoMode) {
                sendHeartbeat()
                timer.cancel()
            }
        }
    }

    DisposableEffect(Unit) {

        val hideLogs: () -> Unit = {
            scope.launch(Dispatchers.Default) {
                delay(3_000)
                playerViewModel.showLogs = false
            }
        }

        //exo player listener
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                //缓冲完成，并非播放/暂停
                if (playbackState == Player.STATE_READY) {
                    playerViewModel.danmakuPlayer?.start(danmakuConfig)
                    playerViewModel.danmakuPlayer?.seekTo(videoPlayer.currentPosition)

                    playerViewModel.showBuffering = false
                    hideLogs()
                } else if (playbackState == Player.STATE_ENDED) {
                    if (!Prefs.incognitoMode) sendHeartbeat()

                    val videoListIndex = playerViewModel.availableVideoList.indexOfFirst {
                        it.cid == playerViewModel.currentCid
                    }
                    //播放下一集
                    if (videoListIndex < playerViewModel.availableVideoList.size - 1) {
                        val nextVideo = playerViewModel.availableVideoList[videoListIndex + 1]
                        playerViewModel.partTitle = nextVideo.title
                        playerViewModel.loadPlayUrl(
                            avid = nextVideo.aid,
                            cid = nextVideo.cid
                        )
                    }
                } else {
                    playerViewModel.danmakuPlayer?.pause()
                    if (playbackState == Player.STATE_BUFFERING) {
                        playerViewModel.showBuffering = true
                    }
                    hideLogs()
                }
            }

            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                if (playWhenReady) {
                    logger.fInfo { "Start danmaku" }
                    playerViewModel.danmakuPlayer?.updateConfig(danmakuConfig)
                } else {
                    logger.fInfo { "Pause danmaku" }
                    playerViewModel.danmakuPlayer?.pause()
                }
            }

            //进度条回退
            override fun onSeekBackIncrementChanged(seekBackIncrementMs: Long) {
                playerViewModel.danmakuPlayer?.seekTo(videoPlayer.currentPosition)
            }

            //进度条快进
            override fun onSeekForwardIncrementChanged(seekForwardIncrementMs: Long) {
                playerViewModel.danmakuPlayer?.seekTo(videoPlayer.currentPosition)
            }
        }

        videoPlayer.addListener(listener)

        //release exo video player
        onDispose {
            videoPlayer.removeListener(listener)
            videoPlayer.release()
        }
    }

    LaunchedEffect(playerViewModel.danmakuData) {
        logger.fInfo { "Update danmaku data" }
        playerViewModel.danmakuPlayer?.updateData(playerViewModel.danmakuData)
    }

    VideoPlayerController(
        modifier = modifier
            .focusRequester(focusRequester)
            .fillMaxSize(),
        playbackState=videoPlayer.playbackState,
        infoData = infoData,

        resolutionMap = playerViewModel.availableQuality,
        availableVideoCodec = playerViewModel.availableVideoCodec,
        availableSubtitle = playerViewModel.availableSubtitle,
        availableVideoList = playerViewModel.availableVideoList,

        currentVideoCid = playerViewModel.currentCid,
        currentVideoAspectRatio = currentVideoAspectRatio,
        currentResolution = playerViewModel.currentQuality,
        currentVideoCodec = playerViewModel.currentVideoCodec,
        currentDanmakuEnabled = playerViewModel.currentDanmakuEnabled,
        currentDanmakuSize = playerViewModel.currentDanmakuSize,
        currentDanmakuTransparency = playerViewModel.currentDanmakuTransparency,
        currentDanmakuArea = playerViewModel.currentDanmakuArea,

        currentSubtitleId = playerViewModel.currentSubtitleId,
        currentSubtitleData = playerViewModel.currentSubtitleData,
        currentSubtitleFontSize = playerViewModel.currentSubtitleFontSize,
        currentSubtitleBottomPadding = playerViewModel.currentSubtitleBottomPadding,
        currentPosition = currentPosition,

        buffering = playerViewModel.showBuffering,
        isPlaying = playerViewModel.player?.isPlaying == true,
        bufferSpeed = "",

        showLogs = playerViewModel.showLogs,
        logs = playerViewModel.logs,

        title = playerViewModel.title,
        partTitle = playerViewModel.partTitle,
        lastPlayed = if (videoPlayer.isPlaying) playerViewModel.lastPlayed else 0,

        onChooseResolution = { qualityId ->
            playerViewModel.currentQuality = qualityId
            videoPlayer.pause()
            val current = videoPlayer.currentPosition
            scope.launch(Dispatchers.Default) {
                playerViewModel.updateAvailableCodec()
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
                playerViewModel.playQuality(
                    playerViewModel.currentQuality,
                    playerViewModel.currentVideoCodec
                )
                withContext(Dispatchers.Main) {
                    videoPlayer.seekTo(current)
                    videoPlayer.play()
                }
            }
        },
        onChooseVideoAspectRatio = { aspectRadio ->
            currentVideoAspectRatio = aspectRadio
            when (aspectRadio) {
                VideoAspectRatio.Default -> {
                    videoPlayerView?.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                    usingDefaultAspectRatio = true
                }

                VideoAspectRatio.FourToThree -> {
                    videoPlayerView?.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                    usingDefaultAspectRatio = false
                    videoPlayerWidth = videoPlayerHeight * (4 / 3f)
                }

                VideoAspectRatio.SixteenToNine -> {
                    videoPlayerView?.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                    usingDefaultAspectRatio = false
                    videoPlayerWidth = videoPlayerHeight * (16 / 9f)
                }
            }
        },
        onSwitchDanmaku = { enable ->
            Prefs.defaultDanmakuEnabled = enable
            playerViewModel.currentDanmakuEnabled = enable
            danmakuConfig.visibility = enable
            playerViewModel.danmakuPlayer?.updateConfig(danmakuConfig)
        },
        onDanmakuSizeChange = { size ->
            Prefs.defaultDanmakuSize = size.ordinal
            playerViewModel.currentDanmakuSize = size
            danmakuConfig.textSizeScale = size.scale * 2
            playerViewModel.danmakuPlayer?.updateConfig(danmakuConfig)
        },
        onDanmakuTransparencyChange = { transparency ->
            Prefs.defaultDanmakuTransparency = transparency.ordinal
            playerViewModel.currentDanmakuTransparency = transparency
            danmakuConfig.alpha = transparency.transparency
            playerViewModel.danmakuPlayer?.updateConfig(danmakuConfig)
        },
        onDanmakuAreaChange = { area ->
            Prefs.defaultDanmakuArea = area
            playerViewModel.currentDanmakuArea = area
        },
        onSubtitleChange = { subtitleId ->
            playerViewModel.loadSubtitle(subtitleId)
        },
        onSubtitleFontSizeChange = { subtitleFontSize ->
            println(subtitleFontSize)
            Prefs.defaultSubtitleFontSize = subtitleFontSize
            playerViewModel.currentSubtitleFontSize = subtitleFontSize
        },
        onSubtitleBottomPaddingChange = { subtitleBottomPadding ->
            println(subtitleBottomPadding)
            Prefs.defaultSubtitleBottomPadding = subtitleBottomPadding
            playerViewModel.currentSubtitleBottomPadding = subtitleBottomPadding
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
            sendHeartbeat()
        },
        onExit = {
            sendHeartbeat()
        },
        requestFocus = {
            focusRequester.requestFocus(scope)
        },
        goBackHistory = {
            videoPlayer.seekTo(playerViewModel.lastPlayed.toLong())
            playerViewModel.lastPlayed = 0
        },
        onVideoSwitch = { videoListItem ->
            playerViewModel.fromSeason = videoListItem.isEpisode
            playerViewModel.partTitle = videoListItem.title
            playerViewModel.loadPlayUrl(
                avid = videoListItem.aid,
                cid = videoListItem.cid
            )
        }
    ) {
        BoxWithConstraints(
            modifier = Modifier.background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            videoPlayerHeight = this.maxHeight

            val videoPlayerModifier = if (usingDefaultAspectRatio) {
                Modifier.fillMaxSize()
            } else {
                Modifier
                    .fillMaxHeight()
                    .width(videoPlayerWidth)
            }

            AndroidView(
                modifier = videoPlayerModifier,
                factory = { ctx ->
                    videoPlayerView = PlayerView(ctx).apply {
                        player = videoPlayer
                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                        useController = false
                    }
                    videoPlayerView!!
                }
            )

            DanmakuPlayerCompose(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxHeight(playerViewModel.currentDanmakuArea),
                danmakuPlayer = playerViewModel.danmakuPlayer
            )
        }
    }
}