package dev.aaa1115910.bv.screen

import android.app.Activity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerConfigData
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerDanmakuMasksData
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerHistoryData
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerLoadStateData
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerLogsData
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerPaymentData
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerSeekThumbData
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerVideoInfoData
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerVideoShotData
import dev.aaa1115910.bv.player.entity.VideoPlayerConfigData
import dev.aaa1115910.bv.player.entity.VideoPlayerDanmakuMasksData
import dev.aaa1115910.bv.player.entity.VideoPlayerHistoryData
import dev.aaa1115910.bv.player.entity.VideoPlayerLoadStateData
import dev.aaa1115910.bv.player.entity.VideoPlayerLogsData
import dev.aaa1115910.bv.player.entity.VideoPlayerPaymentData
import dev.aaa1115910.bv.player.entity.VideoPlayerSeekThumbData
import dev.aaa1115910.bv.player.entity.VideoPlayerVideoInfoData
import dev.aaa1115910.bv.player.entity.VideoPlayerVideoShotData
import dev.aaa1115910.bv.player.tv.BvPlayer
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.swapList
import dev.aaa1115910.bv.viewmodel.VideoPlayerV3ViewModel
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun VideoPlayerV3Screen(
    modifier: Modifier = Modifier,
    playerViewModel: VideoPlayerV3ViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val logger = KotlinLogging.logger { }

    CompositionLocalProvider(
        LocalVideoPlayerSeekThumbData provides VideoPlayerSeekThumbData(
            idleIcon = playerViewModel.playerIconIdle,
            movingIcon = playerViewModel.playerIconMoving
        ),
        LocalVideoPlayerVideoInfoData provides VideoPlayerVideoInfoData(
            width = playerViewModel.currentVideoWidth,
            height = playerViewModel.currentVideoHeight,
            codec = playerViewModel.currentVideoCodec.name,
            title = playerViewModel.title,
            partTitle = playerViewModel.partTitle,
        ),
        LocalVideoPlayerLogsData provides VideoPlayerLogsData(
            logs = playerViewModel.logs
        ),
        LocalVideoPlayerHistoryData provides VideoPlayerHistoryData(
            lastPlayed = playerViewModel.lastPlayed,
        ),
        LocalVideoPlayerPaymentData provides VideoPlayerPaymentData(
            needPay = playerViewModel.needPay,
            epid = playerViewModel.epid,
        ),
        LocalVideoPlayerLoadStateData provides VideoPlayerLoadStateData(
            loadState = playerViewModel.loadState,
            errorMessage = playerViewModel.errorMessage,
        ),
        LocalVideoPlayerConfigData provides VideoPlayerConfigData(
            availableResolutionMap = playerViewModel.availableQuality,
            availableVideoCodec = playerViewModel.availableVideoCodec,
            availableAudio = playerViewModel.availableAudio,
            availableSubtitleTracks = playerViewModel.availableSubtitle,
            availableVideoList = playerViewModel.availableVideoList,
            currentVideoCid = playerViewModel.currentCid,
            currentResolution = playerViewModel.currentQuality,
            currentVideoCodec = playerViewModel.currentVideoCodec,
            currentVideoAspectRatio = playerViewModel.currentVideoAspectRatio,
            currentVideoSpeed = playerViewModel.currentPlaySpeed,
            currentAudio = playerViewModel.currentAudio,
            currentDanmakuEnabled = playerViewModel.currentDanmakuEnabled,
            currentDanmakuEnabledList = playerViewModel.currentDanmakuTypes,
            currentDanmakuScale = playerViewModel.currentDanmakuScale,
            currentDanmakuOpacity = playerViewModel.currentDanmakuOpacity,
            currentDanmakuArea = playerViewModel.currentDanmakuArea,
            currentDanmakuMask = playerViewModel.currentDanmakuMask,
            currentSubtitleId = playerViewModel.currentSubtitleId,
            currentSubtitleData = playerViewModel.currentSubtitleData,
            currentSubtitleFontSize = playerViewModel.currentSubtitleFontSize,
            currentSubtitleBackgroundOpacity = playerViewModel.currentSubtitleBackgroundOpacity,
            currentSubtitleBottomPadding = playerViewModel.currentSubtitleBottomPadding,
            incognitoMode = Prefs.incognitoMode,
        ),
        LocalVideoPlayerDanmakuMasksData provides VideoPlayerDanmakuMasksData(
            danmakuMasks = playerViewModel.danmakuMasks,
        ),
        LocalVideoPlayerVideoShotData provides VideoPlayerVideoShotData(
            videoShot = playerViewModel.videoShot,
        ),
    ) {
        BvPlayer(
            modifier = modifier.fillMaxSize(),
            videoPlayer = playerViewModel.videoPlayer!!,
            danmakuPlayer = playerViewModel.danmakuPlayer,
            onSendHeartbeat = playerViewModel::uploadHistory,
            onClearBackToHistoryData = { playerViewModel.lastPlayed = 0 },
            onLoadNextVideo = {
                val videoListIndex = playerViewModel.availableVideoList.indexOfFirst {
                    it.cid == playerViewModel.currentCid
                }
                if (videoListIndex + 1 < playerViewModel.availableVideoList.size) {
                    val nextVideo = playerViewModel.availableVideoList[videoListIndex + 1]
                    logger.info { "Play next video: $nextVideo" }
                    playerViewModel.partTitle = nextVideo.title
                    playerViewModel.loadPlayUrl(
                        avid = nextVideo.aid,
                        cid = nextVideo.cid,
                        epid = nextVideo.epid,
                        seasonId = nextVideo.seasonId,
                        continuePlayNext = true
                    )
                }
            },
            onExit = { (context as Activity).finish() },
            onLoadNewVideo = { videoListItem ->
                playerViewModel.partTitle = videoListItem.title
                playerViewModel.loadPlayUrl(
                    avid = videoListItem.aid,
                    cid = videoListItem.cid,
                    epid = videoListItem.epid,
                    seasonId = videoListItem.seasonId,
                    continuePlayNext = true
                )
            },
            onResolutionChange = { resolutionCode, afterChange ->
                scope.launch(Dispatchers.Default) {
                    playerViewModel.updateAvailableCodec()
                    playerViewModel.playQuality(resolutionCode)
                    afterChange()
                    playerViewModel.currentQuality = resolutionCode
                }
            },
            onCodecChange = { videoCodec, afterChange ->
                playerViewModel.currentVideoCodec = videoCodec
                scope.launch(Dispatchers.Default) {
                    playerViewModel.playQuality(
                        playerViewModel.currentQuality,
                        playerViewModel.currentVideoCodec
                    )
                    afterChange()
                }
            },
            onAspectRatioChange = { aspectRatio ->
                playerViewModel.currentVideoAspectRatio = aspectRatio
            },
            onPlaySpeedChange = { speed ->
                Prefs.defaultPlaySpeed = speed
                playerViewModel.currentPlaySpeed = speed
            },
            onAudioChange = { audio, afterChange ->
                playerViewModel.currentAudio = audio
                scope.launch(Dispatchers.Default) {
                    playerViewModel.updateAvailableCodec()
                    playerViewModel.playQuality(audio = audio)
                    afterChange()
                }
            },
            onDanmakuSwitchChange = { enabledDanmakuTypes ->
                Prefs.defaultDanmakuTypes = enabledDanmakuTypes
                playerViewModel.currentDanmakuTypes.swapList(enabledDanmakuTypes)
            },
            onDanmakuSizeChange = { scale ->
                Prefs.defaultDanmakuScale = scale
                playerViewModel.currentDanmakuScale = scale
            },
            onDanmakuOpacityChange = { opacity ->
                Prefs.defaultDanmakuOpacity = opacity
                playerViewModel.currentDanmakuOpacity = opacity
            },
            onDanmakuAreaChange = { area ->
                Prefs.defaultDanmakuArea = area
                playerViewModel.currentDanmakuArea = area
            },
            onDanmakuMaskChange = { mask ->
                Prefs.defaultDanmakuMask = mask
                playerViewModel.currentDanmakuMask = mask
            },
            onSubtitleChange = { subtitle ->
                playerViewModel.loadSubtitle(subtitle.id)
            },
            onSubtitleSizeChange = { size ->
                Prefs.defaultSubtitleFontSize = size
                playerViewModel.currentSubtitleFontSize = size
            },
            onSubtitleBackgroundOpacityChange = { opacity ->
                Prefs.defaultSubtitleBackgroundOpacity = opacity
                playerViewModel.currentSubtitleBackgroundOpacity = opacity
            },
            onSubtitleBottomPadding = { padding ->
                Prefs.defaultSubtitleBottomPadding = padding
                playerViewModel.currentSubtitleBottomPadding = padding
            },
        )
    }
}
