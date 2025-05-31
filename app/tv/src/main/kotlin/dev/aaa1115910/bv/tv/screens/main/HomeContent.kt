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
import dev.aaa1115910.bv.tv.component.HomeTopNavItem
import dev.aaa1115910.bv.tv.component.TopNav
import dev.aaa1115910.bv.tv.screens.main.home.DynamicsScreen
import dev.aaa1115910.bv.tv.screens.main.home.PopularScreen
import dev.aaa1115910.bv.tv.screens.main.home.RecommendScreen
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.requestFocus
import dev.aaa1115910.bv.util.rememberDebouncer
import dev.aaa1115910.bv.viewmodel.UserViewModel
import dev.aaa1115910.bv.viewmodel.home.DynamicViewModel
import dev.aaa1115910.bv.viewmodel.home.PopularViewModel
import dev.aaa1115910.bv.viewmodel.home.RecommendViewModel
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
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
    userViewModel: UserViewModel = koinViewModel()
) {
    val scope = rememberCoroutineScope()
    val logger = KotlinLogging.logger("HomeContent")

    val recommendState = rememberLazyListState()
    val popularState = rememberLazyListState()
    val dynamicState = rememberLazyListState()
    
    var focusOnContent by remember { mutableStateOf(false) }
    var topNavHasFocus by remember { mutableStateOf(false) }

    // 用于控制Tab选择后的延迟加载的防抖器（自动管理生命周期）
    val tabSelectionDebouncer = rememberDebouncer<HomeTopNavItem>(280L)

    // 从全局状态获取上次选择的标签位置，如果没有则默认为Recommend
    // 将这个值提到可组合函数的顶部，避免在重组时重新计算
    val initialSelectedTabIndex = currentSelectedTabs[DrawerItem.Home]
    var selectedTab by remember(initialSelectedTabIndex) {
        mutableStateOf(
            initialSelectedTabIndex
                ?.let { HomeTopNavItem.entries.getOrNull(it) }
                ?: HomeTopNavItem.Recommend
        )
    }

    // 当选中标签变化时，保存到全局状态
    LaunchedEffect(selectedTab) {
        currentSelectedTabs[DrawerItem.Home] = selectedTab.ordinal
    }
    val currentListOnTop by remember {
        derivedStateOf {
            with(
                when (selectedTab) {
                    HomeTopNavItem.Recommend -> recommendState
                    HomeTopNavItem.Popular -> popularState
                    HomeTopNavItem.Dynamics -> dynamicState
                }
            ) {
                firstVisibleItemIndex == 0 && firstVisibleItemScrollOffset == 0
            }
        }
    }    // 启动时触发一次屏幕切换，确保tab和内容同步
    LaunchedEffect(Unit) {
        // 强制触发一次当前选中tab的内容切换，通过重新设置selectedTab来实现
        val currentTab = selectedTab
        logger.fInfo { "初始化切换到 $currentTab 屏幕" }
        // 短暂延迟后重新设置tab，触发AnimatedContent切换
        scope.launch {
            delay(50)
            selectedTab = currentTab
        }
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
        // // scroll to top
        // scope.launch(Dispatchers.Main) {
        //     when (selectedTab) {
        //         HomeTopNavItem.Recommend -> recommendState.animateScrollToItem(0)
        //         HomeTopNavItem.Popular -> popularState.animateScrollToItem(0)
        //         HomeTopNavItem.Dynamics -> dynamicState.animateScrollToItem(0)
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
                items = HomeTopNavItem.entries,
                isLargePadding = !focusOnContent && currentListOnTop,
                initialSelectedItem = selectedTab,
                onSelectedChanged = { nav ->
                    tabSelectionDebouncer.debounce(scope, nav as HomeTopNavItem) { selectedNavItem ->
                        selectedTab = selectedNavItem
                        // when (selectedNavItem) {
                        //     HomeTopNavItem.Recommend -> {}
                        //     HomeTopNavItem.Popular -> {}
                        //     HomeTopNavItem.Dynamics -> {
                        //         // if (!dynamicViewModel.loadingVideo && dynamicViewModel.isLogin && dynamicViewModel.dynamicVideoList.isEmpty()) {
                        //         //     scope.launch(Dispatchers.IO) { dynamicViewModel.loadMoreVideo() }
                        //         // }
                        //     }
                        // }
                    }
                },
                onClick = { nav ->
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
                    fadeIn() togetherWith fadeOut()
                }
            ) { screen ->
                when (screen) {
                    HomeTopNavItem.Recommend -> RecommendScreen(lazyListState = recommendState)
                    HomeTopNavItem.Popular -> PopularScreen(lazyListState = popularState)
                    HomeTopNavItem.Dynamics -> DynamicsScreen(lazyListState = dynamicState)
                }
            }
        }
    }
}
