package dev.aaa1115910.bv.mobile.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.zIndex
import androidx.lifecycle.ViewModel
import androidx.media3.exoplayer.ExoPlayer
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dev.aaa1115910.bv.mobile.theme.BVMobileTheme
import dev.aaa1115910.bv.player.mobile.component.BvPlayer
import dev.aaa1115910.bv.player.mobile.component.playUrl
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class VideoPlayerActivity : ComponentActivity() {
    private val playerViewModel: MobileVideoPlayerViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initVideoPlayer()
        setContent {
            BVMobileTheme {
                VideoPlayerScreen()
            }
        }
    }

    fun initVideoPlayer() {
        if (playerViewModel.videoPlayer != null) return
        val videoUrl =
            "https://storage.googleapis.com/downloads.webmproject.org/av1/exoplayer/bbb-av1-480p.mp4"
        val audioUrl = null
        playerViewModel.videoPlayer = ExoPlayer.Builder(this).build()
        playerViewModel.videoPlayer?.playUrl(videoUrl, audioUrl)
        playerViewModel.videoPlayer?.prepare()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoPlayerScreen(
    playerViewModel: MobileVideoPlayerViewModel = koinViewModel()
) {
    val context = LocalContext.current

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.isStatusBarVisible = !isLandscape
        systemUiController.isNavigationBarVisible = !isLandscape
    }

    LaunchedEffect(Unit) {
    }

    val bvPlayerContent = remember {
        // TODO movableContentOf here doesn't avoid Media from recreating its surface view when
        // screen rotation changed. Seems like a bug of Compose.
        // see: https://kotlinlang.slack.com/archives/CJLTWPH7S/p1654734644676989
        movableContentOf { isLandscape: Boolean, modifier: Modifier ->
            BvPlayer(
                modifier = modifier,
                isLandscape = isLandscape,
                videoPlayer = playerViewModel.videoPlayer!!,
                onEnterFullScreen = {
                    (context as Activity).requestedOrientation =
                        ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
                },
                onExitFullScreen = {
                    @SuppressLint("SourceLockedOrientationActivity")
                    (context as Activity).requestedOrientation =
                        ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT
                }
            )
        }
    }

    Scaffold(
        topBar = {
            LargeTopAppBar(title = { Text(text = "title") })
        }
    ) { innerPadding ->
        if (playerViewModel.videoPlayer != null) {
            if (!isLandscape) {
                bvPlayerContent(
                    false,
                    Modifier
                        .padding(innerPadding)
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                )
            } else {
                if (playerViewModel.videoPlayer != null)
                    bvPlayerContent(
                        true,
                        Modifier
                            .fillMaxSize()
                            .zIndex(1f)
                    )
            }
        }

    }

    SideEffect {
        if (isLandscape) {
            if ((context as Activity).requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_USER) {
                (context as Activity).requestedOrientation =
                    ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
            }
        } else {
            (context as Activity).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER
        }
    }
}


class MobileVideoPlayerViewModel : ViewModel() {
    var videoPlayer: ExoPlayer? = null
}