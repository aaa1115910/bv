package dev.aaa1115910.bv.player.seekbar

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SeekBar(
    modifier: Modifier = Modifier,
    duration: Long,
    position: Long,
    bufferedPercentage: Int,
    colors: SliderColors = SliderDefaults.colors(),
) {
    val trackWidth = 10.dp
    val density = LocalDensity.current
    val strokeWidthPx = with(density) { trackWidth.roundToPx().toFloat() }
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(trackWidth)
    ) {
        drawLine(
            color = colors.inactiveTrackColor,
            start = Offset(0f, center.y),
            end = Offset(size.width - 0f, center.y),
            strokeWidth = strokeWidthPx,
            cap = StrokeCap.Round
        )
        drawLine(
            color = colors.disabledInactiveTrackColor,
            start = Offset(0f, center.y),
            end = Offset(size.width * bufferedPercentage / 100, center.y),
            strokeWidth = strokeWidthPx,
            cap = StrokeCap.Round
        )
        drawLine(
            color = colors.activeTrackColor,
            start = Offset(0f, center.y),
            end = Offset(size.width * (position / duration.toFloat()), center.y),
            strokeWidth = strokeWidthPx,
            cap = StrokeCap.Round
        )
    }
}

@Preview
@Composable
private fun SeekPreview() {
    MaterialTheme {
        SeekBar(
            modifier = Modifier.padding(horizontal = 16.dp),
            duration = 1000,
            position = 300,
            bufferedPercentage = 50
        )
    }
}