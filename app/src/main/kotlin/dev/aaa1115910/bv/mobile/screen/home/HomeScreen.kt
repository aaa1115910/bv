package dev.aaa1115910.bv.mobile.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.aaa1115910.bv.entity.carddata.VideoCardData
import dev.aaa1115910.bv.mobile.activities.VideoPlayerActivity
import dev.aaa1115910.bv.mobile.component.home.HomeSearchTopBarCompact
import dev.aaa1115910.bv.mobile.component.home.HomeSearchTopBarExpanded
import dev.aaa1115910.bv.mobile.component.videocard.SmallVideoCard
import dev.aaa1115910.bv.viewmodel.home.PopularViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    gridState: LazyGridState,
    homeViewModel: PopularViewModel = koinViewModel(),
    windowSize: WindowWidthSizeClass,
    onSearchActiveChange: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    var searchText by remember { mutableStateOf("") }
    var activeSearch by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        homeViewModel.loadMore()
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            if (windowSize != WindowWidthSizeClass.Expanded) {
                HomeSearchTopBarCompact(
                    query = searchText,
                    active = activeSearch,
                    onQueryChange = { searchText = it },
                    onActiveChange = {
                        activeSearch = it
                        onSearchActiveChange(it)
                    },
                    onOpenNavDrawer = { scope.launch { drawerState.open() } }
                )
            }
        },
    ) { innerPadding ->
        val boxModifier = if (windowSize == WindowWidthSizeClass.Expanded)
            Modifier.padding(innerPadding) else Modifier.padding(top = innerPadding.calculateTopPadding())
        Box(
            modifier = boxModifier.fillMaxSize()
        ) {
            if (windowSize == WindowWidthSizeClass.Expanded) {
                HomeSearchTopBarExpanded(
                    query = searchText,
                    active = activeSearch,
                    onQueryChange = { searchText = it },
                    onActiveChange = { activeSearch = it }
                )
            }
            val gridTopPadding = if (windowSize == WindowWidthSizeClass.Expanded) 68.dp else 8.dp
            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Adaptive(180.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(
                    top = gridTopPadding, start = 8.dp, end = 8.dp, bottom = 8.dp
                )
            ) {

                items(homeViewModel.popularVideoList) { video ->
                    SmallVideoCard(
                        data = VideoCardData(
                            avid = video.aid,
                            title = video.title,
                            cover = video.cover,
                            play = video.play,
                            danmaku = video.danmaku,
                            upName = video.author,
                            time = video.duration * 1000L
                        ),
                        onClick = {
                            VideoPlayerActivity.actionStart(
                                context = context,
                                aid = video.aid
                            )
                        }
                    )
                }
            }
        }
    }
}
