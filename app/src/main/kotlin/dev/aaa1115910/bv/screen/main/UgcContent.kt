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
import dev.aaa1115910.biliapi.entity.ugc.UgcType
import dev.aaa1115910.bv.component.TopNav
import dev.aaa1115910.bv.component.UgcTopNavItem
import dev.aaa1115910.bv.screen.main.ugc.AnimalContent
import dev.aaa1115910.bv.screen.main.ugc.CarContent
import dev.aaa1115910.bv.screen.main.ugc.CinephileContent
import dev.aaa1115910.bv.screen.main.ugc.DanceContent
import dev.aaa1115910.bv.screen.main.ugc.DougaContent
import dev.aaa1115910.bv.screen.main.ugc.EntContent
import dev.aaa1115910.bv.screen.main.ugc.FashionContent
import dev.aaa1115910.bv.screen.main.ugc.FoodContent
import dev.aaa1115910.bv.screen.main.ugc.GameContent
import dev.aaa1115910.bv.screen.main.ugc.InformationContent
import dev.aaa1115910.bv.screen.main.ugc.KichikuContent
import dev.aaa1115910.bv.screen.main.ugc.KnowledgeContent
import dev.aaa1115910.bv.screen.main.ugc.LifeContent
import dev.aaa1115910.bv.screen.main.ugc.MusicContent
import dev.aaa1115910.bv.screen.main.ugc.SportsContent
import dev.aaa1115910.bv.screen.main.ugc.TechContent
import dev.aaa1115910.bv.screen.main.ugc.UgcScaffoldState
import dev.aaa1115910.bv.screen.main.ugc.rememberUgcScaffoldState
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.requestFocus
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun UgcContent(
    modifier: Modifier = Modifier,
    navFocusRequester: FocusRequester,
    dougaState: UgcScaffoldState = rememberUgcScaffoldState(ugcType = UgcType.Douga),
    gameState: UgcScaffoldState = rememberUgcScaffoldState(ugcType = UgcType.Game),
    kichikuState: UgcScaffoldState = rememberUgcScaffoldState(ugcType = UgcType.Kichiku),
    musicState: UgcScaffoldState = rememberUgcScaffoldState(ugcType = UgcType.Music),
    danceState: UgcScaffoldState = rememberUgcScaffoldState(ugcType = UgcType.Dance),
    cinephileState: UgcScaffoldState = rememberUgcScaffoldState(ugcType = UgcType.Cinephile),
    entState: UgcScaffoldState = rememberUgcScaffoldState(ugcType = UgcType.Ent),
    knowledgeState: UgcScaffoldState = rememberUgcScaffoldState(ugcType = UgcType.Knowledge),
    techState: UgcScaffoldState = rememberUgcScaffoldState(ugcType = UgcType.Tech),
    informationState: UgcScaffoldState = rememberUgcScaffoldState(ugcType = UgcType.Information),
    foodState: UgcScaffoldState = rememberUgcScaffoldState(ugcType = UgcType.Food),
    lifeState: UgcScaffoldState = rememberUgcScaffoldState(ugcType = UgcType.Life),
    carState: UgcScaffoldState = rememberUgcScaffoldState(ugcType = UgcType.Car),
    fashionState: UgcScaffoldState = rememberUgcScaffoldState(ugcType = UgcType.Fashion),
    sportsState: UgcScaffoldState = rememberUgcScaffoldState(ugcType = UgcType.Sports),
    animalState: UgcScaffoldState = rememberUgcScaffoldState(ugcType = UgcType.Animal)
) {
    val scope = rememberCoroutineScope()
    val logger = KotlinLogging.logger("UgcContent")

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
                UgcTopNavItem.Douga -> dougaState.lazyListState.animateScrollToItem(0)
                UgcTopNavItem.Game -> gameState.lazyListState.animateScrollToItem(0)
                UgcTopNavItem.Kichiku -> kichikuState.lazyListState.animateScrollToItem(0)
                UgcTopNavItem.Music -> musicState.lazyListState.animateScrollToItem(0)
                UgcTopNavItem.Dance -> danceState.lazyListState.animateScrollToItem(0)
                UgcTopNavItem.Cinephile -> cinephileState.lazyListState.animateScrollToItem(0)
                UgcTopNavItem.Ent -> entState.lazyListState.animateScrollToItem(0)
                UgcTopNavItem.Knowledge -> knowledgeState.lazyListState.animateScrollToItem(0)
                UgcTopNavItem.Tech -> techState.lazyListState.animateScrollToItem(0)
                UgcTopNavItem.Information -> informationState.lazyListState.animateScrollToItem(0)
                UgcTopNavItem.Food -> foodState.lazyListState.animateScrollToItem(0)
                UgcTopNavItem.Life -> lifeState.lazyListState.animateScrollToItem(0)
                UgcTopNavItem.Car -> carState.lazyListState.animateScrollToItem(0)
                UgcTopNavItem.Fashion -> fashionState.lazyListState.animateScrollToItem(0)
                UgcTopNavItem.Sports -> sportsState.lazyListState.animateScrollToItem(0)
                UgcTopNavItem.Animal -> animalState.lazyListState.animateScrollToItem(0)
            }
        }
    }

    Scaffold(
        modifier = modifier,
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
                        UgcTopNavItem.Douga -> dougaState.reloadAll()
                        UgcTopNavItem.Game -> gameState.reloadAll()
                        UgcTopNavItem.Kichiku -> kichikuState.reloadAll()
                        UgcTopNavItem.Music -> musicState.reloadAll()
                        UgcTopNavItem.Dance -> danceState.reloadAll()
                        UgcTopNavItem.Cinephile -> cinephileState.reloadAll()
                        UgcTopNavItem.Ent -> entState.reloadAll()
                        UgcTopNavItem.Knowledge -> knowledgeState.reloadAll()
                        UgcTopNavItem.Tech -> techState.reloadAll()
                        UgcTopNavItem.Information -> informationState.reloadAll()
                        UgcTopNavItem.Food -> foodState.reloadAll()
                        UgcTopNavItem.Life -> lifeState.reloadAll()
                        UgcTopNavItem.Car -> carState.reloadAll()
                        UgcTopNavItem.Fashion -> fashionState.reloadAll()
                        UgcTopNavItem.Sports -> sportsState.reloadAll()
                        UgcTopNavItem.Animal -> animalState.reloadAll()
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
                    UgcTopNavItem.Douga -> DougaContent(state = dougaState)
                    UgcTopNavItem.Game -> GameContent(state = gameState)
                    UgcTopNavItem.Kichiku -> KichikuContent(state = kichikuState)
                    UgcTopNavItem.Music -> MusicContent(state = musicState)
                    UgcTopNavItem.Dance -> DanceContent(state = danceState)
                    UgcTopNavItem.Cinephile -> CinephileContent(state = cinephileState)
                    UgcTopNavItem.Ent -> EntContent(state = entState)
                    UgcTopNavItem.Knowledge -> KnowledgeContent(state = knowledgeState)
                    UgcTopNavItem.Tech -> TechContent(state = techState)
                    UgcTopNavItem.Information -> InformationContent(state = informationState)
                    UgcTopNavItem.Food -> FoodContent(state = foodState)
                    UgcTopNavItem.Life -> LifeContent(state = lifeState)
                    UgcTopNavItem.Car -> CarContent(state = carState)
                    UgcTopNavItem.Fashion -> FashionContent(state = fashionState)
                    UgcTopNavItem.Sports -> SportsContent(state = sportsState)
                    UgcTopNavItem.Animal -> AnimalContent(state = animalState)
                }
            }
        }
    }
}