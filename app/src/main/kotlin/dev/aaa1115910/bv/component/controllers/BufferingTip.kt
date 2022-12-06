package dev.aaa1115910.bv.component.controllers

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aaa1115910.bv.ui.theme.BVTheme

@Composable
fun BufferingTip(
    modifier: Modifier = Modifier,
    speed: String
) {
    Surface(
        modifier = modifier,
        color = Color.Black.copy(0.5f),
        shape = MaterialTheme.shapes.medium
    ) {
        Text(
            modifier = Modifier
                .padding(16.dp, 8.dp),
            text = "缓冲中...$speed",
            fontSize = 24.sp
        )
    }
}

@Preview
@Composable
fun BufferingTipPreview() {
    BVTheme {
        BufferingTip(
            modifier = Modifier.padding(10.dp),
            speed = ""
        )
    }
}