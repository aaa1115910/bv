package dev.aaa1115910.bv.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.aaa1115910.biliapi.http.BiliHttpApi
import dev.aaa1115910.bv.entity.carddata.VideoCardData
import dev.aaa1115910.bv.util.addWithMainContext
import dev.aaa1115910.bv.util.fInfo
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TagViewModel : ViewModel() {
    companion object {
        private val logger = KotlinLogging.logger { }
    }

    var tagName by mutableStateOf("")
    var tagId by mutableIntStateOf(0)
    var topVideos = mutableStateListOf<VideoCardData>()

    private var pageNumber = 1
    private var pageSize = 40
    private var total = -1

    private var updating = false
    var noMore = false

    fun update() {
        viewModelScope.launch(Dispatchers.Default) {
            updateTagVideos()
        }
    }

    private suspend fun updateTagVideos() {
        if (total != -1 && pageNumber * pageSize >= total) {
            noMore = true
            return
        }
        if (updating) return
        updating = true
        runCatching {
            val response = BiliHttpApi.getTagTopVideos(
                tagId = tagId,
                pageNumber = pageNumber,
                pageSize = pageSize
            )
            total = response.total
            val videoList = response.data
            if (videoList.isEmpty()) noMore = true
            videoList.forEach { tagVideoItem ->
                topVideos.addWithMainContext(
                    VideoCardData(
                        avid = tagVideoItem.aid,
                        title = tagVideoItem.title,
                        cover = tagVideoItem.pic,
                        upName = tagVideoItem.owner.name,
                        play = tagVideoItem.stat.view,
                        danmaku = tagVideoItem.stat.danmaku,
                        time = tagVideoItem.duration.toLong()
                    )
                )
            }
            logger.fInfo { "Update tag top videos success" }
        }.onFailure {
            logger.fInfo { "Update tag top videos failed: ${it.stackTraceToString()}" }
        }.onSuccess {
            pageNumber++
        }
        updating = false
    }
}