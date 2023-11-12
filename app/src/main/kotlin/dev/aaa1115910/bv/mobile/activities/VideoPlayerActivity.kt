package dev.aaa1115910.bv.mobile.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.lifecycle.lifecycleScope
import androidx.media3.exoplayer.ExoPlayer
import dev.aaa1115910.bv.mobile.screen.VideoPlayerScreen
import dev.aaa1115910.bv.mobile.theme.BVMobileTheme
import dev.aaa1115910.bv.mobile.viewmodel.MobileVideoPlayerViewModel
import dev.aaa1115910.bv.util.fInfo
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class VideoPlayerActivity : ComponentActivity() {
    companion object {
        fun actionStart(context: Context, aid: Int, fromSeason: Boolean = false) {
            context.startActivity(
                Intent(context, VideoPlayerActivity::class.java).apply {
                    putExtra("aid", aid)
                    putExtra("fromSeason", fromSeason)
                }
            )
        }
    }

    private val playerViewModel: MobileVideoPlayerViewModel by viewModel()
    private val logger = KotlinLogging.logger {}

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initVideoPlayer()
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            BVMobileTheme {
                VideoPlayerScreen(
                    windowSizeClass = windowSizeClass
                )
            }
        }
    }

    private fun initVideoPlayer() {
        if (playerViewModel.videoPlayer != null) return
        logger.fInfo { "initVideoPlayer" }
        playerViewModel.videoPlayer = ExoPlayer.Builder(this).build()
        //TODO 还没处理旋转后的一些判断，就先放这了
        parseIntent()
    }

    private fun parseIntent() {
        val aid = intent.getIntExtra("aid", 0)
        lifecycleScope.launch(Dispatchers.IO) {
            playerViewModel.updateVideoInfo(aid)
            playerViewModel.playFirstPartVideo()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        playerViewModel.videoPlayer?.release()
    }

    override fun onPause() {
        super.onPause()
        playerViewModel.videoPlayer?.pause()
    }
}
