package dev.aaa1115910.bv.component.controllers2.playermenu.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.ExperimentalTvFoundationApi
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.itemsIndexed
import dev.aaa1115910.bv.component.FocusGroup

@OptIn(ExperimentalTvFoundationApi::class)
@Composable
fun RadioMenuList(
    modifier: Modifier = Modifier,
    items: List<String>,
    selected: Int = 0,
    onSelectedChanged: (index: Int) -> Unit,
    onFocusBackToParent: () -> Unit
) {
    FocusGroup {
        TvLazyColumn(
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
                },
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 120.dp, horizontal = 8.dp)
        ) {
            itemsIndexed(items) { index, item ->
                val buttonModifier =
                    (if (selected == index) Modifier.initiallyFocused() else Modifier.restorableFocus())
                        .width(200.dp)

                MenuListItem(
                    modifier = buttonModifier,
                    text = item,
                    selected = selected == index,
                    onClick = {
                        println("Click menu: $item ($index)")
                        onSelectedChanged(index)
                    }
                )
            }
        }
    }
}