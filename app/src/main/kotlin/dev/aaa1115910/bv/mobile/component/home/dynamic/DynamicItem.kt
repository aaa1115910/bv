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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import dev.aaa1115910.biliapi.entity.user.DynamicVideo
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.mobile.component.user.UserAvatar
import dev.aaa1115910.bv.mobile.theme.BVMobileTheme
import dev.aaa1115910.bv.util.ImageSize
import dev.aaa1115910.bv.util.notYetImplemented
import dev.aaa1115910.bv.util.resizedImageUrl

@Composable
fun DynamicItem(
    modifier: Modifier = Modifier,
    dynamicVideo: DynamicVideo,
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
                avatar = "",
                author = dynamicVideo.author,
                time = 0L
            )
            DynamicVideoContent(
                cover = dynamicVideo.cover,
                playCount = dynamicVideo.play.toString(),
                danmakuCount = dynamicVideo.danmaku.toString(),
                duration = dynamicVideo.duration.toString(),
                title = dynamicVideo.title
            )
            DynamicFooter(
                shareCount = 0,
                commentCount = 0,
                likeCount = 0,
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
    cover: String,
    playCount: String,
    danmakuCount: String,
    duration: String,
    title: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
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
                    model = cover.resizedImageUrl(ImageSize.SmallVideoCardCover),
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
                        if (playCount.isNotBlank()) {
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
                                    text = playCount,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White
                                )
                            }
                        }
                        if (danmakuCount.isNotBlank()) {
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
                                    text = danmakuCount,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White
                                )
                            }
                        }
                    }
                    Text(
                        text = duration,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                    )
                }
            }

        }
        Text(text = title)
    }
}

@Composable
fun DynamicHeader(
    modifier: Modifier = Modifier,
    avatar: String,
    author: String,
    time: Long,
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
                avatar = avatar,
                size = 48.dp
            )
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    text = author,
                    maxLines = 1
                )
                Text(
                    text = "time",
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
    shareCount: Int = 0,
    commentCount: Int = 0,
    likeCount: Int = 0,
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
            number = shareCount
        ) { onShare?.invoke() }
        DynamicFooterButton(
            icon = Icons.AutoMirrored.Filled.Comment,
            number = commentCount
        ) { onShowComment?.invoke() }
        DynamicFooterButton(
            icon = if (isLike) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            number = likeCount
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

@Preview
@Composable
private fun DynamicHeaderPreview() {
    BVMobileTheme {
        Surface {
            DynamicHeader(
                avatar = "https://i0.hdslb.com/bfs/article/b6b843d84b84a3ba5526b09ebf538cd4b4c8c3f3.jpg",
                author = "bishi",
                time = 0L
            )
        }
    }
}

@Preview
@Composable
private fun DynamicFooterPreview() {
    BVMobileTheme {
        Surface {
            DynamicFooter()
        }
    }
}

private val emptyDynamicVideoData = DynamicVideo(
    aid = 0,
    cid = 0,
    title = "title",
    cover = "",
    duration = 0,
    play = 0,
    danmaku = 0,
    author = "author"
)

@Preview
@Composable
private fun DynamicItemPreview() {
    BVMobileTheme {
        Surface {
            DynamicItem(
                modifier = Modifier.padding(vertical = 8.dp),
                dynamicVideo = emptyDynamicVideoData
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
                        dynamicVideo = emptyDynamicVideoData
                    )
                }
            }
        }
    }
}