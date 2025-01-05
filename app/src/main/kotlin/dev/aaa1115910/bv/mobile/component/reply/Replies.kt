package dev.aaa1115910.bv.mobile.component.reply

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
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
import dev.aaa1115910.bv.BuildConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Replies(
    modifier: Modifier = Modifier,
    previewerState: ImagePreviewerState,
    rootComment: Comment?,
    replies: List<Comment>,
    replySort: CommentSort,
    repliesCount: Int,
    isLoading: Boolean,
    isRefreshing: Boolean,
    onLoadMoreReplies: () -> Unit,
    onRefreshReplies: () -> Unit,
    onSwitchReplySort: (CommentSort) -> Unit,
    onShowPreviewer: (List<Picture>, () -> Unit) -> Unit,
) {
    val listState = rememberLazyListState()
    val pullToRefreshState = rememberPullToRefreshState()

    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
                ?: return@derivedStateOf true

            lastVisibleItem.index >= listState.layoutInfo.totalItemsCount - 10
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) onLoadMoreReplies()
    }

    PullToRefreshBox(
        modifier = modifier,
        state = pullToRefreshState,
        isRefreshing = isRefreshing,
        onRefresh = onRefreshReplies,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxHeight(),
            state = listState
        ) {
            if (rootComment != null) {
                item {
                    CommentItem(
                        comment = rootComment,
                        previewerState = previewerState,
                        onShowPreviewer = onShowPreviewer,
                        showReplies = false
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "相关回复共 $repliesCount 条",
                        style = MaterialTheme.typography.titleMedium
                    )
                    TextButton(onClick = {
                        onSwitchReplySort(
                            when (replySort) {
                                CommentSort.Hot -> CommentSort.Time
                                CommentSort.Time -> CommentSort.Hot
                                else -> CommentSort.Hot
                            }
                        )
                    }) {
                        Text(
                            text = when (replySort) {
                                CommentSort.Hot -> "按热度"
                                CommentSort.Time -> "按时间"
                                else -> ""
                            }
                        )
                    }
                }
            }

            itemsIndexed(items = replies) { index, reply ->
                Box {
                    CommentItem(
                        comment = reply,
                        previewerState = previewerState,
                        onShowPreviewer = onShowPreviewer,
                        showReplies = false
                    )

                    if (BuildConfig.DEBUG) {
                        Text(text = "$index")
                    }
                }
            }

            if (replies.isEmpty() && !(isLoading || isRefreshing)) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "啥都没有")
                    }
                }
            }

            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.navigationBarsPadding())
            }
        }
    }
}