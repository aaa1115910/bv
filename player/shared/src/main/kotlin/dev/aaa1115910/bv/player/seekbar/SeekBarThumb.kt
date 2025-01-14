package dev.aaa1115910.bv.player.seekbar

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Surface
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun SeekBarThumb(
    modifier: Modifier = Modifier,
    state: SeekMoveState,
    idleJsonUrl: String,
    movingJsonUrl: String,
    size: Dp = 48.dp
) {
    val idleComposition by rememberLottieComposition(
        spec = LottieCompositionSpec.Url(idleJsonUrl)
    )
    val movingComposition by rememberLottieComposition(
        spec = LottieCompositionSpec.Url(movingJsonUrl)
    )

    val idleProgress by animateFloatAsState(
        targetValue = when (state) {
            SeekMoveState.Idle -> 1f
            else -> 0f
        },
        animationSpec = when (state) {
            SeekMoveState.Idle -> tween(800)
            else -> tween(0)
        },
        label = "idle progress"
    )

    val movingProgress by animateFloatAsState(
        targetValue = when (state) {
            SeekMoveState.Backward -> 0f
            SeekMoveState.Forward -> 1f
            else -> 0.5f
        },
        animationSpec = when (state) {
            SeekMoveState.Backward -> tween(800)
            SeekMoveState.Forward -> tween(800)
            else -> tween(0)
        },
        label = "moving progress"
    )

    LottieAnimation(
        modifier = modifier
            .size(size),
        composition = when (state) {
            SeekMoveState.Backward -> movingComposition
            SeekMoveState.Forward -> movingComposition
            SeekMoveState.Idle -> idleComposition
        },
        progress = {
            when (state) {
                SeekMoveState.Backward -> movingProgress
                SeekMoveState.Forward -> movingProgress
                SeekMoveState.Idle -> idleProgress
            }
        }
    )
}

@Preview
@Composable
private fun ProgressSeekThumbPreview() {
    var state by remember { mutableStateOf(SeekMoveState.Idle) }
    Surface {
        Column {
            SeekBarThumb(
                state = state,
                idleJsonUrl = "https://i0.hdslb.com/bfs/garb/item/df917f079cd8175cc851cd1e19a197d810a1c6b7.json",
                movingJsonUrl = "https://i0.hdslb.com/bfs/garb/item/b61bb387a4c895ef165798102ef322c631a9e4e1.json"
            )
            Button(onClick = { state = SeekMoveState.Idle }) { Text(text = "idle") }
            Button(onClick = { state = SeekMoveState.Backward }) { Text(text = "backward") }
            Button(onClick = { state = SeekMoveState.Forward }) { Text(text = "forward") }
        }
    }
}