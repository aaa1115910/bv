package dev.aaa1115910.bv.player.tv.controller

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.automirrored.rounded.PlaylistPlay
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.KeyboardDoubleArrowDown
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.RepeatOne
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.ArrowDropUp
import androidx.compose.material3.Surface
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerClockData
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerConfigData
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerSeekData
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerSeekThumbData
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerStateData
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerVideoInfoData
import dev.aaa1115910.bv.player.entity.VideoPlayerClockData
import dev.aaa1115910.bv.player.entity.VideoPlayerSeekData
import dev.aaa1115910.bv.player.entity.VideoPlayerSeekThumbData
import dev.aaa1115910.bv.player.entity.VideoPlayerVideoInfoData
import dev.aaa1115910.bv.player.seekbar.SeekMoveState
import dev.aaa1115910.bv.player.shared.R
import dev.aaa1115910.bv.player.tv.VideoSeekBar
import dev.aaa1115910.bv.util.formatHourMinSec
import dev.aaa1115910.bv.util.ifElse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import dev.aaa1115910.bv.util.requestFocus
import kotlin.math.roundToInt

private fun formatSpeed(speed: Float): String {
    return "${(speed * 100).roundToInt() / 100f}x"
}

@Composable
fun ControllerVideoInfo(
    modifier: Modifier = Modifier,
    show: Boolean,
    playSpeed: Float = 1f,
    onHideInfo: () -> Unit,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onPlaySpeedChange: (Float) -> Unit,
    onOpenUpSpace: () -> Unit,
    onRefreshVideo: () -> Unit,
    onOpenDanmaku: () -> Unit,
    onHideDanmaku: () -> Unit,
    onOpenPlayList: () -> Unit,
    onOpenRelatedVideo: () -> Unit,
    onOpenSetting: () -> Unit,
    onLoopPlayModeChange: (Boolean) -> Unit
) {
    val videoPlayerClockData = LocalVideoPlayerClockData.current
    val videoPlayerSeekData = LocalVideoPlayerSeekData.current
    val videoPlayerSeekThumbData = LocalVideoPlayerSeekThumbData.current
    val videoPlayerVideoInfoData = LocalVideoPlayerVideoInfoData.current
    val videoPlayerStateData = LocalVideoPlayerStateData.current
    val videoPlayerConfigData = LocalVideoPlayerConfigData.current

//    var seekHideTimer: CountDownTimer? by remember { mutableStateOf(null) }
//    val setCloseInfoTimer: () -> Unit = {
//        if (show) {
//            seekHideTimer?.cancel()
//            seekHideTimer = object : CountDownTimer(5000, 1000) {
//                override fun onTick(millisUntilFinished: Long) {}
//                override fun onFinish() = onHideInfo()
//            }
//            seekHideTimer?.start()
//        } else {
//            seekHideTimer?.cancel()
//            seekHideTimer = null
//        }
//    }
//
//    LaunchedEffect(Unit) {
//        setCloseInfoTimer()
//    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        AnimatedVisibility(
            modifier = Modifier.align(Alignment.TopEnd),
            visible = show,
            enter = expandVertically(),
            exit = shrinkVertically(),
            label = "ControllerTopVideoInfo"
        ) {
            ControllerVideoInfoTop(
                clock = Triple(
                    videoPlayerClockData.hour,
                    videoPlayerClockData.minute,
                    videoPlayerClockData.second
                )
            )
        }
        AnimatedVisibility(
            modifier = Modifier.align(Alignment.BottomCenter),
            visible = show,
            enter = expandVertically(),
            exit = shrinkVertically(),
            label = "ControllerBottomVideoInfo"
        ) {
            ControllerVideoInfoBottom(
                show = show,
                onHideInfo = onHideInfo,
                seekData = videoPlayerSeekData,
                title = videoPlayerVideoInfoData.title,
                partTitle = videoPlayerVideoInfoData.partTitle,
                playSpeed = playSpeed,
                idleIcon = videoPlayerSeekThumbData.idleIcon,
                movingIcon = videoPlayerSeekThumbData.movingIcon,
                play = videoPlayerVideoInfoData.play,
                danmaku = videoPlayerVideoInfoData.danmaku,
                upName = videoPlayerVideoInfoData.upName,
                pubTime = videoPlayerVideoInfoData.pubTime,
                isPlaying = videoPlayerStateData.isPlaying || videoPlayerStateData.isBuffering,
                isLoop = videoPlayerConfigData.isLoop,
                showDanmaku = videoPlayerConfigData.showDanmaku,
                onPlay = onPlay,
                onPause = onPause,
                onPlaySpeedChange = onPlaySpeedChange,
                onOpenUpSpace = onOpenUpSpace,
                onRefreshVideo = onRefreshVideo,
                onOpenDanmaku = onOpenDanmaku,
                onHideDanmaku = onHideDanmaku,
                onOpenPlayList = onOpenPlayList,
                onOpenRelatedVideo = onOpenRelatedVideo,
                onOpenSetting = onOpenSetting,
                onLoopPlayModeChange = onLoopPlayModeChange,
                fromSeason = videoPlayerVideoInfoData.fromSeason
            )
        }
    }
}

