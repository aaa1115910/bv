package dev.aaa1115910.bv.player.tv.controller

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerConfigData
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerSeekData

@Composable
fun BottomSubtitle(
    modifier: Modifier = Modifier
) {
    val videoPlayerConfigData = LocalVideoPlayerConfigData.current
    val videoPlayerSeekData = LocalVideoPlayerSeekData.current
    val subtitleData = videoPlayerConfigData.currentSubtitleData
    val time = videoPlayerSeekData.position

    var currentText by remember { mutableStateOf("") }

    val updateCurrentText: () -> Unit = {
        runCatching {
            currentText = subtitleData.find { it.isShowing(time) }?.content ?: ""
        }
    }

    LaunchedEffect(time) {
        updateCurrentText()
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        if (currentText != "") {
            Text(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = videoPlayerConfigData.currentSubtitleBottomPadding)
                    .clip(MaterialTheme.shapes.small)
                    .background(Color.Black.copy(alpha = videoPlayerConfigData.currentSubtitleBackgroundOpacity))
                    .padding(vertical = 4.dp, horizontal = 12.dp),
                text = currentText,
                fontSize = videoPlayerConfigData.currentSubtitleFontSize,
                textAlign = TextAlign.Center
            )
        }
    }
}