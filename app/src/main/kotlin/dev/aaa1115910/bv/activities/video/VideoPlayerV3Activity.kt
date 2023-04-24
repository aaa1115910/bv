package dev.aaa1115910.bv.activities.video

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.tv.material3.Text
import com.kuaishou.akdanmaku.DanmakuConfig
import com.kuaishou.akdanmaku.data.DanmakuItemData
import com.kuaishou.akdanmaku.ecs.component.filter.TypeFilter
import com.kuaishou.akdanmaku.ext.RETAINER_BILIBILI
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.component.DanmakuPlayerCompose
import dev.aaa1115910.bv.component.controllers.LocalVideoPlayerControllerData
import dev.aaa1115910.bv.component.controllers.VideoPlayerControllerData
import dev.aaa1115910.bv.component.controllers.info.VideoPlayerInfoData
import dev.aaa1115910.bv.component.controllers2.DanmakuType
import dev.aaa1115910.bv.component.controllers2.VideoPlayerController
import dev.aaa1115910.bv.entity.PlayerType
import dev.aaa1115910.bv.entity.VideoAspectRatio
import dev.aaa1115910.bv.player.BvVideoPlayer
import dev.aaa1115910.bv.player.VideoPlayerListener
import dev.aaa1115910.bv.player.VideoPlayerOptions
import dev.aaa1115910.bv.player.impl.exo.ExoPlayerFactory
import dev.aaa1115910.bv.player.impl.vlc.VlcPlayerFactory
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.countDownTimer
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.swapList
import dev.aaa1115910.bv.viewmodel.VideoPlayerV3ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Timer
import java.util.TimerTask

class VideoPlayerV3Activity : ComponentActivity() {
    companion object {
        private val logger = KotlinLogging.logger { }
        fun actionStart(
            context: Context,
            avid: Int,
            cid: Int,
            title: String,
            partTitle: String,
            played: Int,
            fromSeason: Boolean,
            subType: Int? = null,
            epid: Int? = null,
            seasonId: Int? = null
        ) {
            context.startActivity(
                Intent(context, VideoPlayerV3Activity::class.java).apply {
                    putExtra("avid", avid)
                    putExtra("cid", cid)
                    putExtra("title", title)
                    putExtra("partTitle", partTitle)
                    putExtra("played", played)
                    putExtra("fromSeason", fromSeason)
                    putExtra("subType", subType)
                    putExtra("epid", epid)
                    putExtra("seasonId", seasonId)
                }
            )
        }
    }

