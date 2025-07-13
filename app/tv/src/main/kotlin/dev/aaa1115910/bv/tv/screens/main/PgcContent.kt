package dev.aaa1115910.bv.tv.screens.main

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import dev.aaa1115910.bv.tv.component.PgcTopNavItem
import dev.aaa1115910.bv.tv.component.TopNav
import dev.aaa1115910.bv.tv.screens.main.pgc.AnimeContent
import dev.aaa1115910.bv.tv.screens.main.pgc.DocumentaryContent
import dev.aaa1115910.bv.tv.screens.main.pgc.GuoChuangContent
import dev.aaa1115910.bv.tv.screens.main.pgc.MovieContent
import dev.aaa1115910.bv.tv.screens.main.pgc.TvContent
import dev.aaa1115910.bv.tv.screens.main.pgc.VarietyContent
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.requestFocus
import dev.aaa1115910.bv.util.rememberDebouncer
import dev.aaa1115910.bv.viewmodel.pgc.PgcAnimeViewModel
import dev.aaa1115910.bv.viewmodel.pgc.PgcDocumentaryViewModel
import dev.aaa1115910.bv.viewmodel.pgc.PgcGuoChuangViewModel
import dev.aaa1115910.bv.viewmodel.pgc.PgcMovieViewModel
import dev.aaa1115910.bv.viewmodel.pgc.PgcTvViewModel
import dev.aaa1115910.bv.viewmodel.pgc.PgcVarietyViewModel
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun PgcContent(
    modifier: Modifier = Modifier,
    navFocusRequester: FocusRequester,
    pgcAnimeViewModel: PgcAnimeViewModel = koinViewModel(),
    pgcGuoChuangViewModel: PgcGuoChuangViewModel = koinViewModel(),
    pgcMovieViewModel: PgcMovieViewModel = koinViewModel(),
    pgcDocumentaryViewModel: PgcDocumentaryViewModel = koinViewModel(),
    pgcTvViewModel: PgcTvViewModel = koinViewModel(),
    pgcVarietyViewModel: PgcVarietyViewModel = koinViewModel()
) {
    val scope = rememberCoroutineScope()
    val logger = KotlinLogging.logger("PgcContent")

    val animeState = rememberLazyListState()
    val guoChuangState = rememberLazyListState()
    val movieState = rememberLazyListState()
    val documentaryState = rememberLazyListState()
    val tvState = rememberLazyListState()
    val varietyState = rememberLazyListState()
    
    var focusOnContent by remember { mutableStateOf(false) }
    var topNavHasFocus by remember { mutableStateOf(false) }

    // 使用remember的key参数确保只有在DrawerItem.PGC的tab状态变化时才重新计算
    var selectedTab by remember {
        mutableStateOf(
            currentSelectedTabs[DrawerItem.PGC]
                ?.let { PgcTopNavItem.entries.getOrNull(it) }
                ?: PgcTopNavItem.Anime
        )
    }

    // 当选中标签变化时，保存到全局状态
    LaunchedEffect(selectedTab) {
        currentSelectedTabs[DrawerItem.PGC] = selectedTab.ordinal
    }

    val currentListOnTop by remember {
        derivedStateOf {
            with(
                when (selectedTab) {
                    PgcTopNavItem.Anime -> animeState
                    PgcTopNavItem.GuoChuang -> guoChuangState
                    PgcTopNavItem.Movie -> movieState
                    PgcTopNavItem.Documentary -> documentaryState
                    PgcTopNavItem.Tv -> tvState
                    PgcTopNavItem.Variety -> varietyState
                }
            ) {
                firstVisibleItemIndex == 0 && firstVisibleItemScrollOffset == 0
            }
        }
    }

    //启动时加载当前选中tab的数据
    LaunchedEffect(selectedTab) {
        when (selectedTab) {
            PgcTopNavItem.Anime -> {
                if (pgcAnimeViewModel.feedItems.isEmpty()) {
                    logger.fInfo { "加载动画数据" }
                    pgcAnimeViewModel.init()
                }
            }
            PgcTopNavItem.GuoChuang -> {
                if (pgcGuoChuangViewModel.feedItems.isEmpty()) {
                    logger.fInfo { "加载国创数据" }
                    pgcGuoChuangViewModel.init()
                }
            }
            PgcTopNavItem.Movie -> {
                if (pgcMovieViewModel.feedItems.isEmpty()) {
                    logger.fInfo { "加载电影数据" }
                    pgcMovieViewModel.init()
                }
            }
            PgcTopNavItem.Documentary -> {
                if (pgcDocumentaryViewModel.feedItems.isEmpty()) {
                    logger.fInfo { "加载纪录片数据" }
                    pgcDocumentaryViewModel.init()
                }
            }
            PgcTopNavItem.Tv -> {
                if (pgcTvViewModel.feedItems.isEmpty()) {
                    logger.fInfo { "加载电视剧数据" }
                    pgcTvViewModel.init()
                }
            }
            PgcTopNavItem.Variety -> {
                if (pgcVarietyViewModel.feedItems.isEmpty()) {
                    logger.fInfo { "加载综艺数据" }
                    pgcVarietyViewModel.init()
                }
            }
        }
    }

    BackHandler(focusOnContent || topNavHasFocus) {
        logger.fInfo { "onFocusBackToNav" }
        // 如果顶部导航有焦点，则返回到左边栏的PGC位置
        if (topNavHasFocus) {
            drawerItemFocusRequesters[DrawerItem.PGC]?.requestFocus()
            return@BackHandler
        }
        navFocusRequester.requestFocus(scope)
        // // scroll to top
        // scope.launch(Dispatchers.Main) {
        //     when (selectedTab) {
        //         PgcTopNavItem.Anime -> animeState.animateScrollToItem(0)
        //         PgcTopNavItem.GuoChuang -> guoChuangState.animateScrollToItem(0)
        //         PgcTopNavItem.Movie -> movieState.animateScrollToItem(0)
        //         PgcTopNavItem.Documentary -> documentaryState.animateScrollToItem(0)
        //         PgcTopNavItem.Tv -> tvState.animateScrollToItem(0)
        //         PgcTopNavItem.Variety -> varietyState.animateScrollToItem(0)
        //     }
        // }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopNav(
                modifier = Modifier
                    .focusRequester(navFocusRequester)
                    .padding(end = 80.dp)
                    .onFocusChanged { topNavHasFocus = it.hasFocus },
                items = PgcTopNavItem.entries,
                isLargePadding = !focusOnContent && currentListOnTop,
                initialSelectedItem = selectedTab,
                onSelectedChanged = { nav ->
                    selectedTab = nav as PgcTopNavItem
                },
                onClick = { nav ->
                    when (nav) {
                        PgcTopNavItem.Anime -> pgcAnimeViewModel.reloadAll()
                        PgcTopNavItem.GuoChuang -> pgcGuoChuangViewModel.reloadAll()
                        PgcTopNavItem.Movie -> pgcMovieViewModel.reloadAll()
                        PgcTopNavItem.Documentary -> pgcDocumentaryViewModel.reloadAll()
                        PgcTopNavItem.Tv -> pgcTvViewModel.reloadAll()
                        PgcTopNavItem.Variety -> pgcVarietyViewModel.reloadAll()
                    }
                },
                onLeftKeyEvent = {
                    // 顶部栏最左侧按左键时，跳转到左侧导航栏
                    drawerItemFocusRequesters[DrawerItem.PGC]?.requestFocus()
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .onFocusChanged { focusOnContent = it.hasFocus }
        ) {
            AnimatedContent(
                targetState = selectedTab,
                label = "pgc animated content",
                transitionSpec = {
                    val coefficient = 10
                    if (targetState.ordinal < initialState.ordinal) {
                        fadeIn() + slideInHorizontally { -it / coefficient } togetherWith
                                fadeOut() + slideOutHorizontally { it / coefficient }
                    } else {
                        fadeIn() + slideInHorizontally { it / coefficient } togetherWith
                                fadeOut() + slideOutHorizontally { -it / coefficient }
                    }
                }
            ) { screen ->
                when (screen) {
                    PgcTopNavItem.Anime -> AnimeContent(lazyListState = animeState)
                    PgcTopNavItem.GuoChuang -> GuoChuangContent(lazyListState = guoChuangState)
                    PgcTopNavItem.Movie -> MovieContent(lazyListState = movieState)
                    PgcTopNavItem.Documentary -> DocumentaryContent(lazyListState = documentaryState)
                    PgcTopNavItem.Tv -> TvContent(lazyListState = tvState)
                    PgcTopNavItem.Variety -> VarietyContent(lazyListState = varietyState)
                }
            }
        }
    }
}