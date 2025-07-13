package dev.aaa1115910.bv.tv.screens.main.ugc

import android.content.Context
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import dev.aaa1115910.biliapi.entity.CarouselData
import dev.aaa1115910.biliapi.entity.ugc.UgcItem
import dev.aaa1115910.biliapi.entity.ugc.UgcTypeV2
import dev.aaa1115910.biliapi.entity.ugc.region.UgcFeedPage
import dev.aaa1115910.biliapi.repositories.UgcRepository
import dev.aaa1115910.bv.entity.carddata.VideoCardData
import dev.aaa1115910.bv.tv.R
import dev.aaa1115910.bv.tv.activities.video.VideoInfoActivity
import dev.aaa1115910.bv.tv.component.UgcCarousel
import dev.aaa1115910.bv.tv.component.videocard.SmallVideoCard
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.toast
import dev.aaa1115910.bv.viewmodel.ugc.UgcViewModel
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun UgcRegionScaffold(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState,
    ugcViewModel: UgcViewModel,
    childRegionButtons: (@Composable () -> Unit)? = null
) {
    val context = LocalContext.current
    var currentFocusedIndex by remember { mutableIntStateOf(0) }
    val shouldLoadMore by remember {
        derivedStateOf { state.ugcItems.size > 0 && (currentFocusedIndex + 12 > state.ugcItems.size) }
    }
    // 初始化数据
    LaunchedEffect(Unit) {
        if (!state.dataInitialized || state.ugcItems.isEmpty()) {
            state.initUgcRegionData()
        }
    }

    // 监听滚动位置，加载更多内容
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            state.loadMore()
        }
    }

    val padding = dimensionResource(R.dimen.grid_padding)
    val spacedBy = dimensionResource(R.dimen.grid_spacedBy)

    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        state = state.lazyListState
    ) {
        if (ugcViewModel.showCarousel && ugcViewModel.carouselItems.isNotEmpty()) {
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
                            .padding(12.dp, 0.dp),
                        data = ugcViewModel.carouselItems,
                        onClick = { item ->
                            VideoInfoActivity.actionStart(
                                context = context,
                                aid = item.avid!!
                            )
                        }
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
            data = ugcViewModel.ugcItems,
            columnCount = 4,
            modifier = Modifier
                .width(880.dp)
                .padding(padding),
            horizontalArrangement = Arrangement.spacedBy(spacedBy),
            itemContent = { index, item ->
                SmallVideoCard(
                    data = VideoCardData(
                        avid = item.aid,
                        title = item.title,
                        cover = item.cover,
                        play = item.play,
                        danmaku = item.danmaku,
                        upName = item.author,
                        time = item.duration * 1000L,
                        pubTime = item.pubTime
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
    val ugcType: UgcTypeV2,
    private val ugcRepository: UgcRepository
) {
    companion object {
        val logger = KotlinLogging.logger { }

        // 保存每个ugcType的数据状态
        private val dataCache = mutableMapOf<UgcTypeV2, List<UgcItem>>()
        private val pageCache = mutableMapOf<UgcTypeV2, UgcFeedPage>()

        // 清除缓存，可以在内存不足或需要重新加载所有数据时调用
        fun clearCache() {
            dataCache.clear()
            pageCache.clear()
        }
    }

    // val carouselItems = mutableStateListOf<CarouselData.CarouselItem>()
    val ugcItems = mutableStateListOf<UgcItem>()
    var nextPage by mutableStateOf(pageCache[ugcType] ?: UgcFeedPage())
    var hasMore by mutableStateOf(true)
    var updating by mutableStateOf(false)
    var dataInitialized by mutableStateOf(false)
    // var showCarousel by mutableStateOf(true)
      init {
        // 如果有缓存数据，则恢复
        dataCache[ugcType]?.let { cachedItems ->
            if (cachedItems.isNotEmpty()) {
                ugcItems.addAll(cachedItems)
                dataInitialized = true
                logger.fInfo { "Restored ${cachedItems.size} items from cache for $ugcType" }
            }
        }
    }

    suspend fun initUgcRegionData() {
        loadUgcRegionData()
    }
    suspend fun loadUgcRegionData() {
        if (!hasMore && updating) return
        // 如果已经初始化了数据，就不再重新加载
        if (dataInitialized) {
            logger.fInfo { "Data already initialized for $ugcType, skip loading" }
            return
        }

        updating = true
        logger.fInfo { "load ugc $ugcType region data" }
        runCatching {
            val data = withContext(Dispatchers.IO) { ugcRepository.getRegionFeedRcmd(ugcType, nextPage) }
            ugcItems.clear()
            ugcItems.addAll(data.items)
            nextPage = data.nextPage

            updateCache()
            dataInitialized = true
            hasMore = true

            // 初始化后加载更多内容
            loadMore()
        }.onFailure {
            logger.fInfo { "load $ugcType data failed: ${it.stackTraceToString()}" }
            withContext(Dispatchers.Main) {
                "加载 $ugcType 数据失败: ${it.message}".toast(context)
            }
            hasMore = false
        }.also {
            updating = false
        }
    }

    // 将缓存更新逻辑提取为单独的函数
    private fun updateCache() {
        dataCache[ugcType] = ugcItems.toList()
        pageCache[ugcType] = nextPage
    }
    fun reloadAll() {
        logger.fInfo { "reload all $ugcType data" }
        scope.launch {
            withContext(Dispatchers.IO) {
                dataCache.remove(ugcType)
                pageCache.remove(ugcType)
            }

            nextPage = UgcFeedPage()
            hasMore = true
            ugcItems.clear()
            dataInitialized = false

            // 重新初始化数据
            initUgcRegionData()
        }
    }
    suspend fun loadMore() {
        if (!hasMore && updating) return
        updating = true
        runCatching {
            val data = withContext(Dispatchers.IO) { ugcRepository.getRegionFeedRcmd(ugcType, nextPage) }
            ugcItems.addAll(data.items)
            nextPage = data.nextPage
            hasMore = data.items.isNotEmpty()

            updateCache()
        }.onFailure {
            logger.fInfo { "load more $ugcType data failed: ${it.stackTraceToString()}" }
            withContext(Dispatchers.Main) {
                "加载 $ugcType 更多推荐失败: ${it.message}".toast(context)
            }
        }.also {
            updating = false
        }
    }
}

@Composable
fun rememberUgcScaffoldState(
    context: Context = LocalContext.current,
    scope: CoroutineScope = rememberCoroutineScope(),
    lazyListState: LazyListState = rememberLazyListState(),
    ugcType: UgcTypeV2,
    ugcRepository: UgcRepository = koinInject()
): UgcScaffoldState {
    // 使用ugcType作为key，确保不同类型内容有独立状态
    return remember(
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
