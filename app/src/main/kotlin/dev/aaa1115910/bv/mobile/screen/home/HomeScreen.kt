package dev.aaa1115910.bv.mobile.screen.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.aaa1115910.bv.mobile.activities.VideoPlayerActivity
import dev.aaa1115910.bv.mobile.component.home.HomeSearchTopBarCompact
import dev.aaa1115910.bv.mobile.component.home.HomeSearchTopBarExpanded
import dev.aaa1115910.bv.mobile.screen.home.home.PopularPage
import dev.aaa1115910.bv.mobile.screen.home.home.RcmdPage
import dev.aaa1115910.bv.viewmodel.home.PopularViewModel
import dev.aaa1115910.bv.viewmodel.home.RecommendViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    drawerState: DrawerState,
    gridState: LazyGridState,
    popularViewModel: PopularViewModel = koinViewModel(),
    recommendViewModel: RecommendViewModel = koinViewModel(),
    windowSize: WindowWidthSizeClass,
    onSearchActiveChange: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val pageState = rememberPagerState(pageCount = { 2 })
    var searchText by remember { mutableStateOf("") }
    var activeSearch by remember { mutableStateOf(false) }

    //LaunchedEffect(Unit) {
    //    homeViewModel.loadMore()
    //}

    Scaffold(
        modifier = modifier,
        topBar = {
            if (windowSize != WindowWidthSizeClass.Expanded) {
                HomeSearchTopBarCompact(
                    query = searchText,
                    active = activeSearch,
                    selectedTabIndex = pageState.currentPage,
                    onQueryChange = { searchText = it },
                    onActiveChange = {
                        activeSearch = it
                        onSearchActiveChange(it)
                    },
                    onOpenNavDrawer = {
                        scope.launch(Dispatchers.Main) {
                            drawerState.open()
                        }
                    },
                    onChangeTabIndex = {
                        scope.launch {
                            pageState.scrollToPage(it)
                        }
                    }
                )
            }
        },
    ) { innerPadding ->
        val boxModifier = if (windowSize == WindowWidthSizeClass.Expanded)
            Modifier.padding(innerPadding) else Modifier.padding(top = innerPadding.calculateTopPadding())
        Box(
            modifier = boxModifier.fillMaxSize()
        ) {
            if (windowSize == WindowWidthSizeClass.Expanded) {
                HomeSearchTopBarExpanded(
                    query = searchText,
                    active = activeSearch,
                    onQueryChange = { searchText = it },
                    onActiveChange = { activeSearch = it }
                )
            }
            val gridTopPadding = if (windowSize == WindowWidthSizeClass.Expanded) 68.dp else 8.dp
            HorizontalPager(
                modifier = Modifier,
                state = pageState,
            ) { page ->
                when (page) {
                    0 -> {
                        RcmdPage(
                            state = gridState,
                            videos = recommendViewModel.recommendVideoList,
                            onClickVideo = { aid ->
                                VideoPlayerActivity.actionStart(context = context, aid = aid)
                            },
                            refreshing = recommendViewModel.loading,
                            onRefresh = {
                                scope.launch(Dispatchers.IO) {
                                    popularViewModel.resetPage()
                                    //避免刷新太快
                                    delay(300)
                                    recommendViewModel.loadMore {
                                        //clear data before set new data
                                        recommendViewModel.clearData()
                                    }
                                }
                            },
                            loadMore = {
                                scope.launch(Dispatchers.IO) {
                                    recommendViewModel.loadMore()
                                }
                            }
                        )
                    }

                    1 -> {
                        PopularPage(
                            state = gridState,
                            videos = popularViewModel.popularVideoList,
                            onClickVideo = { aid ->
                                VideoPlayerActivity.actionStart(context = context, aid = aid)
                            },
                            refreshing = popularViewModel.loading,
                            onRefresh = {
                                scope.launch(Dispatchers.IO) {
                                    popularViewModel.resetPage()
                                    //避免刷新太快
                                    delay(300)
                                    popularViewModel.loadMore {
                                        //clear data before set new data
                                        popularViewModel.clearData()
                                    }
                                }
                            },
                            loadMore = {
                                scope.launch(Dispatchers.IO) {
                                    popularViewModel.loadMore()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}