package dev.aaa1115910.bv.player.tv.controller

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerSponsorBlockData
import dev.aaa1115910.bv.player.entity.VideoPlayerSponsorBlockData
import kotlinx.coroutines.delay

@Composable
fun SponsorBlockResultToast(modifier: Modifier = Modifier) {
    val sponsorBlockData = LocalVideoPlayerSponsorBlockData.current

    SponsorBlockResultToastContent(
        modifier = modifier,
        show = sponsorBlockData.showResultToast,
        message = sponsorBlockData.resultToastMessage
    )
}

@Composable
private fun SponsorBlockResultToastContent(
    modifier: Modifier = Modifier,
    show: Boolean,
    message: String
) {
    var resultMessage by remember { mutableStateOf(message) }

    LaunchedEffect(message) {
        // 避免动画过程中提示消失
        if (message.isBlank()) delay(300)
        resultMessage = message
    }

    AnimatedVisibility(
        visible = show,
        enter = fadeIn(animationSpec = tween(0)),
        exit = fadeOut(animationSpec = tween(300))
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
                Text(
                    text = resultMessage,
                    color = Color.White,
                    fontSize = 18.sp,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Preview(device = "id:tv_1080p")
@Composable
private fun SponsorBlockResultToastPreview() {
    CompositionLocalProvider(
        LocalVideoPlayerSponsorBlockData provides VideoPlayerSponsorBlockData(
            showResultToast = true,
            resultToastMessage = "已跳过片头"
        )
    ) {
        MaterialTheme {
            SponsorBlockResultToast()
        }
    }
}