package dev.aaa1115910.bv.viewmodel.home

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import dev.aaa1115910.biliapi.BiliApi
import dev.aaa1115910.biliapi.entity.dynamic.DynamicItem
import dev.aaa1115910.bv.BVApp
import dev.aaa1115910.bv.repository.UserRepository
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import mu.KotlinLogging

class DynamicViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    private val logger = KotlinLogging.logger {}
    val dynamicList = mutableStateListOf<DynamicItem>()

    private var currentPage = 0
    var loading = false
    var hasMore = true
    private var offset: String? = null
    val isLogin get() = userRepository.isLogin

    suspend fun loadMore() {
        if (!loading) loadData()
    }

    private suspend fun loadData() {
        if (!hasMore || !userRepository.isLogin) return
        loading = true
        logger.info { "Load more dynamic videos" }
        runCatching {
            val responseData = runBlocking {
                BiliApi.getDynamicList(
                    page = ++currentPage,
                    type = "video",
                    offset = offset,
                    sessData = Prefs.sessData
                ).getResponseData()
            }
            dynamicList.addAll(responseData.items)
            offset = responseData.offset

            logger.info { "Load dynamic list page: ${currentPage},size: ${responseData.items.size}" }
            val avList = responseData.items.map {
                it.modules.moduleDynamic.major!!.archive!!.aid
            }
            logger.info { "Load dynamic list ${avList}}" }

            hasMore = responseData.hasMore
        }.onFailure {
            logger.error { "Load dynamic list failed: ${it.stackTraceToString()}" }
            withContext(Dispatchers.Main) {
                "加载动态失败: ${it.localizedMessage}".toast(BVApp.context)
            }
        }
        loading = false
    }

    fun clearData() {
        dynamicList.clear()
        currentPage = 0
        loading = false
        hasMore = true
        offset = null
    }
}