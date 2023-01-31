package dev.aaa1115910.bv.viewmodel.user

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.aaa1115910.biliapi.BiliApi
import dev.aaa1115910.bv.entity.carddata.VideoCardData
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.fInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mu.KotlinLogging

class UpInfoViewModel : ViewModel() {
    companion object {
        private val logger = KotlinLogging.logger { }
    }

    var upName by mutableStateOf("")
    var upMid by mutableStateOf(0L)
    var spaceVideos = mutableStateListOf<VideoCardData>()

    private var pageNumber = 1
    private var pageSize = 30

    private var updating = false
    var noMore = false

    fun update() {
        viewModelScope.launch(Dispatchers.Default) {
            updateSpaceVideos()
        }
    }

    private suspend fun updateSpaceVideos() {
        if (updating || noMore) return
        logger.fInfo { "Updating up space videos from page $pageNumber" }
        updating = true
        runCatching {
            val responseData = BiliApi.getUserSpaceVideos(
                mid = upMid,
                pageNumber = pageNumber,
                pageSize = pageSize,
                sessData = Prefs.sessData
            ).getResponseData()
            val videoList = responseData.list.vlist
            if (videoList.isEmpty()) noMore = true
            videoList.forEach { spaceVideoItem ->
                spaceVideos.add(
                    VideoCardData(
                        avid = spaceVideoItem.aid.toInt(),
                        title = spaceVideoItem.title,
                        cover = spaceVideoItem.pic,
                        upName = spaceVideoItem.author,
                        timeString = spaceVideoItem.length
                    )
                )
            }
            logger.fInfo { "Update up space videos success" }
        }.onFailure {
            logger.fInfo { "Update up space videos failed: ${it.stackTraceToString()}" }
        }.onSuccess {
            pageNumber++
        }
        updating = false
    }

}