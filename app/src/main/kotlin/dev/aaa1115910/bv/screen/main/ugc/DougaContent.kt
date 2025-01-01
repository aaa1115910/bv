package dev.aaa1115910.bv.screen.main.ugc

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.aaa1115910.biliapi.entity.ugc.UgcType

@Composable
fun DougaContent(
    modifier: Modifier = Modifier,
    state: UgcScaffoldState
) {
    UgcRegionScaffold(
        modifier = modifier,
        state = state,
        childRegionButtons = { DougaChildRegionButtons() }
    )
}

@Composable
fun DougaChildRegionButtons(modifier: Modifier = Modifier) {
    val ugcTypes = listOf(
        UgcType.DougaMad, UgcType.DougaMmd, UgcType.DougaHandDrawn, UgcType.DougaVoice,
        UgcType.DougaGarageKit, UgcType.DougaTokusatsu, UgcType.DougaAcgnTalks, UgcType.DougaOther
    )

    UgcChildRegionButtons(
        modifier = modifier.fillMaxWidth(),
        childUgcTypes = ugcTypes
    )
}