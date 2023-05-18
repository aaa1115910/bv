package dev.aaa1115910.bv.activities.video

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.media3.exoplayer.ExoPlayer
import androidx.tv.material3.Text
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.screen.VideoPlayerScreen
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.viewmodel.PlayerViewModel
import mu.KotlinLogging
import org.koin.androidx.viewmodel.ext.android.viewModel

class VideoPlayerActivity : ComponentActivity() {
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
                Intent(context, VideoPlayerActivity::class.java).apply {
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

    private val playerViewModel: PlayerViewModel by viewModel()

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val player = ExoPlayer
            .Builder(this)
            .setSeekForwardIncrementMs(1000 * 10)
            .setSeekBackIncrementMs(1000 * 5)
            .build()
        playerViewModel.preparePlayer(player)

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

        setContent {
            BVTheme {
                if (playerViewModel.needPay) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.player_tip_need_pay),
                            textAlign = TextAlign.Center
                        )
                    }
                } else if (playerViewModel.show) {
                    VideoPlayerScreen()
                } else {
                    Text(text = playerViewModel.errorMessage)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onPause() {
        super.onPause()
        playerViewModel.player?.pause()
        playerViewModel.danmakuPlayer?.pause()
    }
}
