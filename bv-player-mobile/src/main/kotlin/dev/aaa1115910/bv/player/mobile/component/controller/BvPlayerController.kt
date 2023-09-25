package dev.aaa1115910.bv.player.mobile.component.controller

import android.app.Activity
import android.content.Context
import android.media.AudioManager
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import dev.aaa1115910.bv.player.mobile.component.controller.menu.ResolutionMenuController
import dev.aaa1115910.bv.player.mobile.component.controller.menu.SpeedMenuController
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@Composable
fun BvPlayerController(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
    isFullScreen: Boolean,
    currentTime: Long,
    totalTime: Long,
    currentSeekPosition: Float,
    bufferedSeekPosition: Float,
    currentResolutionCode: Int,
    availableResolutionMap: Map<Int, String>,
    currentSpeed: Float,
    onEnterFullScreen: () -> Unit,
    onExitFullScreen: () -> Unit,
    onBack: () -> Unit,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onSeekToPosition: (Long) -> Unit,
    onChangeResolution: (Int) -> Unit,
    onChangeSpeed: (Float) -> Unit,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    var showBaseUi by remember { mutableStateOf(false) }

    var showResolutionController by remember { mutableStateOf(false) }
    var showSpeedController by remember { mutableStateOf(false) }

    //在手势触发的事件中，直接读取 isPlaying currentTime 参数都只会读取到错误的值，原因未知
    var isPlaying2 by remember { mutableStateOf(isPlaying) }
    LaunchedEffect(isPlaying) { isPlaying2 = isPlaying }
    var currentTime2 by remember { mutableStateOf(currentTime) }
    LaunchedEffect(currentTime) { currentTime2 = currentTime }


    var isMovingSeek by remember { mutableStateOf(false) }
    var moveStartTime by remember { mutableStateOf(0L) }
    var moveMs by remember { mutableStateOf(0L) }

    var isMovingBrightness by remember { mutableStateOf(false) }
    var moveStartBrightness by remember { mutableStateOf(0f) }
    var currentBrightnessProgress by remember { mutableStateOf(0f) }

    var isMovingVolume by remember { mutableStateOf(false) }
    var moveStartVolume by remember { mutableStateOf(0) }
    var currentVolumeProgress by remember { mutableStateOf(0f) }

    val onTap: () -> Unit = {
        Log.i("BvPlayerController", "Screen tap")
        if (showResolutionController) {
            showResolutionController = false
        } else if (showSpeedController) {
            showSpeedController = false
        } else {
            showBaseUi = !showBaseUi
        }
    }

    val onLongPress: () -> Unit = {
        Log.i("BvPlayerController", "Screen long press")
    }

    val onDoubleTap: () -> Unit = {
        Log.i("BvPlayerController", "Screen double tap, isPlaying: $isPlaying")
        if (isPlaying2) onPause() else onPlay()
    }

    val onHorizontalDrag: (Float) -> Unit = { move ->
        Log.i("BvPlayerController", "Screen horizontal drag: $move")
        isMovingSeek = true
        moveMs = move.toLong() * 50
    }

    val onMovingBrightness: (Float) -> Unit = { move ->
        Log.i("BvPlayerController", "Left screen vertical drag: $move")
        val window = (context as Activity).window
        if (isMovingBrightness.not()) {
            val oldBrightness = Settings.System.getInt(
                context.contentResolver,
                Settings.System.SCREEN_BRIGHTNESS
            ) / 255f
            //moveStartBrightness = window.attributes.screenBrightness
            moveStartBrightness = oldBrightness
            isMovingBrightness = true
        }
        val newBrightness = (moveStartBrightness - move / 600).coerceIn(0f, 1f)
        Log.i("BvPlayerController", "Brightness: $moveStartBrightness -> $newBrightness")
        val attr = window.attributes
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
        if (isMovingSeek) moveStartTime = currentTime
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
            totalTime = totalTime
        )
        BrightnessTip(show = isMovingBrightness, progress = currentBrightnessProgress)
        VolumeTip(show = isMovingVolume, progress = currentVolumeProgress)

        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .detectTapAndDragGestures(
                        onTap = onTap,
                        onLongPress = onLongPress,
                        onDoubleTap = onDoubleTap,
                        onVerticalDrag = onMovingBrightness,
                        onHorizontalDrag = onHorizontalDrag,
                        onDragEnd = { verticalMove, horizontalMove ->
                            Log.i(
                                "BvPlayerController",
                                "Left screen drag end: [x=$verticalMove, y=$horizontalMove]"
                            )
                            if (verticalMove != 0f) {
                                isMovingBrightness = false
                            } else {
                                isMovingSeek = false
                                val seekMoveMs = horizontalMove.toLong() * 50
                                onSeekToPosition(moveStartTime + seekMoveMs)
                                Log.i("BvPlayerController", "Seek move $seekMoveMs")
                            }
                        }
                    )
            ) {}
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .detectTapAndDragGestures(
                        onTap = onTap,
                        onLongPress = onLongPress,
                        onDoubleTap = onDoubleTap,
                        onVerticalDrag = onMovingVolume,
                        onHorizontalDrag = onHorizontalDrag,
                        onDragEnd = { verticalMove, horizontalMove ->
                            Log.i(
                                "BvPlayerController",
                                "Right screen drag end: [x=$verticalMove, y=$horizontalMove]"
                            )
                            if (verticalMove != 0f) {
                                isMovingVolume = false
                            } else {
                                isMovingSeek = false
                                val seekMoveMs = horizontalMove.toLong() * 50
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
                    isPlaying = isPlaying,
                    currentTime = currentTime,
                    totalTime = totalTime,
                    currentSeekPosition = currentSeekPosition,
                    bufferedSeekPosition = bufferedSeekPosition,
                    currentResolutionName = availableResolutionMap[currentResolutionCode]
                        ?: "Unknown",
                    onPlay = onPlay,
                    onPause = onPause,
                    onExitFullScreen = onExitFullScreen,
                    onSeekToPosition = onSeekToPosition,
                    onShowResolutionController = {
                        showBaseUi = false
                        showResolutionController = true
                    },
                    onShowSpeedController = {
                        showBaseUi = false
                        showSpeedController = true
                    }
                )
            } else {
                MiniControllers(
                    isPlaying = isPlaying,
                    currentTime = currentTime,
                    totalTime = totalTime,
                    currentSeekPosition = currentSeekPosition,
                    bufferedSeekPosition = bufferedSeekPosition,
                    onBack = onBack,
                    onPlay = onPlay,
                    onPause = onPause,
                    onEnterFullScreen = onEnterFullScreen,
                    onSeekToPosition = onSeekToPosition
                )
            }
        }

        ResolutionMenuController(
            show = showResolutionController,
            currentResolutionCode = currentResolutionCode,
            availableResolutionMap = availableResolutionMap,
            onHideController = { showResolutionController = false },
            onClickResolution = { code ->
                onChangeResolution(code)
                showResolutionController = false
            }
        )

        SpeedMenuController(
            show = showSpeedController,
            currentSpeed = currentSpeed,
            onHideController = { showSpeedController = false },
            onClickSpeed = { speed ->
                onChangeSpeed(speed)
                showSpeedController = false
            }
        )
    }
}

