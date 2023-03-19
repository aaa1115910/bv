package dev.aaa1115910.bv.component.controllers2.playermenu

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.items
import dev.aaa1115910.bv.component.controllers2.VideoPlayerMenuNavItem
import dev.aaa1115910.bv.component.controllers2.playermenu.component.MenuListItem
import dev.aaa1115910.bv.util.requestFocus

@Composable
fun MenuNavList(
    modifier: Modifier = Modifier,
    selectedMenu: VideoPlayerMenuNavItem,
    onSelectedChanged: (VideoPlayerMenuNavItem) -> Unit,
    isFocusing: Boolean
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isFocusing) {
        if (isFocusing) focusRequester.requestFocus(scope)
    }

    TvLazyColumn(
        modifier = modifier.animateContentSize()
    ) {
        items(VideoPlayerMenuNavItem.values()) { item ->
            val buttonModifier =
                (if (selectedMenu == item) Modifier.focusRequester(focusRequester) else Modifier)
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