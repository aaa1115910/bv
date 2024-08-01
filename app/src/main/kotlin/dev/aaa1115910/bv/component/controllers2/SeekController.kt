package dev.aaa1115910.bv.component.controllers2

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import dev.aaa1115910.biliapi.entity.video.VideoShot
import dev.aaa1115910.bv.component.controllers.LocalVideoPlayerControllerData
import dev.aaa1115910.bv.component.controllers.info.VideoPlayerInfoData
import dev.aaa1115910.bv.ui.theme.BVTheme

@Composable
fun SeekController(
    modifier: Modifier = Modifier,
    show: Boolean,
    infoData: VideoPlayerInfoData,
    goTime: Long,
    moveState: SeekMoveState,
    idleIcon: String,
    movingIcon: String
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
                position = goTime,
                moveState = moveState,
                idleIcon = idleIcon,
                movingIcon = movingIcon,
                videoShot = LocalVideoPlayerControllerData.current.videoShot
            )
        }
    }
}

@Composable
private fun SeekController(
    modifier: Modifier = Modifier,
    duration: Long,
    position: Long,
    moveState: SeekMoveState,
    idleIcon: String,
    movingIcon: String,
    videoShot: VideoShot? = null
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (videoShot != null) {
            VideoShot(
                modifier = Modifier
                    .padding(horizontal = 48.dp),
                videoShot = videoShot,
                position = position,
                duration = duration,
                coercedOffset = (-24).dp
            )
        }

        Column(
            modifier = Modifier
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
                    .padding(top = 8.dp),
                duration = duration,
                position = position,
                bufferedPercentage = 1,
                moveState = moveState,
                idleIcon = idleIcon,
                movingIcon = movingIcon,
                showPosition = true
            )
        }
    }
}

@Preview(device = "id:tv_1080p")
@Composable
private fun VideoProgressSeekPreview(@PreviewParameter(VideoProgressProvider::class) data: Pair<Long, Long>) {
    BVTheme {
        SeekController(
            duration = data.first,
            position = data.second,
            moveState = SeekMoveState.Idle,
            idleIcon = "",
            movingIcon = "",
            videoShot = VideoShot(
                times = emptyList(),
                imageCountX = 0,
                imageCountY = 0,
                imageWidth = 0,
                imageHeight = 0,
                images = emptyList()
            )
        )
    }
}

private class VideoProgressProvider : PreviewParameterProvider<Pair<Long, Long>> {
    override val values = sequenceOf(
        Pair(1234_000L, 0L),
        Pair(1234_000L, 234_000L),
        Pair(1234_000L, 555_000L),
        Pair(1234_000L, 999_000L),
        Pair(1234_000L, 1234_000L),
    )
}