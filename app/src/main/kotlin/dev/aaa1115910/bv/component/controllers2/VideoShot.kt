package dev.aaa1115910.bv.component.controllers2

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import dev.aaa1115910.biliapi.entity.video.VideoShot
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.getImage
import io.github.oshai.kotlinlogging.KotlinLogging

@Composable
fun VideoShot(
    modifier: Modifier = Modifier,
    videoShot: VideoShot,
    position: Long,
    duration: Long,
    coercedOffset: Dp = 0.dp
) {
    val view = LocalView.current
    val logger = KotlinLogging.logger {}

    var bitmap by remember { mutableStateOf(ImageBitmap(1, 1)) }
    var screenWidth by remember { mutableStateOf(0.dp) }
    var coercedImageOffset by remember { mutableStateOf(0.dp) }

    LaunchedEffect(position) {
        if (!view.isInEditMode) {
            logger.fInfo { "update progress preview image at $position" }
            bitmap = videoShot.getImage(position.toInt() / 1000).asImageBitmap()
        }
        val baseOffset = (-80).dp
        val imageOffset = baseOffset + screenWidth * (position.toFloat() / duration.toFloat())
        coercedImageOffset =
            imageOffset.coerceIn(0.dp + coercedOffset, screenWidth - 160.dp - coercedOffset)
    }

    BoxWithConstraints(
        modifier = modifier.fillMaxWidth()
    ) {
        screenWidth = maxWidth
        VideoShotImage(
            modifier = Modifier
                .offset(x = coercedImageOffset),
            bitmap = bitmap
        )
    }
}

@Composable
fun VideoShotImage(
    modifier: Modifier = Modifier,
    bitmap: ImageBitmap
) {
    val view = LocalView.current
    Box(
        modifier = modifier
    ) {
        Image(
            modifier = Modifier
                .size(160.dp, 90.dp)
                .clip(MaterialTheme.shapes.large)
                .shadow(4.dp, MaterialTheme.shapes.large),
            bitmap = bitmap,
            contentDescription = null
        )
        if (view.isInEditMode) {
            VerticalDivider(
                modifier = Modifier
                    .height(90.dp)
                    .align(Alignment.Center)
            )
        }
    }

}

@Preview
@Composable
private fun VideoShotImagePreview() {
    BVTheme {
        VideoShotImage(bitmap = ImageBitmap(1, 1))
    }
}

@Preview(device = "id:tv_1080p")
@Composable
private fun VideoShotPreview(@PreviewParameter(VideoShotProgressProvider::class) data: Pair<Long, Long>) {
    BVTheme {
        Column {
            VideoShot(
                videoShot = VideoShot(
                    times = emptyList(),
                    imageCountX = 0,
                    imageCountY = 0,
                    imageWidth = 0,
                    imageHeight = 0,
                    images = emptyList()
                ),
                position = data.second,
                duration = data.first
            )
            VideoProgressSeek(
                duration = data.first,
                position = data.second,
                bufferedPercentage = 1
            )
        }
    }
}

private class VideoShotProgressProvider : PreviewParameterProvider<Pair<Long, Long>> {
    override val values = sequenceOf(
        Pair(1234_000L, 0L),
        Pair(1234_000L, 234_000L),
        Pair(1234_000L, 555_000L),
        Pair(1234_000L, 1234_000L)
    )
}