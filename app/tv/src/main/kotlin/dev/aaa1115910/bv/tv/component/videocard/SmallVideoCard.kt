package dev.aaa1115910.bv.tv.component.videocard

import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.tv.component.UpIcon
import dev.aaa1115910.bv.entity.carddata.VideoCardData
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.util.ImageSize
import dev.aaa1115910.bv.util.ifElse
import dev.aaa1115910.bv.util.resizedImageUrl

@Composable
fun SmallVideoCard(
    modifier: Modifier = Modifier,
    data: VideoCardData,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    onFocus: () -> Unit = {},
    initialFocus: Boolean = false
) {
    var hasFocus by remember { mutableStateOf(initialFocus) }

    // 缓存密度计算，避免重复计算
    val finalOffsetY = LocalDensity.current.run { 5.dp.toPx() }
    val infoOffsetY by animateFloatAsState(
        targetValue = if (hasFocus) finalOffsetY else 0f,
        animationSpec = tween(
            durationMillis = 120
        ),
        label = "info offset y"
    )

    Surface (
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged {
                hasFocus = it.isFocused
                if (hasFocus) onFocus()
            },
        onClick = onClick,
        onLongClick = onLongClick,
        colors = ClickableSurfaceDefaults.colors(
            containerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            pressedContainerColor = Color.Transparent
        ),
        shape = ClickableSurfaceDefaults.shape(shape = MaterialTheme.shapes.medium),
        scale = ClickableSurfaceDefaults.scale(scale = 1f, focusedScale = 1f)
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth(),
        ) {
            CardCover(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .ifElse(hasFocus, Modifier
                        .border (
                            border = BorderStroke(width = 3.dp, color = MaterialTheme.colorScheme.border),
                            shape = MaterialTheme.shapes.medium
                        )
                    ),
                cover = data.cover,
                play = data.playString,
                danmaku = data.danmakuString,
                time = data.timeString
            )
            Spacer(modifier = Modifier.height(8.dp))
            CardInfo(
                modifier = Modifier
                    .graphicsLayer {
                        translationY = infoOffsetY
                    }
                    .fillMaxWidth()
                    .height(80.dp),
                title = data.title,
                upName = data.upName,
                pubTime = data.pubTime
            )
        }
    }
}

@Composable
private fun CoverBottomInfo(
    modifier: Modifier = Modifier,
    play: String,
    danmaku: String,
    time: String
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp, 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (play.isNotBlank()) {
            Icon(
                painter = painterResource(id = R.drawable.ic_play_count),
                contentDescription = null,
                tint = Color.White
            )
            Spacer(Modifier.width(2.dp))
            Text(
                text = play,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White
            )
        }
        
        if (danmaku.isNotBlank()) {
            if (play.isNotBlank()) Spacer(Modifier.width(8.dp))
            Icon(
                painter = painterResource(id = R.drawable.ic_danmaku_count),
                contentDescription = null,
                tint = Color.White
            )
            Spacer(Modifier.width(2.dp))
            Text(
                text = danmaku,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White
            )
        }
        
        Spacer(Modifier.weight(1f))
        Text(
            text = time,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White,
            maxLines = 1
        )
    }
}

@Composable
fun CardCover(
    modifier: Modifier = Modifier,
    cover: String,
    play: String,
    danmaku: String,
    time: String
) {
    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.BottomCenter
    ) {
        val showInfo = maxWidth > 160.dp

        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.6f),
            model = cover.resizedImageUrl(ImageSize.SmallVideoCardCover),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        // 只有需要显示时才创建阴影和信息组件
        if (showInfo) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.8f)
                            )
                        )
                    )
            )
            
            CoverBottomInfo(
                play = play,
                danmaku = danmaku,
                time = time
            )
        }
    }
}

@Composable
private fun CardInfo(
    modifier: Modifier = Modifier,
    title: String,
    upName: String,
    pubTime: String?
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            modifier = Modifier,
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
            maxLines = 2,
            minLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            UpIcon()
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 2.dp),
                text = upName,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            pubTime?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Visible
                )
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SmallVideoCardWithoutFocusPreview() {
    val data = VideoCardData(
        avid = 0,
        title = "震惊！太震惊了！真的是太震惊了！我的天呐！真TMD震惊！",
        cover = "http://i2.hdslb.com/bfs/archive/af17fc07b8f735e822563cc45b7b5607a491dfff.jpg",
        upName = "震惊！太震惊了！真的是太震惊了！我的天呐！真TMD震惊！",
        play = 2333,
        danmaku = 666,
        time = 2333 * 1000,
        pubTime = "3小时前"
    )
    BVTheme {
        Surface(
            modifier = Modifier.width(300.dp)
        ) {
            SmallVideoCard(
                modifier = Modifier.padding(20.dp),
                data = data,
                initialFocus = false
            )
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SmallVideoCardWithFocusPreview() {
    val data = VideoCardData(
        avid = 0,
        title = "震惊！太震惊了！真的是太震惊了！我的天呐！真TMD震惊！",
        cover = "http://i2.hdslb.com/bfs/archive/af17fc07b8f735e822563cc45b7b5607a491dfff.jpg",
        upName = "bishi",
        play = 2333,
        danmaku = 666,
        time = 2333 * 1000,
        pubTime = "3小时前"
    )
    BVTheme {
        Surface(
            modifier = Modifier.width(300.dp)
        ) {
            SmallVideoCard(
                modifier = Modifier.padding(20.dp),
                data = data,
                initialFocus = true
            )
        }
    }
}

@Preview(device = "id:tv_1080p")
@Preview(device = "id:tv_1080p", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SmallVideoCardsPreview() {
    val data = VideoCardData(
        avid = 0,
        title = "震惊！太震惊了！真的是太震惊了！我的天呐！真TMD震惊！",
        //cover = "http://i2.hdslb.com/bfs/archive/af17fc07b8f735e822563cc45b7b5607a491dfff.jpg",
        cover = "",
        upName = "bishi",
        play = 2333,
        danmaku = 666,
        time = 2333 * 1000,
        pubTime = "3小时前"
    )
    BVTheme {
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            repeat(20) {
                item {
                    SmallVideoCard(
                        data = data
                    )
                }
            }
        }
    }
}
