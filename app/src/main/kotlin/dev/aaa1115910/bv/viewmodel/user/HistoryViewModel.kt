package dev.aaa1115910.bv.viewmodel.user

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.aaa1115910.biliapi.BiliApi
import dev.aaa1115910.bv.entity.VideoCardData
import dev.aaa1115910.bv.util.Prefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mu.KotlinLogging

class HistoryViewModel : ViewModel() {
    companion object {
        private val logger = KotlinLogging.logger { }
    }

    var histories = mutableStateListOf<VideoCardData>()

    private var max = 0L
    private var viewAt = 0L

    var showError by mutableStateOf(false)
    var error: Throwable? by mutableStateOf(null)

    private var updating = false

    fun clearData() {
        max = 0L
        viewAt = 0L
        histories.clear()
    }

    fun update() {
        viewModelScope.launch(Dispatchers.Default) {
            updateHistories()
        }
    }

    private suspend fun updateHistories() {
        if (updating) return
        logger.info { "Updating histories with params [max=$max, viewAt=$viewAt]" }
        updating = true
        runCatching {
            val responseData = BiliApi.getHistories(
                max = max,
                viewAt = viewAt,
                pageSize = 30,
                sessData = Prefs.sessData
            ).getResponseData()
            responseData.list.forEach { historyItem ->
                if (historyItem.history.business != "archive") return@forEach
                histories.add(
                    VideoCardData(
                        avid = historyItem.history.oid,
                        title = historyItem.title,
                        cover = historyItem.cover,
                        upName = historyItem.authorName,
                        time = historyItem.duration * 1000L
                    )
                )
            }
            //update cursor
            max = responseData.cursor.max
            viewAt = responseData.cursor.viewAt
            logger.info { "Update history cursor: [max=$max, viewAt=$viewAt]" }
            logger.info { "Update histories success" }
        }.onFailure {
            logger.info { "Update histories failed: ${it.stackTraceToString()}" }
            showError = true
            error = it
        }.onSuccess {
            showError = false
        }
        updating = false
    }
}