package dev.aaa1115910.bv.tv.activities.video

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dev.aaa1115910.biliapi.entity.ApiType
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.entity.PlayerType
import dev.aaa1115910.bv.entity.proxy.ProxyArea
import dev.aaa1115910.bv.player.VideoPlayerOptions
import dev.aaa1115910.bv.player.impl.exo.ExoPlayerFactory
import dev.aaa1115910.bv.tv.screens.VideoPlayerV3Screen
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.viewmodel.VideoPlayerV3ViewModel
import io.github.oshai.kotlinlogging.KotlinLogging
import org.koin.androidx.viewmodel.ext.android.viewModel

class VideoPlayerV3Activity : ComponentActivity() {
    companion object {
        private val logger = KotlinLogging.logger { }
        fun actionStart(
            context: Context,
            avid: Long,
            cid: Long,
            title: String,
            partTitle: String,
            played: Int,
            fromSeason: Boolean,
            subType: Int? = null,
            epid: Int? = null,
            seasonId: Int? = null,
            isVerticalVideo: Boolean = false,
            proxyArea: ProxyArea = ProxyArea.MainLand,
            playerIconIdle: String = "",
            playerIconMoving: String = ""
        ) {
            context.startActivity(
                Intent(
                    context,
                    dev.aaa1115910.bv.tv.activities.video.VideoPlayerV3Activity::class.java
                ).apply {
                    putExtra("avid", avid)
                    putExtra("cid", cid)
                    putExtra("title", title)
                    putExtra("partTitle", partTitle)
                    putExtra("played", played)
                    putExtra("fromSeason", fromSeason)
                    putExtra("subType", subType)
                    putExtra("epid", epid)
                    putExtra("seasonId", seasonId)
                    putExtra("isVerticalVideo", isVerticalVideo)
                    putExtra("proxy_area", proxyArea.ordinal)
                    putExtra("playerIconIdle", playerIconIdle)
                    putExtra("playerIconMoving", playerIconMoving)
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
            BVTheme(
                forceDark = true
            ) {
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
        dev.aaa1115910.bv.tv.activities.video.VideoPlayerV3Activity.Companion.logger.info { "Init video player: ${Prefs.playerType.name}" }
        val options = VideoPlayerOptions(
            userAgent = when (Prefs.apiType) {
                ApiType.Web -> getString(R.string.video_player_user_agent_http)
                ApiType.App -> getString(R.string.video_player_user_agent_client)
            },
            referer = when (Prefs.apiType) {
                ApiType.Web -> getString(R.string.video_player_referer)
                ApiType.App -> null
            },
            enableFfmpegAudioRenderer = Prefs.enableFfmpegAudioRenderer
        )
        val videoPlayer = when (Prefs.playerType) {
            PlayerType.Media3 -> ExoPlayerFactory().create(this, options)
        }
        playerViewModel.videoPlayer = videoPlayer
    }

    /*private fun initDanmakuPlayer() {
        logger.info { "Init danamku player" }
        runBlocking { playerViewModel.initDanmakuPlayer() }
    }*/

    private fun getParamsFromIntent() {
        if (intent.hasExtra("avid")) {
            val aid = intent.getLongExtra("avid", 170001)
            val cid = intent.getLongExtra("cid", 170001)
            val title = intent.getStringExtra("title") ?: "Unknown Title"
            val partTitle = intent.getStringExtra("partTitle") ?: "Unknown Part Title"
            val played = intent.getIntExtra("played", 0)
            val fromSeason = intent.getBooleanExtra("fromSeason", false)
            val subType = intent.getIntExtra("subType", 0)
            val epid = intent.getIntExtra("epid", 0)
            val seasonId = intent.getIntExtra("seasonId", 0)
            val isVerticalVideo = intent.getBooleanExtra("isVerticalVideo", false)
            val proxyArea = ProxyArea.entries[intent.getIntExtra("proxy_area", 0)]
            val playerIconIdle = intent.getStringExtra("playerIconIdle") ?: ""
            val playerIconMoving = intent.getStringExtra("playerIconMoving") ?: ""
            dev.aaa1115910.bv.tv.activities.video.VideoPlayerV3Activity.Companion.logger.fInfo { "Launch parameter: [aid=$aid, cid=$cid]" }
            playerViewModel.apply {
                loadPlayUrl(
                    avid = aid,
                    cid = cid,
                    epid = epid.takeIf { it != 0 }
                )
                this.title = title
                this.partTitle = partTitle
                this.lastPlayed = played
                this.fromSeason = fromSeason
                this.subType = subType
                this.epid = epid
                this.seasonId = seasonId
                this.isVerticalVideo = isVerticalVideo
                this.proxyArea = proxyArea
                this.playerIconIdle = playerIconIdle
                this.playerIconMoving = playerIconMoving
            }
        } else {
            dev.aaa1115910.bv.tv.activities.video.VideoPlayerV3Activity.Companion.logger.fInfo { "Null launch parameter" }
        }
    }
}
