package dev.aaa1115910.bv.screen.home

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.material3.Carousel
import androidx.tv.material3.ExperimentalTvMaterial3Api
import coil.compose.AsyncImage
import dev.aaa1115910.biliapi.BiliApi
import dev.aaa1115910.biliapi.entity.anime.AnimeHomepageDataType
import dev.aaa1115910.biliapi.entity.anime.CarouselItem
import dev.aaa1115910.bv.activities.video.SeasonInfoActivity
import dev.aaa1115910.bv.util.focusedBorder
import dev.aaa1115910.bv.util.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AnimeScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val carouselItems = remember { mutableStateListOf<CarouselItem>() }

    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.Default) {
            runCatching {
                carouselItems.clear()
                carouselItems.addAll(
                    BiliApi.getAnimeHomepageData(
                        dataType = AnimeHomepageDataType.V2
                    )?.getCarouselItems() ?: emptyList()
                )
            }.onFailure {
                withContext(Dispatchers.Main) {
                    it.stackTraceToString().toast(context)
                }
            }
        }
    }

    TvLazyColumn {
        item {
            AnimeCarousel(
                modifier = Modifier.padding(48.dp, 0.dp),
                data = carouselItems
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun AnimeCarousel(
    modifier: Modifier = Modifier,
    data: List<CarouselItem>
) {
    val context = LocalContext.current

    Carousel(
        slideCount = data.size,
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(MaterialTheme.shapes.large)
            .focusedBorder(),
    ) { itemIndex ->
        CarouselItem(
            modifier = Modifier
                .clickable {
                    SeasonInfoActivity.actionStart(
                        context = context,
                        epId = data[itemIndex].episodeId,
                        seasonId = data[itemIndex].seasonId
                    )
                }
        ) {
            AnimeCarouselCard(
                data = data[itemIndex]
            )
        }
    }
}

@Composable
fun AnimeCarouselCard(
    modifier: Modifier = Modifier,
    data: CarouselItem
) {
    AsyncImage(
        modifier = modifier.fillMaxWidth(),
        model = data.cover,
        contentDescription = null,
        contentScale = ContentScale.FillWidth,
        alignment = Alignment.TopCenter
    )
}