@Composable
fun ControllerVideoInfoTop(
    modifier: Modifier = Modifier,
    clock: Triple<Int, Int, Int>
) {
    Clock(
        modifier = modifier
            .padding(horizontal = 32.dp, vertical = 16.dp),
        hour = clock.first,
        minute = clock.second,
        second = clock.third
    )
}

data class ControlButton(
    val id: String,
    val icon: ImageVector? = null,
    val text: String? = null,
    val onClick: () -> Unit,
    val visible: Boolean = true,
    val scale: Float = 1f,
    val painterId: Int? = null,
    val tint: Color = Color.White.copy(alpha = 0.8f),
    val width: Int? = null
)

@Composable
fun ControllerVideoInfoBottom(
    show: Boolean,
    onHideInfo: () -> Unit,
    modifier: Modifier = Modifier,
    playSpeed: Float = 1f,
    title: String,
    partTitle: String,
    seekData: VideoPlayerSeekData,
    idleIcon: String,
    movingIcon: String,
    play: Int,
    danmaku: Int,
    upName: String,
    pubTime: String,
    isPlaying: Boolean,
    isLoop: Boolean,
    showDanmaku: Boolean,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onPlaySpeedChange: (Float) -> Unit,
    onOpenUpSpace: () -> Unit,
    onRefreshVideo: () -> Unit,
    onOpenDanmaku: () -> Unit,
    onHideDanmaku: () -> Unit,
    onOpenPlayList: () -> Unit,
    onOpenRelatedVideo: () -> Unit,
    onOpenSetting: () -> Unit,
    onLoopPlayModeChange: (Boolean) -> Unit,
    fromSeason: Boolean = false
) {
    val scope = rememberCoroutineScope()
    var hideVideoInfoJob by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }
    var showSpeedDialog by remember { mutableStateOf(false) }
    var speed by remember { mutableStateOf(playSpeed) }
    val danmakuIconId = if (showDanmaku) R.drawable.ic_danmaku_on else R.drawable.ic_danmaku_hide
    val buttons = remember(fromSeason, showDanmaku, isPlaying, isLoop, speed) {
        listOf(
            ControlButton(
                id = "play",
                icon = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                onClick = { if (isPlaying) onPause() else onPlay() }
            ),
            ControlButton(
                id = "speed",
                text = formatSpeed(speed),
                onClick = { showSpeedDialog = true },
                width = 46
            ),
            ControlButton(
                id = "upSpace",
                icon = Icons.Rounded.Person,
                onClick = onOpenUpSpace,
                visible = !fromSeason
            ),
            ControlButton(
                id = "refresh",
                icon = Icons.Rounded.Refresh,
                onClick = onRefreshVideo
            ),
            ControlButton(
                id = "danmaku",
                painterId = danmakuIconId,
                onClick = { if (showDanmaku) onHideDanmaku() else onOpenDanmaku() }
            ),
            ControlButton(
                id = "loop",
                icon = if (isLoop) Icons.Rounded.RepeatOne else Icons.Rounded.Repeat,
                onClick = { onLoopPlayModeChange(!isLoop) }
            ),
            ControlButton(
                id = "playlist",
                icon = Icons.AutoMirrored.Rounded.PlaylistPlay,
                onClick = onOpenPlayList,
                scale = 1.2f
            ),
            ControlButton(
                id = "related",
                icon = Icons.Rounded.KeyboardDoubleArrowDown,
                onClick = onOpenRelatedVideo,
                visible = !fromSeason
            ),
            ControlButton(
                id = "settings",
                icon = Icons.Outlined.Settings,
                onClick = onOpenSetting
            )
        ).filter { it.visible }
    }

    var focusedButtonId by remember { mutableStateOf(buttons.firstOrNull()?.id ?: "") }
    val focusRequesters = remember(buttons) {
        buttons.associate { button ->
            button.id to FocusRequester()
        }.toMutableMap()
    }

    LaunchedEffect( Unit) {
        // 初始聚焦第一个按钮
        runCatching {
            buttons.firstOrNull()?.let {
                focusRequesters[it.id]?.requestFocus()
                focusedButtonId = it.id
            }
        }
        delay(300) // 有动画，确保在界面渲染后执行
        runCatching {
            buttons.firstOrNull()?.let {
                focusRequesters[it.id]?.requestFocus()
                focusedButtonId = it.id
            }
        }
    }

    LaunchedEffect(show, focusedButtonId) {
        hideVideoInfoJob?.cancel()
        hideVideoInfoJob = null
        if (show && !showSpeedDialog) {
            hideVideoInfoJob = scope.launch {
                delay(5000)
                withContext(Dispatchers.Main) {
                    onHideInfo()
                }
            }
        }
    }

    LaunchedEffect(showSpeedDialog) {
        if (showSpeedDialog) {
            hideVideoInfoJob?.cancel()
            hideVideoInfoJob = null
        } else {
            if (show) {
                hideVideoInfoJob?.cancel()
                hideVideoInfoJob = scope.launch {
                    delay(5000)
                    withContext(Dispatchers.Main) { onHideInfo() }
                }
            }
        }
    }

    Column(
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.5f)
                    ),
                    endY = 136f
                )
            ),
        verticalArrangement = Arrangement.Bottom
    ) {
        Spacer(
            modifier = Modifier
                .padding(top = 32.dp)
        )
        if (title.isNotEmpty() && partTitle.isNotEmpty() && title != partTitle) {
            Text(
                modifier = Modifier
                    .padding(horizontal = 32.dp),
                text = title,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.headlineSmall,
            )
        }
        Text(
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .fillMaxWidth(),
            text = if (partTitle.isEmpty() || title == partTitle) title else partTitle,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = if (partTitle.isEmpty() || title == partTitle) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.titleLarge,
        )
        if(upName.isNotEmpty()) {
            Text(
                modifier = Modifier
                    .padding(start = 32.dp, end = 32.dp, top = 8.dp, bottom = 0.dp)
                    .fillMaxWidth(),
                text = "$upName · ${
                    if (play >= 10000) "${play / 10000}万" else "$play"
                }次播放 · ${
                    if (danmaku >= 10000) "${danmaku / 10000}万" else "$danmaku"
                }弹幕 · 发布于 $pubTime",
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        VideoSeekBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, bottom = 2.dp),
            duration = seekData.duration,
            position = seekData.position,
            bufferedPercentage = seekData.bufferedPercentage,
            moveState = SeekMoveState.Idle,
            idleIcon = idleIcon,
            movingIcon = movingIcon
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp, end = 32.dp, top = 0.dp, bottom = 12.dp)
                .onPreviewKeyEvent { event ->
                    if (event.type == KeyEventType.KeyDown) {
                        val currentIndex = buttons.indexOfFirst { it.id == focusedButtonId }
                        when (event.key) {
                            Key.DirectionRight -> {
                                if (currentIndex < buttons.size - 1) {
                                    val nextButton = buttons[currentIndex + 1]
                                    focusRequesters[nextButton.id]?.requestFocus()
                                    focusedButtonId = nextButton.id
                                    return@onPreviewKeyEvent true
                                }
                            }
                            Key.DirectionLeft -> {
                                if (currentIndex > 0) {
                                    val prevButton = buttons[currentIndex - 1]
                                    focusRequesters[prevButton.id]?.requestFocus()
                                    focusedButtonId = prevButton.id
                                    return@onPreviewKeyEvent true
                                }
                            }
                            Key.DirectionDown -> {
                                if (!fromSeason) {
                                    onOpenRelatedVideo()
                                    return@onPreviewKeyEvent true
                                }
                            }
                        }
                    }
                    false
                },
            verticalAlignment = Alignment.Top
        ) {
            buttons.forEachIndexed { index, button ->
                Button(
                    modifier = Modifier
                        .height(32.dp)
                        .width((button.width ?: 32).dp)
                        .focusRequester(focusRequesters[button.id] ?: FocusRequester())
                        .focusable()
                        .onFocusChanged { if (it.isFocused) focusedButtonId = button.id }
                        .background(
                            color = Color.White.copy(alpha = if (focusedButtonId == button.id) 0.25f else 0f),
                            shape = MaterialTheme.shapes.small
                        ),
                    onClick = button.onClick,
                    contentPadding = PaddingValues(2.dp),
                    colors = ButtonDefaults.colors(containerColor = Color.Transparent)
                ) {
                    if (button.text != null) {
                        Text(
                            text = button.text,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge,
                            color = button.tint,
                            modifier = Modifier.ifElse(button.scale != 1f, Modifier.scale(button.scale))
                        )
                    } else if (button.painterId != null) {
                        Icon(
                            painter = painterResource(id = button.painterId),
                            contentDescription = null,
                            tint = button.tint
                        )
                    } else {
                        button.icon?.let {
                            Icon(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .ifElse(button.scale != 1f, Modifier.scale(button.scale)),
                                imageVector = it,
                                contentDescription = null,
                                tint = button.tint
                            )
                        }
                    }
                }
                if (index < buttons.size - 1) {
                    Spacer(Modifier.width(12.dp))
                }
            }

            Spacer(Modifier.weight(1f))
            Text(
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 0.dp),
                text = "${seekData.position.formatHourMinSec()} / ${seekData.duration.formatHourMinSec()}",
                color = Color.White
            )
        }
    }
    
    SpeedDialog(
        show = showSpeedDialog,
        onHideDialog = { showSpeedDialog = false },
        speed = speed,
        onSpeedChange = {
            speed = it
            onPlaySpeedChange(it)
        }
    )
}

