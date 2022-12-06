package dev.aaa1115910.bv

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.KeyEvent
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.items
import com.kuaishou.akdanmaku.DanmakuConfig
import com.kuaishou.akdanmaku.render.SimpleRenderer
import com.kuaishou.akdanmaku.ui.DanmakuPlayer
import dev.aaa1115910.bv.component.DanmakuPlayerCompose
import dev.aaa1115910.bv.component.controllers.BottomControls
import dev.aaa1115910.bv.component.controllers.BufferingTip
import dev.aaa1115910.bv.component.controllers.PauseIcon
import dev.aaa1115910.bv.component.controllers.TopController
import dev.aaa1115910.bv.component.controllers.info.VideoPlayerInfoData
import dev.aaa1115910.bv.component.controllers.info.VideoPlayerInfoTip
import dev.aaa1115910.bv.entity.DanmakuSize
import dev.aaa1115910.bv.entity.DanmakuTransparency
import dev.aaa1115910.bv.entity.Resolution
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.viewmodel.PlayerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Timer
import java.util.TimerTask

class VideoPlayerActivity : ComponentActivity() {
    companion object {
        private val logger = KotlinLogging.logger { }
        fun actionStart(context: Context, avid: Int, cid: Int, title: String, partTitle: String) {
            context.startActivity(
                Intent(context, VideoPlayerActivity::class.java).apply {
                    putExtra("avid", avid)
                    putExtra("cid", cid)
                    putExtra("title", title)
                    putExtra("partTitle", partTitle)
                }
            )
        }
    }

    private val playerViewModel: PlayerViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        val player = ExoPlayer
            .Builder(this)
            .setSeekForwardIncrementMs(1000 * 10)
            .setSeekBackIncrementMs(1000 * 5)
            .build()
        val danmakuPlayer = DanmakuPlayer(SimpleRenderer())
        playerViewModel.preparePlayer(player)
        playerViewModel.prepareDanmakuPlayer(danmakuPlayer)

        if (intent.hasExtra("avid")) {
            val aid = intent.getIntExtra("avid", 170001)
            val cid = intent.getIntExtra("cid", 170001)
            val title = intent.getStringExtra("title") ?: "Unknown Title"
            val partTitle = intent.getStringExtra("partTitle") ?: "Unknown Part Title"
            logger.info { "Launch parameter: [aid=$aid, cid=$cid]" }
            playerViewModel.loadPlayUrl(aid, cid)
            playerViewModel.title = title
        } else {
            logger.info { "Null launch parameter" }
        }

        setContent {
            BVTheme {
                if (playerViewModel.show) {
                    VideoPlayerScreen()
                } else {
                    Text(
                        modifier = Modifier.background(Color.White),
                        text = playerViewModel.errorMessage
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}

@Composable
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
fun VideoPlayerScreen(
    modifier: Modifier = Modifier,
    playerViewModel: PlayerViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val logger = KotlinLogging.logger { }

    val videoPlayer = playerViewModel.player!!
    val danmakuPlayer = playerViewModel.danmakuPlayer!!

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        logger.info { "Request focus on controller" }
        focusRequester.requestFocus()
        //focusRequester.captureFocus()
    }

    var infoData by remember {
        mutableStateOf(
            VideoPlayerInfoData(
                totalDuration = 0,
                currentTime = 0,
                bufferedPercentage = 0,
                resolutionWidth = 0,
                resolutionHeight = 0
            )
        )
    }

    val danmakuConfig by remember {
        mutableStateOf(
            DanmakuConfig(
                visibility = playerViewModel.currentDanmakuEnabled,
                textSizeScale = playerViewModel.currentDanmakuSize.scale * 2,
                alpha = playerViewModel.currentDanmakuTransparency.transparency
            )
        )
    }

    val updateSeek: () -> Unit = {
        infoData = VideoPlayerInfoData(
            totalDuration = videoPlayer.duration.coerceAtLeast(0L),
            currentTime = videoPlayer.currentPosition.coerceAtLeast(0L),
            bufferedPercentage = videoPlayer.bufferedPercentage,
            resolutionWidth = videoPlayer.videoSize.width,
            resolutionHeight = videoPlayer.videoSize.height
        )
    }

    //定时刷新进度条
    DisposableEffect(Unit) {
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                scope.launch {
                    updateSeek()
                }
            }

        }, 500, 500)
        onDispose {
            timer.cancel()
        }
    }

