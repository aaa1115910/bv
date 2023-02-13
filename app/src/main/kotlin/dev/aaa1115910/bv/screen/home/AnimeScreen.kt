package dev.aaa1115910.bv.screen.home

import android.content.Intent
import android.view.KeyEvent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.TvLazyRow
import androidx.tv.foundation.lazy.list.itemsIndexed
import androidx.tv.material3.Carousel
import androidx.tv.material3.ExperimentalTvMaterial3Api
import coil.compose.AsyncImage
import dev.aaa1115910.biliapi.BiliApi
import dev.aaa1115910.biliapi.entity.anime.AnimeFeedData
import dev.aaa1115910.biliapi.entity.anime.AnimeHomepageDataType
import dev.aaa1115910.biliapi.entity.anime.CarouselItem
import dev.aaa1115910.biliapi.entity.web.Hover
import dev.aaa1115910.bv.activities.anime.AnimeTimelineActivity
import dev.aaa1115910.bv.activities.video.SeasonInfoActivity
import dev.aaa1115910.bv.component.videocard.SeasonCard
import dev.aaa1115910.bv.entity.carddata.SeasonCardData
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.util.ImageSize
import dev.aaa1115910.bv.util.focusedBorder
import dev.aaa1115910.bv.util.focusedScale
import dev.aaa1115910.bv.util.resizedImageUrl
import dev.aaa1115910.bv.util.toast
import dev.aaa1115910.bv.viewmodel.home.AnimeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel

@Composable
fun AnimeScreen(
    modifier: Modifier = Modifier,
    animeViewModel: AnimeViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val carouselItems = remember { mutableStateListOf<CarouselItem>() }
    val animeFeeds = animeViewModel.feedItems

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

    TvLazyColumn(
        modifier = modifier
    ) {
        item {
            AnimeCarousel(
                modifier = Modifier.padding(48.dp, 0.dp),
                data = carouselItems
            )
        }
        item {
            Buttons(
                modifier = Modifier.padding(48.dp, 24.dp),
                onOpenTimeline = {
                    context.startActivity(Intent(context, AnimeTimelineActivity::class.java))
                },
                onOpenFollowing = {},
                onOpenIndex = {}
            )
        }
        itemsIndexed(items = animeFeeds) { index, feedItems ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged {
                        if (it.hasFocus) {
                            if (index + 10 > animeFeeds.size) {
                                animeViewModel.loadMore()
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                when (feedItems.firstOrNull()?.cardStyle) {
                    "v_card" -> AnimeFeedVideoRow(
                        modifier = modifier,
                        data = feedItems
                    )

                    "rank" -> AnimeFeedRankRow(
                        modifier = modifier,
                        data = feedItems
                    )
                }
            }
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

@Composable
private fun Buttons(
    modifier: Modifier = Modifier,
    onOpenTimeline: () -> Unit,
    onOpenFollowing: () -> Unit,
    onOpenIndex: () -> Unit
) {
    Row(
        modifier = modifier
            .height(100.dp)
    ) {
        Surface(
            modifier = Modifier
                .focusedBorder()
                .focusedScale()
                .weight(1f)
                .clickable { onOpenTimeline() },
            shape = MaterialTheme.shapes.large
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Timeline")
            }
        }
        Surface(
            modifier = Modifier
                .focusedBorder()
                .focusedScale()
                .weight(1f)
                .clickable { onOpenFollowing() },
            shape = MaterialTheme.shapes.large
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Following")
            }
        }
        Surface(
            modifier = Modifier
                .focusedBorder()
                .focusedScale()
                .weight(1f)
                .clickable { onOpenIndex() },
            shape = MaterialTheme.shapes.large
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Index")
            }
        }
        Surface(
            modifier = Modifier
                .focusedBorder()
                .focusedScale()
                .weight(1f)
                .clickable { },
            shape = MaterialTheme.shapes.large
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Unknown")
            }
        }
    }
}

@Composable
fun AnimeFeedVideoRow(
    modifier: Modifier = Modifier,
    data: List<AnimeFeedData.FeedItem.FeedSubItem>
) {
    TvLazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 24.dp)
    ) {
        data.forEachIndexed { index, feedItem ->
            val cardModifier = if (index == data.lastIndex) {
                Modifier.onPreviewKeyEvent {
                    when (it.nativeKeyEvent.keyCode) {
                        KeyEvent.KEYCODE_DPAD_RIGHT -> return@onPreviewKeyEvent true
                    }
                    false
                }
            } else {
                Modifier
            }

            item {
                SeasonCard(
                    modifier = cardModifier,
                    coverHeight = 200.dp,
                    data = SeasonCardData(
                        seasonId = feedItem.seasonId ?: 0,
                        title = feedItem.title,
                        cover = feedItem.cover.resizedImageUrl(ImageSize.SeasonCoverThumb)
                    )
                )
            }
        }
    }
}

