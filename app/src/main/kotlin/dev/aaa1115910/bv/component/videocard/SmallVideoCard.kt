package dev.aaa1115910.bv.component.videocard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.component.UpIcon
import dev.aaa1115910.bv.entity.carddata.VideoCardData
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.util.ImageSize
import dev.aaa1115910.bv.util.resizedImageUrl

@Composable
fun SmallVideoCard(
    modifier: Modifier = Modifier,
    data: VideoCardData,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    onFocus: () -> Unit = {}
) {
    var hasFocus by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    SmallVideoCardContent(
        modifier = modifier
            .onFocusChanged {
                hasFocus = it.isFocused
                if (hasFocus) onFocus()
            },
        data = data,
        hasFocus = hasFocus,
        interactionSource = interactionSource,
        onClick = onClick,
        onLongClick = onLongClick,
        onFocusChanged = {
            hasFocus = it
            if (it) onFocus()
        }
    )
}

@Composable
fun SmallVideoCardContent(
    modifier: Modifier = Modifier,
    data: VideoCardData,
    hasFocus: Boolean,
    interactionSource: MutableInteractionSource? = null,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    onFocusChanged: (Boolean) -> Unit = {}
) {
    /*val infoScale by animateFloatAsState(
        targetValue = if (hasFocus) 1.05f else 1f,
        animationSpec = spring(),
        label = "info scale"
    )*/
    val infoOffsetY by animateDpAsState(
        targetValue = if (hasFocus) 8.dp else 0.dp,
        animationSpec = spring(),
        label = "info offset y"
    )

    Column(
        modifier = modifier
    ) {
        Card(
            onClick = onClick,
            onLongClick = onLongClick,
            colors = CardDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surface,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                pressedContainerColor = MaterialTheme.colorScheme.surface
            ),
            shape = CardDefaults.shape(shape = MaterialTheme.shapes.large),
            border = CardDefaults.border(
                focusedBorder = Border(
                    border = BorderStroke(width = 3.dp, color = MaterialTheme.colorScheme.border),
                    shape = MaterialTheme.shapes.large
                )
            )
        ) {
            CardCover(
                cover = data.cover,
                play = data.playString,
                danmaku = data.danmakuString,
                time = data.timeString
            )
        }

        CardInfo(
            modifier = Modifier
                //.scale(infoScale)
                .offset(y = infoOffsetY),
            title = data.title,
            upName = data.upName
        )
    }
}

@Composable
private fun PlayText(
    modifier: Modifier = Modifier,
    text: String
) {
    if (text.isNotBlank()) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Icon(
                modifier = Modifier,
                painter = painterResource(id = R.drawable.ic_play_count),
                contentDescription = null
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White
            )
        }
    }
}

@Composable
private fun DanmakuText(
    modifier: Modifier = Modifier,
    text: String
) {
    if (text.isNotBlank()) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Icon(
                modifier = Modifier,
                painter = painterResource(id = R.drawable.ic_danmaku_count),
                contentDescription = null
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White
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
            .padding(12.dp, 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PlayText(text = play)
            DanmakuText(text = danmaku)
        }
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
    var width by remember { mutableStateOf(200.dp) }
    val showInfo by remember { derivedStateOf { width > 160.dp } }

    BoxWithConstraints(
        modifier = modifier.clip(MaterialTheme.shapes.large),
        contentAlignment = Alignment.BottomCenter
    ) {
        val boxWithConstraintsScope = this
        width = boxWithConstraintsScope.maxWidth
        val shadowAlpha by animateFloatAsState(
            targetValue = if (showInfo) 0.8f else 0f,
            label = "shadow alpha"
        )

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
                            Color.Black.copy(alpha = shadowAlpha)
                        )
                    )
                )
        )
        AnimatedVisibility(
            visible = showInfo,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
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
    upName: String
) {
    Column(
        modifier = modifier.padding(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            UpIcon()
            Text(
                text = upName,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview
@Composable
fun SmallVideoCardWithoutFocusPreview() {
    val data = VideoCardData(
        avid = 0,
        title = "震惊！太震惊了！真的是太震惊了！我的天呐！真TMD震惊！",
        cover = "http://i2.hdslb.com/bfs/archive/af17fc07b8f735e822563cc45b7b5607a491dfff.jpg",
        upName = "bishi",
        play = 2333,
        danmaku = 666,
        time = 2333 * 1000
    )
    BVTheme {
        Surface(
            modifier = Modifier.width(300.dp)
        ) {
            SmallVideoCardContent(
                modifier = Modifier.padding(20.dp),
                data = data,
                hasFocus = false
            )
        }
    }
}

@Preview
@Composable
fun SmallVideoCardWithFocusPreview() {
    val data = VideoCardData(
        avid = 0,
        title = "震惊！太震惊了！真的是太震惊了！我的天呐！真TMD震惊！",
        cover = "http://i2.hdslb.com/bfs/archive/af17fc07b8f735e822563cc45b7b5607a491dfff.jpg",
        upName = "bishi",
        play = 2333,
        danmaku = 666,
        time = 2333 * 1000
    )
    BVTheme {
        Surface(
            modifier = Modifier.width(300.dp)
        ) {
            SmallVideoCardContent(
                modifier = Modifier.padding(20.dp),
                data = data,
                hasFocus = true
            )
        }
    }
}

@Preview(device = "id:tv_1080p")
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
        time = 2333 * 1000
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

@Preview
@Composable
private fun DanmakuTextPreview() {
    BVTheme {
        DanmakuText(text = "233")
    }
}

@Preview
@Composable
private fun PlayTextPreview() {
    BVTheme {
        PlayText(text = "233")
    }
}
