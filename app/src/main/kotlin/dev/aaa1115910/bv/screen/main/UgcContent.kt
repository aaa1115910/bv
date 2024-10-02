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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import dev.aaa1115910.bv.component.DevelopingTipContent
import dev.aaa1115910.bv.component.TopNav
import dev.aaa1115910.bv.component.UgcTopNavItem
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.requestFocus
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun UgcContent(
    modifier: Modifier = Modifier,
    navFocusRequester: FocusRequester,
) {
    val scope = rememberCoroutineScope()
    val logger = KotlinLogging.logger("UgcContent")

    val dougaState = rememberLazyListState()
    val gameState = rememberLazyListState()
    val kichikuState = rememberLazyListState()
    val musicState = rememberLazyListState()
    val danceState = rememberLazyListState()
    val cinephileState = rememberLazyListState()
    val entState = rememberLazyListState()
    val knowledgeState = rememberLazyListState()
    val techState = rememberLazyListState()
    val informationState = rememberLazyListState()
    val foodState = rememberLazyListState()
    val lifeState = rememberLazyListState()
    val carState = rememberLazyListState()
    val fashionState = rememberLazyListState()
    val sportsState = rememberLazyListState()
    val animalState = rememberLazyListState()

    var selectedTab by remember { mutableStateOf(UgcTopNavItem.Douga) }
    var focusOnContent by remember { mutableStateOf(false) }

    //启动时刷新数据
    LaunchedEffect(Unit) {

    }

    BackHandler(focusOnContent) {
        logger.fInfo { "onFocusBackToNav" }
        navFocusRequester.requestFocus(scope)
        // scroll to top
        scope.launch(Dispatchers.Main) {
            when (selectedTab) {
                UgcTopNavItem.Douga -> dougaState.animateScrollToItem(0)
                UgcTopNavItem.Game -> gameState.animateScrollToItem(0)
                UgcTopNavItem.Kichiku -> kichikuState.animateScrollToItem(0)
                UgcTopNavItem.Music -> musicState.animateScrollToItem(0)
                UgcTopNavItem.Dance -> danceState.animateScrollToItem(0)
                UgcTopNavItem.Cinephile -> cinephileState.animateScrollToItem(0)
                UgcTopNavItem.Ent -> entState.animateScrollToItem(0)
                UgcTopNavItem.Knowledge -> knowledgeState.animateScrollToItem(0)
                UgcTopNavItem.Tech -> techState.animateScrollToItem(0)
                UgcTopNavItem.Information -> informationState.animateScrollToItem(0)
                UgcTopNavItem.Food -> foodState.animateScrollToItem(0)
                UgcTopNavItem.Life -> lifeState.animateScrollToItem(0)
                UgcTopNavItem.Car -> carState.animateScrollToItem(0)
                UgcTopNavItem.Fashion -> fashionState.animateScrollToItem(0)
                UgcTopNavItem.Sports -> sportsState.animateScrollToItem(0)
                UgcTopNavItem.Animal -> animalState.animateScrollToItem(0)
            }
        }
    }

    Scaffold(
        modifier = Modifier,
        topBar = {
            TopNav(
                modifier = Modifier
                    .focusRequester(navFocusRequester),
                items = UgcTopNavItem.entries,
                isLargePadding = !focusOnContent,
                onSelectedChanged = { nav ->
                    selectedTab = nav as UgcTopNavItem
                },
                onClick = { nav ->
                    when (nav) {
                        UgcTopNavItem.Douga -> {}
                        UgcTopNavItem.Game -> {}
                        UgcTopNavItem.Kichiku -> {}
                        UgcTopNavItem.Music -> {}
                        UgcTopNavItem.Dance -> {}
                        UgcTopNavItem.Cinephile -> {}
                        UgcTopNavItem.Ent -> {}
                        UgcTopNavItem.Knowledge -> {}
                        UgcTopNavItem.Tech -> {}
                        UgcTopNavItem.Information -> {}
                        UgcTopNavItem.Food -> {}
                        UgcTopNavItem.Life -> {}
                        UgcTopNavItem.Car -> {}
                        UgcTopNavItem.Fashion -> {}
                        UgcTopNavItem.Sports -> {}
                        UgcTopNavItem.Animal -> {}
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
                label = "ugc animated content",
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
                    UgcTopNavItem.Douga -> DevelopingTipContent()
                    UgcTopNavItem.Game -> DevelopingTipContent()
                    UgcTopNavItem.Kichiku -> DevelopingTipContent()
                    UgcTopNavItem.Music -> DevelopingTipContent()
                    UgcTopNavItem.Dance -> DevelopingTipContent()
                    UgcTopNavItem.Cinephile -> DevelopingTipContent()
                    UgcTopNavItem.Ent -> DevelopingTipContent()
                    UgcTopNavItem.Knowledge -> DevelopingTipContent()
                    UgcTopNavItem.Tech -> DevelopingTipContent()
                    UgcTopNavItem.Information -> DevelopingTipContent()
                    UgcTopNavItem.Food -> DevelopingTipContent()
                    UgcTopNavItem.Life -> DevelopingTipContent()
                    UgcTopNavItem.Car -> DevelopingTipContent()
                    UgcTopNavItem.Fashion -> DevelopingTipContent()
                    UgcTopNavItem.Sports -> DevelopingTipContent()
                    UgcTopNavItem.Animal -> DevelopingTipContent()
                }
            }
        }
    }
}