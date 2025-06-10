package dev.aaa1115910.bv.player.tv.controller

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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ListItem
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import androidx.tv.material3.Text
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerConfigData
import dev.aaa1115910.bv.player.entity.VideoListItem
import dev.aaa1115910.bv.player.entity.VideoListItemData
import dev.aaa1115910.bv.player.entity.VideoListPart
import dev.aaa1115910.bv.player.entity.VideoListPgcEpisode
import dev.aaa1115910.bv.player.entity.VideoListUgcEpisode
import dev.aaa1115910.bv.player.entity.VideoListUgcEpisodeTitle
import dev.aaa1115910.bv.util.requestFocus
import kotlinx.coroutines.delay

@Composable
fun VideoListController(
    modifier: Modifier = Modifier,
    show: Boolean,
    onPlayNewVideo: (VideoListItem) -> Unit
) {
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val videoPlayerConfigData = LocalVideoPlayerConfigData.current
    val focusRequester = remember { FocusRequester() }
    val videoListContainsUgcEpisode by remember {
        derivedStateOf {
            videoPlayerConfigData.availableVideoList.any { it is VideoListUgcEpisode }
        }
    }

    Box {
        AnimatedVisibility(
            visible = show,
            enter = expandHorizontally(),
            exit = shrinkHorizontally()
        ) {
            // 在动画内容中处理滚动和焦点请求
            LaunchedEffect(Unit) {
                val currentIndex = videoPlayerConfigData.availableVideoList
                    .indexOfFirst {
                        when (it) {
                            is VideoListItemData -> it.cid == videoPlayerConfigData.currentVideoCid
                            else -> false
                        }
                    }
                listState.animateScrollToItem(currentIndex)
                focusRequester.requestFocus(scope)
            }
            Surface(
                modifier = modifier,
                colors = SurfaceDefaults.colors(
                    containerColor = Color.Black.copy(alpha = 0.5f)
                )
            ) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .width(300.dp)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    LazyColumn(
                        state = listState,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 120.dp)
                    ) {
                        items(items = videoPlayerConfigData.availableVideoList) { video ->
                            when (video) {
                                is VideoListPart -> {
                                    val isSelected =
                                        video.cid == videoPlayerConfigData.currentVideoCid
                                    val itemModifier = if (isSelected) {
                                        Modifier
                                            .fillMaxWidth()
                                            .focusRequester(focusRequester)
                                    } else {
                                        Modifier.fillMaxWidth()
                                    }
                                    ListItem(
                                        modifier = itemModifier,
                                        headlineContent = {
                                            Text(text = (" - ".takeIf { videoListContainsUgcEpisode }
                                                ?: "") + "P${video.index + 1} ${if (video.partTitle.isNotEmpty()) video.partTitle else video.title}")
                                        },
                                        onClick = { if (!isSelected) onPlayNewVideo(video) },
                                        selected = isSelected
                                    )
                                }

                                is VideoListUgcEpisode -> {
                                    val isSelected =
                                        video.cid == videoPlayerConfigData.currentVideoCid
                                    val itemModifier = if (isSelected) {
                                        Modifier
                                            .fillMaxWidth()
                                            .focusRequester(focusRequester)
                                    } else {
                                        Modifier.fillMaxWidth()
                                    }
                                    ListItem(
                                        modifier = itemModifier,
                                        headlineContent = { Text(text = "EP${video.index + 1} ${if (video.partTitle.isNotEmpty()) video.partTitle else video.title}") },
                                        onClick = { if (!isSelected) onPlayNewVideo(video) },
                                        selected = isSelected
                                    )
                                }

                                is VideoListPgcEpisode -> {
                                    val isSelected =
                                        video.cid == videoPlayerConfigData.currentVideoCid
                                    val itemModifier = if (isSelected) {
                                        Modifier
                                            .fillMaxWidth()
                                            .focusRequester(focusRequester)
                                    } else {
                                        Modifier.fillMaxWidth()
                                    }
                                    ListItem(
                                        modifier = itemModifier,
                                        headlineContent = { Text(text = video.partTitle) },
                                        onClick = { if (!isSelected) onPlayNewVideo(video) },
                                        selected = isSelected
                                    )
                                }

                                is VideoListUgcEpisodeTitle -> {
                                    Text(
                                        modifier = Modifier.padding(
                                            horizontal = 16.dp,
                                            vertical = 12.dp
                                        ),
                                        text = "EP${video.index + 1} ${video.title}",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}