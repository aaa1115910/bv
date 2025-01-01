package dev.aaa1115910.bv.viewmodel.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.aaa1115910.biliapi.entity.search.Hotword
import dev.aaa1115910.biliapi.repositories.SearchRepository
import dev.aaa1115910.bv.BVApp
import dev.aaa1115910.bv.dao.AppDatabase
import dev.aaa1115910.bv.entity.db.SearchHistoryDB
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.swapListWithMainContext
import dev.aaa1115910.bv.util.toast
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class SearchInputViewModel(
    private val searchRepository: SearchRepository,
    private val db: AppDatabase = BVApp.getAppDatabase()
) : ViewModel() {
    private val logger = KotlinLogging.logger { }

    var keyword by mutableStateOf("")
    val hotwords = mutableStateListOf<Hotword>()
    val suggests = mutableStateListOf<String>()
    val searchHistories = mutableStateListOf<SearchHistoryDB>()

    init {
        updateHotwords()
        loadSearchHistories()
    }

    private fun updateHotwords() {
        logger.fInfo { "Update hotwords" }
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val hotwordData = searchRepository.getSearchHotwords(
                    limit = 50,
                    preferApiType = Prefs.apiType
                )
                logger.debug { "Find hotwords: $hotwordData" }
                withContext(Dispatchers.Main) { hotwords.addAll(hotwordData) }
            }.onFailure {
                withContext(Dispatchers.Main) {
                    "bilibili 热搜加载失败".toast(BVApp.context)
                }
                logger.info { it.stackTraceToString() }
            }
        }
    }

    fun updateSuggests() {
        logger.fInfo { "Update search suggests with '$keyword'" }
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val keywordSuggest = searchRepository.getSearchSuggest(
                    keyword = keyword,
                    preferApiType = Prefs.apiType
                )
                logger.debug { "Find suggests: $keywordSuggest" }
                suggests.swapListWithMainContext(keywordSuggest)
            }.onFailure {
                withContext(Dispatchers.Main) {
                    "bilibili 搜索建议加载失败".toast(BVApp.context)
                }
                logger.info { it.stackTraceToString() }
            }
        }
    }

    private fun loadSearchHistories() {
        logger.fInfo { "Load search histories" }
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                searchHistories.swapListWithMainContext(db.searchHistoryDao().getHistories(20))
                logger.fInfo { "Load search histories finish, size: ${searchHistories.size}" }
            }
        }
    }

    fun addSearchHistory(keyword: String) {
        logger.fInfo { "Add search history: $keyword" }
        viewModelScope.launch(Dispatchers.IO) {
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