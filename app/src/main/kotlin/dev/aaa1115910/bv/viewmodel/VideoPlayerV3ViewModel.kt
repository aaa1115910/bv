package dev.aaa1115910.bv.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuaishou.akdanmaku.data.DanmakuItemData
import com.kuaishou.akdanmaku.render.SimpleRenderer
import com.kuaishou.akdanmaku.ui.DanmakuPlayer
import dev.aaa1115910.biliapi.BiliApi
import dev.aaa1115910.biliapi.entity.video.Dash
import dev.aaa1115910.biliapi.entity.video.PlayUrlData
import dev.aaa1115910.biliapi.entity.video.VideoMoreInfo
import dev.aaa1115910.bilisubtitle.SubtitleParser
import dev.aaa1115910.bilisubtitle.entity.SubtitleItem
import dev.aaa1115910.bv.BVApp
import dev.aaa1115910.bv.component.controllers2.DanmakuType
import dev.aaa1115910.bv.entity.VideoCodec
import dev.aaa1115910.bv.player.AbstractVideoPlayer
import dev.aaa1115910.bv.repository.VideoInfoRepository
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.fException
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.fWarn
import dev.aaa1115910.bv.util.swapList
import dev.aaa1115910.bv.util.swapMap
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mu.KotlinLogging

