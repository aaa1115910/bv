package dev.aaa1115910.bv.screen.main.ugc

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.aaa1115910.biliapi.entity.ugc.UgcType

@Composable
fun KnowledgeContent(
    modifier: Modifier = Modifier,
    state: UgcScaffoldState
) {
    UgcRegionScaffold(
        modifier = modifier,
        state = state,
        childRegionButtons = { KnowledgeChildRegionButtons() }
    )
}

@Composable
fun KnowledgeChildRegionButtons(modifier: Modifier = Modifier) {
    val ugcTypes = listOf(
        UgcType.KnowledgeScience, UgcType.KnowledgeSocialScience, UgcType.KnowledgeHumanity,
        UgcType.KnowledgeBusiness, UgcType.KnowledgeCampus, UgcType.KnowledgeCareer,
        UgcType.KnowledgeDesign, UgcType.KnowledgeSkill
    )

    UgcChildRegionButtons(
        modifier = modifier.fillMaxWidth(),
        childUgcTypes = ugcTypes
    )
}