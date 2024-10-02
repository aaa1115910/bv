package dev.aaa1115910.bv.screen.main

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
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
import dev.aaa1115910.bv.component.DevelopingTipContent
import dev.aaa1115910.bv.component.HomeTopNavItem
import dev.aaa1115910.bv.component.PgcTopNavItem
import dev.aaa1115910.bv.component.TopNav
import dev.aaa1115910.bv.screen.main.pgc.AnimeContent
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.requestFocus
import dev.aaa1115910.bv.viewmodel.home.AnimeViewModel
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun PgcContent(
    modifier: Modifier = Modifier,
    navFocusRequester: FocusRequester,
    animeViewModel: AnimeViewModel = koinViewModel()
) {
    val scope = rememberCoroutineScope()
    val logger = KotlinLogging.logger("PgcContent")

    val animeState = rememberLazyListState()
    val guoChuangState = rememberLazyListState()
    val movieState = rememberLazyListState()
    val documentaryState = rememberLazyListState()
    val tvState = rememberLazyListState()
    val varietyState = rememberLazyListState()

    var selectedTab by remember { mutableStateOf(PgcTopNavItem.Anime) }
    var focusOnContent by remember { mutableStateOf(false) }
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

    //启动时刷新数据
    LaunchedEffect(Unit) {

    }

    BackHandler(focusOnContent) {
        logger.fInfo { "onFocusBackToNav" }
        navFocusRequester.requestFocus(scope)
        // scroll to top
        scope.launch(Dispatchers.Main) {
            when (selectedTab) {
                PgcTopNavItem.Anime -> animeState.animateScrollToItem(0)
                PgcTopNavItem.GuoChuang -> guoChuangState.animateScrollToItem(0)
                PgcTopNavItem.Movie -> movieState.animateScrollToItem(0)
                PgcTopNavItem.Documentary -> documentaryState.animateScrollToItem(0)
                PgcTopNavItem.Tv -> tvState.animateScrollToItem(0)
                PgcTopNavItem.Variety -> varietyState.animateScrollToItem(0)
            }
        }
    }

    Scaffold(
        modifier = Modifier,
        topBar = {
            TopNav(
                modifier = Modifier
                    .focusRequester(navFocusRequester)
                    .padding(end = 80.dp),
                items = PgcTopNavItem.entries,
                isLargePadding = !focusOnContent && currentListOnTop,
                onSelectedChanged = { nav ->
                    selectedTab = nav as PgcTopNavItem
                },
                onClick = { nav ->
                    when (nav) {
                        PgcTopNavItem.Anime -> {
                            logger.fInfo { "reload anime data" }
                            animeViewModel.reloadAll()
                        }

                        PgcTopNavItem.GuoChuang -> {}
                        PgcTopNavItem.Movie -> {}
                        PgcTopNavItem.Documentary -> {}
                        PgcTopNavItem.Tv -> {}
                        PgcTopNavItem.Variety -> {}
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
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
                    PgcTopNavItem.GuoChuang -> DevelopingTipContent()
                    PgcTopNavItem.Movie -> DevelopingTipContent()
                    PgcTopNavItem.Documentary -> DevelopingTipContent()
                    PgcTopNavItem.Tv -> DevelopingTipContent()
                    PgcTopNavItem.Variety -> DevelopingTipContent()
                }
            }
        }
    }
}