class VideoPlayerV3ViewModel(
    private val videoInfoRepository: VideoInfoRepository
) : ViewModel() {
    private val logger = KotlinLogging.logger { }

    var videoPlayer: AbstractVideoPlayer? by mutableStateOf(null)
    var danmakuPlayer: DanmakuPlayer? by mutableStateOf(null)
    var show by mutableStateOf(false)

    var loadState by mutableStateOf(RequestState.Ready)
    var errorMessage by mutableStateOf("")

    private var playUrlResponse: PlayUrlData? by mutableStateOf(null)
    var danmakuData = mutableStateListOf<DanmakuItemData>()
    var dashData: Dash? = null

    var availableQuality = mutableStateMapOf<Int, String>()
    var availableVideoCodec = mutableStateListOf<VideoCodec>()
    var availableSubtitle = mutableStateListOf<VideoMoreInfo.SubtitleItem>()
    val availableVideoList get() = videoInfoRepository.videoList

    var currentQuality by mutableStateOf(Prefs.defaultQuality)
    var currentVideoCodec by mutableStateOf(Prefs.defaultVideoCodec)
    var currentDanmakuScale by mutableStateOf(Prefs.defaultDanmakuScale)
    var currentDanmakuOpacity by mutableStateOf(Prefs.defaultDanmakuOpacity)
    var currentDanmakuEnabled by mutableStateOf(Prefs.defaultDanmakuEnabled)
    val currentDanmakuTypes = mutableStateListOf<DanmakuType>().apply {
        addAll(Prefs.defaultDanmakuTypes)
    }
    var currentDanmakuArea by mutableStateOf(Prefs.defaultDanmakuArea)
    var currentSubtitleId by mutableStateOf(-1L)
    var currentSubtitleData = mutableStateListOf<SubtitleItem>()
    var currentSubtitleFontSize by mutableStateOf(Prefs.defaultSubtitleFontSize)
    var currentSubtitleBackgroundOpacity by mutableStateOf(Prefs.defaultSubtitleBackgroundOpacity)
    var currentSubtitleBottomPadding by mutableStateOf(Prefs.defaultSubtitleBottomPadding)

    var title by mutableStateOf("")
    var partTitle by mutableStateOf("")
    var lastPlayed by mutableStateOf(0)
    var fromSeason by mutableStateOf(false)
    var subType by mutableStateOf(0)
    var epid by mutableStateOf(0)
    var seasonId by mutableStateOf(0)

    var needPay by mutableStateOf(false)

    var logs by mutableStateOf("")
    var showLogs by mutableStateOf(false)
    var showBuffering by mutableStateOf(false)

    private var currentAid = 0
    var currentCid = 0

    private suspend fun releaseDanmakuPlayer() = withContext(Dispatchers.Main) {
        danmakuPlayer?.release()
    }

    suspend fun initDanmakuPlayer() = withContext(Dispatchers.Main) {
        danmakuPlayer = DanmakuPlayer(SimpleRenderer())
    }

    fun loadPlayUrl(
        avid: Int,
        cid: Int
    ) {
        showLogs = true
        currentAid = avid
        currentCid = cid
        addLogs("加载视频中")
        viewModelScope.launch(Dispatchers.Default) {
            releaseDanmakuPlayer()
            initDanmakuPlayer()
            addLogs("初始化弹幕引擎")
            addLogs("av$avid，cid:$cid")
            updateSubtitle()
            loadPlayUrl(avid, cid, 4048)
            addLogs("加载弹幕中")
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
        logger.fInfo { "Load play url: [av=$avid, cid=$cid, fnval=$fnval, qn=$qn, fnver=$fnver, fourk=$fourk]" }
        loadState = RequestState.Ready
        logger.fInfo { "Set request state: ready" }
        runCatching {
            val responseData = (
                    if (!fromSeason) BiliApi.getVideoPlayUrl(
                        av = avid,
                        cid = cid,
                        fnval = fnval,
                        qn = qn,
                        fnver = fnver,
                        fourk = fourk,
                        sessData = Prefs.sessData
                    ) else BiliApi.getPgcVideoPlayUrl(
                        av = avid,
                        cid = cid,
                        fnval = fnval,
                        qn = qn,
                        fnver = fnver,
                        fourk = fourk,
                        sessData = Prefs.sessData
                    )).getResponseData()

            //检查是否需要购买，如果未购买，则正片返回的dash为null，非正片例如可以免费观看的预告片等则会返回数据，此时不做提示
            needPay = !responseData.hasPaid && fromSeason && responseData.dash == null
            if (needPay) return@runCatching

            playUrlResponse = responseData
            logger.fInfo { "Load play url response success" }
            //logger.info { "Play url response: $responseData" }

            //读取清晰度
            val resolutionMap = mutableMapOf<Int, String>()
            responseData.dash?.video?.forEach {
                if (!resolutionMap.containsKey(it.id)) {
                    val index = responseData.acceptQuality.indexOf(it.id)
                    resolutionMap[it.id] = responseData.acceptDescription[index]
                }
            }

            logger.fInfo { "Video available resolution: $resolutionMap" }
            availableQuality.swapMap(resolutionMap)

            //先确认最终所选清晰度
            val existDefaultResolution =
                availableQuality.keys.find { it == Prefs.defaultQuality } != null

            if (!existDefaultResolution) {
                val tempList = resolutionMap.keys.sorted()
                currentQuality = tempList.first()
                tempList.forEach {
                    if (it <= Prefs.defaultQuality) {
                        currentQuality = it
                    }
                }
            }

            //再确认最终所选视频编码
            updateAvailableCodec()

            dashData = responseData.dash!!

            playQuality(qn = currentQuality, codec = currentVideoCodec)

        }.onFailure {
            addLogs("加载视频地址失败：${it.localizedMessage}")
            errorMessage = it.stackTraceToString()
            loadState = RequestState.Failed
            logger.fException(it) { "Load video failed" }
        }.onSuccess {
            addLogs("加载视频地址成功")
            loadState = RequestState.Success
            logger.fInfo { "Load play url success" }
        }
    }

    fun updateAvailableCodec() {
        val currentResolutionInfo =
            playUrlResponse!!.supportFormats.find { it.quality == currentQuality }
        val codecList = currentResolutionInfo!!.codecs!!
            .mapNotNull { VideoCodec.fromCodecString(it) }
        availableVideoCodec.swapList(codecList)
        logger.fInfo { "Video available codec: ${availableVideoCodec.toList()}" }

        logger.fInfo { "Default codec: $currentVideoCodec" }
        currentVideoCodec = if (codecList.contains(Prefs.defaultVideoCodec)) {
            Prefs.defaultVideoCodec
        } else {
            codecList.minByOrNull { it.ordinal }!!
        }
        logger.fInfo { "Select codec: $currentVideoCodec" }
    }

    suspend fun playQuality(qn: Int = currentQuality, codec: VideoCodec = currentVideoCodec) {
        logger.fInfo { "Select resolution: $qn, codec: $codec" }
        showLogs = true
        addLogs("播放清晰度：${availableQuality[qn]}, 视频编码：${codec.getDisplayName(BVApp.context)}")

        val videoUrl = dashData!!.video
            .find { it.id == qn && it.codecs.startsWith(codec.prefix) }?.baseUrl
            ?: dashData!!.video[0].baseUrl
        val audioUrl = dashData?.audio?.first()?.baseUrl

        withContext(Dispatchers.Main) {
            videoPlayer!!.playUrl(videoUrl, audioUrl)
            videoPlayer!!.prepare()
            showBuffering = true
        }
    }

    suspend fun loadDanmaku(cid: Int) {
        runCatching {
            val danmakuXmlData = BiliApi.getDanmakuXml(cid = cid, sessData = Prefs.sessData)

            danmakuData.clear()
            danmakuData.addAll(danmakuXmlData.data.map {
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
                    textColor = Color(it.color).toArgb()
                )
            })
            danmakuPlayer?.updateData(danmakuData)
        }.onFailure {
            addLogs("加载弹幕失败：${it.localizedMessage}")
            logger.fWarn { "Load danmaku filed: ${it.stackTraceToString()}" }
        }.onSuccess {
            addLogs("已加载 ${danmakuData.size} 条弹幕")
            logger.fInfo { "Load danmaku success, size=${danmakuData.size}" }
        }
    }

    private suspend fun updateSubtitle() {
        currentSubtitleId = -1
        currentSubtitleData.clear()

        val responseData = runCatching {
            BiliApi.getVideoMoreInfo(
                avid = currentAid,
                cid = currentCid,
                sessData = Prefs.sessData
            ).getResponseData()
        }.getOrNull() ?: return
        availableSubtitle.clear()
        availableSubtitle.add(
            VideoMoreInfo.SubtitleItem(
                id = -1,
                lanDoc = "关闭",
                lan = "",
                isLock = false,
                subtitleUrl = "",
                type = 0,
                idStr = "",
                aiType = 0,
                aiStatus = 0
            )
        )
        availableSubtitle.addAll(responseData.subtitle.subtitles)
        availableSubtitle.sortBy { it.id }
        addLogs("获取到 ${responseData.subtitle.subtitles.size} 条字幕: ${responseData.subtitle.subtitles.map { it.lanDoc }}")
        logger.fInfo { "Update subtitle size: ${responseData.subtitle.subtitles.size}" }
    }

    private fun addLogs(text: String) {
        println(text)
        val lines = logs.lines().toMutableList()
        lines.add(text)
        while (lines.size > 8) {
            lines.removeAt(0)
        }
        var newTip = ""
        lines.forEach {
            newTip += if (newTip == "") it else "\n$it"
        }
        logs = newTip
    }

    suspend fun uploadHistory(time: Int) {
        runCatching {
            if (!fromSeason) {
                logger.info { "Send heartbeat: [avid=$currentAid, cid=$currentCid, time=$time]" }
                BiliApi.sendHeartbeat(
                    avid = currentAid.toLong(),
                    cid = currentCid,
                    playedTime = time,
                    csrf = Prefs.biliJct,
                    sessData = Prefs.sessData
                )
            } else {
                logger.info { "Send heartbeat: [avid=$currentAid, cid=$currentCid, epid=$epid, sid=$seasonId, time=$time]" }
                BiliApi.sendHeartbeat(
                    avid = currentAid.toLong(),
                    cid = currentCid,
                    playedTime = time,
                    type = 4,
                    subType = subType,
                    epid = epid,
                    sid = seasonId,
                    csrf = Prefs.biliJct,
                    sessData = Prefs.sessData
                )
            }
        }.onSuccess {
            logger.info { "Send heartbeat success" }
        }.onFailure {
            logger.warn { "Send heartbeat failed: ${it.stackTraceToString()}" }
        }
    }

    fun loadSubtitle(id: Long) {
        viewModelScope.launch(Dispatchers.Default) {
            if (id == 0L) {
                currentSubtitleData.clear()
                currentSubtitleId = -1
                return@launch
            }
            var subtitleName = ""
            runCatching {
                val subtitle = availableSubtitle.find { it.id == id } ?: return@runCatching
                subtitleName = subtitle.lanDoc
                logger.info { "Subtitle url: ${subtitle.subtitleUrl}" }
                val client = HttpClient(OkHttp)
                val responseText = client.get(subtitle.subtitleUrl).bodyAsText()
                val subtitleData = SubtitleParser.fromBccString(responseText)
                currentSubtitleId = id
                currentSubtitleData.swapList(subtitleData)
            }.onFailure {
                logger.fInfo { "Load subtitle failed: ${it.stackTraceToString()}" }
                addLogs("加载字幕 $subtitleName 失败: ${it.localizedMessage}")
            }.onSuccess {
                logger.fInfo { "Load subtitle $subtitleName success" }
                addLogs("加载字幕 $subtitleName 成功，数量: ${currentSubtitleData.size}")
            }
        }
    }
}