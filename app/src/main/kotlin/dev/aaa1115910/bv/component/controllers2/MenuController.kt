package dev.aaa1115910.bv.component.controllers2

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ClearAll
import androidx.compose.material.icons.outlined.ClosedCaption
import androidx.compose.material.icons.outlined.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import dev.aaa1115910.biliapi.entity.video.Subtitle
import dev.aaa1115910.biliapi.entity.video.SubtitleAiStatus
import dev.aaa1115910.biliapi.entity.video.SubtitleAiType
import dev.aaa1115910.biliapi.entity.video.SubtitleType
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.component.controllers.LocalVideoPlayerControllerData
import dev.aaa1115910.bv.component.controllers.VideoPlayerControllerData
import dev.aaa1115910.bv.component.controllers2.playermenu.ClosedCaptionMenuList
import dev.aaa1115910.bv.component.controllers2.playermenu.DanmakuMenuList
import dev.aaa1115910.bv.component.controllers2.playermenu.MenuNavList
import dev.aaa1115910.bv.component.controllers2.playermenu.PictureMenuList
import dev.aaa1115910.bv.entity.Audio
import dev.aaa1115910.bv.entity.VideoAspectRatio
import dev.aaa1115910.bv.entity.VideoCodec
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.util.requestFocus
import dev.aaa1115910.bv.util.swapList
import kotlinx.coroutines.delay

