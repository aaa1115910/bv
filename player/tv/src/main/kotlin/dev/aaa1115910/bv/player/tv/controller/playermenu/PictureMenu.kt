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
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.aaa1115910.bv.player.entity.Audio
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerConfigData
import dev.aaa1115910.bv.player.entity.Resolution
import dev.aaa1115910.bv.player.entity.VideoAspectRatio
import dev.aaa1115910.bv.player.entity.VideoCodec
import dev.aaa1115910.bv.player.entity.VideoPlayerPictureMenuItem
import dev.aaa1115910.bv.player.tv.controller.LocalMenuFocusStateData
import dev.aaa1115910.bv.player.tv.controller.MenuFocusState
import dev.aaa1115910.bv.player.tv.controller.playermenu.component.MenuListItem
import dev.aaa1115910.bv.player.tv.controller.playermenu.component.RadioMenuList
import dev.aaa1115910.bv.player.tv.controller.playermenu.component.StepLessMenuItem
import dev.aaa1115910.bv.util.ifElse
import kotlin.math.roundToInt

@Composable
fun PictureMenuList(
    modifier: Modifier = Modifier,
    onResolutionChange: (Resolution) -> Unit,
    onCodecChange: (VideoCodec) -> Unit,
    onAspectRatioChange: (VideoAspectRatio) -> Unit,
    onPlaySpeedChange: (Float) -> Unit,
    onAudioChange: (Audio) -> Unit,
    onFocusStateChange: (MenuFocusState) -> Unit
) {
    val context = LocalContext.current
    val focusState = LocalMenuFocusStateData.current
    val videoPlayerConfigData = LocalVideoPlayerConfigData.current
    val parentMenuFocusRequester = remember { FocusRequester() }
    val parentMenuPositionFocusRequester = remember { FocusRequester() }
    var selectedPictureMenuItem by remember { mutableStateOf(VideoPlayerPictureMenuItem.PlaySpeed) }
    val resolutionList = remember(videoPlayerConfigData.availableResolutions) {
        videoPlayerConfigData.availableResolutions.sortedByDescending { it.code }
    }
    val audioList = remember(videoPlayerConfigData.availableAudio) {
        videoPlayerConfigData.availableAudio.sortedBy { it.ordinal }
    }

    Row(
        modifier = modifier.fillMaxHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val menuItemsModifier = Modifier
            .width(216.dp)
            .padding(horizontal = 8.dp)
        AnimatedVisibility(visible = focusState.focusState != MenuFocusState.MenuNav) {
            when (selectedPictureMenuItem) {
                VideoPlayerPictureMenuItem.Resolution -> RadioMenuList(
                    modifier = menuItemsModifier,
                    items = resolutionList.map { resolution ->
                        resolution.getShortDisplayName(context)
                    },
                    selected = resolutionList.indexOf(videoPlayerConfigData.currentResolution),
                    onSelectedChanged = { onResolutionChange(resolutionList[it]) },
                    onFocusBackToParent = {
                        onFocusStateChange(MenuFocusState.Menu)
                        parentMenuFocusRequester.requestFocus()
                    }
                )

                VideoPlayerPictureMenuItem.Codec -> RadioMenuList(
                    modifier = menuItemsModifier,
                    items = videoPlayerConfigData.availableVideoCodec
                        .map { it.getDisplayName(context) },
                    selected = videoPlayerConfigData.availableVideoCodec
                        .indexOf(videoPlayerConfigData.currentVideoCodec),
                    onSelectedChanged = { onCodecChange(videoPlayerConfigData.availableVideoCodec[it]) },
                    onFocusBackToParent = {
                        onFocusStateChange(MenuFocusState.Menu)
                        parentMenuFocusRequester.requestFocus()
                    }
                )

                VideoPlayerPictureMenuItem.AspectRatio -> RadioMenuList(
                    modifier = menuItemsModifier,
                    items = VideoAspectRatio.entries.map { it.getDisplayName(context) },
                    selected = VideoAspectRatio.entries
                        .indexOf(videoPlayerConfigData.currentVideoAspectRatio),
                    onSelectedChanged = { onAspectRatioChange(VideoAspectRatio.entries[it]) },
                    onFocusBackToParent = {
                        onFocusStateChange(MenuFocusState.Menu)
                        parentMenuFocusRequester.requestFocus()
                    }
                )

                VideoPlayerPictureMenuItem.PlaySpeed -> StepLessMenuItem(
                    modifier = menuItemsModifier,
                    value = videoPlayerConfigData.currentVideoSpeed,
                    step = 0.25f,
                    range = 0.25f..3f,
                    text = "${(videoPlayerConfigData.currentVideoSpeed * 100).roundToInt() / 100f}x",
                    onValueChange = onPlaySpeedChange,
                    onFocusBackToParent = { onFocusStateChange(MenuFocusState.Menu) }
                )

                VideoPlayerPictureMenuItem.Audio -> RadioMenuList(
                    modifier = menuItemsModifier,
                    items = audioList.map { audio -> audio.getDisplayName(context) },
                    selected = audioList.indexOf(videoPlayerConfigData.currentAudio),
                    onSelectedChanged = { onAudioChange(audioList[it]) },
                    onFocusBackToParent = {
                        onFocusStateChange(MenuFocusState.Menu)
                        parentMenuFocusRequester.requestFocus()
                    }
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .focusRequester(parentMenuFocusRequester)
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
                .focusRestorer(parentMenuPositionFocusRequester),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(8.dp)
        ) {
            itemsIndexed(VideoPlayerPictureMenuItem.entries.toMutableList()) { index, item ->
                MenuListItem(
                    modifier = Modifier
                        .ifElse(
                            index == 0,
                            Modifier.focusRequester(parentMenuPositionFocusRequester)
                        ),
                    text = item.getDisplayName(context),
                    selected = selectedPictureMenuItem == item,
                    onClick = {},
                    onFocus = { selectedPictureMenuItem = item },
                )
            }
        }
    }
}