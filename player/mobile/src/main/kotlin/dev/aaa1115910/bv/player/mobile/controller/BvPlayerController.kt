package dev.aaa1115910.bv.player.mobile.controller

import android.app.Activity
import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import dev.aaa1115910.bv.player.entity.Audio
import dev.aaa1115910.bv.player.entity.DanmakuType
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerConfigData
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerSeekData
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerStateData
import dev.aaa1115910.bv.player.entity.Resolution
import dev.aaa1115910.bv.player.entity.VideoCodec
import dev.aaa1115910.bv.player.entity.VideoListItem
import dev.aaa1115910.bv.player.entity.VideoPlayerConfigData
import dev.aaa1115910.bv.player.entity.VideoPlayerSeekData
import dev.aaa1115910.bv.player.entity.VideoPlayerStateData
import dev.aaa1115910.bv.player.mobile.MaterialDarkTheme
import dev.aaa1115910.bv.player.mobile.controller.menu.DanmakuMenu
import dev.aaa1115910.bv.player.mobile.controller.menu.DashMenu
import dev.aaa1115910.bv.player.mobile.controller.menu.MoreMenu
import dev.aaa1115910.bv.player.mobile.controller.menu.SpeedMenu
import dev.aaa1115910.bv.player.mobile.controller.menu.VideoListMenu
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@Composable
fun BvPlayerController(
    modifier: Modifier = Modifier,
    isFullScreen: Boolean,
    onEnterFullScreen: () -> Unit,
    onExitFullScreen: () -> Unit,
    onBack: () -> Unit,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onSeekToPosition: (Long) -> Unit,
    onChangeResolution: (Resolution) -> Unit,
    onChangeVideoCodec: (VideoCodec) -> Unit,
    onChangeAudio: (Audio) -> Unit,
    onChangeSpeed: (Float) -> Unit,
    onToggleDanmaku: (Boolean) -> Unit,
    onEnabledDanmakuTypesChange: (List<DanmakuType>) -> Unit,
    onDanmakuOpacityChange: (Float) -> Unit,
    onDanmakuScaleChange: (Float) -> Unit,
    onDanmakuAreaChange: (Float) -> Unit,
    onPlayNewVideo: (VideoListItem) -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val view = LocalView.current

    //TODO 临时解决方案，应该根据手机垂直方向来屏幕宽度
    //val screenHeight = with(density) { context.resources.displayMetrics.heightPixels.toDp() }
    val screenWidth = with(density) { context.resources.displayMetrics.widthPixels.toDp() }
    val screenHeight = with(density) { context.resources.displayMetrics.heightPixels.toDp() }

    var isMenuOpen by remember { mutableStateOf(false) }
    val videoContentWidth by animateFloatAsState(
        targetValue = if (isMenuOpen) 0.7f else 1f
    )
    val settingsContentOffset by remember(screenHeight, screenWidth) {
        derivedStateOf {
            // 不知为何在预览时获取到的宽度有问题
            if (view.isInEditMode) max(screenWidth, screenHeight) * ((videoContentWidth) - 0.7f)
            else screenWidth * (videoContentWidth - 0.7f)
        }
    }
    var menuType by remember { mutableStateOf(MenuType.None) }

    val openMenu: (menu: MenuType) -> Unit = { menu ->
        println("open menu: $menu")
        menuType = menu
        isMenuOpen = true
    }

    LaunchedEffect(isFullScreen) {
        if (!isFullScreen) isMenuOpen = false
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(videoContentWidth)
        ) {
            BvPlayerControllerVideoContent(
                modifier = Modifier.fillMaxSize(),
                isMenuOpen = isMenuOpen,
                isFullScreen = isFullScreen,
                onEnterFullScreen = onEnterFullScreen,
                onExitFullScreen = onExitFullScreen,
                onBack = onBack,
                onPlay = onPlay,
                onPause = onPause,
                onSeekToPosition = onSeekToPosition,
                onChangeSpeed = onChangeSpeed,
                onToggleDanmaku = onToggleDanmaku,
                onOpenMoreMenu = { openMenu(MenuType.More) },
                onOpenSpeedMenu = { openMenu(MenuType.Speed) },
                onOpenResolutionMenu = { openMenu(MenuType.Resolution) },
                onOpenDanmakuMenu = { openMenu(MenuType.Danmaku) },
                onOpenListMenu = { openMenu(MenuType.List) },
                onCloseMenu = { isMenuOpen = false }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(0.dp))
                ) {
                    content()
                }
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .fillMaxWidth(0.3f)
                .offset {
                    IntOffset(
                        x = with(density) { settingsContentOffset.roundToPx() },
                        y = 0
                    )
                }
                .background(Color.Black)
        ) {
            BvPlayerControllerSettings(
                modifier = Modifier.fillMaxSize(),
                menuType = menuType,
                onCloseMenu = { isMenuOpen = false },
                onChangeResolution = onChangeResolution,
                onChangeVideoCodec = onChangeVideoCodec,
                onChangeAudio = onChangeAudio,
                onChangeSpeed = onChangeSpeed,
                onEnabledDanmakuTypesChange = onEnabledDanmakuTypesChange,
                onDanmakuOpacityChange = onDanmakuOpacityChange,
                onDanmakuScaleChange = onDanmakuScaleChange,
                onDanmakuAreaChange = onDanmakuAreaChange,
                onPlayNewVideo = onPlayNewVideo
            )
        }
    }
}

