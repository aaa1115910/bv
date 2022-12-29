package dev.aaa1115910.bv.component.controllers

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.aaa1115910.bv.component.controllers.info.VideoPlayerInfoData
import dev.aaa1115910.bv.util.formatMinSec

@Composable
fun BottomControls(
    modifier: Modifier = Modifier,
    partTitle: String,
    infoData: VideoPlayerInfoData
) {
    Column(
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    0.0f to Color.Transparent,
                    0.1f to Color.Black.copy(0.05f),
                    0.2f to Color.Black.copy(alpha = 0.2f),
                    0.5f to Color.Black.copy(alpha = 0.47f),
                    0.8f to Color.Black.copy(alpha = 0.7f),
                    1f to Color.Black.copy(alpha = 0.8f),
                    startY = 0.0f,
                    endY = 400.0f
                )
            )
            .height(140.dp)
            .padding(bottom = 12.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .focusable(false),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                modifier = Modifier
                    .padding(horizontal = 28.dp)
                    .fillMaxWidth(0.5f),
                text = partTitle,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                modifier = Modifier.padding(top = 16.dp, bottom = 0.dp, end = 40.dp),
                text = "${infoData.currentTime.formatMinSec()} / ${infoData.totalDuration.formatMinSec()}",
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
                value = infoData.bufferedPercentage.toFloat(),
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
                value = infoData.currentTime.toFloat(),
                onValueChange = {},
                valueRange = 0f..infoData.totalDuration.toFloat(),
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
    Surface {
        Column {
            TopController(
                title = "This is a video title"
            )
            Spacer(modifier = Modifier.height(20.dp))
            BottomControls(
                partTitle = "This is P1 This is P1 This is P1 This is P1 This is P1",
                infoData = VideoPlayerInfoData(
                    totalDuration = 34567,
                    currentTime = 12345,
                    bufferedPercentage = 80,
                    resolutionHeight = 0,
                    resolutionWidth = 0,
                    codec = ""
                )
            )
        }
    }
}