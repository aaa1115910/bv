package dev.aaa1115910.bv.screen.main.pgc

import android.app.Activity
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
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
import androidx.tv.material3.OutlinedButton
import androidx.tv.material3.Text
import dev.aaa1115910.biliapi.entity.pgc.PgcType
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.activities.video.SeasonInfoActivity
import dev.aaa1115910.bv.component.pgc.IndexFilter
import dev.aaa1115910.bv.component.videocard.SeasonCard
import dev.aaa1115910.bv.entity.carddata.SeasonCardData
import dev.aaa1115910.bv.entity.proxy.ProxyArea
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.getDisplayName
import dev.aaa1115910.bv.viewmodel.index.PgcIndexViewModel
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun PgcIndexScreen(
    modifier: Modifier = Modifier,
    pgcIndexViewModel: PgcIndexViewModel = koinViewModel()
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

    val pgcItems = pgcIndexViewModel.indexResultItems
    val noMore = pgcIndexViewModel.noMore
    var showFilter by remember { mutableStateOf(false) }

    val onLongClickSeason = {
        showFilter = true
    }

    val reloadData = {
        scope.launch(Dispatchers.IO) {
            pgcIndexViewModel.clearData()
            pgcIndexViewModel.loadMore()
        }
    }

    LaunchedEffect(Unit) {
        val intent = (context as Activity).intent
        val pgcType = runCatching {
            PgcType.entries[intent.getIntExtra("pgcType", 0)]
        }.onFailure {
            logger.warn { "get pgcType from intent failed: ${it.stackTraceToString()}" }
        }.getOrDefault(PgcType.Anime)
        logger.fInfo { "index pgcType: $pgcType" }
        pgcIndexViewModel.changePgcType(pgcType)
        reloadData()
    }

    LaunchedEffect(
        pgcIndexViewModel.indexOrder,
        pgcIndexViewModel.indexOrderType,
        pgcIndexViewModel.seasonVersion,
        pgcIndexViewModel.spokenLanguage,
        pgcIndexViewModel.area,
        pgcIndexViewModel.isFinish,
        pgcIndexViewModel.copyright,
        pgcIndexViewModel.seasonStatus,
        pgcIndexViewModel.seasonMonth,
        pgcIndexViewModel.producer,
        pgcIndexViewModel.year,
        pgcIndexViewModel.releaseDate,
        pgcIndexViewModel.style,
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
                        text = stringResource(id = R.string.title_activity_pgc_index) +
                                " - " + pgcIndexViewModel.pgcType.getDisplayName(context),
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
        LazyVerticalGrid(
            modifier = Modifier.padding(innerPadding),
            columns = GridCells.Fixed(6),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            itemsIndexed(items = pgcItems) { index, pgcItem ->
                SeasonCard(
                    data = SeasonCardData.fromPgcItem(pgcItem),
                    onFocus = {
                        currentSeasonIndex = index
                        if (index + 30 > pgcItems.size) {
                            println("load more by focus")
                            scope.launch(Dispatchers.IO) { pgcIndexViewModel.loadMore() }
                        }
                    },
                    onClick = {
                        SeasonInfoActivity.actionStart(
                            context = context,
                            seasonId = pgcItem.seasonId,
                            proxyArea = ProxyArea.checkProxyArea(pgcItem.title)
                        )
                    },
                    onLongClick = onLongClickSeason
                )
            }
            if (pgcItems.isEmpty() && noMore) {
                item(
                    span = { GridItemSpan(6) }
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

    IndexFilter(
        type = pgcIndexViewModel.pgcType,
        show = showFilter,
        onDismissRequest = { showFilter = false },
        order = pgcIndexViewModel.indexOrder,
        orderType = pgcIndexViewModel.indexOrderType,
        seasonVersion = pgcIndexViewModel.seasonVersion,
        spokenLanguage = pgcIndexViewModel.spokenLanguage,
        area = pgcIndexViewModel.area,
        isFinish = pgcIndexViewModel.isFinish,
        copyright = pgcIndexViewModel.copyright,
        seasonStatus = pgcIndexViewModel.seasonStatus,
        seasonMonth = pgcIndexViewModel.seasonMonth,
        producer = pgcIndexViewModel.producer,
        year = pgcIndexViewModel.year,
        releaseDate = pgcIndexViewModel.releaseDate,
        style = pgcIndexViewModel.style,
        onOrderChange = { pgcIndexViewModel.indexOrder = it },
        onOrderTypeChange = { pgcIndexViewModel.indexOrderType = it },
        onSeasonVersionChange = { pgcIndexViewModel.seasonVersion = it },
        onSpokenLanguageChange = { pgcIndexViewModel.spokenLanguage = it },
        onAreaChange = { pgcIndexViewModel.area = it },
        onIsFinishChange = { pgcIndexViewModel.isFinish = it },
        onCopyrightChange = { pgcIndexViewModel.copyright = it },
        onSeasonStatusChange = { pgcIndexViewModel.seasonStatus = it },
        onSeasonMonthChange = { pgcIndexViewModel.seasonMonth = it },
        onProducerChange = { pgcIndexViewModel.producer = it },
        onYearChange = { pgcIndexViewModel.year = it },
        onReleaseDateChange = { pgcIndexViewModel.releaseDate = it },
        onStyleChange = { pgcIndexViewModel.style = it }
    )
}