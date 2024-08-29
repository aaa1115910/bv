package dev.aaa1115910.bv.component.controllers

import android.content.Context
import android.view.KeyEvent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.DenseListItem
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import androidx.tv.material3.Text
import dev.aaa1115910.biliapi.http.entity.video.VideoMoreInfo
import dev.aaa1115910.bv.BuildConfig
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.component.BottomTip
import dev.aaa1115910.bv.entity.DanmakuSize
import dev.aaa1115910.bv.entity.DanmakuTransparency
import dev.aaa1115910.bv.entity.Resolution
import dev.aaa1115910.bv.entity.VideoAspectRatio
import dev.aaa1115910.bv.entity.VideoCodec
import dev.aaa1115910.bv.util.requestFocus
import io.github.oshai.kotlinlogging.KotlinLogging
import java.text.NumberFormat

@Composable
fun VideoPlayerMenuController(
    modifier: Modifier = Modifier,
    onChooseResolution: (Int) -> Unit,
    onChooseVideoCodec: (VideoCodec) -> Unit,
    onChooseVideoAspectRatio: (VideoAspectRatio) -> Unit,
    onSwitchDanmaku: (Boolean) -> Unit,
    onDanmakuSizeChange: (DanmakuSize) -> Unit,
    onDanmakuTransparencyChange: (DanmakuTransparency) -> Unit,
    onDanmakuAreaChange: (Float) -> Unit,
    onSubtitleChange: (Long) -> Unit,
    onSubtitleFontSizeChange: (TextUnit) -> Unit,
    onSubtitleBottomPaddingChange: (Dp) -> Unit
) {
    val scope = rememberCoroutineScope()

    var currentMenu by remember { mutableStateOf(VideoPlayerMenuItem.Resolution) }
    var focusInNav by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus(scope)
    }

    Surface(
        modifier = modifier,
        colors = SurfaceDefaults.colors(
            containerColor = Color.Black.copy(alpha = 0.5f)
        )
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
                onChooseResolution = onChooseResolution,
                onChooseVideoCodec = onChooseVideoCodec,
                onChooseVideoAspectRatio = onChooseVideoAspectRatio,
                onSwitchDanmaku = onSwitchDanmaku,
                onDanmakuSizeChange = onDanmakuSizeChange,
                onDanmakuTransparencyChange = onDanmakuTransparencyChange,
                onDanmakuAreaChange = onDanmakuAreaChange,
                onSubtitleChange = onSubtitleChange,
                onSubtitleFontSizeChange = onSubtitleFontSizeChange,
                onSubtitleBottomPaddingChange = onSubtitleBottomPaddingChange
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
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isFocusing) {
        if (isFocusing) focusRequester.requestFocus(scope)
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus(scope)
    }

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 120.dp)
    ) {
        items(items = VideoPlayerMenuItem.entries, key = { it.ordinal }) { item ->
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
    VideoCodec(R.string.player_controller_menu_item_video_codec),
    VideoAspectRatio(R.string.player_controller_menu_item_video_aspect_ratio),
    DanmakuSwitch(R.string.player_controller_menu_item_danmaku_switch),
    DanmakuSize(R.string.player_controller_menu_item_dankamu_size),
    DanmakuTransparency(R.string.player_controller_menu_item_danmaku_transparency),
    DanmakuArea(R.string.player_controller_menu_item_danmaku_area),
    Subtitle(R.string.player_controller_menu_item_subtitle),
    SubtitleSize(R.string.player_controller_menu_item_subtitle_font_size),
    SubtitlePadding(R.string.player_controller_menu_item_subtitle_padding);

    fun getDisplayName(context: Context) = context.getString(strRes)
}


@Composable
private fun VideoPlayerMenuControllerContent(
    modifier: Modifier = Modifier,
    onFocusBackMenuList: () -> Unit,
    currentMenu: VideoPlayerMenuItem,
    onChooseResolution: (Int) -> Unit,
    onChooseVideoCodec: (VideoCodec) -> Unit,
    onChooseVideoAspectRatio: (VideoAspectRatio) -> Unit,
    onSwitchDanmaku: (Boolean) -> Unit,
    onDanmakuSizeChange: (DanmakuSize) -> Unit,
    onDanmakuTransparencyChange: (DanmakuTransparency) -> Unit,
    onDanmakuAreaChange: (Float) -> Unit,
    onSubtitleChange: (Long) -> Unit,
    onSubtitleFontSizeChange: (TextUnit) -> Unit,
    onSubtitleBottomPaddingChange: (Dp) -> Unit
) {
    val videoPlayerControllerData = LocalVideoPlayerControllerData.current
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
                resolutionMap = videoPlayerControllerData.resolutionMap,
                currentResolution = videoPlayerControllerData.currentResolution,
                onResolutionChange = onChooseResolution
            )

            VideoPlayerMenuItem.DanmakuSwitch -> DanmakuSwitchMenuContent(
                currentDanmakuEnabled = videoPlayerControllerData.currentDanmakuEnabled,
                onSwitchDanmaku = onSwitchDanmaku
            )

            VideoPlayerMenuItem.DanmakuSize -> DanmakuSizeMenuContent(
                currentDanmakuSize = videoPlayerControllerData.currentDanmakuSize,
                onDanmakuSizeChange = onDanmakuSizeChange
            )

            VideoPlayerMenuItem.DanmakuTransparency -> DanmakuTransparencyMenuContent(
                currentDanmakuTransparency = videoPlayerControllerData.currentDanmakuTransparency,
                onDanmakuTransparencyChange = onDanmakuTransparencyChange
            )

            VideoPlayerMenuItem.VideoCodec -> VideoCodecMenuContent(
                availableVideoCodec = videoPlayerControllerData.availableVideoCodec,
                currentVideoCodec = videoPlayerControllerData.currentVideoCodec,
                onVideoCodecChange = onChooseVideoCodec
            )

            VideoPlayerMenuItem.DanmakuArea -> DanmakuAreaMenuContent(
                currentDanmakuArea = videoPlayerControllerData.currentDanmakuArea,
                onDanmakuAreaChange = onDanmakuAreaChange
            )

            VideoPlayerMenuItem.VideoAspectRatio -> VideoAspectRatioMenuContent(
                currentVideoAspectRatio = videoPlayerControllerData.currentVideoAspectRatio,
                onVideoAspectRatioChange = onChooseVideoAspectRatio
            )

            VideoPlayerMenuItem.Subtitle -> SubtitleContent(
                currentSubtitleId = videoPlayerControllerData.currentSubtitleId,
                availableSubtitle = videoPlayerControllerData.availableSubtitle,
                onSubtitleChange = onSubtitleChange
            )

            VideoPlayerMenuItem.SubtitleSize -> SubtitleFontSizeContent(
                currentFontSize = videoPlayerControllerData.currentSubtitleFontSize,
                onSubtitleFontSizeChange = onSubtitleFontSizeChange
            )

            VideoPlayerMenuItem.SubtitlePadding -> SubtitleBottomPaddingContent(
                currentBottomPadding = videoPlayerControllerData.currentSubtitleBottomPadding,
                onSubtitleBottomPaddingChange = onSubtitleBottomPaddingChange
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

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 120.dp)
    ) {
        items(items = qualityMap.keys.toList()) { id ->
            MenuListItem(
                modifier = Modifier.fillMaxWidth(),
                text = runCatching {
                    Resolution.entries.find { it.code == id }!!.getShortDisplayName(context)
                }.getOrDefault("unknown: $id"),
                selected = currentResolution == id
            ) { onResolutionChange(id) }
        }
    }
}

