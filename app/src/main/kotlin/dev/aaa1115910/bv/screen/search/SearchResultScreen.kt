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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.foundation.lazy.grid.TvGridCells
import androidx.tv.foundation.lazy.grid.TvLazyVerticalGrid
import androidx.tv.foundation.lazy.grid.itemsIndexed
import androidx.tv.material.LocalContentColor
import androidx.tv.material.Tab
import androidx.tv.material.TabRow
import dev.aaa1115910.biliapi.entity.search.SearchMediaResult
import dev.aaa1115910.biliapi.entity.search.SearchResultItem
import dev.aaa1115910.biliapi.entity.search.SearchVideoResult
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.activities.video.SeasonInfoActivity
import dev.aaa1115910.bv.activities.video.VideoInfoActivity
import dev.aaa1115910.bv.component.videocard.SeasonCard
import dev.aaa1115910.bv.component.videocard.SmallVideoCard
import dev.aaa1115910.bv.entity.carddata.SeasonCardData
import dev.aaa1115910.bv.entity.carddata.VideoCardData
import dev.aaa1115910.bv.util.removeHtmlTags
import dev.aaa1115910.bv.viewmodel.SearchResultType
import dev.aaa1115910.bv.viewmodel.SearchResultViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultScreen(
    modifier: Modifier = Modifier,
    searchResultViewModel: SearchResultViewModel = koinViewModel()
) {
    val context = LocalContext.current

    var rowSize by remember { mutableStateOf(4) }
    var currentIndex by remember { mutableStateOf(0) }
    val showLargeTitle by remember { derivedStateOf { currentIndex < rowSize } }
    val titleFontSize by animateFloatAsState(targetValue = if (showLargeTitle) 48f else 24f)

    var searchKeyword by remember { mutableStateOf("") }

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

            else -> {}
        }
    }

    LaunchedEffect(Unit) {
        val intent = (context as Activity).intent
        if (intent.hasExtra("keyword")) {
            searchKeyword = intent.getStringExtra("keyword") ?: ""
            if (searchKeyword == "") context.finish()
            searchResultViewModel.keyword = searchKeyword
            searchResultViewModel.update()
        } else {
            context.finish()
        }
    }

    LaunchedEffect(searchResultViewModel.searchType) {
        searchResultViewModel.update()
        rowSize = when (searchResultViewModel.searchType) {
            SearchResultType.Video -> 4
            SearchResultType.MediaBangumi, SearchResultType.MediaFt -> 6
            SearchResultType.BiliUser -> 3
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
                        text = "搜索: $searchKeyword",
                        fontSize = titleFontSize.sp
                    )
                    Text(
                        text = stringResource(
                            R.string.load_data_count,
                            searchResultViewModel.searchResult.size
                        ),
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            TabRow(
                modifier = Modifier
                    .onFocusChanged {
                        if (it.hasFocus) {
                            //scope.launch(Dispatchers.Main) {
                            //    listState.scrollToItem(0)
                            //}
                        }
                    },
                selectedTabIndex = searchResultViewModel.searchType.ordinal,
                separator = { Spacer(modifier = Modifier.width(12.dp)) },
            ) {
                SearchResultType.values().forEach { type ->
                    Tab(
                        selected = type == searchResultViewModel.searchType,
                        onFocus = { searchResultViewModel.searchType = type },
                    ) {
                        Text(
                            text = type.type,
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

            TvLazyVerticalGrid(
                columns = TvGridCells.Fixed(rowSize),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                itemsIndexed(items = searchResultViewModel.searchResult) { index, searchResult ->
                    SearchResultListItem(
                        searchResult = searchResult,
                        onClick = { onClickResult(searchResult) },
                        onFocus = {
                            currentIndex = index
                            if (index + 20 > searchResultViewModel.searchResult.size) {
                                searchResultViewModel.loadMore()
                            }
                        }
                    )
                }
            }
        }
    }
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
                    cover = searchResult.cover
                ),
                onClick = onClick,
                onFocus = onFocus
            )
        }

        else -> {

        }
    }
}