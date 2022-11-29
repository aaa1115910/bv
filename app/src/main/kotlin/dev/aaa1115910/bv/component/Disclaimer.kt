package dev.aaa1115910.bv.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import dev.aaa1115910.bv.R
import dev.burnoo.compose.rememberpreference.rememberBooleanPreference

@Composable
fun Disclaimer() {
    var show by rememberBooleanPreference(
        keyName = "disclaimer",
        initialValue = false,
        defaultValue = true
    )

    if (show) {
        AlertDialog(
            title = { Text(text = stringResource(R.string.disclaimer_dialog_title)) },
            text = {
                Text(
                    text = stringResource(R.string.disclaimer_dialog_text),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            },
            onDismissRequest = { },
            confirmButton = {
                TextButton(onClick = { show = false }) {
                    Text(text = stringResource(R.string.confirm))
                }
            }
        )
    }
}