@Composable
private fun VideoCodecMenuContent(
    modifier: Modifier = Modifier,
    availableVideoCodec: List<VideoCodec> = emptyList(),
    currentVideoCodec: VideoCodec,
    onVideoCodecChange: (VideoCodec) -> Unit,
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 120.dp)
    ) {
        items(items = availableVideoCodec) { videoCodec ->
            MenuListItem(
                modifier = Modifier.fillMaxWidth(),
                text = videoCodec.getDisplayName(context),
                selected = currentVideoCodec == videoCodec
            ) { onVideoCodecChange(videoCodec) }
        }
    }
}

@Composable
private fun VideoAspectRatioMenuContent(
    modifier: Modifier = Modifier,
    currentVideoAspectRatio: VideoAspectRatio,
    onVideoAspectRatioChange: (VideoAspectRatio) -> Unit,
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 120.dp)
    ) {
        items(items = VideoAspectRatio.entries) { aspectRatio ->
            MenuListItem(
                modifier = Modifier.fillMaxWidth(),
                text = aspectRatio.getDisplayName(context),
                selected = currentVideoAspectRatio == aspectRatio
            ) { onVideoAspectRatioChange(aspectRatio) }
        }
    }
}

@Composable
private fun DanmakuSwitchMenuContent(
    modifier: Modifier = Modifier,
    currentDanmakuEnabled: Boolean,
    onSwitchDanmaku: (Boolean) -> Unit
) {
    LazyColumn(
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
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 120.dp)
    ) {
        items(items = DanmakuSize.entries) { danmakuSize ->
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
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 120.dp)
    ) {
        items(items = DanmakuTransparency.entries) { danmakuTransparency ->
            MenuListItem(
                modifier = Modifier.fillMaxWidth(),
                text = "${danmakuTransparency.transparency}",
                selected = currentDanmakuTransparency == danmakuTransparency
            ) { onDanmakuTransparencyChange(danmakuTransparency) }
        }
    }
}

