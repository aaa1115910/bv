package dev.aaa1115910.bv.component.controllers

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.NonInteractiveSurfaceDefaults
import androidx.tv.material3.Surface
import dev.aaa1115910.bv.ui.theme.BVTheme

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun PauseIcon(
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        colors = NonInteractiveSurfaceDefaults.colors(
            containerColor = Color.Black.copy(0.5f)
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Icon(
            modifier = Modifier
                .padding(12.dp, 4.dp)
                .size(50.dp),
            imageVector = Icons.Rounded.Pause,
            contentDescription = null,
            tint = Color.White
        )
    }
}

@Preview
@Composable
fun PauseIconPreview() {
    BVTheme {
        Box(
            modifier = Modifier.padding(10.dp)
        ) {
            PauseIcon()
        }
    }
}