    DisposableEffect(Unit) {
        //exo player listener
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                //缓冲完成，并非播放/暂停
                if (playbackState == Player.STATE_READY) {
                    danmakuPlayer.start(danmakuConfig)
                    danmakuPlayer.seekTo(videoPlayer.currentPosition)

                    playerViewModel.showBuffering = false
                } else {
                    danmakuPlayer.pause()
                    if (playbackState == Player.STATE_BUFFERING) {
                        playerViewModel.showBuffering = true
                    }

                    //隐藏左下角日志
                    scope.launch(Dispatchers.Default) {
                        delay(3_000)
                        playerViewModel.showLogs = false
                    }
                }
            }

            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                if (playWhenReady) {
                    logger.info { "Start danmaku" }
                    danmakuPlayer.updateConfig(danmakuConfig)
                } else {
                    logger.info { "Pause danmaku" }
                    danmakuPlayer.pause()
                }
            }

            //进度条回退
            override fun onSeekBackIncrementChanged(seekBackIncrementMs: Long) {
                danmakuPlayer.seekTo(videoPlayer.currentPosition)
            }

            //进度条快进
            override fun onSeekForwardIncrementChanged(seekForwardIncrementMs: Long) {
                danmakuPlayer.seekTo(videoPlayer.currentPosition)
            }
        }

        val analyticsListener = object : AnalyticsListener {
            override fun onBandwidthEstimate(
                eventTime: AnalyticsListener.EventTime,
                totalLoadTimeMs: Int,
                totalBytesLoaded: Long,
                bitrateEstimate: Long
            ) {
                println("----$totalLoadTimeMs----$totalBytesLoaded----${bitrateEstimate / 1024}KB/s----")
            }
        }

        videoPlayer.addListener(listener)
        videoPlayer.addAnalyticsListener(analyticsListener)

        //release exo video player
        onDispose {
            videoPlayer.removeListener(listener)
            videoPlayer.release()
        }
    }

    LaunchedEffect(playerViewModel.danmakuData) {
        logger.info { "Update danmaku data" }
        danmakuPlayer.updateData(playerViewModel.danmakuData)
    }

    VideoPlayerController(
        modifier = modifier
            //.focusable()
            .focusRequester(focusRequester)
            .fillMaxSize(),
        infoData = infoData,

        resolutionMap = playerViewModel.availableQuality,
        currentResolution = playerViewModel.currentQuality,
        currentDanmakuEnabled = playerViewModel.currentDanmakuEnabled,
        currentDanmakuSize = playerViewModel.currentDanmakuSize,
        currentDanmakuTransparency = playerViewModel.currentDanmakuTransparency,

        buffering = playerViewModel.showBuffering,
        isPlaying = playerViewModel.player?.isPlaying == true,
        bufferSpeed = "playerViewModel.player?.",

        showLogs = playerViewModel.showLogs,
        logs = playerViewModel.logs,

        title = playerViewModel.title,

        onChooseResolution = { qualityId ->
            playerViewModel.currentQuality = qualityId
            videoPlayer.pause()
            val current = videoPlayer.currentPosition
            scope.launch(Dispatchers.Default) {
                playerViewModel.playQuality(qualityId)
                withContext(Dispatchers.Main) {
                    videoPlayer.seekTo(current)
                    videoPlayer.play()
                }
            }
        },
        onSwitchDanmaku = { enable ->
            Prefs.defaultDanmakuEnabled = enable
            playerViewModel.currentDanmakuEnabled = enable
            danmakuConfig.visibility = enable
            danmakuPlayer.updateConfig(danmakuConfig)
        },
        onDanmakuSizeChange = { size ->
            Prefs.defaultDanmakuSize = size.ordinal
            playerViewModel.currentDanmakuSize = size
            danmakuConfig.textSizeScale = size.scale * 2
            danmakuPlayer.updateConfig(danmakuConfig)
        },
        onDanmakuTransparencyChange = { transparency ->
            Prefs.defaultDanmakuTransparency = transparency.ordinal
            playerViewModel.currentDanmakuTransparency = transparency
            danmakuConfig.alpha = transparency.transparency
            danmakuPlayer.updateConfig(danmakuConfig)
        },
        onSeekBack = {
            playerViewModel.player?.seekBack()
            playerViewModel.player?.play()
        },
        onSeekForward = {
            playerViewModel.player?.seekForward()
            playerViewModel.player?.play()
        },
        onPlay = {
            playerViewModel.player?.play()
            playerViewModel.danmakuPlayer?.start()
        },
        onPause = {
            playerViewModel.player?.pause()
        },
        requestFocus = {
            focusRequester.requestFocus()
        }
    ) {
        Box {
            AndroidView(
                factory = {
                    PlayerView(context).apply {
                        player = videoPlayer
                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                        useController = false
                    }
                }
            )

            DanmakuPlayerCompose(
                danmakuPlayer = danmakuPlayer
            )
        }
    }
}