private enum class MenuType {
    None, Speed, Resolution, Danmaku, List, Subtitle, More
}

@Composable
private fun BvPlayerControllerSettings(
    modifier: Modifier = Modifier,
    menuType: MenuType,
    onCloseMenu: () -> Unit,
    onChangeResolution: (Resolution) -> Unit,
    onChangeVideoCodec: (VideoCodec) -> Unit,
    onChangeAudio: (Audio) -> Unit,
    onChangeSpeed: (Float) -> Unit,
    onEnabledDanmakuTypesChange: (List<DanmakuType>) -> Unit,
    onDanmakuOpacityChange: (Float) -> Unit,
    onDanmakuScaleChange: (Float) -> Unit,
    onDanmakuAreaChange: (Float) -> Unit,
    onPlayNewVideo: (VideoListItem) -> Unit
) {
    MaterialDarkTheme {
        Box(
            modifier = modifier
        ) {
            when (menuType) {
                MenuType.None -> {

                }

                MenuType.Speed -> {
                    SpeedMenu(
                        onClickSpeed = onChangeSpeed,
                        onClose = onCloseMenu
                    )
                }

                MenuType.Resolution -> {
                    DashMenu(
                        onChangeResolution = onChangeResolution,
                        onChangeVideoCodec = onChangeVideoCodec,
                        onChangeAudio = onChangeAudio,
                        onClose = onCloseMenu
                    )
                }

                MenuType.Danmaku -> {
                    DanmakuMenu(
                        onEnabledDanmakuTypeChange = onEnabledDanmakuTypesChange,
                        onDanmakuScaleChange = onDanmakuScaleChange,
                        onDanmakuOpacityChange = onDanmakuOpacityChange,
                        onDanmakuAreaChange = onDanmakuAreaChange,
                        onClose = onCloseMenu
                    )
                }

                MenuType.List -> {
                    VideoListMenu(
                        onClickVideoListItem = onPlayNewVideo,
                        onClose = onCloseMenu
                    )
                }

                MenuType.Subtitle -> {

                }

                MenuType.More -> {
                    MoreMenu(
                        onClose = onCloseMenu
                    )
                }
            }
        }
    }
}

