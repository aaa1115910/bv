package dev.aaa1115910.bv.screen.main.ugc

import android.content.Context
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.aaa1115910.biliapi.entity.CarouselData
import dev.aaa1115910.biliapi.entity.ugc.UgcItem
import dev.aaa1115910.biliapi.entity.ugc.UgcType
import dev.aaa1115910.biliapi.entity.ugc.region.UgcRegionPage
import dev.aaa1115910.biliapi.repositories.UgcRepository
import dev.aaa1115910.bv.activities.video.VideoInfoActivity
import dev.aaa1115910.bv.component.UgcCarousel
import dev.aaa1115910.bv.component.videocard.SmallVideoCard
import dev.aaa1115910.bv.entity.carddata.VideoCardData
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.toast
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject

@Composable
fun UgcRegionScaffold(
    modifier: Modifier = Modifier,
    state: UgcScaffoldState,
    childRegionButtons: (@Composable () -> Unit)? = null
) {
    val context = LocalContext.current
    var currentFocusedIndex by remember { mutableIntStateOf(0) }
    val shouldLoadMore by remember {
        derivedStateOf { currentFocusedIndex + 24 > state.ugcItems.size }
    }

    LaunchedEffect(Unit) { if (state.ugcItems.isEmpty()) state.initUgcRegionData() }
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            state.loadMore()
            currentFocusedIndex = -100
        }
    }

    LazyColumn(
        modifier = modifier,
        state = state.lazyListState
    ) {
        if (state.showCarousel) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.Center
                ) {
                    UgcCarousel(
                        modifier = Modifier
                            .width(880.dp)
                            .padding(32.dp, 0.dp),
                        data = state.carouselItems
                    )
                }
            }
        }

        if (childRegionButtons != null) {
            item {
                childRegionButtons()
            }
        } else {
            item {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                )
            }
        }

        gridItems(
            data = state.ugcItems,
            columnCount = 4,
            modifier = Modifier
                .width(880.dp)
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            itemContent = { index, item ->
                SmallVideoCard(
                    data = VideoCardData(
                        avid = item.aid,
                        title = item.title,
                        cover = item.cover,
                        play = item.play,
                        danmaku = item.danmaku,
                        upName = item.author,
                        time = item.duration * 1000L
                    ),
                    onClick = { VideoInfoActivity.actionStart(context, item.aid) },
                    onFocus = { currentFocusedIndex = index }
                )
            }
        )
    }
}

fun <T> LazyListScope.gridItems(
    data: List<T>,
    key: ((index: Int) -> Any)? = null,
    columnCount: Int,
    modifier: Modifier = Modifier,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    itemContent: @Composable BoxScope.(Int, T) -> Unit,
) {
    val size = data.count()
    val rows = if (size == 0) 0 else 1 + (size - 1) / columnCount
    items(rows, key = key) { rowIndex ->
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            item {
                Row(
                    verticalAlignment = verticalAlignment,
                    horizontalArrangement = horizontalArrangement,
                    modifier = modifier
                ) {
                    for (columnIndex in 0 until columnCount) {
                        val itemIndex = rowIndex * columnCount + columnIndex
                        if (itemIndex < size) {
                            Box(
                                modifier = Modifier.weight(1F, fill = true),
                                propagateMinConstraints = true
                            ) {
                                itemContent(itemIndex, data[itemIndex])
                            }
                        } else {
                            Spacer(Modifier.weight(1F, fill = true))
                        }
                    }
                }
            }
        }
    }
}

data class UgcScaffoldState(
    val context: Context,
    val scope: CoroutineScope,
    val lazyListState: LazyListState,
    val ugcType: UgcType,
    private val ugcRepository: UgcRepository
) {
    companion object {
        val logger = KotlinLogging.logger { }
    }

    val carouselItems = mutableStateListOf<CarouselData.CarouselItem>()
    val ugcItems = mutableStateListOf<UgcItem>()
    var nextPage by mutableStateOf(UgcRegionPage())
    var hasMore by mutableStateOf(true)
    var updating by mutableStateOf(false)
    var showCarousel by mutableStateOf(true)

    suspend fun initUgcRegionData() {
        loadUgcRegionData()
        loadMore()
    }

    suspend fun loadUgcRegionData() {
        if (!hasMore && updating) return
        updating = true
        logger.fInfo { "load ugc $ugcType region data" }
        runCatching {
            val data = ugcRepository.getRegionData(ugcType)
            carouselItems.clear()
            ugcItems.clear()
            carouselItems.addAll(data.carouselData?.items ?: emptyList())
            ugcItems.addAll(data.items)
            nextPage = data.next
            showCarousel = carouselItems.isNotEmpty()
        }.onFailure {
            logger.fInfo { "load $ugcType data failed: ${it.stackTraceToString()}" }
            withContext(Dispatchers.Main) {
                "加载 $ugcType 数据失败: ${it.message}".toast(context)
            }
        }
        hasMore = true
        updating = false
    }

    fun reloadAll() {
        logger.fInfo { "reload all $ugcType data" }
        scope.launch(Dispatchers.IO) {
            nextPage = UgcRegionPage()
            hasMore = true
            showCarousel = true
            carouselItems.clear()
            ugcItems.clear()
            initUgcRegionData()
        }
    }

    suspend fun loadMore() {
        if (!hasMore && updating) return
        updating = true
        runCatching {
            val data = ugcRepository.getRegionMoreData(ugcType)
            ugcItems.addAll(data.items)
            nextPage = data.next
            hasMore = data.items.isNotEmpty()
        }.onFailure {
            logger.fInfo { "load more $ugcType data failed: ${it.stackTraceToString()}" }
            withContext(Dispatchers.Main) {
                "加载 $ugcType 更多推荐失败: ${it.message}".toast(context)
            }
        }
        updating = false
    }
}

@Composable
fun rememberUgcScaffoldState(
    context: Context = LocalContext.current,
    scope: CoroutineScope = rememberCoroutineScope(),
    lazyListState: LazyListState = rememberLazyListState(),
    ugcType: UgcType,
    ugcRepository: UgcRepository = koinInject()
): UgcScaffoldState {
    return remember(
        context,
        scope,
        lazyListState,
        ugcType,
        ugcRepository
    ) {
        UgcScaffoldState(
            context = context,
            scope = scope,
            lazyListState = lazyListState,
            ugcType = ugcType,
            ugcRepository = ugcRepository
        )
    }
}