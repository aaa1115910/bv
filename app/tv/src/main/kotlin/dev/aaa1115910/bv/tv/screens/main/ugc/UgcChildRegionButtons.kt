package dev.aaa1115910.bv.tv.screens.main.ugc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.SuggestionChip
import androidx.tv.material3.Text
import dev.aaa1115910.biliapi.entity.ugc.UgcTypeV2
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.getDisplayName
import dev.aaa1115910.bv.util.ifElse
import dev.aaa1115910.bv.util.toast
import io.github.oshai.kotlinlogging.KotlinLogging

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun UgcChildRegionButtons(
    modifier: Modifier = Modifier,
    childUgcTypes: List<UgcTypeV2>
) {
//    val context = LocalContext.current
//    val logger = KotlinLogging.logger { }
//
//    val onClickChildRegion: (UgcTypeV2) -> Unit = { ugcType ->
//        logger.fInfo { "onClickChildRegion: $ugcType" }
//        "占位".toast(context)
//    }
//
//    UgcChildRegionButtonsContent(
//        modifier = modifier
//            .padding(vertical = 12.dp),
//        childUgcTypes = childUgcTypes,
//        onClickChildRegion = onClickChildRegion
//    )
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun UgcChildRegionButtonsContent(
    modifier: Modifier = Modifier,
    childUgcTypes: List<UgcTypeV2>,
    onClickChildRegion: (UgcTypeV2) -> Unit
) {
    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }

    LazyRow(
        modifier = modifier.focusRestorer(focusRequester),
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
    ) {
        itemsIndexed(items = childUgcTypes) { index, ugcType ->
            SuggestionChip(
                modifier = Modifier.ifElse(index == 0, Modifier.focusRequester(focusRequester)),
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
    BVTheme {
        UgcChildRegionButtons(
            modifier = Modifier.fillMaxWidth(),
            childUgcTypes = UgcTypeV2.dougaList
        )
    }
}