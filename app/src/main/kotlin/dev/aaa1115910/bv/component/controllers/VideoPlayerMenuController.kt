package dev.aaa1115910.bv.component.controllers

import android.content.Context
import android.view.KeyEvent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.nativeKeyCode
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.items
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.entity.DanmakuSize
import dev.aaa1115910.bv.entity.DanmakuTransparency
import dev.aaa1115910.bv.entity.Resolution


@Composable
fun VideoPlayerMenuController(
    modifier: Modifier = Modifier,
    resolutionMap: Map<Int, String> = emptyMap(),
    currentResolution: Int? = null,
    currentDanmakuEnabled: Boolean = true,
    currentDanmakuSize: DanmakuSize = DanmakuSize.S2,
    currentDanmakuTransparency: DanmakuTransparency = DanmakuTransparency.T1,
    onChooseResolution: (Int) -> Unit,
    onSwitchDanmaku: (Boolean) -> Unit,
    onDanmakuSizeChange: (DanmakuSize) -> Unit,
    onDanmakuTransparencyChange: (DanmakuTransparency) -> Unit
) {
    var currentMenu by remember { mutableStateOf(VideoPlayerMenuItem.Resolution) }
    var focusInNav by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Surface(
        modifier = modifier,
        color = Color.Black.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .width(400.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            VideoPlayerMenuControllerContent(
                modifier = Modifier.weight(1f),
                onFocusBackMenuList = { focusInNav = true },
                currentMenu = currentMenu,
                resolutionMap = resolutionMap,
                currentResolution = currentResolution,
                currentDanmakuEnabled = currentDanmakuEnabled,
                currentDanmakuSize = currentDanmakuSize,
                currentDanmakuTransparency = currentDanmakuTransparency,
                onChooseResolution = onChooseResolution,
                onSwitchDanmaku = onSwitchDanmaku,
                onDanmakuSizeChange = onDanmakuSizeChange,
                onDanmakuTransparencyChange = onDanmakuTransparencyChange
            )
            VideoPlayerMenuControllerNav(
                modifier = Modifier
                    .onFocusChanged { focusInNav = it.hasFocus }
                    .focusRequester(focusRequester)
                    .weight(1f),
                currentMenu = currentMenu,
                onMenuChanged = { currentMenu = it },
                isFocusing = focusInNav
            )
        }
    }
}

@Composable
private fun VideoPlayerMenuControllerNav(
    modifier: Modifier = Modifier,
    currentMenu: VideoPlayerMenuItem,
    onMenuChanged: (VideoPlayerMenuItem) -> Unit,
    isFocusing: Boolean
) {
    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isFocusing) {
        if (isFocusing) focusRequester.requestFocus()
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    TvLazyColumn(
        modifier = modifier
    ) {
        items(VideoPlayerMenuItem.values()) { item ->
            val buttonModifier = if (currentMenu == item) Modifier
                .focusRequester(focusRequester)
                .fillMaxWidth()
            else Modifier.fillMaxWidth()
            MenuListItem(
                modifier = buttonModifier,
                text = item.getDisplayName(context),
                selected = currentMenu == item,
                onFocus = {
                    onMenuChanged(item)
                },
                onClick = {}
            )
        }
    }
}

private enum class VideoPlayerMenuItem(private val strRes: Int) {
    Resolution(R.string.player_controller_menu_item_resolution),
    DanmakuSwitch(R.string.player_controller_menu_item_danmaku_switch),
    DanmakuSize(R.string.player_controller_menu_item_dankamu_size),
    DanmakuTransparency(R.string.player_controller_menu_item_danmaku_transparency);

    fun getDisplayName(context: Context) = context.getString(strRes)
}


