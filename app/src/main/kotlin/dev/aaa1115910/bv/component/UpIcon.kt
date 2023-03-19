package dev.aaa1115910.bv.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun UpIcon(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Box(
        modifier = modifier
            .padding(horizontal = 0.dp)
            .scale(0.7f)
            .border(
                width = 2.dp,
                color = color,
                shape = RoundedCornerShape(6.dp)
            )
    ) {
        Text(
            text = " UP ",
            color = color,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Default
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Preview
@Composable
fun UpIconPreview() {
    MaterialTheme {
        Surface(
            enabled = false,
            onClick = {}
        ) {
            Row {
                UpIcon()
                Text(text = "bishi")
            }
        }
    }
}