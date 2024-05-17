package dev.aaa1115910.bv.component

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.ui.theme.BVTheme

@Composable
fun UpIcon(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Icon(
        modifier = modifier,
        painter = painterResource(id = R.drawable.ic_up),
        contentDescription = null,
        tint = color
    )
}

@Preview
@Composable
fun UpIconPreview() {
    BVTheme {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            UpIcon()
            Text(text = "bishi")
        }
    }
}