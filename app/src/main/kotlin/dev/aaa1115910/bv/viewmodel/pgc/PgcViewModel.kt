package dev.aaa1115910.bv.viewmodel.pgc

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.aaa1115910.biliapi.entity.CarouselData
import dev.aaa1115910.biliapi.entity.pgc.PgcFeedData
import dev.aaa1115910.biliapi.entity.pgc.PgcItem
import dev.aaa1115910.biliapi.entity.pgc.PgcType
import dev.aaa1115910.biliapi.repositories.PgcRepository
import dev.aaa1115910.bv.BVApp
import dev.aaa1115910.bv.BuildConfig
import dev.aaa1115910.bv.util.addWithMainContext
import dev.aaa1115910.bv.util.addAllWithMainContext
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.fWarn
import dev.aaa1115910.bv.util.toast
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class PgcViewModel(
    open val pgcRepository: PgcRepository,
    val pgcType: PgcType,
) : ViewModel() {
    private val logger = KotlinLogging.logger("PgcViewModel[$pgcType]")

    /**
     * 轮播图
     */
    val carouselItems = mutableStateListOf<CarouselData.CarouselItem>()

    /**
     * 猜你喜欢
     */
    val feedItems = mutableStateListOf<FeedListItem>()

    /**
     * 推荐数据中会穿插排行榜，为了避免出现某一行仅出现单独几个剧集，因此将不满一行的剧集单独存起来
     */
    private val restSubItems = mutableListOf<PgcItem>()

    var updating by mutableStateOf(false)
    var hasNext by mutableStateOf(true)
    private var cursor = 0

    init {
        loadMore()
        viewModelScope.launch(Dispatchers.IO) {
            updateCarousel()
        }
    }

    /**
     * 加载更多推荐数据
     */
    fun loadMore() {
        if (hasNext) {
            viewModelScope.launch(Dispatchers.IO) {
                updateFeed()
            }
        }
    }

    /**
     * 重新加载所有数据，点击界面顶部 Tab 时使用
     */
    fun reloadAll() {
        logger.fInfo { "Reload all $pgcType data" }
        clearAll()
        viewModelScope.launch(Dispatchers.IO) {
            updateCarousel()
            updateFeed()
        }
    }

    /**
     * 清理所有数据
     */
    fun clearAll() {
        logger.fInfo { "Clear all data" }
        carouselItems.clear()
        feedItems.clear()
        restSubItems.clear()
        cursor = 0
        hasNext = true
    }

    /**
     * 更新轮播图
     */
    private suspend fun updateCarousel() {
        logger.fInfo { "Updating $pgcType carousel" }
        runCatching {
            // 由于未知原因，注入的 PgcRepository 可能获取到的对象为 null
            var maxRetry = 10
            while (pgcRepository == null && maxRetry > 0) {
                delay(10)
                maxRetry--
            }
            if (BuildConfig.DEBUG && maxRetry != 10) {
                logger.fWarn { "Retry ${10 - maxRetry} times to get pgcRepository" }
                withContext(Dispatchers.Main) {
                    "Retry ${10 - maxRetry} times to get pgcRepository($pgcType)".toast(BVApp.context)
                }
            }

            val carouselData = pgcRepository.getCarousel(pgcType)
            logger.fInfo { "Find $pgcType carousels, size: ${carouselData.items.size}" }
            carouselItems.addAllWithMainContext(carouselData.items)
            logger.debug { "carouselItems: $carouselItems" }
        }.onFailure {
            logger.fInfo { "Update $pgcType carousel failed: ${it.stackTraceToString()}" }
            withContext(Dispatchers.Main) {
                "加载 $pgcType 轮播图失败: ${it.message}".toast(BVApp.context)
            }
        }
    }

    /**
     * 获取推荐数据
     */
    private suspend fun updateFeed() {
        if (updating) return
        withContext(Dispatchers.Main) { updating = true }
        logger.fInfo { "Update anime feed" }
        runCatching {
            val pgcFeedData = pgcRepository.getFeed(
                pgcType = pgcType,
                cursor = cursor
            )
            cursor = pgcFeedData.cursor
            withContext(Dispatchers.Main) { hasNext = pgcFeedData.hasNext }
            updateFeedItems(pgcFeedData)
        }.onFailure {
            logger.fInfo { "Update $pgcType feeds failed: ${it.stackTraceToString()}" }
        }
        withContext(Dispatchers.Main) { updating = false }
    }

    /**
     * 对 [updateFeed] 获取到得数据进行二次整理并更新到 feedItems
     */
    private suspend fun updateFeedItems(data: PgcFeedData) {
        logger.fInfo { "update $pgcType feed items: [items: ${data.items.size}, ranks: ${data.ranks.size}]" }
        val epList = mutableStateListOf<PgcItem>()
        epList.addAll(restSubItems)
        epList.addAll(data.items)

        epList.chunked(5).forEach { chunkedVCardList ->
            if (chunkedVCardList.size == 5) {
                feedItems.addWithMainContext(
                    FeedListItem(
                        type = FeedListType.Ep,
                        items = chunkedVCardList
                    )
                )
            } else {
                restSubItems.clear()
                restSubItems.addAll(chunkedVCardList)
            }
        }

        data.ranks.forEach { rank ->
            feedItems.addWithMainContext(
                FeedListItem(
                    type = FeedListType.Rank,
                    rank = rank
                )
            )
        }
    }
}

data class FeedListItem(
    val type: FeedListType,
    val items: List<PgcItem>? = emptyList(),
    val rank: PgcFeedData.FeedRank? = null
)

enum class FeedListType {
    Ep, Rank
}