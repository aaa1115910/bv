package dev.aaa1115910.bv.screen

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.tv.foundation.lazy.grid.rememberTvLazyGridState
import dev.aaa1115910.bv.component.TopNav
import dev.aaa1115910.bv.component.TopNavItem
import dev.aaa1115910.bv.screen.home.AnimeScreen
import dev.aaa1115910.bv.screen.home.DynamicsScreen
import dev.aaa1115910.bv.screen.home.PartitionScreen
import dev.aaa1115910.bv.screen.home.PopularScreen
import dev.aaa1115910.bv.viewmodel.UserViewModel
import dev.aaa1115910.bv.viewmodel.home.DynamicViewModel
import dev.aaa1115910.bv.viewmodel.home.PopularViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    popularViewModel: PopularViewModel = koinViewModel(),
    dynamicViewModel: DynamicViewModel = koinViewModel(),
    userViewModel: UserViewModel = koinViewModel()
) {
    val scope = rememberCoroutineScope()
    val logger = KotlinLogging.logger { }

    val popularState = rememberTvLazyGridState()
    val dynamicState = rememberTvLazyGridState()

    var selectedTab by remember { mutableStateOf(TopNavItem.Popular) }

    //启动时刷新数据
    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.Default) {
            popularViewModel.loadMore()
        }
        scope.launch(Dispatchers.Default) {
            dynamicViewModel.loadMore()
        }
        scope.launch(Dispatchers.Default) {
            userViewModel.updateUserInfo()
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

    Scaffold(
        modifier = modifier,
        topBar = {
            TopNav(
                isLogin = userViewModel.isLogin,
                username = userViewModel.username,
                face = userViewModel.face,
                onSelectedChange = { nav ->
                    selectedTab = nav
                    when (nav) {
                        TopNavItem.Popular -> {
                            //scope.launch(Dispatchers.Default) { popularState.scrollToItem(0, 0) }
                        }

                        TopNavItem.Partition -> {

                        }

                        TopNavItem.Anime -> {

                        }

                        TopNavItem.Dynamics -> {
                            //scope.launch(Dispatchers.Default) { dynamicState.scrollToItem(0, 0) }
                            if (!dynamicViewModel.loading && dynamicViewModel.isLogin && dynamicViewModel.dynamicList.isEmpty()) {
                                scope.launch(Dispatchers.Default) { dynamicViewModel.loadMore() }
                            }
                        }

                        TopNavItem.Search -> {

                        }
                    }
                },
                onClick = { nav ->
                    when (nav) {
                        TopNavItem.Popular -> {
                            //scope.launch(Dispatchers.Default) { popularState.scrollToItem(0, 0) }
                            logger.info { "clear popular data" }
                            popularViewModel.clearData()
                            logger.info { "reload popular data" }
                            scope.launch(Dispatchers.Default) { popularViewModel.loadMore() }
                        }

                        TopNavItem.Partition -> {

                        }

                        TopNavItem.Anime -> {

                        }

                        TopNavItem.Dynamics -> {
                            //scope.launch(Dispatchers.Default) { dynamicState.scrollToItem(0, 0) }
                            dynamicViewModel.clearData()
                            scope.launch(Dispatchers.Default) { dynamicViewModel.loadMore() }
                        }

                        TopNavItem.Search -> {

                        }
                    }
                }
            )
        },
        containerColor = Color.Black
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ) {
            Crossfade(targetState = selectedTab) { screen ->
                when (screen) {
                    TopNavItem.Popular -> PopularScreen(
                        tvLazyGridState = popularState
                    )

                    TopNavItem.Partition -> PartitionScreen()
                    TopNavItem.Anime -> AnimeScreen()
                    TopNavItem.Dynamics -> DynamicsScreen(
                        tvLazyGridState = dynamicState
                    )

                    else -> PopularScreen(
                        tvLazyGridState = popularState
                    )
                }
            }
        }
    }
}
