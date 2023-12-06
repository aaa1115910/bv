package dev.aaa1115910.bv.viewmodel.home

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.aaa1115910.biliapi.http.BiliHttpApi
import dev.aaa1115910.biliapi.http.entity.anime.AnimeFeedData
import dev.aaa1115910.biliapi.http.entity.anime.AnimeHomepageDataType
import dev.aaa1115910.biliapi.http.entity.anime.CarouselItem
import dev.aaa1115910.bv.BVApp
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.toast
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AnimeViewModel : ViewModel() {
    companion object {
        private val logger = KotlinLogging.logger { }
    }

    val carouselItems = mutableStateListOf<CarouselItem>()
    val feedItems = mutableStateListOf<List<AnimeFeedData.FeedItem.FeedSubItem>>()
    private val restSubItems = mutableListOf<AnimeFeedData.FeedItem.FeedSubItem>()

    private var updating = false
    private var cursor = 0
    private var hasNext = true

    init {
        loadMore()
        viewModelScope.launch(Dispatchers.Default) {
            updateCarousel()
        }
    }

    fun loadMore() {
        if (hasNext) {
            viewModelScope.launch(Dispatchers.Default) {
                updateFeed()
            }
        }
    }

    private fun clearAll() {
        logger.fInfo { "Clear all data" }
        carouselItems.clear()
        feedItems.clear()
        restSubItems.clear()
        cursor = 0
        hasNext = true
    }

    fun reloadAll() {
        logger.fInfo { "Reload all" }
        clearAll()
        viewModelScope.launch(Dispatchers.Default) {
            updateCarousel()
            updateFeed()
        }
    }

    private suspend fun updateCarousel() {
        logger.fInfo { "Update anime carousel" }
        runCatching {
            val items = BiliHttpApi.getAnimeHomepageData(dataType = AnimeHomepageDataType.V2)
                ?.getCarouselItems() ?: emptyList()
            logger.fInfo { "Find anime carousels, size: ${items.size}" }
            carouselItems.addAll(items)
        }.onFailure {
            logger.fInfo { "Update anime carousel failed: ${it.stackTraceToString()}" }
            withContext(Dispatchers.Main) {
                "加载轮播图失败: ${it.message}".toast(BVApp.context)
            }
        }
    }

    private suspend fun updateFeed() {
        if (updating) return
        updating = true
        logger.fInfo { "Update anime feed" }
        runCatching {
            val responseData = BiliHttpApi.getAnimeFeed(cursor = cursor).getResponseData()
            cursor = responseData.coursor
            hasNext = responseData.hasNext
            updateFeedItems(responseData.items)
        }.onFailure {
            logger.fInfo { "Update anime feeds failed: ${it.stackTraceToString()}" }
        }
        updating = false
    }

    private fun updateFeedItems(items: List<AnimeFeedData.FeedItem>) {
        val vCardList = mutableListOf<AnimeFeedData.FeedItem.FeedSubItem>()
        val rankList = mutableStateListOf<AnimeFeedData.FeedItem>()

        vCardList.addAll(restSubItems)
        items.forEach { feedItem ->
            when (feedItem.subItems.firstOrNull()?.cardStyle) {
                "v_card" -> vCardList.addAll(feedItem.subItems)
                "rank" -> rankList.add(feedItem)
            }
        }

        vCardList.chunked(6).forEach { chunkedVCardList ->
            if (chunkedVCardList.size == 6) {
                feedItems.add(chunkedVCardList)
            } else {
                restSubItems.clear()
                restSubItems.addAll(chunkedVCardList)
            }
        }
        rankList.forEach { rankListItem ->
            rankListItem.subItems.forEach { feedItems.add(listOf(it)) }
        }
    }
}