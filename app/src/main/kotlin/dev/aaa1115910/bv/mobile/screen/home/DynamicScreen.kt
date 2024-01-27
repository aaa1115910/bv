package dev.aaa1115910.bv.mobile.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.aaa1115910.biliapi.entity.user.DynamicType
import dev.aaa1115910.bv.mobile.activities.VideoPlayerActivity
import dev.aaa1115910.bv.mobile.component.home.dynamic.DynamicItem
import dev.aaa1115910.bv.util.isScrolledToEnd
import dev.aaa1115910.bv.viewmodel.home.DynamicViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DynamicScreen(
    modifier: Modifier = Modifier,
    dynamicViewModel: DynamicViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val videoListState = rememberLazyListState()
    val endOfListReached by remember { derivedStateOf { videoListState.isScrolledToEnd() } }

    LaunchedEffect(Unit) {
        dynamicViewModel.loadMoreAll()
    }

    LaunchedEffect(endOfListReached) {
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
            modifier = Modifier
                .padding(innerPadding)
                .padding(bottom = 80.dp)
        ) {
            LazyColumn(
                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant),
                state = videoListState
            ) {
                items(items = dynamicViewModel.dynamicAllList) { dynamicItem ->
                    DynamicItem(
                        modifier = Modifier.padding(bottom = 12.dp),
                        dynamicItem = dynamicItem,
                        onClick = {
                            if (dynamicItem.type ==DynamicType.Av)
                            VideoPlayerActivity.actionStart(
                                context = context,
                                aid = dynamicItem.video!!.aid,
                                fromSeason = dynamicItem.video!!.seasonId != 0
                            )
                        }
                    )
                }
            }
        }
    }
}