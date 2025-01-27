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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aaa1115910.biliapi.entity.video.Subtitle
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerConfigData
import dev.aaa1115910.bv.player.entity.VideoPlayerClosedCaptionMenuItem
import dev.aaa1115910.bv.player.tv.controller.LocalMenuFocusStateData
import dev.aaa1115910.bv.player.tv.controller.MenuFocusState
import dev.aaa1115910.bv.player.tv.controller.playermenu.component.MenuListItem
import dev.aaa1115910.bv.player.tv.controller.playermenu.component.RadioMenuList
import dev.aaa1115910.bv.player.tv.controller.playermenu.component.StepLessMenuItem
import dev.aaa1115910.bv.util.ifElse
import java.text.NumberFormat

@Composable
fun ClosedCaptionMenuList(
    modifier: Modifier = Modifier,
    onSubtitleChange: (Subtitle) -> Unit,
    onSubtitleSizeChange: (TextUnit) -> Unit,
    onSubtitleBackgroundOpacityChange: (Float) -> Unit,
    onSubtitleBottomPadding: (Dp) -> Unit,
    onFocusStateChange: (MenuFocusState) -> Unit
) {
    val context = LocalContext.current
    val videoPlayerConfigData = LocalVideoPlayerConfigData.current
    val focusState = LocalMenuFocusStateData.current
    val focusRestorerModifiers = dev.aaa1115910.bv.util.createCustomInitialFocusRestorerModifiers()

    val focusRequester = remember { FocusRequester() }
    var selectedClosedCaptionMenuItem by remember { mutableStateOf(VideoPlayerClosedCaptionMenuItem.Switch) }

    Row(
        modifier = modifier.fillMaxHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val menuItemsModifier = Modifier
            .width(216.dp)
            .padding(horizontal = 8.dp)
        AnimatedVisibility(visible = focusState.focusState != MenuFocusState.MenuNav) {
            when (selectedClosedCaptionMenuItem) {
                VideoPlayerClosedCaptionMenuItem.Switch -> RadioMenuList(
                    modifier = menuItemsModifier,
                    items = videoPlayerConfigData.availableSubtitleTracks.map { it.langDoc },
                    selected = videoPlayerConfigData.availableSubtitleTracks
                        .indexOfFirst { it.id == videoPlayerConfigData.currentSubtitleId },
                    onSelectedChanged = { onSubtitleChange(videoPlayerConfigData.availableSubtitleTracks[it]) },
                    onFocusBackToParent = {
                        onFocusStateChange(MenuFocusState.Menu)
                        focusRequester.requestFocus()
                    },
                )

                VideoPlayerClosedCaptionMenuItem.Size -> StepLessMenuItem(
                    modifier = menuItemsModifier,
                    value = videoPlayerConfigData.currentSubtitleFontSize.value.toInt(),
                    step = 1,
                    range = 12..48,
                    text = "${videoPlayerConfigData.currentSubtitleFontSize.value.toInt()} SP",
                    onValueChange = { onSubtitleSizeChange(it.sp) },
                    onFocusBackToParent = { onFocusStateChange(MenuFocusState.Menu) }
                )

                VideoPlayerClosedCaptionMenuItem.Opacity -> StepLessMenuItem(
                    modifier = menuItemsModifier,
                    value = videoPlayerConfigData.currentSubtitleBackgroundOpacity,
                    step = 0.01f,
                    range = 0f..1f,
                    text = NumberFormat.getPercentInstance()
                        .apply { maximumFractionDigits = 0 }
                        .format(videoPlayerConfigData.currentSubtitleBackgroundOpacity),
                    onValueChange = onSubtitleBackgroundOpacityChange,
                    onFocusBackToParent = { onFocusStateChange(MenuFocusState.Menu) }
                )

                VideoPlayerClosedCaptionMenuItem.Padding -> StepLessMenuItem(
                    modifier = menuItemsModifier,
                    value = videoPlayerConfigData.currentSubtitleBottomPadding.value.toInt(),
                    step = 1,
                    range = 0..48,
                    text = "${videoPlayerConfigData.currentSubtitleBottomPadding.value.toInt()} DP",
                    onValueChange = { onSubtitleBottomPadding(it.dp) },
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
            itemsIndexed(VideoPlayerClosedCaptionMenuItem.entries) { index, item ->
                MenuListItem(
                    modifier = Modifier
                        .ifElse(index == 0, focusRestorerModifiers.childModifier),
                    text = item.getDisplayName(context),
                    selected = selectedClosedCaptionMenuItem == item,
                    onClick = {},
                    onFocus = { selectedClosedCaptionMenuItem = item },
                )
            }
        }
    }
}