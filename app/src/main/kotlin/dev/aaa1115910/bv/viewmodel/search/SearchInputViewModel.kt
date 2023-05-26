package dev.aaa1115910.bv.viewmodel.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.aaa1115910.biliapi.http.BiliHttpApi
import dev.aaa1115910.biliapi.http.entity.search.Hotword
import dev.aaa1115910.biliapi.http.entity.search.KeywordSuggest
import dev.aaa1115910.bv.BVApp
import dev.aaa1115910.bv.dao.AppDatabase
import dev.aaa1115910.bv.entity.db.SearchHistoryDB
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import java.util.Date

class SearchInputViewModel(
    private val db: AppDatabase = BVApp.getAppDatabase()
) : ViewModel() {
    private val logger = KotlinLogging.logger { }

    var keyword by mutableStateOf("")
    val hotwords = mutableStateListOf<Hotword>()
    val suggests = mutableStateListOf<KeywordSuggest.Result.Tag>()
    val searchHistories = mutableStateListOf<SearchHistoryDB>()

    init {
        updateHotwords()
        loadSearchHistories()
    }

    private fun updateHotwords() {
        logger.fInfo { "Update hotwords" }
        viewModelScope.launch(Dispatchers.Default) {
            runCatching {
                val hotwordData = BiliHttpApi.getHotwords(
                    limit = 50
                ).getResponseData()
                logger.debug { "Find hotwords: ${hotwordData.trending.list}" }
                hotwords.addAll(hotwordData.trending.list)
            }.onFailure {
                withContext(Dispatchers.Main) {
                    "bilibili 热搜加载失败".toast(BVApp.context)
                }
            }
        }
    }

    fun updateSuggests() {
        logger.fInfo { "Update search suggests with '$keyword'" }
        viewModelScope.launch(Dispatchers.Default) {
            runCatching {
                val keywordSuggest = BiliHttpApi.getKeywordSuggest(keyword)
                logger.debug { "Find suggests: ${keywordSuggest.result}" }
                suggests.clear()
                suggests.addAll(keywordSuggest.suggests)
            }.onFailure {
                withContext(Dispatchers.Main) {
                    "bilibili 搜索建议加载失败".toast(BVApp.context)
                }
            }
        }
    }

    private fun loadSearchHistories() {
        logger.fInfo { "Load search histories" }
        viewModelScope.launch(Dispatchers.Default) {
            //当第一次调用时，可能会出现异常 IllegalStateException: Reading a state that was created after the snapshot was taken or in a snapshot that has not yet been applied
            runCatching { searchHistories.clear() }
            runCatching {
                searchHistories.addAll(db.searchHistoryDao().getHistories(20))
                logger.fInfo { "Load search histories finish, size: ${searchHistories.size}" }
            }
        }
    }

    fun addSearchHistory(keyword: String) {
        logger.fInfo { "Add search history: $keyword" }
        viewModelScope.launch(Dispatchers.Default) {
            db.searchHistoryDao().findHistory(keyword)?.let { history ->
                logger.fInfo { "Search history $keyword already exist" }
                history.searchDate = Date()
                db.searchHistoryDao().update(history)
            } ?: let {
                logger.fInfo { "Insert new search history $keyword" }
                val history = SearchHistoryDB(keyword = keyword)
                db.searchHistoryDao().insert(history)
            }
            loadSearchHistories()
        }
    }
}