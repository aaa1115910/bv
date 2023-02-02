package dev.aaa1115910.bv.viewmodel.user

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.aaa1115910.biliapi.BiliApi
import dev.aaa1115910.biliapi.entity.AuthFailureException
import dev.aaa1115910.bv.BVApp
import dev.aaa1115910.bv.BuildConfig
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.entity.carddata.VideoCardData
import dev.aaa1115910.bv.repository.UserRepository
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.fWarn
import dev.aaa1115910.bv.util.formatMinSec
import dev.aaa1115910.bv.util.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mu.KotlinLogging

class HistoryViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
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

    private suspend fun updateHistories(context: Context = BVApp.context) {
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
                        timeString = if (historyItem.progress == -1) context.getString(R.string.play_time_finish)
                        else context.getString(
                            R.string.play_time_history,
                            (historyItem.progress * 1000L).formatMinSec(),
                            (historyItem.duration * 1000L).formatMinSec()
                        )
                    )
                )
            }
            //update cursor
            max = responseData.cursor.max
            viewAt = responseData.cursor.viewAt
            logger.fInfo { "Update history cursor: [max=$max, viewAt=$viewAt]" }
            logger.fInfo { "Update histories success" }
        }.onFailure {
            logger.fWarn { "Update histories failed: ${it.stackTraceToString()}" }
            when (it) {
                is AuthFailureException -> {
                    withContext(Dispatchers.Main) {
                        BVApp.context.getString(R.string.exception_auth_failure)
                            .toast(BVApp.context)
                    }
                    logger.fInfo { "User auth failure" }
                    if (!BuildConfig.DEBUG) userRepository.logout()
                }

                else -> {}
            }
        }
        updating = false
    }
}