package dev.aaa1115910.bv.component.controllers2.playermenu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.tv.foundation.ExperimentalTvFoundationApi
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.itemsIndexed
import dev.aaa1115910.bv.component.FocusGroup
import dev.aaa1115910.bv.component.controllers.LocalVideoPlayerControllerData
import dev.aaa1115910.bv.component.controllers2.LocalMenuFocusStateData
import dev.aaa1115910.bv.component.controllers2.MenuFocusState
import dev.aaa1115910.bv.component.controllers2.VideoPlayerPictureMenuItem
import dev.aaa1115910.bv.component.controllers2.playermenu.component.MenuListItem
import dev.aaa1115910.bv.component.controllers2.playermenu.component.RadioMenuList
import dev.aaa1115910.bv.component.controllers2.playermenu.component.StepLessMenuItem
import dev.aaa1115910.bv.entity.Audio
import dev.aaa1115910.bv.entity.Resolution
import dev.aaa1115910.bv.entity.VideoAspectRatio
import dev.aaa1115910.bv.entity.VideoCodec
import kotlin.math.roundToInt

@OptIn(ExperimentalTvFoundationApi::class)
@Composable
fun PictureMenuList(
    modifier: Modifier = Modifier,
    onResolutionChange: (Int) -> Unit,
    onCodecChange: (VideoCodec) -> Unit,
    onAspectRatioChange: (VideoAspectRatio) -> Unit,
    onPlaySpeedChange: (Float) -> Unit,
    onAudioChange: (Audio) -> Unit,
    onFocusStateChange: (MenuFocusState) -> Unit
) {
    val context = LocalContext.current
    val focusState = LocalMenuFocusStateData.current
    val data = LocalVideoPlayerControllerData.current

    val focusRequester = remember { FocusRequester() }
    var selectedPictureMenuItem by remember { mutableStateOf(VideoPlayerPictureMenuItem.Resolution) }
    val resolutionMap = remember(data.resolutionMap) {
        data.resolutionMap
            .toList()
            .sortedByDescending { (key, _) -> key }
            .toMap()
    }
    val audioList = remember(data.availableAudio) {
        data.availableAudio.sortedBy { it.ordinal }
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
                VideoPlayerPictureMenuItem.Resolution -> RadioMenuList(
                    modifier = menuItemsModifier,
                    items = resolutionMap.keys.toList().map { resolutionCode ->
                        runCatching {
                            Resolution.values().find { it.code == resolutionCode }!!
                                .getShortDisplayName(context)
                        }.getOrDefault("unknown: $resolutionCode")
                    },
                    selected = resolutionMap.keys.indexOf(data.currentResolution),
                    onSelectedChanged = { onResolutionChange(resolutionMap.keys.toList()[it]) },
                    onFocusBackToParent = {
                        onFocusStateChange(MenuFocusState.Menu)
                        focusRequester.requestFocus()
                    }
                )

                VideoPlayerPictureMenuItem.Codec -> RadioMenuList(
                    modifier = menuItemsModifier,
                    items = data.availableVideoCodec.map { it.getDisplayName(context) },
                    selected = data.availableVideoCodec.indexOf(data.currentVideoCodec),
                    onSelectedChanged = { onCodecChange(data.availableVideoCodec[it]) },
                    onFocusBackToParent = {
                        onFocusStateChange(MenuFocusState.Menu)
                        focusRequester.requestFocus()
                    }
                )

                VideoPlayerPictureMenuItem.AspectRatio -> RadioMenuList(
                    modifier = menuItemsModifier,
                    items = VideoAspectRatio.values().map { it.getDisplayName(context) },
                    selected = VideoAspectRatio.values().indexOf(data.currentVideoAspectRatio),
                    onSelectedChanged = { onAspectRatioChange(VideoAspectRatio.values()[it]) },
                    onFocusBackToParent = {
                        onFocusStateChange(MenuFocusState.Menu)
                        focusRequester.requestFocus()
                    }
                )

                VideoPlayerPictureMenuItem.PlaySpeed -> StepLessMenuItem(
                    modifier = menuItemsModifier,
                    value = data.currentVideoSpeed,
                    step = 0.25f,
                    range = 0.25f..2f,
                    text = "${(data.currentVideoSpeed * 100).roundToInt() / 100f}x",
                    onValueChange = onPlaySpeedChange,
                    onFocusBackToParent = { onFocusStateChange(MenuFocusState.Menu) }
                )

                VideoPlayerPictureMenuItem.Audio -> RadioMenuList(
                    modifier = menuItemsModifier,
                    items = audioList.map { audio -> audio.getDisplayName(context) },
                    selected = audioList.indexOf(data.currentAudio),
                    onSelectedChanged = { onAudioChange(audioList[it]) },
                    onFocusBackToParent = {
                        onFocusStateChange(MenuFocusState.Menu)
                        focusRequester.requestFocus()
                    }
                )
            }
        }
        FocusGroup(
            modifier = Modifier.focusRequester(focusRequester)
        ) {
            TvLazyColumn(
                modifier = Modifier
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
                    },
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(8.dp)
            ) {
                itemsIndexed(VideoPlayerPictureMenuItem.values()) { index, item ->
                    val buttonModifier =
                        (if (index == 0) Modifier.initiallyFocused() else Modifier.restorableFocus())
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
}