package dev.aaa1115910.bv.activities.video

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.entity.PlayerType
import dev.aaa1115910.bv.player.VideoPlayerOptions
import dev.aaa1115910.bv.player.impl.exo.ExoPlayerFactory
import dev.aaa1115910.bv.player.impl.vlc.VlcPlayerFactory
import dev.aaa1115910.bv.screen.VideoPlayerV3Screen
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.viewmodel.VideoPlayerV3ViewModel
import mu.KotlinLogging
import org.koin.androidx.viewmodel.ext.android.viewModel

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

        if (Prefs.useOldPlayer) {
            launchOldPlayer()
            finish()
        }

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

    private fun launchOldPlayer() {
        VideoPlayerActivity.actionStart(
            context = this,
            avid = intent.getIntExtra("avid", 170001),
            cid = intent.getIntExtra("cid", 170001),
            title = intent.getStringExtra("title") ?: "Unknown Title",
            partTitle = intent.getStringExtra("partTitle") ?: "Unknown Part Title",
            played = intent.getIntExtra("played", 0),
            fromSeason = intent.getBooleanExtra("fromSeason", false),
            subType = intent.getIntExtra("subType", 0),
            epid = intent.getIntExtra("epid", 0),
            seasonId = intent.getIntExtra("seasonId", 0)
        )
    }
}
