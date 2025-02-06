package dev.aaa1115910.bv.mobile.component.reply

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.origeek.imageViewer.previewer.ImagePreviewerState
import com.origeek.imageViewer.previewer.TransformImageView
import com.origeek.imageViewer.previewer.TransformItemState
import com.origeek.imageViewer.previewer.rememberPreviewerState
import com.origeek.imageViewer.previewer.rememberTransformItemState
import dev.aaa1115910.biliapi.entity.Picture
import dev.aaa1115910.biliapi.entity.reply.Comment
import dev.aaa1115910.biliapi.entity.reply.EmoteSize
import dev.aaa1115910.bv.mobile.theme.BVMobileTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CommentItem(
    modifier: Modifier = Modifier,
    comment: Comment,
    previewerState: ImagePreviewerState,
    showReplies: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    onShowPreviewer: (newPictures: List<Picture>, afterSetPictures: () -> Unit) -> Unit,
    onShowReply: (rpid: Long) -> Unit = {}
) {
    Surface(
        modifier = modifier,
        color = containerColor
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .padding(end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row {
                AsyncImage(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Gray),
                    model = comment.member.avatar,
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column {
                        Text(
                            modifier = Modifier
                                .width(200.dp)
                                .basicMarquee(),
                            text = comment.member.name
                        )
                        Text(
                            text = comment.timeDesc,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    Box {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            //TODO like comment
                            /*IconButton(
                                modifier = Modifier.size(24.dp),
                                onClick = { *//*TODO*//* }
                            ) {
                                Icon(
                                    modifier = Modifier.size(20.dp),
                                    imageVector = Icons.Outlined.ThumbUpAlt,
                                    contentDescription = null
                                )
                            }
                            Text(text = "233")*/
                        }
                    }
                }
            }
            Column(
                modifier = Modifier.padding(start = 72.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CommentText(
                    content = comment.content,
                    emotes = comment.emotes
                )
                if (comment.pictures.isNotEmpty()) {
                    CommentPictures(
                        pictures = comment.pictures,
                        previewerState = previewerState,
                        onShowPreviewer = onShowPreviewer
                    )
                }
                if (showReplies && (comment.repliesCount != 0 || comment.replies.isNotEmpty())) {
                    CommentReplies(
                        replies = comment.replies,
                        repliesCount = comment.repliesCount,
                        onOpenCommentSheet = { onShowReply(comment.rpid) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CommentText(
    modifier: Modifier = Modifier,
    content: List<String>,
    emotes: List<Comment.Emote>,
    maxLines: Int = 6,
    showMoreButton: Boolean = true
) {
    val emoteNameList = emotes.map { it.text }
    val inlineContentMap = emotes.map { emote ->
        emote.text to InlineTextContent(
            Placeholder(
                width = emote.size.fontSize.sp,
                height = emote.size.fontSize.sp,
                placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
            )
        ) {
            AsyncImage(model = emote.url, contentDescription = null)
        }
    }.toMap()

    var lineCount by remember { mutableIntStateOf(0) }
    var maxLinesValue by remember { mutableIntStateOf(maxLines) }
    val currentMaxLines by animateIntAsState(targetValue = maxLinesValue, label = "text max line")
    var textMoreThan6Lines by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
    ) {
        Text(
            text = buildAnnotatedString {
                content.forEach { text ->
                    if (emoteNameList.contains(text)) {
                        appendInlineContent(text)
                    } else {
                        append(text)
                    }
                }
            },
            inlineContent = inlineContentMap,
            maxLines = currentMaxLines,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { textLayoutResult ->
                if (textLayoutResult.hasVisualOverflow) textMoreThan6Lines = true
                lineCount = textLayoutResult.lineCount
            }
        )
        if (showMoreButton && textMoreThan6Lines) {
            if (maxLinesValue == maxLines) {
                Text(
                    modifier = Modifier.clickable { maxLinesValue = 999 },
                    text = "展开",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Text(
                    modifier = Modifier.clickable { maxLinesValue = 6 },
                    text = "收起",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun CommentPictures(
    modifier: Modifier = Modifier,
    pictures: List<Picture>,
    previewerState: ImagePreviewerState,
    onShowPreviewer: (newPictures: List<Picture>, afterSetPictures: () -> Unit) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val imageBaseShape = MaterialTheme.shapes.medium

    val onClickPicture: (index: Int, itemState: TransformItemState) -> Unit = { index, itemState ->
        onShowPreviewer(pictures) {
            scope.launch {
                previewerState.openTransform(
                    index = index,
                    itemState = itemState,
                )
            }
        }
    }

    Box(
        modifier = modifier
    ) {
        when {
            pictures.size == 1 -> {
                Row {
                    val itemState = rememberTransformItemState()
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(2f),
                        color = Color.Gray,
                        shape = imageBaseShape
                    ) {
                        TransformImageView(
                            modifier = Modifier.clickable { onClickPicture(0, itemState) },
                            painter = rememberAsyncImagePainter(pictures.first().url),
                            key = pictures.first().key,
                            itemState = itemState,
                            previewerState = previewerState,
                        )
                    }
                }
            }

            pictures.size == 2 -> {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    pictures.forEachIndexed { index, picture ->
                        val itemState = rememberTransformItemState()
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f),
                            color = Color.Gray,
                            shape = when (index) {
                                0 -> imageBaseShape.copy(
                                    topEnd = CornerSize(0.dp), bottomEnd = CornerSize(0.dp)
                                )

                                1 -> imageBaseShape.copy(
                                    topStart = CornerSize(0.dp), bottomStart = CornerSize(0.dp)
                                )

                                else -> RoundedCornerShape(0.dp)
                            }
                        ) {
                            TransformImageView(
                                modifier = Modifier.clickable { onClickPicture(index, itemState) },
                                painter = rememberAsyncImagePainter(picture.url),
                                key = picture.key,
                                itemState = itemState,
                                previewerState = previewerState,
                            )
                        }
                    }
                }
            }

            pictures.size >= 3 -> {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    pictures.take(3).forEachIndexed { index, picture ->
                        val itemState = rememberTransformItemState()
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f),
                            color = Color.Gray,
                            shape = when (index) {
                                0 -> imageBaseShape.copy(
                                    topEnd = CornerSize(0.dp), bottomEnd = CornerSize(0.dp)
                                )

                                2 -> imageBaseShape.copy(
                                    topStart = CornerSize(0.dp), bottomStart = CornerSize(0.dp)
                                )

                                else -> RoundedCornerShape(0.dp)
                            }
                        ) {
                            TransformImageView(
                                modifier = Modifier.clickable { onClickPicture(index, itemState) },
                                painter = rememberAsyncImagePainter(picture.url),
                                key = picture.key,
                                itemState = itemState,
                                previewerState = previewerState,
                            )
                        }
                    }
                }

                if (pictures.size > 3) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .clip(
                                MaterialTheme.shapes.medium.copy(
                                    topEnd = CornerSize(0.dp),
                                    bottomStart = CornerSize(0.dp)
                                )
                            )
                            .background(Color.Black.copy(alpha = 0.4f))
                            .padding(horizontal = 8.dp),
                        text = "+${pictures.size - 3}",
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun CommentReplies(
    modifier: Modifier = Modifier,
    replies: List<Comment>,
    repliesCount: Int,
    onOpenCommentSheet: () -> Unit
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        onClick = onOpenCommentSheet,
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column(
            modifier = modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            replies.forEach { reply ->
                val replyContent = if (reply.content.firstOrNull()?.startsWith("回复") == true) {
                    listOf("${reply.member.name} ")
                } else {
                    listOf("${reply.member.name} : ")
                } + reply.content
                CommentText(
                    content = replyContent,
                    emotes = reply.emotes,
                    maxLines = 2,
                    showMoreButton = false
                )
            }
            if (repliesCount > replies.size) {
                Text(
                    text = "共 $repliesCount 条回复",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

private class CommentItemPreviewParameterProvider :
    PreviewParameterProvider<Comment> {
    override val values = sequenceOf(
        Comment(
            rpid = 0,
            mid = 0,
            oid = 0,
            parent = 0,
            type = 0,
            content = listOf("单行文字。你好", "[doge]", "World!"),
            member = Comment.Member(mid = 0, avatar = "", name = "username"),
            timeDesc = "4小时前",
            emotes = listOf(
                Comment.Emote(
                    text = "[doge]",
                    url = "https://i0.hdslb.com/bfs/emote/3087d273a78ccaff4bb1e9972e2ba2a7583c9f11.png",
                    size = EmoteSize.Small
                )
            ),
            pictures = emptyList(),
            replies = emptyList(),
            repliesCount = 0
        ),
        Comment(
            rpid = 0,
            mid = 0,
            oid = 0,
            parent = 0,
            type = 0,
            content = listOf("超长评论。If you were a web designer in the early days of the Internet, you might remember that there were few “web safe” typefaces, such as Arial and Georgia. As a result, many websites looked similar. To use a new typeface, you had to embed small Flash files for each heading in your layout."),
            member = Comment.Member(
                mid = 0,
                avatar = "",
                name = "超长用户名 超长用户名 超长用户名 超长用户名"
            ),
            timeDesc = "4小时前",
            emotes = listOf(
                Comment.Emote(
                    text = "[doge]",
                    url = "https://i0.hdslb.com/bfs/emote/3087d273a78ccaff4bb1e9972e2ba2a7583c9f11.png",
                    size = EmoteSize.Small
                )
            ),
            pictures = emptyList(),
            replies = emptyList(),
            repliesCount = 0
        ),
        Comment(
            rpid = 0,
            mid = 0,
            oid = 0,
            parent = 0,
            type = 0,
            content = listOf("单图片, 1 picture."),
            member = Comment.Member(mid = 0, avatar = "", name = "username"),
            timeDesc = "4小时前",
            emotes = emptyList(),
            pictures = listOf(
                Picture(
                    url = "",
                    width = 0,
                    height = 0,
                    key = ""
                )
            ),
            replies = emptyList(),
            repliesCount = 0
        ),
        Comment(
            rpid = 0,
            mid = 0,
            oid = 0,
            parent = 0,
            type = 0,
            content = listOf("双图片, 2 pictures."),
            member = Comment.Member(mid = 0, avatar = "", name = "username"),
            timeDesc = "4小时前",
            emotes = emptyList(),
            pictures = listOf(
                Picture(url = "", width = 0, height = 0, key = "1"),
                Picture(url = "", width = 0, height = 0, key = "2")
            ),
            replies = emptyList(),
            repliesCount = 0
        ),
        Comment(
            rpid = 0,
            mid = 0,
            oid = 0,
            parent = 0,
            type = 0,
            content = listOf("三图片, 3 pictures."),
            member = Comment.Member(mid = 0, avatar = "", name = "username"),
            timeDesc = "4小时前",
            emotes = emptyList(),
            pictures = listOf(
                Picture(url = "", width = 0, height = 0, key = "1"),
                Picture(url = "", width = 0, height = 0, key = "2"),
                Picture(url = "", width = 0, height = 0, key = "3")
            ),
            replies = emptyList(),
            repliesCount = 0
        ),
        Comment(
            rpid = 0,
            mid = 0,
            oid = 0,
            parent = 0,
            type = 0,
            content = listOf("四图片, four pictures."),
            member = Comment.Member(mid = 0, avatar = "", name = "username"),
            timeDesc = "4小时前",
            emotes = emptyList(),
            pictures = listOf(
                Picture(url = "", width = 0, height = 0, key = "1"),
                Picture(url = "", width = 0, height = 0, key = "2"),
                Picture(url = "", width = 0, height = 0, key = "3"),
                Picture(url = "", width = 0, height = 0, key = "4")
            ),
            replies = emptyList(),
            repliesCount = 0
        ),
        Comment(
            rpid = 0,
            mid = 0,
            oid = 0,
            parent = 0,
            type = 0,
            content = listOf("先兼容后慢慢过渡到完全自主，虽然看起来像安卓套壳，但能避免跨度太大扯到蛋。"),
            member = Comment.Member(mid = 0, avatar = "", name = "username"),
            timeDesc = "4小时前",
            emotes = emptyList(),
            pictures = listOf(
                Picture(url = "", width = 0, height = 0, key = "1"),
                Picture(url = "", width = 0, height = 0, key = "2"),
                Picture(url = "", width = 0, height = 0, key = "3"),
                Picture(url = "", width = 0, height = 0, key = "4")
            ),
            replies = listOf(
                Comment(
                    rpid = 0,
                    mid = 0,
                    oid = 0,
                    parent = 0,
                    type = 0,
                    content = listOf("其他视频的置顶：美国商务部的源文件里写的很清楚，对于消费用途的产品（consumer application）是exemption(豁免)。但是基于AD102的产品不得在中国大陆生产，也就是说未来国内销售的RTX 4090将会是在境外生产再运输回国内卖，这是唯一的不同点。估计后续也会是商家炒作显卡涨价的理由。"),
                    member = Comment.Member(mid = 0, avatar = "", name = "余Mercury"),
                    timeDesc = "4小时前",
                    emotes = emptyList(),
                    pictures = emptyList(),
                    replies = emptyList(),
                    repliesCount = 0
                ),
                Comment(
                    rpid = 0,
                    mid = 0,
                    oid = 0,
                    parent = 0,
                    type = 0,
                    content = listOf("回复 @余Mercury : 中东佬禁酒,用的泡沫水"),
                    member = Comment.Member(mid = 0, avatar = "", name = "铭轩-T"),
                    timeDesc = "4小时前",
                    emotes = emptyList(),
                    pictures = emptyList(),
                    replies = emptyList(),
                    repliesCount = 0
                ),
                Comment(
                    rpid = 0,
                    mid = 0,
                    oid = 0,
                    parent = 0,
                    type = 0,
                    content = listOf("澄清完更好笑了"),
                    member = Comment.Member(mid = 0, avatar = "", name = "Gemini好辣辣"),
                    timeDesc = "4小时前",
                    emotes = emptyList(),
                    pictures = emptyList(),
                    replies = emptyList(),
                    repliesCount = 0
                )
            ),
            repliesCount = 0
        )
    )
}

@Preview
@Composable
private fun CommentItemPreview(
    @PreviewParameter(CommentItemPreviewParameterProvider::class) comment: Comment
) {
    val previewerState = rememberPreviewerState(pageCount = { 0 })
    BVMobileTheme {
        CommentItem(
            comment = comment,
            previewerState = previewerState,
            onShowPreviewer = { _, _ -> }
        )
    }
}