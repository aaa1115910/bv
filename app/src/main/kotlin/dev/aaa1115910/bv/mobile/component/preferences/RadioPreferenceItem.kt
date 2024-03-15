package dev.aaa1115910.bv.mobile.component.preferences

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ListItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import de.schnettler.datastore.manager.DataStoreManager
import de.schnettler.datastore.manager.PreferenceRequest
import dev.aaa1115910.bv.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun <T> RadioPreferenceItem(
    modifier: Modifier = Modifier,
    title: String,
    summary: String? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    singleLineTitle: Boolean = false,
    singleLineSummary: Boolean = false,
    onSelectedChange: (T) -> Unit,
    items: Map<T, String>,
    selectedValue: T
) {
    var showRadioDialog by remember { mutableStateOf(false) }

    TextPreferenceItem(
        modifier = modifier,
        title = title,
        summary = summary,
        leadingContent = leadingContent,
        enabled = enabled,
        singleLineTitle = singleLineTitle,
        singleLineSummary = singleLineSummary,
        onClick = { showRadioDialog = true }
    )

    RadioListDialog(
        show = showRadioDialog,
        onHideDialog = { showRadioDialog = false },
        values = items,
        selectedValue = selectedValue,
        onSelectValue = { onSelectedChange(it) }
    )
}

@Composable
fun <T> RadioPreferenceItem(
    modifier: Modifier = Modifier,
    title: String,
    summary: String? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    singleLineTitle: Boolean = false,
    singleLineSummary: Boolean = false,
    items: Map<T, String>,
    prefReq: PreferenceRequest<T>
) {
    val dataStoreManager = DataStoreManager(LocalContext.current.dataStore)
    val prefs by dataStoreManager.preferenceFlow.collectAsState(initial = null)
    val scope = rememberCoroutineScope()

    RadioPreferenceItem(
        modifier = modifier,
        title = title,
        summary = summary,
        leadingContent = leadingContent,
        enabled = enabled,
        singleLineTitle = singleLineTitle,
        singleLineSummary = singleLineSummary,
        onSelectedChange = { newValue ->
            println("set ${prefReq.key} to $newValue")
            scope.launch(Dispatchers.IO) {
                dataStoreManager.editPreference(prefReq.key, newValue)
            }
        },
        items = items,
        selectedValue = prefs.getOrDefault(prefReq)
    )
}

@Composable
private fun <T> RadioListDialog(
    modifier: Modifier = Modifier,
    show: Boolean,
    onHideDialog: () -> Unit,
    values: Map<T, String>,
    selectedValue: T,
    onSelectValue: (T) -> Unit,
) {
    if (show) {
        AlertDialog(
            modifier = modifier,
            onDismissRequest = onHideDialog,
            confirmButton = { },
            text = {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 300.dp)
                ) {
                    items(values.toList()) { (value, name) ->
                        RadioListItem(
                            text = name,
                            onClick = {
                                onSelectValue(value)
                                onHideDialog()
                            },
                            selected = value == selectedValue
                        )
                    }
                }
            }
        )
    }
}

@Composable
fun RadioListItem(
    modifier: Modifier = Modifier,
    text: String,
    onClick: (() -> Unit),
    selected: Boolean,
) {
    ListItem(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        headlineContent = { Text(text = text) },
        leadingContent = {
            RadioButton(
                selected = selected,
                onClick = onClick
            )
        }
    )
}