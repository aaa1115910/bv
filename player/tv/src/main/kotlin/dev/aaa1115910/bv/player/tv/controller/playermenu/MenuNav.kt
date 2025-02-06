package dev.aaa1115910.bv.player.tv.controller.playermenu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.aaa1115910.bv.player.entity.VideoPlayerMenuNavItem
import dev.aaa1115910.bv.player.tv.controller.playermenu.component.MenuListItem
import dev.aaa1115910.bv.util.createCustomInitialFocusRestorerModifiers
import dev.aaa1115910.bv.util.ifElse

@Composable
fun MenuNavList(
    modifier: Modifier = Modifier,
    selectedMenu: VideoPlayerMenuNavItem,
    onSelectedChanged: (VideoPlayerMenuNavItem) -> Unit,
    isFocusing: Boolean
) {
    val context = LocalContext.current
    val focusRestorerModifiers = createCustomInitialFocusRestorerModifiers()

    LazyColumn(
        modifier = modifier
            .then(focusRestorerModifiers.parentModifier),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        itemsIndexed(VideoPlayerMenuNavItem.entries) { index, item ->
            MenuListItem(
                modifier = Modifier
                    .ifElse(index == 0, focusRestorerModifiers.childModifier),
                text = item.getDisplayName(context),
                icon = item.icon,
                expanded = isFocusing,
                selected = selectedMenu == item,
                onClick = {},
                onFocus = { onSelectedChanged(item) },
            )
        }
    }
}