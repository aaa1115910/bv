package dev.aaa1115910.bv.component.controllers2.playermenu

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.itemsIndexed
import dev.aaa1115910.bv.component.controllers2.VideoPlayerMenuNavItem
import dev.aaa1115910.bv.component.controllers2.playermenu.component.MenuListItem
import dev.aaa1115910.bv.component.createCustomInitialFocusRestorerModifiers
import dev.aaa1115910.bv.component.ifElse

@Composable
fun MenuNavList(
    modifier: Modifier = Modifier,
    selectedMenu: VideoPlayerMenuNavItem,
    onSelectedChanged: (VideoPlayerMenuNavItem) -> Unit,
    isFocusing: Boolean
) {
    val context = LocalContext.current
    val focusRestorerModifiers = createCustomInitialFocusRestorerModifiers()

    TvLazyColumn(
        modifier = modifier
            .animateContentSize()
            .then(focusRestorerModifiers.parentModifier),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        itemsIndexed(VideoPlayerMenuNavItem.values()) { index, item ->
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