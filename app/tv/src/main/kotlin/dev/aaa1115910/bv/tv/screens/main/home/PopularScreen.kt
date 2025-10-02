package dev.aaa1115910.bv.tv.screens.main.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import dev.aaa1115910.biliapi.entity.ugc.UgcItem
import dev.aaa1115910.bv.tv.component.LoadingTip
import dev.aaa1115910.bv.entity.carddata.VideoCardData
import dev.aaa1115910.bv.tv.R
import dev.aaa1115910.bv.tv.activities.video.UpInfoActivity
import dev.aaa1115910.bv.tv.activities.video.VideoInfoActivity
import dev.aaa1115910.bv.tv.component.videocard.SmallVideoCard
import dev.aaa1115910.bv.tv.util.ProvideListBringIntoViewSpec
import dev.aaa1115910.bv.viewmodel.home.PopularViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun PopularScreen(
    modifier: Modifier = Modifier,
    lazyGridState: LazyGridState = rememberLazyGridState(),
    popularViewModel: PopularViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var currentFocusedIndex by remember { mutableIntStateOf(0) }
    val shouldLoadMore by remember {
        derivedStateOf { popularViewModel.popularVideoList.isNotEmpty() && currentFocusedIndex + 12 > popularViewModel.popularVideoList.size }
    }

    val onClickVideo: (UgcItem) -> Unit = { ugcItem ->
        VideoInfoActivity.actionStart(context, ugcItem.aid)
    }

    val onLongClickVideo: (UgcItem) -> Unit = { ugcItem ->
        UpInfoActivity.actionStart(
            context,
            mid = ugcItem.authorId,
            name = ugcItem.author,
            face = ugcItem.authorFace
        )
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            scope.launch(Dispatchers.IO) {
                popularViewModel.loadMore()
            }
        }
    }

    val padding = dimensionResource(R.dimen.grid_padding)
    val spacedBy = dimensionResource(R.dimen.grid_spacedBy)
    ProvideListBringIntoViewSpec {
        LazyVerticalGrid(
            modifier = modifier.fillMaxSize(),
            columns = GridCells.Fixed(4),
            state = lazyGridState,
            contentPadding = PaddingValues(padding),
            verticalArrangement = Arrangement.spacedBy(spacedBy),
            horizontalArrangement = Arrangement.spacedBy(spacedBy)
        ) {
            itemsIndexed(popularViewModel.popularVideoList) { index, item ->
                SmallVideoCard(
                    data = remember(item.aid) {
                        VideoCardData(
                            avid = item.aid,
                            title = item.title,
                            cover = item.cover,
                            play = item.play,
                            danmaku = item.danmaku,
                            upName = item.author,
                            time = item.duration * 1000L,
                            pubTime = item.pubTime
                        )
                    },
                    onClick = { onClickVideo(item) },
                    onLongClick = {onLongClickVideo(item) },
                    onFocus = { currentFocusedIndex = index }
                )
            }

            if (popularViewModel.loading) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        LoadingTip()
                    }
                }
            }
        }
    }
}