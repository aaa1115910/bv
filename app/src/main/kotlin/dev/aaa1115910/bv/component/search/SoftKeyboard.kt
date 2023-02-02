package dev.aaa1115910.bv.component.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.util.focusedBorder

@Composable
fun SoftKeyboard(
    modifier: Modifier = Modifier,
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
        keys.forEach { rowKeys ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                rowKeys.forEach { key ->
                    SoftKeyboardKey(
                        key = key,
                        onClick = { onClick(key) }
                    )
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            SoftKeyboardKey(
                modifier = Modifier.weight(1f),
                key = "清空",
                onClick = onClear
            )
            SoftKeyboardKey(
                modifier = Modifier.weight(1f),
                key = "删除",
                onClick = onDelete
            )
            SoftKeyboardKey(
                modifier = Modifier.weight(1f),
                key = "搜索",
                onClick = onSearch
            )
        }
    }
}

@Composable
fun SoftKeyboardKey(
    modifier: Modifier = Modifier,
    key: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .focusedBorder(MaterialTheme.shapes.small)
            .clickable {
                onClick()
            },
        color = Color.White.copy(0.1f),
        shape = MaterialTheme.shapes.small
    ) {
        Box(
            modifier = Modifier.size(38.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = key,
                fontSize = 24.sp
            )
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
    BVTheme {
        SoftKeyboard(
            onClick = {},
            onClear = {},
            onDelete = {},
            onSearch = {}
        )
    }
}