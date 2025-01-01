package dev.aaa1115910.bv.component.controllers2

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import dev.aaa1115910.bv.component.controllers.MenuListItem
import dev.aaa1115910.bv.repository.VideoListItem
import dev.aaa1115910.bv.util.requestFocus

@Composable
fun VideoListController(
    modifier: Modifier = Modifier,
    show: Boolean,
    currentCid: Long,
    videoList: List<VideoListItem>,
    onPlayNewVideo: (VideoListItem) -> Unit
) {
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(show) {
        if (show) {
            val currentIndex = videoList.indexOfFirst { it.cid == currentCid }
            listState.animateScrollToItem(currentIndex)
            focusRequester.requestFocus(scope)
        }
    }

    Box {
        AnimatedVisibility(
            visible = show,
            enter = expandHorizontally(),
            exit = shrinkHorizontally()
        ) {
            Surface(
                modifier = modifier,
                colors = SurfaceDefaults.colors(
                    containerColor = Color.Black.copy(alpha = 0.5f)
                )
            ) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .width(300.dp)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    LazyColumn(
                        state = listState,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 120.dp)
                    ) {
                        items(items = videoList) { video ->
                            val isSelected = video.cid == currentCid
                            val itemModifier = if (isSelected) {
                                Modifier
                                    .fillMaxWidth()
                                    .focusRequester(focusRequester)
                            } else {
                                Modifier.fillMaxWidth()
                            }
                            MenuListItem(
                                modifier = itemModifier,
                                text = if (video.isEpisode) {
                                    video.title
                                } else {
                                    "P${video.index + 1} ${video.title}"
                                },
                                selected = isSelected,
                                textAlign = TextAlign.Start
                            ) {
                                if (!isSelected) onPlayNewVideo(video)
                            }
                        }
                    }
                }
            }
        }
    }
}