package dev.aaa1115910.bv.component.controllers2

import android.content.Context
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.aaa1115910.bv.component.controllers.LocalVideoPlayerControllerData
import dev.aaa1115910.bv.component.controllers.VideoPlayerControllerData
import dev.aaa1115910.bv.component.controllers2.playermenu.MenuNavList
import dev.aaa1115910.bv.component.controllers2.playermenu.PictureMenuList
import dev.aaa1115910.bv.entity.VideoAspectRatio
import dev.aaa1115910.bv.entity.VideoCodec
import dev.aaa1115910.bv.ui.theme.BVTheme

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MenuController(
    modifier: Modifier = Modifier,
    onResolutionChange: (Int) -> Unit = {},
    onCodecChange: (VideoCodec) -> Unit = {},
    onAspectRatioChange: (VideoAspectRatio) -> Unit,
) {
    var selectedNavItem by remember { mutableStateOf(VideoPlayerMenuNavItem.Picture) }
    var focusState by remember { mutableStateOf(MenuFocusState.MenuNav) }

    Surface(
        modifier = modifier
            .fillMaxHeight(),
        color = Color.Black.copy(alpha = 0.5f)
    ) {
        CompositionLocalProvider(
            LocalMenuFocusStateData provides MenuFocusStateData(
                focusState = focusState
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                MenuList(
                    selectedNavMenu = selectedNavItem,
                    onResolutionChange = onResolutionChange,
                    onCodecChange = onCodecChange,
                    onFocusStateChange = { focusState = it },
                    onAspectRatioChange = onAspectRatioChange
                )
                MenuNavList(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .onPreviewKeyEvent {
                            if (it.type == KeyEventType.KeyUp) return@onPreviewKeyEvent true
                            println(it)
                            val result = it.key == Key.DirectionLeft
                            if (result) focusState = MenuFocusState.Menu
                            result
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
    onFocusStateChange: (MenuFocusState) -> Unit
) {
    Box(
        modifier = modifier
            .animateContentSize(),
        contentAlignment = Alignment.Center
    ) {
        when (selectedNavMenu) {
            VideoPlayerMenuNavItem.Picture -> {
                PictureMenuList(
                    onResolutionChange = onResolutionChange,
                    onFocusStateChange = onFocusStateChange,
                    onCodecChange = onCodecChange,
                    onAspectRatioChange = onAspectRatioChange,
                )
            }

            VideoPlayerMenuNavItem.Danmaku -> {}
            VideoPlayerMenuNavItem.ClosedCaption -> {}
        }
    }
}


enum class VideoPlayerMenuNavItem(private val strRes: String) {
    Picture("画面设置"),
    Danmaku("弹幕设置"),
    ClosedCaption("字幕设置");

    fun getDisplayName(context: Context) = strRes
}

enum class VideoPlayerPictureMenuItem(private val strRes: String) {
    Resolution("清晰度"),
    Codec("视频编码"),
    AspectRatio("画面比例");

    fun getDisplayName(context: Context) = strRes
}

enum class VideoPlayerDanmakuMenuItem(private val strRes: String) {
    Switch("开关"),
    Size("大小"),
    Opacity("透明度"),
    Area("区域");

    fun getDisplayName(context: Context) = strRes
}

enum class DanmakuType(private val strRes: String) {
    All("全部弹幕"),
    Top("顶部弹幕"),
    Cross("滚动弹幕"),
    Bottom("底部弹幕");

    fun getDisplayName(context: Context) = strRes
}

@Preview(device = "id:tv_1080p")
@Composable
fun MenuControllerPreview() {

    var currentResolution by remember { mutableStateOf(1) }
    var currentCodec by remember { mutableStateOf(VideoCodec.HEVC) }
    var currentVideoAspectRatio by remember { mutableStateOf(VideoAspectRatio.Default) }

    BVTheme {
        Surface(
            color = Color.White
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
                        availableVideoCodec = VideoCodec.values().toList(),

                        currentResolution = currentResolution,
                        currentVideoCodec = currentCodec,
                        currentVideoAspectRatio = currentVideoAspectRatio,
                    )
                ) {
                    MenuController(
                        modifier = Modifier
                            .align(Alignment.CenterEnd),
                        onResolutionChange = { currentResolution = it },
                        onCodecChange = { currentCodec = it },
                        onAspectRatioChange = { currentVideoAspectRatio = it }
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