@Composable
private fun VideoPlayerMenuControllerContent(
    modifier: Modifier = Modifier,
    onFocusBackMenuList: () -> Unit,
    currentMenu: VideoPlayerMenuItem,
    resolutionMap: Map<Int, String> = emptyMap(),
    currentResolution: Int? = null,
    currentDanmakuEnabled: Boolean = true,
    currentDanmakuSize: DanmakuSize = DanmakuSize.S2,
    currentDanmakuTransparency: DanmakuTransparency = DanmakuTransparency.T1,
    onChooseResolution: (Int) -> Unit,
    onSwitchDanmaku: (Boolean) -> Unit,
    onDanmakuSizeChange: (DanmakuSize) -> Unit,
    onDanmakuTransparencyChange: (DanmakuTransparency) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .onPreviewKeyEvent {
                val result = it.key.nativeKeyCode == KeyEvent.KEYCODE_DPAD_RIGHT
                if (result) onFocusBackMenuList()
                result
            }
    ) {
        when (currentMenu) {
            VideoPlayerMenuItem.Resolution -> ResolutionMenuContent(
                resolutionMap = resolutionMap,
                currentResolution = currentResolution,
                onResolutionChange = onChooseResolution
            )

            VideoPlayerMenuItem.DanmakuSwitch -> DanmakuSwitchMenuContent(
                currentDanmakuEnabled = currentDanmakuEnabled,
                onSwitchDanmaku = onSwitchDanmaku
            )

            VideoPlayerMenuItem.DanmakuSize -> DanmakuSizeMenuContent(
                currentDanmakuSize = currentDanmakuSize,
                onDanmakuSizeChange = onDanmakuSizeChange
            )

            VideoPlayerMenuItem.DanmakuTransparency -> DanmakuTransparencyMenuContent(
                currentDanmakuTransparency = currentDanmakuTransparency,
                onDanmakuTransparencyChange = onDanmakuTransparencyChange
            )
        }
    }
}



@Composable
private fun ResolutionMenuContent(
    modifier: Modifier = Modifier,
    resolutionMap: Map<Int, String> = emptyMap(),
    currentResolution: Int?,
    onResolutionChange: (Int) -> Unit,
) {
    val context = LocalContext.current
    val qualityMap by remember { mutableStateOf(resolutionMap.toSortedMap(compareByDescending { it })) }

    TvLazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 120.dp)
    ) {
        items(items = qualityMap.keys.toList()) { id ->
            MenuListItem(
                modifier = Modifier.fillMaxWidth(),
                text = runCatching {
                    Resolution.values().find { it.code == id }!!.getShortDisplayName(context)
                }.getOrDefault("unknown: $id"),
                selected = currentResolution == id
            ) { onResolutionChange(id) }
        }
    }
}

@Composable
private fun DanmakuSwitchMenuContent(
    modifier: Modifier = Modifier,
    currentDanmakuEnabled: Boolean,
    onSwitchDanmaku: (Boolean) -> Unit
) {
    TvLazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 120.dp)
    ) {
        item {
            MenuListItem(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.player_controller_menu_danmaku_enabled),
                selected = currentDanmakuEnabled
            ) { onSwitchDanmaku(true) }
        }
        item {
            MenuListItem(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.player_controller_menu_danmaku_disabled),
                selected = !currentDanmakuEnabled
            ) { onSwitchDanmaku(false) }
        }
    }
}

@Composable
private fun DanmakuSizeMenuContent(
    modifier: Modifier = Modifier,
    currentDanmakuSize: DanmakuSize = DanmakuSize.S2,
    onDanmakuSizeChange: (DanmakuSize) -> Unit
) {
    TvLazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 120.dp)
    ) {
        items(items = DanmakuSize.values()) { danmakuSize ->
            MenuListItem(
                modifier = Modifier.fillMaxWidth(),
                text = "${danmakuSize.scale}x",
                selected = currentDanmakuSize == danmakuSize
            ) { onDanmakuSizeChange(danmakuSize) }
        }
    }
}

@Composable
private fun DanmakuTransparencyMenuContent(
    modifier: Modifier = Modifier,
    currentDanmakuTransparency: DanmakuTransparency = DanmakuTransparency.T1,
    onDanmakuTransparencyChange: (DanmakuTransparency) -> Unit
) {
    TvLazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 120.dp)
    ) {
        items(items = DanmakuTransparency.values()) { danmakuTransparency ->
            MenuListItem(
                modifier = Modifier.fillMaxWidth(),
                text = "${danmakuTransparency.transparency}",
                selected = currentDanmakuTransparency == danmakuTransparency
            ) { onDanmakuTransparencyChange(danmakuTransparency) }
        }
    }
}


@Composable
private fun MenuListItem(
    modifier: Modifier = Modifier,
    text: String,
    selected: Boolean,
    onFocus: () -> Unit = {},
    onClick: () -> Unit
) {
    var hasFocus by remember { mutableStateOf(false) }

    val buttonBackgroundColor =
        if (hasFocus) MaterialTheme.colorScheme.primary
        else if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
        else Color.Transparent

    Surface(
        modifier = modifier
            .onFocusChanged {
                hasFocus = it.hasFocus
                if (hasFocus) onFocus()
            }
            .clickable { onClick() },
        color = buttonBackgroundColor,
        shape = MaterialTheme.shapes.small
    ) {
        Box {
            Text(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(
                        vertical = 6.dp,
                        horizontal = 24.dp
                    ),
                text = text,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}
