package dev.aaa1115910.bv.component

import android.view.KeyEvent
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.util.focusedBorder

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TvOutlinedTextFiled(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    onPressEnter: () -> Unit = {},
    onMoveFocusToDown: () -> Unit = {},
    onMoveFocusToUp: () -> Unit = {},
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    val keyboard = LocalSoftwareKeyboardController.current

    Box(
        modifier = modifier
            .focusedBorder()
            .focusable()
    ) {
        OutlinedTextField(
            modifier = modifier
                .onPreviewKeyEvent {
                    when (it.nativeKeyEvent.keyCode) {
                        KeyEvent.KEYCODE_DPAD_CENTER -> {
                            if (it.nativeKeyEvent.action == KeyEvent.ACTION_UP) {
                                keyboard?.show()
                            }
                        }

                        KeyEvent.KEYCODE_ENTER -> {
                            if (it.nativeKeyEvent.action == KeyEvent.ACTION_UP) {
                                onPressEnter()
                            }
                            return@onPreviewKeyEvent true
                        }

                        KeyEvent.KEYCODE_DPAD_DOWN -> {
                            if (it.nativeKeyEvent.action == KeyEvent.ACTION_UP) {
                                onMoveFocusToDown()
                            }
                            return@onPreviewKeyEvent true
                        }

                        KeyEvent.KEYCODE_DPAD_UP -> {
                            if (it.nativeKeyEvent.action == KeyEvent.ACTION_UP) {
                                onMoveFocusToUp()
                            }
                            return@onPreviewKeyEvent true
                        }
                    }
                    return@onPreviewKeyEvent false
                },
            value = value,
            onValueChange = onValueChange,
            maxLines = 1,
            shape = MaterialTheme.shapes.large,
            keyboardActions = keyboardActions,
            keyboardOptions = keyboardOptions
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Preview
@Composable
fun TvOutlinedTextFiledPreview() {
    BVTheme {
        Surface {
            Box(
                modifier = Modifier.padding(12.dp)
            ) {
                var text by remember { mutableStateOf("") }

                Column {
                    Row {
                        repeat(6) {
                            TextButton(onClick = { }) {
                                Text(text = "Button")
                            }
                        }
                    }
                    Row {
                        TextButton(onClick = { }) {
                            Text(text = "Button")
                        }
                        TvOutlinedTextFiled(
                            modifier = Modifier,
                            value = text,
                            onValueChange = { text = it }
                        )
                        TextButton(onClick = { }) {
                            Text(text = "Button")
                        }
                    }
                    Row {
                        repeat(6) {
                            TextButton(onClick = { }) {
                                Text(text = "Button")
                            }
                        }
                    }
                }
            }
        }
    }
}