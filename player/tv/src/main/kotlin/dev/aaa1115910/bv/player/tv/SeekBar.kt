package dev.aaa1115910.bv.player.tv

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import androidx.tv.material3.darkColorScheme
import dev.aaa1115910.bv.player.seekbar.SeekBar
import dev.aaa1115910.bv.player.seekbar.SeekBarThumb
import dev.aaa1115910.bv.player.seekbar.SeekMoveState
import dev.aaa1115910.bv.util.formatHourMinSec
import kotlin.math.max

@Composable
fun VideoSeekBar(
    modifier: Modifier = Modifier,
    duration: Long,
    position: Long,
    bufferedPercentage: Int,
    idleIcon: String = "",
    movingIcon: String = "",
    moveState: SeekMoveState = SeekMoveState.Idle,
    showPosition: Boolean = false
) {
    VideoSeekBar(
        modifier = modifier,
        duration = duration,
        position = position,
        bufferedPercentage = bufferedPercentage,
        showPosition = showPosition,
        thumb = { thumbModifier ->
            SeekBarThumb(
                modifier = thumbModifier,
                state = moveState,
                idleJsonUrl = idleIcon,
                movingJsonUrl = movingIcon
            )
        }
    )
}

@Composable
private fun VideoSeekBar(
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

            SeekBar(
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
                    text = position.formatHourMinSec(),
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

@Preview(device = "id:tv_1080p")
@Composable
private fun SeekWithThumbPreview(@PreviewParameter(ProgressProvider::class) data: Triple<Long, Long, Int>) {
    MaterialTheme(
        colorScheme = darkColorScheme()
    ) {
        Surface {
            VideoSeekBar(
                duration = data.first,
                position = data.second,
                bufferedPercentage = data.third,
                showPosition = true,
                thumb = { modifier ->
                    SeekBarThumb(
                        modifier = modifier,
                        state = SeekMoveState.Idle,
                        idleJsonUrl = "https://i0.hdslb.com/bfs/garb/item/df917f079cd8175cc851cd1e19a197d810a1c6b7.json",
                        movingJsonUrl = "https://i0.hdslb.com/bfs/garb/item/b61bb387a4c895ef165798102ef322c631a9e4e1.json"
                    )
                }
            )
        }

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
