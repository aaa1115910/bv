package dev.aaa1115910.bv.component.controllers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TopController(
    modifier: Modifier = Modifier,
    title: String
) {
    Column(
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    0.0f to Color.Black.copy(alpha = 0.8f),
                    0.2f to Color.Black.copy(alpha = 0.6f),
                    0.8f to Color.Black.copy(alpha = 0.2f),
                    1.0f to Color.Transparent,
                    startY = 0.0f,
                    //endY = 200.0f
                )
            )
            .height(80.dp)
            .fillMaxWidth()
            .padding(top = 8.dp, start = 32.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.displaySmall,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Preview
@Composable
private fun TopControllerPreview() {
    Surface {
        Column {
            TopController(
                title = "This is a video title"
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}