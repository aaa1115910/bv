package dev.aaa1115910.bv.tv.screens

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.MaterialTheme
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.tv.activities.video.SeasonInfoActivity
import dev.aaa1115910.bv.tv.activities.video.VideoInfoActivity
import dev.aaa1115910.bv.entity.proxy.ProxyArea
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerConfigData
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerDanmakuMasksData
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerHistoryData
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerLoadStateData
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerLogsData
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerPaymentData
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerSeekThumbData
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerVideoInfoData
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerVideoShotData
import dev.aaa1115910.bv.player.entity.VideoListItemData
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
import dev.aaa1115910.bv.player.tv.controller.SkipTip
import dev.aaa1115910.bv.tv.activities.video.UpInfoActivity
import dev.aaa1115910.bv.tv.component.buttons.CoinButton
import dev.aaa1115910.bv.tv.component.buttons.FavoriteButton
import dev.aaa1115910.bv.tv.component.buttons.LikeButton
import dev.aaa1115910.bv.tv.manager.VideoUserActionManager
import dev.aaa1115910.bv.tv.manager.VideoUserActionManager.getStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import dev.aaa1115910.bv.tv.component.videocard.VideosRow
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.toast
import dev.aaa1115910.bv.util.formatHourMinSec
import dev.aaa1115910.bv.util.swapList
import dev.aaa1115910.bv.viewmodel.VideoPlayerV3ViewModel
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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

    // subscribe shared action state by aid
    val currentAid = playerViewModel.currentAid
    val sharedActionFlow = remember(currentAid) { getStateFlow(currentAid, Prefs.uid) }
    val sharedActionState by sharedActionFlow.collectAsState()

    // 倒计时相关状态
    var autoActionCountdownJob by remember { mutableStateOf<Job?>(null) }
    var autoActionTipVisible by remember { mutableStateOf(false) }
    var autoActionTipText by remember { mutableStateOf("") }
    
    // 焦点管理
    val relatedVideosFocusRequester = remember { FocusRequester() }
    
    // 当显示相关视频时，自动将焦点转移到VideosRow的第一个卡片
    LaunchedEffect(playerViewModel.showRelatedVideos) {
        if (playerViewModel.showRelatedVideos) {
            delay(300)
            kotlin.runCatching {
                relatedVideosFocusRequester.requestFocus()
            }
        }
    }
    
    // 处理back键，当推荐视频有焦点时隐藏推荐视频并将焦点返回到播放器
    BackHandler(enabled = playerViewModel.showRelatedVideos) {
        playerViewModel.showRelatedVideos = false
    }

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
            play = playerViewModel.play,
            danmaku = playerViewModel.danmaku,
            upName = playerViewModel.upName,
            pubTime = playerViewModel.pubTime,
            fromSeason = playerViewModel.fromSeason
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
            availableResolutions = playerViewModel.availableQuality,
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
            isLoop = playerViewModel.isLoop,
            showDanmaku = playerViewModel.showDanmaku,
            showRelatedVideos = playerViewModel.showRelatedVideos
        ),
        LocalVideoPlayerDanmakuMasksData provides VideoPlayerDanmakuMasksData(
            danmakuMasks = playerViewModel.danmakuMasks,
        ),
        LocalVideoPlayerVideoShotData provides VideoPlayerVideoShotData(
            videoShot = playerViewModel.videoShot,
        ),
    ) {
        Box(
            modifier = Modifier
                .onPreviewKeyEvent { keyEvent ->
                    if (keyEvent.type == KeyEventType.KeyUp && autoActionCountdownJob != null) {
                        // 任何按键都可以取消倒计时
                        logger.debug { "按下按键: ${keyEvent.key}, 取消播放下一集（或自动退出）" }
                        autoActionCountdownJob?.cancel()
                        autoActionCountdownJob = null
                        autoActionTipVisible = false
                        return@onPreviewKeyEvent true
                    }
                    false
                }
        ) {
            BvPlayer(
                modifier = modifier
                    .fillMaxSize(),
                videoPlayer = playerViewModel.videoPlayer!!,
                danmakuPlayer = playerViewModel.danmakuPlayer,
                playerSeekForwardStep = Prefs.playerSeekForwardStep,
                playerSeekBackwardStep = Prefs.playerSeekBackwardStep,
                showBottomProgressBar = Prefs.playerShowBottomProgressBar,
                onToggleRelatedVideos = { state ->
                    playerViewModel.showRelatedVideos = if (playerViewModel.relatedVideos.isNotEmpty()) state else false
                },
                onSendHeartbeat = playerViewModel::uploadHistory,
                onClearBackToHistoryData = { playerViewModel.lastPlayed = 0 },
                onLoadNextVideo = {
                    val currentIndex = playerViewModel.availableVideoList
                        .indexOfFirst {
                            when (it) {
                                is VideoListItemData -> it.cid == playerViewModel.currentCid
                                else -> false
                            }
                        }
                    val nextVideo = if (currentIndex >= 0 && currentIndex + 1 < playerViewModel.availableVideoList.size) {
                        playerViewModel.availableVideoList
                            .drop(currentIndex + 1)
                            .firstOrNull { it is VideoListItemData } as? VideoListItemData
                    } else {
                        null
                    }

                    if (nextVideo != null && Prefs.playerAutoPlayNextVideo) {
                        logger.info { "Play next video: $nextVideo" }
                        // 启动倒计时 toast 提示
                        autoActionCountdownJob = scope.launch {
                            try {
                                // 更新 SkipTip 文本和显示状态
                                autoActionTipText = "播放结束，即将播放下一集"
                                autoActionTipVisible = true
                                delay(1500)

                                // 如果没有被取消，切换到下一个视频
                                autoActionTipVisible = false
                                if (autoActionCountdownJob != null) {
                                    autoActionCountdownJob = null
                                    playerViewModel.title = nextVideo.title
                                    playerViewModel.partTitle = nextVideo.partTitle
                                    if(nextVideo.seasonId == null && playerViewModel.currentAid != nextVideo.aid) {
                                        VideoInfoActivity.actionStart(
                                            context = context,
                                            aid = nextVideo.aid,
                                            cid = nextVideo.cid,
                                            fromPlayer = true
                                        )
                                    } else {
                                        playerViewModel.loadPlayUrl(
                                            avid = nextVideo.aid,
                                            cid = nextVideo.cid,
                                            epid = nextVideo.epid,
                                            seasonId = nextVideo.seasonId,
                                            continuePlayNext = true
                                        )
                                    }
                                }
                            } catch (e: Exception) {
                                // 倒计时被取消
                                autoActionTipVisible = false
                                autoActionCountdownJob = null
                            }
                        }
                    } else if (Prefs.playerExitWhenAllIsPlayed) {
                        // 启动倒计时提示
                        autoActionCountdownJob = scope.launch {
                            try {
                                // 更新 SkipTip 文本和显示状态
                                autoActionTipText = "播放结束，即将退出"
                                autoActionTipVisible = true
                                delay(1500)

                                // 如果没有被取消，退出播放
                                autoActionTipVisible = false
                                if (autoActionCountdownJob != null) {
                                    autoActionCountdownJob = null
                                    Prefs.currentPlaySpeed = Prefs.defaultPlaySpeed
                                    (context as Activity).finish()
                                }
                            } catch (e: Exception) {
                                // 倒计时被取消
                                autoActionTipVisible = false
                                autoActionCountdownJob = null
                            }
                        }
                    }
                },
                onExit = {
                    Prefs.currentPlaySpeed = Prefs.defaultPlaySpeed
                    (context as Activity).finish()
                },
                onLoadNewVideo = { videoListItem ->
                    when (videoListItem) {
                        is VideoListItemData -> {
                            playerViewModel.title = videoListItem.title
                            playerViewModel.partTitle = videoListItem.partTitle
                            if(videoListItem.seasonId == null && playerViewModel.currentAid != videoListItem.aid) {
                                VideoInfoActivity.actionStart(
                                    context = context,
                                    aid = videoListItem.aid,
                                    cid = videoListItem.cid,
                                    fromPlayer = true
                                )
                            } else {
                                playerViewModel.loadPlayUrl(
                                    avid = videoListItem.aid,
                                    cid = videoListItem.cid,
                                    epid = videoListItem.epid,
                                    seasonId = videoListItem.seasonId,
                                    continuePlayNext = true
                                )
                            }
                        }
                    }
                },
                onRefreshVideo= {
                    val time = playerViewModel.videoPlayer?.currentPosition ?:0
                    logger.info { "Reload video and back to time: ${time.formatHourMinSec()}" }
                    scope.launch {
                        playerViewModel.playQuality()
                        delay(300)
                        playerViewModel.videoPlayer?.seekTo(time)
                        playerViewModel.danmakuPlayer?.seekTo(time)
                        playerViewModel.danmakuPlayer?.pause()
                        playerViewModel.videoPlayer?.start()
                    }
                },
                onResolutionChange = { resolutionCode, afterChange ->
                    scope.launch(Dispatchers.Default) {
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
                    Prefs.currentPlaySpeed = speed
                    playerViewModel.currentPlaySpeed = speed
                },
                onAudioChange = { audio, afterChange ->
                    playerViewModel.currentAudio = audio
                    scope.launch(Dispatchers.Default) {
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
                onOpenUpSpace = {
                    UpInfoActivity.actionStart(
                        context,
                        mid = playerViewModel.upId,
                        name = playerViewModel.upName,
                        face = playerViewModel.upFace
                    )
                },
                onShowDanmakuChange = {
                    Prefs.showDanmaku = it
                    playerViewModel.showDanmaku = it
                },
                onLoopPlayModeChange = {
                    Prefs.isLoop = it
                    playerViewModel.isLoop = it
                },
                userActionContent = { focusMap, onFocus, onPauseAutoHide ->
                    if (Prefs.isLogin && !playerViewModel.fromSeason) {
                        // 增加操作：点赞、收藏、投币。通过 focusMap 获取 focusRequester 并在 onFocusChanged 回调时通知 controller
                        val likeFocus = focusMap["like"]
                        val favFocus = focusMap["fav"]
                        val coinFocus = focusMap["coin"]

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 32.dp)
                                .offset(y = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            LikeButton(
                                modifier = Modifier
                                    .height(24.dp)
                                    .onFocusChanged { if (it.isFocused) onFocus("like") }
                                    .then(likeFocus?.let { Modifier.focusRequester(it) } ?: Modifier),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                                colors = ButtonDefaults.colors(
                                    containerColor = Color.Transparent,
                                    focusedContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                    focusedContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                ),
                                // use shared state
                                isLike = sharedActionState.liked,
                                onToggleLike = {
                                    val aid = playerViewModel.currentAid
                                    scope.launch {
                                        val flow = getStateFlow(aid, Prefs.uid)
                                        val current = flow.value
                                        if (current.liked) {
                                            VideoUserActionManager.delLike(aid, Prefs.uid)
                                        } else {
                                            VideoUserActionManager.addLike(aid, Prefs.uid)
                                        }
                                    }
                                }
                            )
                            FavoriteButton(
                                modifier = Modifier
                                    .height(24.dp)
                                    .onFocusChanged { if (it.isFocused) onFocus("fav") }
                                    .then(favFocus?.let { Modifier.focusRequester(it) } ?: Modifier),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                                colors = ButtonDefaults.colors(
                                    containerColor = Color.Transparent,
                                    focusedContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                    focusedContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                ),
                                dialogContainerColor = Color.Black.copy(alpha = 0.5f),
                                isFavorite = sharedActionState.favorited,
                                // read shared state snapshot (UI will recompose when collectAsState in parent is implemented)
                                userFavoriteFolders = sharedActionState.favoriteFolders,
                                favoriteFolderIds = sharedActionState.favoriteFolderIds,
                                onAddToDefaultFavoriteFolder = {
                                    scope.launch {
                                        val success = VideoUserActionManager.addToDefaultFavoriteFolder(playerViewModel.currentAid, Prefs.uid)
                                        if (success) {
                                            // OK
                                        }
                                    }
                                },
                                onUpdateFavoriteFolders = {
                                    scope.launch {
                                        val success = VideoUserActionManager.updateVideoFavoriteFolders(playerViewModel.currentAid, it, Prefs.uid)
                                        if (success) {
                                            // OK
                                        }
                                    }
                                },
                                onDialogVisibilityChanged = onPauseAutoHide
                            )
                            CoinButton(
                                modifier = Modifier
                                    .height(24.dp)
                                    .onFocusChanged { if (it.isFocused) onFocus("coin") }
                                    .then(coinFocus?.let { Modifier.focusRequester(it) } ?: Modifier),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                                colors = ButtonDefaults.colors(
                                    containerColor = Color.Transparent,
                                    focusedContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                    focusedContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                ),
                                isCoin = sharedActionState.coin,
                                onAddCoin = {
                                    scope.launch {
                                        val success = VideoUserActionManager.addCoin(playerViewModel.currentAid, Prefs.uid)
                                        withContext(Dispatchers.Main) {
                                            if (success) {
                                                "投币成功".toast(context)
                                            } else {
                                                "投币失败".toast(context)
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            )

            // 显示跳过提示
            if (autoActionTipVisible) {
                SkipTip(
                    show = true,
                    text = autoActionTipText,
                    align = Alignment.BottomEnd
                )
            }
            // 推荐视频
            AnimatedVisibility(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth(),
                visible = playerViewModel.showRelatedVideos,
                enter = expandVertically(),
                exit = shrinkVertically(),
                label = "RelatedVideosForPlayer"
            ) {
                VideosRow(
                    header = stringResource(R.string.video_info_related_video_title),
                    videos = playerViewModel.relatedVideos,
                    showMore = {},
                    focusRequester = relatedVideosFocusRequester,
                    onOpenSeasonInfo = { videoData ->
                        SeasonInfoActivity.actionStart(
                            context = context,
                            epId = videoData.epId!!,
                            proxyArea = ProxyArea.checkProxyArea(videoData.title)
                        )
                    },
                    onOpenVideoInfo = { videoData ->
                        VideoInfoActivity.actionStart(
                            context = context,
                            aid = videoData.avid,
                            fromPlayer = true
                        )
                    }
                )
            }
        }
    }
}
