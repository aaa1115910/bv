package dev.aaa1115910.bv.screen.main.ugc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.SuggestionChip
import androidx.tv.material3.Text
import dev.aaa1115910.biliapi.entity.ugc.UgcType
import dev.aaa1115910.bv.component.createCustomInitialFocusRestorerModifiers
import dev.aaa1115910.bv.component.ifElse
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.getDisplayName
import dev.aaa1115910.bv.util.toast
import io.github.oshai.kotlinlogging.KotlinLogging

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun UgcChildRegionButtons(
    modifier: Modifier = Modifier,
    childUgcTypes: List<UgcType>
) {
    val context = LocalContext.current
    val logger = KotlinLogging.logger { }

    val onClickChildRegion: (UgcType) -> Unit = { ugcType ->
        logger.fInfo { "onClickChildRegion: $ugcType" }
        "占位".toast(context)
    }

    UgcChildRegionButtonsContent(
        modifier = modifier
            .padding(vertical = 12.dp),
        childUgcTypes = childUgcTypes,
        onClickChildRegion = onClickChildRegion
    )
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun UgcChildRegionButtonsContent(
    modifier: Modifier = Modifier,
    childUgcTypes: List<UgcType>,
    onClickChildRegion: (UgcType) -> Unit
) {
    val context = LocalContext.current
    val focusRestorerModifiers = createCustomInitialFocusRestorerModifiers()

    LazyRow(
        modifier = modifier.then(focusRestorerModifiers.parentModifier),
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
    ) {
        itemsIndexed(items = childUgcTypes) { index, ugcType ->
            SuggestionChip(
                modifier = Modifier.ifElse(index == 0, focusRestorerModifiers.childModifier),
                onClick = { onClickChildRegion(ugcType) }
            ) {
                Text(text = ugcType.getDisplayName(context))
            }
        }
    }
}

@Preview(device = "id:tv_1080p")
@Composable
private fun UgcChildRegionButtonsPreview() {
    val ugcTypes = listOf(
        UgcType.Douga, UgcType.DougaMad, UgcType.DougaMmd, UgcType.DougaHandDrawn,
        UgcType.DougaVoice, UgcType.DougaGarageKit, UgcType.DougaTokusatsu,
        UgcType.DougaAcgnTalks, UgcType.DougaOther
    )

    BVTheme {
        UgcChildRegionButtons(
            modifier = Modifier.fillMaxWidth(),
            childUgcTypes = ugcTypes
        )
    }
}