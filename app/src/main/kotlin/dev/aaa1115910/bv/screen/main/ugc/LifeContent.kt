package dev.aaa1115910.bv.screen.main.ugc

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.aaa1115910.biliapi.entity.ugc.UgcType

@Composable
fun LifeContent(
    modifier: Modifier = Modifier,
    state: UgcScaffoldState
) {
    UgcRegionScaffold(
        modifier = modifier,
        state = state,
        childRegionButtons = { LifeChildRegionButtons() }
    )
}

@Composable
fun LifeChildRegionButtons(modifier: Modifier = Modifier) {
    val ugcTypes = listOf(
        UgcType.LifeFunny, UgcType.LifeParenting, UgcType.LifeTravel, UgcType.LiseRuralLife,
        UgcType.LifeHome, UgcType.LifeHandMake, UgcType.LifePainting, UgcType.LifeDaily
    )

    UgcChildRegionButtons(
        modifier = modifier.fillMaxWidth(),
        childUgcTypes = ugcTypes
    )
}