@Composable
private fun VideoPlayerController(
    modifier: Modifier = Modifier,
    infoData: VideoPlayerInfoData,
    resolutionMap: Map<Int, String> = emptyMap(),
    currentResolution: Int? = null,
    currentDanmakuEnabled: Boolean = true,
    currentDanmakuSize: DanmakuSize = DanmakuSize.S2,
    currentDanmakuTransparency: DanmakuTransparency = DanmakuTransparency.T1,
    buffering: Boolean,
    isPlaying: Boolean,
    bufferSpeed: Any,
    showLogs: Boolean,
    logs: String,
    title: String,
    onChooseResolution: (qualityId: Int) -> Unit,
    onSwitchDanmaku: (enable: Boolean) -> Unit,
    onDanmakuSizeChange: (DanmakuSize) -> Unit,
    onDanmakuTransparencyChange: (DanmakuTransparency) -> Unit,
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
        logger.info { "Request focus on controller" }
        //focusRequester.captureFocus()
    }

    LaunchedEffect(tick) {
        //logger.info { "Request focus on controller2" }
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
                if (BuildConfig.DEBUG) logger.info { "Native key event: ${it.nativeKeyEvent}" }

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
                        logger.info { "Pressed dpad up" }
                        if (showingRightController()) return@onPreviewKeyEvent false

                    }

                    KeyEvent.KEYCODE_DPAD_DOWN -> {
                        if (it.nativeKeyEvent.action == KeyEvent.ACTION_DOWN) return@onPreviewKeyEvent true
                        logger.info { "Pressed dpad down" }
                        if (showingRightController()) return@onPreviewKeyEvent false
                        showSeekController = !showSeekController
                        return@onPreviewKeyEvent true
                    }

                    KeyEvent.KEYCODE_DPAD_LEFT -> {
                        if (it.nativeKeyEvent.action == KeyEvent.ACTION_UP) return@onPreviewKeyEvent true
                        logger.info { "Pressed dpad left" }
                        if (showingRightController()) return@onPreviewKeyEvent false
                        showSeekController = true
                        lastChangedSeek = System.currentTimeMillis()
                        onSeekBack()
                        return@onPreviewKeyEvent true
                    }

                    KeyEvent.KEYCODE_DPAD_RIGHT -> {
                        if (it.nativeKeyEvent.action == KeyEvent.ACTION_UP) return@onPreviewKeyEvent true
                        logger.info { "Pressed dpad right" }
                        if (showingRightController()) return@onPreviewKeyEvent false
                        showSeekController = true
                        lastChangedSeek = System.currentTimeMillis()
                        onSeekForward()
                        return@onPreviewKeyEvent true
                    }

                    KeyEvent.KEYCODE_DPAD_CENTER -> {
                        logger.info { "Pressed dpad center" }
                        if (showingRightController()) return@onPreviewKeyEvent false
                        if (it.nativeKeyEvent.isLongPress) {
                            logger.info { "long pressing" }
                            showMenuController = true
                            return@onPreviewKeyEvent true
                        }
                        logger.info { "short pressing" }
                        if (it.nativeKeyEvent.action == KeyEvent.ACTION_DOWN) return@onPreviewKeyEvent true
                        if (isPlaying) onPause() else onPlay()
                        return@onPreviewKeyEvent true
                    }

                    KeyEvent.KEYCODE_MENU, KeyEvent.KEYCODE_BOOKMARK -> {
                        if (it.nativeKeyEvent.action == KeyEvent.ACTION_DOWN) return@onPreviewKeyEvent true
                        logger.info { "Pressed dpad menu" }
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
                        logger.info { "Pressed dpad back" }
                        if (showingRightController()) {
                            logger.info { "Close menu and part controller" }
                            showMenuController = false
                            showPartController = false
                            return@onPreviewKeyEvent true
                        } else {
                            logger.info { "Exiting video player" }
                            (context as Activity).finish()
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
                currentResolution = currentResolution,
                currentDanmakuEnabled = currentDanmakuEnabled,
                currentDanmakuSize = currentDanmakuSize,
                currentDanmakuTransparency = currentDanmakuTransparency,
                onChooseResolution = onChooseResolution,
                onSwitchDanmaku = onSwitchDanmaku,
                onDanmakuSizeChange = onDanmakuSizeChange,
                onDanmakuTransparencyChange = onDanmakuTransparencyChange
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



