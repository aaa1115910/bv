package dev.aaa1115910.bv.screen.main.ugc

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.aaa1115910.biliapi.entity.ugc.UgcType

@Composable
fun FoodContent(
    modifier: Modifier = Modifier,
    state: UgcScaffoldState
) {
    UgcRegionScaffold(
        modifier = modifier,
        state = state,
        childRegionButtons = { FoodChildRegionButtons() }
    )
}

@Composable
fun FoodChildRegionButtons(modifier: Modifier = Modifier) {
    val ugcTypes = listOf(
        UgcType.FoodMake, UgcType.FoodDetective, UgcType.FoodMeasurement,
        UgcType.FoodRural, UgcType.FoodRecord
    )

    UgcChildRegionButtons(
        modifier = modifier.fillMaxWidth(),
        childUgcTypes = ugcTypes
    )
}