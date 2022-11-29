package dev.aaa1115910.bv.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.grid.TvGridCells
import androidx.tv.foundation.lazy.grid.TvGridItemSpan
import androidx.tv.foundation.lazy.grid.TvLazyVerticalGrid
import androidx.tv.foundation.lazy.grid.itemsIndexed
import dev.aaa1115910.bv.VideoInfoActivity
import dev.aaa1115910.bv.component.VideoCard
import dev.aaa1115910.bv.viewmodel.home.PopularViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun PopularScreen(
    modifier: Modifier = Modifier,
    popularViewModel: PopularViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    TvLazyVerticalGrid(
        modifier = modifier,
        columns = TvGridCells.Fixed(4),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        /*item(
            span = { TvGridItemSpan(4) }
        ) {
            HomeCarousel()
        }*/
        itemsIndexed(popularViewModel.popularVideoList) { index, video ->
            Box(
                contentAlignment = Alignment.Center
            ) {
                VideoCard(
                    video = video,
                    onClick = {
                        VideoInfoActivity.actionStart(context, video.aid)
                    },
                    onFocus = {
                        if (index + 12 > popularViewModel.popularVideoList.size) {
                            scope.launch(Dispatchers.Default) { popularViewModel.loadMore() }

                        }
                    }
                )
            }
        }
        if (popularViewModel.loading)
            item(
                span = { TvGridItemSpan(4) }
            ) {
                Text(text = "Loading")
            }
    }
}