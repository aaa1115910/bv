package dev.aaa1115910.bv.viewmodel.home

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.aaa1115910.biliapi.BiliApi
import dev.aaa1115910.biliapi.entity.anime.AnimeFeedData
import dev.aaa1115910.bv.util.fInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mu.KotlinLogging

class AnimeViewModel : ViewModel() {
    companion object {
        private val logger = KotlinLogging.logger { }
    }

    val feedItems = mutableStateListOf<List<AnimeFeedData.FeedItem.FeedSubItem>>()
    private val restSubItems = mutableListOf<AnimeFeedData.FeedItem.FeedSubItem>()

    private var updating = false
    private var cursor = 0
    private var hasNext = true

    init {
        loadMore()
    }

    fun loadMore() {
        if (hasNext) {
            viewModelScope.launch(Dispatchers.Default) {
                updateFeed()
            }
        }
    }

    private suspend fun updateFeed() {
        if (updating) return
        updating = true
        runCatching {
            val responseData = BiliApi.getAnimeFeed(cursor = cursor).getResponseData()
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