package dev.aaa1115910.bv.player.mobile.component.controller

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun SeekSlider(
    modifier: Modifier = Modifier,
    currentSeekPosition: Float,
    bufferedSeekPosition: Float,
    onSeekChange: (Float) -> Unit
) {
    val scope = rememberCoroutineScope()
    var isPressing by remember { mutableStateOf(false) }
    var changedSeekPosition by remember { mutableStateOf(0f) }

    Box(
        modifier = modifier
    ) {
        Slider(
            modifier = Modifier.fillMaxWidth(),
            value = bufferedSeekPosition,
            onValueChange = {},
            enabled = false,
            colors = SliderDefaults.colors(
                disabledActiveTrackColor = Color.White.copy(alpha = 0.5f),
                disabledInactiveTrackColor = Color.White.copy(alpha = 0.2f),
                disabledThumbColor = Color.Transparent
            )
        )
        Slider(
            modifier = Modifier.fillMaxWidth(),
            value = if (isPressing) changedSeekPosition else currentSeekPosition,
            onValueChange = {
                changedSeekPosition = it
                isPressing = true
            },
            onValueChangeFinished = {
                onSeekChange(changedSeekPosition)
                //避免松手后进度条闪回当前播放位置
                scope.launch(Dispatchers.IO) {
                    delay(200)
                    isPressing = false
                }
            },
            colors = SliderDefaults.colors(
                inactiveTrackColor = Color.Transparent
            )
        )
    }
}

@Preview
@Composable
private fun SeekSliderPreview() {
    var currentSeekPosition by remember { mutableStateOf(0.3f) }
    val bufferedSeekPosition by remember {
        derivedStateOf {
            val buffered = currentSeekPosition + 0.2f
            if (buffered > 1f) 1f else buffered
        }
    }

    MaterialTheme {
        Surface {
            Box(
                modifier = Modifier.background(Color.Black.copy(0.5f))
            ) {
                SeekSlider(
                    currentSeekPosition = currentSeekPosition,
                    bufferedSeekPosition = bufferedSeekPosition,
                    onSeekChange = { currentSeekPosition = it }
                )
            }
        }
    }
}