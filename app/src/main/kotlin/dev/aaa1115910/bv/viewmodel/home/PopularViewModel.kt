package dev.aaa1115910.bv.viewmodel.home

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import dev.aaa1115910.biliapi.entity.rank.PopularVideo
import dev.aaa1115910.biliapi.entity.rank.PopularVideoPage
import dev.aaa1115910.biliapi.repositories.PopularVideoRepository
import dev.aaa1115910.bv.BVApp
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.fError
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mu.KotlinLogging

class PopularViewModel(
    private val popularVideoRepository: PopularVideoRepository
) : ViewModel() {
    private val logger = KotlinLogging.logger {}
    val popularVideoList = mutableStateListOf<PopularVideo>()

    private var nextPage = PopularVideoPage()
    var loading = false

    suspend fun loadMore() {
        if (!loading) loadData()
    }

    private suspend fun loadData() {
        loading = true
        logger.fInfo { "Load more popular videos" }
        runCatching {
            val popularVideoData = popularVideoRepository.getPopularVideos(
                page = nextPage,
                preferApiType = Prefs.apiType
            )
            nextPage = popularVideoData.nextPage
            popularVideoList.addAll(popularVideoData.list)
        }.onFailure {
            logger.fError { "Load popular video list failed: ${it.stackTraceToString()}" }
            withContext(Dispatchers.Main) {
                "加载热门视频失败: ${it.localizedMessage}".toast(BVApp.context)
            }
        }
        loading = false
    }

    fun clearData() {
        popularVideoList.clear()
        nextPage = PopularVideoPage()
        loading = false
    }
}