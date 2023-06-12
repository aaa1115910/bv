package dev.aaa1115910.bv.screen.search

import android.app.Activity
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.foundation.lazy.grid.TvGridCells
import androidx.tv.foundation.lazy.grid.TvLazyVerticalGrid
import androidx.tv.foundation.lazy.grid.itemsIndexed
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.LocalContentColor
import androidx.tv.material3.Tab
import androidx.tv.material3.TabRow
import androidx.tv.material3.Text
import dev.aaa1115910.biliapi.entity.search.SearchBiliUserResult
import dev.aaa1115910.biliapi.entity.search.SearchMediaResult
import dev.aaa1115910.biliapi.entity.search.SearchResultItem
import dev.aaa1115910.biliapi.entity.search.SearchVideoResult
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.activities.video.SeasonInfoActivity
import dev.aaa1115910.bv.activities.video.UpInfoActivity
import dev.aaa1115910.bv.activities.video.VideoInfoActivity
import dev.aaa1115910.bv.component.videocard.SeasonCard
import dev.aaa1115910.bv.component.videocard.SmallVideoCard
import dev.aaa1115910.bv.entity.carddata.SeasonCardData
import dev.aaa1115910.bv.entity.carddata.VideoCardData
import dev.aaa1115910.bv.screen.user.UpCard
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.focusedScale
import dev.aaa1115910.bv.util.removeHtmlTags
import dev.aaa1115910.bv.util.requestFocus
import dev.aaa1115910.bv.viewmodel.search.SearchResultType
import dev.aaa1115910.bv.viewmodel.search.SearchResultViewModel
import mu.KotlinLogging
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SearchResultScreen(
    modifier: Modifier = Modifier,
    searchResultViewModel: SearchResultViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val logger = KotlinLogging.logger { }
    val tabRowFocusRequester = remember { FocusRequester() }

    var rowSize by remember { mutableIntStateOf(4) }
    var currentIndex by remember { mutableIntStateOf(0) }
    val showLargeTitle by remember { derivedStateOf { currentIndex < rowSize } }
    val titleFontSize by animateFloatAsState(targetValue = if (showLargeTitle) 48f else 24f)

    var searchKeyword by remember { mutableStateOf("") }

    val searchResult = when (searchResultViewModel.searchType) {
        SearchResultType.Video -> searchResultViewModel.videoSearchResult
        SearchResultType.MediaBangumi -> searchResultViewModel.mediaBangumiSearchResult
        SearchResultType.MediaFt -> searchResultViewModel.mediaFtSearchResult
        SearchResultType.BiliUser -> searchResultViewModel.biliUserSearchResult
    }

    var showFilter by remember { mutableStateOf(false) }

    val selectedOrder = searchResultViewModel.selectedOrder
    val selectedDuration = searchResultViewModel.selectedDuration
    val selectedPartition = searchResultViewModel.selectedPartition
    val selectedChildPartition = searchResultViewModel.selectedChildPartition

    val onClickResult: (SearchResultItem) -> Unit = { resultItem ->
        when (resultItem) {
            is SearchVideoResult -> {
                VideoInfoActivity.actionStart(
                    context = context,
                    aid = resultItem.aid,
                    fromSeason = false
                )
            }

            is SearchMediaResult -> {
                SeasonInfoActivity.actionStart(
                    context = context,
                    seasonId = resultItem.seasonId
                )
            }

            is SearchBiliUserResult -> {
                UpInfoActivity.actionStart(
                    context = context,
                    mid = resultItem.mid,
                    name = resultItem.uname
                )
            }

            else -> {}
        }
    }

    val backToTabRow: () -> Unit = {
        tabRowFocusRequester.requestFocus(scope)
    }

    LaunchedEffect(Unit) {
        val intent = (context as Activity).intent
        if (intent.hasExtra("keyword")) {
            searchKeyword = intent.getStringExtra("keyword") ?: ""
            if (searchKeyword == "") context.finish()
            searchResultViewModel.keyword = searchKeyword
        } else {
            context.finish()
        }
    }

    LaunchedEffect(searchResultViewModel.searchType) {
        rowSize = when (searchResultViewModel.searchType) {
            SearchResultType.Video -> 4
            SearchResultType.MediaBangumi, SearchResultType.MediaFt -> 6
            SearchResultType.BiliUser -> 3
        }
    }

    LaunchedEffect(
        selectedOrder, selectedDuration, selectedPartition, selectedChildPartition
    ) {
        logger.fInfo { "Start update search result because filter updated" }
        searchResultViewModel.update()
    }

    LaunchedEffect(currentIndex) {
        if (currentIndex + 24 > searchResult.results.size) {
            searchResultViewModel.loadMore(searchResult.type)
        }
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
                        modifier = Modifier.fillMaxWidth(0.7f),
                        text = searchKeyword,
                        fontSize = titleFontSize.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Column(
                        horizontalAlignment = Alignment.End,
                    ) {
                        Text(
                            text = stringResource(R.string.filter_dialog_open_tip),
                            color = Color.White.copy(alpha = 0.6f)
                        )
                        Text(
                            text = stringResource(
                                R.string.load_data_count,
                                searchResult.results.size
                            ),
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TabRow(
                    selectedTabIndex = searchResultViewModel.searchType.ordinal,
                    separator = { Spacer(modifier = Modifier.width(12.dp)) },
                ) {
                    SearchResultType.values().forEach { type ->
                        val isSelected = type == searchResultViewModel.searchType
                        val tabModifier =
                            if (isSelected) Modifier.focusRequester(tabRowFocusRequester) else Modifier
                        Tab(
                            modifier = tabModifier,
                            selected = isSelected,
                            onFocus = { searchResultViewModel.searchType = type },
                        ) {
                            Text(
                                text = type.getDisplayName(context),
                                fontSize = 12.sp,
                                color = LocalContentColor.current,
                                modifier = Modifier.padding(
                                    horizontal = 16.dp,
                                    vertical = 6.dp
                                )
                            )
                        }
                    }
                }
            }

            TvLazyVerticalGrid(
                modifier = Modifier.onPreviewKeyEvent {
                    when (it.key) {
                        Key.Back -> {
                            if (it.type == KeyEventType.KeyUp) backToTabRow()
                            return@onPreviewKeyEvent true
                        }

                        Key.DirectionCenter, Key.Enter -> {
                            // 让 Surface 监听到 KeyUp 事件，否则 Surface 将会是一直按下的状态
                            if (it.type == KeyEventType.KeyUp) return@onPreviewKeyEvent false

                            if (it.nativeKeyEvent.isLongPress) {
                                if (searchResultViewModel.searchType == SearchResultType.Video) {
                                    showFilter = true
                                }
                                return@onPreviewKeyEvent true
                            }
                            if (showFilter) return@onPreviewKeyEvent true
                        }
                    }
                    false
                },
                columns = TvGridCells.Fixed(rowSize),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                itemsIndexed(items = searchResult.results) { index, searchResultItem ->
                    SearchResultListItem(
                        searchResult = searchResultItem,
                        onClick = { if (!showFilter) onClickResult(searchResultItem) },
                        onFocus = { currentIndex = index }
                    )
                }
            }
        }
    }

    SearchResultVideoFilter(
        show = showFilter,
        onHideFilter = { showFilter = false },
        selectedOrder = selectedOrder,
        selectedDuration = selectedDuration,
        selectedPartition = selectedPartition,
        selectedChildPartition = selectedChildPartition,
        onSelectedOrderChange = { searchResultViewModel.selectedOrder = it },
        onSelectedDurationChange = { searchResultViewModel.selectedDuration = it },
        onSelectedPartitionChange = { searchResultViewModel.selectedPartition = it },
        onSelectedChildPartitionChange = { searchResultViewModel.selectedChildPartition = it }
    )
}

