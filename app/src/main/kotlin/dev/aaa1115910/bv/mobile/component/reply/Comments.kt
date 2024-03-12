package dev.aaa1115910.bv.mobile.component.reply

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.origeek.imageViewer.previewer.ImagePreviewerState
import dev.aaa1115910.biliapi.entity.Picture
import dev.aaa1115910.biliapi.entity.reply.Comment
import dev.aaa1115910.biliapi.entity.reply.CommentSort

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun Comments(
    modifier: Modifier = Modifier,
    header: (@Composable () -> Unit)? = null,
    previewerState: ImagePreviewerState,
    comments: List<Comment>,
    commentSort: CommentSort,
    refreshingComments: Boolean,
    onLoadMoreComments: () -> Unit,
    onRefreshComments: () -> Unit,
    onSwitchCommentSort: (CommentSort) -> Unit,
    onShowPreviewer: (newPictures: List<Picture>, afterSetPictures: () -> Unit) -> Unit,
    onShowReplies: (rpId: Long, repliesCount: Int) -> Unit
) {
    val listState = rememberLazyListState()
    val pullRefreshState = rememberPullRefreshState(refreshingComments, { onRefreshComments() })

    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
                ?: return@derivedStateOf true

            lastVisibleItem.index >= listState.layoutInfo.totalItemsCount - 10
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) onLoadMoreComments()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pullRefresh(state = pullRefreshState)
    ) {
        LazyColumn(
            state = listState
        ) {
            item {
                header?.invoke()
            }
            stickyHeader {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(start = 16.dp, end = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when (commentSort) {
                            CommentSort.Hot -> "热门评论"
                            CommentSort.Time -> "最新评论"
                            else -> ""
                        },
                        style = MaterialTheme.typography.titleMedium
                    )
                    TextButton(onClick = {
                        onSwitchCommentSort(
                            when (commentSort) {
                                CommentSort.Hot -> CommentSort.Time
                                CommentSort.Time -> CommentSort.Hot
                                else -> CommentSort.Hot
                            }
                        )
                    }) {
                        Text(
                            text = when (commentSort) {
                                CommentSort.Hot -> "按热度"
                                CommentSort.Time -> "按时间"
                                else -> ""
                            }
                        )
                    }
                }
            }

            itemsIndexed(items = comments) { index, comment ->
                Box {
                    CommentItem(
                        comment = comment,
                        previewerState = previewerState,
                        onShowPreviewer = onShowPreviewer,
                        onShowReply = { rpId ->
                            onShowReplies(rpId, comment.repliesCount)
                        }
                    )
                }
            }
            if (comments.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.height(400.dp)
                    ) {
                        //Text(text="Empty")
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.navigationBarsPadding())
            }
        }
        PullRefreshIndicator(
            refreshingComments,
            pullRefreshState,
            Modifier.align(Alignment.TopCenter)
        )
    }
}