@Composable
fun BvPlayerControllerVideoContent(
    modifier: Modifier = Modifier,
    isMenuOpen: Boolean,
    isFullScreen: Boolean,
    onEnterFullScreen: () -> Unit,
    onExitFullScreen: () -> Unit,
    onBack: () -> Unit,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onSeekToPosition: (Long) -> Unit,
    onChangeSpeed: (Float) -> Unit,
    onToggleDanmaku: (Boolean) -> Unit,
    onOpenMoreMenu: () -> Unit,
    onOpenSpeedMenu: () -> Unit,
    onOpenResolutionMenu: () -> Unit,
    onOpenDanmakuMenu: () -> Unit,
    onOpenListMenu: () -> Unit,
    onCloseMenu: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val videoPlayerSeekData = LocalVideoPlayerSeekData.current
    val videoPlayerStateData = LocalVideoPlayerStateData.current
    val videoPlayerConfigData = LocalVideoPlayerConfigData.current
    var showBaseUi by remember { mutableStateOf(false) }
    val isMenuOpen by rememberUpdatedState(isMenuOpen)

    //在手势触发的事件中，直接读取 isPlaying currentTime 参数都只会读取到错误的值，原因未知
    var isPlaying by remember { mutableStateOf(videoPlayerStateData.isPlaying) }
    LaunchedEffect(videoPlayerStateData.isPlaying) { isPlaying = videoPlayerStateData.isPlaying }
    var currentTime by remember { mutableStateOf(videoPlayerSeekData.position) }
    LaunchedEffect(videoPlayerSeekData.position) { currentTime = videoPlayerSeekData.position }

    var is2xPlaying by remember { mutableStateOf(false) }
    var isMovingSeek by remember { mutableStateOf(false) }
    var moveStartTime by remember { mutableStateOf(0L) }
    var moveMs by remember { mutableStateOf(0L) }
    var moveStartInSafetyArea by remember { mutableStateOf(false) }

    var isMovingBrightness by remember { mutableStateOf(false) }
    var movedBrightness by remember { mutableStateOf(false) }
    var moveStartBrightness by remember { mutableStateOf(0f) }
    var currentBrightnessProgress by remember { mutableStateOf(0f) }

    var isMovingVolume by remember { mutableStateOf(false) }
    var moveStartVolume by remember { mutableStateOf(0) }
    var currentVolumeProgress by remember { mutableStateOf(0f) }

    val onTap: () -> Unit = {
        Log.i("BvPlayerController", "Screen tap")
        if (isMenuOpen) {
            onCloseMenu()
        } else {
            if (!is2xPlaying) showBaseUi = !showBaseUi
        }
    }

    val onLongPress: () -> Unit = {
        Log.i("BvPlayerController", "Screen long press")
        is2xPlaying = true
        onChangeSpeed(2f)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val vibrator = context.getSystemService(Vibrator::class.java)
            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
        }
    }

    val onLongPressEnd: (speed: Float) -> Unit = { oldSpeed ->
        Log.i("BvPlayerController", "Screen long press end")
        is2xPlaying = false
        onChangeSpeed(oldSpeed)
    }

    val onDoubleTap: () -> Unit = {
        Log.i(
            "BvPlayerController",
            "Screen double tap, isPlaying: $isPlaying"
        )
        if (isPlaying) onPause() else onPlay()
    }

    val onHorizontalDrag: (Float) -> Unit = { move ->
        Log.i("BvPlayerController", "Screen horizontal drag: $move")
        isMovingSeek = true
        moveMs = move.toLong() * 50
    }

    val onMovingBrightness: (Float) -> Unit = { move ->
        Log.i("BvPlayerController", "Left screen vertical drag: $move")
        val window = (context as Activity).window
        val attr = window.attributes
        if (isMovingBrightness.not()) {
            // Settings.System.SCREEN_BRIGHTNESS [0, 255]
            // attr.screenBrightness [0, 1f]
            val oldBrightness = if (movedBrightness) attr.screenBrightness else {
                Settings.System.getInt(
                    context.contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS
                ) / 255f
            }
            moveStartBrightness = oldBrightness
            isMovingBrightness = true
            movedBrightness = true
        }
        val newBrightness = (moveStartBrightness - move / 600).coerceIn(0f, 1f)
        Log.i("BvPlayerController", "Brightness: $moveStartBrightness -> $newBrightness")
        attr.screenBrightness = newBrightness
        window.attributes = attr
        //window.attributes.screenBrightness = newBrightness
        currentBrightnessProgress = newBrightness
    }

    val onMovingVolume: (Float) -> Unit = { move ->
        Log.i("BvPlayerController", "Right screen vertical drag: $move")
        val audioManager =
            (context as Activity).getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        if (isMovingVolume.not()) {
            moveStartVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            isMovingVolume = true
        }
        val step = maxVolume.toFloat() / 600f
        val newVolume = ((moveStartVolume - move * step).roundToInt()).coerceIn(0, maxVolume)
        Log.i("BvPlayerController", "Volume: $moveStartVolume -> $newVolume")
        currentVolumeProgress = newVolume / maxVolume.toFloat()
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0)
    }

    LaunchedEffect(isMovingSeek) {
        if (isMovingSeek) moveStartTime = videoPlayerSeekData.position
    }

    LaunchedEffect(is2xPlaying) {
        if (is2xPlaying) showBaseUi = false
    }

    Box(
        modifier = modifier
            .background(Color.Black)
    ) {
        content()

        SeekMoveTip(
            show = isMovingSeek,
            startTime = moveStartTime,
            move = moveMs,
            totalTime = videoPlayerSeekData.duration
        )
        BrightnessTip(show = isMovingBrightness, progress = currentBrightnessProgress)
        VolumeTip(show = isMovingVolume, progress = currentVolumeProgress)
        QuickDoubleSpeedPlaybackTip(show = is2xPlaying)

        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .detectPlayerGestures(
                        enableSafetyArea = isFullScreen,
                        currentSpeed = videoPlayerConfigData.currentVideoSpeed,
                        onTap = onTap,
                        onLongPress = onLongPress,
                        onLongPressEnd = onLongPressEnd,
                        onDoubleTap = onDoubleTap,
                        onVolumeDrag = onMovingVolume,
                        onBrightnessDrag = onMovingBrightness,
                        onSeekDrag = onHorizontalDrag,
                        onDragEnd = { volumeMove, brightnessMove, seekMove ->
                            Log.i(
                                "BvPlayerController",
                                "screen drag end: [volume=$volumeMove, brightness=$brightnessMove, seek=$seekMove]"
                            )
                            if (volumeMove != 0f) {
                                isMovingVolume = false
                                Log.i("BvPlayerController", "Stop move volume")
                            } else if (brightnessMove != 0f) {
                                isMovingBrightness = false
                                Log.i("BvPlayerController", "Stop move brightness")
                            } else {
                                isMovingSeek = false
                                if (moveStartInSafetyArea) {
                                    moveStartInSafetyArea = false
                                    return@detectPlayerGestures
                                }
                                val seekMoveMs = seekMove.toLong() * 50
                                onSeekToPosition(moveStartTime + seekMoveMs)
                                Log.i("BvPlayerController", "Seek move $seekMoveMs")
                            }
                        }
                    )
            ) {}
        }

        if (showBaseUi) {
            if (isFullScreen) {
                FullscreenControllers(
                    onPlay = onPlay,
                    onPause = onPause,
                    onExitFullScreen = onExitFullScreen,
                    onSeekToPosition = onSeekToPosition,
                    onShowResolutionController = {
                        showBaseUi = false
                        onOpenResolutionMenu()
                    },
                    onShowSpeedController = {
                        showBaseUi = false
                        onOpenSpeedMenu()
                    },
                    onToggleDanmaku = onToggleDanmaku,
                    onShowDanmakuController = {
                        showBaseUi = false
                        onOpenDanmakuMenu()
                    },
                    onShowVideoListController = {
                        showBaseUi = false
                        onOpenListMenu()
                    },
                    onOpenMoreMenu = onOpenMoreMenu
                )
            } else {
                MiniControllers(
                    onBack = onBack,
                    onPlay = onPlay,
                    onPause = onPause,
                    onEnterFullScreen = onEnterFullScreen,
                    onSeekToPosition = onSeekToPosition
                )
            }
        }
    }
}

