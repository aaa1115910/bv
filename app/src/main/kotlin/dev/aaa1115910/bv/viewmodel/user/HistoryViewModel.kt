package dev.aaa1115910.bv.viewmodel.user

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.aaa1115910.biliapi.BiliApi
import dev.aaa1115910.bv.entity.VideoCardData
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.fInfo
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

    private var updating = false

    fun update() {
        viewModelScope.launch(Dispatchers.Default) {
            updateHistories()
        }
    }

    private suspend fun updateHistories() {
        if (updating) return
        logger.fInfo { "Updating histories with params [max=$max, viewAt=$viewAt]" }
        updating = true
        runCatching {
            val responseData = BiliApi.getHistories(
                max = max,
                viewAt = viewAt,
                pageSize = 30,
                sessData = Prefs.sessData
            ).getResponseData()
            responseData.list.forEach { historyItem ->
                val supportedBusinessList = listOf("archive", "pgc")
                if (!supportedBusinessList.contains(historyItem.history.business)) return@forEach
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
            logger.fInfo { "Update history cursor: [max=$max, viewAt=$viewAt]" }
            logger.fInfo { "Update histories success" }
        }.onFailure {
            logger.fInfo { "Update histories failed: ${it.stackTraceToString()}" }
        }
        updating = false
    }
}