    private val playerViewModel: VideoPlayerV3ViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initVideoPlayer()
        //initDanmakuPlayer()
        getParamsFromIntent()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContent {
            BVTheme {
                VideoPlayerV3Screen()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onPause() {
        super.onPause()
        playerViewModel.videoPlayer?.pause()
        playerViewModel.danmakuPlayer?.pause()
    }

    private fun initVideoPlayer() {
        logger.info { "Init video player: ${Prefs.playerType.name}" }
        val options = VideoPlayerOptions(
            userAgent = getString(R.string.video_player_user_agent),
            referer = getString(R.string.video_player_referer)
        )
        val videoPlayer = when (Prefs.playerType) {
            PlayerType.Media3 -> ExoPlayerFactory().create(this, options)
            PlayerType.LibVLC -> VlcPlayerFactory().create(this, options)
        }
        playerViewModel.videoPlayer = videoPlayer
    }

    /*private fun initDanmakuPlayer() {
        logger.info { "Init danamku player" }
        runBlocking { playerViewModel.initDanmakuPlayer() }
    }*/

    private fun getParamsFromIntent() {
        if (intent.hasExtra("avid")) {
            val aid = intent.getIntExtra("avid", 170001)
            val cid = intent.getIntExtra("cid", 170001)
            val title = intent.getStringExtra("title") ?: "Unknown Title"
            val partTitle = intent.getStringExtra("partTitle") ?: "Unknown Part Title"
            val played = intent.getIntExtra("played", 0)
            val fromSeason = intent.getBooleanExtra("fromSeason", false)
            val subType = intent.getIntExtra("subType", 0)
            val epid = intent.getIntExtra("epid", 0)
            val seasonId = intent.getIntExtra("seasonId", 0)
            logger.fInfo { "Launch parameter: [aid=$aid, cid=$cid]" }
            playerViewModel.apply {
                loadPlayUrl(aid, cid)
                this.title = title
                this.partTitle = partTitle
                this.lastPlayed = played
                this.fromSeason = fromSeason
                this.subType = subType
                this.epid = epid
                this.seasonId = seasonId
            }
        } else {
            logger.fInfo { "Null launch parameter" }
        }
    }
}

@Composable
fun VideoPlayerV3Screen(
    modifier: Modifier = Modifier,
    playerViewModel: VideoPlayerV3ViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val videoPlayer = playerViewModel.videoPlayer!!
    val logger = KotlinLogging.logger { }

    val focusRequester = remember { FocusRequester() }

    var infoData by remember {
        mutableStateOf(
            VideoPlayerInfoData(
                totalDuration = 0,
                currentTime = 0,
                bufferedPercentage = 0,
                resolutionWidth = 0,
                resolutionHeight = 0,
                codec = "null"
            )
        )
    }
    var debugInfo by remember { mutableStateOf("") }
    var showLogs by remember { mutableStateOf(false) }

    var typeFilter by remember { mutableStateOf(TypeFilter()) }
    var danmakuConfig by remember { mutableStateOf(DanmakuConfig()) }

    var currentVideoAspectRatio by remember { mutableStateOf(VideoAspectRatio.Default) }
    var videoPlayerHeight by remember { mutableStateOf(0.dp) }
    var videoPlayerWidth by remember { mutableStateOf(0.dp) }
    var currentPosition by remember { mutableStateOf(0L) }

    var hideLogsTimer: CountDownTimer? by remember { mutableStateOf(null) }

    val updateSeek: () -> Unit = {
        currentPosition = videoPlayer.currentPosition.coerceAtLeast(0L)
        infoData = VideoPlayerInfoData(
            totalDuration = videoPlayer.duration.coerceAtLeast(0L),
            currentTime = videoPlayer.currentPosition.coerceAtLeast(0L),
            bufferedPercentage = videoPlayer.bufferedPercentage,
            resolutionWidth = videoPlayer.videoWidth,
            resolutionHeight = videoPlayer.videoHeight,
            codec = ""//videoPlayer.videoFormat?.sampleMimeType ?: "null"
        )
        debugInfo = videoPlayer.debugInfo
    }

    val initDanmakuConfig: () -> Unit = {
        val danmakuTypes = playerViewModel.currentDanmakuTypes
        if (!danmakuTypes.contains(DanmakuType.All)) {
            val types = DanmakuType.values().toMutableList()
            types.remove(DanmakuType.All)
            types.removeAll(danmakuTypes)
            val filterTypes = types.mapNotNull {
                when (it) {
                    DanmakuType.Rolling -> DanmakuItemData.DANMAKU_MODE_ROLLING
                    DanmakuType.Top -> DanmakuItemData.DANMAKU_MODE_CENTER_TOP
                    DanmakuType.Bottom -> DanmakuItemData.DANMAKU_MODE_CENTER_BOTTOM
                    else -> null
                }
            }
            filterTypes.forEach { typeFilter.addFilterItem(it) }
        }
        danmakuConfig = danmakuConfig.copy(
            retainerPolicy = RETAINER_BILIBILI,
            textSizeScale = playerViewModel.currentDanmakuScale,
            dataFilter = listOf(typeFilter)
        )
        danmakuConfig.updateFilter()
        logger.info { "Init danmaku config: $danmakuConfig" }
        playerViewModel.danmakuPlayer?.updateConfig(danmakuConfig)
    }

    val updateDanmakuConfigTypeFilter: () -> Unit = {
        val danmakuTypes = playerViewModel.currentDanmakuTypes
        typeFilter.clear()
        if (!danmakuTypes.contains(DanmakuType.All)) {
            val types = DanmakuType.values().toMutableList()
            types.remove(DanmakuType.All)
            types.removeAll(danmakuTypes)
            val filterTypes = types.mapNotNull {
                when (it) {
                    DanmakuType.Rolling -> DanmakuItemData.DANMAKU_MODE_ROLLING
                    DanmakuType.Top -> DanmakuItemData.DANMAKU_MODE_CENTER_TOP
                    DanmakuType.Bottom -> DanmakuItemData.DANMAKU_MODE_CENTER_BOTTOM
                    else -> null
                }
            }
            filterTypes.forEach { typeFilter.addFilterItem(it) }
        }
        logger.info { "Update danmaku type filters: ${typeFilter.filterSet}" }
        danmakuConfig.updateFilter()
        playerViewModel.danmakuPlayer?.updateConfig(danmakuConfig)
    }

    val updateDanmakuConfig: () -> Unit = {
        danmakuConfig = danmakuConfig.copy(
            retainerPolicy = RETAINER_BILIBILI,
            textSizeScale = playerViewModel.currentDanmakuScale,
        )
        logger.info { "Update danmaku config: $danmakuConfig" }
        playerViewModel.danmakuPlayer?.updateConfig(danmakuConfig)
    }

    val updateVideoAspectRatio: () -> Unit = {
        videoPlayerWidth = when (currentVideoAspectRatio) {
            VideoAspectRatio.Default -> {
                (playerViewModel.currentVideoWidth / playerViewModel.currentVideoHeight.toFloat()) * videoPlayerHeight
            }

            VideoAspectRatio.FourToThree -> {
                videoPlayerHeight * (4 / 3f)
            }

            VideoAspectRatio.SixteenToNine -> {
                videoPlayerHeight * (16 / 9f)
            }
        }
        logger.info { "Update video player size: $videoPlayerWidth x $videoPlayerHeight" }
    }

    val videoPlayerListener = object : VideoPlayerListener {
        override fun onError(error: String) {
            logger.info { "onError: $error" }
            //TODO("Not yet implemented")
        }

        override fun onReady() {
            logger.info { "onReady" }
            initDanmakuConfig()
            updateVideoAspectRatio()
        }

        override fun onPlay() {
            logger.info { "onPlay" }
            playerViewModel.danmakuPlayer?.start()
        }

        override fun onPause() {
            logger.info { "onPause" }
            playerViewModel.danmakuPlayer?.pause()
        }

        override fun onBuffering() {
            logger.info { "onBuffering" }
        }

        override fun onEnd() {
            logger.info { "onEnd" }
            playerViewModel.danmakuPlayer?.pause()
        }

        override fun onSeekBack(seekBackIncrementMs: Long) {
            playerViewModel.danmakuPlayer?.seekTo(currentPosition)
        }

        override fun onSeekForward(seekForwardIncrementMs: Long) {
            playerViewModel.danmakuPlayer?.seekTo(currentPosition)
        }
    }

    LaunchedEffect(Unit) {
        // LibVLC 需要提前初始化播放器的宽高，才能正常播放
        if (Prefs.playerType == PlayerType.LibVLC) updateVideoAspectRatio()

        focusRequester.requestFocus()
    }

    DisposableEffect(Unit) {
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                scope.launch {
                    updateSeek()

                    //播放一段时间后隐藏跳转历史记录
                    if (playerViewModel.lastPlayed != 0 && infoData.currentTime > 3000) {
                        playerViewModel.lastPlayed = 0
                    }
                }
            }
        }, 0, 100)
        onDispose {
            timer.cancel()
        }
    }

    LaunchedEffect(playerViewModel.lastChangedLog) {
        hideLogsTimer?.cancel()
        showLogs = true
        hideLogsTimer = countDownTimer(3000, 1000, "hideLogsTimer") {
            showLogs = false
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            videoPlayer.release()
        }
    }

    CompositionLocalProvider(
        LocalVideoPlayerControllerData provides VideoPlayerControllerData(
            debugInfo = debugInfo,
            infoData = infoData,
            resolutionMap = playerViewModel.availableQuality,
            availableVideoCodec = playerViewModel.availableVideoCodec,
            availableSubtitle = playerViewModel.availableSubtitle,
            availableSubtitleTracks = playerViewModel.availableSubtitle,
            availableVideoList = playerViewModel.availableVideoList,
            currentVideoCid = playerViewModel.currentCid,
            currentResolution = playerViewModel.currentQuality,
            currentVideoCodec = playerViewModel.currentVideoCodec,
            currentVideoAspectRatio = currentVideoAspectRatio,
            currentDanmakuEnabled = playerViewModel.currentDanmakuEnabled,
            currentDanmakuEnabledList = playerViewModel.currentDanmakuTypes,
            currentDanmakuScale = playerViewModel.currentDanmakuScale,
            currentDanmakuOpacity = playerViewModel.currentDanmakuOpacity,
            currentDanmakuArea = playerViewModel.currentDanmakuArea,
            currentSubtitleId = playerViewModel.currentSubtitleId,
            currentSubtitleData = playerViewModel.currentSubtitleData,
            currentSubtitleFontSize = playerViewModel.currentSubtitleFontSize,
            currentSubtitleBackgroundOpacity = playerViewModel.currentSubtitleBackgroundOpacity,
            currentSubtitleBottomPadding = playerViewModel.currentSubtitleBottomPadding,
            currentPosition = currentPosition,
            lastPlayed = playerViewModel.lastPlayed,
            title = playerViewModel.title,
            secondTitle = playerViewModel.partTitle,
        )
    ) {
        VideoPlayerController(
            modifier = modifier
                .focusRequester(focusRequester),
            videoPlayer = playerViewModel.videoPlayer!!,
            onPlay = { videoPlayer.start() },
            onPause = {
                // TODO 暂停时上报播放记录
                videoPlayer.pause()

            },
            onExit = {
                // TODO 退出前上报播放记录
                (context as Activity).finish()
            },
            onGoTime = {
                videoPlayer.seekTo(it)
                playerViewModel.danmakuPlayer?.seekTo(it)
                // akdanmaku 会在跳转后立即播放，如果需要缓冲则会导致弹幕不同步
                playerViewModel.danmakuPlayer?.pause()
            },
            onBackToHistory = { videoPlayer.seekTo(playerViewModel.lastPlayed * 1000L) },
            onPlayNewVideo = {
                // TODO 播放新视频前上报播放记录
                playerViewModel.loadPlayUrl(
                    avid = it.aid,
                    cid = it.cid,
                    epid = it.epid,
                    seasonId = it.seasonId
                )
            },

            onResolutionChange = { qualityId ->
                playerViewModel.currentQuality = qualityId
                videoPlayer.pause()
                val current = videoPlayer.currentPosition
                scope.launch(Dispatchers.Default) {
                    playerViewModel.updateAvailableCodec()
                    playerViewModel.playQuality(qualityId)
                    withContext(Dispatchers.Main) {
                        videoPlayer.seekTo(current)
                        videoPlayer.start()
                    }
                }
            },
            onCodecChange = { videoCodec ->
                playerViewModel.currentVideoCodec = videoCodec
                videoPlayer.pause()
                val current = videoPlayer.currentPosition
                scope.launch(Dispatchers.Default) {
                    playerViewModel.playQuality(
                        playerViewModel.currentQuality,
                        playerViewModel.currentVideoCodec
                    )
                    withContext(Dispatchers.Main) {
                        videoPlayer.seekTo(current)
                        videoPlayer.start()
                    }
                }
            },
            onAspectRatioChange = { aspectRadio ->
                currentVideoAspectRatio = aspectRadio
                updateVideoAspectRatio()
            },
            onDanmakuSwitchChange = { enabledDanmakuTypes ->
                logger.info { "On enabled danmaku type change: $enabledDanmakuTypes" }
                Prefs.defaultDanmakuTypes = enabledDanmakuTypes
                playerViewModel.currentDanmakuTypes.swapList(enabledDanmakuTypes)
                updateDanmakuConfigTypeFilter()
            },
            onDanmakuSizeChange = { scale ->
                logger.info { "On danmaku scale change: $scale" }
                Prefs.defaultDanmakuScale = scale
                playerViewModel.currentDanmakuScale = scale
                updateDanmakuConfig()
            },
            onDanmakuOpacityChange = { opacity ->
                logger.info { "On danmaku opacity change: $opacity" }
                Prefs.defaultDanmakuOpacity = opacity
                playerViewModel.currentDanmakuOpacity = opacity
            },
            onDanmakuAreaChange = { area ->
                logger.info { "On danmaku area change: $area" }
                Prefs.defaultDanmakuArea = area
                playerViewModel.currentDanmakuArea = area
            },
            onSubtitleChange = { subtitle ->
                playerViewModel.loadSubtitle(subtitle.id)
            },
            onSubtitleSizeChange = { size ->
                logger.info { "On subtitle font size change: $size" }
                Prefs.defaultSubtitleFontSize = size
                playerViewModel.currentSubtitleFontSize = size
            },
            onSubtitleBackgroundOpacityChange = { opacity ->
                logger.info { "On subtitle background opacity change: $opacity" }
                Prefs.defaultSubtitleBackgroundOpacity = opacity
                playerViewModel.currentSubtitleBackgroundOpacity = opacity
            },
            onSubtitleBottomPadding = { padding ->
                logger.info { "On subtitle bottom padding change: $padding" }
                Prefs.defaultSubtitleBottomPadding = padding
                playerViewModel.currentSubtitleBottomPadding = padding
            }
        ) {
            BoxWithConstraints(
                modifier = Modifier.background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                videoPlayerHeight = this.maxHeight

                LaunchedEffect(Unit) {
                    videoPlayer.setOptions()
                }

                BvVideoPlayer(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(videoPlayerWidth),
                    videoPlayer = videoPlayer,
                    playerListener = videoPlayerListener
                )

                DanmakuPlayerCompose(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .fillMaxHeight(playerViewModel.currentDanmakuArea)
                        // 在之前版本中，设置 DanmakuConfig 透明度后，更改其它弹幕设置后，可能会导致弹幕透明度
                        // 突然变成完全不透明一瞬间，因此这次新版选择直接在此处设置透明度
                        .alpha(playerViewModel.currentDanmakuOpacity),
                    danmakuPlayer = playerViewModel.danmakuPlayer
                )

                if (showLogs) {
                    Column(
                        modifier = Modifier.align(Alignment.BottomStart)
                    ) {
                        Text(text = playerViewModel.logs)
                    }
                }
            }
        }
    }
}
