package dev.aaa1115910.bv.mobile.screen

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.aaa1115910.biliapi.entity.season.FollowingSeasonType
import dev.aaa1115910.bv.entity.carddata.SeasonCardData
import dev.aaa1115910.bv.mobile.component.videocard.SeasonCard
import dev.aaa1115910.bv.mobile.theme.BVMobileTheme
import dev.aaa1115910.bv.util.OnBottomReached
import dev.aaa1115910.bv.util.calculateWindowSizeClassInPreview
import dev.aaa1115910.bv.util.getDisplayName
import dev.aaa1115910.bv.viewmodel.user.FollowingSeasonViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun FollowingSeasonScreen(
    modifier: Modifier = Modifier,
    windowSize: WindowSizeClass,
    followingSeasonViewModel: FollowingSeasonViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val listState = rememberLazyGridState()

    listState.OnBottomReached(
        loading = followingSeasonViewModel.updating
    ) {
        if (followingSeasonViewModel.noMore) return@OnBottomReached
        followingSeasonViewModel.loadMore()
    }

    FollowingSeasonContent(
        modifier = modifier,
        windowSize = windowSize,
        type = followingSeasonViewModel.followingSeasonType,
        seasons = followingSeasonViewModel.followingSeasons.map(SeasonCardData::fromFollowingSeason),
        onBack = { (context as Activity).finish() },
        onTypeChange = {
            followingSeasonViewModel.followingSeasonType = it
            followingSeasonViewModel.clearData()
            followingSeasonViewModel.loadMore()
        },
        onClickSeason = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FollowingSeasonContent(
    modifier: Modifier = Modifier,
    windowSize: WindowSizeClass,
    type: FollowingSeasonType,
    seasons: List<SeasonCardData>,
    onBack: () -> Unit,
    onTypeChange: (FollowingSeasonType) -> Unit,
    onClickSeason: (SeasonCardData) -> Unit,
) {
    val context = LocalContext.current
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Column {
                LargeTopAppBar(
                    title = { Text(text = "我的追番") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = null
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
                PrimaryTabRow(
                    selectedTabIndex = type.ordinal,
                ) {
                    FollowingSeasonType.entries.forEach { seasonType ->
                        Tab(
                            selected = type == seasonType,
                            text = { Text(text = seasonType.getDisplayName(context)) },
                            onClick = { onTypeChange(seasonType) }
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        LazyVerticalGrid(
            modifier = Modifier.padding(top = innerPadding.calculateTopPadding()),
            columns = GridCells.Adaptive(100.dp),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(seasons) { index, season ->
                SeasonCard(
                    data = season,
                    onClick = { onClickSeason(season) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview
@Composable
private fun FollowingSeasonContentPreview() {
    val windowSize = calculateWindowSizeClassInPreview()
    var selectedType by remember { mutableStateOf(FollowingSeasonType.Bangumi) }

    val seasons = (1..50).map {
        SeasonCardData(
            seasonId = it,
            title = "Title $it",
            cover = "http://i0.hdslb.com/bfs/bangumi/image/8d211c396aad084d6fa413015200dda6ed260768.png",
            rating = "8.6"
        )
    }

    BVMobileTheme {
        FollowingSeasonContent(
            windowSize = windowSize,
            type = selectedType,
            seasons = seasons,
            onBack = {},
            onTypeChange = { selectedType = it },
            onClickSeason = {}
        )
    }
}