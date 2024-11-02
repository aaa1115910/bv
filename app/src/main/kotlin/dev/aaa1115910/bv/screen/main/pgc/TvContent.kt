package dev.aaa1115910.bv.screen.main.pgc

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.rounded.QuestionMark
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.aaa1115910.biliapi.entity.pgc.PgcType
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.activities.pgc.PgcIndexActivity
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.viewmodel.pgc.PgcTvViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun TvContent(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState,
    pgcViewModel: PgcTvViewModel = koinViewModel()
) {
    val context = LocalContext.current

    val onOpenIndex: () -> Unit = {
        PgcIndexActivity.actionStart(context = context, pgcType = PgcType.Tv)
    }

    PgcScaffold(
        lazyListState = lazyListState,
        pgcViewModel = pgcViewModel,
        pgcType = PgcType.Tv,
        featureButtons = {
            TvFeatureButtons(
                modifier = Modifier.padding(vertical = 24.dp),
                onOpenIndex = onOpenIndex
            )
        }
    )
}

@Composable
private fun TvFeatureButtons(
    modifier: Modifier = Modifier,
    onOpenIndex: () -> Unit
) {
    val buttons = listOf(
        Triple(
            stringResource(R.string.anime_home_button_index),
            Icons.AutoMirrored.Rounded.List,
            onOpenIndex
        ),
        Triple(
            stringResource(R.string.pgc_home_button_unknown),
            Icons.Rounded.QuestionMark,
            showPlaceholderToast
        ),
        Triple(
            stringResource(R.string.pgc_home_button_unknown),
            Icons.Rounded.QuestionMark,
            showPlaceholderToast
        ),
        Triple(
            stringResource(R.string.pgc_home_button_unknown),
            Icons.Rounded.QuestionMark,
            showPlaceholderToast
        )
    )
    PgcFeatureButtons(
        modifier = modifier,
        buttons = buttons
    )
}

@Preview(device = "id:tv_1080p")
@Composable
private fun TvFeatureButtonsPreview() {
    BVTheme {
        TvFeatureButtons(
            modifier = Modifier,
            onOpenIndex = {},
        )
    }
}
