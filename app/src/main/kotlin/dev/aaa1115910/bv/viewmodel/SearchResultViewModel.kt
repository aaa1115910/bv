package dev.aaa1115910.bv.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.aaa1115910.biliapi.BiliApi
import dev.aaa1115910.biliapi.entity.search.SearchResultItem
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.screen.search.SearchResultFilterDuration
import dev.aaa1115910.bv.screen.search.SearchResultFilterOrderType
import dev.aaa1115910.bv.util.Partition
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

    var selectedOrder by mutableStateOf(SearchResultFilterOrderType.ComprehensiveSort)
    var selectedDuration by mutableStateOf(SearchResultFilterDuration.All)
    var selectedPartition: Partition? by mutableStateOf(null)
    var selectedChildPartition: Partition? by mutableStateOf(null)

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
            logger.fInfo { "Load search result: [keyword=$keyword, type=$searchType, pageNumber=$pageNumber]" }
            runCatching {
                val searchResultResponse =
                    BiliApi.searchType(
                        keyword = keyword,
                        type = searchType.type,
                        page = pageNumber,
                        tid = selectedChildPartition?.tid ?: selectedPartition?.tid,
                        order = selectedOrder.order,
                        duration = selectedDuration.duration
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