@Composable
private fun DanmakuAreaMenuContent(
    modifier: Modifier = Modifier,
    currentDanmakuArea: Float = 1f,
    onDanmakuAreaChange: (Float) -> Unit
) {
    val logger = KotlinLogging.logger { }

    val getAreaDisplayString: () -> String = {
        val percentInstance: NumberFormat = NumberFormat.getPercentInstance()
        percentInstance.maximumFractionDigits = 0
        percentInstance.format(currentDanmakuArea)
    }
    var currentDanmakuAreaString by remember { mutableStateOf(getAreaDisplayString()) }

    LaunchedEffect(currentDanmakuArea) {
        currentDanmakuAreaString = getAreaDisplayString()
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        MenuListItem(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .onPreviewKeyEvent {
                    if (BuildConfig.DEBUG) logger.info { "Native key event: ${it.nativeKeyEvent}" }

                    when (it.nativeKeyEvent.keyCode) {
                        KeyEvent.KEYCODE_DPAD_UP -> {
                            if (it.nativeKeyEvent.action == KeyEvent.ACTION_UP) return@onPreviewKeyEvent true
                            if (currentDanmakuArea >= 0.99f) {
                                onDanmakuAreaChange(1f)
                            } else {
                                onDanmakuAreaChange(currentDanmakuArea + 0.01f)
                            }
                            return@onPreviewKeyEvent true
                        }

                        KeyEvent.KEYCODE_DPAD_DOWN -> {
                            if (it.nativeKeyEvent.action == KeyEvent.ACTION_UP) return@onPreviewKeyEvent true
                            if (currentDanmakuArea <= 0.01f) {
                                onDanmakuAreaChange(0f)
                            } else {
                                onDanmakuAreaChange(currentDanmakuArea - 0.01f)
                            }
                            return@onPreviewKeyEvent true
                        }
                    }
                    false
                },
            text = currentDanmakuAreaString,
            selected = false
        ) { }

        BottomTip(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp),
            text = stringResource(R.string.video_controller_menu_danmaku_area_tip)
        )
    }
}

