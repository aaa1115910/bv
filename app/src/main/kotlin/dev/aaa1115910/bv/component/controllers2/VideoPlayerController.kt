package dev.aaa1115910.bv.component.controllers2

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.tv.material3.Text
import dev.aaa1115910.biliapi.entity.video.VideoMoreInfo
import dev.aaa1115910.bv.BuildConfig
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.component.controllers.LocalVideoPlayerControllerData
import dev.aaa1115910.bv.entity.VideoAspectRatio
import dev.aaa1115910.bv.entity.VideoCodec
import dev.aaa1115910.bv.player.AbstractVideoPlayer
import dev.aaa1115910.bv.repository.VideoListItem
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.toast
import mu.KotlinLogging

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun VideoPlayerController(
    modifier: Modifier = Modifier,
    videoPlayer: AbstractVideoPlayer,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onExit: () -> Unit,
    onGoTime: (time: Int) -> Unit,
    onBackToHistory: () -> Unit,
    onPlayNewVideo: (VideoListItem) -> Unit,

    //menu events
    onResolutionChange: (Int) -> Unit = {},
    onCodecChange: (VideoCodec) -> Unit = {},
    onAspectRatioChange: (VideoAspectRatio) -> Unit,
    onDanmakuSwitchChange: (List<DanmakuType>) -> Unit,
    onDanmakuSizeChange: (Float) -> Unit,
    onDanmakuOpacityChange: (Float) -> Unit,
    onDanmakuAreaChange: (Float) -> Unit,
    onSubtitleChange: (VideoMoreInfo.SubtitleItem) -> Unit,
    onSubtitleSizeChange: (TextUnit) -> Unit,
    onSubtitleBackgroundOpacityChange: (Float) -> Unit,
    onSubtitleBottomPadding: (Dp) -> Unit,

    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val data = LocalVideoPlayerControllerData.current
    val logger = KotlinLogging.logger {}

    var showListController by remember { mutableStateOf(false) }
    var showMenuController by remember { mutableStateOf(false) }
    var showSeekController by remember { mutableStateOf(false) }
    var showInfo by remember { mutableStateOf(false) }
    var showBackToHistory by remember { mutableStateOf(false) }
    val showClickableControllers by remember { derivedStateOf { showListController || showMenuController } }

    var lastPressBack by remember { mutableStateOf(0L) }
    var hasFocus by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .onFocusChanged { hasFocus = it.hasFocus }
            .focusable()
            .onPreviewKeyEvent {

                if (showClickableControllers) {
                    if (listOf(Key.Back, Key.Menu).contains(it.key)) {
                        if (it.type == KeyEventType.KeyUp) {
                            logger.fInfo { "[${it.key}] hide all controllers" }
                            showMenuController = false
                            showListController = false
                            showSeekController = false
                        }
                        return@onPreviewKeyEvent true
                    }
                    return@onPreviewKeyEvent false
                }

                when (it.key) {
                    Key.DirectionCenter, Key.Enter, Key.Spacebar -> {
                        if (data.lastPlayed != 0) {
                            onBackToHistory()
                            return@onPreviewKeyEvent true
                        }

                        if (it.nativeKeyEvent.isLongPress) {
                            logger.fInfo { "[${it.key}] long press" }
                            showMenuController = true
                            return@onPreviewKeyEvent true
                        }

                        logger.fInfo { "[${it.key}] short press" }
                        if (it.type == KeyEventType.KeyDown) return@onPreviewKeyEvent true
                        if (videoPlayer.isPlaying) onPause() else onPlay()
                        return@onPreviewKeyEvent false
                    }

                    // KEYCODE_CENTER_LONG
                    // 一切设备上长按 DirectionCenter 键会是这个按键事件
                    Key(763) -> {
                        showMenuController = true
                        return@onPreviewKeyEvent true
                    }

                    Key.DirectionUp -> {
                        if (it.type == KeyEventType.KeyDown) return@onPreviewKeyEvent true
                        logger.info { "[${it.key} press]" }
                        showListController = true
                        return@onPreviewKeyEvent true
                    }

                    Key.DirectionDown -> {
                        if (it.type == KeyEventType.KeyDown) return@onPreviewKeyEvent true
                        logger.info { "[${it.key} press]" }
                        showInfo = !showInfo
                        return@onPreviewKeyEvent true
                    }

                    Key.Menu -> {
                        if (it.type == KeyEventType.KeyDown) return@onPreviewKeyEvent true
                        logger.info { "[${it.key} press]" }
                        showMenuController = !showMenuController
                        return@onPreviewKeyEvent true
                    }

                    Key.Back -> {
                        if (it.type == KeyEventType.KeyDown) return@onPreviewKeyEvent true
                        logger.info { "[${it.key} press]" }
                        //TODO 播放停止时按一次返回键退出播放器
                        val currentTime = System.currentTimeMillis()
                        if (currentTime - lastPressBack < 1000 * 3) {
                            logger.fInfo { "Exiting video player" }
                            onExit()
                        } else {
                            lastPressBack = currentTime
                            R.string.video_player_press_back_again_to_exit.toast(context)
                        }
                        return@onPreviewKeyEvent true
                    }

                    Key.MediaPlayPause -> {
                        if (it.type == KeyEventType.KeyDown) return@onPreviewKeyEvent true
                        logger.info { "[${it.key} press]" }
                        if (videoPlayer.isPlaying) onPause() else onPlay()
                        return@onPreviewKeyEvent true
                    }

                    Key.MediaPlay -> {
                        if (it.type == KeyEventType.KeyDown) return@onPreviewKeyEvent true
                        logger.info { "[${it.key} press]" }
                        if (!videoPlayer.isPlaying) onPlay()
                        return@onPreviewKeyEvent true
                    }

                    Key.MediaPause -> {
                        if (it.type == KeyEventType.KeyDown) return@onPreviewKeyEvent true
                        logger.info { "[${it.key} press]" }
                        if (videoPlayer.isPlaying) onPause()
                        return@onPreviewKeyEvent true
                    }

                    Key.MediaFastForward -> {
                        if (it.type == KeyEventType.KeyDown) return@onPreviewKeyEvent true
                        logger.info { "[${it.key} press]" }
                        showSeekController = true
                    }

                    Key.MediaRewind -> {
                        if (it.type == KeyEventType.KeyDown) return@onPreviewKeyEvent true
                        logger.info { "[${it.key} press]" }
                        showSeekController = true
                    }
                }

                false
            }
    ) {
        content()
        if (BuildConfig.DEBUG) {
            Text(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .background(Color.Black.copy(alpha = 0.3f)),
                text = data.debugInfo
            )
        }
        ControllerVideoInfo(
            show = showInfo,
            infoData = data.infoData,
            title = data.title,
            secondTitle = data.secondTitle,
            onHideInfo = { showInfo = false }
        )
        SeekController(
            show = showSeekController,
            infoData = data.infoData,
            onGoTime = onGoTime
        )
        VideoListController(
            show = showListController,
            currentCid = data.currentVideoCid,
            videoList = data.availableVideoList,
            onPlayNewVideo = onPlayNewVideo
        )
        MenuController(
            show = showMenuController,
            onResolutionChange = onResolutionChange,
            onCodecChange = onCodecChange,
            onAspectRatioChange = onAspectRatioChange,
            onDanmakuSwitchChange = onDanmakuSwitchChange,
            onDanmakuSizeChange = onDanmakuSizeChange,
            onDanmakuOpacityChange = onDanmakuOpacityChange,
            onDanmakuAreaChange = onDanmakuAreaChange,
            onSubtitleChange = onSubtitleChange,
            onSubtitleSizeChange = onSubtitleSizeChange,
            onSubtitleBackgroundOpacityChange = onSubtitleBackgroundOpacityChange,
            onSubtitleBottomPadding = onSubtitleBottomPadding
        )
    }
}