@Composable
private fun SpeedDialog(
    modifier: Modifier = Modifier,
    show: Boolean,
    onHideDialog: () -> Unit,
    speed: Float,
    step: Float = 0.25f,
    min: Float = 0.25f,
    max: Float = 3f,
    onSpeedChange: (Float) -> Unit
) {
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(show) {
        if (show) focusRequester.requestFocus(scope)
    }

    if (show) {
        Dialog(onDismissRequest = { onHideDialog() }) {
            Surface(
                modifier = modifier
                    .width(240.dp),
                color = Color.Black.copy(alpha = 0.5f),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    Text(
                        text = "播放速度",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Column(
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .focusable()
                            .fillMaxWidth()
                            .onPreviewKeyEvent {
                                if (it.key == Key.DirectionUp || it.key == Key.DirectionDown) {
                                    if (it.type == KeyEventType.KeyDown) {
                                        var newValue = if (it.key == Key.DirectionUp)
                                            speed + step
                                        else
                                            speed - step
                                        if (newValue < min) newValue = min
                                        if (newValue > max) newValue = max
                                        onSpeedChange(newValue)
                                    }
                                }
                                false
                            },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Rounded.ArrowDropUp, contentDescription = null, tint = Color.White)
                        Text(text = "${speed}x", color = Color.White)
                        Icon(imageVector = Icons.Rounded.ArrowDropDown, contentDescription = null, tint = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun Clock(
    modifier: Modifier = Modifier,
    hour: Int,
    minute: Int,
    second: Int
) {
    Text(
        modifier = modifier,
        color = Color.White,
        fontWeight = FontWeight.Bold,
        letterSpacing = 2.sp,
        style = TextStyle(
            shadow = Shadow(
                color = Color.Black,
                blurRadius = 4f
            )
        ),
        text = buildAnnotatedString {
            withStyle(SpanStyle(fontSize = 32.sp)) {
                append("$hour".padStart(2, '0'))
                append(":")
                append("$minute".padStart(2, '0'))
            }
            withStyle(SpanStyle(fontSize = 18.sp)) {
                append(":")
                append("$second".padStart(2, '0'))
            }
        }
    )
}

@Preview
@Composable
private fun ClockPreview() {
    val clock = Triple(12, 30, 30)
    MaterialTheme {
        Clock(
            hour = clock.first,
            minute = clock.second,
            second = clock.third
        )
    }
}

@Preview(device = "id:tv_1080p")
@Composable
private fun ControllerVideoInfoPreview() {
    var show by remember { mutableStateOf(true) }

    CompositionLocalProvider(
        LocalVideoPlayerSeekData provides VideoPlayerSeekData(
            duration = 100,
            position = 33,
            bufferedPercentage = 66
        ),
        LocalVideoPlayerVideoInfoData provides VideoPlayerVideoInfoData(
            title = "【A320】民航史上最佳逆袭！A320的前世今生！民航史上最佳逆袭！A320的前世今生！",
            partTitle = "2023车队车手介绍分析预测 2023车队车手介绍分析预测 2023车队车手介绍分析预测",
            upName = "upName",
            play = 1,
            danmaku = 1,
            pubTime = "2025-08-05"
        ),
        LocalVideoPlayerClockData provides VideoPlayerClockData(
            hour = 12,
            minute = 30,
            second = 30
        ),
        LocalVideoPlayerSeekThumbData provides VideoPlayerSeekThumbData(
            idleIcon = "",
            movingIcon = ""
        )
    ) {
        MaterialTheme {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Button(onClick = { show = !show }) {
                    Text(text = "Switch")
                }
            }
            ControllerVideoInfo(
                modifier = Modifier.fillMaxSize(),
                show = show,
                playSpeed = 1.25f,
                onHideInfo = {},
                onPlay = {},
                onPause = {},
                onPlaySpeedChange = {},
                onOpenUpSpace = {},
                onRefreshVideo = {},
                onOpenDanmaku = {},
                onHideDanmaku = {},
                onOpenPlayList = {},
                onOpenRelatedVideo = {},
                onOpenSetting = {},
                onLoopPlayModeChange = {}
            )
        }
    }
}


@Preview
@Composable
private fun SpeedDialogPreview() {
    SpeedDialog(
        speed = 1.25f,
        show = true,
        onHideDialog = {},
        onSpeedChange = {}
    )
}