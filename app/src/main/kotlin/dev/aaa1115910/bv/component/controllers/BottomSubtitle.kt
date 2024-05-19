package dev.aaa1115910.bv.component.controllers

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
import dev.aaa1115910.bv.BuildConfig

@Composable
fun BottomSubtitle(
    modifier: Modifier = Modifier
) {
    val data = LocalVideoPlayerControllerData.current
    val subtitleData = data.currentSubtitleData
    val time = data.currentPosition

    var currentText by remember { mutableStateOf("") }

    val updateCurrentText: () -> Unit = {
        currentText = subtitleData.find { it.isShowing(time) }?.content
            ?: if (BuildConfig.DEBUG) "【DEBUG】无内容" else ""
    }

    LaunchedEffect(time) {
        updateCurrentText()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        if (currentText != "") {
            Text(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = data.currentSubtitleBottomPadding)
                    .clip(MaterialTheme.shapes.small)
                    .background(Color.Black.copy(alpha = 0.4f))
                    .padding(vertical = 4.dp, horizontal = 12.dp),
                text = currentText,
                fontSize = data.currentSubtitleFontSize,
                textAlign = TextAlign.Center
            )
        }
    }
}