package dev.aaa1115910.bv.component.controllers2.playermenu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.items
import dev.aaa1115910.bv.component.controllers.LocalVideoPlayerControllerData
import dev.aaa1115910.bv.component.controllers2.LocalMenuFocusStateData
import dev.aaa1115910.bv.component.controllers2.MenuFocusState
import dev.aaa1115910.bv.component.controllers2.VideoPlayerPictureMenuItem
import dev.aaa1115910.bv.component.controllers2.playermenu.component.MenuListItem
import dev.aaa1115910.bv.component.controllers2.playermenu.component.RadioMenuList
import dev.aaa1115910.bv.entity.VideoAspectRatio
import dev.aaa1115910.bv.entity.VideoCodec
import dev.aaa1115910.bv.util.requestFocus

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PictureMenuList(
    modifier: Modifier = Modifier,
    onResolutionChange: (Int) -> Unit,
    onCodecChange: (VideoCodec) -> Unit,
    onAspectRatioChange: (VideoAspectRatio) -> Unit,


    onFocusStateChange: (MenuFocusState) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val focusState = LocalMenuFocusStateData.current

    val focusRequester = remember { FocusRequester() }
    var selectedPictureMenuItem by remember { mutableStateOf(VideoPlayerPictureMenuItem.Resolution) }

    val data = LocalVideoPlayerControllerData.current

    LaunchedEffect(focusState.focusState) {
        if (focusState.focusState == MenuFocusState.Menu) focusRequester.requestFocus(scope)
    }
    Row(
        modifier = modifier.fillMaxHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val menuItemsModifier = Modifier
            .width(200.dp)
            .padding(horizontal = 8.dp)
        AnimatedVisibility(visible = focusState.focusState != MenuFocusState.MenuNav) {

            when (selectedPictureMenuItem) {
                VideoPlayerPictureMenuItem.Resolution -> {
                    RadioMenuList(
                        modifier = menuItemsModifier,
                        items = data.resolutionMap.values.toList(),
                        selected = data.resolutionMap.keys.indexOf(data.currentResolution),
                        isFocusing = focusState.focusState == MenuFocusState.Items,
                        onSelectedChanged = { onResolutionChange(data.resolutionMap.keys.toList()[it]) },
                        onFocusBackToParent = { onFocusStateChange(MenuFocusState.Menu) },
                    )
                }

                VideoPlayerPictureMenuItem.Codec -> {
                    RadioMenuList(
                        modifier = menuItemsModifier,
                        items = data.availableVideoCodec.map { it.getDisplayName(context) },
                        selected = data.availableVideoCodec.indexOf(data.currentVideoCodec),
                        isFocusing = focusState.focusState == MenuFocusState.Items,
                        onSelectedChanged = { onCodecChange(data.availableVideoCodec[it]) },
                        onFocusBackToParent = { onFocusStateChange(MenuFocusState.Menu) },
                    )
                }

                VideoPlayerPictureMenuItem.AspectRatio -> {
                    RadioMenuList(
                        modifier = menuItemsModifier,
                        items = VideoAspectRatio.values().map { it.getDisplayName(context) },
                        selected = VideoAspectRatio.values().indexOf(data.currentVideoAspectRatio),
                        isFocusing = focusState.focusState == MenuFocusState.Items,
                        onSelectedChanged = { onAspectRatioChange(VideoAspectRatio.values()[it]) },
                        onFocusBackToParent = { onFocusStateChange(MenuFocusState.Menu) },
                    )
                }
            }

        }
        TvLazyColumn(
            modifier = modifier
                .padding(horizontal = 8.dp)
                .onPreviewKeyEvent {
                    if (it.type == KeyEventType.KeyUp) return@onPreviewKeyEvent true
                    when (it.key) {
                        Key.DirectionRight -> {
                            onFocusStateChange(MenuFocusState.MenuNav)
                            return@onPreviewKeyEvent true
                        }

                        Key.DirectionLeft -> {
                            onFocusStateChange(MenuFocusState.Items)
                            return@onPreviewKeyEvent true
                        }

                        else -> return@onPreviewKeyEvent false
                    }
                }
        ) {
            items(VideoPlayerPictureMenuItem.values()) { item ->
                val buttonModifier =
                    (if (selectedPictureMenuItem == item) Modifier.focusRequester(focusRequester) else Modifier)
                        .width(200.dp)
                MenuListItem(
                    modifier = buttonModifier,
                    text = item.getDisplayName(context),
                    selected = selectedPictureMenuItem == item,
                    onClick = {},
                    onFocus = { selectedPictureMenuItem = item },
                )
            }
        }
    }
}