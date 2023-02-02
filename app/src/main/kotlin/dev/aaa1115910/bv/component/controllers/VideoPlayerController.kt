package dev.aaa1115910.bv.component.controllers

import android.app.Activity
import android.os.CountDownTimer
import android.view.KeyEvent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.Player
import dev.aaa1115910.biliapi.entity.video.VideoMoreInfo
import dev.aaa1115910.bilisubtitle.entity.SubtitleItem
import dev.aaa1115910.bv.BuildConfig
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.component.controllers.info.VideoPlayerInfoData
import dev.aaa1115910.bv.component.controllers.info.VideoPlayerInfoTip
import dev.aaa1115910.bv.entity.DanmakuSize
import dev.aaa1115910.bv.entity.DanmakuTransparency
import dev.aaa1115910.bv.entity.VideoAspectRatio
import dev.aaa1115910.bv.entity.VideoCodec
import dev.aaa1115910.bv.repository.VideoListItem
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.toast
import mu.KotlinLogging

@Composable
fun VideoPlayerController(
    modifier: Modifier = Modifier,
    playbackState: Int? = -1,
    infoData: VideoPlayerInfoData,
    resolutionMap: Map<Int, String> = emptyMap(),
    availableVideoCodec: List<VideoCodec> = emptyList(),
    availableSubtitle: List<VideoMoreInfo.SubtitleItem> = emptyList(),
    availableVideoList: List<VideoListItem> = emptyList(),
    currentVideoCid: Int = 0,
    currentResolution: Int? = null,
    currentVideoCodec: VideoCodec = VideoCodec.AVC,
    currentVideoAspectRatio: VideoAspectRatio = VideoAspectRatio.Default,
    currentDanmakuEnabled: Boolean = true,
    currentDanmakuSize: DanmakuSize = DanmakuSize.S2,
    currentDanmakuTransparency: DanmakuTransparency = DanmakuTransparency.T1,
    currentDanmakuArea: Float = 1f,
    currentSubtitleId: Long = 0,
    currentSubtitleData: List<SubtitleItem>,
    currentPosition: Long,
    currentSubtitleFontSize: TextUnit = 24.sp,
    currentSubtitleBottomPadding: Dp = 12.dp,
    buffering: Boolean,
    isPlaying: Boolean,
    @Suppress("UNUSED_PARAMETER") bufferSpeed: String,
    showLogs: Boolean,
    logs: String,
    title: String,
    partTitle: String,
    lastPlayed: Int,
    onChooseResolution: (qualityId: Int) -> Unit,
    onChooseVideoCodec: (videoCodec: VideoCodec) -> Unit,
    onChooseVideoAspectRatio: (VideoAspectRatio) -> Unit,
    onSwitchDanmaku: (enable: Boolean) -> Unit,
    onDanmakuSizeChange: (DanmakuSize) -> Unit,
    onDanmakuTransparencyChange: (DanmakuTransparency) -> Unit,
    onDanmakuAreaChange: (Float) -> Unit,
    onSubtitleChange: (Long) -> Unit,
    onSubtitleFontSizeChange: (TextUnit) -> Unit,
    onSubtitleBottomPaddingChange: (Dp) -> Unit,
    onSeekBack: () -> Unit,
    onSeekForward: () -> Unit,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onExit: () -> Unit,
    requestFocus: () -> Unit,
    goBackHistory: () -> Unit = {},
    onVideoSwitch: (VideoListItem) -> Unit = {},
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val logger = KotlinLogging.logger { }

    var showSeekController by remember { mutableStateOf(false) }
    var showMenuController by remember { mutableStateOf(false) }
    var showPartController by remember { mutableStateOf(false) }

    val showingRightController: () -> Boolean = { showMenuController || showPartController }

    var seekHideTimer: CountDownTimer? by remember { mutableStateOf(null) }
    var lastChangedSeek by remember { mutableStateOf(0L) }
    var lastPressBack by remember { mutableStateOf(0L) }

    var hasFocus by remember { mutableStateOf(false) }

    val setCloseSeekTimer: () -> Unit = {
        if (showSeekController) {
            seekHideTimer?.cancel()
            seekHideTimer = object : CountDownTimer(5000, 1000) {
                override fun onTick(millisUntilFinished: Long) {

                }

                override fun onFinish() {
                    showSeekController = false
                }
            }
            seekHideTimer?.start()
        } else {
            seekHideTimer?.cancel()
            seekHideTimer = null
        }
    }

    LaunchedEffect(Unit) {
        logger.fInfo { "Request focus on controller" }
        //focusRequester.captureFocus()
    }

    LaunchedEffect(showMenuController) {
        if (!showMenuController) requestFocus()
    }

    LaunchedEffect(showSeekController) {
        setCloseSeekTimer()
    }

    LaunchedEffect(lastChangedSeek) {
        setCloseSeekTimer()
    }

    CompositionLocalProvider(
        LocalVideoPlayerControllerData provides VideoPlayerControllerData(
            infoData = infoData,
            resolutionMap = resolutionMap,
            availableVideoCodec = availableVideoCodec,
            availableSubtitle = availableSubtitle,
            availableVideoList = availableVideoList,
            currentVideoCid = currentVideoCid,
            currentResolution = currentResolution,
            currentVideoCodec = currentVideoCodec,
            currentVideoAspectRatio = currentVideoAspectRatio,
            currentDanmakuEnabled = currentDanmakuEnabled,
            currentDanmakuSize = currentDanmakuSize,
            currentDanmakuTransparency = currentDanmakuTransparency,
            currentDanmakuArea = currentDanmakuArea,
            currentSubtitleId = currentSubtitleId,
            currentSubtitleData = currentSubtitleData,
            currentSubtitleFontSize = currentSubtitleFontSize,
            currentSubtitleBottomPadding = currentSubtitleBottomPadding,
            currentPosition = currentPosition
        )
    ) {
        Box(
            modifier = modifier
                .onFocusChanged { hasFocus = it.hasFocus }
                //.focusRequester(focusRequester)
                .focusable()
                .fillMaxSize()
                .onPreviewKeyEvent {
                    if (BuildConfig.DEBUG) logger.fInfo { "Native key event: ${it.nativeKeyEvent}" }

                    if (showingRightController()) {
                        if (listOf(
                                KeyEvent.KEYCODE_BACK,
                                KeyEvent.KEYCODE_BOOKMARK,
                                KeyEvent.KEYCODE_MENU
                            ).contains(it.nativeKeyEvent.keyCode)
                        ) {
                            if (it.nativeKeyEvent.action == KeyEvent.ACTION_UP) {
                                showMenuController = false
                                showPartController = false
                                return@onPreviewKeyEvent true
                            }
                        }
                        return@onPreviewKeyEvent false
                    }

                    when (it.nativeKeyEvent.keyCode) {
                        KeyEvent.KEYCODE_DPAD_UP -> {
                            if (it.nativeKeyEvent.action == KeyEvent.ACTION_DOWN) return@onPreviewKeyEvent true
                            logger.fInfo { "Pressed dpad up" }
                            if (showingRightController()) return@onPreviewKeyEvent false
                            showPartController = true
                            return@onPreviewKeyEvent true
                        }

                        KeyEvent.KEYCODE_DPAD_DOWN -> {
                            if (it.nativeKeyEvent.action == KeyEvent.ACTION_DOWN) return@onPreviewKeyEvent true
                            logger.fInfo { "Pressed dpad down" }
                            if (showingRightController()) return@onPreviewKeyEvent false
                            showSeekController = !showSeekController
                            return@onPreviewKeyEvent true
                        }

                        KeyEvent.KEYCODE_DPAD_LEFT -> {
                            if (it.nativeKeyEvent.action == KeyEvent.ACTION_UP) return@onPreviewKeyEvent true
                            logger.fInfo { "Pressed dpad left" }
                            if (showingRightController()) return@onPreviewKeyEvent false
                            showSeekController = true
                            lastChangedSeek = System.currentTimeMillis()
                            onSeekBack()
                            return@onPreviewKeyEvent true
                        }

                        KeyEvent.KEYCODE_DPAD_RIGHT -> {
                            if (it.nativeKeyEvent.action == KeyEvent.ACTION_UP) return@onPreviewKeyEvent true
                            logger.fInfo { "Pressed dpad right" }
                            if (showingRightController()) return@onPreviewKeyEvent false
                            showSeekController = true
                            lastChangedSeek = System.currentTimeMillis()
                            onSeekForward()
                            return@onPreviewKeyEvent true
                        }

                        KeyEvent.KEYCODE_DPAD_CENTER -> {
                            logger.fInfo { "Pressed dpad center" }
                            if (lastPlayed != 0) {
                                goBackHistory()
                                return@onPreviewKeyEvent true
                            }
                            if (showingRightController()) return@onPreviewKeyEvent false
                            if (it.nativeKeyEvent.isLongPress) {
                                logger.fInfo { "long pressing" }
                                showMenuController = true
                                return@onPreviewKeyEvent true
                            }
                            logger.fInfo { "short pressing" }
                            if (it.nativeKeyEvent.action == KeyEvent.ACTION_DOWN) return@onPreviewKeyEvent true
                            if (isPlaying) onPause() else onPlay()
                            return@onPreviewKeyEvent true
                        }

                        //KEYCODE_CENTER_LONG
                        763 -> {
                            if (showingRightController()) return@onPreviewKeyEvent false
                            showMenuController = true
                            return@onPreviewKeyEvent true
                        }

                        KeyEvent.KEYCODE_MENU, KeyEvent.KEYCODE_BOOKMARK -> {
                            if (it.nativeKeyEvent.action == KeyEvent.ACTION_DOWN) return@onPreviewKeyEvent true
                            logger.fInfo { "Pressed dpad menu" }
                            if (showingRightController()) {
                                showMenuController = false
                                showPartController = false
                                return@onPreviewKeyEvent true
                            }
                            showMenuController = !showMenuController
                            return@onPreviewKeyEvent true
                        }

                        KeyEvent.KEYCODE_BACK -> {
                            if (it.nativeKeyEvent.action == KeyEvent.ACTION_DOWN) return@onPreviewKeyEvent true
                            logger.fInfo { "Pressed dpad back" }
                            if (showingRightController()) {
                                logger.fInfo { "Close menu and part controller" }
                                showMenuController = false
                                showPartController = false
                                return@onPreviewKeyEvent true
                            } else {
                                //播放停止时按返回键直接退出
                                if (playbackState == Player.STATE_ENDED) {
                                    (context as Activity).finish()
                                    return@onPreviewKeyEvent true
                                }
                                val currentTime = System.currentTimeMillis()
                                if (currentTime - lastPressBack < 1000 * 3) {
                                    logger.fInfo { "Exiting video player" }
                                    onExit()
                                    (context as Activity).finish()
                                } else {
                                    lastPressBack = currentTime
                                    R.string.video_player_press_back_again_to_exit.toast(context)
                                }
                            }
                            return@onPreviewKeyEvent true
                        }
                    }
                    false
                }
        ) {
            content()

            if (BuildConfig.DEBUG) {
                Text(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .background(Color.Black),
                    text = "焦点: $hasFocus"
                )
            }

            if (BuildConfig.DEBUG) {
                VideoPlayerInfoTip(
                    modifier = Modifier.align(Alignment.TopStart),
                    data = infoData
                )
            }

            if (showLogs) {
                Text(
                    modifier = Modifier.align(Alignment.BottomStart),
                    text = logs
                )
            }

            if (!isPlaying && !buffering) {
                PauseIcon(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(32.dp)
                )
            }

            //底部字幕
            BottomSubtitle(
                modifier = Modifier
            )

            //底部进度条
            AnimatedVisibility(
                modifier = Modifier.align(Alignment.BottomCenter),
                visible = showSeekController,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                BottomControls(
                    partTitle = partTitle,
                    infoData = infoData
                )
            }

            //顶部标题
            AnimatedVisibility(
                modifier = Modifier.align(Alignment.TopCenter),
                visible = showSeekController,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                TopController(title = title)
            }

            //右侧菜单
            AnimatedVisibility(
                modifier = Modifier.align(Alignment.CenterEnd),
                visible = showMenuController,
                enter = expandHorizontally(),
                exit = shrinkHorizontally()
            ) {
                VideoPlayerMenuController(
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
            }

            //左侧分P
            AnimatedVisibility(
                modifier = Modifier.align(Alignment.CenterStart),
                visible = showPartController,
                enter = expandHorizontally(),
                exit = shrinkHorizontally()
            ) {
                VideoListController(
                    onVideoSwitch = onVideoSwitch
                )
            }

            //正中间缓冲
            AnimatedVisibility(
                modifier = Modifier.align(Alignment.Center),
                visible = buffering,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                BufferingTip(speed = "")
            }

            //跳转历史记录提醒
            AnimatedVisibility(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = 32.dp),
                visible = lastPlayed > 0,
                enter = expandHorizontally(),
                exit = shrinkHorizontally()
            ) {
                GoBackHistoryTip(played = lastPlayed)
            }
        }
    }
}

