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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.rounded.PlaylistPlay
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.KeyboardDoubleArrowDown
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.ArrowDropUp
import androidx.compose.material.icons.twotone.ScreenRotation
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import dev.aaa1115910.biliapi.entity.video.Subtitle
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerClockState
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerConfigData
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerSeekState
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerSeekThumbData
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerStateData
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerVideoInfoData
import dev.aaa1115910.bv.player.entity.VideoPlayerClockState
import dev.aaa1115910.bv.player.entity.VideoPlayerSeekState
import dev.aaa1115910.bv.player.entity.VideoPlayerSeekThumbData
import dev.aaa1115910.bv.player.entity.VideoPlayerVideoInfoData
import dev.aaa1115910.bv.player.entity.VideoRotation
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
import kotlinx.coroutines.Job
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
    onLoopPlayModeChange: (Boolean) -> Unit,
    onRotationChange: (VideoRotation) -> Unit,
    userActionContent: @Composable (
        modifier: Modifier,
        focusMap: Map<String, FocusRequester>,
        onFocus: (String) -> Unit,
        onPauseAutoHide: (Boolean) -> Unit
    ) -> Unit = { _, _, _, _ -> },
    onSeekBack: () -> Unit,
    onSeekForward: () -> Unit,
    onSubtitleChange: (Subtitle) -> Unit,
) {
    val videoPlayerClockState = LocalVideoPlayerClockState.current
    val videoPlayerSeekState = LocalVideoPlayerSeekState.current
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
                    videoPlayerClockState.hour,
                    videoPlayerClockState.minute,
                    videoPlayerClockState.second
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
                seekData = videoPlayerSeekState,
                title = videoPlayerVideoInfoData.title,
                partTitle = videoPlayerVideoInfoData.partTitle,
                playSpeed = playSpeed,
                rotation = videoPlayerConfigData.currentVideoRotation,
                idleIcon = videoPlayerSeekThumbData.idleIcon,
                movingIcon = videoPlayerSeekThumbData.movingIcon,
                play = videoPlayerVideoInfoData.play,
                danmaku = videoPlayerVideoInfoData.danmaku,
                like = videoPlayerVideoInfoData.like,
                coin = videoPlayerVideoInfoData.coin,
                favorite = videoPlayerVideoInfoData.favorite,
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
                onRotationChange = onRotationChange,
                fromSeason = videoPlayerVideoInfoData.fromSeason,
                userActionContent = userActionContent,
                onSeekBack = onSeekBack,
                onSeekForward = onSeekForward,
                availableSubtitleTracks = videoPlayerConfigData.availableSubtitleTracks,
                currentSubtitleId = videoPlayerConfigData.currentSubtitleId,
                onSubtitleChange = { id ->
                    val track = videoPlayerConfigData.availableSubtitleTracks.firstOrNull { it.id == id }
                    track?.let { onSubtitleChange(it) }
                },
                isFollowingUp = videoPlayerVideoInfoData.isFollowingUp
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
    rotation: VideoRotation,
    title: String,
    partTitle: String,
    seekData: VideoPlayerSeekState,
    idleIcon: String,
    movingIcon: String,
    play: Long,
    danmaku: Int,
    like: Int,
    coin: Int,
    favorite: Int,
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
    onRotationChange: (VideoRotation) -> Unit,
    fromSeason: Boolean = false,
    isFollowingUp: Boolean = false,
    userActionContent: @Composable (
        modifier: Modifier,
        focusMap: Map<String, FocusRequester>,
        onFocus: (String) -> Unit,
        onPauseAutoHide: (Boolean) -> Unit
    ) -> Unit = { _, _, _, _ -> },
    onSeekBack: () -> Unit,
    onSeekForward: () -> Unit,
    availableSubtitleTracks: List<Subtitle> = emptyList(),
    currentSubtitleId: Long,
    onSubtitleChange: (Long) -> Unit
) {
    val scope = rememberCoroutineScope()
    var hideVideoInfoJob by remember { mutableStateOf<Job?>(null) }
    var pauseAutoHide by remember { mutableStateOf(false) }
    var showSpeedDialog by remember { mutableStateOf(false) }
    var showRotationDialog by remember { mutableStateOf(false) }
    var showSubtitleDialog by remember { mutableStateOf(false) }
    var speed by remember { mutableFloatStateOf(playSpeed) }
    val danmakuIconId = if (showDanmaku) R.drawable.ic_danmaku_on else R.drawable.ic_danmaku_hide
    val subtitleIconId = if (currentSubtitleId > -1) R.drawable.ic_subtitle_on else R.drawable.ic_subtitle_off
    val upSpaceIconId = if (isFollowingUp) R.drawable.person_following else R.drawable.person
    val buttons = remember(fromSeason, showDanmaku, isPlaying, isLoop, speed, rotation, currentSubtitleId, isFollowingUp) {
        listOf(
            ControlButton(
                id = "speed",
                text = formatSpeed(speed),
                onClick = { showSpeedDialog = true },
                width = 46
            ),
            ControlButton(
                id = "upSpace",
                painterId = upSpaceIconId,
                scale = 0.72f,
                onClick = onOpenUpSpace,
                visible = !fromSeason
            ),
            ControlButton(
                id = "rotation",
                icon = Icons.TwoTone.ScreenRotation,
                onClick = { showRotationDialog = true },
                scale = 0.75f
            ),
            ControlButton(
                id = "refresh",
                icon = Icons.Rounded.Refresh,
                onClick = onRefreshVideo
            ),
            ControlButton(
                id = "subtitle",
                painterId = subtitleIconId,
                scale = 0.97f,
                onClick = { showSubtitleDialog = true },
                visible = availableSubtitleTracks.count() > 1
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

    val focusRequesters = remember(buttons) {
        buttons.associate { button ->
            button.id to FocusRequester()
        }.toMutableMap()
    }

    // user action focus requesters (由 Controller 提供给调用方)。创建默认的三项：like/fav/coin
    val userActionFocusRequesters = remember {
        mutableStateOf(
            mapOf(
                "like" to FocusRequester(),
                "fav" to FocusRequester(),
                "coin" to FocusRequester()
            )
        )
    }

    val seekbarFocusRequester = remember { FocusRequester() }
    var seekbarHasFocus by remember { mutableStateOf(false) }

    val statString by remember {
        mutableStateOf(
            if (upName.isNotEmpty()) {
                "$upName  ·  ${
                    if (play >= 10000) String.format("%.1f", play / 10000.0) + " 万" else "$play "
                }播放  ·  ${
                    if (danmaku >= 10000) String.format("%.1f", danmaku / 10000.0) + "万" else "$danmaku "
                }弹幕  ·  ${
                    if (like >= 10000) String.format("%.1f", like / 10000.0) + "万" else "$like "
                }点赞  ·  ${
                    if (favorite >= 10000) String.format("%.1f", favorite / 10000.0) + "万" else "$favorite "
                }收藏  ·  ${
                    if (coin >= 10000) String.format("%.1f", coin / 10000.0) + "万" else "$coin "
                }投币  ·  发布于 $pubTime"
            } else ""
        )
    }

    LaunchedEffect(show) {
        if (show) {
            // 初始聚焦 进度条
            seekbarFocusRequester.requestFocus()
        }
    }

    fun cancelHideJob() {
        hideVideoInfoJob?.cancel()
        hideVideoInfoJob = null
    }

    fun scheduleHideJob() {
        cancelHideJob()
        if (show && !showSpeedDialog && !showRotationDialog && !showSubtitleDialog && !pauseAutoHide) {
            hideVideoInfoJob = scope.launch {
                delay(5000)
                withContext(Dispatchers.Main) { onHideInfo() }
            }
        }
    }

    LaunchedEffect(show, showSpeedDialog, showRotationDialog, showSubtitleDialog, pauseAutoHide) {
        scheduleHideJob()
    }

    Column(
        modifier = modifier
            .onPreviewKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown) {
                    scheduleHideJob()
                }
                false
            }
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
        if (upName.isNotEmpty()) {
            Text(
                modifier = Modifier
                    .padding(start = 32.dp, end = 32.dp, top = 8.dp, bottom = 0.dp)
                    .fillMaxWidth(),
                text = statString,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        // 当前注入的是：点赞、收藏、投币
        userActionContent(
            Modifier.focusProperties {
                down = seekbarFocusRequester
            },
            userActionFocusRequesters.value,
            { id ->
                // 当用户 action 获得焦点时，设置当前聚焦 id 并重置自动隐藏计时
                scheduleHideJob()
            },
            { pause ->
                pauseAutoHide = pause
                if (pause) cancelHideJob() else scheduleHideJob()
            }
        )
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(48.dp)
//                .padding(start = 32.dp, end = 32.dp, top = 14.dp, bottom = 6.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Icon(
//                imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
//                contentDescription = null,
//                tint = Color.White.copy(alpha = 0.5f)
//            )
        VideoSeekBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 30.dp, end = 30.dp, bottom = 2.dp)
                .focusRequester(seekbarFocusRequester)
                .onFocusChanged {
                    scheduleHideJob()
                    seekbarHasFocus = it.isFocused
                }
                .focusProperties {
                    up = userActionFocusRequesters.value["like"] ?: FocusRequester()
                    down = focusRequesters["speed"] ?: FocusRequester()
                }
                .focusable()
                .onPreviewKeyEvent {
                    if (seekbarHasFocus && it.type == KeyEventType.KeyDown) {
                        when (it.key) {
                            Key.DirectionLeft -> onSeekBack()
                            Key.DirectionRight -> onSeekForward()
                            Key.Enter -> if (isPlaying) onPause() else onPlay()
                            Key.DirectionCenter -> if (isPlaying) onPause() else onPlay()
                        }
                    }
                    false
                },
            duration = seekData.duration,
            position = seekData.position,
            bufferedPercentage = seekData.bufferedPercentage,
            moveState = SeekMoveState.Idle,
            idleIcon = idleIcon,
            movingIcon = movingIcon,
            isFocused = seekbarHasFocus
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp, end = 32.dp, top = 0.dp, bottom = 10.dp)
                .focusProperties {
                    up = seekbarFocusRequester
                }
                .onPreviewKeyEvent { event ->
                    if (event.type == KeyEventType.KeyDown) {
                        if (!fromSeason && event.key == Key.DirectionDown) {
                            onOpenRelatedVideo()
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
                        .focusRequester(focusRequesters[button.id] ?: FocusRequester()),
                    onClick = button.onClick,
                    shape = ButtonDefaults.shape(shape = RoundedCornerShape(8.dp)),
                    contentPadding = PaddingValues(2.dp),
                    colors = ButtonDefaults.colors(
                        containerColor = Color.Transparent,
                        focusedContainerColor = Color.White.copy(alpha = 0.3f)
                    )
                ) {
                    if (button.text != null) {
                        Text(
                            text = button.text,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge,
                            color = button.tint,
                            modifier = Modifier.ifElse(
                                button.scale != 1f,
                                Modifier.scale(button.scale)
                            )
                        )
                    } else if (button.painterId != null) {
                        Icon(
                            modifier = Modifier
                                .ifElse(button.scale != 1f, Modifier.scale(button.scale)),
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

    if (showSpeedDialog) {
        SpeedDialog(
            onHideDialog = { showSpeedDialog = false },
            speed = speed,
            onSpeedChange = {
                speed = it
                onPlaySpeedChange(it)
            }
        )
    }

    if (showRotationDialog) {
        RotationDialog(
            onHideDialog = { showRotationDialog = false },
            rotation = rotation,
            onRotationChange = onRotationChange
        )
    }

    if (showSubtitleDialog) {
        val currentSubtitle = availableSubtitleTracks.firstOrNull { it.id == currentSubtitleId }
        if (currentSubtitle != null) {
            SubtitleDialog(
                onHideDialog = { showSubtitleDialog = false },
                subtitle = currentSubtitle,
                availableSubtitleTracks = availableSubtitleTracks,
                onSubtitleChange = { subtitle ->
                    onSubtitleChange(subtitle.id)
                }
            )
        }
    }
}

@Composable
private fun SpeedDialog(
    modifier: Modifier = Modifier,
    onHideDialog: () -> Unit,
    speed: Float,
    step: Float = 0.25f,
    min: Float = 0.25f,
    max: Float = 3f,
    onSpeedChange: (Float) -> Unit
) {
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus(scope)
    }

    Dialog(onDismissRequest = { onHideDialog() }) {
        Surface(
            modifier = modifier
                .width(240.dp),
            color = Color.Black.copy(alpha = 0.5f),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text(
                    text = "播放速度",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 18.sp
                )

                Column(
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .focusable()
                        .fillMaxWidth()
                        .onPreviewKeyEvent {
                            if (it.key == Key.DirectionUp || it.key == Key.DirectionDown || it.key == Key.DirectionLeft || it.key == Key.DirectionRight) {
                                if (it.type == KeyEventType.KeyDown) {
                                    var newValue = if (it.key == Key.DirectionUp || it.key == Key.DirectionRight)
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
                    Icon(
                        imageVector = Icons.Rounded.ArrowDropUp,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Text(text = "${speed}x", color = Color.White, fontSize = 16.sp)
                    Icon(
                        imageVector = Icons.Rounded.ArrowDropDown,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun RotationDialog(
    modifier: Modifier = Modifier,
    rotation: VideoRotation,
    onHideDialog: () -> Unit,
    onRotationChange: (VideoRotation) -> Unit
) {
    val scope = rememberCoroutineScope()
    val options = remember { VideoRotation.entries }
    val context = LocalContext.current
    val focusRequesters = remember { options.associateWith { FocusRequester() } }

    LaunchedEffect(rotation) {
        focusRequesters[rotation]?.requestFocus(scope)
    }

    Dialog(onDismissRequest = { onHideDialog() }) {
        Surface(
            modifier = modifier
                .width(240.dp),
            color = Color.Black.copy(alpha = 0.5f),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text(
                    text = stringResource(R.string.video_player_menu_picture_rotation),
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 18.sp
                )

                Column {
                    options.forEach { option ->
                        val selected = option == rotation
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, start = 8.dp, end = 8.dp)
                                .focusRequester(focusRequesters[option]!!),
                            shape = ButtonDefaults.shape(MaterialTheme.shapes.medium),
                            scale = ButtonDefaults.scale(focusedScale = 1f),
                            colors = ButtonDefaults.colors(
                                containerColor = if (selected) MaterialTheme.colorScheme.inverseSurface.copy(
                                    alpha = 0.4f
                                ) else Color.Transparent,
                                contentColor = Color.White,
                                focusedContainerColor = MaterialTheme.colorScheme.inverseSurface,
                                focusedContentColor = Color.Black
                            ),
                            onClick = { onRotationChange(option) }
                        ) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = option.getDisplayName(context),
                                textAlign = TextAlign.Center,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SubtitleDialog(
    modifier: Modifier = Modifier,
    subtitle: Subtitle,
    availableSubtitleTracks: List<Subtitle>,
    onHideDialog: () -> Unit,
    onSubtitleChange: (Subtitle) -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val focusRequesters = remember { availableSubtitleTracks.map { it.id }.associateWith { FocusRequester() } }

    LaunchedEffect(subtitle) {
        focusRequesters[subtitle.id]?.requestFocus(scope)
    }

    Dialog(onDismissRequest = { onHideDialog() }) {
        Surface(
            modifier = modifier
                .width(240.dp),
            color = Color.Black.copy(alpha = 0.5f),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text(
                    text = stringResource(R.string.video_player_menu_subtitle_switch),
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 18.sp
                )

                Column {
                    availableSubtitleTracks.forEach { option ->
                        val selected = option == subtitle
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, start = 8.dp, end = 8.dp)
                                .focusRequester(focusRequesters[option.id]!!),
                            shape = ButtonDefaults.shape(MaterialTheme.shapes.medium),
                            scale = ButtonDefaults.scale(focusedScale = 1f),
                            colors = ButtonDefaults.colors(
                                containerColor = if (selected) MaterialTheme.colorScheme.inverseSurface.copy(
                                    alpha = 0.4f
                                ) else Color.Transparent,
                                contentColor = Color.White,
                                focusedContainerColor = MaterialTheme.colorScheme.inverseSurface,
                                focusedContentColor = Color.Black
                            ),
                            onClick = { onSubtitleChange(option) }
                        ) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = option.langDoc,
                                textAlign = TextAlign.Center,
                                fontSize = 16.sp
                            )
                        }
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

    val clockState = VideoPlayerClockState(hour = 12, minute = 30, second = 30)
    CompositionLocalProvider(
        LocalVideoPlayerVideoInfoData provides VideoPlayerVideoInfoData(
            title = "【A320】民航史上最佳逆袭！A320的前世今生！民航史上最佳逆袭！A320的前世今生！",
            partTitle = "2023车队车手介绍分析预测 2023车队车手介绍分析预测 2023车队车手介绍分析预测",
            upName = "upName",
            play = 1,
            danmaku = 1,
            pubTime = "2025-08-05"
        ),
        LocalVideoPlayerClockState provides clockState,
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
                onLoopPlayModeChange = {},
                onRotationChange = {},
                userActionContent = { _, _, _, _ ->
                    // User action buttons go here
                },
                onSeekBack = {},
                onSeekForward = {},
                onSubtitleChange = {}
            )
        }
    }
}


@Preview
@Composable
private fun SpeedDialogPreview() {
    SpeedDialog(
        speed = 1.25f,
        onHideDialog = {},
        onSpeedChange = {}
    )
}

@Preview
@Composable
private fun RotationDialogPreview() {
    RotationDialog(
        rotation = VideoRotation.Rotate90,
        onHideDialog = {},
        onRotationChange = {}
    )
}