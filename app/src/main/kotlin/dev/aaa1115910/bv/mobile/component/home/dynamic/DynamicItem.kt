package dev.aaa1115910.bv.mobile.component.home.dynamic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
import dev.aaa1115910.biliapi.entity.user.DynamicItem
import dev.aaa1115910.biliapi.entity.user.DynamicType
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.mobile.component.user.UserAvatar
import dev.aaa1115910.bv.mobile.theme.BVMobileTheme
import dev.aaa1115910.bv.util.ImageSize
import dev.aaa1115910.bv.util.notYetImplemented
import dev.aaa1115910.bv.util.resizedImageUrl
import kotlinx.coroutines.launch

@Composable
fun DynamicItem(
    modifier: Modifier = Modifier,
    dynamicItem: DynamicItem,
    previewerState: ImagePreviewerState = rememberPreviewerState(pageCount = { 0 }),
    onShowPreviewer: (newPictures: List<String>, afterSetPictures: () -> Unit) -> Unit = { _, _ -> },
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DynamicHeader(
                author = dynamicItem.author
            )
            when (dynamicItem.type) {
                DynamicType.Av -> DynamicVideoContent(
                    video = dynamicItem.video!!
                )

                DynamicType.UgcSeason -> TODO()
                DynamicType.Forward -> TODO()
                DynamicType.Word -> DynamicWord(
                    word = dynamicItem.word!!
                )

                DynamicType.Draw -> DynamicDraw(
                    draw = dynamicItem.draw!!,
                    previewerState = previewerState,
                    onShowPreviewer = onShowPreviewer
                )
            }

            DynamicFooter(
                footer = dynamicItem.footer,
                isLike = false,
                onShare = {
                    //TODO 动态分享按钮
                    notYetImplemented()
                },
                onShowComment = {
                    //TODO 动态查看评论按钮
                    notYetImplemented()
                },
                onLike = {
                    //TODO 动态点赞按钮
                    notYetImplemented()
                }
            )
        }
    }
}

@Composable
fun DynamicVideoContent(
    modifier: Modifier = Modifier,
    video: DynamicItem.DynamicVideoModule
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (video.text.isNotBlank()) {
            Text(text = video.text)
        }
        Card(
            modifier = modifier
                .fillMaxWidth()
                .aspectRatio(1.6f)
        ) {
            Box(
                contentAlignment = Alignment.BottomCenter
            ) {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.6f)
                        .clip(MaterialTheme.shapes.large),
                    model = video.cover.resizedImageUrl(ImageSize.SmallVideoCardCover),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.3f)
                                )
                            )
                        )
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp, 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (video.play.isNotBlank()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(
                                    modifier = Modifier,
                                    painter = painterResource(id = R.drawable.ic_play_count),
                                    contentDescription = null,
                                    tint = Color.White
                                )
                                Text(
                                    text = video.play,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White
                                )
                            }
                        }
                        if (video.danmaku.isNotBlank()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(
                                    modifier = Modifier,
                                    painter = painterResource(id = R.drawable.ic_danmaku_count),
                                    contentDescription = null,
                                    tint = Color.White
                                )
                                Text(
                                    text = video.danmaku,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White
                                )
                            }
                        }
                    }
                    Text(
                        text = video.duration,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                    )
                }
            }

        }
        Text(text = video.title)
    }
}

@Composable
fun DynamicHeader(
    modifier: Modifier = Modifier,
    author: DynamicItem.DynamicAuthorModule
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxWidth()
                .padding(end = 30.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            UserAvatar(
                avatar = author.avatar,
                size = 48.dp
            )
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    text = author.author,
                    maxLines = 1
                )
                Text(
                    text = author.pubTime + " ${author.pubAction}",
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onSurface.copy(0.8f),
                    fontSize = 14.sp,
                    lineHeight = 14.sp
                )
            }
        }

        IconButton(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(30.dp),
            onClick = {
                //TODO 动态右上角按钮
                notYetImplemented()
            }
        ) {
            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Menu")
        }
    }

}

@Composable
fun DynamicFooter(
    modifier: Modifier = Modifier,
    footer: DynamicItem.DynamicFooterModule,
    isLike: Boolean = false,
    onShare: (() -> Unit)? = null,
    onShowComment: (() -> Unit)? = null,
    onLike: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp),
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        DynamicFooterButton(
            icon = Icons.Default.Share,
            number = footer.share
        ) { onShare?.invoke() }
        DynamicFooterButton(
            icon = Icons.AutoMirrored.Filled.Comment,
            number = footer.comment
        ) { onShowComment?.invoke() }
        DynamicFooterButton(
            icon = if (isLike) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            number = footer.like
        ) { onLike?.invoke() }
    }
}