@Composable
fun MenuController(
    modifier: Modifier = Modifier,
    show: Boolean,
    onResolutionChange: (Int) -> Unit = {},
    onCodecChange: (VideoCodec) -> Unit = {},
    onAspectRatioChange: (VideoAspectRatio) -> Unit,
    onPlaySpeedChange: (Float) -> Unit = {},
    onAudioChange: (Audio) -> Unit,
    onDanmakuSwitchChange: (List<DanmakuType>) -> Unit,
    onDanmakuSizeChange: (Float) -> Unit,
    onDanmakuOpacityChange: (Float) -> Unit,
    onDanmakuAreaChange: (Float) -> Unit,
    onDanmakuMaskChange: (Boolean) -> Unit = {},
    onSubtitleChange: (Subtitle) -> Unit,
    onSubtitleSizeChange: (TextUnit) -> Unit,
    onSubtitleBackgroundOpacityChange: (Float) -> Unit,
    onSubtitleBottomPadding: (Dp) -> Unit
) {
    val scope = rememberCoroutineScope()
    val defaultFocusRequester = remember { FocusRequester() }

    LaunchedEffect(show) {
        if (show) {
            delay(100)
            defaultFocusRequester.requestFocus(scope)
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.CenterEnd
    ) {
        AnimatedVisibility(
            visible = show,
            enter = expandHorizontally(),
            exit = shrinkHorizontally()
        ) {
            MenuController(
                defaultFocusRequester = defaultFocusRequester,
                onResolutionChange = onResolutionChange,
                onCodecChange = onCodecChange,
                onAspectRatioChange = onAspectRatioChange,
                onPlaySpeedChange = onPlaySpeedChange,
                onAudioChange = onAudioChange,
                onDanmakuSwitchChange = onDanmakuSwitchChange,
                onDanmakuSizeChange = onDanmakuSizeChange,
                onDanmakuOpacityChange = onDanmakuOpacityChange,
                onDanmakuAreaChange = onDanmakuAreaChange,
                onDanmakuMaskChange = onDanmakuMaskChange,
                onSubtitleChange = onSubtitleChange,
                onSubtitleSizeChange = onSubtitleSizeChange,
                onSubtitleBackgroundOpacityChange = onSubtitleBackgroundOpacityChange,
                onSubtitleBottomPadding = onSubtitleBottomPadding
            )
        }
    }
}

@Composable
fun MenuController(
    modifier: Modifier = Modifier,
    defaultFocusRequester: FocusRequester,
    onResolutionChange: (Int) -> Unit = {},
    onCodecChange: (VideoCodec) -> Unit = {},
    onAspectRatioChange: (VideoAspectRatio) -> Unit,
    onPlaySpeedChange: (Float) -> Unit,
    onAudioChange: (Audio) -> Unit,
    onDanmakuSwitchChange: (List<DanmakuType>) -> Unit,
    onDanmakuSizeChange: (Float) -> Unit,
    onDanmakuOpacityChange: (Float) -> Unit,
    onDanmakuAreaChange: (Float) -> Unit,
    onDanmakuMaskChange: (Boolean) -> Unit = {},
    onSubtitleChange: (Subtitle) -> Unit,
    onSubtitleSizeChange: (TextUnit) -> Unit,
    onSubtitleBackgroundOpacityChange: (Float) -> Unit,
    onSubtitleBottomPadding: (Dp) -> Unit
) {
    var selectedNavItem by remember { mutableStateOf(VideoPlayerMenuNavItem.Picture) }
    var focusState by remember { mutableStateOf(MenuFocusState.MenuNav) }

    Surface(
        modifier = modifier
            .fillMaxHeight(),
        colors = SurfaceDefaults.colors(
            containerColor = Color.Black.copy(alpha = 0.5f)
        )
    ) {
        CompositionLocalProvider(
            LocalMenuFocusStateData provides MenuFocusStateData(
                focusState = focusState
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                MenuList(
                    selectedNavMenu = selectedNavItem,
                    onResolutionChange = onResolutionChange,
                    onCodecChange = onCodecChange,
                    onPlaySpeedChange = onPlaySpeedChange,
                    onAspectRatioChange = onAspectRatioChange,
                    onAudioChange = onAudioChange,
                    onDanmakuSwitchChange = onDanmakuSwitchChange,
                    onDanmakuSizeChange = onDanmakuSizeChange,
                    onDanmakuOpacityChange = onDanmakuOpacityChange,
                    onDanmakuAreaChange = onDanmakuAreaChange,
                    onDanmakuMaskChange = onDanmakuMaskChange,
                    onFocusStateChange = { focusState = it },
                    onSubtitleChange = onSubtitleChange,
                    onSubtitleSizeChange = onSubtitleSizeChange,
                    onSubtitleBackgroundOpacityChange = onSubtitleBackgroundOpacityChange,
                    onSubtitleBottomPadding = onSubtitleBottomPadding
                )
                MenuNavList(
                    modifier = Modifier
                        .focusRequester(defaultFocusRequester)
                        .onPreviewKeyEvent {
                            if (it.type == KeyEventType.KeyUp) {
                                if (listOf(Key.Enter, Key.DirectionCenter).contains(it.key)) {
                                    return@onPreviewKeyEvent false
                                }
                                return@onPreviewKeyEvent true
                            }
                            if (it.key == Key.DirectionLeft) focusState = MenuFocusState.Menu
                            false
                        },
                    selectedMenu = selectedNavItem,
                    onSelectedChanged = { selectedNavItem = it },
                    isFocusing = focusState == MenuFocusState.MenuNav
                )
            }
        }
    }
}

@Composable
private fun MenuList(
    modifier: Modifier = Modifier,
    selectedNavMenu: VideoPlayerMenuNavItem,
    onResolutionChange: (Int) -> Unit,
    onCodecChange: (VideoCodec) -> Unit,
    onAspectRatioChange: (VideoAspectRatio) -> Unit,
    onPlaySpeedChange: (Float) -> Unit,
    onAudioChange: (Audio) -> Unit,
    onDanmakuSwitchChange: (List<DanmakuType>) -> Unit,
    onDanmakuSizeChange: (Float) -> Unit,
    onDanmakuOpacityChange: (Float) -> Unit,
    onDanmakuAreaChange: (Float) -> Unit,
    onDanmakuMaskChange: (Boolean) -> Unit = {},
    onSubtitleChange: (Subtitle) -> Unit,
    onSubtitleSizeChange: (TextUnit) -> Unit,
    onSubtitleBackgroundOpacityChange: (Float) -> Unit,
    onSubtitleBottomPadding: (Dp) -> Unit,
    onFocusStateChange: (MenuFocusState) -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when (selectedNavMenu) {
            VideoPlayerMenuNavItem.Picture -> {
                PictureMenuList(
                    onResolutionChange = onResolutionChange,
                    onCodecChange = onCodecChange,
                    onAspectRatioChange = onAspectRatioChange,
                    onPlaySpeedChange = onPlaySpeedChange,
                    onAudioChange = onAudioChange,
                    onFocusStateChange = onFocusStateChange
                )
            }

            VideoPlayerMenuNavItem.Danmaku -> {
                DanmakuMenuList(
                    onDanmakuSwitchChange = onDanmakuSwitchChange,
                    onDanmakuSizeChange = onDanmakuSizeChange,
                    onDanmakuOpacityChange = onDanmakuOpacityChange,
                    onDanmakuAreaChange = onDanmakuAreaChange,
                    onFocusStateChange = onFocusStateChange,
                    onDanmakuMaskChange = onDanmakuMaskChange
                )
            }

            VideoPlayerMenuNavItem.ClosedCaption -> {
                ClosedCaptionMenuList(
                    onSubtitleChange = onSubtitleChange,
                    onSubtitleSizeChange = onSubtitleSizeChange,
                    onSubtitleBackgroundOpacityChange = onSubtitleBackgroundOpacityChange,
                    onSubtitleBottomPadding = onSubtitleBottomPadding,
                    onFocusStateChange = onFocusStateChange
                )
            }
        }
    }
}


