package dev.aaa1115910.bv.screen.main.ugc

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.aaa1115910.biliapi.entity.ugc.UgcType

@Composable
fun CinephileContent(
    modifier: Modifier = Modifier,
    state: UgcScaffoldState
) {
    UgcRegionScaffold(
        modifier = modifier,
        state = state,
        childRegionButtons = { CinephileChildRegionButtons() }
    )
}

@Composable
fun CinephileChildRegionButtons(modifier: Modifier = Modifier) {
    val ugcTypes = listOf(
        UgcType.CinephileCinecism, UgcType.CinephileNibtage, UgcType.CinephileMashup,
        UgcType.CinephileAiImagine, UgcType.CinephileTrailerInfo, UgcType.CinephileShortPlay,
        UgcType.CinephileShortFilm, UgcType.CinephileComperhensive
    )

    UgcChildRegionButtons(
        modifier = modifier.fillMaxWidth(),
        childUgcTypes = ugcTypes
    )
}