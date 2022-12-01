package dev.aaa1115910.bv.component

import android.app.Activity
import android.os.CountDownTimer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.kuaishou.akdanmaku.DanmakuConfig
import com.kuaishou.akdanmaku.ui.DanmakuPlayer
import com.kuaishou.akdanmaku.ui.DanmakuView
import dev.aaa1115910.bv.BuildConfig
import dev.aaa1115910.bv.Keys
import dev.aaa1115910.bv.component.controllers.BottomControls
import dev.aaa1115910.bv.component.controllers.RightMenuControl
import dev.aaa1115910.bv.component.controllers.RightPartControl
import dev.aaa1115910.bv.entity.DanmakuSize
import dev.aaa1115910.bv.entity.DanmakuTransparency
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.toast
import dev.aaa1115910.bv.viewmodel.PlayerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.koin.androidx.compose.koinViewModel
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.TimeUnit

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(
    modifier: Modifier = Modifier,
    player: ExoPlayer,
    danmakuPlayer: DanmakuPlayer,
    playerViewModel: PlayerViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val logger = KotlinLogging.logger { }

    var totalDuration by remember { mutableStateOf(0L) }
    var currentTime by remember { mutableStateOf(0L) }
    var bufferedPercentage by remember { mutableStateOf(0) }
    var size by remember { mutableStateOf("") }

    var danmakuConfig by remember {
        mutableStateOf(
            DanmakuConfig(
                visibility = playerViewModel.currentDanmakuEnabled,
                textSizeScale = playerViewModel.currentDanmakuSize.scale * 2,
                alpha = playerViewModel.currentDanmakuTransparency.transparency
            )
        )
    }

    val updateSeek: () -> Unit = {
        totalDuration = player.duration.coerceAtLeast(0L)
        currentTime = player.currentPosition.coerceAtLeast(0L)
        bufferedPercentage = player.bufferedPercentage
        size = "${player.videoSize.width} x ${player.videoSize.height}"
    }

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

    LaunchedEffect(playerViewModel.danmakuData) {
        logger.info { "update danmaku data" }
        danmakuPlayer.updateData(playerViewModel.danmakuData)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        DisposableEffect(key1 = Unit) {

            val listener =
                object : Player.Listener {
                    override fun onEvents(player: Player, events: Player.Events) {
                        logger.info { "onEvent: [player=$player, events=$events]" }
                        super.onEvents(player, events)
                        updateSeek()
                        logger.info { "totalDuration: $totalDuration currentTime:$currentTime bufferedPercentage:$bufferedPercentage" }
                        when (events) {
                        }
                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        logger.info { "onPlaybackStateChanged: [playbackState=$playbackState]" }
                        if (playbackState == Player.STATE_READY) {
                            danmakuPlayer.start(danmakuConfig)
                            danmakuPlayer.seekTo(player.currentPosition)

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
                        logger.info { "onPlayWhenReadyChanged: [playWhenReady=$playWhenReady, reason=$reason]" }
                        if (playWhenReady) {
                            logger.info { "Start danmaku" }
                            danmakuPlayer.updateConfig(danmakuConfig)
                        } else {
                            logger.info { "Pause danmaku" }
                            danmakuPlayer.pause()
                        }
                    }

                    override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                        logger.info { "onTimelineChanged: [timeline=$timeline, reason=$reason]" }
                        super.onTimelineChanged(timeline, reason)
                    }

                    override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                        super.onMediaMetadataChanged(mediaMetadata)
                    }

                    //进度条回退
                    override fun onSeekBackIncrementChanged(seekBackIncrementMs: Long) {
                        danmakuPlayer.seekTo(player.currentPosition)
                    }

                    //进度条快进
                    override fun onSeekForwardIncrementChanged(seekForwardIncrementMs: Long) {
                        danmakuPlayer.seekTo(player.currentPosition)
                    }

                    override fun onSeekProcessed() {
                        println(player.currentPosition)
                    }
                }

            player.addListener(listener)

            onDispose {
                player.removeListener(listener)
                player.release()
            }
        }

        AndroidView(
            factory = {
                PlayerView(context).apply {
                    this.player = player
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                    useController = false
                }
            }
        )

        DanmakuPlayerCompose(
            danmakuPlayer = danmakuPlayer
        )

        PlayerControllers(
            totalDuration = totalDuration,
            currentTime = currentTime,
            bufferedPercentage = bufferedPercentage,
            buffering = playerViewModel.showBuffering,
            onSeekChanged = { timeMs ->
                player.seekTo(timeMs.toLong())
            },
            onChooseResolution = { qualityId ->
                Prefs.defaultQuality = qualityId
                playerViewModel.currentQuality = qualityId
                player.pause()
                val current = player.currentPosition
                scope.launch(Dispatchers.Default) {
                    playerViewModel.playQuality(qualityId)
                    withContext(Dispatchers.Main) {
                        player.seekTo(current)
                        player.play()
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
            }
        )

        if (BuildConfig.DEBUG) {
            Column(
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Text("视频长度: $totalDuration")
                Text("当前时间: $currentTime")
                Text("缓冲: $bufferedPercentage%")
                Text("分辨率: $size")
                Text("")
            }
        }
        if (playerViewModel.showLogs) {
            Text(
                modifier = Modifier.align(Alignment.BottomStart),
                text = playerViewModel.logs
            )
        }
    }
}

@Composable
fun DanmakuPlayerCompose(
    modifier: Modifier = Modifier,
    danmakuPlayer: DanmakuPlayer
) {
    val context = LocalContext.current

    DisposableEffect(key1 = Unit) {
        onDispose {
            danmakuPlayer.release()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                DanmakuView(context).apply {
                    danmakuPlayer.bindView(this)
                }
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerControllers(
    modifier: Modifier = Modifier,
    totalDuration: Long,
    currentTime: Long,
    bufferedPercentage: Int,
    buffering: Boolean,
    onSeekChanged: (timeMs: Float) -> Unit,
    onChooseResolution: (qualityId: Int) -> Unit,
    onSwitchDanmaku: (enable: Boolean) -> Unit,
    onDanmakuSizeChange: (DanmakuSize) -> Unit,
    onDanmakuTransparencyChange: (DanmakuTransparency) -> Unit
) {
    val context = LocalContext.current
    val playerViewModel: PlayerViewModel = koinViewModel()
    val logger = KotlinLogging.logger { }

    var showBottomController by remember { mutableStateOf(false) }
    var showTopController by remember { mutableStateOf(false) }
    var showRightMenuController by remember { mutableStateOf(false) }
    var showRightPartController by remember { mutableStateOf(false) }
    var lastPressBack by remember { mutableStateOf(0L) }
    var lastChangedSeek by remember { mutableStateOf(0L) }

    var timer: CountDownTimer? by remember { mutableStateOf(null) }

    val showRightController: () -> Boolean = { showRightMenuController || showRightPartController }

    val onBackRightController: () -> Unit = {

    }

    val onHideRightMenu: () -> Unit = {
        if (showRightPartController) {
            //do nothing
        } else {
            showRightMenuController = false
        }
    }

    val onHideRightPart: () -> Unit = {
        if (showRightMenuController) {
            //do nothing
        } else {
            showRightPartController = false
        }
    }

    LaunchedEffect(showRightMenuController || showRightPartController) {
        playerViewModel.showingRightMenu = showRightMenuController || showRightPartController
    }

    //listen key events
    LaunchedEffect(playerViewModel.lastPressedTime) {
        if (playerViewModel.lastPressedTime == 0L) return@LaunchedEffect
        logger.info { "new key pressed: ${playerViewModel.lastPressedKey}" }
        if (playerViewModel.lastPressedKey != Keys.Other && playerViewModel.lastPressedTime != playerViewModel.lastConsumeTime) {
            when (playerViewModel.lastPressedKey) {
                //show bottom controller
                Keys.Down -> {
                    if (showRightController()) return@LaunchedEffect

                    showBottomController = !showBottomController
                }

                //change progress
                Keys.Right -> {
                    if (showRightController()) return@LaunchedEffect

                    showBottomController = true
                    lastChangedSeek = System.currentTimeMillis()
                    playerViewModel.player?.seekForward()
                    playerViewModel.player?.play()
                }

                //change progress
                Keys.Left -> {
                    if (showRightController()) return@LaunchedEffect

                    showBottomController = true
                    lastChangedSeek = System.currentTimeMillis()
                    playerViewModel.player?.seekBack()
                    playerViewModel.player?.play()
                }

                //show right controller - part
                Keys.Up -> {
                    if (showRightController()) return@LaunchedEffect

                    //showRightPartController = true
                }

                //show right controller - menu
                Keys.Menu -> {
                    if (showRightController()) {
                        onHideRightMenu()
                        return@LaunchedEffect
                    }

                    showRightMenuController = !showRightMenuController
                }

                //play and pause
                Keys.Center -> {
                    if (showRightMenuController) return@LaunchedEffect

                    if (playerViewModel.player?.isPlaying == true) {
                        playerViewModel.player?.pause()
                    } else {
                        playerViewModel.player?.play()
                        playerViewModel.danmakuPlayer?.start()
                    }
                }

                Keys.Back -> {
                    if (showRightController()) {
                        showRightMenuController = false
                        return@LaunchedEffect
                    }

                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastPressBack < 1000 * 3) {
                        (context as Activity).finish()
                    } else {
                        lastPressBack = currentTime
                        "Press back button again to exit".toast(context)
                    }
                }

                else -> {}
            }
        }
        playerViewModel.lastConsumeTime = playerViewModel.lastPressedTime
    }

    /*BackHandler(!showBottomController && !showTopController && !showRightMenuController) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastPressBack < 1000 * 3) {
            (context as Activity).finish()
        } else {
            lastPressBack = currentTime
            "Press back button again to exit".toast(context)
        }
    }*/

    val setCloseBottomTimer: () -> Unit = {
        if (showBottomController) {
            timer?.cancel()
            timer = object : CountDownTimer(5000, 1000) {
                override fun onTick(millisUntilFinished: Long) {

                }

                override fun onFinish() {
                    showBottomController = false
                }
            }
            timer?.start()
        } else {
            timer?.cancel()
            timer = null
        }
    }

    LaunchedEffect(showBottomController) {
        setCloseBottomTimer()
    }

    LaunchedEffect(lastChangedSeek) {
        setCloseBottomTimer()
    }

    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            if (playerViewModel.player?.isPlaying != true) {
                Surface(
                    modifier = Modifier
                        .padding(28.dp, 28.dp, 28.dp, (3 * 28).dp)
                        .size(64.dp),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (buffering) {
                            Text(text = "缓冲中")
                        } else {
                            Text(text = "暂停")
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedVisibility(
                modifier = Modifier.align(Alignment.BottomCenter),
                visible = showBottomController,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                BottomControls(
                    totalDuration = totalDuration,
                    currentTime = currentTime,
                    onSeekChanged = onSeekChanged,
                    bufferedPercentage = bufferedPercentage
                )
            }
            AnimatedVisibility(
                modifier = Modifier.align(Alignment.CenterEnd),
                visible = showRightMenuController,
                enter = expandHorizontally(),
                exit = shrinkHorizontally()
            ) {
                RightMenuControl(
                    resolutionMap = playerViewModel.availableQuality,
                    currentResolution = playerViewModel.currentQuality,
                    currentDanmakuEnabled = playerViewModel.currentDanmakuEnabled,
                    currentDanmakuSize = playerViewModel.currentDanmakuSize,
                    currentDanmakuTransparency = playerViewModel.currentDanmakuTransparency,
                    onChooseResolution = onChooseResolution,
                    onSwitchDanmaku = onSwitchDanmaku,
                    onDanmakuSizeChange = onDanmakuSizeChange,
                    onDanmakuTransparencyChange = onDanmakuTransparencyChange
                )
            }
            AnimatedVisibility(visible = showRightPartController) {
                RightPartControl()
            }
            AnimatedVisibility(
                modifier = Modifier.align(Alignment.BottomCenter),
                visible = showTopController,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                TopController(
                    title = "This is video title"
                )
            }
        }
    }
}

@Composable
private fun TopController(
    modifier: Modifier = Modifier,
    title: String
) {
    Column(
        modifier = modifier
            .padding(top = 32.dp, start = 32.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.displayLarge
        )
    }
}

fun Long.formatMinSec(): String {
    return if (this == 0L) {
        "..."
    } else {
        String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(this),
            TimeUnit.MILLISECONDS.toSeconds(this) -
                    TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(this)
                    )
        )
    }
}
