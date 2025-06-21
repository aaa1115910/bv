package dev.aaa1115910.bv.player.mobile.controller

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FullscreenExit
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerConfigData
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerSeekData
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerStateData
import dev.aaa1115910.bv.player.entity.Resolution
import dev.aaa1115910.bv.player.entity.VideoPlayerConfigData
import dev.aaa1115910.bv.player.entity.VideoPlayerSeekData
import dev.aaa1115910.bv.player.entity.VideoPlayerStateData
import dev.aaa1115910.bv.player.mobile.VideoSeekBar
import dev.aaa1115910.bv.player.mobile.noRippleClickable
import dev.aaa1115910.bv.util.formatHourMinSec

@Composable
fun FullscreenControllers(
    modifier: Modifier = Modifier,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onExitFullScreen: () -> Unit,
    onSeekToPosition: (Long) -> Unit,
    onShowResolutionController: () -> Unit,
    onShowSpeedController: () -> Unit,
    onToggleDanmaku: (Boolean) -> Unit,
    onShowDanmakuController: () -> Unit,
    onShowVideoListController: () -> Unit,
    onOpenMoreMenu: () -> Unit
) {
    val context = LocalContext.current
    val videoPlayerSeekData = LocalVideoPlayerSeekData.current
    val videoPlayerStateData = LocalVideoPlayerStateData.current
    val videoPlayerConfigData = LocalVideoPlayerConfigData.current
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        TopControllers(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .noRippleClickable { },
            onOpenMoreMenu = onOpenMoreMenu
        )
        BottomControllers(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .noRippleClickable { },
            isPlaying = videoPlayerStateData.isPlaying,
            currentTime = videoPlayerSeekData.position,
            totalTime = videoPlayerSeekData.duration,
            bufferedSeekPosition = videoPlayerSeekData.bufferedPercentage,
            currentResolutionName = videoPlayerConfigData.currentResolution.getDisplayName(context),
            enabledDanmaku = videoPlayerConfigData.currentDanmakuEnabled,
            showPartButton = videoPlayerConfigData.availableVideoList.size > 1,
            onPlay = onPlay,
            onPause = onPause,
            onExitFullScreen = onExitFullScreen,
            onSeekToPosition = onSeekToPosition,
            onShowResolutionController = onShowResolutionController,
            onShowSpeedController = onShowSpeedController,
            onToggleDanmaku = onToggleDanmaku,
            onShowDanmakuController = onShowDanmakuController,
            onShowVideoListController = onShowVideoListController
        )
    }
}

@Composable
private fun TopControllers(
    modifier: Modifier = Modifier,
    onOpenMoreMenu: () -> Unit
) {
    Box(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier.padding(start = 24.dp),
                text = "这是一个标题",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color.White
            )
            IconButton(
                onClick = onOpenMoreMenu,
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = Color.White
                )
            ) {
                Icon(imageVector = Icons.Rounded.MoreVert, contentDescription = null)
            }
        }
    }
}

@Composable
private fun BottomControllers(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
    currentTime: Long,
    totalTime: Long,
    bufferedSeekPosition: Int,
    currentResolutionName: String,
    enabledDanmaku: Boolean,
    showPartButton: Boolean,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onExitFullScreen: () -> Unit,
    onSeekToPosition: (Long) -> Unit,
    onShowResolutionController: () -> Unit,
    onShowSpeedController: () -> Unit,
    onToggleDanmaku: (Boolean) -> Unit,
    onShowDanmakuController: () -> Unit,
    onShowVideoListController: () -> Unit
) {
    Box(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.6f))
    ) {
        Column {
            ConstraintLayout(
                modifier = Modifier
                    .padding(top = 8.dp)
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
                    text = currentTime.formatHourMinSec(),
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
                    bufferedPercentage = bufferedSeekPosition,
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
                    text = totalTime.formatHourMinSec(),
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
                        if (showPartButton) {
                            TextButton(onClick = onShowVideoListController) {
                                Text(text = "选集")
                            }
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
            FullscreenControllers(
                onPlay = {},
                onPause = {},
                onExitFullScreen = {},
                onSeekToPosition = {},
                onShowResolutionController = {},
                onShowSpeedController = {},
                onToggleDanmaku = {},
                onShowDanmakuController = {},
                onShowVideoListController = {},
                onOpenMoreMenu = {}
            )
        }
    }
}