@Composable
fun DynamicFooterButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    number: Int,
    onClick: () -> Unit
) {
    TextButton(
        modifier = modifier,
        onClick = onClick
    ) {
        Icon(
            modifier = Modifier.size(16.dp),
            imageVector = icon,
            contentDescription = null
        )
        Text(
            text = number.toString(),
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

//TODO 富文本
@Composable
fun DynamicDraw(
    modifier: Modifier = Modifier,
    draw: DynamicItem.DynamicDrawModule,
    previewerState: ImagePreviewerState,
    onShowPreviewer: (newPictures: List<String>, afterSetPictures: () -> Unit) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = draw.text)
        DynamicPictures(
            pictures = draw.images,
            previewerState = previewerState,
            onShowPreviewer = onShowPreviewer
        )
    }
}


@Composable
fun DynamicPictures(
    modifier: Modifier = Modifier,
    pictures: List<String>,
    previewerState: ImagePreviewerState,
    onShowPreviewer: (newPictures: List<String>, afterSetPictures: () -> Unit) -> Unit,
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
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(2f),
                        shape = imageBaseShape,
                        onClick = {
                            onClickPicture(0, itemState)
                        }
                    ) {
                        TransformImageView(
                            painter = rememberAsyncImagePainter(pictures.first()),
                            key = pictures.first(),
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
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f),
                            shape = when (index) {
                                0 -> imageBaseShape.copy(
                                    topEnd = CornerSize(0.dp), bottomEnd = CornerSize(0.dp)
                                )

                                1 -> imageBaseShape.copy(
                                    topStart = CornerSize(0.dp), bottomStart = CornerSize(0.dp)
                                )

                                else -> RoundedCornerShape(0.dp)
                            },
                            onClick = {
                                onClickPicture(index, itemState)
                            }
                        ) {
                            TransformImageView(
                                painter = rememberAsyncImagePainter(picture),
                                key = picture,
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
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f),
                            shape = when (index) {
                                0 -> imageBaseShape.copy(
                                    topEnd = CornerSize(0.dp), bottomEnd = CornerSize(0.dp)
                                )

                                2 -> imageBaseShape.copy(
                                    topStart = CornerSize(0.dp), bottomStart = CornerSize(0.dp)
                                )

                                else -> RoundedCornerShape(0.dp)
                            },
                            onClick = {
                                onClickPicture(index, itemState)
                            }
                        ) {
                            TransformImageView(
                                painter = rememberAsyncImagePainter(picture),
                                key = picture,
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
                            .background(Color.Black.copy(alpha = 0.2f))
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
fun DynamicWord(
    modifier: Modifier = Modifier,
    word: DynamicItem.DynamicWordModule
) {
    Text(
        modifier = modifier,
        text = word.text
    )
}

@Preview
@Composable
private fun DynamicHeaderPreview() {
    BVMobileTheme {
        Surface {
            DynamicHeader(
                author = emptyDynamicVideoData.author
            )
        }
    }
}

@Preview
@Composable
private fun DynamicFooterPreview() {
    BVMobileTheme {
        Surface {
            DynamicFooter(
                footer = emptyDynamicVideoData.footer
            )
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

private val emptyDynamicData = DynamicItem(
    type = DynamicType.Av,
    author = exampleAuthorData,
    footer = exampleFooterData
)

private val emptyDynamicVideoData = DynamicItem(
    type = DynamicType.Av,
    author = exampleAuthorData,
    video = exampleVideoData,
    footer = exampleFooterData
)

private val emptyDynamicDrawData = DynamicItem(
    type = DynamicType.Draw,
    author = exampleAuthorData,
    draw = DynamicItem.DynamicDrawModule(
        text = "draw",
        images = emptyList()
    ),
    footer = exampleFooterData
)

@Preview
@Composable
private fun DynamicVideoItemPreview() {
    BVMobileTheme {
        Surface {
            DynamicItem(
                modifier = Modifier.padding(vertical = 8.dp),
                dynamicItem = emptyDynamicVideoData
            )
        }
    }
}

private class DynamicDrawItemProvider : PreviewParameterProvider<DynamicItem> {
    override val values = List(5) { index ->
        emptyDynamicData.copy(
            type = DynamicType.Draw,
            draw = DynamicItem.DynamicDrawModule(
                text = "this is $index picture draw",
                images = Array(index) { "" }.toList()
            )
        )
    }.asSequence()
}

@Preview
@Composable
private fun DynamicDrawItemPreview(@PreviewParameter(DynamicDrawItemProvider::class) dynamicItem: DynamicItem) {
    BVMobileTheme {
        Surface {
            DynamicItem(
                modifier = Modifier.padding(vertical = 8.dp),
                dynamicItem = dynamicItem
            )
        }
    }
}

@Preview
@Composable
private fun DynamicItemListPreview() {
    BVMobileTheme {
        Surface {
            LazyColumn(
                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                items(3) {
                    DynamicItem(
                        modifier = Modifier.padding(bottom = 8.dp),
                        dynamicItem = emptyDynamicVideoData
                    )
                }
            }
        }
    }
}