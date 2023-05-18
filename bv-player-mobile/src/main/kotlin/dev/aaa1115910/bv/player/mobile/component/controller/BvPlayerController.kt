package dev.aaa1115910.bv.player.mobile.component.controller

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.absoluteValue

@Composable
fun BvPlayerController(
    modifier: Modifier = Modifier,
    isLandscape: Boolean,
    onEnterFullScreen: () -> Unit,
    onExitFullScreen: () -> Unit,
    content: @Composable () -> Unit
) {
    var showUi by remember { mutableStateOf(false) }

    val onTap: () -> Unit = {
        Log.i("BvPlayerController", "Left screen tap")
        showUi = !showUi
    }

    val onLongPress: () -> Unit = {
        Log.i("BvPlayerController", "Left screen long press")
    }

    val onHorizontalDrag: (Float) -> Unit = { move ->
        Log.i("BvPlayerController", "Screen horizontal drag: $move")
    }

    Box(
        modifier = modifier
            .background(Color.Black)
    ) {
        content()
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
                        onVerticalDrag = { move ->
                            Log.i("BvPlayerController", "Left screen vertical drag: $move")
                        },
                        onHorizontalDrag = onHorizontalDrag,
                        onDragEnd = { verticalMove, horizontalMove ->
                            Log.i(
                                "BvPlayerController",
                                "Left screen drag end: [x=$verticalMove, y=$horizontalMove]"
                            )
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
                        onVerticalDrag = { move ->
                            Log.i("BvPlayerController", "Right screen vertical drag: $move")
                        },
                        onHorizontalDrag = onHorizontalDrag,
                        onDragEnd = { verticalMove, horizontalMove ->
                            Log.i(
                                "BvPlayerController",
                                "Right screen drag end: [x=$verticalMove, y=$horizontalMove]"
                            )
                        }
                    )
            ) {}
        }

        if (showUi) {
            if (isLandscape) {
                LandscapeControllers(
                    onExitFullScreen = onExitFullScreen
                )
            } else {
                PortraitControllers(
                    onBack = {},
                    onEnterFullScreen = onEnterFullScreen
                )
            }
        }
    }
}

private fun Modifier.detectTapAndDragGestures(
    onTap: () -> Unit,
    onLongPress: () -> Unit,
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
            onLongPress = { onLongPress() }
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