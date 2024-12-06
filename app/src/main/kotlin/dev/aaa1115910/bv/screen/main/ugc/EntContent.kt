package dev.aaa1115910.bv.screen.main.ugc

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.aaa1115910.biliapi.entity.ugc.UgcType

@Composable
fun EntContent(
    modifier: Modifier = Modifier,
    state: UgcScaffoldState
) {
    UgcRegionScaffold(
        modifier = modifier,
        state = state,
        childRegionButtons = { EntChildRegionButtons() }
    )
}

@Composable
fun EntChildRegionButtons(modifier: Modifier = Modifier) {
    val ugcTypes = listOf(
        UgcType.EntTalker, UgcType.EntCpRecommendation, UgcType.EntBeauty, UgcType.EntFans,
        UgcType.EntEntertainmentNews, UgcType.EntCelebrity, UgcType.EntVariety
    )

    UgcChildRegionButtons(
        modifier = modifier.fillMaxWidth(),
        childUgcTypes = ugcTypes
    )
}