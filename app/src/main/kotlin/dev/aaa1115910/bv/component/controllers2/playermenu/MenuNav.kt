package dev.aaa1115910.bv.component.controllers2.playermenu

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.ExperimentalTvFoundationApi
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.itemsIndexed
import dev.aaa1115910.bv.component.FocusGroup
import dev.aaa1115910.bv.component.controllers2.VideoPlayerMenuNavItem
import dev.aaa1115910.bv.component.controllers2.playermenu.component.MenuListItem

@OptIn(ExperimentalTvFoundationApi::class)
@Composable
fun MenuNavList(
    modifier: Modifier = Modifier,
    selectedMenu: VideoPlayerMenuNavItem,
    onSelectedChanged: (VideoPlayerMenuNavItem) -> Unit,
    isFocusing: Boolean
) {
    val context = LocalContext.current

    FocusGroup(
        modifier = modifier,
    ) {
        TvLazyColumn(
            modifier = Modifier.animateContentSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            itemsIndexed(VideoPlayerMenuNavItem.values()) { index, item ->
                val buttonModifier =
                    (if (index == 0) Modifier.initiallyFocused() else Modifier.restorableFocus())
                        .width(if (isFocusing) 200.dp else 46.dp)
                MenuListItem(
                    modifier = buttonModifier,
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
}