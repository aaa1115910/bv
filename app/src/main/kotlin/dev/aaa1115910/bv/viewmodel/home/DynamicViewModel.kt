package dev.aaa1115910.bv.viewmodel.home

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dev.aaa1115910.biliapi.entity.user.DynamicItem
import dev.aaa1115910.biliapi.entity.user.DynamicVideo
import dev.aaa1115910.biliapi.http.entity.AuthFailureException
import dev.aaa1115910.biliapi.repositories.UserRepository
import dev.aaa1115910.bv.BVApp
import dev.aaa1115910.bv.BuildConfig
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.addAllWithMainContext
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.fWarn
import dev.aaa1115910.bv.util.toast
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import dev.aaa1115910.bv.repository.UserRepository as BvUserRepository

class DynamicViewModel(
    private val bvUserRepository: BvUserRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    val dynamicVideoList = mutableStateListOf<DynamicVideo>()
    val dynamicAllList = mutableStateListOf<DynamicItem>()

    private var currentVideoPage = 0
    var loadingVideo = false
    var videoHasMore = true
    private var videoHistoryOffset: String? = null
    private var videoUpdateBaseline: String? = null

    private var currentAllPage = 0
    var loadingAll by mutableStateOf(false)
    var allHasMore by mutableStateOf(true)
    private var allHistoryOffset: String? = null
    private var allUpdateBaseline: String? = null

    val isLogin get() = bvUserRepository.isLogin

    init {
        println("=====init DynamicViewModel")
    }

    suspend fun loadMoreVideo() {
        if (!loadingVideo) loadVideoData()
    }

    suspend fun loadMoreAll() {
        if (!loadingAll) loadAllData()
    }

    private suspend fun loadVideoData() {
        if (!videoHasMore || !bvUserRepository.isLogin) return
        loadingVideo = true
        logger.fInfo { "Load more dynamic videos [apiType=${Prefs.apiType}, offset=$videoHistoryOffset, page=${currentVideoPage + 1}]" }
        runCatching {
            val dynamicVideoData = userRepository.getDynamicVideos(
                page = ++currentVideoPage,
                offset = videoHistoryOffset ?: "",
                updateBaseline = videoUpdateBaseline ?: "",
                preferApiType = Prefs.apiType
            )
            dynamicVideoList.addAllWithMainContext(dynamicVideoData.videos)
            videoHistoryOffset = dynamicVideoData.historyOffset
            videoUpdateBaseline = dynamicVideoData.updateBaseline
            videoHasMore = dynamicVideoData.hasMore

            logger.fInfo { "Load dynamic video list page: ${currentVideoPage},size: ${dynamicVideoData.videos.size}" }
            val avList = dynamicVideoData.videos.map {
                it.aid
            }
            logger.fInfo { "Load dynamic video size: ${avList.size}" }
            logger.info { "Load dynamic video list ${avList}}" }
        }.onFailure {
            logger.fWarn { "Load dynamic video list failed: ${it.stackTraceToString()}" }
            when (it) {
                is AuthFailureException -> {
                    withContext(Dispatchers.Main) {
                        BVApp.context.getString(R.string.exception_auth_failure)
                            .toast(BVApp.context)
                    }
                    logger.fInfo { "User auth failure" }
                    if (!BuildConfig.DEBUG) bvUserRepository.logout()
                }

                else -> {
                    withContext(Dispatchers.Main) {
                        "加载动态失败: ${it.localizedMessage}".toast(BVApp.context)
                    }
                }
            }
        }
        loadingVideo = false
    }

    private suspend fun loadAllData() {
        if (!allHasMore || !bvUserRepository.isLogin) return
        loadingAll = true
        logger.fInfo { "Load more dynamic all [apiType=${Prefs.apiType}, offset=$allHistoryOffset, page=${currentVideoPage + 1}]" }
        runCatching {
            val dynamicData = userRepository.getDynamics(
                page = ++currentVideoPage,
                offset = allHistoryOffset ?: "",
                updateBaseline = allUpdateBaseline ?: "",
                preferApiType = Prefs.apiType
            )
            dynamicAllList.addAll(dynamicData.dynamics)
            allHistoryOffset = dynamicData.historyOffset
            allUpdateBaseline = dynamicData.updateBaseline
            allHasMore = dynamicData.hasMore

            logger.fInfo { "Load dynamic all list page: ${currentVideoPage},size: ${dynamicData.dynamics.size}" }
        }.onFailure {
            logger.fWarn { "Load dynamic all list failed: ${it.stackTraceToString()}" }
            when (it) {
                is AuthFailureException -> {
                    withContext(Dispatchers.Main) {
                        BVApp.context.getString(R.string.exception_auth_failure)
                            .toast(BVApp.context)
                    }
                    logger.fInfo { "User auth failure" }
                }

                else -> {
                    withContext(Dispatchers.Main) {
                        "加载动态失败: ${it.localizedMessage}".toast(BVApp.context)
                    }
                }
            }
        }
        loadingAll = false
    }

    fun clearVideoData() {
        dynamicVideoList.clear()
        currentVideoPage = 0
        loadingVideo = false
        videoHasMore = true
        videoHistoryOffset = null
    }

    fun clearAllData() {
        dynamicAllList.clear()
        currentAllPage = 0
        loadingAll = false
        allHasMore = true
        allHistoryOffset = null
    }
}