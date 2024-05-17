package dev.aaa1115910.bv.component.controllers2

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import dev.aaa1115910.bv.component.controllers.info.VideoPlayerInfoData
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.util.formatMinSec

@Composable
fun SeekController(
    modifier: Modifier = Modifier,
    show: Boolean,
    infoData: VideoPlayerInfoData,
    goTime: Long
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        AnimatedVisibility(
            modifier = Modifier.align(Alignment.BottomCenter),
            visible = show,
            enter = expandVertically(),
            exit = shrinkVertically(),
            label = "SeekControllerVisible"
        ) {
            SeekController(
                duration = infoData.totalDuration,
                position = goTime
            )
        }
    }
}

@Composable
private fun SeekController(
    modifier: Modifier = Modifier,
    duration: Long,
    position: Long
) {
    Box {
        Column(
            modifier = modifier
                .clip(
                    MaterialTheme.shapes.large
                        .copy(bottomStart = CornerSize(0.dp), bottomEnd = CornerSize(0.dp))
                )
                .background(Color.Black.copy(0.5f)),
            verticalArrangement = Arrangement.Bottom
        ) {
            VideoProgressSeek(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .offset(y = 12.dp),
                duration = duration,
                position = position,
                bufferedPercentage = 0
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = (12.5).dp)
                .offset(y = (-6).dp),
            horizontalArrangement = Arrangement.End
        ) {
            CurrentPositionTip(position = position)
            Spacer(
                // 避免在开始播放前，获取到总长度为 0 时导致崩溃
                modifier = Modifier.fillMaxWidth((duration - position) / (duration + 1).toFloat())
            )
        }
    }
}

@Composable
private fun CurrentPositionTip(
    modifier: Modifier = Modifier,
    position: Long
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.offset(y = 8.dp),
            text = position.formatMinSec()
        )
        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
    }
}

@Preview
@Composable
private fun CurrentPositionTipPreview() {
    CurrentPositionTip(position = 234_000)
}

@Preview(device = "id:tv_1080p")
@Composable
private fun VideoProgressSeekPreview(@PreviewParameter(VideoProgressProvider::class) data: Pair<Long, Long>) {
    BVTheme {
        SeekController(
            duration = data.first,
            position = data.second
        )
    }
}

private class VideoProgressProvider : PreviewParameterProvider<Pair<Long, Long>> {
    override val values = sequenceOf(
        Pair(1234_000L, 0L),
        Pair(1234_000L, 234_000L),
        Pair(1234_000L, 555_000L),
        Pair(1234_000L, 1234_000L),
    )
}