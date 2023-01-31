package dev.aaa1115910.bv.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.aaa1115910.biliapi.BiliApi
import dev.aaa1115910.biliapi.entity.search.SearchResultItem
import dev.aaa1115910.bv.util.fInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mu.KotlinLogging

class SearchResultViewModel : ViewModel() {
    companion object {
        private val logger = KotlinLogging.logger { }
    }

    var keyword by mutableStateOf("")
    var searchType by mutableStateOf(SearchResultType.Video)
    var searchResult = mutableStateListOf<SearchResultItem>()

    private var pageNumber = 1
    private var maxPages = 50

    private var updating = false
    var noMore = false

    fun update() {
        pageNumber = 1
        maxPages = 50
        searchResult.clear()
        loadMore()
    }

    fun loadMore() {
        if (pageNumber > maxPages) {
            noMore = true
            return
        }
        if (updating) return
        updating = true
        viewModelScope.launch(Dispatchers.Default) {
            logger.fInfo { "Load search result: [keyword=$keyword, type=$searchResult, pageNumber=$pageNumber]" }
            runCatching {
                val searchResultResponse =
                    BiliApi.searchType(
                        keyword = keyword,
                        type = searchType.type,
                        page = pageNumber
                    ).getResponseData()
                searchResult.addAll(searchResultResponse.searchTypeResults)
                maxPages = searchResultResponse.numPages
            }.onSuccess {
                pageNumber++
            }
            updating = false
        }
    }


}

enum class SearchResultType(val type: String) {
    Video("video"), MediaBangumi("media_bangumi"), MediaFt("media_ft"), BiliUser("bili_user")
}