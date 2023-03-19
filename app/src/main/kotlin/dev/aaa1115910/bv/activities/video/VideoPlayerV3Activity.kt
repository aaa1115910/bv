package dev.aaa1115910.bv.activities.video

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.component.DanmakuPlayerCompose
import dev.aaa1115910.bv.component.controllers.LocalVideoPlayerControllerData
import dev.aaa1115910.bv.component.controllers.VideoPlayerControllerData
import dev.aaa1115910.bv.component.controllers.info.VideoPlayerInfoData
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
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.viewmodel.VideoPlayerV3ViewModel
import kotlinx.coroutines.launch
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
            PlayerType.ExoPlayer -> ExoPlayerFactory().create(this, options)
            PlayerType.VLC -> VlcPlayerFactory().create(this, options)
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

    var usingDefaultAspectRatio by remember { mutableStateOf(true) }
    var currentVideoAspectRatio by remember { mutableStateOf(VideoAspectRatio.Default) }
    var currentPosition by remember { mutableStateOf(0L) }

    val updateSeek: () -> Unit = {
        currentPosition = videoPlayer.currentPosition.coerceAtLeast(0L)
        infoData = VideoPlayerInfoData(
            totalDuration = videoPlayer.duration.coerceAtLeast(0L),
            currentTime = videoPlayer.currentPosition.coerceAtLeast(0L),
            bufferedPercentage = videoPlayer.bufferedPercentage,
            resolutionWidth = 0,//videoPlayer.videoSize.width,
            resolutionHeight = 0,//videoPlayer.videoSize.height,
            codec = ""//videoPlayer.videoFormat?.sampleMimeType ?: "null"
        )
    }

    val videoPlayerListener = object : VideoPlayerListener {
        override fun onError(error: String) {
            println("onError: $error")
            //TODO("Not yet implemented")
        }

        override fun onReady() {
            println("onReady")
            //TODO("Not yet implemented")
        }

        override fun onPlay() {
            println("onPlay")
            //TODO("Not yet implemented")
        }

        override fun onPause() {
            println("onPause")
            //TODO("Not yet implemented")
        }

        override fun onBuffering() {
            println("onBuffering")
            //TODO("Not yet implemented")
        }

        override fun onEnd() {
            println("onEnd")
            //TODO("Not yet implemented")
        }

        override fun onSeekBack(seekBackIncrementMs: Long) {
            //TODO("Not yet implemented")
        }

        override fun onSeekForward(seekForwardIncrementMs: Long) {
            //TODO("Not yet implemented")
        }
    }

    LaunchedEffect(Unit) {
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

    DisposableEffect(Unit) {
        onDispose {
            videoPlayer.release()
        }
    }

    CompositionLocalProvider(
        LocalVideoPlayerControllerData provides VideoPlayerControllerData(
            infoData = infoData,
            resolutionMap = playerViewModel.availableQuality,
            availableVideoCodec = playerViewModel.availableVideoCodec,
            availableSubtitle = playerViewModel.availableSubtitle,
            availableVideoList = playerViewModel.availableVideoList,
            currentVideoCid = playerViewModel.currentCid,
            currentResolution = playerViewModel.currentQuality,
            currentVideoCodec = playerViewModel.currentVideoCodec,
            currentVideoAspectRatio = currentVideoAspectRatio,
            currentDanmakuEnabled = playerViewModel.currentDanmakuEnabled,
            currentDanmakuSize = playerViewModel.currentDanmakuSize,
            currentDanmakuTransparency = playerViewModel.currentDanmakuTransparency,
            currentDanmakuArea = playerViewModel.currentDanmakuArea,
            currentSubtitleId = playerViewModel.currentSubtitleId,
            currentSubtitleData = playerViewModel.currentSubtitleData,
            currentSubtitleFontSize = playerViewModel.currentSubtitleFontSize,
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
            onGoTime = { videoPlayer.seekTo(it * 1000L) },
            onBackToHistory = { videoPlayer.seekTo(playerViewModel.lastPlayed * 1000L) },
            onPlayNewVideo = {
                // TODO 播放新视频前上报播放记录
                playerViewModel.loadPlayUrl(it.aid, it.cid)
            }
        ) {
            BoxWithConstraints(
                modifier = Modifier.background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                //videoPlayerHeight = this.maxHeight

                /*val videoPlayerModifier = if (usingDefaultAspectRatio) {
                    Modifier.fillMaxSize()
                } else {
                    Modifier
                        .fillMaxHeight()
                        .width(videoPlayerWidth)
                }*/

                LaunchedEffect(Unit) {
                    videoPlayer.setOptions()
                }

                BvVideoPlayer(
                    modifier = Modifier.fillMaxSize(),
                    videoPlayer = videoPlayer,
                    playerListener = videoPlayerListener
                )

                DanmakuPlayerCompose(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxHeight(playerViewModel.currentDanmakuArea),
                    danmakuPlayer = playerViewModel.danmakuPlayer
                )
            }
        }
    }
}
