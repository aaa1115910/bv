package dev.aaa1115910.bv.tv.component.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.tv.material3.ListItem
import androidx.tv.material3.Text

@Composable
fun SettingListItem(
    modifier: Modifier = Modifier,
    title: String,
    supportText: String,
    defaultHasFocus: Boolean = false,
    onClick: () -> Unit,
    valueText: String? = null
) {
    var hasFocus by remember { mutableStateOf(defaultHasFocus) }

    ListItem(
        modifier = modifier.onFocusChanged { hasFocus = it.hasFocus },
        headlineContent = { Text(text = title) },
        supportingContent = { Text(text = supportText) },
        trailingContent = { if (valueText?.isNotEmpty() == true) Text(text = valueText) },
        onClick = onClick,
        selected = false
    )
}