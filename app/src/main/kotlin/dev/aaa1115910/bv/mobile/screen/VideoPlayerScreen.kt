package dev.aaa1115910.bv.mobile.screen

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.mobile.viewmodel.MobileVideoPlayerViewModel
import dev.aaa1115910.bv.mobile.theme.BVMobileTheme
import dev.aaa1115910.bv.player.mobile.component.BvPlayer
import org.koin.androidx.compose.koinViewModel

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
        systemUiController.setStatusBarColor(Color.Black)
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

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            if (playerViewModel.videoPlayer != null) {
                if (!isLandscape) {
                    bvPlayerContent(
                        false,
                        Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                    )
                } else {
                    bvPlayerContent(
                        true,
                        Modifier
                            .fillMaxSize()
                            .zIndex(1f)
                    )
                }
            }
            VideoPlayerInfo(
                modifier = Modifier.padding(12.dp),
                upAvatar = playerViewModel.videoInfo?.owner?.face ?: "",
                upName = playerViewModel.videoInfo?.owner?.name ?: "",
                upFansCount = 0,
                title = playerViewModel.videoInfo?.title ?: "",
                description = playerViewModel.videoInfo?.desc ?: "",
                playCount = playerViewModel.videoInfo?.stat?.view ?: 0,
                danmakuCount = playerViewModel.videoInfo?.stat?.danmaku ?: 0,
                date = playerViewModel.videoInfo?.pubdate?.toString() ?: "",
                avid = playerViewModel.videoInfo?.aid ?: 0
            )
        }
    }

    SideEffect {
        if (isLandscape) {
            if ((context as Activity).requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_USER) {
                (context as Activity).requestedOrientation =
                    ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
            }
            (context as Activity).window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        } else {
            (context as Activity).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER
        }
    }
}

@Composable
fun VideoPlayerInfo(
    modifier: Modifier = Modifier,
    upAvatar: String,
    upName: String,
    upFansCount: Int,
    title: String,
    description: String,
    playCount: Int,
    danmakuCount: Int,
    date: String,
    avid: Int
) {
    val summaryTextStyle = MaterialTheme.typography.bodySmall.copy(
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    )

    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.height(64.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .padding(0.dp, 8.dp, 8.dp, 8.dp)
                ) {
                    AsyncImage(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.Gray),
                        model = upAvatar,
                        contentDescription = null
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(vertical = 12.dp),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        text = upName,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = "$upFansCount",
                        style = summaryTextStyle,
                        fontSize = 10.sp
                    )
                }
            }

            Button(onClick = { /*TODO*/ }) {
                Text(text = "Follow")
            }
        }
        Text(
            text = title,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleLarge
        )
        ProvideTextStyle(summaryTextStyle) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        modifier = Modifier,
                        painter = painterResource(id = R.drawable.ic_play_count),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(text = "$playCount")
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        modifier = Modifier,
                        painter = painterResource(id = R.drawable.ic_danmaku_count),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(text = "$danmakuCount")
                }
                Text(text = date)
                Text(text = "av$avid")
            }
            Text(text = description)
        }
    }
}

@Preview
@Composable
private fun VideoPlayerInfoPreview() {
    BVMobileTheme {
        Surface {
            VideoPlayerInfo(
                modifier = Modifier.padding(24.dp),
                upAvatar = "https://i0.hdslb.com/bfs/article/b6b843d84b84a3ba5526b09ebf538cd4b4c8c3f3.jpg@450w_450h_progressive.webp",
                upName = "bishi",
                upFansCount = 1400000000,
                title = "This is the video title... repeat, this is the video title.",
                description = "descriptions....descriptions....descriptions....descriptions....descriptions....descriptions....descriptions....descriptions....descriptions....",
                playCount = 2434,
                danmakuCount = 14,
                date = "2023-5-22 23:17",
                avid = 170001,
            )
        }
    }
}