@Composable
private fun BvPlayerControllerSettingsContent(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val context = LocalContext.current
    val colorScheme =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) dynamicDarkColorScheme(context) else darkColorScheme()

    MaterialTheme(colorScheme = colorScheme) {
        Surface(
            modifier = modifier,
            shape = MaterialTheme.shapes.medium.copy(
                topEnd = CornerSize(0),
                bottomEnd = CornerSize(0)
            ),
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                content()
            }
        }
    }
}

fun Modifier.detectPlayerGestures(
    enableSafetyArea: Boolean,
    currentSpeed: Float,
    onTap: () -> Unit,
    onLongPress: () -> Unit,
    onLongPressEnd: (speed: Float) -> Unit,
    onDoubleTap: () -> Unit,
    onVolumeDrag: (move: Float) -> Unit,
    onBrightnessDrag: (move: Float) -> Unit,
    onSeekDrag: (move: Float) -> Unit,
    onDragEnd: (volumeMove: Float, brightnessMove: Float, seekMove: Float) -> Unit,
): Modifier = composed {
    val currentSpeedState = rememberUpdatedState(currentSpeed)
    var oldPlaySpeed by remember { mutableFloatStateOf(1f) }
    var componentWidth by remember { mutableIntStateOf(0) }
    var componentHeight by remember { mutableIntStateOf(0) }
    val horizontalSafetyArea = 0.1f
    val verticalSafetyArea = 0.2f
    var determinedDirection by remember { mutableStateOf(false) }
    var isHorizontal by remember { mutableStateOf(false) }
    var horizontalPointMove by remember { mutableFloatStateOf(0f) }
    var verticalPointMove by remember { mutableFloatStateOf(0f) }
    var inSafetyArea by remember { mutableStateOf(false) }
    var longPressing by remember { mutableStateOf(false) }
    var isMovingVolume by remember { mutableStateOf(false) }
    var isMovingBrightness by remember { mutableStateOf(false) }

    pointerInput(Unit) {
        detectTapGestures(
            onTap = {
                if (longPressing) return@detectTapGestures
                onTap()
            },
            onLongPress = {
                onLongPress()
                oldPlaySpeed = currentSpeedState.value
                longPressing = true
            },
            onDoubleTap = {
                if (longPressing) return@detectTapGestures
                onDoubleTap()
            },
            onPress = {
                tryAwaitRelease()
                if (longPressing) onLongPressEnd(oldPlaySpeed)
                longPressing = false
            }
        )
    }
        .onSizeChanged { size ->
            componentWidth = size.width
            componentHeight = size.height
        }
        .pointerInput(enableSafetyArea) {
            if (longPressing) return@pointerInput

            detectDragGestures(
                onDragStart = {
                    println("Drag start: $it, safety x range: (${componentWidth * horizontalSafetyArea}, ${componentWidth * (1 - horizontalSafetyArea)}), safety y range: (${componentHeight * verticalSafetyArea}, ${componentHeight * (1 - verticalSafetyArea)})")
                    val inHorizontalSafetyArea =
                        it.x > componentWidth * horizontalSafetyArea * 0.5f && it.x < componentWidth * (1 - horizontalSafetyArea * 0.5f)
                    val inVerticalSafetyArea =
                        it.y > componentHeight * verticalSafetyArea * 0.5f && it.y < componentHeight * (1 - verticalSafetyArea * 0.5f)
                    inSafetyArea =
                        inHorizontalSafetyArea && inVerticalSafetyArea || !enableSafetyArea
                    if (!inSafetyArea) return@detectDragGestures

                    if (it.x < componentWidth * 0.5f) {
                        isMovingBrightness = true
                    } else if (it.x >= componentWidth * 0.5f) {
                        isMovingVolume = true
                    }
                },
                onDragEnd = {
                    if (!inSafetyArea) return@detectDragGestures

                    if (isHorizontal) {
                        onDragEnd(0f, 0f, horizontalPointMove)
                    } else {
                        if (isMovingVolume) {
                            onDragEnd(verticalPointMove, 0f, 0f)
                        } else if (isMovingBrightness) {
                            onDragEnd(0f, verticalPointMove, 0f)
                        }
                    }

                    horizontalPointMove = 0f
                    verticalPointMove = 0f
                    determinedDirection = false
                    isMovingVolume = false
                    isMovingBrightness = false
                }
            ) { _, dragAmount ->
                if (!inSafetyArea) return@detectDragGestures
                horizontalPointMove += dragAmount.x
                verticalPointMove += dragAmount.y
                if (!determinedDirection) {
                    if (horizontalPointMove.absoluteValue > 20f) {
                        determinedDirection = true
                        isHorizontal = true
                    } else if (verticalPointMove.absoluteValue > 20f) {
                        determinedDirection = true
                        isHorizontal = false
                    }
                }
                if (determinedDirection) {
                    if (isHorizontal) {
                        onSeekDrag(horizontalPointMove)
                    } else {
                        if (isMovingVolume) {
                            onVolumeDrag(verticalPointMove)
                        } else if (isMovingBrightness) {
                            onBrightnessDrag(verticalPointMove)
                        }
                    }
                }
            }
        }
}