private fun Modifier.detectTapAndDragGestures(
    onTap: () -> Unit,
    onLongPress: () -> Unit,
    onDoubleTap: () -> Unit,
    onVerticalDrag: (move: Float) -> Unit,
    onHorizontalDrag: (move: Float) -> Unit,
    onDragEnd: (verticalMove: Float, horizontalMove: Float) -> Unit,
): Modifier = composed {
    var determinedDirection by remember { mutableStateOf(false) }
    var isHorizontal by remember { mutableStateOf(false) }

    var horizontalPointMove by remember { mutableStateOf(0f) }
    var verticalPointMove by remember { mutableStateOf(0f) }

    pointerInput(Unit) {
        detectTapGestures(
            onTap = { onTap() },
            onLongPress = { onLongPress() },
            onDoubleTap = { onDoubleTap() }
        )
    }
        .pointerInput(Unit) {
            detectDragGestures(
                onDragStart = {},
                onDragEnd = {
                    if (isHorizontal) {
                        onDragEnd(0f, horizontalPointMove)
                    } else {
                        onDragEnd(verticalPointMove, 0f)
                    }
                    horizontalPointMove = 0f
                    verticalPointMove = 0f
                    determinedDirection = false
                }
            ) { _, dragAmount ->
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
                        onHorizontalDrag(horizontalPointMove)
                    } else {
                        onVerticalDrag(verticalPointMove)
                    }
                }
            }
        }
}