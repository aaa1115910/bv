package dev.aaa1115910.bv.viewmodel.ugc

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.aaa1115910.biliapi.entity.CarouselData
import dev.aaa1115910.biliapi.entity.ugc.UgcItem
import dev.aaa1115910.biliapi.entity.ugc.UgcTypeV2
import dev.aaa1115910.biliapi.entity.ugc.region.UgcFeedPage
import dev.aaa1115910.biliapi.repositories.UgcRepository
import dev.aaa1115910.bv.BVApp
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.toast
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class UgcViewModel(
    open val ugcRepository: UgcRepository,
    val ugcType: UgcTypeV2
) : ViewModel() {
    private val logger = KotlinLogging.logger("UgvViewModel[$ugcType]")

    /**
     * 轮播图
     */
    val carouselItems = mutableStateListOf<CarouselData.CarouselItem>()

    /**
     * UGC数据列表
     */
    val ugcItems = mutableStateListOf<UgcItem>()

    var nextPage by mutableStateOf(UgcFeedPage())
    var hasMore by mutableStateOf(true)
    var updating by mutableStateOf(false)
    var showCarousel by mutableStateOf(true)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            initUgcRegionData()
        }
    }

    private suspend fun initUgcRegionData() {
        loadUgcRegionData()
        loadMore()
    }

    suspend fun loadUgcRegionData() {
        if (!hasMore && updating) return
        updating = true
        logger.fInfo { "load ugc $ugcType region data" }
        runCatching {
            val carouselData = ugcRepository.getCarousel(ugcType)
            val data = ugcRepository.getRegionFeedRcmd(ugcType, nextPage)
            carouselItems.clear()
            ugcItems.clear()
            carouselItems.addAll(carouselData.items)
            ugcItems.addAll(data.items)
            nextPage = data.nextPage
            showCarousel = carouselItems.isNotEmpty()
        }.onFailure {
            logger.fInfo { "load $ugcType data failed: ${it.stackTraceToString()}" }
            withContext(Dispatchers.Main) {
                "加载 $ugcType 数据失败: ${it.message}".toast(BVApp.context)
            }
        }
        hasMore = true
        updating = false
    }

    fun reloadAll() {
        logger.fInfo { "reload all $ugcType data" }
        viewModelScope.launch(Dispatchers.IO) {
            nextPage = UgcFeedPage()
            hasMore = true
            showCarousel = true
            carouselItems.clear()
            ugcItems.clear()
            initUgcRegionData()
        }
    }

    /**
     * 延迟加载数据，如果在延迟期间被取消则不加载
     */
    fun loadDataWithDelay(delayMs: Long = 300L) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch(Dispatchers.IO) {
            delay(delayMs)
            if (ugcItems.isEmpty()) {
                initUgcRegionData()
            }
        }
    }

    /**
     * 取消延迟加载
     */
    fun cancelDelayedLoad() {
        loadJob?.cancel()
        loadJob = null
    }

    suspend fun loadMore() {
        if (!hasMore && updating) return
        updating = true
        runCatching {
            val data = ugcRepository.getRegionFeedRcmd(ugcType, nextPage)
            ugcItems.addAll(data.items)
            nextPage = data.nextPage
            hasMore = data.items.isNotEmpty()
        }.onFailure {
            logger.fInfo { "load more $ugcType data failed: ${it.stackTraceToString()}" }
            withContext(Dispatchers.Main) {
                "加载 $ugcType 更多推荐失败: ${it.message}".toast(BVApp.context)
            }
        }
        updating = false
    }

}