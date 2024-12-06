package dev.aaa1115910.bv.screen.main.ugc

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.aaa1115910.biliapi.entity.ugc.UgcType

@Composable
fun MusicContent(
    modifier: Modifier = Modifier,
    state: UgcScaffoldState
) {
    UgcRegionScaffold(
        modifier = modifier,
        state = state,
        childRegionButtons = { MusicChildRegionButtons() }
    )
}

@Composable
fun MusicChildRegionButtons(modifier: Modifier = Modifier) {
    val ugcTypes = listOf(
        UgcType.MusicOriginal, UgcType.MusicLive, UgcType.MusicCover, UgcType.MusicPerform,
        UgcType.MusicCommentary, UgcType.MusicVocaloidUtau, UgcType.MusicMv, UgcType.MusicFanVideos,
        UgcType.MusicAiMusic, UgcType.MusicRadio, UgcType.MusicTutorial, UgcType.MusicOther
    )

    UgcChildRegionButtons(
        modifier = modifier.fillMaxWidth(),
        childUgcTypes = ugcTypes
    )
}