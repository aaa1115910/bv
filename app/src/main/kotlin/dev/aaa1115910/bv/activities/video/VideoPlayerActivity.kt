package dev.aaa1115910.bv.activities.video

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.media3.exoplayer.ExoPlayer
import com.kuaishou.akdanmaku.render.SimpleRenderer
import com.kuaishou.akdanmaku.ui.DanmakuPlayer
import dev.aaa1115910.bv.screen.VideoPlayerScreen
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.viewmodel.PlayerViewModel
import mu.KotlinLogging
import org.koin.androidx.viewmodel.ext.android.viewModel

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
            logger.fInfo { "Launch parameter: [aid=$aid, cid=$cid]" }
            playerViewModel.loadPlayUrl(aid, cid)
            playerViewModel.title = title
        } else {
            logger.fInfo { "Null launch parameter" }
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
