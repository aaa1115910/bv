package dev.aaa1115910.bv.mobile.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.origeek.imageViewer.previewer.ImagePreviewerState
import dev.aaa1115910.biliapi.entity.Picture
import dev.aaa1115910.biliapi.entity.user.DynamicItem
import dev.aaa1115910.biliapi.entity.user.DynamicType
import dev.aaa1115910.bv.mobile.activities.DynamicDetailActivity
import dev.aaa1115910.bv.mobile.component.home.dynamic.DynamicItem
import dev.aaa1115910.bv.player.mobile.util.ifElse
import dev.aaa1115910.bv.util.getLane
import dev.aaa1115910.bv.util.isScrolledToEnd
import dev.aaa1115910.bv.viewmodel.home.DynamicViewModel
import org.koin.androidx.compose.navigation.koinNavViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DynamicScreen(
    modifier: Modifier = Modifier,
    dynamicViewModel: DynamicViewModel = koinNavViewModel(),
    previewerState: ImagePreviewerState,
    onShowPreviewer: (newPictures: List<Picture>, afterSetPictures: () -> Unit) -> Unit
) {
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val dynamicGridState = rememberLazyStaggeredGridState()
    val endOfGridReached by remember { derivedStateOf { dynamicGridState.isScrolledToEnd() } }
    val lane by remember { derivedStateOf { dynamicGridState.getLane() } }

    val onClickDynamicItem: (DynamicItem) -> Unit = { dynamicItem ->
        DynamicDetailActivity.actionStart(context, dynamicItem.id)

        /*if (dynamicItem.type == DynamicType.Av)
            VideoPlayerActivity.actionStart(
                context = context,
                aid = dynamicItem.video!!.aid,
                fromSeason = dynamicItem.video!!.seasonId != 0
            )*/
    }

    LaunchedEffect(Unit) {
        dynamicViewModel.loadMoreAll()
    }

    LaunchedEffect(endOfGridReached) {
        dynamicViewModel.loadMoreAll()
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text(text = "Dynamic") },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(top = innerPadding.calculateTopPadding())
        ) {
            LazyVerticalStaggeredGrid(
                modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
                columns = StaggeredGridCells.Adaptive(300.dp),
                state = dynamicGridState,
                verticalItemSpacing = 8.dp,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(if (lane == 1) 0.dp else 8.dp)
            ) {
                items(items = dynamicViewModel.dynamicAllList) { dynamicItem ->
                    DynamicItem(
                        modifier = Modifier
                            .ifElse(lane != 1, Modifier.clip(MaterialTheme.shapes.medium)),
                        dynamicItem = dynamicItem,
                        previewerState = previewerState,
                        onShowPreviewer = onShowPreviewer,
                        onClick = onClickDynamicItem
                    )
                }
            }
        }
    }
}

private val exampleAuthorData = DynamicItem.DynamicAuthorModule(
    author = "author",
    avatar = "",
    mid = 0,
    pubTime = "54 分钟前 投稿了视频",
    pubAction = ""
)

private val exampleFooterData = DynamicItem.DynamicFooterModule(
    like = 2,
    comment = 61,
    share = 8,
)

private val exampleVideoData = DynamicItem.DynamicVideoModule(
    aid = 0,
    title = "title",
    cover = "",
    duration = "23:45",
    play = "xx play",
    danmaku = "xx dm",
    seasonId = 0,
    cid = 0,
    text = "desc"
)

private val exampleDynamicItemData = DynamicItem(
    type = DynamicType.Av,
    author = exampleAuthorData,
    video = exampleVideoData,
    footer = exampleFooterData
)
