package dev.aaa1115910.bv.player.mobile.controller

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FullscreenExit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import dev.aaa1115910.bv.player.mobile.VideoSeekBar
import dev.aaa1115910.bv.player.mobile.noRippleClickable
import dev.aaa1115910.bv.util.formatMinSec
import kotlin.math.roundToInt

@Composable
fun FullscreenControllers(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
    currentTime: Long,
    totalTime: Long,
    currentSeekPosition: Float,
    bufferedSeekPosition: Float,
    currentResolutionName: String,
    enabledDanmaku: Boolean,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onExitFullScreen: () -> Unit,
    onSeekToPosition: (Long) -> Unit,
    onShowResolutionController: () -> Unit,
    onShowSpeedController: () -> Unit,
    onToggleDanmaku: (Boolean) -> Unit,
    onShowDanmakuController: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        TopControllers(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .noRippleClickable { }
        )
        BottomControllers(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .noRippleClickable { },
            isPlaying = isPlaying,
            currentTime = currentTime,
            totalTime = totalTime,
            currentSeekPosition = currentSeekPosition,
            bufferedSeekPosition = bufferedSeekPosition,
            currentResolutionName = currentResolutionName,
            enabledDanmaku = enabledDanmaku,
            onPlay = onPlay,
            onPause = onPause,
            onExitFullScreen = onExitFullScreen,
            onSeekToPosition = onSeekToPosition,
            onShowResolutionController = onShowResolutionController,
            onShowSpeedController = onShowSpeedController,
            onToggleDanmaku = onToggleDanmaku,
            onShowDanmakuController = onShowDanmakuController
        )
    }
}

@Composable
private fun TopControllers(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier.height(60.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(start = 24.dp),
                text = "这是一个标题",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color.White
            )
        }
    }
}

@Composable
private fun BottomControllers(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
    currentTime: Long,
    totalTime: Long,
    currentSeekPosition: Float,
    bufferedSeekPosition: Float,
    currentResolutionName: String,
    enabledDanmaku: Boolean,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onExitFullScreen: () -> Unit,
    onSeekToPosition: (Long) -> Unit,
    onShowResolutionController: () -> Unit,
    onShowSpeedController: () -> Unit,
    onToggleDanmaku: (Boolean) -> Unit,
    onShowDanmakuController: () -> Unit
) {
    Box(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.6f))
    ) {
        Column {
            ConstraintLayout(
                modifier = Modifier
            ) {
                val (positionTimeText, seekSlider, totalTimeText) = createRefs()

                Text(
                    modifier = Modifier
                        .constrainAs(positionTimeText) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        }
                        .width(80.dp),
                    text = currentTime.formatMinSec(),
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                VideoSeekBar(
                    modifier = Modifier.constrainAs(seekSlider) {
                        top.linkTo(parent.top)
                        start.linkTo(positionTimeText.end)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(totalTimeText.start)
                        width = Dimension.preferredWrapContent
                    },
                    duration = totalTime,
                    position = currentTime,
                    bufferedPercentage = (bufferedSeekPosition * 100).roundToInt(),
                    onPositionChange = { newPosition, isPressing ->
                        if (!isPressing) onSeekToPosition(newPosition)
                    }
                )
                Text(
                    modifier = Modifier
                        .constrainAs(totalTimeText) {
                            end.linkTo(parent.end)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        }
                        .width(80.dp),
                    text = totalTime.formatMinSec(),
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }

            ProvideTextStyle(TextStyle(color = Color.White)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row {
                        PlayPauseButton(
                            isPlaying = isPlaying,
                            onPlay = onPlay,
                            onPause = onPause
                        )
                        TextButton(onClick = { onToggleDanmaku(enabledDanmaku) }) {
                            Text(text = "弹幕开关" + if (enabledDanmaku) "✔" else "✖")
                        }
                        TextButton(onClick = onShowDanmakuController) {
                            Text(text = "弹幕设置")
                        }
                    }

                    Row {
                        TextButton(onClick = { /*TODO*/ }) {
                            Text(text = "字幕")
                        }
                        TextButton(onClick = { /*TODO*/ }) {
                            Text(text = "选集")
                        }
                        TextButton(onClick = onShowSpeedController) {
                            Text(text = "倍速")
                        }
                        TextButton(onClick = onShowResolutionController) {
                            Text(text = currentResolutionName)
                        }
                        IconButton(onClick = onExitFullScreen) {
                            Icon(
                                imageVector = Icons.Rounded.FullscreenExit,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(device = "spec:width=1920px,height=1080px")
@Composable
fun FullscreenControllerPreview() {
    MaterialTheme {
        FullscreenControllers(
            isPlaying = true,
            currentTime = 12345,
            totalTime = 123456,
            currentSeekPosition = 0.3f,
            bufferedSeekPosition = 0.6f,
            currentResolutionName = "1080P",
            enabledDanmaku = false,
            onPlay = {},
            onPause = {},
            onExitFullScreen = {},
            onSeekToPosition = {},
            onShowResolutionController = {},
            onShowSpeedController = {},
            onToggleDanmaku = {},
            onShowDanmakuController = {}
        )
    }
}