package dev.aaa1115910.bv.component

import android.app.Activity
import android.os.CountDownTimer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.kuaishou.akdanmaku.DanmakuConfig
import com.kuaishou.akdanmaku.ui.DanmakuPlayer
import com.kuaishou.akdanmaku.ui.DanmakuView
import dev.aaa1115910.bv.Keys
import dev.aaa1115910.bv.util.toast
import dev.aaa1115910.bv.viewmodel.PlayerViewModel
import kotlinx.coroutines.launch
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

    val updateSeek: () -> Unit = {
        totalDuration = player.duration.coerceAtLeast(0L)
        currentTime = player.currentPosition.coerceAtLeast(0L)
        bufferedPercentage = player.bufferedPercentage
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

    Box(modifier = modifier) {
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
                            danmakuPlayer.seekTo(player.currentPosition)
                        } else {
                            danmakuPlayer.pause()
                        }
                    }

                    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                        logger.info { "onPlayWhenReadyChanged: [playWhenReady=$playWhenReady, reason=$reason]" }
                        if (playWhenReady) {
                            logger.info { "Start danmaku" }
                            val danmakuConfig = DanmakuConfig(visibility = true)
                            danmakuPlayer.updateConfig(danmakuConfig)
                            danmakuPlayer.start(danmakuConfig)
                            danmakuPlayer.seekTo(player.currentPosition)
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
            onSeekChanged = { timeMs ->
                player.seekTo(timeMs.toLong())
            }
        )

        Column {
            Text("totalDuration: $totalDuration")
            Text("currentTime: $currentTime")
            Text("bufferedPercentage: $bufferedPercentage")
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
    onSeekChanged: (timeMs: Float) -> Unit
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
                }

                //change progress
                Keys.Left -> {
                    if (showRightController()) return@LaunchedEffect

                    showBottomController = true
                    lastChangedSeek = System.currentTimeMillis()
                    playerViewModel.player?.seekBack()
                }

                //show right controller - part
                Keys.Up -> {
                    if (showRightController()) return@LaunchedEffect

                    //showRightPartController = true
                }

                //show right controller - menu
                Keys.Menu -> {
                    if (showRightController()) return@LaunchedEffect

                    //showRightMenuController = true
                }

                //play and pause
                Keys.Center -> {
                    if (showRightMenuController) return@LaunchedEffect

                    if (playerViewModel.player?.isPlaying == true) {
                        playerViewModel.player?.pause()
                    } else {
                        playerViewModel.player?.play()
                    }
                }

                Keys.Back -> {
                    if (showRightController()) return@LaunchedEffect

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
                    color = Color.Green
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("暂停")
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
            AnimatedVisibility(visible = showRightMenuController) {
                RightMenuControl()
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
private fun BottomControls(
    modifier: Modifier = Modifier,
    totalDuration: Long,
    currentTime: Long,
    bufferedPercentage: Int,
    onSeekChanged: (timeMs: Float) -> Unit
) {
    Column(
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    0.0f to Color.Transparent,
                    1.0f to Color.Black.copy(alpha = 0.5f),
                    startY = 0.0f,
                    endY = 80.0f
                )
            )
            .padding(bottom = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .focusable(false),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier)
            Text(
                modifier = Modifier.padding(top = 16.dp, bottom = 0.dp, end = 40.dp),
                text = "${currentTime.formatMinSec()} / ${totalDuration.formatMinSec()}",
                color = Color.White
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Slider(
                modifier = Modifier.fillMaxWidth(),
                value = bufferedPercentage.toFloat(),
                enabled = false,
                onValueChange = { /*do nothing*/ },
                valueRange = 0f..100f,
                colors = SliderDefaults.colors(
                    disabledThumbColor = Color.Transparent,
                    disabledInactiveTrackColor = Color.Transparent,
                    disabledActiveTrackColor = Color.Gray
                )
            )

            Slider(
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                value = currentTime.toFloat(),
                onValueChange = onSeekChanged,
                valueRange = 0f..totalDuration.toFloat(),
                colors = SliderDefaults.colors(
                    disabledThumbColor = Color.Transparent,
                    disabledInactiveTrackColor = Color.Gray.copy(alpha = 0.5f),
                    disabledActiveTrackColor = Color.White
                )
            )
        }
    }
}

@Composable
private fun RightMenuControl(
    modifier: Modifier = Modifier
) {

}


@Composable
private fun RightPartControl(
    modifier: Modifier = Modifier
) {

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

@Preview
@Composable
fun BottomControlsPreview() {
    BottomControls(
        totalDuration = 123456,
        currentTime = 23333,
        bufferedPercentage = 68,
        onSeekChanged = {}
    )
}