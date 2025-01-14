package dev.aaa1115910.bv.player.mobile.controller

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun PlayPauseButton(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
    onPlay: () -> Unit,
    onPause: () -> Unit
) {
    IconButton(
        modifier = modifier,
        onClick = if (isPlaying) onPause else onPlay
    ) {
        if (isPlaying) {
            Icon(
                imageVector = Icons.Rounded.Pause,
                contentDescription = null,
                tint = Color.White
            )
        } else {
            Icon(
                imageVector = Icons.Rounded.PlayArrow,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}