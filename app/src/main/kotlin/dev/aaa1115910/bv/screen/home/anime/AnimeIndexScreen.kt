package dev.aaa1115910.bv.screen.home.anime

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.foundation.lazy.grid.TvGridCells
import androidx.tv.foundation.lazy.grid.TvGridItemSpan
import androidx.tv.foundation.lazy.grid.TvLazyVerticalGrid
import androidx.tv.foundation.lazy.grid.itemsIndexed
import androidx.tv.material3.OutlinedButton
import androidx.tv.material3.Text
import dev.aaa1115910.biliapi.http.entity.search.SearchMediaResult
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.activities.video.SeasonInfoActivity
import dev.aaa1115910.bv.component.index.AnimeIndexFilter
import dev.aaa1115910.bv.component.videocard.SeasonCard
import dev.aaa1115910.bv.entity.carddata.SeasonCardData
import dev.aaa1115910.bv.entity.proxy.ProxyArea
import dev.aaa1115910.bv.util.ImageSize
import dev.aaa1115910.bv.util.resizedImageUrl
import dev.aaa1115910.bv.viewmodel.index.AnimeIndexViewModel
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun AnimeIndexScreen(
    modifier: Modifier = Modifier,
    animeIndexViewModel: AnimeIndexViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val logger = KotlinLogging.logger { }

    var currentSeasonIndex by remember { mutableIntStateOf(0) }
    val showLargeTitle by remember {
        derivedStateOf {
            currentSeasonIndex < 5
        }
    }
    val titleFontSize by animateFloatAsState(
        targetValue = if (showLargeTitle) 48f else 24f,
        label = "title font size"
    )

    val indexResultItems = animeIndexViewModel.indexResultItems
    val noMore = animeIndexViewModel.noMore
    var showFilter by remember { mutableStateOf(false) }

    val onLongClickSeason = {
        showFilter = true
    }

    val reloadData = {
        scope.launch(Dispatchers.IO) {
            animeIndexViewModel.clearData()
            animeIndexViewModel.loadMore()
        }
    }

    LaunchedEffect(Unit) {
        reloadData()
    }

    LaunchedEffect(
        animeIndexViewModel.order,
        animeIndexViewModel.seasonVersion,
        animeIndexViewModel.spokenLanguageType,
        animeIndexViewModel.area,
        animeIndexViewModel.isFinish,
        animeIndexViewModel.copyright,
        animeIndexViewModel.seasonStatus,
        animeIndexViewModel.seasonMonth,
        animeIndexViewModel.year,
        animeIndexViewModel.styleId,
        animeIndexViewModel.desc
    ) {
        reloadData()
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            Box(
                modifier = Modifier.padding(start = 48.dp, top = 24.dp, bottom = 8.dp, end = 48.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(id = R.string.title_activity_anime_index),
                        fontSize = titleFontSize.sp,
                    )
                    Text(
                        text = stringResource(R.string.filter_dialog_open_tip),
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }

            }
        }
    ) { innerPadding ->
        TvLazyVerticalGrid(
            modifier = Modifier.padding(innerPadding),
            columns = TvGridCells.Fixed(6),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            itemsIndexed(items = indexResultItems) { index, indexResultItem ->
                SeasonCard(
                    data = SeasonCardData(
                        seasonId = indexResultItem.seasonId,
                        title = indexResultItem.title,
                        cover = indexResultItem.cover.resizedImageUrl(ImageSize.SeasonCoverThumbnail),
                        rating = indexResultItem.score.takeIf { it.isNotEmpty() },
                        // TODO 新增一个通用的 Badge
                        badge = SearchMediaResult.Badge(
                            text = indexResultItem.badge?.text ?: "",
                            textColor = "",
                            textColorNight = "",
                            bgColor = indexResultItem.badge?.bgColor ?: "",
                            bgColorNight = indexResultItem.badge?.bgColorNight ?: "",
                            borderColor = "",
                            borderColorNight = "",
                            bgStyle = 0
                        ).takeIf { indexResultItem.badge != null },
                    ),
                    onFocus = {
                        currentSeasonIndex = index
                        if (index + 30 > indexResultItems.size) {
                            println("load more by focus")
                            scope.launch(Dispatchers.IO) { animeIndexViewModel.loadMore() }
                        }
                    },
                    onClick = {
                        SeasonInfoActivity.actionStart(
                            context = context,
                            seasonId = indexResultItem.seasonId,
                            proxyArea = ProxyArea.checkProxyArea(indexResultItem.title)
                        )
                    },
                    onLongClick = onLongClickSeason
                )
            }
            if (indexResultItems.isEmpty() && noMore) {
                item(
                    span = { TvGridItemSpan(6) }
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = stringResource(R.string.no_data))
                            OutlinedButton(onClick = { showFilter = true }) {
                                Text(text = stringResource(R.string.filter_dialog_open_tip_click))
                            }
                        }
                    }
                }
            }
        }
    }

    AnimeIndexFilter(
        show = showFilter,
        onDismissRequest = { showFilter = false },
        order = animeIndexViewModel.order,
        seasonVersion = animeIndexViewModel.seasonVersion,
        spokenLanguageType = animeIndexViewModel.spokenLanguageType,
        area = animeIndexViewModel.area,
        isFinish = animeIndexViewModel.isFinish,
        copyright = animeIndexViewModel.copyright,
        seasonStatus = animeIndexViewModel.seasonStatus,
        seasonMonth = animeIndexViewModel.seasonMonth,
        year = animeIndexViewModel.year,
        styleId = animeIndexViewModel.styleId,
        desc = animeIndexViewModel.desc,
        onOrderChange = { animeIndexViewModel.order = it },
        onSeasonVersionChange = { animeIndexViewModel.seasonVersion = it },
        onSpokenLanguageTypeChange = { animeIndexViewModel.spokenLanguageType = it },
        onAreaChange = { animeIndexViewModel.area = it },
        onIsFinishChange = { animeIndexViewModel.isFinish = it },
        onCopyrightChange = { animeIndexViewModel.copyright = it },
        onSeasonStatusChange = { animeIndexViewModel.seasonStatus = it },
        onSeasonMonthChange = { animeIndexViewModel.seasonMonth = it },
        onYearChange = { animeIndexViewModel.year = it },
        onStyleIdChange = { animeIndexViewModel.styleId = it },
        onDescChange = { animeIndexViewModel.desc = it }
    )
}