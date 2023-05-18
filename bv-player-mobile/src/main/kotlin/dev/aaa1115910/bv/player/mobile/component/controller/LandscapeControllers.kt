package dev.aaa1115910.bv.player.mobile.component.controller

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun LandscapeControllers(
    modifier: Modifier = Modifier,
    onExitFullScreen: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        TopControllers(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
        )
        BottomControllers(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            onExitFullScreen = onExitFullScreen
        )
    }
}

@Composable
private fun TopControllers(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        Row {
            Text(
                text = "这是一个标题",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun BottomControllers(
    modifier: Modifier = Modifier,
    onExitFullScreen: () -> Unit
) {
    Box(
        modifier = modifier
    ) {
        Column {
            Text(text = "进度条")
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier)
                IconButton(onClick = onExitFullScreen) {
                    Icon(imageVector = Icons.Default.AddCircle, contentDescription = null)
                }
            }
        }
    }
}