package dev.aaa1115910.bv.component

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Carousel
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import coil.compose.AsyncImage
import dev.aaa1115910.biliapi.entity.CarouselData
import dev.aaa1115910.bv.activities.video.SeasonInfoActivity
import dev.aaa1115910.bv.activities.video.VideoInfoActivity
import dev.aaa1115910.bv.entity.proxy.ProxyArea
import dev.aaa1115910.bv.util.focusedBorder

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun PgcCarousel(
    modifier: Modifier = Modifier,
    data: List<CarouselData.CarouselItem>
) {
    val context = LocalContext.current

    CarouselContent(
        modifier = modifier,
        data = data,
        onClick = { item ->
            SeasonInfoActivity.actionStart(
                context = context,
                epId = item.episodeId,
                seasonId = item.seasonId,
                proxyArea = ProxyArea.checkProxyArea(item.title)
            )
        }
    )
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun UgcCarousel(
    modifier: Modifier = Modifier,
    data: List<CarouselData.CarouselItem>
) {
    val context = LocalContext.current

    CarouselContent(
        modifier = modifier,
        data = data,
        onClick = { item ->
            VideoInfoActivity.actionStart(
                context = context,
                aid = item.avid!!
            )
        }
    )
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun CarouselContent(
    modifier: Modifier = Modifier,
    data: List<CarouselData.CarouselItem>,
    onClick: (CarouselData.CarouselItem) -> Unit
) {
    Carousel(
        itemCount = data.size,
        modifier = modifier
            .height(240.dp)
            .clip(MaterialTheme.shapes.large)
            .focusedBorder(),
        contentTransformEndToStart =
        fadeIn(tween(1000)).togetherWith(fadeOut(tween(1000))),
        contentTransformStartToEnd =
        fadeIn(tween(1000)).togetherWith(fadeOut(tween(1000)))
    ) { itemIndex ->
        CarouselCard(
            data = data[itemIndex],
            onClick = { onClick(data[itemIndex]) }
        )
    }
}

@Composable
fun CarouselCard(
    modifier: Modifier = Modifier,
    data: CarouselData.CarouselItem,
    onClick: () -> Unit = {}
) {
    AsyncImage(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .clickable { onClick() },
        model = data.cover,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        alignment = Alignment.TopCenter
    )
}