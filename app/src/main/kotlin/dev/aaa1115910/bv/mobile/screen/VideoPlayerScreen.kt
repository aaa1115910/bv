package dev.aaa1115910.bv.mobile.screen

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.view.WindowManager
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import dev.aaa1115910.bv.mobile.activities.VideoPlayerActivity
import dev.aaa1115910.bv.mobile.component.videocard.RelatedVideoItem
import dev.aaa1115910.bv.mobile.theme.BVMobileTheme
import dev.aaa1115910.bv.mobile.viewmodel.MobileVideoPlayerViewModel
import dev.aaa1115910.bv.player.mobile.component.BvPlayer
import dev.aaa1115910.bv.player.mobile.util.LocalMobileVideoPlayerData
import dev.aaa1115910.bv.player.mobile.util.MobileVideoPlayerData
import dev.aaa1115910.bv.util.formatPubTimeString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VideoPlayerScreen(
    playerViewModel: MobileVideoPlayerViewModel = koinViewModel(),
    windowSizeClass: WindowSizeClass
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val systemUiController = rememberSystemUiController()

    var isVideoFullscreen by rememberSaveable { mutableStateOf(false) }
    val forcePortrait =
        windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact || windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact

    SideEffect {
        systemUiController.isStatusBarVisible = !isVideoFullscreen
        systemUiController.isNavigationBarVisible = !isVideoFullscreen
        systemUiController.setStatusBarColor(Color.Black)
        if (forcePortrait) {
            if (isVideoFullscreen) {
                (context as Activity).requestedOrientation =
                    ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            } else {
                //在模拟器设为手机尺寸时，横屏时会莫名其妙抛出异常，貌似与折叠屏特性有关，因此手机上强制竖屏
                //java.lang.IllegalArgumentException: Bounding rectangle must start at the top or left window edge for folding features
                @SuppressLint("SourceLockedOrientationActivity")
                (context as Activity).requestedOrientation =
                    ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
            }
        } else {
            (context as Activity).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    LaunchedEffect(isVideoFullscreen) {
        if (isVideoFullscreen) {
            (context as Activity).window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        } else {
            (context as Activity).window.clearFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    val bvPlayerContent = remember {
        // TODO movableContentOf here doesn't avoid Media from recreating its surface view when
        // screen rotation changed. Seems like a bug of Compose.
        // see: https://kotlinlang.slack.com/archives/CJLTWPH7S/p1654734644676989
        movableContentOf { isLandscape: Boolean, modifier: Modifier ->
            BvPlayer(
                modifier = modifier,
                isFullScreen = isLandscape,
                videoPlayer = playerViewModel.videoPlayer!!,
                onEnterFullScreen = {
                    isVideoFullscreen = true
                    //(context as Activity).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
                },
                onExitFullScreen = {
                    isVideoFullscreen = false
                    //@SuppressLint("SourceLockedOrientationActivity")
                    //(context as Activity).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT
                },
                onBack = { (context as Activity).finish() },
                onChangeResolution = {}
            )
        }
    }

    Scaffold { innerPadding ->
        Row(
            modifier = Modifier.padding(top = innerPadding.calculateTopPadding())
        ) {
            val leftPartWidth by animateFloatAsState(
                targetValue = if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded && !isVideoFullscreen) 0.6f else 1f,
                label = "VideoPlayerLeftPartWidth"
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth(leftPartWidth)
            ) {
                if (playerViewModel.videoPlayer != null) {
                    CompositionLocalProvider(
                        LocalMobileVideoPlayerData provides MobileVideoPlayerData(
                            currentResolutionCode = playerViewModel.currentQuality,
                            availableResolutionMap = playerViewModel.availableQuality
                        )
                    ) {
                        BvPlayer(
                            modifier = if (isVideoFullscreen) Modifier
                                .fillMaxSize()
                                .zIndex(1f)
                            else Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f),
                            isFullScreen = isVideoFullscreen,
                            videoPlayer = playerViewModel.videoPlayer!!,
                            onEnterFullScreen = {
                                isVideoFullscreen = true
                            },
                            onExitFullScreen = {
                                isVideoFullscreen = false
                            },
                            onBack = { (context as Activity).finish() },
                            onChangeResolution = { code ->
                                scope.launch(Dispatchers.IO) {
                                    playerViewModel.currentQuality = code
                                    playerViewModel.playQuality(code)
                                }
                            }
                        )
                    }
                }
                val titles = listOf("简介", "评论")
                val pagerState = rememberPagerState(
                    initialPage = 0,
                    initialPageOffsetFraction = 0f,
                    pageCount = { 2 }
                )
                if (windowSizeClass.widthSizeClass != WindowWidthSizeClass.Expanded) {
                    TabRow(
                        selectedTabIndex = pagerState.currentPage
                    ) {
                        titles.forEachIndexed { index, title ->
                            Tab(
                                selected = pagerState.currentPage == index,
                                onClick = { scope.launch { pagerState.scrollToPage(index) } },
                                text = {
                                    Text(
                                        text = title,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            )
                        }
                    }
                    HorizontalPager(
                        state = pagerState
                    ) { page ->
                        when (page) {
                            0 -> {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    item {
                                        VideoPlayerInfo(
                                            modifier = Modifier.padding(12.dp),
                                            upAvatar = playerViewModel.videoDetail?.author?.face
                                                ?: "",
                                            upName = playerViewModel.videoDetail?.author?.name
                                                ?: "",
                                            upFansCount = 0,
                                            title = playerViewModel.videoDetail?.title ?: "",
                                            description = playerViewModel.videoDetail?.description
                                                ?: "",
                                            playCount = playerViewModel.videoDetail?.stat?.view
                                                ?: 0,
                                            danmakuCount = playerViewModel.videoDetail?.stat?.danmaku
                                                ?: 0,
                                            date = playerViewModel.videoDetail?.publishDate
                                                ?.formatPubTimeString(context) ?: "",
                                            avid = playerViewModel.videoDetail?.aid ?: 0
                                        )
                                    }
                                    items(
                                        items = playerViewModel.videoDetail?.relatedVideos
                                            ?: emptyList()
                                    ) { relatedVideo ->
                                        RelatedVideoItem(
                                            relatedVideo = relatedVideo,
                                            onClick = {
                                                VideoPlayerActivity.actionStart(
                                                    context = context,
                                                    aid = relatedVideo.aid,
                                                    fromSeason = relatedVideo.jumpToSeason
                                                )
                                            }
                                        )
                                    }
                                    item {
                                        Spacer(modifier = Modifier.navigationBarsPadding())
                                    }
                                }
                            }

                            1 -> {
                                VideoComments()
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            VideoPlayerInfo(
                                modifier = Modifier.padding(12.dp),
                                upAvatar = playerViewModel.videoDetail?.author?.face ?: "",
                                upName = playerViewModel.videoDetail?.author?.name ?: "",
                                upFansCount = 0,
                                title = playerViewModel.videoDetail?.title ?: "",
                                description = playerViewModel.videoDetail?.description ?: "",
                                playCount = playerViewModel.videoDetail?.stat?.view ?: 0,
                                danmakuCount = playerViewModel.videoDetail?.stat?.danmaku
                                    ?: 0,
                                date = playerViewModel.videoDetail?.publishDate
                                    ?.formatPubTimeString(context) ?: "",
                                avid = playerViewModel.videoDetail?.aid ?: 0
                            )
                        }
                        items(
                            items = playerViewModel.videoDetail?.relatedVideos ?: emptyList()
                        ) { relatedVideo ->
                            RelatedVideoItem(
                                relatedVideo = relatedVideo,
                                onClick = {
                                    VideoPlayerActivity.actionStart(
                                        context = context,
                                        aid = relatedVideo.aid,
                                        fromSeason = relatedVideo.jumpToSeason
                                    )
                                }
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.navigationBarsPadding())
                        }
                    }
                }
            }
            VideoComments(
                modifier = Modifier.fillMaxWidth()
            )
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
            style = MaterialTheme.typography.titleMedium
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

@Composable
fun VideoComments(
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        repeat(100) {
            item {
                ListItem(
                    headlineContent = {
                        Text(text = "This is a comment #$it")
                    }
                )
            }
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