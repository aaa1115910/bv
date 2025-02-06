package dev.aaa1115910.bv.player.tv.controller.playermenu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import dev.aaa1115910.bv.player.entity.DanmakuType
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerConfigData
import dev.aaa1115910.bv.player.entity.VideoPlayerDanmakuMenuItem
import dev.aaa1115910.bv.player.tv.controller.LocalMenuFocusStateData
import dev.aaa1115910.bv.player.tv.controller.MenuFocusState
import dev.aaa1115910.bv.player.tv.controller.playermenu.component.CheckBoxMenuList
import dev.aaa1115910.bv.player.tv.controller.playermenu.component.MenuListItem
import dev.aaa1115910.bv.player.tv.controller.playermenu.component.RadioMenuList
import dev.aaa1115910.bv.player.tv.controller.playermenu.component.StepLessMenuItem
import dev.aaa1115910.bv.util.createCustomInitialFocusRestorerModifiers
import dev.aaa1115910.bv.util.ifElse
import java.text.NumberFormat

@Composable
fun DanmakuMenuList(
    modifier: Modifier = Modifier,
    onDanmakuSwitchChange: (List<DanmakuType>) -> Unit,
    onDanmakuSizeChange: (Float) -> Unit,
    onDanmakuOpacityChange: (Float) -> Unit,
    onDanmakuAreaChange: (Float) -> Unit,
    onDanmakuMaskChange: (Boolean) -> Unit,
    onFocusStateChange: (MenuFocusState) -> Unit
) {
    val context = LocalContext.current
    val videoPlayerConfigData = LocalVideoPlayerConfigData.current
    val focusState = LocalMenuFocusStateData.current
    val focusRestorerModifiers = createCustomInitialFocusRestorerModifiers()

    val focusRequester = remember { FocusRequester() }
    var selectedDanmakuMenuItem by remember { mutableStateOf(VideoPlayerDanmakuMenuItem.Switch) }

    Row(
        modifier = modifier.fillMaxHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val menuItemsModifier = Modifier
            .width(216.dp)
            .padding(horizontal = 8.dp)
        AnimatedVisibility(visible = focusState.focusState != MenuFocusState.MenuNav) {
            when (selectedDanmakuMenuItem) {
                VideoPlayerDanmakuMenuItem.Switch -> CheckBoxMenuList(
                    modifier = menuItemsModifier,
                    items = DanmakuType.entries.map { it.getDisplayName(context) },
                    selected = videoPlayerConfigData.currentDanmakuEnabledList.map { it.ordinal },
                    onSelectedChanged = {
                        val newEnabledDanmakuList = it
                            .map { index -> DanmakuType.entries[index] }
                            .toMutableList()

                        if (
                            newEnabledDanmakuList.contains(DanmakuType.All) &&
                            !videoPlayerConfigData.currentDanmakuEnabledList.contains(DanmakuType.All)
                        ) {
                            // 勾选了全部
                            onDanmakuSwitchChange(DanmakuType.entries)
                        } else if (
                            videoPlayerConfigData.currentDanmakuEnabledList.contains(DanmakuType.All) &&
                            !newEnabledDanmakuList.contains(DanmakuType.All)
                        ) {
                            // 取消了全部
                            onDanmakuSwitchChange(listOf())
                        } else if (
                            videoPlayerConfigData.currentDanmakuEnabledList.contains(DanmakuType.All) &&
                            newEnabledDanmakuList.contains(DanmakuType.All) &&
                            videoPlayerConfigData.currentDanmakuEnabledList.size != newEnabledDanmakuList.size
                        ) {
                            // 在勾选全部时，取消某一项
                            newEnabledDanmakuList.remove(DanmakuType.All)
                            onDanmakuSwitchChange(newEnabledDanmakuList)
                        } else if (
                            !videoPlayerConfigData.currentDanmakuEnabledList.contains(DanmakuType.All) &&
                            newEnabledDanmakuList.size == DanmakuType.entries.size - 1
                        ) {
                            // 在勾选了全部之外的所有项时，勾选全部项
                            onDanmakuSwitchChange(DanmakuType.entries)
                        } else {
                            onDanmakuSwitchChange(newEnabledDanmakuList)
                        }
                    },
                    onFocusBackToParent = {
                        onFocusStateChange(MenuFocusState.Menu)
                        focusRequester.requestFocus()
                    }
                )

                VideoPlayerDanmakuMenuItem.Size -> StepLessMenuItem(
                    modifier = menuItemsModifier,
                    value = videoPlayerConfigData.currentDanmakuScale,
                    step = 0.01f,
                    range = 0.5f..4f,
                    text = NumberFormat.getPercentInstance()
                        .apply { maximumFractionDigits = 0 }
                        .format(videoPlayerConfigData.currentDanmakuScale),
                    onValueChange = onDanmakuSizeChange,
                    onFocusBackToParent = { onFocusStateChange(MenuFocusState.Menu) }
                )

                VideoPlayerDanmakuMenuItem.Opacity -> StepLessMenuItem(
                    modifier = menuItemsModifier,
                    value = videoPlayerConfigData.currentDanmakuOpacity,
                    step = 0.01f,
                    range = 0f..1f,
                    text = NumberFormat.getPercentInstance()
                        .apply { maximumFractionDigits = 0 }
                        .format(videoPlayerConfigData.currentDanmakuOpacity),
                    onValueChange = onDanmakuOpacityChange,
                    onFocusBackToParent = { onFocusStateChange(MenuFocusState.Menu) }
                )

                VideoPlayerDanmakuMenuItem.Area -> StepLessMenuItem(
                    modifier = menuItemsModifier,
                    value = videoPlayerConfigData.currentDanmakuArea,
                    step = 0.01f,
                    range = 0f..1f,
                    text = NumberFormat.getPercentInstance()
                        .apply { maximumFractionDigits = 0 }
                        .format(videoPlayerConfigData.currentDanmakuArea),
                    onValueChange = onDanmakuAreaChange,
                    onFocusBackToParent = { onFocusStateChange(MenuFocusState.Menu) }
                )

                VideoPlayerDanmakuMenuItem.Mask -> RadioMenuList(
                    modifier = menuItemsModifier,
                    items = listOf("关闭", "开启"),
                    selected = if (videoPlayerConfigData.currentDanmakuMask) 1 else 0,
                    onSelectedChanged = { onDanmakuMaskChange(it == 1) },
                    onFocusBackToParent = { onFocusStateChange(MenuFocusState.Menu) }
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .focusRequester(focusRequester)
                .padding(horizontal = 8.dp)
                .onPreviewKeyEvent {
                    if (it.type == KeyEventType.KeyUp) {
                        if (listOf(Key.Enter, Key.DirectionCenter).contains(it.key)) {
                            return@onPreviewKeyEvent false
                        }
                        return@onPreviewKeyEvent true
                    }
                    when (it.key) {
                        Key.DirectionRight -> onFocusStateChange(MenuFocusState.MenuNav)
                        Key.DirectionLeft -> onFocusStateChange(MenuFocusState.Items)
                        else -> {}
                    }
                    false
                }
                .then(focusRestorerModifiers.parentModifier),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(8.dp)
        ) {
            itemsIndexed(VideoPlayerDanmakuMenuItem.entries) { index, item ->
                MenuListItem(
                    modifier = Modifier
                        .ifElse(index == 0, focusRestorerModifiers.childModifier),
                    text = item.getDisplayName(context),
                    selected = selectedDanmakuMenuItem == item,
                    onClick = {},
                    onFocus = { selectedDanmakuMenuItem = item },
                )
            }
        }
    }
}