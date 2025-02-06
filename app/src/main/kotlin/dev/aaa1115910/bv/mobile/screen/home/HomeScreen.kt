package dev.aaa1115910.bv.mobile.screen.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import dev.aaa1115910.bv.mobile.activities.VideoPlayerActivity
import dev.aaa1115910.bv.mobile.component.home.HomeTab
import dev.aaa1115910.bv.mobile.screen.home.home.PopularPage
import dev.aaa1115910.bv.mobile.screen.home.home.RcmdPage
import dev.aaa1115910.bv.viewmodel.UserViewModel
import dev.aaa1115910.bv.viewmodel.home.PopularViewModel
import dev.aaa1115910.bv.viewmodel.home.RecommendViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.Int

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    rcmdGridState: LazyGridState,
    popularGridState: LazyGridState,
    popularViewModel: PopularViewModel = koinViewModel(),
    recommendViewModel: RecommendViewModel = koinViewModel(),
    userViewModel: UserViewModel = koinViewModel(),
    windowSize: WindowWidthSizeClass,
    onShowUserDialog: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val pageState = rememberPagerState(pageCount = { 2 })

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        topBar = {
            HomeTopAppBar(
                windowSize = windowSize,
                avatar = userViewModel.face,
                onShowUserDialog = onShowUserDialog
            )
        }
    ) { innerPadding ->
        HomeScreenContent(
            modifier = Modifier.padding(top = innerPadding.calculateTopPadding()),
            pageState = pageState,
            selectedTabIndex = pageState.currentPage,
            windowSize = windowSize,
            rcmdGridState = rcmdGridState,
            popularGridState = popularGridState,
            onChangeTabIndex = { scope.launch { pageState.animateScrollToPage(it) } },
            popularViewModel = popularViewModel,
            recommendViewModel = recommendViewModel,
        )
    }
}

@Composable
fun HomeScreenContent(
    modifier: Modifier = Modifier,
    pageState: PagerState,
    selectedTabIndex: Int,
    windowSize: WindowWidthSizeClass,
    rcmdGridState: LazyGridState,
    popularGridState: LazyGridState,
    onChangeTabIndex: (Int) -> Unit,
    popularViewModel: PopularViewModel = koinViewModel(),
    recommendViewModel: RecommendViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceContainer),
    ) {
        TabRow(
            modifier = Modifier
                .zIndex(1f),
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ) {
            HomeTab.entries.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = {
                        onChangeTabIndex(index)
                    },
                    text = {
                        Text(
                            text = tab.getDisplayName(context),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                )
            }
        }

        Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = if (windowSize == WindowWidthSizeClass.Compact) RoundedCornerShape(0.dp) else MaterialTheme.shapes.large,
        ) {
            HorizontalPager(
                modifier = Modifier,
                state = pageState,
            ) { page ->
                when (page) {
                    0 -> {
                        RcmdPage(
                            state = rcmdGridState,
                            windowSize = windowSize,
                            videos = recommendViewModel.recommendVideoList,
                            onClickVideo = { aid ->
                                VideoPlayerActivity.actionStart(context = context, aid = aid)
                            },
                            loading = recommendViewModel.loading,
                            refreshing = recommendViewModel.refreshing,
                            onRefresh = {
                                scope.launch(Dispatchers.IO) {
                                    recommendViewModel.resetPage()
                                    //避免刷新太快
                                    delay(300)
                                    recommendViewModel.loadMore {
                                        //clear data before set new data
                                        recommendViewModel.clearData()
                                    }
                                    recommendViewModel.refreshing = false
                                }
                            },
                            loadMore = {
                                scope.launch(Dispatchers.IO) {
                                    recommendViewModel.loadMore()
                                    recommendViewModel.refreshing = false
                                }
                            }
                        )
                    }

                    1 -> {
                        PopularPage(
                            state = popularGridState,
                            windowSize = windowSize,
                            videos = popularViewModel.popularVideoList,
                            onClickVideo = { aid ->
                                VideoPlayerActivity.actionStart(context = context, aid = aid)
                            },
                            loading = popularViewModel.loading,
                            refreshing = popularViewModel.refreshing,
                            onRefresh = {
                                scope.launch(Dispatchers.IO) {
                                    popularViewModel.resetPage()
                                    //避免刷新太快
                                    delay(300)
                                    popularViewModel.loadMore {
                                        //clear data before set new data
                                        popularViewModel.clearData()
                                    }
                                    popularViewModel.refreshing = false
                                }
                            },
                            loadMore = {
                                scope.launch(Dispatchers.IO) {
                                    popularViewModel.loadMore()
                                    popularViewModel.refreshing = false
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopAppBar(
    modifier: Modifier = Modifier,
    windowSize: WindowWidthSizeClass,
    avatar: String,
    onShowUserDialog: () -> Unit,
) {
    if (windowSize == WindowWidthSizeClass.Compact) {
        TopAppBar(
            modifier = modifier,
            title = {
            },
            navigationIcon = {},
            actions = {
                IconButton(onClick = onShowUserDialog) {
                    if (avatar.isBlank()) {
                        Icon(
                            imageVector = Icons.Rounded.Person,
                            contentDescription = null
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.Gray)
                        ) {
                            AsyncImage(
                                modifier = Modifier
                                    .size(36.dp),
                                model = avatar,
                                contentDescription = null,
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
            )
        )
    }
}
