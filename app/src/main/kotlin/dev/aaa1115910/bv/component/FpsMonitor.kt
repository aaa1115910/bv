package dev.aaa1115910.bv.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text

@Composable
fun FpsMonitor(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var fpsCount by remember { mutableIntStateOf(0) }
    var fps by remember { mutableIntStateOf(0) }
    var lastUpdate by remember { mutableLongStateOf(0L) }

    LaunchedEffect(Unit) {
        while (true) {
            withFrameMillis { ms ->
                fpsCount++
                if (fpsCount == 5) {
                    fps = (5000 / (ms - lastUpdate)).toInt()
                    lastUpdate = ms
                    fpsCount = 0
                }
            }
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        content()
        Text(
            modifier = modifier
                .size(60.dp)
                .align(Alignment.TopStart),
            text = "Fps: $fps",
            color = Color.Red,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}