data class VideoPlayerControllerData(
    val infoData: VideoPlayerInfoData = VideoPlayerInfoData(0, 0, 0, 0, 0, ""),
    val resolutionMap: Map<Int, String> = emptyMap(),
    val availableVideoCodec: List<VideoCodec> = emptyList(),
    val availableSubtitle: List<VideoMoreInfo.SubtitleItem> = emptyList(),
    val availableVideoList: List<VideoListItem> = emptyList(),
    val currentVideoCid: Int = 0,
    val currentResolution: Int? = null,
    val currentVideoCodec: VideoCodec = VideoCodec.AVC,
    val currentVideoAspectRatio: VideoAspectRatio = VideoAspectRatio.Default,
    val currentDanmakuEnabled: Boolean = true,
    val currentDanmakuSize: DanmakuSize = DanmakuSize.S2,
    val currentDanmakuTransparency: DanmakuTransparency = DanmakuTransparency.T1,
    val currentDanmakuArea: Float = 1f,
    val currentSubtitleId: Long = 0,
    val currentSubtitleData: List<SubtitleItem> = emptyList(),
    val currentPosition: Long = 0,
    val currentSubtitleFontSize: TextUnit = 24.sp,
    val currentSubtitleBottomPadding: Dp = 12.dp
)

val LocalVideoPlayerControllerData = compositionLocalOf { VideoPlayerControllerData() }
