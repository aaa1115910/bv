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
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Text
import dev.aaa1115910.bv.tv.component.HomeTopNavItem
import dev.aaa1115910.bv.tv.component.TopNav
import dev.aaa1115910.bv.tv.screens.main.home.DynamicsScreen
import dev.aaa1115910.bv.tv.screens.main.home.PopularScreen
import dev.aaa1115910.bv.tv.screens.main.home.RecommendScreen
import dev.aaa1115910.bv.tv.screens.user.FavoriteScreen
import dev.aaa1115910.bv.tv.screens.user.FollowingSeasonScreen
import dev.aaa1115910.bv.tv.screens.user.HistoryScreen
import dev.aaa1115910.bv.tv.screens.user.ToViewScreen
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.requestFocus
import dev.aaa1115910.bv.viewmodel.UserViewModel
import dev.aaa1115910.bv.viewmodel.home.DynamicViewModel
import dev.aaa1115910.bv.viewmodel.home.PopularViewModel
import dev.aaa1115910.bv.viewmodel.home.RecommendViewModel
import dev.aaa1115910.bv.viewmodel.user.FavoriteViewModel
import dev.aaa1115910.bv.viewmodel.user.FollowingSeasonViewModel
import dev.aaa1115910.bv.viewmodel.user.HistoryViewModel
import dev.aaa1115910.bv.viewmodel.user.ToViewViewModel
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    navFocusRequester: FocusRequester,
    recommendViewModel: RecommendViewModel = koinViewModel(),
    popularViewModel: PopularViewModel = koinViewModel(),
    dynamicViewModel: DynamicViewModel = koinViewModel(),
    favouriteViewModel: FavoriteViewModel = koinViewModel(),
    followingSeasonViewModel: FollowingSeasonViewModel = koinViewModel(),
    historyViewModel: HistoryViewModel = koinViewModel(),
    toViewViewModel: ToViewViewModel = koinViewModel(),
    userViewModel: UserViewModel = koinViewModel()
) {
    val scope = rememberCoroutineScope()
    val logger = KotlinLogging.logger("HomeContent")

    val recommendState = rememberLazyGridState()
    val popularState = rememberLazyGridState()
    val dynamicState = rememberLazyGridState()
    val favoriteState = rememberLazyGridState()
    val followingSeasonState = rememberLazyGridState()
    val historyState = rememberLazyGridState()
    val toViewState = rememberLazyGridState()
    
    var focusOnContent by remember { mutableStateOf(false) }
    var topNavHasFocus by remember { mutableStateOf(false) }
    
    // 用于管理延迟加载的Job
    var loadJob by remember { mutableStateOf<Job?>(null) }

    // 从全局状态获取上次选择的标签位置，如果没有则默认为Recommend
    var selectedTab by remember {
        mutableStateOf(
            currentSelectedTabs[DrawerItem.Home]
                ?.let { HomeTopNavItem.entries.getOrNull(it) }
                ?: HomeTopNavItem.entries.getOrElse(Prefs.defaultHomeTab) { HomeTopNavItem.Recommend }
        )
    }

    fun initData () {
        scope.launch {
            when (selectedTab) {
                HomeTopNavItem.Recommend -> {
                    if (recommendViewModel.recommendVideoList.isEmpty()) {
                        recommendViewModel.loadMore()
                    }
                }

                HomeTopNavItem.Popular -> {
                    if (popularViewModel.popularVideoList.isEmpty()) {
                        popularViewModel.loadMore()
                    }
                }

                HomeTopNavItem.Dynamics -> {
                    if (dynamicViewModel.dynamicVideoList.isEmpty()) {
                        dynamicViewModel.loadMoreVideo()
                    }
                }

                HomeTopNavItem.Favorite -> {
//                    if (favouriteViewModel.favorites.isEmpty() && userViewModel.isLogin) {
//                        favouriteViewModel.updateFoldersInfo()
//                    }
                }

                HomeTopNavItem.FollowingSeason -> {
//                    if (followingSeasonViewModel.followingSeasons.isEmpty() && userViewModel.isLogin) {
//                        followingSeasonViewModel.loadMore()
//                    }
                }

                HomeTopNavItem.History -> {
//                    if (historyViewModel.histories.isEmpty() && userViewModel.isLogin) {
//                        historyViewModel.update()
//                    }
                }

                HomeTopNavItem.ToView -> {
//                    if (toViewViewModel.histories.isEmpty() && userViewModel.isLogin) {
//                        toViewViewModel.update()
//                    }
                }
            }
        }
    }

    // 当选中标签变化时，保存到全局状态并处理延迟加载
    LaunchedEffect(selectedTab) {
        currentSelectedTabs[DrawerItem.Home] = selectedTab.ordinal
        
        // 取消之前的延迟加载
        loadJob?.cancel()
        
        // 开始新的延迟加载
        loadJob = scope.launch(Dispatchers.IO) {
            delay(300L)
            initData()
        }
    }
    val currentListOnTop by remember {
        derivedStateOf {
            with(
                when (selectedTab) {
                    HomeTopNavItem.Recommend -> recommendState
                    HomeTopNavItem.Popular -> popularState
                    HomeTopNavItem.Dynamics -> dynamicState
                    HomeTopNavItem.Favorite -> favoriteState
                    HomeTopNavItem.FollowingSeason -> followingSeasonState
                    HomeTopNavItem.History -> historyState
                    HomeTopNavItem.ToView -> toViewState
                }
            ) {
                firstVisibleItemIndex == 0 && firstVisibleItemScrollOffset == 0
            }
        }
    }

    LaunchedEffect(Unit) {
        initData()
    }

    //监听登录变化
    LaunchedEffect(userViewModel.isLogin) {
        if (userViewModel.isLogin) {
            //login
            userViewModel.updateUserInfo()
        } else {
            //logout
            userViewModel.clearUserInfo()
        }
    }

    BackHandler(focusOnContent || topNavHasFocus) {
        if (topNavHasFocus) {
            drawerItemFocusRequesters[DrawerItem.Home]?.requestFocus()
            return@BackHandler
        }
        navFocusRequester.requestFocus(scope)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopNav(
                modifier = Modifier
                    .focusRequester(navFocusRequester)
                    .padding(end = 80.dp)
                    .onFocusChanged { topNavHasFocus = it.hasFocus },
                items = HomeTopNavItem.entries,
                isLargePadding = !focusOnContent && currentListOnTop,
                initialSelectedItem = selectedTab,
                onSelectedChanged = { nav ->
                    loadJob?.cancel()
                    selectedTab = nav as HomeTopNavItem
                },
                onClick = { nav ->
                    loadJob?.cancel()
                    
                    when (nav) {
                        HomeTopNavItem.Recommend -> {
                            logger.fInfo { "clear recommend data" }
                            recommendViewModel.clearData()
                            logger.fInfo { "reload recommend data" }
                            scope.launch(Dispatchers.IO) { recommendViewModel.loadMore() }
                        }

                        HomeTopNavItem.Popular -> {
                            logger.fInfo { "clear popular data" }
                            popularViewModel.clearData()
                            logger.fInfo { "reload popular data" }
                            scope.launch(Dispatchers.IO) { popularViewModel.loadMore() }
                        }

                        HomeTopNavItem.Dynamics -> {
                            logger.fInfo { "clear dynamic data" }
                            dynamicViewModel.clearVideoData()
                            logger.fInfo { "reload dynamic data" }
                            scope.launch(Dispatchers.IO) { dynamicViewModel.loadMoreVideo() }
                        }

                        HomeTopNavItem.Favorite -> {
                            if (userViewModel.isLogin) {
                                favouriteViewModel.clearData()
                                favouriteViewModel.updateFoldersInfo()
                            }
                        }

                        HomeTopNavItem.FollowingSeason -> {
                            if (userViewModel.isLogin) {
                                followingSeasonViewModel.clearData()
                                followingSeasonViewModel.loadMore()
                            }
                        }

                        HomeTopNavItem.History -> {
                            if (userViewModel.isLogin) {
                                historyViewModel.clearData()
                                historyViewModel.update()
                            }
                        }

                        HomeTopNavItem.ToView -> {
                            if (userViewModel.isLogin) {
                                toViewViewModel.clearData()
                                toViewViewModel.update()
                            }
                        }
                    }
                },
                onLeftKeyEvent = {
                    // 顶部栏最左侧按左键时，跳转到左侧导航栏
                    drawerItemFocusRequesters[DrawerItem.Home]?.requestFocus()
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
                label = "home animated content",
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
                    HomeTopNavItem.Recommend -> RecommendScreen(lazyGridState = recommendState)
                    HomeTopNavItem.Popular -> PopularScreen(lazyGridState = popularState)
                    HomeTopNavItem.Dynamics -> {
                        if (userViewModel.isLogin) {
                            DynamicsScreen(lazyGridState = dynamicState)
                        } else {
                            LoginRequiredScreen()
                        }
                    }
                    HomeTopNavItem.Favorite -> {
                        if (userViewModel.isLogin) {
                            FavoriteScreen(showPageTitle = false)
                        } else {
                            LoginRequiredScreen()
                        }
                    }
                    HomeTopNavItem.FollowingSeason -> {
                        if (userViewModel.isLogin) {
                            FollowingSeasonScreen(showPageTitle = false)
                        } else {
                            LoginRequiredScreen()
                        }
                    }
                    HomeTopNavItem.History -> {
                        if (userViewModel.isLogin) {
                            HistoryScreen(showPageTitle = false)
                        } else {
                            LoginRequiredScreen()
                        }
                    }
                    HomeTopNavItem.ToView -> {
                        if (userViewModel.isLogin) {
                            ToViewScreen(showPageTitle = false)
                        } else {
                            LoginRequiredScreen()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LoginRequiredScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "请先登录")
    }
}
