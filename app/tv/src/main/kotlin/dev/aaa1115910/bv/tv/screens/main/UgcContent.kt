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
import dev.aaa1115910.bv.tv.screens.main.ugc.CreateUgcContent
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.requestFocus
import dev.aaa1115910.bv.viewmodel.ugc.UgcAiViewModel
import dev.aaa1115910.bv.viewmodel.ugc.UgcAnimalViewModel
import dev.aaa1115910.bv.viewmodel.ugc.UgcCarViewModel
import dev.aaa1115910.bv.viewmodel.ugc.UgcCinephileViewModel
import dev.aaa1115910.bv.viewmodel.ugc.UgcDanceViewModel
import dev.aaa1115910.bv.viewmodel.ugc.UgcDougaViewModel
import dev.aaa1115910.bv.viewmodel.ugc.UgcEmotionViewModel
import dev.aaa1115910.bv.viewmodel.ugc.UgcEntViewModel
import dev.aaa1115910.bv.viewmodel.ugc.UgcFashionViewModel
import dev.aaa1115910.bv.viewmodel.ugc.UgcFoodViewModel
import dev.aaa1115910.bv.viewmodel.ugc.UgcGameViewModel
import dev.aaa1115910.bv.viewmodel.ugc.UgcGymViewModel
import dev.aaa1115910.bv.viewmodel.ugc.UgcHandmakeViewModel
import dev.aaa1115910.bv.viewmodel.ugc.UgcHealthViewModel
import dev.aaa1115910.bv.viewmodel.ugc.UgcHomeViewModel
import dev.aaa1115910.bv.viewmodel.ugc.UgcInformationViewModel
import dev.aaa1115910.bv.viewmodel.ugc.UgcKichikuViewModel
import dev.aaa1115910.bv.viewmodel.ugc.UgcKnowledgeViewModel
import dev.aaa1115910.bv.viewmodel.ugc.UgcLifeExperienceViewModel
import dev.aaa1115910.bv.viewmodel.ugc.UgcLifeJoyViewModel
import dev.aaa1115910.bv.viewmodel.ugc.UgcMusicViewModel
import dev.aaa1115910.bv.viewmodel.ugc.UgcMysticismViewModel
import dev.aaa1115910.bv.viewmodel.ugc.UgcOutdoorsViewModel
import dev.aaa1115910.bv.viewmodel.ugc.UgcPaintingViewModel
import dev.aaa1115910.bv.viewmodel.ugc.UgcParentingViewModel
import dev.aaa1115910.bv.viewmodel.ugc.UgcRuralViewModel
import dev.aaa1115910.bv.viewmodel.ugc.UgcShortplayViewModel
import dev.aaa1115910.bv.viewmodel.ugc.UgcSportsViewModel
import dev.aaa1115910.bv.viewmodel.ugc.UgcTechViewModel
import dev.aaa1115910.bv.viewmodel.ugc.UgcTravelViewModel
import dev.aaa1115910.bv.viewmodel.ugc.UgcVlogViewModel
import io.github.oshai.kotlinlogging.KotlinLogging
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

    // 为当前选中的tab创建LazyGridState
    val currentLazyGridState = rememberLazyGridState()
    var focusOnContent by remember { mutableStateOf(false) }
    var topNavHasFocus by remember { mutableStateOf(false) }

    // 使用remember的key参数确保只有在DrawerItem.UGC的tab状态变化时才重新计算
    var selectedTab by remember {
        mutableStateOf(
            currentSelectedTabs[DrawerItem.UGC]
                ?.let { UgcTopNavItem.entries.getOrNull(it) }
                ?: UgcTopNavItem.Douga
        )
    }

    // 获取所有ViewModels的映射
    val viewModelMap = remember {
        mapOf(
            UgcTopNavItem.Douga to ugcDougaViewModel,
            UgcTopNavItem.Game to ugcGameViewModel,
            UgcTopNavItem.Kichiku to ugcKichikuViewModel,
            UgcTopNavItem.Music to ugcMusicViewModel,
            UgcTopNavItem.Dance to ugcDanceViewModel,
            UgcTopNavItem.Cinephile to ugcCinephileViewModel,
            UgcTopNavItem.Ent to ugcEntViewModel,
            UgcTopNavItem.Knowledge to ugcKnowledgeViewModel,
            UgcTopNavItem.Tech to ugcTechViewModel,
            UgcTopNavItem.Information to ugcInformationViewModel,
            UgcTopNavItem.Food to ugcFoodViewModel,
            UgcTopNavItem.ShortPlay to ugcShortplayViewModel,
            UgcTopNavItem.Car to ugcCarViewModel,
            UgcTopNavItem.Fashion to ugcFashionViewModel,
            UgcTopNavItem.Sports to ugcSportsViewModel,
            UgcTopNavItem.Animal to ugcAnimalViewModel,
            UgcTopNavItem.Vlog to ugcVlogViewModel,
            UgcTopNavItem.Painting to ugcPaintingViewModel,
            UgcTopNavItem.Ai to ugcAiViewModel,
            UgcTopNavItem.Home to ugcHomeViewModel,
            UgcTopNavItem.Outdoors to ugcOutdoorsViewModel,
            UgcTopNavItem.Gym to ugcGymViewModel,
            UgcTopNavItem.Handmake to ugcHandmakeViewModel,
            UgcTopNavItem.Travel to ugcTravelViewModel,
            UgcTopNavItem.Rural to ugcRuralViewModel,
            UgcTopNavItem.Parenting to ugcParentingViewModel,
            UgcTopNavItem.Health to ugcHealthViewModel,
            UgcTopNavItem.Emotion to ugcEmotionViewModel,
            UgcTopNavItem.LifeJoy to ugcLifeJoyViewModel,
            UgcTopNavItem.LifeExperience to ugcLifeExperienceViewModel,
            UgcTopNavItem.Mysticism to ugcMysticismViewModel
        )
    }

    // 当选中标签变化时，保存到全局状态并处理懒加载
    LaunchedEffect(selectedTab) {
        currentSelectedTabs[DrawerItem.UGC] = selectedTab.ordinal

        // 取消所有其他ViewModel的延迟加载
        viewModelMap.values.forEach { viewModel ->
            viewModel.cancelDelayedLoad()
        }

        // 为当前选中的ViewModel开始延迟加载
        viewModelMap[selectedTab]?.loadDataWithDelay(300L)
    }

    BackHandler(focusOnContent || topNavHasFocus) {
        logger.fInfo { "onFocusBackToNav" }
        if (topNavHasFocus) {
            drawerItemFocusRequesters[DrawerItem.UGC]?.requestFocus()
            return@BackHandler
        }
        navFocusRequester.requestFocus(scope)
        // 滚动到顶部（如果需要的话）
        // scope.launch(Dispatchers.Main) {
        //     currentLazyGridState.animateScrollToItem(0)
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
                    selectedTab = nav as UgcTopNavItem
                    // 取消非selectedTab的所有延迟加载
                    viewModelMap
                        .filterKeys { it != selectedTab }
                        .values
                        .forEach { it.cancelDelayedLoad() }

                },
                onClick = { nav ->
                    // 点击时立即加载数据
                    viewModelMap[nav as UgcTopNavItem]?.reloadAll()
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
                CreateUgcContent(
                    navItem = screen,
                    lazyGridState = currentLazyGridState,
                    ugcViewModel = viewModelMap[screen]!!
                )
            }
        }
    }
}