enum class VideoPlayerMenuNavItem(private val strRes: Int, val icon: ImageVector) {
    Picture(R.string.video_player_menu_nav_picture, Icons.Outlined.Image),
    Danmaku(R.string.video_player_menu_nav_danmaku, Icons.Outlined.ClearAll),
    ClosedCaption(R.string.video_player_menu_nav_subtitle, Icons.Outlined.ClosedCaption);

    fun getDisplayName(context: Context) = context.getString(strRes)
}

enum class VideoPlayerPictureMenuItem(private val strRes: Int) {
    Resolution(R.string.video_player_menu_picture_resolution),
    Codec(R.string.video_player_menu_picture_codec),
    AspectRatio(R.string.video_player_menu_picture_aspect_ratio),
    PlaySpeed(R.string.video_player_menu_picture_play_speed),
    Audio(R.string.video_player_menu_picture_audio);

    fun getDisplayName(context: Context) = context.getString(strRes)
}

enum class VideoPlayerDanmakuMenuItem(private val strRes: Int) {
    Switch(R.string.video_player_menu_danmaku_switch),
    Size(R.string.video_player_menu_danmaku_size),
    Opacity(R.string.video_player_menu_danmaku_opacity),
    Area(R.string.video_player_menu_danmaku_area),
    Mask(R.string.video_player_menu_danmaku_mask);

    fun getDisplayName(context: Context) = context.getString(strRes)
}

enum class VideoPlayerClosedCaptionMenuItem(private val strRes: Int) {
    Switch(R.string.video_player_menu_subtitle_switch),
    Size(R.string.video_player_menu_subtitle_size),
    Opacity(R.string.video_player_menu_subtitle_background_opacity),
    Padding(R.string.video_player_menu_subtitle_bottom_padding);

    fun getDisplayName(context: Context) = context.getString(strRes)
}

enum class DanmakuType(private val strRes: Int) {
    All(R.string.video_player_menu_danmaku_type_all),
    Top(R.string.video_player_menu_danmaku_type_top),
    Rolling(R.string.video_player_menu_danmaku_type_cross),
    Bottom(R.string.video_player_menu_danmaku_type_bottom);

    fun getDisplayName(context: Context) = context.getString(strRes)
}

