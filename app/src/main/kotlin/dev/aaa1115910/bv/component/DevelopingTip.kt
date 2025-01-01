package dev.aaa1115910.bv.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import dev.aaa1115910.bv.ui.theme.BVTheme

@Composable
fun DevelopingTip(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "\uD83D\uDEA7",
            style = MaterialTheme.typography.displayLarge
        )
        Text(
            text = "前方施工 请绕行",
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
fun DevelopingTipContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        DevelopingTip()
    }
}

@Preview
@Composable
private fun DevelopingTipPreview() {
    BVTheme {
        DevelopingTip()
    }
}

@Preview(device = "id:tv_1080p")
@Composable
private fun DevelopingTipContentPreview() {
    BVTheme {
        DevelopingTipContent()
    }
}