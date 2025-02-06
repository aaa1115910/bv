package dev.aaa1115910.bv.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dev.aaa1115910.biliapi.entity.ApiType
import dev.aaa1115910.biliapi.entity.video.season.PgcSeason
import dev.aaa1115910.biliapi.entity.video.season.SeasonDetail
import dev.aaa1115910.biliapi.repositories.UserRepository
import dev.aaa1115910.biliapi.repositories.VideoDetailRepository
import dev.aaa1115910.bv.BVApp
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.entity.proxy.ProxyArea
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.swapList
import dev.aaa1115910.bv.util.toast
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.UUID

class SeasonViewModel(
    private val videoDetailRepository: VideoDetailRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    var seasonId by mutableStateOf<Int?>(null)
    var epId by mutableStateOf<Int?>(null)
    var proxyArea by mutableStateOf(ProxyArea.MainLand)

    val seasons = mutableStateListOf<PgcSeason>()
    var seasonData by mutableStateOf<SeasonDetail?>(null)

    var isFollowing by mutableStateOf(false)
    var lastPlayProgress by mutableStateOf<SeasonDetail.UserStatus.Progress?>(null)

    var tip by mutableStateOf("Loading")

    val uuid: String = UUID.randomUUID().toString()

    suspend fun updateSeasonData() {
        runCatching {
            val data = videoDetailRepository.getPgcVideoDetail(
                seasonId = seasonId,
                epid = epId,
                preferApiType = if (proxyArea != ProxyArea.MainLand) ApiType.App else Prefs.apiType
            )
            withContext(Dispatchers.Main) {
                seasonData = data.copy()
                seasons.swapList(data.seasons)
                isFollowing = data.userStatus.follow
                lastPlayProgress = data.userStatus.progress
            }
            logger.fInfo { "Get season info success, seasonData: ${seasonData}" }
        }.onFailure {
            tip = it.localizedMessage ?: "未知错误"
            logger.fInfo { "Get season info failed: ${it.stackTraceToString()}" }
        }
    }

    suspend fun updateLastPlayProgress() {
        //延迟 200ms，避免获取到的依旧是旧数据
        delay(200)
        runCatching {
            val data = videoDetailRepository.getPgcVideoDetail(
                seasonId = seasonId,
                epid = epId,
                preferApiType = if (proxyArea != ProxyArea.MainLand) ApiType.App else Prefs.apiType
            ).userStatus.progress
            withContext(Dispatchers.Main) { lastPlayProgress = data }
            logger.info { "update user status progress: $lastPlayProgress" }
        }.onFailure {
            logger.fInfo { "update user status progress failed: ${it.stackTraceToString()}" }
        }
    }

    suspend fun followSeason() {
        runCatching {
            val resultToast = userRepository.addSeasonFollow(
                seasonId = seasonData?.seasonId ?: return@runCatching,
                preferApiType = Prefs.apiType
            )
            isFollowing = true
            withContext(Dispatchers.Main) {
                resultToast.toast(BVApp.context)
            }
        }.onFailure {
            logger.fInfo { "Add season follow failed: ${it.stackTraceToString()}" }
            withContext(Dispatchers.Main) {
                R.string.follow_bangumi_enable_fail.toast(BVApp.context)
            }
        }
    }

    suspend fun unFollowSeason() {
        runCatching {
            val resultToast = userRepository.delSeasonFollow(
                seasonId = seasonData?.seasonId ?: return@runCatching,
                preferApiType = Prefs.apiType
            )
            isFollowing = false
            withContext(Dispatchers.Main) {
                resultToast.toast(BVApp.context)
            }
        }.onFailure {
            logger.fInfo { "Del season follow failed: ${it.stackTraceToString()}" }
            withContext(Dispatchers.Main) {
                R.string.follow_bangumi_disable_fail.toast(BVApp.context)
            }
        }
    }
}