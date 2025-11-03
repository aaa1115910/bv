package dev.aaa1115910.bv.tv.component.settings

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ListItem
import androidx.tv.material3.RadioButton
import androidx.tv.material3.Text
import dev.aaa1115910.bv.tv.component.TvAlertDialog

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

@Composable
fun <T> SettingListItemWithDialog(
    modifier: Modifier = Modifier,
    title: String,
    supportText: String,
    options: List<T>,
    getDisplayName: (T, Context) -> String,
    value: T,
    onValueChange: (T) -> Unit,
    defaultHasFocus: Boolean = false
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    SettingListItem(
        modifier = modifier,
        title = title,
        supportText = supportText,
        defaultHasFocus = defaultHasFocus,
        valueText = getDisplayName(value, context),
        onClick = { showDialog = true }
    )

    SelectionDialog(
        show = showDialog,
        title = title,
        onHideDialog = { showDialog = false },
        options = options,
        getDisplayName = getDisplayName,
        value = value,
        onChange = onValueChange
    )
}


@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun <T> SelectionDialog(
    modifier: Modifier = Modifier,
    show: Boolean,
    title: String = "",
    options: List<T>,
    getDisplayName: (T, Context) -> String,
    value: T,
    onChange: (T) -> Unit,
    onHideDialog: () -> Unit
) {
    if (show) {
        val context = LocalContext.current
        val configuration = LocalConfiguration.current
        val maxHeight = (configuration.screenHeightDp * 0.5).dp
        TvAlertDialog(
            modifier = modifier,
            onDismissRequest = { onHideDialog() },
            title = { if (title.isNotEmpty()) Text(text = title) },
            text = {
                Column(
                    modifier = Modifier
                        .heightIn(max = maxHeight)
                        .verticalScroll(rememberScrollState())
                ) {
                    options.forEach {
                        ListItem(
                            selected = value == it,
                            onClick = { onChange(it) },
                            headlineContent = {
                                Text(text = getDisplayName(it, context))
                            },
                            trailingContent = {
                                RadioButton(
                                    selected = value == it,
                                    onClick = null
                                )
                            }
                        )
                    }
                }
            },
            confirmButton = {}
        )
    }
}