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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import dev.aaa1115910.bv.tv.component.TopNav
import dev.aaa1115910.bv.tv.component.UgcTopNavItem
import dev.aaa1115910.bv.tv.screens.main.ugc.AiContent
import dev.aaa1115910.bv.tv.screens.main.ugc.AnimalContent
import dev.aaa1115910.bv.tv.screens.main.ugc.CarContent
import dev.aaa1115910.bv.tv.screens.main.ugc.CinephileContent
import dev.aaa1115910.bv.tv.screens.main.ugc.DanceContent
import dev.aaa1115910.bv.tv.screens.main.ugc.DougaContent
import dev.aaa1115910.bv.tv.screens.main.ugc.EmotionContent
import dev.aaa1115910.bv.tv.screens.main.ugc.EntContent
import dev.aaa1115910.bv.tv.screens.main.ugc.FashionContent
import dev.aaa1115910.bv.tv.screens.main.ugc.FoodContent
import dev.aaa1115910.bv.tv.screens.main.ugc.GameContent
import dev.aaa1115910.bv.tv.screens.main.ugc.GymContent
import dev.aaa1115910.bv.tv.screens.main.ugc.HandmakeContent
import dev.aaa1115910.bv.tv.screens.main.ugc.HealthContent
import dev.aaa1115910.bv.tv.screens.main.ugc.HomeContent
import dev.aaa1115910.bv.tv.screens.main.ugc.InformationContent
import dev.aaa1115910.bv.tv.screens.main.ugc.KichikuContent
import dev.aaa1115910.bv.tv.screens.main.ugc.KnowledgeContent
import dev.aaa1115910.bv.tv.screens.main.ugc.LifeExperienceContent
import dev.aaa1115910.bv.tv.screens.main.ugc.LifeJoyContent
import dev.aaa1115910.bv.tv.screens.main.ugc.MusicContent
import dev.aaa1115910.bv.tv.screens.main.ugc.MysticismContent
import dev.aaa1115910.bv.tv.screens.main.ugc.OutdoorsContent
import dev.aaa1115910.bv.tv.screens.main.ugc.PaintingContent
import dev.aaa1115910.bv.tv.screens.main.ugc.ParentingContent
import dev.aaa1115910.bv.tv.screens.main.ugc.RuralContent
import dev.aaa1115910.bv.tv.screens.main.ugc.ShortPlayContent
import dev.aaa1115910.bv.tv.screens.main.ugc.SportsContent
import dev.aaa1115910.bv.tv.screens.main.ugc.TechContent
import dev.aaa1115910.bv.tv.screens.main.ugc.TravelContent
import dev.aaa1115910.bv.tv.screens.main.ugc.VlogContent
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.requestFocus
import dev.aaa1115910.bv.util.rememberDebouncer
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun UgcContent(
    modifier: Modifier = Modifier,
    navFocusRequester: FocusRequester,
    ugcDougaViewModel: UgcDougaViewModel = koinViewModel(),
    ugcGameViewModel: UgcGameViewModel = koinViewModel(),
    ugcKichikuViewModel: UgcKichikuViewModel = koinViewModel(),
    ugcMusicViewModel: UgcMusicViewModel = koinViewModel(),
    ugcDanceViewModel: UgcDanceViewModel = koinViewModel(),
    ugcCinephileViewModel: UgcCinephileViewModel = koinViewModel(),
    ugcEntViewModel: UgcEntViewModel = koinViewModel(),
    ugcKnowledgeViewModel: UgcKnowledgeViewModel = koinViewModel(),
    ugcTechViewModel: UgcTechViewModel = koinViewModel(),
    ugcInformationViewModel: UgcInformationViewModel = koinViewModel(),
    ugcFoodViewModel: UgcFoodViewModel = koinViewModel(),
    ugcShortplayViewModel: UgcShortplayViewModel = koinViewModel(),
    ugcCarViewModel: UgcCarViewModel = koinViewModel(),
    ugcFashionViewModel: UgcFashionViewModel = koinViewModel(),
    ugcSportsViewModel: UgcSportsViewModel = koinViewModel(),
    ugcAnimalViewModel: UgcAnimalViewModel = koinViewModel(),
    ugcVlogViewModel: UgcVlogViewModel = koinViewModel(),
    ugcPaintingViewModel: UgcPaintingViewModel = koinViewModel(),
    ugcAiViewModel: UgcAiViewModel = koinViewModel(),
    ugcHomeViewModel: UgcHomeViewModel = koinViewModel(),
    ugcOutdoorsViewModel: UgcOutdoorsViewModel = koinViewModel(),
    ugcGymViewModel: UgcGymViewModel = koinViewModel(),
    ugcHandmakeViewModel: UgcHandmakeViewModel = koinViewModel(),
    ugcTravelViewModel: UgcTravelViewModel = koinViewModel(),
    ugcRuralViewModel: UgcRuralViewModel = koinViewModel(),
    ugcParentingViewModel: UgcParentingViewModel = koinViewModel(),
    ugcHealthViewModel: UgcHealthViewModel = koinViewModel(),
    ugcEmotionViewModel: UgcEmotionViewModel = koinViewModel(),
    ugcLifeJoyViewModel: UgcLifeJoyViewModel = koinViewModel(),
    ugcLifeExperienceViewModel: UgcLifeExperienceViewModel = koinViewModel(),
    ugcMysticismViewModel: UgcMysticismViewModel = koinViewModel(),
) {
    val scope = rememberCoroutineScope()
    val logger = KotlinLogging.logger("UgcContent")
    var focusOnContent by remember { mutableStateOf(false) }
    var topNavHasFocus by remember { mutableStateOf(false) }
    // 用于控制Tab选择后的延迟加载的防抖器（自动管理生命周期）
    val tabSelectionDebouncer = rememberDebouncer<UgcTopNavItem>(280L)

    // 使用remember的key参数确保只有在DrawerItem.UGC的tab状态变化时才重新计算
    val initialSelectedTabIndex = currentSelectedTabs[DrawerItem.UGC]
    var selectedTab by remember(initialSelectedTabIndex) {
        mutableStateOf(
            initialSelectedTabIndex
                ?.let { UgcTopNavItem.entries.getOrNull(it) }
                ?: UgcTopNavItem.Douga
        )
    }

    // 当选中标签变化时，保存到全局状态
    LaunchedEffect(selectedTab) {
        currentSelectedTabs[DrawerItem.UGC] = selectedTab.ordinal
    }

    //启动时刷新数据
    LaunchedEffect(Unit) {

    }

    BackHandler(focusOnContent || topNavHasFocus) {
        logger.fInfo { "onFocusBackToNav" }
        if (topNavHasFocus) {
            drawerItemFocusRequesters[DrawerItem.UGC]?.requestFocus()
            return@BackHandler
        }
        navFocusRequester.requestFocus(scope)
        // // scroll to top
        // scope.launch(Dispatchers.Main) {
        //     when (selectedTab) {
        //         UgcTopNavItem.Douga -> dougaState.lazyListState.animateScrollToItem(0)
        //         UgcTopNavItem.Game -> gameState.lazyListState.animateScrollToItem(0)
        //         UgcTopNavItem.Kichiku -> kichikuState.lazyListState.animateScrollToItem(0)
        //         UgcTopNavItem.Music -> musicState.lazyListState.animateScrollToItem(0)
        //         UgcTopNavItem.Dance -> danceState.lazyListState.animateScrollToItem(0)
        //         UgcTopNavItem.Cinephile -> cinephileState.lazyListState.animateScrollToItem(0)
        //         UgcTopNavItem.Ent -> entState.lazyListState.animateScrollToItem(0)
        //         UgcTopNavItem.Knowledge -> knowledgeState.lazyListState.animateScrollToItem(0)
        //         UgcTopNavItem.Tech -> techState.lazyListState.animateScrollToItem(0)
        //         UgcTopNavItem.Information -> informationState.lazyListState.animateScrollToItem(0)
        //         UgcTopNavItem.Food -> foodState.lazyListState.animateScrollToItem(0)
        //         UgcTopNavItem.ShortPlay -> shortPlayState.lazyListState.animateScrollToItem(0)
        //         UgcTopNavItem.Car -> carState.lazyListState.animateScrollToItem(0)
        //         UgcTopNavItem.Fashion -> fashionState.lazyListState.animateScrollToItem(0)
        //         UgcTopNavItem.Sports -> sportsState.lazyListState.animateScrollToItem(0)
        //         UgcTopNavItem.Animal -> animalState.lazyListState.animateScrollToItem(0)
        //         UgcTopNavItem.Vlog -> vlogState.lazyListState.animateScrollToItem(0)
        //         UgcTopNavItem.Painting -> paintingState.lazyListState.animateScrollToItem(0)
        //         UgcTopNavItem.Ai -> aiState.lazyListState.animateScrollToItem(0)
        //         UgcTopNavItem.Home -> homeState.lazyListState.animateScrollToItem(0)
        //         UgcTopNavItem.Outdoors -> outdoorsState.lazyListState.animateScrollToItem(0)
        //         UgcTopNavItem.Gym -> gymState.lazyListState.animateScrollToItem(0)
        //         UgcTopNavItem.Handmake -> handmakeState.lazyListState.animateScrollToItem(0)
        //         UgcTopNavItem.Travel -> travelState.lazyListState.animateScrollToItem(0)
        //         UgcTopNavItem.Rural -> ruralState.lazyListState.animateScrollToItem(0)
        //         UgcTopNavItem.Parenting -> parentingState.lazyListState.animateScrollToItem(0)
        //         UgcTopNavItem.Health -> healthState.lazyListState.animateScrollToItem(0)
        //         UgcTopNavItem.Emotion -> emotionState.lazyListState.animateScrollToItem(0)
        //         UgcTopNavItem.LifeJoy -> lifeJoyState.lazyListState.animateScrollToItem(0)
        //         UgcTopNavItem.LifeExperience -> lifeExperienceState.lazyListState.animateScrollToItem(0)
        //         UgcTopNavItem.Mysticism -> mysticismState.lazyListState.animateScrollToItem(0)
        //     }
        // }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopNav(
                modifier = Modifier
                    .focusRequester(navFocusRequester)
                    .onFocusChanged { topNavHasFocus = it.hasFocus },
                items = UgcTopNavItem.entries,
                isLargePadding = !focusOnContent,
                initialSelectedItem = selectedTab,
                onSelectedChanged = { nav ->
                    tabSelectionDebouncer.debounce(scope, nav as UgcTopNavItem) { selectedNavItem ->
                        selectedTab = selectedNavItem
                    }
                },
                onClick = { nav ->
                    when (nav) {
                        UgcTopNavItem.Douga -> ugcDougaViewModel.reloadAll()
                        UgcTopNavItem.Game -> ugcGameViewModel.reloadAll()
                        UgcTopNavItem.Kichiku -> ugcKichikuViewModel.reloadAll()
                        UgcTopNavItem.Music -> ugcMusicViewModel.reloadAll()
                        UgcTopNavItem.Dance -> ugcDanceViewModel.reloadAll()
                        UgcTopNavItem.Cinephile -> ugcCinephileViewModel.reloadAll()
                        UgcTopNavItem.Ent -> ugcEntViewModel.reloadAll()
                        UgcTopNavItem.Knowledge -> ugcKnowledgeViewModel.reloadAll()
                        UgcTopNavItem.Tech -> ugcTechViewModel.reloadAll()
                        UgcTopNavItem.Information -> ugcInformationViewModel.reloadAll()
                        UgcTopNavItem.Food -> ugcFoodViewModel.reloadAll()
                        UgcTopNavItem.ShortPlay -> ugcShortplayViewModel.reloadAll()
                        UgcTopNavItem.Car -> ugcCarViewModel.reloadAll()
                        UgcTopNavItem.Fashion -> ugcFashionViewModel.reloadAll()
                        UgcTopNavItem.Sports -> ugcSportsViewModel.reloadAll()
                        UgcTopNavItem.Animal -> ugcAnimalViewModel.reloadAll()
                        UgcTopNavItem.Vlog -> ugcVlogViewModel.reloadAll()
                        UgcTopNavItem.Painting -> ugcPaintingViewModel.reloadAll()
                        UgcTopNavItem.Ai -> ugcAiViewModel.reloadAll()
                        UgcTopNavItem.Home -> ugcHomeViewModel.reloadAll()
                        UgcTopNavItem.Outdoors -> ugcOutdoorsViewModel.reloadAll()
                        UgcTopNavItem.Gym -> ugcGymViewModel.reloadAll()
                        UgcTopNavItem.Handmake -> ugcHandmakeViewModel.reloadAll()
                        UgcTopNavItem.Travel -> ugcTravelViewModel.reloadAll()
                        UgcTopNavItem.Rural -> ugcRuralViewModel.reloadAll()
                        UgcTopNavItem.Parenting -> ugcParentingViewModel.reloadAll()
                        UgcTopNavItem.Health -> ugcHealthViewModel.reloadAll()
                        UgcTopNavItem.Emotion -> ugcEmotionViewModel.reloadAll()
                        UgcTopNavItem.LifeJoy -> ugcLifeJoyViewModel.reloadAll()
                        UgcTopNavItem.LifeExperience -> ugcLifeExperienceViewModel.reloadAll()
                        UgcTopNavItem.Mysticism -> ugcMysticismViewModel.reloadAll()
                    }
                },
                onLeftKeyEvent = {
                    // 顶部栏最左侧按左键时，跳转到左侧导航栏
                    drawerItemFocusRequesters[DrawerItem.UGC]?.requestFocus()
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
                label = "ugc animated content",
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                }
            ) { screen ->
                when (screen) {
                    UgcTopNavItem.Douga -> DougaContent(lazyListState = dougaState)
                    UgcTopNavItem.Game -> GameContent(lazyListState = gameState)
                    UgcTopNavItem.Kichiku -> KichikuContent(lazyListState = kichikuState)
                    UgcTopNavItem.Music -> MusicContent(lazyListState = musicState)
                    UgcTopNavItem.Dance -> DanceContent(lazyListState = danceState)
                    UgcTopNavItem.Cinephile -> CinephileContent(lazyListState = cinephileState)
                    UgcTopNavItem.Ent -> EntContent(lazyListState = entState)
                    UgcTopNavItem.Knowledge -> KnowledgeContent(lazyListState = knowledgeState)
                    UgcTopNavItem.Tech -> TechContent(lazyListState = techState)
                    UgcTopNavItem.Information -> InformationContent(lazyListState = informationState)
                    UgcTopNavItem.Food -> FoodContent(lazyListState = foodState)
                    UgcTopNavItem.ShortPlay -> ShortPlayContent(lazyListState = shortPlayState)
                    UgcTopNavItem.Car -> CarContent(lazyListState = carState)
                    UgcTopNavItem.Fashion -> FashionContent(lazyListState = fashionState)
                    UgcTopNavItem.Sports -> SportsContent(lazyListState = sportsState)
                    UgcTopNavItem.Animal -> AnimalContent(lazyListState = animalState)
                    UgcTopNavItem.Vlog -> VlogContent(lazyListState = vlogState)
                    UgcTopNavItem.Painting -> PaintingContent(lazyListState = paintingState)
                    UgcTopNavItem.Ai -> AiContent(lazyListState = aiState)
                    UgcTopNavItem.Home -> HomeContent(lazyListState = homeState)
                    UgcTopNavItem.Outdoors -> OutdoorsContent(lazyListState = outdoorsState)
                    UgcTopNavItem.Gym -> GymContent(lazyListState = gymState)
                    UgcTopNavItem.Handmake -> HandmakeContent(lazyListState = handmakeState)
                    UgcTopNavItem.Travel -> TravelContent(lazyListState = travelState)
                    UgcTopNavItem.Rural -> RuralContent(lazyListState = ruralState)
                    UgcTopNavItem.Parenting -> ParentingContent(lazyListState = parentingState)
                    UgcTopNavItem.Health -> HealthContent(lazyListState = healthState)
                    UgcTopNavItem.Emotion -> EmotionContent(lazyListState = emotionState)
                    UgcTopNavItem.LifeJoy -> LifeJoyContent(lazyListState = lifeJoyState)
                    UgcTopNavItem.LifeExperience -> LifeExperienceContent(lazyListState = lifeExperienceState)
                    UgcTopNavItem.Mysticism -> MysticismContent(lazyListState = mysticismState)
                }
            }
        }
    }
}