package dev.aaa1115910.bv.mobile.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.aaa1115910.bv.entity.carddata.VideoCardData
import dev.aaa1115910.bv.mobile.componment.videocard.SmallVideoCard
import dev.aaa1115910.bv.viewmodel.home.PopularViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun PopularScreen(
    modifier: Modifier = Modifier,
    gridState: LazyGridState,
    homeViewModel: PopularViewModel = koinViewModel()
) {
    LaunchedEffect(Unit) {
        homeViewModel.loadMore()
    }

    LazyVerticalGrid(
        modifier = modifier.fillMaxSize(),
        state = gridState,
        columns = GridCells.Adaptive(180.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(8.dp)
    ) {

        items(homeViewModel.popularVideoList) { video ->
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
                onClick = { },
            )
        }
    }
}

