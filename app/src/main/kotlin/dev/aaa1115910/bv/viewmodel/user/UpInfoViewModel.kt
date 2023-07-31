package dev.aaa1115910.bv.viewmodel.user

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.aaa1115910.biliapi.repositories.UserRepository
import dev.aaa1115910.bv.entity.carddata.VideoCardData
import dev.aaa1115910.bv.util.fInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mu.KotlinLogging

class UpInfoViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    companion object {
        private val logger = KotlinLogging.logger { }
    }

    var upName by mutableStateOf("")
    var upMid by mutableLongStateOf(0L)
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
            val videoList = userRepository.getSpaceVideos(
                mid = upMid,
                pageNumber = pageNumber,
                pageSize = pageSize
            )
            if (videoList.isEmpty()) noMore = true
            videoList.forEach { spaceVideoItem ->
                spaceVideos.add(
                    VideoCardData(
                        avid = spaceVideoItem.aid,
                        title = spaceVideoItem.title,
                        //TODO 这里在改造 app 端接口时，没找到在空间内显示为合集样式封面的UP,没法进一步测试接口
                        cover = spaceVideoItem.cover,
                        upName = spaceVideoItem.author,
                        time = spaceVideoItem.duration * 1000L
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