@Preview(device = "id:tv_1080p")
@Composable
fun MenuControllerPreview() {

    val defaultFocusRequester = remember { FocusRequester() }

    var currentResolution by remember { mutableIntStateOf(1) }
    var currentCodec by remember { mutableStateOf(VideoCodec.HEVC) }
    var currentVideoAspectRatio by remember { mutableStateOf(VideoAspectRatio.Default) }
    var currentPlaySpeed by remember { mutableFloatStateOf(1f) }
    var currentAudio by remember { mutableStateOf(Audio.A192K) }

    val currentDanmakuSwitch = remember { mutableStateListOf<DanmakuType>() }
    var currentDanmakuSize by remember { mutableFloatStateOf(1f) }
    var currentDanmakuOpacity by remember { mutableFloatStateOf(1f) }
    var currentDanmakuArea by remember { mutableFloatStateOf(1f) }
    var currentDanmakuMask by remember { mutableStateOf(false) }

    var currentSubtitleId by remember { mutableLongStateOf(-1L) }
    val currentSubtitleList = remember { mutableStateListOf<Subtitle>() }
    var currentSubtitleFontSize by remember { mutableStateOf(24.sp) }
    var currentSubtitleBackgroundOpacity by remember { mutableFloatStateOf(0.4f) }
    var currentSubtitleBottomPadding by remember { mutableStateOf(8.dp) }

    LaunchedEffect(Unit) {
        currentSubtitleList.apply {
            addAll(
                listOf(
                    Subtitle(
                        id = -1,
                        langDoc = "关闭",
                        lang = "",
                        url = "",
                        type = SubtitleType.CC,
                        aiType = SubtitleAiType.Normal,
                        aiStatus = SubtitleAiStatus.None
                    ),
                    Subtitle(
                        id = 1111,
                        langDoc = "ai-zh",
                        lang = "中文（自动翻译）",
                        url = "",
                        type = SubtitleType.CC,
                        aiType = SubtitleAiType.Normal,
                        aiStatus = SubtitleAiStatus.None
                    ),
                    Subtitle(
                        id = 222,
                        lang = "zh",
                        langDoc = "中文",
                        url = "",
                        type = SubtitleType.CC,
                        aiType = SubtitleAiType.Normal,
                        aiStatus = SubtitleAiStatus.None
                    ),
                    Subtitle(
                        id = 1333,
                        lang = "ai-en",
                        langDoc = "English",
                        url = "",
                        type = SubtitleType.CC,
                        aiType = SubtitleAiType.Normal,
                        aiStatus = SubtitleAiStatus.None
                    )
                )
            )
        }
    }

    BVTheme {
        Surface(
            colors = SurfaceDefaults.colors(
                containerColor = Color.White
            )
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                CompositionLocalProvider(
                    LocalVideoPlayerControllerData provides VideoPlayerControllerData(
                        resolutionMap = mapOf(
                            1 to "1440",
                            2 to "1080",
                            3 to "720",
                            4 to "480",
                            5 to "360"
                        ),
                        availableVideoCodec = VideoCodec.entries,
                        availableAudio = Audio.entries,

                        currentResolution = currentResolution,
                        currentVideoCodec = currentCodec,
                        currentVideoAspectRatio = currentVideoAspectRatio,
                        currentVideoSpeed = currentPlaySpeed,
                        currentAudio = currentAudio,

                        currentDanmakuEnabledList = currentDanmakuSwitch,
                        currentDanmakuScale = currentDanmakuSize,
                        currentDanmakuOpacity = currentDanmakuOpacity,
                        currentDanmakuArea = currentDanmakuArea,
                        currentDanmakuMask = currentDanmakuMask,

                        currentSubtitleId = currentSubtitleId,
                        availableSubtitleTracks = currentSubtitleList,
                        currentSubtitleFontSize = currentSubtitleFontSize,
                        currentSubtitleBackgroundOpacity = currentSubtitleBackgroundOpacity,
                        currentSubtitleBottomPadding = currentSubtitleBottomPadding
                    )
                ) {
                    MenuController(
                        modifier = Modifier
                            .align(Alignment.CenterEnd),
                        defaultFocusRequester = defaultFocusRequester,
                        onResolutionChange = { currentResolution = it },
                        onCodecChange = { currentCodec = it },
                        onAspectRatioChange = { currentVideoAspectRatio = it },
                        onPlaySpeedChange = { currentPlaySpeed = it },
                        onAudioChange = { currentAudio = it },
                        onDanmakuSwitchChange = {
                            val a = currentDanmakuSwitch.toList()
                            currentDanmakuSwitch.swapList(it)
                            val b = currentDanmakuSwitch.toList()
                            println("a=$a")
                            println("b=$b")

                        },
                        onDanmakuSizeChange = { currentDanmakuSize = it },
                        onDanmakuOpacityChange = { currentDanmakuOpacity = it },
                        onDanmakuAreaChange = { currentDanmakuArea = it },
                        onDanmakuMaskChange = { currentDanmakuMask = it },
                        onSubtitleChange = { currentSubtitleId = it.id },
                        onSubtitleSizeChange = { currentSubtitleFontSize = it },
                        onSubtitleBackgroundOpacityChange = {
                            currentSubtitleBackgroundOpacity = it
                        },
                        onSubtitleBottomPadding = { currentSubtitleBottomPadding = it }
                    )
                }
            }
        }
    }
}

enum class MenuFocusState {
    MenuNav, Menu, Items
}

data class MenuFocusStateData(
    val focusState: MenuFocusState = MenuFocusState.MenuNav
)

val LocalMenuFocusStateData = compositionLocalOf { MenuFocusStateData() }
