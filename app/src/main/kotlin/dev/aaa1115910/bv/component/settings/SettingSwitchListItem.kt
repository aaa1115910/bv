package dev.aaa1115910.bv.component.settings

import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ListItem
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Switch
import androidx.tv.material3.SwitchDefaults
import androidx.tv.material3.Text
import dev.aaa1115910.bv.ui.theme.BVTheme

@Composable
fun SettingSwitchListItem(
    modifier: Modifier = Modifier,
    title: String,
    supportText: String,
    checked: Boolean,
    defaultHasFocus: Boolean = false,
    onCheckedChange: (Boolean) -> Unit
) {
    var hasFocus by remember { mutableStateOf(defaultHasFocus) }
    var switchChecked by remember { mutableStateOf(checked) }

    ListItem(
        modifier = modifier.onFocusChanged { hasFocus = it.hasFocus },
        headlineContent = { Text(text = title) },
        supportingContent = { Text(text = supportText) },
        trailingContent = {
            Box(
                modifier = Modifier
                    .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
            ) {
                Switch(
                    modifier = Modifier
                        .focusable(false)
                        .padding(2.dp),
                    checked = switchChecked,
                    onCheckedChange = null,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.inverseSurface,
                        checkedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                        uncheckedThumbColor = MaterialTheme.colorScheme.onSurface,
                        uncheckedTrackColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        },
        onClick = {
            switchChecked = !switchChecked
            onCheckedChange(switchChecked)
        },
        selected = hasFocus
    )
}

@Preview
@Composable
fun SettingSwitchListItemFocusedAndEnabledPreview() {
    BVTheme {
        SettingSwitchListItem(
            title = "This is a title",
            supportText = "This is a support text",
            checked = true,
            defaultHasFocus = true,
            onCheckedChange = {}
        )
    }
}

@Preview
@Composable
fun SettingSwitchListItemFocusedAndDisabledPreview() {
    BVTheme {
        SettingSwitchListItem(
            title = "This is a title",
            supportText = "This is a support text",
            checked = false,
            defaultHasFocus = true,
            onCheckedChange = {}
        )
    }
}

@Preview
@Composable
fun SettingSwitchListItemNotFocusedAndEnabledPreview() {
    BVTheme {
        SettingSwitchListItem(
            title = "This is a title",
            supportText = "This is a support text",
            checked = true,
            defaultHasFocus = false,
            onCheckedChange = {}
        )
    }
}

@Preview
@Composable
fun SettingSwitchListItemNotFocusedAndDisabledPreview() {
    BVTheme {
        SettingSwitchListItem(
            title = "This is a title",
            supportText = "This is a support text",
            checked = false,
            defaultHasFocus = false,
            onCheckedChange = {}
        )
    }
}