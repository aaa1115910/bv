package dev.aaa1115910.bv.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text

@Composable
fun BottomTip(
    modifier: Modifier = Modifier,
    text: String
) {
    Column(
        modifier = modifier
    ) {
        Icon(
            modifier = Modifier.height(48.dp),
            imageVector = Icons.Outlined.Info,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = text.replace("\n", "\n\n"),
            style = MaterialTheme.typography.bodySmall
        )
    }
}