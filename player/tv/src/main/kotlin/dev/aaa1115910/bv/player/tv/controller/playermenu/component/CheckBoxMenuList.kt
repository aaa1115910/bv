package dev.aaa1115910.bv.player.tv.controller.playermenu.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import dev.aaa1115910.bv.util.createCustomInitialFocusRestorerModifiers
import dev.aaa1115910.bv.util.ifElse

@Composable
fun CheckBoxMenuList(
    modifier: Modifier = Modifier,
    items: List<String>,
    selected: List<Int> = listOf(),
    onSelectedChanged: (indexes: List<Int>) -> Unit,
    onFocusBackToParent: () -> Unit
) {
    val focusRestorerModifiers = createCustomInitialFocusRestorerModifiers()
    LazyColumn(
        modifier = modifier
            .onPreviewKeyEvent {
                println(it)
                if (it.type == KeyEventType.KeyUp) {
                    if (listOf(Key.Enter, Key.DirectionCenter).contains(it.key)) {
                        return@onPreviewKeyEvent false
                    }
                    return@onPreviewKeyEvent true
                }
                val result = it.key == Key.DirectionRight
                if (result) onFocusBackToParent()
                result
            }
            .then(focusRestorerModifiers.parentModifier),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 120.dp, horizontal = 8.dp)
    ) {
        itemsIndexed(items) { index, item ->
            MenuListItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .ifElse(index == 0, focusRestorerModifiers.childModifier),
                text = item,
                selected = selected.contains(index),
                onClick = {
                    val newSelectedIndexes = selected.toMutableList()
                    if (newSelectedIndexes.contains(index)) newSelectedIndexes.remove(index)
                    else newSelectedIndexes.add(index)
                    onSelectedChanged(newSelectedIndexes)
                }
            )
        }
    }
}