package dev.aaa1115910.bv.component.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.ui.theme.BVTheme

@Composable
fun SoftKeyboard(
    modifier: Modifier = Modifier,
    firstButtonFocusRequester: FocusRequester,
    onClick: (String) -> Unit,
    onClear: () -> Unit,
    onDelete: () -> Unit,
    onSearch: () -> Unit
) {
    val keys = listOf(
        listOf("A", "B", "C", "D", "E", "F"),
        listOf("G", "H", "I", "J", "K", "L"),
        listOf("M", "N", "O", "P", "Q", "R"),
        listOf("S", "T", "U", "V", "W", "X"),
        listOf("Y", "Z", "1", "2", "3", "4"),
        listOf("5", "6", "7", "8", "9", "0")
    )

    Column(
        modifier = modifier.width(258.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        keys.forEachIndexed { rowIndex, rowKeys ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                rowKeys.forEachIndexed { index, key ->
                    val keyModifier = if (rowIndex == 0 && index == 0) {
                        Modifier.focusRequester(firstButtonFocusRequester)
                    } else {
                        Modifier
                    }
                    SoftKeyboardKey(
                        modifier = keyModifier,
                        key = key,
                        onClick = { onClick(key) }
                    )
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            SoftKeyboardButton(
                modifier = Modifier.weight(1f),
                key = stringResource(R.string.search_input_soft_keybord_clear),
                onClick = onClear
            )
            SoftKeyboardButton(
                modifier = Modifier.weight(1f),
                key = stringResource(R.string.search_input_soft_keybord_delete),
                onClick = onDelete
            )
            SoftKeyboardButton(
                modifier = Modifier.weight(1f),
                key = stringResource(R.string.search_input_soft_keybord_search),
                onClick = onSearch
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SoftKeyboardKey(
    modifier: Modifier = Modifier,
    key: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier,
        colors = ClickableSurfaceDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.primary,
            pressedContainerColor = MaterialTheme.colorScheme.primary
        ),
        shape = ClickableSurfaceDefaults.shape(shape = MaterialTheme.shapes.small),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier.size(38.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = key, fontSize = 24.sp)
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SoftKeyboardButton(
    modifier: Modifier = Modifier,
    key: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.height(38.dp),
        colors = ClickableSurfaceDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.primary,
            pressedContainerColor = MaterialTheme.colorScheme.primary
        ),
        shape = ClickableSurfaceDefaults.shape(shape = MaterialTheme.shapes.small),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = key, fontSize = 24.sp)
        }
    }
}

@Preview
@Composable
private fun SoftKeyboardKeyPreview() {
    BVTheme {
        SoftKeyboardKey(
            key = "X",
            onClick = {}
        )
    }
}

@Preview
@Composable
private fun SoftKeyboardPreview() {
    val firstButtonFocusRequester = remember { FocusRequester() }
    BVTheme {
        SoftKeyboard(
            firstButtonFocusRequester = firstButtonFocusRequester,
            onClick = {},
            onClear = {},
            onDelete = {},
            onSearch = {}
        )
    }
}