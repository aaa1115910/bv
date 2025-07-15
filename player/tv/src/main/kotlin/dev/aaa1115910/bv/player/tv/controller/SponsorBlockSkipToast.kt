package dev.aaa1115910.bv.player.tv.controller

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerSponsorBlockData
import dev.aaa1115910.bv.player.entity.SkipToastType
import dev.aaa1115910.bv.player.entity.VideoPlayerSponsorBlockData
import kotlinx.coroutines.delay

@Composable
fun SponsorBlockSkipToast(
    modifier: Modifier = Modifier,
) {
    val sponsorBlockData = LocalVideoPlayerSponsorBlockData.current

    val animatedConfirmProgress by animateFloatAsState(
        targetValue = sponsorBlockData.skipToastConfirmProgress,
        label = "confirm progress"
    )

    SponsorBlockSkipToastContent(
        modifier = modifier,
        show = sponsorBlockData.showSkipToast,
        message = sponsorBlockData.skipToastMessage,
        isManualSkip = sponsorBlockData.skipToastType == SkipToastType.MANUAL_SKIP,
        confirmProgress = animatedConfirmProgress
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SponsorBlockSkipToastContent(
    modifier: Modifier = Modifier,
    show: Boolean,
    message: String,
    isManualSkip: Boolean = false,
    confirmProgress: Float
) {
    val showProgressIndicator by remember(confirmProgress) { derivedStateOf { confirmProgress > 0f } }

    AnimatedVisibility(
        visible = show,
        enter = fadeIn(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(0)),
        label = "skip toast visibility"
    ) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomEnd
        ) {
            Box(
                modifier = Modifier
                    .padding(bottom = 80.dp, end = 16.dp)
                    .clip(MaterialTheme.shapes.large)
                    .background(Color.Black.copy(alpha = 0.8f))
                    .padding(horizontal = 32.dp, vertical = 20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AnimatedVisibility(
                        visible = showProgressIndicator,
                        label = "prefix icon visibility",
                    ) {
                        AnimatedContent(
                            targetState = confirmProgress == 1f,
                            transitionSpec = { scaleIn() togetherWith scaleOut() },
                            label = "prefix icon animation"
                        ) { isConfirmed ->
                            if (isConfirmed) {
                                Icon(
                                    modifier = Modifier
                                        .padding(end = 12.dp)
                                        .size(42.dp),
                                    imageVector = Icons.Default.CheckCircleOutline,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            } else {
                                CircularWavyProgressIndicator(
                                    modifier = Modifier.padding(end = 12.dp),
                                    progress = { confirmProgress }
                                )
                            }
                        }
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isManualSkip) "可跳过$message" else "即将跳过$message",
                            color = Color.White,
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = if (confirmProgress != 1f) {
                                if (isManualSkip) "长按下方向键执行跳过" else "长按下方向键取消跳过"
                            } else {
                                if (isManualSkip) "即将跳过" else "已取消自动跳过"
                            },
                            color = Color.White.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

private class SponsorBlockSkipToastPreviewParameterProvider :
    PreviewParameterProvider<VideoPlayerSponsorBlockData> {
    override val values = sequenceOf(
        VideoPlayerSponsorBlockData(
            skipToastType = SkipToastType.AUTO_SKIP,
            showSkipToast = true,
            skipToastConfirmProgress = 0.4f
        ),

        VideoPlayerSponsorBlockData(
            skipToastType = SkipToastType.MANUAL_SKIP,
            showSkipToast = true,
            skipToastConfirmProgress = 0.4f
        )
    )
}

@Preview(device = "id:tv_1080p")
@Composable
private fun SponsorBlockSkipToastPreview(
    @PreviewParameter(SponsorBlockSkipToastPreviewParameterProvider::class) videoPlayerSponsorBlockData: VideoPlayerSponsorBlockData
) {
    MaterialTheme {
        CompositionLocalProvider(
            LocalVideoPlayerSponsorBlockData provides videoPlayerSponsorBlockData
        ) {
            SponsorBlockSkipToast()
        }
    }
}

@Preview(device = "id:tv_1080p")
@Composable
private fun SponsorBlockSkipToastAnimatePreview() {
    var progress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            if (progress < 1f) {
                progress += 0.1f
                if (progress >= 1f) progress = 1f
            }
            delay(300)
        }
    }

    MaterialTheme {
        CompositionLocalProvider(
            LocalVideoPlayerSponsorBlockData provides VideoPlayerSponsorBlockData(
                showSkipToast = true,
                skipToastConfirmProgress = progress
            )
        ) {
            SponsorBlockSkipToast()
            androidx.compose.material3.Button(
                onClick = {
                    progress = 0f
                }
            ) {
                Text(text = "reset progress")
            }
        }
    }
}
