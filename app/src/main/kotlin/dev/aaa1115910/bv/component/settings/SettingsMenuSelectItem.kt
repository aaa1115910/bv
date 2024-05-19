package dev.aaa1115910.bv.component.settings

import androidx.compose.foundation.focusable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.tv.material3.ListItem
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.RadioButton
import androidx.tv.material3.RadioButtonDefaults
import androidx.tv.material3.Text
import dev.aaa1115910.bv.ui.theme.BVTheme

@Composable
fun SettingsMenuSelectItem(
    modifier: Modifier = Modifier,
    text: String,
    selected: Boolean,
    defaultHasFocus: Boolean = false,
    onClick: () -> Unit
) {
    var hasFocus by remember { mutableStateOf(defaultHasFocus) }

    ListItem(
        modifier = modifier.onFocusChanged { hasFocus = it.hasFocus },
        headlineContent = { Text(text = text) },
        trailingContent = {
            RadioButton(
                modifier = Modifier.focusable(false),
                selected = selected,
                onClick = { },
                colors = RadioButtonDefaults.colors(
                    selectedColor = if (hasFocus) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedColor = if (hasFocus) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        },
        onClick = onClick,
        selected = selected
    )
}

private class SettingsMenuSelectItemPreviewParameterProvider :
    PreviewParameterProvider<SettingsMenuSelectItemData> {
    override val values = sequenceOf(
        SettingsMenuSelectItemData(text = "This is a text", selected = false, onFocused = false),
        SettingsMenuSelectItemData(text = "This is a text", selected = false, onFocused = true),
        SettingsMenuSelectItemData(text = "This is a text", selected = true, onFocused = false),
        SettingsMenuSelectItemData(text = "This is a text", selected = true, onFocused = true),
    )
}

private data class SettingsMenuSelectItemData(
    val text: String,
    val selected: Boolean,
    val onFocused: Boolean
)

@Preview
@Composable
private fun SettingsMenuSelectItemPreview(
    @PreviewParameter(SettingsMenuSelectItemPreviewParameterProvider::class) data: SettingsMenuSelectItemData
) {
    BVTheme {
        SettingsMenuSelectItem(
            text = data.text,
            selected = data.selected,
            defaultHasFocus = data.onFocused,
            onClick = {}
        )
    }
}