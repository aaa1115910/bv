package dev.aaa1115910.bv.tv.component.settings

import android.content.res.Configuration
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
import androidx.tv.material3.Text
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.ArrowDropUp
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import kotlin.math.round
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.tv.component.TvAlertDialog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import dev.aaa1115910.bv.util.requestFocus
import kotlin.math.max
import kotlin.math.min

@Composable
fun SettingNumberListItem(
    modifier: Modifier = Modifier,
    title: String,
    supportText: String,
    value: Double,
    minValue: Double = 0.0,
    maxValue: Double = 100.0,
    isInteger: Boolean = true,
    step: Double = 1.0,
    defaultHasFocus: Boolean = false,
    onValueChange: (Double) -> Unit
) {
    var hasFocus by remember { mutableStateOf(defaultHasFocus) }
    var currentValue by remember { mutableStateOf(value) }
    var showDialog by remember { mutableStateOf(false) }

    ListItem(
        modifier = modifier.onFocusChanged { hasFocus = it.hasFocus },
        headlineContent = { Text(text = title) },
        supportingContent = { Text(text = supportText) },
        trailingContent = {
            Text(
                modifier = Modifier
                    .widthIn(48.dp, 96.dp),
                text = if (isInteger) currentValue.toInt().toString() else String.format(
                    "%.2f",
                    currentValue
                ),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = if (hasFocus) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface
            )
        },
        onClick = {
            showDialog = true
        },
        selected = hasFocus
    )

    NumberDialog(
        show = showDialog,
        title = title,
        initValue = currentValue,
        minValue = minValue,
        maxValue = maxValue,
        step = step,
        isInteger = isInteger,
        onHideDialog = { showDialog = false },
        onChange = { newValue ->
            currentValue = newValue.toDouble()
            onValueChange(currentValue)
        }
    )
}

@Composable
private fun NumberDialog(
    modifier: Modifier = Modifier,
    show: Boolean,
    title: String? = null,
    onHideDialog: () -> Unit,
    onChange: (Float) -> Unit,
    initValue: Double = 0.0,
    minValue: Double = 0.0,
    maxValue: Double = 100.0,
    step: Double = 1.0,
    isInteger: Boolean = true,
) {
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }
    var currentValue by remember { mutableStateOf(initValue) }

    LaunchedEffect(show) {
        if (show) focusRequester.requestFocus(scope)
    }

    if (show) {
        TvAlertDialog(
            modifier = modifier,
            onDismissRequest = { onHideDialog() },
            title = { Text(text = title ?: "") },
            text = {
                Column(
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .focusable()
                        .fillMaxWidth()
                        .onPreviewKeyEvent {
                            if (it.key == Key.DirectionUp || it.key == Key.DirectionDown) {
                                if (it.type == KeyEventType.KeyDown) {
                                    val newValue = if (it.key == Key.DirectionUp) 
                                        (currentValue + step).coerceAtMost(maxValue)
                                        else (currentValue - step).coerceAtLeast(minValue)
                                    currentValue = if (isInteger) round(newValue) else newValue
                                    onChange(currentValue.toFloat())
                                }
                                true
                            } else if (listOf(Key.Enter, Key.DirectionCenter).contains(it.key) && it.type == KeyEventType.KeyDown) {
                                onHideDialog()
                                true
                            } else {
                                false
                            }
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(imageVector = Icons.Rounded.ArrowDropUp, contentDescription = null)
                    Text(
                        text = if (isInteger) currentValue.toInt().toString() else String.format("%.2f", currentValue),
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Icon(imageVector = Icons.Rounded.ArrowDropDown, contentDescription = null)
                }
            },
            confirmButton = {}
        )
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SettingNumberListItemIntegerPreview() {
    BVTheme {
        SettingNumberListItem(
            title = "Integer Setting",
            supportText = "This is an integer value setting",
            value = 50.0,
            minValue = 0.0,
            maxValue = 100.0,
            isInteger = true,
            defaultHasFocus = true,
            onValueChange = {}
        )
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SettingNumberListItemDecimalPreview() {
    BVTheme {
        SettingNumberListItem(
            title = "Decimal Setting",
            supportText = "This is a decimal value setting",
            value = 2.25,
            minValue = 0.0,
            maxValue = 10.0,
            isInteger = false,
            step = 0.25,
            defaultHasFocus = true,
            onValueChange = {}
        )
    }
}