@Preview(device = "spec:width=1920px,height=1080px")
@Composable
private fun BvPlayerControllerPreview() {
    var isFullScreen by remember { mutableStateOf(false) }

    MaterialTheme {
        CompositionLocalProvider(
            LocalVideoPlayerSeekData provides VideoPlayerSeekData(
                duration = 123456,
                position = 12345,
                bufferedPercentage = 60
            ),
            LocalVideoPlayerStateData provides VideoPlayerStateData(
                isPlaying = true,
            ),
            LocalVideoPlayerConfigData provides VideoPlayerConfigData(
                currentResolution = Resolution.R1080P,
                currentDanmakuEnabled = false
            )
        ) {
            BvPlayerController(
                isFullScreen = isFullScreen,
                onEnterFullScreen = { isFullScreen = true },
                onExitFullScreen = { isFullScreen = false },
                onBack = {},
                onPlay = {},
                onPause = {},
                onSeekToPosition = {},
                onChangeResolution = {},
                onChangeVideoCodec = {},
                onChangeAudio = {},
                onChangeSpeed = {},
                onToggleDanmaku = {},
                onEnabledDanmakuTypesChange = {},
                onDanmakuOpacityChange = {},
                onDanmakuAreaChange = {},
                onDanmakuScaleChange = {},
                onPlayNewVideo = {}
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                ) {}
            }
        }
    }
}