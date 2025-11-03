package dev.aaa1115910.bv.viewmodel.user

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.aaa1115910.biliapi.entity.ugc.toSmartDate
import dev.aaa1115910.biliapi.entity.user.SpaceVideoPage
import dev.aaa1115910.biliapi.entity.user.SpaceVideo
import dev.aaa1115910.biliapi.repositories.UserRepository
import dev.aaa1115910.bv.entity.carddata.VideoCardData
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.addWithMainContext
import dev.aaa1115910.bv.util.fInfo
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class UserSpaceViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    companion object {
        private val logger = KotlinLogging.logger { }
    }

    var upFace by mutableStateOf("")
    var upName by mutableStateOf("")
    var sign by mutableStateOf("") // 个性签名
    var fans by mutableIntStateOf(0) // 粉丝数
    var friend by mutableIntStateOf(0) // 关注数
    var upMid by mutableLongStateOf(0L)
    var tvSpaceVideos = mutableStateListOf<VideoCardData>()
    var spaceVideos = mutableStateListOf<SpaceVideo>()

    private var page = SpaceVideoPage()
    private var updating = false
    val noMore get() = !page.hasNext

    fun update() {
        viewModelScope.launch(Dispatchers.Default) {
            updateSpaceVideos()
        }
    }

    private suspend fun updateSpaceVideos() {
        if (updating || noMore) return
        logger.fInfo { "Updating up [mid=$upMid] space videos from page $page" }
        updating = true
        runCatching {
            val spaceVideoData = userRepository.getSpaceVideos(
                mid = upMid,
                page = page,
                preferApiType = Prefs.apiType
            )
            spaceVideos.addAll(spaceVideoData.videos)
            spaceVideoData.videos.forEach { spaceVideoItem ->
                tvSpaceVideos.addWithMainContext(
                    VideoCardData(
                        avid = spaceVideoItem.aid,
                        title = spaceVideoItem.title,
                        //TODO 这里在改造 app 端接口时，没找到在空间内显示为合集样式封面的UP,没法进一步测试接口
                        cover = spaceVideoItem.cover,
                        play = spaceVideoItem.play,
                        danmaku = spaceVideoItem.danmaku,
                        upName = spaceVideoItem.author,
                        time = spaceVideoItem.duration * 1000L,
                        pubTime = spaceVideoItem.publishDate.getTime().toSmartDate(),
                        isChargingArc = spaceVideoItem.isChargingArc
                    )
                )
            }
            page = spaceVideoData.page
            logger.fInfo { "Update up space videos success" }
        }.onFailure {
            logger.fInfo { "Update up space videos failed: ${it.stackTraceToString()}" }
        }
        updating = false
    }
}