@Composable
private fun SearchResultListItem(
    modifier: Modifier = Modifier,
    searchResult: SearchResultItem,
    onClick: () -> Unit,
    onFocus: () -> Unit
) {
    when (searchResult) {
        is SearchVideoResult -> {
            SmallVideoCard(
                modifier = modifier,
                data = VideoCardData(
                    avid = searchResult.aid,
                    title = searchResult.title.removeHtmlTags(),
                    cover = "https:${searchResult.pic}",
                    upName = searchResult.author,
                    timeString = searchResult.duration
                ),
                onClick = onClick,
                onFocus = onFocus
            )
        }

        is SearchMediaResult -> {
            SeasonCard(
                modifier = modifier,
                data = SeasonCardData(
                    seasonId = searchResult.seasonId,
                    title = searchResult.title.removeHtmlTags(),
                    cover = searchResult.cover,
                    rating = String.format("%.1f", searchResult.mediaScore.score)
                ),
                onClick = onClick,
                onFocus = onFocus
            )
        }

        is SearchBiliUserResult -> {
            UpCard(
                modifier = modifier.focusedScale(0.95f),
                face = "https:${searchResult.upic}",
                sign = searchResult.usign,
                username = searchResult.uname,
                onFocusChange = { if (it) onFocus() },
                onClick = onClick
            )
        }

        else -> {

        }
    }
}
