package dev.aaa1115910.bv.player.mobile

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import dev.aaa1115910.bv.player.seekbar.SeekBar
import dev.aaa1115910.bv.player.seekbar.SeekBarThumb
import dev.aaa1115910.bv.player.seekbar.SeekMoveState
import dev.aaa1115910.bv.util.formatHourMinSec
import kotlinx.coroutines.delay
import kotlin.math.max

@Composable
fun VideoSeekBar(
    modifier: Modifier = Modifier,
    duration: Long,
    position: Long,
    bufferedPercentage: Int,
    colors: SliderColors = SliderDefaults.colors(),
    thumb: (@Composable (Modifier, SeekMoveState?) -> Unit)? = null,
    onPositionChange: ((position: Long, pressing: Boolean) -> Unit)? = null
) {
    val density = LocalDensity.current
    var sliderWidth by remember { mutableStateOf(0.dp) }
    var thumbOffsetX by remember { mutableFloatStateOf(0f) }
    var pressing by remember { mutableStateOf(false) }
    var previewPosition by remember { mutableLongStateOf(0L) }
    var thumbSize by remember { mutableIntStateOf(0) }
    var thumbOffsetXMax by remember { mutableFloatStateOf(1f) }
    val thumbOffsetXMin by remember { mutableFloatStateOf(0f) }
    var seekMoveState by remember { mutableStateOf<SeekMoveState?>(null) }

    val draggableState = rememberDraggableState {
        pressing = true
        thumbOffsetX = (thumbOffsetX + it).coerceIn(thumbOffsetXMin, thumbOffsetXMax)
        val percent = thumbOffsetX / thumbOffsetXMax
        previewPosition = (percent * duration).toLong()
        onPositionChange?.invoke(previewPosition, true)
        if (it > 0) {
            seekMoveState = SeekMoveState.Forward
        } else if (it < 0) {
            seekMoveState = SeekMoveState.Backward
        }
    }

    LaunchedEffect(sliderWidth) {
        thumbOffsetXMax = with(density) { max(1f, (sliderWidth - 32.dp).toPx()) }
    }

    LaunchedEffect(position) {
        if (pressing) return@LaunchedEffect
        val percent = (position.toFloat() / duration.toFloat()).coerceIn(0f, 1f)
        thumbOffsetX = (percent * thumbOffsetXMax).coerceIn(thumbOffsetXMin, thumbOffsetXMax)
    }

    BoxWithConstraints(
        modifier = modifier
            .draggable(
                state = draggableState,
                orientation = Orientation.Horizontal,
                onDragStopped = {
                    seekMoveState = null
                    onPositionChange?.invoke(previewPosition, false)
                    delay(200)
                    pressing = false
                }
            )
            .pointerInput(Unit) {
                detectTapGestures {
                    val xDp = with(density) { it.x.toDp() }
                    val percent =
                        (xDp.coerceIn(16.dp, sliderWidth - 16.dp) - 16.dp) / (sliderWidth - 32.dp)
                    val newPosition = (percent * duration).toLong()
                    onPositionChange?.invoke(newPosition, false)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        sliderWidth = this.maxWidth
        SeekBar(
            modifier = Modifier.padding(horizontal = 16.dp),
            duration = duration,
            position = if (pressing) previewPosition else position,
            bufferedPercentage = bufferedPercentage,
            colors = colors
        )
        Box(modifier = Modifier.fillMaxWidth()) {
            val thumbModifier = Modifier
                .onSizeChanged { thumbSize = it.width }
                .offset { IntOffset(thumbOffsetX.toInt(), 0) }
            thumb?.invoke(thumbModifier, seekMoveState)
        }
    }
}


@Preview(device = "id:tv_1080p")
@Preview
@Composable
private fun DraggableSeekPreview() {
    val view = LocalView.current

    var duration by remember { mutableLongStateOf(100000L) }
    var position by remember { mutableLongStateOf(50000L) }
    var bufferedPercentage by remember { mutableIntStateOf(66) }

    MaterialTheme {
        Column(
            modifier = Modifier.safeContentPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = if (view.isInEditMode) Modifier
                else Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                VideoSeekBar(
                    duration = duration,
                    position = position,
                    bufferedPercentage = bufferedPercentage,
                    thumb = { modifier, seekMoveState ->
                        if (!view.isInEditMode) {
                            SeekBarThumb(
                                modifier = modifier.alpha(0.3f),
                                state = seekMoveState ?: SeekMoveState.Idle,
                                idleJsonUrl = "https://i0.hdslb.com/bfs/garb/item/df917f079cd8175cc851cd1e19a197d810a1c6b7.json",
                                movingJsonUrl = "https://i0.hdslb.com/bfs/garb/item/b61bb387a4c895ef165798102ef322c631a9e4e1.json",
                                size = 32.dp
                            )
                        } else {
                            Box(
                                modifier = modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(999.dp))
                                    .alpha(0.4f)
                                    .background(Color.Blue)
                            ) { }
                        }
                    },
                    onPositionChange = { newPosition, pressing ->
                        if (!pressing) position = newPosition
                    }
                )
            }
            Text("${position.formatHourMinSec()}/${duration.formatHourMinSec()}")
        }
    }
}