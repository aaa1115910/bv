package dev.aaa1115910.bv.viewmodel.user

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.aaa1115910.biliapi.http.BiliHttpApi
import dev.aaa1115910.biliapi.http.entity.season.FollowingSeason
import dev.aaa1115910.biliapi.http.entity.season.FollowingSeasonStatus
import dev.aaa1115910.biliapi.http.entity.season.FollowingSeasonType
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.fInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mu.KotlinLogging

class FollowingSeasonViewModel : ViewModel() {
    companion object {
        private val logger = KotlinLogging.logger { }
    }

    val followingSeasons = mutableStateListOf<FollowingSeason>()
    var followingSeasonType = FollowingSeasonType.Bangumi
    var followingSeasonStatus = FollowingSeasonStatus.All

    private var pageNumber = 1
    private var pageSize = 30
    var noMore by mutableStateOf(false)
    private var updating = false

    init {
        followingSeasonType = FollowingSeasonType.Bangumi
        followingSeasonStatus = FollowingSeasonStatus.All
        loadMore()
    }

    fun clearData() {
        pageNumber = 1
        pageSize = 30
        updating = false
        noMore = false
        followingSeasons.clear()
    }

    fun loadMore() {
        viewModelScope.launch(Dispatchers.Default) {
            updateData()
        }
    }

    private suspend fun updateData() {
        if (updating) return
        updating = true
        runCatching {
            logger.fInfo { "Init following seasons" }
            val response = BiliHttpApi.getFollowingSeasons(
                type = followingSeasonType,
                status = followingSeasonStatus,
                pageNumber = pageNumber,
                pageSize = pageSize,
                mid = Prefs.uid,
                sessData = Prefs.sessData
            ).getResponseData()
            if (response.pageSize * response.pageNumber >= response.total) {
                noMore = true
            }
            pageNumber++
            followingSeasons.addAll(response.list)
            logger.fInfo { "Following season count: ${response.list.size}" }
        }.onFailure {
            logger.fInfo { "Update following seasons failed: ${it.stackTraceToString()}" }
        }
        updating = false
    }
}

