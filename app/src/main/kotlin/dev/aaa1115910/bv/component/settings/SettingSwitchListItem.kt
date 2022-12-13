package dev.aaa1115910.bv.component.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.aaa1115910.bv.ui.theme.BVTheme

@OptIn(ExperimentalMaterial3Api::class)
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
        modifier = modifier
            .onFocusChanged { hasFocus = it.hasFocus }
            .clip(MaterialTheme.shapes.small)
            .clickable {
                switchChecked = !switchChecked
                onCheckedChange(switchChecked)
            },
        headlineText = { Text(text = title) },
        supportingText = { Text(text = supportText) },
        trailingContent = {
            Switch(
                modifier = Modifier
                    .focusable(false)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(0.dp),
                checked = switchChecked,
                onCheckedChange = null,
                colors = SwitchDefaults.colors(
                    checkedBorderColor = if (checked) MaterialTheme.colorScheme.surface else Color.Transparent
                )
            )
        },
        colors = ListItemDefaults.colors(
            containerColor = if (hasFocus) MaterialTheme.colorScheme.primary else Color.Transparent,
            headlineColor = if (hasFocus) MaterialTheme.colorScheme.onPrimary else ListItemDefaults.contentColor,
            supportingColor = if (hasFocus) MaterialTheme.colorScheme.onPrimary else ListItemDefaults.contentColor
        )
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