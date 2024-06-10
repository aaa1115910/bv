package dev.aaa1115910.bv.component.controllers2

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.tv.material3.Text
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.util.formatMinSec
import kotlin.math.max

@Composable
fun VideoProgressSeek(
    modifier: Modifier = Modifier,
    duration: Long,
    position: Long,
    bufferedPercentage: Int,
    idleIcon: String = "",
    movingIcon: String = "",
    moveState: SeekMoveState = SeekMoveState.Idle,
    showPosition: Boolean = false
) {
    Seek(
        modifier = modifier,
        duration = duration,
        position = position,
        bufferedPercentage = bufferedPercentage,
        showPosition = showPosition,
        thumb = { thumbModifier ->
            ProgressSeekThumb(
                modifier = thumbModifier,
                state = moveState,
                idleJsonUrl = idleIcon,
                movingJsonUrl = movingIcon
            )
        }
    )
}

@Composable
private fun Seek(
    modifier: Modifier = Modifier,
    duration: Long,
    position: Long,
    bufferedPercentage: Int,
    colors: SliderColors = SliderDefaults.colors(),
    showPosition: Boolean = false,
    thumb: (@Composable (Modifier) -> Unit)? = null
) {
    BoxWithConstraints(
        modifier = modifier
    ) {
        val width = this.maxWidth

        ConstraintLayout(
            modifier = Modifier.fillMaxWidth()
        ) {
            val (positionText, seek, thumbIcon) = createRefs()

            Seek(
                modifier = Modifier
                    .constrainAs(seek) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom, 16.dp)
                    }
                    .padding(horizontal = 16.dp),
                duration = duration,
                position = position,
                bufferedPercentage = bufferedPercentage,
                colors = colors
            )
            thumb?.invoke(
                Modifier
                    .constrainAs(thumbIcon) {
                        start.linkTo(
                            parent.start,
                            (width - 48.dp) * (position / max(duration.toFloat(), 1f))
                        )
                        bottom.linkTo(seek.bottom)
                        top.linkTo(seek.top)
                    }
            )
            if (showPosition) {
                Text(
                    text = position.formatMinSec(),
                    modifier = Modifier.constrainAs(positionText) {
                        start.linkTo(thumbIcon.start)
                        end.linkTo(thumbIcon.end)
                        bottom.linkTo(thumbIcon.top)
                    }
                )
            }
        }
    }


}

@Composable
private fun Seek(
    modifier: Modifier = Modifier,
    duration: Long,
    position: Long,
    bufferedPercentage: Int,
    colors: SliderColors = SliderDefaults.colors(),
) {
    val trackWidth = 32f
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(trackWidth.dp)
    ) {
        drawLine(
            color = colors.inactiveTrackColor,
            start = Offset(trackWidth / 2, center.y),
            end = Offset(size.width - trackWidth / 2, center.y),
            strokeWidth = trackWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = colors.disabledActiveTrackColor,
            start = Offset(trackWidth / 2, center.y),
            end = Offset(size.width * bufferedPercentage / 100, center.y),
            strokeWidth = trackWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = colors.activeTrackColor,
            start = Offset(trackWidth / 2, center.y),
            end = Offset(size.width * (position / duration.toFloat()), center.y),
            strokeWidth = trackWidth,
            cap = StrokeCap.Round
        )
    }
}

@Preview(device = "id:tv_1080p")
@Composable
private fun SeekPreview() {
    BVTheme {
        Seek(
            duration = 1000,
            position = 300,
            bufferedPercentage = 50
        )
    }
}

@Preview(device = "id:tv_1080p")
@Composable
private fun SeekWithThumbPreview(@PreviewParameter(ProgressProvider::class) data: Triple<Long, Long, Int>) {
    BVTheme {
        Seek(
            duration = data.first,
            position = data.second,
            bufferedPercentage = data.third,
            showPosition = true,
            thumb = { modifier ->
                ProgressSeekThumb(
                    modifier = modifier,
                    state = SeekMoveState.Idle,
                    idleJsonUrl = "https://i0.hdslb.com/bfs/garb/item/df917f079cd8175cc851cd1e19a197d810a1c6b7.json",
                    movingJsonUrl = "https://i0.hdslb.com/bfs/garb/item/b61bb387a4c895ef165798102ef322c631a9e4e1.json"
                )
            }
        )
    }
}

private class ProgressProvider : PreviewParameterProvider<Triple<Long, Long, Int>> {
    override val values = sequenceOf(
        Triple(1234_000L, 0L, 3),
        Triple(1234_000L, 234_000L, 24),
        Triple(1234_000L, 555_000L, 57),
        Triple(1234_000L, 1234_000L, 100)
    )
}