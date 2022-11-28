package dev.aaa1115910.bv.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import dev.aaa1115910.biliapi.BiliApi
import dev.aaa1115910.biliapi.entity.video.VideoInfo
import dev.aaa1115910.bv.BVApp
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.toast
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging

class HomeViewModel : ViewModel() {
    private val logger = KotlinLogging.logger {}
    val popularVideoList = mutableStateListOf<VideoInfo>()

    private var currentPage = 0
    private var pageSize = 20
    var loading = false

    @Suppress("RedundantSuspendModifier")
    suspend fun loadMore() {
        if (!loading) loadData()
    }

    @Synchronized
    private fun loadData() {
        loading = true
        logger.info { "Load more popular videos" }
        runCatching {
            val response = runBlocking {
                BiliApi.getPopularVideoData(
                    pageNumber = ++currentPage,
                    pageSize = pageSize,
                    sessData = Prefs.sessData
                )
            }
            popularVideoList.addAll(response.data.list)
        }.onFailure {
            "加载热门视频失败: ${it.localizedMessage}".toast(BVApp.context)
            logger.error { "Load popular video list failed: ${it.stackTraceToString()}" }
        }
        loading = false
    }
}