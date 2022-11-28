package dev.aaa1115910.bv.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MergingMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.kuaishou.akdanmaku.data.DanmakuItemData
import com.kuaishou.akdanmaku.ui.DanmakuPlayer
import dev.aaa1115910.biliapi.BiliApi
import dev.aaa1115910.biliapi.entity.video.Dash
import dev.aaa1115910.bv.Keys
import dev.aaa1115910.bv.RequestState
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.swapMap
import dev.aaa1115910.bv.util.toAndroidColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mu.KotlinLogging

class PlayerViewModel : ViewModel() {
    var player: ExoPlayer? by mutableStateOf(null)
    var danmakuPlayer: DanmakuPlayer? by mutableStateOf(null)
    var show by mutableStateOf(false)
    var showingRightMenu by mutableStateOf(false)

    var lastPressedKey by mutableStateOf(Keys.Other)
    var lastPressedTime by mutableStateOf(0L)
    var lastConsumeTime by mutableStateOf(0L)

    var loadState by mutableStateOf(RequestState.Ready)
    var errorMessage by mutableStateOf("")

    var availableQuality = mutableStateMapOf<Int, String>()
    var currentQuality by mutableStateOf(0)

    var danmakuData = mutableStateListOf<DanmakuItemData>()

    var dashData: Dash? = null

    companion object {
        private val logger = KotlinLogging.logger { }
    }

    fun preparePlayer(player: ExoPlayer) {
        logger.info { "Set player" }
        this.player = player
        show = true
    }

    fun prepareDanmakuPlayer(danmakuPlayer: DanmakuPlayer) {
        logger.info { "Set danmaku plauer" }
        this.danmakuPlayer = danmakuPlayer
    }

    init {
        initData()
    }

    fun initData() {

    }

    fun loadPlayUrl(
        avid: Int,
        cid: Int
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            loadPlayUrl(avid, cid, 4048)
            loadDanmaku(cid)
        }
    }

    private suspend fun loadPlayUrl(
        avid: Int,
        cid: Int,
        fnval: Int = 4048,
        qn: Int = 80,
        fnver: Int = 0,
        fourk: Int = 0
    ) {
        logger.info { "Load play url: [av=$avid, cid=$cid, fnval=$fnval, qn=$qn, fnver=$fnver, fourk=$fourk]" }
        loadState = RequestState.Ready
        logger.info { "Set request state: ready" }
        runCatching {
            val response = BiliApi.getVideoPlayUrl(
                av = avid,
                cid = cid,
                fnval = fnval,
                qn = qn,
                fnver = fnver,
                fourk = fourk,
                sessData = Prefs.sessData
            )
            logger.info { "Load play url response: $response" }
            if (response.code != 0) {
                logger.info { "Error code, finish method" }
                errorMessage = response.message
                loadState = RequestState.Failed
                return
            }

            //读取清晰度
            val qualityMap = mutableMapOf<Int, String>()
            val qualityIdList = response.data?.dash?.video
                ?.map { it.id }?.toHashSet()?.toList() ?: emptyList()
            qualityIdList.forEach { qualityId ->
                val index = response.data?.acceptQuality?.indexOf(qualityId) ?: -1
                qualityMap[qualityId] = response.data?.acceptDescription?.get(index) ?: "未知清晰度"
            }
            logger.info { "Video available quality: $qualityMap" }
            availableQuality.swapMap(qualityMap)
            currentQuality = response.data!!.dash!!.video[0].id

            dashData = response.data!!.dash!!

            playQuality(qn)

        }.onFailure {
            errorMessage = it.stackTraceToString()
            loadState = RequestState.Failed
            logger.warn { "Load video filed: ${it.message}" }
            logger.error { it.stackTraceToString() }
        }.onSuccess {
            loadState = RequestState.Success
            logger.warn { "Load play url success" }
        }
    }

    suspend fun playQuality(qn: Int = 80) {
        val videoUrl = dashData!!.video
            .find { it.id == qn }
            ?.baseUrl
            ?: dashData!!.video[0].baseUrl
        val audioUrl = dashData!!.audio
            .find { it.id == qn }
            ?.baseUrl
            ?: dashData!!.audio[0].baseUrl
        val videoMediaItem = MediaItem.fromUri(videoUrl)
        val audioMediaItem = MediaItem.fromUri(audioUrl)

        val userAgent =
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36"
        val defaultHttpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent(userAgent)
            .setDefaultRequestProperties(
                mapOf(
                    "referer" to "https://www.bilibili.com"
                )
            )

        val videoMediaSource =
            ProgressiveMediaSource.Factory(defaultHttpDataSourceFactory)
                .createMediaSource(videoMediaItem)
        val audioMediaSource =
            ProgressiveMediaSource.Factory(defaultHttpDataSourceFactory)
                .createMediaSource(audioMediaItem)
        //set data
        val mms = MergingMediaSource(videoMediaSource, audioMediaSource)

        withContext(Dispatchers.Main) {
            player!!.setMediaSource(mms)
            player!!.prepare()
            player!!.playWhenReady = true
        }
    }

    suspend fun loadDanmaku(cid: Int) {
        runCatching {
            val test = BiliApi.getDanmakuXml(cid = cid, sessData = Prefs.sessData)
            danmakuData.addAll(test.data.map {
                DanmakuItemData(
                    danmakuId = it.dmid,
                    position = (it.time * 1000).toLong(),
                    content = it.text,
                    mode = when (it.type) {
                        4 -> DanmakuItemData.DANMAKU_MODE_CENTER_TOP
                        5 -> DanmakuItemData.DANMAKU_MODE_CENTER_BOTTOM
                        else -> DanmakuItemData.DANMAKU_MODE_ROLLING
                    },
                    textSize = it.size,
                    textColor = Color(it.color).toAndroidColor()
                )
            })
            danmakuPlayer?.updateData(danmakuData)
        }.onFailure {
            withContext(Dispatchers.Main) {
                "Load danmaku failed: ${it.message}"
            }
            logger.warn { "Load danmaku filed: ${it.message}" }
        }.onSuccess {
            withContext(Dispatchers.Main) {
                "Load danmaku success: ${danmakuData.size}"
            }
            logger.warn { "Load danmaku success: ${danmakuData.size}" }
        }
    }
}