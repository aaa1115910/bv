package dev.aaa1115910.bv.mobile.component.preferences.items

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.schnettler.datastore.manager.DataStoreManager
import de.schnettler.datastore.manager.PreferenceRequest
import dev.aaa1115910.bv.dataStore
import dev.aaa1115910.bv.mobile.component.preferences.PreferenceGroupScope
import dev.aaa1115910.bv.mobile.theme.BVMobileTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
private fun <T> RadioPreference(
    modifier: Modifier = Modifier,
    title: String,
    summary: String?,
    selected: Boolean = false,
    shape: Shape = RoundedCornerShape(0.dp),
    enabled: Boolean = true,
    leadingContent: @Composable() (() -> Unit)? = null,
    value: T,
    values: Map<T, String>,
    onValueChange: ((T) -> Unit)
) {
    var showDialog by remember { mutableStateOf(false) }

    BaseListItem(
        modifier = modifier,
        headlineContent = { Text(text = title) },
        supportingContent = { Text(text = summary ?: "unknown") },
        selected = selected,
        enabled = enabled,
        leadingContent = leadingContent,
        onClick = { showDialog = true },
        shape = shape
    )

    if (showDialog) {
        RadioDialog(
            modifier = Modifier.fillMaxWidth(),
            title = title,
            value = value,
            values = values,
            onValueChange = { newValue ->
                onValueChange(newValue)
                showDialog = false
            },
            onDismissRequest = { showDialog = false }
        )
    }
}

@Composable
private fun <T> RadioDialog(
    modifier: Modifier = Modifier,
    title: String,
    value: T,
    values: Map<T, String>,
    onValueChange: (T) -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        title = { Text(text = title) },
        text = {
            LazyColumn {
                items(values.toList()) { (itemValue, label) ->
                    ListItem(
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.medium)
                            .clickable { onValueChange(itemValue) },
                        headlineContent = { Text(text = label) },
                        leadingContent = {
                            RadioButton(
                                selected = itemValue == value,
                                onClick = { onValueChange(itemValue) }
                            )
                        },
                        colors = ListItemDefaults.colors(
                            containerColor = Color.Transparent,
                        )
                    )
                }
            }
        },
        confirmButton = {},
    )
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun RadioPreferencePreview() {
    BVMobileTheme {
        RadioPreference(
            title = "Radio Preference",
            summary = "value",
            leadingContent = {
                Icon(
                    imageVector = Icons.Default.PlayCircleOutline,
                    contentDescription = null
                )
            },
            selected = false,
            shape = RoundedCornerShape(8.dp),
            enabled = true,
            value = 123,
            values = mapOf(
                123 to "Option 1",
                456 to "Option 2",
                789 to "Option 3"
            ),
            onValueChange = { /* Handle checked change */ }
        )
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun RadioDialogPreview() {
    BVMobileTheme {
        var value by remember { mutableIntStateOf(123) }
        RadioDialog(
            title = "Select Option",
            value = value,
            values = mapOf(
                123 to "Option 1",
                456 to "Option 2",
                789 to "Option 3"
            ),
            onValueChange = { value = it },
            onDismissRequest = { /* Handle dismiss */ }
        )
    }
}

fun <T> PreferenceGroupScope.radioPreference(
    title: String,
    leadingContent: @Composable() (() -> Unit)? = null,
    onSelected: Boolean = false,
    enabled: Boolean = true,
    value: T,
    values: Map<T, String>,
    onValueChange: (T) -> Unit
) {
    preferences += { shape, modifier ->
        RadioPreference(
            modifier = modifier,
            title = title,
            summary = values[value],
            leadingContent = leadingContent,
            selected = onSelected,
            shape = shape,
            enabled = enabled,
            value = value,
            values = values,
            onValueChange = onValueChange
        )
    }
}

fun <T> PreferenceGroupScope.radioPreference(
    title: String,
    leadingContent: @Composable() (() -> Unit)? = null,
    onSelected: Boolean = false,
    enabled: Boolean = true,
    prefReq: PreferenceRequest<T>,
    values: Map<T, String>,
    onValueChange: (T) -> Boolean = { true }
) {
    preferences += { shape, modifier ->
        val scope = rememberCoroutineScope()
        val dataStoreManager = DataStoreManager(LocalContext.current.dataStore)

        val value by dataStoreManager.getPreferenceState(prefReq)
        val setValue = { newValue: T ->
            scope.launch(Dispatchers.IO) {
                dataStoreManager.editPreference(prefReq.key, newValue)
            }
            onValueChange(newValue)
        }

        RadioPreference(
            modifier = modifier,
            title = title,
            summary = values[value],
            leadingContent = leadingContent,
            selected = onSelected,
            shape = shape,
            enabled = enabled,
            value = value,
            values = values,
            onValueChange = {
                if (onValueChange(it)) setValue(it)
            }
        )
    }
}

