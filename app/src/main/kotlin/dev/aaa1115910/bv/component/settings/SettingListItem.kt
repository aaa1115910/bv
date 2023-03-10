package dev.aaa1115910.bv.component.settings

import androidx.compose.foundation.clickable
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color

@Composable
fun SettingListItem(
    modifier: Modifier = Modifier,
    title: String,
    supportText: String,
    defaultHasFocus: Boolean = false,
    onClick: () -> Unit
) {
    var hasFocus by remember { mutableStateOf(defaultHasFocus) }

    ListItem(
        modifier = modifier
            .onFocusChanged { hasFocus = it.hasFocus }
            .clip(MaterialTheme.shapes.small)
            .clickable { onClick() },
        headlineContent = { Text(text = title) },
        supportingContent = { Text(text = supportText) },
        trailingContent = { },
        colors = ListItemDefaults.colors(
            containerColor = if (hasFocus) MaterialTheme.colorScheme.primary else Color.Transparent,
            headlineColor = if (hasFocus) MaterialTheme.colorScheme.onPrimary else ListItemDefaults.contentColor,
            supportingColor = if (hasFocus) MaterialTheme.colorScheme.onPrimary else ListItemDefaults.contentColor
        )
    )
}