@Composable
fun SubtitleContent(
    modifier: Modifier = Modifier,
    currentSubtitleId: Long = 0,
    availableSubtitle: List<VideoMoreInfo.SubtitleItem> = emptyList(),
    onSubtitleChange: (Long) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 120.dp)
    ) {
        item {
            MenuListItem(
                modifier = Modifier.fillMaxWidth(),
                text = "关闭",
                selected = currentSubtitleId == 0L
            ) { onSubtitleChange(0) }
        }
        items(items = availableSubtitle, key = { it.id }) { subtitle ->
            MenuListItem(
                modifier = Modifier.fillMaxWidth(),
                text = subtitle.lanDoc,
                selected = subtitle.id == currentSubtitleId
            ) { onSubtitleChange(subtitle.id) }
        }
    }
}

@Composable
fun SubtitleFontSizeContent(
    modifier: Modifier = Modifier,
    currentFontSize: TextUnit = 24.sp,
    onSubtitleFontSizeChange: (TextUnit) -> Unit
) {
    val logger = KotlinLogging.logger { }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        MenuListItem(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .onPreviewKeyEvent {
                    if (BuildConfig.DEBUG) logger.info { "Native key event: ${it.nativeKeyEvent}" }

                    when (it.nativeKeyEvent.keyCode) {
                        KeyEvent.KEYCODE_DPAD_UP -> {
                            if (it.nativeKeyEvent.action == KeyEvent.ACTION_UP) return@onPreviewKeyEvent true
                            onSubtitleFontSizeChange((currentFontSize.value + 1).sp)
                            return@onPreviewKeyEvent true
                        }

                        KeyEvent.KEYCODE_DPAD_DOWN -> {
                            if (it.nativeKeyEvent.action == KeyEvent.ACTION_UP) return@onPreviewKeyEvent true
                            if (currentFontSize <= 2.sp) {
                                onSubtitleFontSizeChange(1.sp)
                            } else {
                                onSubtitleFontSizeChange((currentFontSize.value - 1).sp)
                            }
                            return@onPreviewKeyEvent true
                        }
                    }
                    false
                },
            text = "${currentFontSize.value} SP",
            selected = false
        ) { }

        BottomTip(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp),
            text = stringResource(R.string.video_controller_menu_danmaku_area_tip)
        )
    }
}

@Composable
fun SubtitleBottomPaddingContent(
    modifier: Modifier = Modifier,
    currentBottomPadding: Dp = 12.dp,
    onSubtitleBottomPaddingChange: (Dp) -> Unit
) {
    val logger = KotlinLogging.logger { }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        MenuListItem(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .onPreviewKeyEvent {
                    if (BuildConfig.DEBUG) logger.info { "Native key event: ${it.nativeKeyEvent}" }

                    when (it.nativeKeyEvent.keyCode) {
                        KeyEvent.KEYCODE_DPAD_UP -> {
                            if (it.nativeKeyEvent.action == KeyEvent.ACTION_UP) return@onPreviewKeyEvent true
                            onSubtitleBottomPaddingChange((currentBottomPadding.value + 1).dp)
                            return@onPreviewKeyEvent true
                        }

                        KeyEvent.KEYCODE_DPAD_DOWN -> {
                            if (it.nativeKeyEvent.action == KeyEvent.ACTION_UP) return@onPreviewKeyEvent true
                            if (currentBottomPadding <= 1.dp) {
                                onSubtitleBottomPaddingChange(0.dp)
                            } else {
                                onSubtitleBottomPaddingChange((currentBottomPadding.value - 1).dp)
                            }
                            return@onPreviewKeyEvent true
                        }
                    }
                    false
                },
            text = "${currentBottomPadding.value} DP",
            selected = false
        ) { }

        BottomTip(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp),
            text = stringResource(R.string.video_controller_menu_danmaku_area_tip)
        )
    }
}

@Composable
fun MenuListItem(
    modifier: Modifier = Modifier,
    text: String,
    selected: Boolean,
    textAlign: TextAlign = TextAlign.Center,
    onFocus: () -> Unit = {},
    onClick: () -> Unit
) {
    DenseListItem(
        modifier = modifier
            .onFocusChanged { if (it.hasFocus) onFocus() },
        selected = selected,
        onClick = onClick,
        headlineContent = {
            Text(
                text = text,
                textAlign = textAlign
            )
        }
    )
}
