package dev.aaa1115910.bv.screen.home

import android.view.KeyEvent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.grid.TvGridCells
import androidx.tv.foundation.lazy.grid.TvGridItemSpan
import androidx.tv.foundation.lazy.grid.TvLazyGridState
import androidx.tv.foundation.lazy.grid.TvLazyVerticalGrid
import androidx.tv.foundation.lazy.grid.itemsIndexed
import dev.aaa1115910.bv.activities.video.VideoInfoActivity
import dev.aaa1115910.bv.component.LoadingTip
import dev.aaa1115910.bv.component.videocard.SmallVideoCard
import dev.aaa1115910.bv.entity.carddata.VideoCardData
import dev.aaa1115910.bv.viewmodel.home.PopularViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun PopularScreen(
    modifier: Modifier = Modifier,
    tvLazyGridState: TvLazyGridState,
    onBackNav: () -> Unit,
    popularViewModel: PopularViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    TvLazyVerticalGrid(
        modifier = modifier
            .onPreviewKeyEvent {
                when (it.nativeKeyEvent.keyCode) {
                    KeyEvent.KEYCODE_BACK -> {
                        if (it.nativeKeyEvent.action == KeyEvent.ACTION_UP) {
                            scope.launch(Dispatchers.Main) {
                                tvLazyGridState.animateScrollToItem(0)
                            }
                            onBackNav()
                        }
                        return@onPreviewKeyEvent true
                    }
                }
                return@onPreviewKeyEvent false
            },
        state = tvLazyGridState,
        columns = TvGridCells.Fixed(4),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
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
                SmallVideoCard(
                    data = VideoCardData(
                        avid = video.aid,
                        title = video.title,
                        cover = video.pic,
                        play = video.stat.view,
                        danmaku = video.stat.danmaku,
                        upName = video.owner.name,
                        time = video.duration * 1000L
                    ),
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
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingTip()
                }
            }
    }
}