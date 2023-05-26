package dev.aaa1115910.bv.viewmodel.search

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.aaa1115910.biliapi.http.BiliHttpApi
import dev.aaa1115910.biliapi.http.entity.search.SearchResultData
import dev.aaa1115910.biliapi.http.entity.search.SearchResultItem
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.screen.search.SearchResultFilterDuration
import dev.aaa1115910.bv.screen.search.SearchResultFilterOrderType
import dev.aaa1115910.bv.util.Partition
import dev.aaa1115910.bv.util.Prefs
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

    var videoSearchResult by mutableStateOf(SearchResult(SearchResultType.Video))
    var mediaBangumiSearchResult by mutableStateOf(SearchResult(SearchResultType.MediaBangumi))
    var mediaFtSearchResult by mutableStateOf(SearchResult(SearchResultType.MediaFt))
    var biliUserSearchResult by mutableStateOf(SearchResult(SearchResultType.BiliUser))

    var selectedOrder by mutableStateOf(SearchResultFilterOrderType.ComprehensiveSort)
    var selectedDuration by mutableStateOf(SearchResultFilterDuration.All)
    var selectedPartition: Partition? by mutableStateOf(null)
    var selectedChildPartition: Partition? by mutableStateOf(null)

    private var updating = false

    fun update() {
        resetPages()
        clearResults()
        SearchResultType.values().forEach { loadMore(it, true) }
    }

    private fun resetPages() {
        videoSearchResult.page.reset()
        mediaBangumiSearchResult.page.reset()
        mediaFtSearchResult.page.reset()
        biliUserSearchResult.page.reset()
    }

    private fun clearResults() {
        videoSearchResult.results = listOf()
        mediaBangumiSearchResult.results = listOf()
        mediaFtSearchResult.results = listOf()
        biliUserSearchResult.results = listOf()
    }

    fun loadMore(
        searchType: SearchResultType,
        ignoreUpdating: Boolean = false
    ) {
        val currentPage = when (searchType) {
            SearchResultType.Video -> videoSearchResult.page
            SearchResultType.MediaBangumi -> mediaBangumiSearchResult.page
            SearchResultType.MediaFt -> mediaFtSearchResult.page
            SearchResultType.BiliUser -> biliUserSearchResult.page
        }
        if (currentPage.pageNumber >= currentPage.maxPages) return
        if (updating && !ignoreUpdating) return

        updating = true
        viewModelScope.launch(Dispatchers.Default) {
            logger.fInfo { "Load search result: [keyword=$keyword, type=$searchType, pageNumber=${currentPage.pageNumber}]" }
            runCatching {
                val searchResultResponse =
                    BiliHttpApi.searchType(
                        keyword = keyword,
                        type = searchType.type,
                        page = currentPage.pageNumber,
                        tid = selectedChildPartition?.tid ?: selectedPartition?.tid,
                        order = selectedOrder.order,
                        duration = selectedDuration.duration,
                        buvid3 = Prefs.buvid3
                    ).getResponseData()
                when (searchType) {
                    SearchResultType.Video -> videoSearchResult =
                        videoSearchResult.appendSearchResultData(searchResultResponse)

                    SearchResultType.MediaBangumi -> mediaBangumiSearchResult =
                        mediaBangumiSearchResult.appendSearchResultData(searchResultResponse)

                    SearchResultType.MediaFt -> mediaFtSearchResult =
                        mediaFtSearchResult.appendSearchResultData(searchResultResponse)

                    SearchResultType.BiliUser -> biliUserSearchResult =
                        biliUserSearchResult.appendSearchResultData(searchResultResponse)
                }
            }
            updating = false
        }
    }

    data class SearchResult(
        var type: SearchResultType,
        var results: List<SearchResultItem> = ArrayList(),
        var page: SearchResultPage = SearchResultPage()
    ) {
        data class SearchResultPage(
            var pageNumber: Int = 1,
            var maxPages: Int = 50
        ) {
            fun reset() {
                pageNumber = 1
                maxPages = 50
            }
        }

        fun appendSearchResultData(searchResultData: SearchResultData): SearchResult {
            val newSearchResult = SearchResult(type)
            newSearchResult.results = results + searchResultData.searchTypeResults
            newSearchResult.page = SearchResultPage(
                pageNumber = page.pageNumber + 1,
                maxPages = searchResultData.numPages
            )
            return newSearchResult
        }
    }
}

enum class SearchResultType(
    val type: String,
    private val strRes: Int
) {
    Video(type = "video", strRes = R.string.search_result_type_name_video),
    MediaBangumi(type = "media_bangumi", R.string.search_result_type_name_media_bangumi),
    MediaFt(type = "media_ft", strRes = R.string.search_result_type_name_media_ft),
    BiliUser(type = "bili_user", strRes = R.string.search_result_type_name_bili_user);

    fun getDisplayName(context: Context) = context.getString(strRes)
}
