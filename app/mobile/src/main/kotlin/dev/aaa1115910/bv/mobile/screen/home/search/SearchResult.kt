package dev.aaa1115910.bv.mobile.screen.home.search

import android.app.Activity
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExpandedDockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.aaa1115910.biliapi.entity.ugc.toSmartDate
import dev.aaa1115910.biliapi.repositories.SearchType
import dev.aaa1115910.biliapi.repositories.SearchTypeResult
import dev.aaa1115910.bv.entity.carddata.VideoCardData
import dev.aaa1115910.bv.mobile.component.search.UgcListItem
import dev.aaa1115910.bv.mobile.component.videocard.SmallVideoCard
import dev.aaa1115910.bv.mobile.screen.home.SearchBarResultContent
import dev.aaa1115910.bv.util.removeHtmlTags


@OptIn(
    ExperimentalSharedTransitionApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3WindowSizeClassApi::class
)
@Composable
fun SearchResultContent(
    modifier: Modifier = Modifier,
    searchBarState: SearchBarState = rememberSearchBarState(),
    textFieldState: TextFieldState = rememberTextFieldState(),
    keywordSuggestions: List<String>,
    historyKeywords: List<String>,
    matchedHistory: List<String>,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    inputField: @Composable () -> Unit = {},
    videoSearchResult: List<SearchTypeResult.Video>,
    mediaBangumiSearchResult: List<SearchTypeResult.Pgc>,
    mediaFtSearchResult: List<SearchTypeResult.Pgc>,
    biliUserSearchResult: List<SearchTypeResult.User>,
    onSearch: (String) -> Unit,
    onOpenUgc: (Long) -> Unit
) {
    val context = LocalContext.current
    val windowSize = calculateWindowSizeClass(context as Activity).widthSizeClass
    var searchType by remember { mutableStateOf(SearchType.Video) }

    Scaffold(
        modifier = modifier,
        topBar = {
            when (windowSize) {
                WindowWidthSizeClass.Compact -> {
                    Column(
                        modifier = Modifier
                            .statusBarsPadding()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            with(sharedTransitionScope) {
                                SearchBar(
                                    modifier = Modifier
                                        .padding(vertical = 4.dp)
                                        .sharedElement(
                                            sharedContentState = rememberSharedContentState("searchBar"),
                                            animatedVisibilityScope = animatedVisibilityScope
                                        ),
                                    state = searchBarState,
                                    inputField = inputField
                                )
                            }
                        }
                        PrimaryScrollableTabRow(
                            selectedTabIndex = searchType.ordinal,
                        ) {
                            SearchType.entries.forEachIndexed { index, title ->
                                Tab(
                                    selected = searchType.ordinal == index,
                                    onClick = { searchType = title },
                                    text = { Text(text = title.name) },
                                )
                            }
                        }
                    }
                }

                else -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .statusBarsPadding()
                    ) {
                        with(sharedTransitionScope) {
                            Box(
                                modifier = Modifier
                                    .padding(vertical = 4.dp, horizontal = 36.dp)
                                    .sharedElement(
                                        sharedContentState = rememberSharedContentState("dockedSearchBar"),
                                        animatedVisibilityScope = animatedVisibilityScope
                                    )
                            ) {
                                SearchBar(
                                    modifier = Modifier,
                                    state = searchBarState,
                                    inputField = inputField
                                )
                                ExpandedDockedSearchBar(
                                    state = searchBarState,
                                    inputField = inputField
                                ) {
                                    SearchBarResultContent(
                                        keyword = textFieldState.text.toString(),
                                        recentHistory = historyKeywords,
                                        matchedHistory = matchedHistory,
                                        suggestions = keywordSuggestions,
                                        onSearch = onSearch,
                                        onDeleteHistory = {}
                                    )
                                }
                            }
                        }

                        PrimaryScrollableTabRow(
                            modifier = Modifier
                                .align(Alignment.Bottom),
                            selectedTabIndex = searchType.ordinal,
                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                        ) {
                            SearchType.entries.forEachIndexed { index, title ->
                                Tab(
                                    selected = searchType.ordinal == index,
                                    onClick = { searchType = title },
                                    text = { Text(text = title.name) },
                                )
                            }
                        }
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(top = innerPadding.calculateTopPadding())
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.surface,
            shape = if (windowSize == WindowWidthSizeClass.Compact) RoundedCornerShape(0.dp) else MaterialTheme.shapes.large,
        ) {
            when (searchType) {
                SearchType.Video -> VideoSearchResult(
                    videoList = videoSearchResult,
                    onClickVideo = onOpenUgc
                )

                SearchType.MediaBangumi -> MediaBangumiSearchResult(
                    mediaBangumiList = mediaBangumiSearchResult
                )

                SearchType.MediaFt -> MediaFtSearchResult(
                    mediaFtList = mediaFtSearchResult
                )

                SearchType.BiliUser -> BiliUserSearchResult(
                    biliUserList = biliUserSearchResult
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
private fun VideoSearchResult(
    modifier: Modifier = Modifier,
    videoList: List<SearchTypeResult.Video>,
    onClickVideo: (aid: Long) -> Unit
) {
    val context = LocalContext.current
    val windowSize = calculateWindowSizeClass(context as Activity).widthSizeClass

    when (windowSize) {
        WindowWidthSizeClass.Compact -> LazyColumn(
            modifier = modifier
        ) {
            items(videoList) { video ->
                UgcListItem(
                    data = VideoCardData(
                        avid = video.aid,
                        title = video.title.removeHtmlTags(),
                        cover = video.cover,
                        play = video.play,
                        danmaku = video.danmaku,
                        upName = video.author,
                        time = video.duration * 1000L,
                        pubTime = video.pubDate.toLong().toSmartDate()
                    ),
                    onClick = { onClickVideo(video.aid) }
                )
            }
        }

        else -> LazyVerticalGrid(
            modifier = modifier,
            columns = GridCells.Adaptive(220.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(videoList) { video ->
                SmallVideoCard(
                    data = VideoCardData(
                        avid = video.aid,
                        title = video.title.removeHtmlTags(),
                        cover = video.cover,
                        play = video.play,
                        danmaku = video.danmaku,
                        upName = video.author,
                        time = video.duration * 1000L,
                        pubTime = video.pubDate.toLong().toSmartDate()
                    ),
                    onClick = { onClickVideo(video.aid) }
                )
            }
        }
    }
}


@Composable
private fun MediaBangumiSearchResult(
    modifier: Modifier = Modifier,
    mediaBangumiList: List<SearchTypeResult.Pgc>,
) {

}


@Composable
private fun MediaFtSearchResult(
    modifier: Modifier = Modifier,
    mediaFtList: List<SearchTypeResult.Pgc>,
) {

}


@Composable
private fun BiliUserSearchResult(
    modifier: Modifier = Modifier,
    biliUserList: List<SearchTypeResult.User>,
) {

}
