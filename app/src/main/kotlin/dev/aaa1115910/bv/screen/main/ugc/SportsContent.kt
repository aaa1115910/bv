package dev.aaa1115910.bv.screen.main.ugc

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.aaa1115910.biliapi.entity.ugc.UgcType

@Composable
fun SportsContent(
    modifier: Modifier = Modifier,
    state: UgcScaffoldState
) {
    UgcRegionScaffold(
        modifier = modifier,
        state = state,
        childRegionButtons = { SportsChildRegionButtons() }
    )
}

@Composable
fun SportsChildRegionButtons(modifier: Modifier = Modifier) {
    val ugcTypes = listOf(
        UgcType.SportsBasketball, UgcType.SportsFootball, UgcType.SportsAerobics,
        UgcType.SportsAthletic, UgcType.SportsCulture, UgcType.SportsComprehensive
    )

    UgcChildRegionButtons(
        modifier = modifier.fillMaxWidth(),
        childUgcTypes = ugcTypes
    )
}