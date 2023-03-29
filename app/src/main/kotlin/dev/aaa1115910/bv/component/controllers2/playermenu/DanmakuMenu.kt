package dev.aaa1115910.bv.component.controllers2.playermenu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.PaddingValues
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
import dev.aaa1115910.bv.component.controllers2.DanmakuType
import dev.aaa1115910.bv.component.controllers2.LocalMenuFocusStateData
import dev.aaa1115910.bv.component.controllers2.MenuFocusState
import dev.aaa1115910.bv.component.controllers2.VideoPlayerDanmakuMenuItem
import dev.aaa1115910.bv.component.controllers2.playermenu.component.CheckBoxMenuList
import dev.aaa1115910.bv.component.controllers2.playermenu.component.MenuListItem
import dev.aaa1115910.bv.component.controllers2.playermenu.component.StepLessMenuItem
import dev.aaa1115910.bv.util.requestFocus
import java.text.NumberFormat

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DanmakuMenuList(
    modifier: Modifier = Modifier,
    onDanmakuSwitchChange: (List<DanmakuType>) -> Unit,
    onDanmakuSizeChange: (Float) -> Unit,
    onDanmakuOpacityChange: (Float) -> Unit,
    onDanmakuAreaChange: (Float) -> Unit,
    onFocusStateChange: (MenuFocusState) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val data = LocalVideoPlayerControllerData.current
    val focusState = LocalMenuFocusStateData.current

    val focusRequester = remember { FocusRequester() }
    var selectedDanmakuMenuItem by remember { mutableStateOf(VideoPlayerDanmakuMenuItem.Switch) }

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
            when (selectedDanmakuMenuItem) {
                VideoPlayerDanmakuMenuItem.Switch -> CheckBoxMenuList(
                    modifier = menuItemsModifier,
                    items = DanmakuType.values().map { it.getDisplayName(context) },
                    selected = data.currentDanmakuEnabledList.map { it.ordinal },
                    isFocusing = focusState.focusState == MenuFocusState.Items,
                    onSelectedChanged = {
                        val newEnabledDanmakuList = it
                            .map { index -> DanmakuType.values()[index] }
                            .toMutableList()

                        if (
                            newEnabledDanmakuList.contains(DanmakuType.All) &&
                            !data.currentDanmakuEnabledList.contains(DanmakuType.All)
                        ) {
                            // 勾选了全部
                            onDanmakuSwitchChange(DanmakuType.values().toList())
                        } else if (
                            data.currentDanmakuEnabledList.contains(DanmakuType.All) &&
                            !newEnabledDanmakuList.contains(DanmakuType.All)
                        ) {
                            // 取消了全部
                            onDanmakuSwitchChange(listOf())
                        } else if (
                            data.currentDanmakuEnabledList.contains(DanmakuType.All) &&
                            newEnabledDanmakuList.contains(DanmakuType.All) &&
                            data.currentDanmakuEnabledList.size != newEnabledDanmakuList.size
                        ) {
                            // 在勾选全部时，取消某一项
                            newEnabledDanmakuList.remove(DanmakuType.All)
                            onDanmakuSwitchChange(newEnabledDanmakuList)
                        } else if (
                            !data.currentDanmakuEnabledList.contains(DanmakuType.All) &&
                            newEnabledDanmakuList.size == DanmakuType.values().size - 1
                        ) {
                            // 在勾选了全部之外的所有项时，勾选全部项
                            onDanmakuSwitchChange(DanmakuType.values().toList())
                        } else {
                            onDanmakuSwitchChange(newEnabledDanmakuList)
                        }
                    },
                    onFocusBackToParent = { onFocusStateChange(MenuFocusState.Menu) }
                )

                VideoPlayerDanmakuMenuItem.Size -> StepLessMenuItem(
                    modifier = menuItemsModifier,
                    value = data.currentDanmakuScale,
                    step = 0.01f,
                    range = 0.5f..2f,
                    text = NumberFormat.getPercentInstance()
                        .apply { maximumFractionDigits = 0 }
                        .format(data.currentDanmakuScale),
                    isFocusing = focusState.focusState == MenuFocusState.Items,
                    onValueChange = onDanmakuSizeChange,
                    onFocusBackToParent = { onFocusStateChange(MenuFocusState.Menu) }
                )

                VideoPlayerDanmakuMenuItem.Opacity -> StepLessMenuItem(
                    modifier = menuItemsModifier,
                    value = data.currentDanmakuOpacity,
                    step = 0.01f,
                    range = 0f..1f,
                    text = NumberFormat.getPercentInstance()
                        .apply { maximumFractionDigits = 0 }
                        .format(data.currentDanmakuOpacity),
                    isFocusing = focusState.focusState == MenuFocusState.Items,
                    onValueChange = onDanmakuOpacityChange,
                    onFocusBackToParent = { onFocusStateChange(MenuFocusState.Menu) }
                )

                VideoPlayerDanmakuMenuItem.Area -> StepLessMenuItem(
                    modifier = menuItemsModifier,
                    value = data.currentDanmakuArea,
                    step = 0.01f,
                    range = 0f..1f,
                    text = NumberFormat.getPercentInstance()
                        .apply { maximumFractionDigits = 0 }
                        .format(data.currentDanmakuArea),
                    isFocusing = focusState.focusState == MenuFocusState.Items,
                    onValueChange = onDanmakuAreaChange,
                    onFocusBackToParent = { onFocusStateChange(MenuFocusState.Menu) }
                )
            }
        }
        TvLazyColumn(
            modifier = modifier
                .padding(horizontal = 8.dp)
                .onPreviewKeyEvent {
                    if (it.type == KeyEventType.KeyUp) {
                        if (listOf(Key.Enter, Key.DirectionCenter).contains(it.key)) {
                            return@onPreviewKeyEvent false
                        }
                        return@onPreviewKeyEvent true
                    }
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
                },
            contentPadding = PaddingValues(8.dp)
        ) {
            items(VideoPlayerDanmakuMenuItem.values()) { item ->
                val buttonModifier =
                    (if (selectedDanmakuMenuItem == item) Modifier.focusRequester(focusRequester) else Modifier)
                        .width(200.dp)
                MenuListItem(
                    modifier = buttonModifier,
                    text = item.getDisplayName(context),
                    selected = selectedDanmakuMenuItem == item,
                    onClick = {},
                    onFocus = { selectedDanmakuMenuItem = item },
                )
            }
        }
    }
}