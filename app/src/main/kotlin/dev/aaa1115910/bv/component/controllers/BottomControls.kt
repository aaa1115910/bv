package dev.aaa1115910.bv.component.controllers

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.aaa1115910.bv.component.formatMinSec

@Composable
fun BottomControls(
    modifier: Modifier = Modifier,
    totalDuration: Long,
    currentTime: Long,
    bufferedPercentage: Int,
    onSeekChanged: (timeMs: Float) -> Unit
) {
    Column(
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    0.0f to Color.Transparent,
                    1.0f to Color.Black.copy(alpha = 0.5f),
                    startY = 0.0f,
                    endY = 80.0f
                )
            )
            .padding(bottom = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .focusable(false),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier)
            Text(
                modifier = Modifier.padding(top = 16.dp, bottom = 0.dp, end = 40.dp),
                text = "${currentTime.formatMinSec()} / ${totalDuration.formatMinSec()}",
                color = Color.White
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Slider(
                modifier = Modifier.fillMaxWidth(),
                value = bufferedPercentage.toFloat(),
                enabled = false,
                onValueChange = { /*do nothing*/ },
                valueRange = 0f..100f,
                colors = SliderDefaults.colors(
                    disabledThumbColor = Color.Transparent,
                    disabledInactiveTrackColor = Color.Transparent,
                    disabledActiveTrackColor = Color.Gray
                )
            )

            Slider(
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                value = currentTime.toFloat(),
                onValueChange = onSeekChanged,
                valueRange = 0f..totalDuration.toFloat(),
                colors = SliderDefaults.colors(
                    disabledThumbColor = Color.Transparent,
                    disabledInactiveTrackColor = Color.Gray.copy(alpha = 0.5f),
                    disabledActiveTrackColor = Color.White
                )
            )
        }
    }
}

@Preview
@Composable
fun BottomControlsPreview() {
    BottomControls(
        totalDuration = 123456,
        currentTime = 23333,
        bufferedPercentage = 68,
        onSeekChanged = {}
    )
}