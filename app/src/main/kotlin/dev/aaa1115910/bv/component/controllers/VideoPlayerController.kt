package dev.aaa1115910.bv.component.controllers

import android.app.Activity
import android.os.CountDownTimer
import android.view.KeyEvent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.aaa1115910.bv.BuildConfig
import dev.aaa1115910.bv.component.controllers.info.VideoPlayerInfoData
import dev.aaa1115910.bv.component.controllers.info.VideoPlayerInfoTip
import dev.aaa1115910.bv.entity.DanmakuSize
import dev.aaa1115910.bv.entity.DanmakuTransparency
import dev.aaa1115910.bv.entity.VideoAspectRatio
import dev.aaa1115910.bv.entity.VideoCodec
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.toast
import mu.KotlinLogging

@Composable
fun VideoPlayerController(
    modifier: Modifier = Modifier,
    infoData: VideoPlayerInfoData,
    resolutionMap: Map<Int, String> = emptyMap(),
    availableVideoCodec: List<VideoCodec> = emptyList(),
    currentResolution: Int? = null,
    currentVideoCodec: VideoCodec = VideoCodec.AVC,
    currentVideoAspectRatio: VideoAspectRatio = VideoAspectRatio.Default,
    currentDanmakuEnabled: Boolean = true,
    currentDanmakuSize: DanmakuSize = DanmakuSize.S2,
    currentDanmakuTransparency: DanmakuTransparency = DanmakuTransparency.T1,
    currentDanmakuArea: Float = 1f,
    buffering: Boolean,
    isPlaying: Boolean,
    bufferSpeed: Any,
    showLogs: Boolean,
    logs: String,
    title: String,
    onChooseResolution: (qualityId: Int) -> Unit,
    onChooseVideoCodec: (videoCodec: VideoCodec) -> Unit,
    onChooseVideoAspectRatio: (VideoAspectRatio) -> Unit,
    onSwitchDanmaku: (enable: Boolean) -> Unit,
    onDanmakuSizeChange: (DanmakuSize) -> Unit,
    onDanmakuTransparencyChange: (DanmakuTransparency) -> Unit,
    onDanmakuAreaChange: (Float) -> Unit,
    onSeekBack: () -> Unit,
    onSeekForward: () -> Unit,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    requestFocus: () -> Unit,
    freeFocus: () -> Unit = {},
    captureFocus: () -> Unit = {},
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }
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

    val infiniteTransition = rememberInfiniteTransition()

    val iconRotate by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(200, easing = LinearEasing), RepeatMode.Reverse
        )
    )

    val tick by remember {
        derivedStateOf { iconRotate > 0.5f }
    }

    LaunchedEffect(Unit) {
        logger.fInfo { "Request focus on controller" }
        //focusRequester.captureFocus()
    }

    LaunchedEffect(tick) {
        //logger.fInfo { "Request focus on controller2" }
        //focusRequester.requestFocus()
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
                            KeyEvent.KEYCODE_BACK, KeyEvent.KEYCODE_BOOKMARK, KeyEvent.KEYCODE_MENU
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
                            val currentTime = System.currentTimeMillis()
                            if (currentTime - lastPressBack < 1000 * 3) {
                                logger.fInfo { "Exiting video player" }
                                (context as Activity).finish()
                            } else {
                                lastPressBack = currentTime
                                "再次按下返回键退出播放".toast(context)
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
                text = "$logs"
            )
        }

        if (!isPlaying && !buffering) {
            PauseIcon(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(32.dp)
            )
        }

        //底部进度条
        AnimatedVisibility(
            modifier = Modifier.align(Alignment.BottomCenter),
            visible = showSeekController,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            BottomControls(infoData = infoData)
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
                resolutionMap = resolutionMap,
                availableVideoCodec = availableVideoCodec,
                currentResolution = currentResolution,
                currentVideoCodec = currentVideoCodec,
                currentVideoAspectRatio = currentVideoAspectRatio,
                currentDanmakuEnabled = currentDanmakuEnabled,
                currentDanmakuSize = currentDanmakuSize,
                currentDanmakuTransparency = currentDanmakuTransparency,
                currentDanmakuArea = currentDanmakuArea,
                onChooseResolution = onChooseResolution,
                onChooseVideoCodec = onChooseVideoCodec,
                onChooseVideoAspectRatio = onChooseVideoAspectRatio,
                onSwitchDanmaku = onSwitchDanmaku,
                onDanmakuSizeChange = onDanmakuSizeChange,
                onDanmakuTransparencyChange = onDanmakuTransparencyChange,
                onDanmakuAreaChange = onDanmakuAreaChange
            )
        }

        //右侧分P


        //正中间缓冲
        AnimatedVisibility(
            modifier = Modifier.align(Alignment.Center),
            visible = buffering,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            BufferingTip(speed = "")
        }
    }
}
