package dev.aaa1115910.bv.mobile.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.lifecycle.lifecycleScope
import com.kuaishou.akdanmaku.render.SimpleRenderer
import com.kuaishou.akdanmaku.ui.DanmakuPlayer
import dev.aaa1115910.biliapi.entity.ApiType
import dev.aaa1115910.biliapi.http.BiliHttpApi
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.entity.PlayerType
import dev.aaa1115910.bv.mobile.screen.VideoPlayerScreen
import dev.aaa1115910.bv.mobile.theme.BVMobileTheme
import dev.aaa1115910.bv.player.VideoPlayerOptions
import dev.aaa1115910.bv.player.impl.exo.ExoPlayerFactory
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.toast
import dev.aaa1115910.bv.viewmodel.CommentViewModel
import dev.aaa1115910.bv.viewmodel.VideoPlayerV3ViewModel
import dev.aaa1115910.bv.viewmodel.video.VideoDetailViewModel
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel

class VideoPlayerActivity : ComponentActivity() {
    companion object {
        fun actionStart(
            context: Context,
            aid: Long,
            //cid: Long,
            fromSeason: Boolean = false,
            epid: Int? = null,
            seasonId: Int? = null,
        ) {
            context.startActivity(
                Intent(context, VideoPlayerActivity::class.java).apply {
                    putExtra("aid", aid)
                    //putExtra("cid", cid)
                    putExtra("fromSeason", fromSeason)
                    epid?.let { putExtra("epid", it) }
                    seasonId?.let { putExtra("seasonId", it) }
                }
            )
        }
    }

    private val playerViewModel: VideoPlayerV3ViewModel by viewModel()
    private val commentViewModel: CommentViewModel by viewModel()
    private val videoDetailViewModel: VideoDetailViewModel by viewModel()
    private val logger = KotlinLogging.logger {}

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initVideoPlayer()
        initDanmakuPlayer()
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
        val options = VideoPlayerOptions(
            userAgent = when (Prefs.apiType) {
                ApiType.Web -> getString(R.string.video_player_user_agent_http)
                ApiType.App -> getString(R.string.video_player_user_agent_client)
            },
            referer = when (Prefs.apiType) {
                ApiType.Web -> getString(R.string.video_player_referer)
                ApiType.App -> null
            }
        )
        val videoPlayer = when (Prefs.playerType) {
            PlayerType.Media3 -> ExoPlayerFactory().create(this, options)
        }
        playerViewModel.videoPlayer = videoPlayer
        //TODO 还没处理旋转后的一些判断，就先放这了
        parseIntent()
    }

    private fun initDanmakuPlayer() {
        if (playerViewModel.danmakuPlayer != null) return
        logger.fInfo { "initDanmakuPlayer" }
        playerViewModel.danmakuPlayer = DanmakuPlayer(SimpleRenderer())
    }

    private fun parseIntent() {
        var aid = intent.getLongExtra("aid", 0)
        var cid = intent.getLongExtra("cid", 0)
        val fromSeason = intent.getBooleanExtra("fromSeason", false)
        val epid = intent.getIntExtra("epid", 0)
        val seasonId = intent.getIntExtra("seasonId", 0)

        lifecycleScope.launch(Dispatchers.IO) {
            if (aid == 0L && cid == 0L) {
                runCatching {
                    val acid = BiliHttpApi.getAidCidByEpid(epid)!!
                    aid = acid.first
                    cid = acid.second
                }.onFailure {
                    logger.fInfo { "get avid & cid by epid failed: ${it.stackTraceToString()}" }
                    withContext(Dispatchers.Main) {
                        it.message?.toast(this@VideoPlayerActivity)
                    }
                }
            }

            commentViewModel.commentType = 1
            commentViewModel.commentId = aid

            runCatching {
                videoDetailViewModel.loadDetail(aid, fromSeason)
            }.onFailure {
                withContext(Dispatchers.Main) {
                    it.message?.toast(this@VideoPlayerActivity)
                }
            }
            runCatching {
                playerViewModel.fromSeason = fromSeason
                playerViewModel.loadPlayUrl(
                    avid = videoDetailViewModel.videoDetail?.aid ?: 0,
                    cid = videoDetailViewModel.videoDetail?.cid ?: 0,
                    epid = epid.takeIf { it != 0 },
                    seasonId = seasonId.takeIf { it != 0 }
                )
            }.onFailure {
                withContext(Dispatchers.Main) {
                    it.message?.toast(this@VideoPlayerActivity)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        playerViewModel.videoPlayer?.release()
    }

    override fun onPause() {
        super.onPause()
        playerViewModel.videoPlayer?.pause()
        playerViewModel.danmakuPlayer?.pause()
    }
}
