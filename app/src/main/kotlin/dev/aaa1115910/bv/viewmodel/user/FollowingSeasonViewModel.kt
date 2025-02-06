package dev.aaa1115910.bv.viewmodel.user

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.aaa1115910.biliapi.entity.season.FollowingSeason
import dev.aaa1115910.biliapi.entity.season.FollowingSeasonStatus
import dev.aaa1115910.biliapi.entity.season.FollowingSeasonType
import dev.aaa1115910.biliapi.repositories.SeasonRepository
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.fInfo
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FollowingSeasonViewModel(
    private val seasonRepository: SeasonRepository
) : ViewModel() {
    companion object {
        private val logger = KotlinLogging.logger { }
    }

    val followingSeasons = mutableStateListOf<FollowingSeason>()
    var followingSeasonType by mutableStateOf(FollowingSeasonType.Bangumi)
    var followingSeasonStatus by mutableStateOf(FollowingSeasonStatus.All)

    private var pageNumber = 1
    private var pageSize = 30
    var noMore by mutableStateOf(false)
    var updating by mutableStateOf(false)

    init {
        followingSeasonType = FollowingSeasonType.Bangumi
        followingSeasonStatus = FollowingSeasonStatus.All
    }

    fun clearData() {
        pageNumber = 1
        pageSize = 30
        updating = false
        noMore = false
        followingSeasons.clear()
    }

    fun loadMore() {
        viewModelScope.launch(Dispatchers.IO) {
            updateData()
        }
    }

    private suspend fun updateData() {
        if (updating) return
        updating = true
        runCatching {
            logger.fInfo { "Updating following season data" }
            val response = seasonRepository.getFollowingSeasons(
                type = followingSeasonType,
                status = followingSeasonStatus,
                pageNumber = pageNumber,
                pageSize = pageSize,
                preferApiType = Prefs.apiType
            )
            withContext(Dispatchers.Main) {
                if (pageSize * pageNumber >= response.total) noMore = true
                pageNumber++
                followingSeasons.addAll(response.list)
            }
            logger.fInfo { "Following season count: ${response.list.size}" }
        }.onFailure {
            logger.fInfo { "Update following seasons failed: ${it.stackTraceToString()}" }
        }
        updating = false
    }
}

