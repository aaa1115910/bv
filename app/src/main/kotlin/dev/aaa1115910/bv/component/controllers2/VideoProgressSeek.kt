package dev.aaa1115910.bv.component.controllers2

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import dev.aaa1115910.bv.ui.theme.BVTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoProgressSeek(
    modifier: Modifier = Modifier,
    duration: Long,
    position: Long,
    bufferedPercentage: Int
) {
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        Slider(
            modifier = Modifier.fillMaxWidth(),
            value = bufferedPercentage.toFloat(),
            enabled = false,
            onValueChange = {},
            valueRange = 0f..100f,
            colors = SliderDefaults.colors(
                disabledInactiveTrackColor = Color.Transparent,
                disabledActiveTrackColor = Color.Gray
            ),
            thumb = {}
        )

        Slider(
            modifier = Modifier.fillMaxWidth(),
            enabled = false,
            value = position.toFloat(),
            onValueChange = {},
            valueRange = 0f..duration.toFloat(),
            colors = SliderDefaults.colors(
                disabledInactiveTrackColor = Color.Gray.copy(alpha = 0.5f),
                disabledActiveTrackColor = Color.White
            ),
            thumb = {}
        )
    }
}

@Preview
@Composable
private fun VideoProgressSliderPreview() {
    BVTheme {
        VideoProgressSeek(
            duration = 100,
            position = 33,
            bufferedPercentage = 66
        )
    }
}
