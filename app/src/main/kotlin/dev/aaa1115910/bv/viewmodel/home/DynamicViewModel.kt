package dev.aaa1115910.bv.viewmodel.home

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import dev.aaa1115910.biliapi.BiliApi
import dev.aaa1115910.biliapi.entity.dynamic.DynamicItem
import dev.aaa1115910.bv.BVApp
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import mu.KotlinLogging

class DynamicViewModel : ViewModel() {
    private val logger = KotlinLogging.logger {}
    val dynamicList = mutableStateListOf<DynamicItem>()

    private var currentPage = 0
    var loading = false
    var hasMore = true
    private var offset: String? = null

    @Suppress("RedundantSuspendModifier")
    suspend fun loadMore() {
        if (!loading) loadData()
    }

    private suspend fun loadData() {
        if (!hasMore) return
        loading = true
        logger.info { "Load more dynamic videos" }
        runCatching {
            val response = runBlocking {
                BiliApi.getDynamicList(
                    page = ++currentPage,
                    type = "video",
                    offset = offset,
                    sessData = Prefs.sessData
                )
            }
            dynamicList.addAll(response.data?.items ?: emptyList())
            offset = response.data!!.offset

            logger.info { "Load dynamic list page: ${currentPage},size: ${response.data?.items?.size}" }
            val avList = response.data?.items?.map {
                it.modules.moduleDynamic.major!!.archive!!.aid
            }
            logger.info { "Load dynamic list ${avList}}" }

            hasMore = response.data!!.hasMore
        }.onFailure {
            logger.error { "Load dynamic list failed: ${it.stackTraceToString()}" }
            withContext(Dispatchers.Main) {
                "加载动态失败: ${it.localizedMessage}".toast(BVApp.context)
            }
        }
        loading = false
    }
}