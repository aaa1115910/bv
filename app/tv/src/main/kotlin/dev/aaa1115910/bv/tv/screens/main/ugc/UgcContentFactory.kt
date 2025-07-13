package dev.aaa1115910.bv.tv.screens.main.ugc

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.aaa1115910.biliapi.entity.ugc.UgcTypeV2
import dev.aaa1115910.bv.tv.component.UgcTopNavItem
import dev.aaa1115910.bv.viewmodel.ugc.UgcViewModel

/**
 * 通用UGC内容组件，用于替代所有重复的*Content.kt文件
 */
@Composable
fun GenericUgcContent(
    modifier: Modifier = Modifier,
    lazyGridState: LazyGridState = rememberLazyGridState(),
    ugcViewModel: UgcViewModel,
    childUgcTypes: List<UgcTypeV2>
) {
    UgcRegionScaffold(
        modifier = modifier,
        lazyGridState = lazyGridState,
        ugcViewModel = ugcViewModel,
        childRegionButtons = {
            UgcChildRegionButtons(
                modifier = Modifier.fillMaxWidth(),
                childUgcTypes = childUgcTypes
            )
        }
    )
}

/**
 * UGC内容工厂，根据TopNavItem创建对应的内容组件
 */
@Composable
fun CreateUgcContent(
    navItem: UgcTopNavItem,
    modifier: Modifier = Modifier,
    lazyGridState: LazyGridState = rememberLazyGridState(),
    ugcViewModel: UgcViewModel
) {
    val childUgcTypes = when (navItem) {
        UgcTopNavItem.Douga -> UgcTypeV2.dougaList
        UgcTopNavItem.Game -> UgcTypeV2.gameList
        UgcTopNavItem.Kichiku -> UgcTypeV2.kichikuList
        UgcTopNavItem.Music -> UgcTypeV2.musicList
        UgcTopNavItem.Dance -> UgcTypeV2.danceList
        UgcTopNavItem.Cinephile -> UgcTypeV2.cinephileList
        UgcTopNavItem.Ent -> UgcTypeV2.entList
        UgcTopNavItem.Knowledge -> UgcTypeV2.knowledgeList
        UgcTopNavItem.Tech -> UgcTypeV2.techList
        UgcTopNavItem.Information -> UgcTypeV2.informationList
        UgcTopNavItem.Food -> UgcTypeV2.foodList
        UgcTopNavItem.ShortPlay -> UgcTypeV2.shortplayList
        UgcTopNavItem.Car -> UgcTypeV2.carList
        UgcTopNavItem.Fashion -> UgcTypeV2.fashionList
        UgcTopNavItem.Sports -> UgcTypeV2.sportsList
        UgcTopNavItem.Animal -> UgcTypeV2.animalList
        UgcTopNavItem.Vlog -> UgcTypeV2.vlogList
        UgcTopNavItem.Painting -> UgcTypeV2.paintingList
        UgcTopNavItem.Ai -> UgcTypeV2.aiList
        UgcTopNavItem.Home -> UgcTypeV2.homeList
        UgcTopNavItem.Outdoors -> UgcTypeV2.outdoorsList
        UgcTopNavItem.Gym -> UgcTypeV2.gymList
        UgcTopNavItem.Handmake -> UgcTypeV2.handmakeList
        UgcTopNavItem.Travel -> UgcTypeV2.travelList
        UgcTopNavItem.Rural -> UgcTypeV2.ruralList
        UgcTopNavItem.Parenting -> UgcTypeV2.parentingList
        UgcTopNavItem.Health -> UgcTypeV2.healthList
        UgcTopNavItem.Emotion -> UgcTypeV2.emotionList
        UgcTopNavItem.LifeJoy -> UgcTypeV2.lifeJoyList
        UgcTopNavItem.LifeExperience -> UgcTypeV2.lifeExperienceList
        UgcTopNavItem.Mysticism -> UgcTypeV2.mysticismList
    }

    GenericUgcContent(
        modifier = modifier,
        lazyGridState = lazyGridState,
        ugcViewModel = ugcViewModel,
        childUgcTypes = childUgcTypes
    )
}
