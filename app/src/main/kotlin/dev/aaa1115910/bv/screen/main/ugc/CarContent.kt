package dev.aaa1115910.bv.screen.main.ugc

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.aaa1115910.biliapi.entity.ugc.UgcType

@Composable
fun CarContent(
    modifier: Modifier = Modifier,
    state: UgcScaffoldState
) {
    UgcRegionScaffold(
        modifier = modifier,
        state = state,
        childRegionButtons = { CarChildRegionButtons() }
    )
}

@Composable
fun CarChildRegionButtons(modifier: Modifier = Modifier) {
    val ugcTypes = listOf(
        UgcType.CarKnowledge, UgcType.CarStrategy, UgcType.CarNewEnergyVehicle,
        UgcType.CarRacing, UgcType.CarModifiedVehicle, UgcType.CarMotorcycle,
        UgcType.CarTouringCar, UgcType.CarLife
    )

    UgcChildRegionButtons(
        modifier = modifier.fillMaxWidth(),
        childUgcTypes = ugcTypes
    )
}