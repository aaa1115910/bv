package dev.aaa1115910.bv.mobile.component.preferences

import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.Preferences
import de.schnettler.datastore.manager.DataStoreManager
import de.schnettler.datastore.manager.PreferenceRequest
import dev.aaa1115910.bv.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SwitchPreferenceItem(
    modifier: Modifier = Modifier,
    title: String,
    summary: String? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    singleLineTitle: Boolean = false,
    singleLineSummary: Boolean = false,
    onClick: (() -> Unit)? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    TextPreferenceItem(
        modifier = modifier,
        title = title,
        summary = summary,
        leadingContent = leadingContent,
        trailingContent = {
            Switch(
                enabled = enabled,
                checked = checked,
                onCheckedChange = { newValue ->
                    onCheckedChange(newValue)
                    onClick?.invoke()
                }
            )
        },
        enabled = enabled,
        singleLineTitle = singleLineTitle,
        singleLineSummary = singleLineSummary,
        onClick = {
            println("clicked listitem, checked: $checked")
            onCheckedChange(!checked)
            onClick?.invoke()
        }
    )
}

@Composable
fun SwitchPreferenceItem(
    modifier: Modifier = Modifier,
    title: String,
    summary: String? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    singleLineTitle: Boolean = false,
    singleLineSummary: Boolean = false,
    onClick: (() -> Unit)? = null,
    prefReq: PreferenceRequest<Boolean>
) {
    val dataStoreManager = DataStoreManager(LocalContext.current.dataStore)
    val prefs by dataStoreManager.preferenceFlow.collectAsState(initial = null)
    val scope = rememberCoroutineScope()

    SwitchPreferenceItem(
        modifier = modifier,
        title = title,
        summary = summary,
        leadingContent = leadingContent,
        enabled = enabled,
        singleLineTitle = singleLineTitle,
        singleLineSummary = singleLineSummary,
        onClick = { onClick?.invoke() },
        checked = prefs.getOrDefault(prefReq),
        onCheckedChange = { newValue ->
            println("set ${prefReq.key} to $newValue")
            scope.launch(Dispatchers.IO) {
                dataStoreManager.editPreference(prefReq.key, newValue)
            }
        }
    )
}

fun <T> Preferences?.getOrDefault(prefReq: PreferenceRequest<T>): T {
    return this?.get(prefReq.key) ?: prefReq.defaultValue
}