@Composable
fun AnimeFeedRankRow(
    modifier: Modifier = Modifier,
    data: List<AnimeFeedData.FeedItem.FeedSubItem>
) {
    Box(
        modifier = modifier
            .height(300.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            // light theme color: Color(250, 222, 214)
                            Color(20, 18, 17, 255),
                            Color(20, 18, 17, 255).copy(alpha = 0.298f)
                        )
                    )
                )
        ) {}
        BoxWithConstraints() {
            AsyncImage(
                modifier = Modifier
                    .fillMaxHeight()
                    .offset(x = (-1 * (0.25 * 1.6 * this.maxHeight.value)).dp)
                    .graphicsLayer { alpha = 0.99f }
                    .drawWithContent {
                        val colors = listOf(
                            Color.Black,
                            Color.Transparent
                        )
                        drawContent()
                        drawRect(
                            brush = Brush.horizontalGradient(colors),
                            blendMode = BlendMode.DstIn
                        )
                        drawRect(
                            brush = Brush.verticalGradient(colors),
                            blendMode = BlendMode.DstIn
                        )
                    },
                model = data.first().cover,
                contentDescription = null,
                contentScale = ContentScale.FillHeight,
                alpha = 1f
            )
        }
        Row(
            modifier = Modifier
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(240.dp)
                    .padding(32.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = data.first().title,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
                Text(
                    text = data.first().subTitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }

            TvLazyRow(
                modifier = modifier,
                contentPadding = PaddingValues(end = 32.dp)
            ) {
                data.first().subItems?.forEachIndexed { index, feedItem ->
                    val cardModifier = if (index == data.first().subItems?.lastIndex) {
                        Modifier.onPreviewKeyEvent {
                            when (it.nativeKeyEvent.keyCode) {
                                KeyEvent.KEYCODE_DPAD_RIGHT -> return@onPreviewKeyEvent true
                            }
                            false
                        }
                    } else {
                        Modifier
                    }

                    item {
                        SeasonCard(
                            modifier = cardModifier,
                            coverHeight = 200.dp,
                            data = SeasonCardData(
                                seasonId = feedItem.seasonId ?: 0,
                                title = feedItem.title,
                                cover = feedItem.cover.resizedImageUrl(ImageSize.SeasonCoverThumb)
                            )
                        )
                    }
                }
            }
        }
    }
}

@Preview(device = "id:tv_1080p")
@Composable
fun AnimeFeedRankRowPreview() {
    val data = listOf(
        AnimeFeedData.FeedItem.FeedSubItem(
            cardStyle = "rank",
            rankId = 126,
            cover = "http://i0.hdslb.com/bfs/archive/aae451dabf64ead2e983f92be76039a8ba233ade.png",
            title = "热门热血番剧榜",
            subTitle = "每小时更新",
            report = AnimeFeedData.FeedItem.FeedSubItem.Report(),
            subItems = List(8) {
                AnimeFeedData.FeedItem.FeedSubItem(
                    cardStyle = "v_card",
                    rankId = 0,
                    cover = "https://i0.hdslb.com/bfs/bangumi/image/f610305ad3922bee9d51748ab38da0c54e785b44.png",
                    hover = Hover(
                        img = "http://i0.hdslb.com/bfs/archive/aae451dabf64ead2e983f92be76039a8ba233ade.png",
                        text = listOf("漫画改", "热血", "更新至第6话")
                    ),
                    title = "解雇后走上人生巅峰",
                    subTitle = "被解雇的暗黑士兵慢生活的第二人生",
                    report = AnimeFeedData.FeedItem.FeedSubItem.Report()
                )
            }
        )
    )
    BVTheme {
        AnimeFeedRankRow(data = data)
    }
}