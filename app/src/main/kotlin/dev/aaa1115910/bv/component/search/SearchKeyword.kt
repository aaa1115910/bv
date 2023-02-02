package dev.aaa1115910.bv.component.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun SearchKeyword(
    modifier: Modifier = Modifier,
    keyword: String,
    icon: String,
    onClick: () -> Unit
) {
    var hasFocus by remember { mutableStateOf(false) }

    val buttonBackgroundColor =
        if (hasFocus) MaterialTheme.colorScheme.primary
        else Color.Transparent

    Surface(
        modifier = modifier
            .onFocusChanged {
                hasFocus = it.hasFocus
            }
            .clickable { onClick() },
        color = buttonBackgroundColor,
        shape = MaterialTheme.shapes.small
    ) {
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        vertical = 8.dp,
                        horizontal = 12.dp
                    ),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (icon != "") {
                    AsyncImage(
                        modifier = Modifier.size(16.dp),
                        model = icon,
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds
                    )
                }
                Text(
                    text = keyword,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}