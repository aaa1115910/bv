package dev.aaa1115910.bv.player.tv.controller

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import androidx.tv.material3.Text
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerHistoryData
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerStateData
import dev.aaa1115910.bv.util.formatHourMinSec

// TODO 跳转历史记录
@Composable
fun BackToHistoryTip(
    modifier: Modifier = Modifier,
    show: Boolean,
    time: String
) {
    SkipTip(
        modifier = modifier,
        show = show,
        text = "上次看到 $time 点击确认键跳转"
    )
}

// TODO 跳过片头
@Composable
fun SkipOpTip(
    modifier: Modifier = Modifier,
    show: Boolean
) {
    SkipTip(
        modifier = modifier,
        show = show,
        text = "跳过片头"
    )
}

// TODO 跳过片尾
@Composable
fun SkipEdTip(
    modifier: Modifier = Modifier,
    show: Boolean
) {
    SkipTip(
        modifier = modifier,
        show = show,
        text = "跳过片尾"
    )
}

@Composable
fun SkipTip(
    modifier: Modifier = Modifier,
    show: Boolean,
    text: String
) {
    AnimatedVisibility(
        visible = show,
        enter = expandHorizontally(),
        exit = shrinkHorizontally()
    ) {
        Box(
            modifier = modifier.fillMaxSize()
        ) {
            Surface(
                modifier = modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = 32.dp),
                colors = SurfaceDefaults.colors(
                    containerColor = Color.Black.copy(alpha = 0.6f)
                ),
                shape = MaterialTheme.shapes.medium.copy(
                    topStart = CornerSize(0.dp), bottomStart = CornerSize(0.dp)
                )
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = text,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}

@Composable
fun SkipTips(
    modifier: Modifier = Modifier,
    showSkipOp: Boolean = false,
    showSkipEd: Boolean = false,
) {
    val videoPlayerHistoryData = LocalVideoPlayerHistoryData.current
    val videoPlayerStateData = LocalVideoPlayerStateData.current

    Box(modifier = modifier.fillMaxSize()) {
        BackToHistoryTip(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 32.dp),
            show = videoPlayerStateData.showBackToHistory,
            time = videoPlayerHistoryData.lastPlayed.toLong().formatHourMinSec()
        )
    }
}