package dev.aaa1115910.bv.mobile.component.reply

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.origeek.imageViewer.previewer.ImagePreviewerState
import dev.aaa1115910.biliapi.entity.Picture
import dev.aaa1115910.biliapi.entity.reply.Comment
import dev.aaa1115910.biliapi.entity.reply.CommentSort
import dev.aaa1115910.bv.BuildConfig

@Composable
fun Replies(
    modifier: Modifier = Modifier,
    comment: Comment?,
    sort: CommentSort,
    replies: List<Comment>,
    onSwitchSort: (CommentSort) -> Unit,
    previewerState: ImagePreviewerState,
    onShowPreviewer: (List<Picture>, () -> Unit) -> Unit,
    repliesCount: Int
) {
    val listState = rememberLazyListState()
    LazyColumn(
        modifier = modifier.fillMaxHeight(),
        state = listState
    ) {
        if (comment != null) {
            item {
                CommentItem(
                    comment = comment,
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
                    onSwitchSort(
                        when (sort) {
                            CommentSort.Hot -> CommentSort.Time
                            CommentSort.Time -> CommentSort.Hot
                            else -> CommentSort.Hot
                        }
                    )
                }) {
                    Text(
                        text = when (sort) {
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

        item {
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}