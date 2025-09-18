package dev.aaa1115910.bv.tv.screens

import android.app.Activity
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.ViewModule
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.LocalContentColor
import androidx.tv.material3.LocalTextStyle
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.SuggestionChip
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import androidx.tv.material3.Tab
import androidx.tv.material3.TabRow
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.BlurTransformation
import dev.aaa1115910.biliapi.entity.ApiType
import dev.aaa1115910.biliapi.entity.FavoriteFolderMetadata
import dev.aaa1115910.biliapi.entity.video.Dimension
import dev.aaa1115910.biliapi.entity.video.Tag
import dev.aaa1115910.biliapi.entity.video.VideoDetail
import dev.aaa1115910.biliapi.entity.video.VideoPage
import dev.aaa1115910.biliapi.entity.video.season.Episode
import dev.aaa1115910.biliapi.http.BiliPlusHttpApi
import dev.aaa1115910.biliapi.repositories.UserRepository
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.entity.proxy.ProxyArea
import dev.aaa1115910.bv.player.entity.VideoListItem
import dev.aaa1115910.bv.player.entity.VideoListPart
import dev.aaa1115910.bv.player.entity.VideoListUgcEpisode
import dev.aaa1115910.bv.player.entity.VideoListUgcEpisodeTitle
import dev.aaa1115910.bv.repository.VideoInfoRepository
import dev.aaa1115910.bv.tv.activities.video.SeasonInfoActivity
import dev.aaa1115910.bv.tv.activities.video.TagActivity
import dev.aaa1115910.bv.tv.activities.video.UpInfoActivity
import dev.aaa1115910.bv.tv.activities.video.VideoInfoActivity
import dev.aaa1115910.bv.tv.component.LoadingTip
import dev.aaa1115910.bv.tv.component.TvAlertDialog
import dev.aaa1115910.bv.tv.component.UpIcon
import dev.aaa1115910.bv.tv.component.buttons.LikeButton
import dev.aaa1115910.bv.tv.component.buttons.CoinButton
import dev.aaa1115910.bv.tv.component.buttons.FavoriteButton
import dev.aaa1115910.bv.tv.manager.VideoUserActionManager
import dev.aaa1115910.bv.tv.manager.VideoUserActionManager.getStateFlow
import dev.aaa1115910.bv.tv.component.videocard.VideosRow
import dev.aaa1115910.bv.tv.util.launchPlayerActivity
import dev.aaa1115910.bv.tv.manager.FollowStateManager
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.fWarn
import dev.aaa1115910.bv.util.focusedBorder
import dev.aaa1115910.bv.util.formatHourMinSec
import dev.aaa1115910.bv.util.formatPubTimeString
import dev.aaa1115910.bv.util.ifElse
import dev.aaa1115910.bv.util.onBackPressed
import dev.aaa1115910.bv.util.requestFocus
import dev.aaa1115910.bv.util.swapList
import dev.aaa1115910.bv.util.swapListWithMainContext
import dev.aaa1115910.bv.util.toast
import dev.aaa1115910.bv.viewmodel.video.VideoDetailViewModel
import dev.aaa1115910.bv.viewmodel.video.VideoInfoState
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.getKoin
import kotlin.math.ceil

@Composable
fun VideoInfoScreen(
    modifier: Modifier = Modifier,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    videoInfoRepository: VideoInfoRepository = getKoin().get(),
    videoDetailViewModel: VideoDetailViewModel = koinViewModel(),
    userRepository: UserRepository = getKoin().get(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val intent = (context as Activity).intent
    val logger = KotlinLogging.logger { }
    val defaultFocusRequester = remember { FocusRequester() }
    val lazyListState = rememberLazyListState()

    var showFollowButton by remember { mutableStateOf(false) }
    var isFollowing by remember { mutableStateOf(false) }
    
    // 监听关注状态变化
    val followStateMap by FollowStateManager.followStateMap.collectAsState()
    
    // 当关注状态map变化时，更新当前用户的关注状态
    LaunchedEffect(followStateMap, videoDetailViewModel.videoDetail?.author?.mid) {
        videoDetailViewModel.videoDetail?.author?.mid?.let { mid ->
            FollowStateManager.getFollowState(mid)?.let { following ->
                isFollowing = following
            }
        }
    }
    
    // 添加用于管理简介对话框的状态
    var showDescriptionDialog by remember { mutableStateOf(false) }

    var lastPlayedCid by remember { mutableLongStateOf(0) }
    var lastPlayedTime by remember { mutableIntStateOf(0) }

    var tip by remember { mutableStateOf("Loading") }
    var showUGCVideoInfo by remember { mutableStateOf(Prefs.showUGCVideoInfo) }
    var fromSeason by remember { mutableStateOf(false) }
    var fromPlayer by remember { mutableStateOf(false) }
    var paused by remember { mutableStateOf(false) }
    var proxyArea by remember { mutableStateOf(ProxyArea.MainLand) }

    val containsVerticalScreenVideo by remember {
        derivedStateOf {
            videoDetailViewModel.videoDetail?.pages?.any { it.dimension.isVertical } ?: false
                    || videoDetailViewModel.videoDetail?.ugcSeason?.sections?.any { section -> section.episodes.any { it.dimension!!.isVertical } } ?: false
        }
    }

    var favorited by remember { mutableStateOf(false) }
    // local copies for like / coin sync
    var liked by remember { mutableStateOf(false) }
    var isCoin by remember { mutableStateOf(false) }
    val favoriteFolderMetadataList = remember { mutableStateListOf<FavoriteFolderMetadata>() }
    val videoInFavoriteFolderIds = remember { mutableStateListOf<Long>() }

    // subscribe shared action state by aid
    val aid = videoDetailViewModel.videoDetail?.aid ?: 0L
    val sharedActionStateFlow = remember(aid) { getStateFlow(aid, Prefs.uid) }
    val sharedState by sharedActionStateFlow.collectAsState()

    // keep local copies in sync with shared state
    LaunchedEffect(sharedState) {
        // sync favorite/like/coin from shared manager
        favorited = sharedState.favorited
        liked = sharedState.liked
        isCoin = sharedState.coin
        videoInFavoriteFolderIds.swapList(sharedState.favoriteFolderIds)
        favoriteFolderMetadataList.swapList(sharedState.favoriteFolders)
    }

    val setHistory = {
        logger.info { "play history: ${videoDetailViewModel.videoDetail?.history}" }
        lastPlayedCid = videoDetailViewModel.videoDetail?.history?.lastPlayedCid ?: 0
        lastPlayedTime = videoDetailViewModel.videoDetail?.history?.progress ?: 0
    }

    val updateHistory = {
        scope.launch(Dispatchers.IO) {
            runCatching {
                videoDetailViewModel.loadDetailOnlyUpdateHistory(videoDetailViewModel.videoDetail!!.aid)
            }
            withContext(Dispatchers.Main) {
                setHistory()
            }
        }
    }


    val updateFollowingState: () -> Unit = {
        scope.launch(Dispatchers.IO) {
            val userMid = videoDetailViewModel.videoDetail?.author?.mid ?: -1
            
            // 先检查缓存中是否有关注状态
            val cachedState = FollowStateManager.getFollowState(userMid)
            if (cachedState != null) {
                withContext(Dispatchers.Main) {
                    showFollowButton = true
                    isFollowing = cachedState
                }
                return@launch
            }
            
            // 缓存中没有，调用API获取
            logger.fInfo { "Checking is following user $userMid" }
            val success = userRepository.checkIsFollowing(
                mid = userMid,
                preferApiType = Prefs.apiType
            )
            logger.fInfo { "Following user result: $success" }
            withContext(Dispatchers.Main) {
                showFollowButton = success != null
                if (success != null) {
                    isFollowing = success
                    // 更新到缓存中
                    FollowStateManager.updateFollowState(userMid, success)
                }
            }
        }
    }

    val addFollow: (afterModify: (success: Boolean) -> Unit) -> Unit = { afterModify ->
        scope.launch(Dispatchers.IO) {
            val userMid = videoDetailViewModel.videoDetail?.author?.mid ?: -1
            logger.fInfo { "Add follow to user $userMid" }
            val success = userRepository.followUser(
                mid = userMid,
                preferApiType = Prefs.apiType
            )
            logger.fInfo { "Add follow result: $success" }
            // 更新缓存状态
            if (success) {
                FollowStateManager.updateFollowState(userMid, true)
            }
            afterModify(success)
        }
    }

    val delFollow: (afterModify: (success: Boolean) -> Unit) -> Unit = { afterModify ->
        scope.launch(Dispatchers.IO) {
            val userMid = videoDetailViewModel.videoDetail?.author?.mid ?: -1
            logger.fInfo { "Del follow to user $userMid" }
            val success = userRepository.unfollowUser(
                mid = userMid,
                preferApiType = Prefs.apiType
            )
            logger.fInfo { "Del follow result: $success" }
            // 更新缓存状态
            if (success) {
                FollowStateManager.updateFollowState(userMid, false)
            }
            afterModify(success)
        }
    }

    val fetchFavoriteData: (Long) -> Unit = { avid ->
        scope.launch {
            VideoUserActionManager.fetchFavoriteData(avid, Prefs.uid)
        }
    }

    val updateVideoFavoriteData: (List<Long>) -> Unit = { folderIds ->
        scope.launch {
            val success = VideoUserActionManager.updateVideoFavoriteFolders(aid, folderIds, Prefs.uid)
            if (!success) {
                "收藏操作失败".toast(context)
            }
        }
    }

    val addVideoToDefaultFavoriteFolder: () -> Unit = {
        scope.launch {
            val success = VideoUserActionManager.addToDefaultFavoriteFolder(aid, Prefs.uid)
            if (!success) {
                "添加收藏失败".toast(context)
            }
        }
    }

    val updateVideoUserActionData = {
        // update shared state from loaded data
        val configAid = videoDetailViewModel.videoDetail?.aid ?: 0L
        VideoUserActionManager.updateFromLoadedData(
            configAid,
            liked = videoDetailViewModel.videoDetail?.userActions?.like ?: false,
            favorited = videoDetailViewModel.videoDetail?.userActions?.favorite ?: false,
            coin = videoDetailViewModel.videoDetail?.userActions?.coin ?: false
        )
    }

    val updateUgcSeasonSectionVideoList: (Int) -> Unit = { sectionIndex ->
        val partVideoList = mutableListOf<VideoListItem>()
        val sectionTitle = videoDetailViewModel.videoDetail!!.ugcSeason!!.sections[sectionIndex]?.title ?: ""
        videoDetailViewModel.videoDetail!!.ugcSeason!!.sections[sectionIndex].episodes.mapIndexed { epIndex, episode ->
            if (episode.pages.size == 1) {
                episode.pages.mapIndexed { pageInd, videoPage ->
                    partVideoList.add(
                        VideoListUgcEpisode(
                            aid = episode.aid,
                            cid = videoPage.cid,
                            title = if (sectionTitle == "正片") episode.title else sectionTitle,
                            partTitle = if (sectionTitle == "正片") "" else episode.title,
                            index = epIndex
                        )
                    )
                }
            } else {
                partVideoList.add(
                    VideoListUgcEpisodeTitle(
                        title = episode.title,
                        index = epIndex,
                    )
                )
                episode.pages.mapIndexed { pageIndex, videoPage ->
                    partVideoList.add(
                        VideoListPart(
                            aid = episode.aid,
                            cid = videoPage.cid,
                            title = episode.title,
                            partTitle = videoPage.title,
                            index = pageIndex,
                        )
                    )
                }
            }
        }
        videoInfoRepository.videoList.clear()
        videoInfoRepository.videoList.addAll(partVideoList)
    }


    suspend fun addVideoLike(): Boolean {
        val configAid = videoDetailViewModel.videoDetail?.aid ?: 0L
        return VideoUserActionManager.addLike(configAid, Prefs.uid)
    }

    suspend fun delVideoLike(): Boolean {
        val configAid = videoDetailViewModel.videoDetail?.aid ?: 0L
        return VideoUserActionManager.delLike(configAid, Prefs.uid)
    }


  suspend fun addVideoCoin(): Boolean {
      val configAid = videoDetailViewModel.videoDetail?.aid ?: 0L
      return VideoUserActionManager.addCoin(configAid, Prefs.uid)
  }

    LaunchedEffect(Unit) {
        if (intent.hasExtra("aid")) {
            val aid = intent.getLongExtra("aid", 170001)
            var cid = intent.getLongExtra("cid", 0)
            fromSeason = intent.getBooleanExtra("fromSeason", false)
            fromPlayer = intent.getBooleanExtra("fromPlayer", false)
            proxyArea = ProxyArea.entries[intent.getIntExtra("proxy_area", 0)]
            //获取视频信息
            scope.launch(Dispatchers.IO) {
                if (proxyArea != ProxyArea.MainLand) {
                    runCatching {
                        val seasonId = BiliPlusHttpApi.getSeasonIdByAvid(aid)
                        logger.info { "Get season id from biliplus: $seasonId" }
                        seasonId?.let {
                            logger.fInfo { "Redirect to season $seasonId" }
                            SeasonInfoActivity.actionStart(
                                context = context,
                                seasonId = seasonId,
                                proxyArea = proxyArea
                            )
                            context.finish()
                        }
                    }.onFailure {
                        logger.fWarn { "Redirect failed: ${it.stackTraceToString()}" }
                    }
                }

                runCatching {
                    videoDetailViewModel.loadDetail(aid, fromSeason)
                    withContext(Dispatchers.Main) {
                        updateVideoUserActionData()
                        setHistory()
                    }
                    if (Prefs.isLogin) fetchFavoriteData(aid)

                    videoInfoRepository.relatedVideos.clear()
                    if (!fromSeason) {
                        videoInfoRepository.relatedVideos.addAll(videoDetailViewModel.relatedVideos.subList(0, videoDetailViewModel.relatedVideos.size.takeIf { it<12 } ?: 12))
                    }
                    // 从播放器推荐视频打开时 fromPlayer=true 并显示loading。300m后 fromPlayer改成false，此后从播放器返回详情页，正常显示详情内容
                    //如果是从剧集跳转过来的或设置不显示视频详情，就直接播放 P1
                    if (fromSeason || !showUGCVideoInfo || fromPlayer) {
                        val playPart = videoDetailViewModel.videoDetail!!.pages.first()
                        cid = cid.takeIf { it > 0L } ?: playPart.cid

                        if (videoDetailViewModel.videoDetail!!.ugcSeason !== null) {
                            val sectionIndex =
                                videoDetailViewModel.videoDetail!!.ugcSeason!!.sections
                                    .indexOfFirst { section -> section.episodes.any { it.cid == cid || it.pages.any{ it.cid == cid } } }
                            updateUgcSeasonSectionVideoList(sectionIndex)
                        }

                        launchPlayerActivity(
                            context = context,
                            avid = videoDetailViewModel.videoDetail!!.aid,
                            cid = cid,
                            title = videoDetailViewModel.videoDetail!!.title,
                            partTitle = videoDetailViewModel.videoDetail!!.pages.find { it.cid == cid }!!.title,
                            played = if (cid == lastPlayedCid) lastPlayedTime * 1000 else 0,
                            fromSeason = fromSeason,
                            isVerticalVideo = videoDetailViewModel.videoDetail!!.pages.find { it.cid == cid }!!.dimension.isVertical,
                            playerIconIdle = videoDetailViewModel.videoDetail!!.playerIcon?.idle
                                ?: "",
                            playerIconMoving = videoDetailViewModel.videoDetail!!.playerIcon?.moving
                                ?: "",
                            play = videoDetailViewModel.videoDetail!!.stat.view,
                            danmaku = videoDetailViewModel.videoDetail!!.stat.danmaku,
                            upName = videoDetailViewModel.videoDetail!!.author.name,
                            upId = videoDetailViewModel.videoDetail!!.author.mid,
                            upFace = videoDetailViewModel.videoDetail!!.author.face,
                            pubTime = videoDetailViewModel.videoDetail!!.publishDate.formatPubTimeString()
                        )
                        if(fromPlayer) {
                            // 清除标记, 以便从播放器返回过来的可以进入详情页
                            scope.launch {
                                delay(1200)
                                fromPlayer = false
                                intent.removeExtra("fromPlayer")
                                if (!showUGCVideoInfo) {
                                    context.finish()
                                }
                            }
                        }
                        if (!fromPlayer) {
                            context.finish()
                        }
                    }
                }.onFailure {
                    val errorMessage = it.localizedMessage
                    val isVideoNotFound = when (Prefs.apiType) {
                        ApiType.Web -> errorMessage == "啥都木有"
                        ApiType.App -> errorMessage == "访问权限不足"
                    }

                    logger.fInfo { "Get video info failed: ${it.stackTraceToString()}" }
                    if (!isVideoNotFound || !Prefs.enableProxy) {
                        withContext(Dispatchers.Main) {
                            tip = it.localizedMessage ?: "未知错误"
                        }
                        return@onFailure
                    }
                    withContext(Dispatchers.Main) {
                        videoDetailViewModel.state = VideoInfoState.Loading
                    }

                    logger.fInfo { "Trying get video info through proxy server" }
                    runCatching {
                        val seasonId = BiliPlusHttpApi.getSeasonIdByAvid(aid)
                        logger.info { "Get season id from biliplus: $seasonId" }
                        seasonId?.let {
                            logger.fInfo { "Redirect to season $seasonId" }
                            SeasonInfoActivity.actionStart(
                                context = context,
                                seasonId = seasonId,
                                proxyArea = ProxyArea.HongKong
                            )
                            context.finish()
                        } ?: let {
                            withContext(Dispatchers.Main) {
                                tip = "视频不存在"
                                videoDetailViewModel.state = VideoInfoState.Error
                            }
                        }
                    }.onFailure { e ->
                        logger.fWarn { "Redirect failed: ${e.stackTraceToString()}" }
                        withContext(Dispatchers.Main) {
                            tip = e.localizedMessage ?: "未知错误"
                            videoDetailViewModel.state = VideoInfoState.Error
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(videoDetailViewModel.videoDetail) {
        //如果是从剧集页跳转回来的，那就不需要再跳转到剧集页了
        if (fromSeason || !showUGCVideoInfo) return@LaunchedEffect

        videoDetailViewModel.videoDetail?.let {
            if (it.redirectToEp) {
                runCatching {
                    logger.fInfo { "Redirect to ep ${it.epid}" }
                    SeasonInfoActivity.actionStart(
                        context = context,
                        epId = it.epid,
                        proxyArea = proxyArea
                    )
                    context.finish()
                }.onFailure {
                    logger.fWarn { "Redirect failed: ${it.stackTraceToString()}" }
                }
            } else {
                logger.fInfo { "No redirection required" }
                defaultFocusRequester.requestFocus(scope)
            }

            if (!fromSeason && showUGCVideoInfo) updateFollowingState()
        }
    }

    // 确保页面显示时封面获得焦点
    LaunchedEffect(videoDetailViewModel.videoDetail, fromSeason, showUGCVideoInfo, fromPlayer) {
        if (videoDetailViewModel.videoDetail != null && 
            !videoDetailViewModel.videoDetail!!.redirectToEp && 
            !fromSeason && 
            showUGCVideoInfo && 
            !fromPlayer) {
            // 延迟一小段时间确保UI完全渲染
            delay(300)
            defaultFocusRequester.requestFocus(scope)
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                paused = true
            } else if (event == Lifecycle.Event.ON_RESUME) {
                // 如果 pause==true 那可能是从播放页返回回来的，此时更新历史记录
                if (paused) updateHistory()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    if (videoDetailViewModel.videoDetail == null || videoDetailViewModel.videoDetail?.redirectToEp == true || fromSeason || !showUGCVideoInfo || fromPlayer) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            if (tip == "Loading") {
                LoadingTip()
            }else{
                Text(
                    text = tip,
                    fontSize = 20.sp
                )
            }
        }
    } else {
        Scaffold(
            modifier = modifier
        ) { innerPadding ->
            Box(
                Modifier.padding(innerPadding)
            ) {
                // 图片加载成功后，动画 alpha，从 0 -> 0.6f
                val bgLoaded = remember { mutableStateOf(false) }
                val animatedAlpha by animateFloatAsState(
                    targetValue = if (bgLoaded.value) 0.6f else 0f,
                    animationSpec = tween(durationMillis = 500)
                )
                AsyncImage(
                    modifier = Modifier.fillMaxSize(),
                    model = ImageRequest.Builder(LocalContext.current)
                    .data(videoDetailViewModel.videoDetail?.cover)
                    .transformations(BlurTransformation(LocalContext.current, 20f, 5f))
                    .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    alpha = animatedAlpha,
                    onSuccess = { bgLoaded.value = true },
                    onError = { bgLoaded.value = false }
                )

                LazyColumn(
                    modifier = Modifier
                        .onKeyEvent { event ->
                             if (event.type == KeyEventType.KeyDown) {
                                 when (event.key) {
                                     Key.DirectionUp -> {
                                         scope.launch {
                                             lazyListState.animateScrollBy(-200f)
                                         }
                                     }
                                     Key.DirectionDown -> {
                                         scope.launch {
                                             lazyListState.animateScrollBy(200f)
                                         }
                                     }
                                 }
                             }
                             return@onKeyEvent false
                        },
                    state = lazyListState,
                    contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (containsVerticalScreenVideo) {
                                ArgueTip(text = stringResource(R.string.video_info_argue_tip_vertical_screen))
                            }
                            if (videoDetailViewModel.videoDetail?.argueTip != null) {
                                ArgueTip(text = videoDetailViewModel.videoDetail!!.argueTip!!)
                            }
                        }
                    }
                    item {
                        VideoInfoData(
                            defaultFocusRequester = defaultFocusRequester,
                            videoDetail = videoDetailViewModel.videoDetail!!,
                            showFollowButton = showFollowButton,
                            isFollowing = isFollowing,
                            tags = videoDetailViewModel.videoDetail!!.tags,
                            isFavorite = favorited,
                            userFavoriteFolders = favoriteFolderMetadataList,
                            favoriteFolderIds = videoInFavoriteFolderIds,
                            onClickCover = {
                                logger.fInfo { "Click video cover" }
                                var title = ""
                                var partTitle = ""
                                //set video list
                                if (videoDetailViewModel.videoDetail?.ugcSeason != null) {
                                    // 合集
                                    if (videoDetailViewModel.videoDetail!!.ugcSeason!!.sections.size == 1) {
                                        // 只有一个分组
                                        updateUgcSeasonSectionVideoList(0)
                                    } else {
                                        // 多个组，找默认播放哪个组的
                                        val cid =
                                            videoDetailViewModel.videoDetail!!.pages.first().cid
                                        val sectionIndex =
                                            videoDetailViewModel.videoDetail!!.ugcSeason!!.sections
                                                .indexOfFirst { section -> section.episodes.any { it.cid == cid || it.pages.any{ it.cid == cid } } }
                                        val section = videoDetailViewModel.videoDetail!!.ugcSeason!!.sections.getOrNull(sectionIndex)
                                        title = if (section?.title == "正片") section.episodes.find { it.cid == cid }!!.title else section?.title ?: ""
                                        partTitle = if (section?.title == "正片") "" else section?.episodes?.find { it.cid == cid }!!.title
                                        updateUgcSeasonSectionVideoList(sectionIndex)
                                    }
                                } else {
                                    // 分 p
                                    val partVideoList =
                                        videoDetailViewModel.videoDetail!!.pages.mapIndexed { index, videoPage ->
                                            VideoListPart(
                                                aid = videoDetailViewModel.videoDetail!!.aid,
                                                cid = videoPage.cid,
                                                title = videoDetailViewModel.videoDetail!!.title,
                                                partTitle = if (videoDetailViewModel.videoDetail!!.pages.size == 1) "" else videoPage.title,
                                                index = index,
                                            )
                                        }
                                    videoInfoRepository.videoList.clear()
                                    videoInfoRepository.videoList.addAll(partVideoList)
                                }

                                val lastPlayedPage =
                                    videoDetailViewModel.videoDetail!!.pages.find { it.cid == lastPlayedCid }
                                val playPage = lastPlayedPage
                                    ?: videoDetailViewModel.videoDetail!!.pages.first()

                                launchPlayerActivity(
                                    context = context,
                                    avid = videoDetailViewModel.videoDetail!!.aid,
                                    cid = playPage.cid,
                                    title = if (title.isNotEmpty()) title else videoDetailViewModel.videoDetail!!.title,
                                    partTitle = if (partTitle.isNotEmpty()) partTitle else if (videoDetailViewModel.videoDetail!!.pages.size == 1) "" else playPage.title,
                                    played = if (playPage.cid == lastPlayedCid) lastPlayedTime * 1000 else 0,
                                    fromSeason = false,
                                    isVerticalVideo = videoDetailViewModel.videoDetail!!.pages.first().dimension.isVertical,
                                    playerIconIdle = videoDetailViewModel.videoDetail!!.playerIcon?.idle
                                        ?: "",
                                    playerIconMoving = videoDetailViewModel.videoDetail!!.playerIcon?.moving
                                        ?: "",
                                    play = videoDetailViewModel.videoDetail!!.stat.view,
                                    danmaku = videoDetailViewModel.videoDetail!!.stat.danmaku,
                                    upName = videoDetailViewModel.videoDetail!!.author.name,
                                    upId = videoDetailViewModel.videoDetail!!.author.mid,
                                    upFace = videoDetailViewModel.videoDetail!!.author.face,
                                    pubTime = videoDetailViewModel.videoDetail!!.publishDate.formatPubTimeString()
                                )
                            },
                            onClickUp = {
                                UpInfoActivity.actionStart(
                                    context,
                                    mid = videoDetailViewModel.videoDetail!!.author.mid,
                                    name = videoDetailViewModel.videoDetail!!.author.name,
                                    face = videoDetailViewModel.videoDetail!!.author.face
                                )
                            },
                            onAddFollow = {
                                addFollow { success ->
                                    scope.launch(Dispatchers.Main) {
                                        if (success) {
                                            "关注成功".toast(context)
                                        } else {
                                            "关注失败".toast(context)
                                        }
                                    }
                                }
                            },
                            onDelFollow = {
                                delFollow { success ->
                                    scope.launch(Dispatchers.Main) {
                                        if (success) {
                                            "已取消关注".toast(context)
                                        } else {
                                            "取消关注失败".toast(context)
                                        }
                                    }
                                }
                            },
                            onClickTip = { tag ->
                                TagActivity.actionStart(
                                    context = context,
                                    tagId = tag.id,
                                    tagName = tag.name
                                )
                            },
                            onAddToDefaultFavoriteFolder = {
                                addVideoToDefaultFavoriteFolder()
                                favorited = true
                                "已添加到默认收藏夹".toast(context)
                            },
                            onUpdateFavoriteFolders = {
                                updateVideoFavoriteData(it)
                                favorited = it.isNotEmpty()
                                videoInFavoriteFolderIds.swapList(it)
                                if (it.isNotEmpty()) {
                                    "收藏成功".toast(context)
                                } else {
                                    "已取消收藏".toast(context)
                                }
                            },
                            isLike = liked,
                            onAddLike = {
                                scope.launch {
                                    if (!liked) {
                                        if (addVideoLike()) {
                                            liked = true
                                            "点赞成功".toast(context)
                                        } else {
                                            "点赞失败".toast(context)
                                        }
                                    }
                                }
                            },
                            onDelLike = {
                                scope.launch {
                                    if (liked) {
                                        if (delVideoLike()) {
                                            liked = false
                                            "已取消点赞".toast(context)
                                        } else {
                                            "取消点赞失败".toast(context)
                                        }
                                    }
                                }
                            },
                            isCoin = isCoin,
                            onAddCoin = {
                                scope.launch {
                                    if (!isCoin) {
                                        if (addVideoCoin()) {
                                            isCoin = true
                                            "投币成功".toast(context)
                                        } else {
                                            "投币失败".toast(context)
                                        }
                                    }
                                }
                            },
                            onShowDescription = {
                                showDescriptionDialog = true
                            }
                        )
                    }
                    if (videoDetailViewModel.videoDetail?.ugcSeason == null) {
                        item {
                            VideoPartRow(
                                pages = videoDetailViewModel.videoDetail?.pages ?: emptyList(),
                                lastPlayedCid = lastPlayedCid,
                                lastPlayedTime = lastPlayedTime,
                                enablePartListDialog =
                                    (videoDetailViewModel.videoDetail?.pages?.size ?: 0) > 5,
                                onClick = { cid ->
                                    logger.fInfo { "Click video part: [av:${videoDetailViewModel.videoDetail?.aid}, bv:${videoDetailViewModel.videoDetail?.bvid}, cid:$cid]" }
                                    launchPlayerActivity(
                                        context = context,
                                        avid = videoDetailViewModel.videoDetail!!.aid,
                                        cid = cid,
                                        title = videoDetailViewModel.videoDetail!!.title,
                                        partTitle = videoDetailViewModel.videoDetail!!.pages.find { it.cid == cid }!!.title,
                                        played = if (cid == lastPlayedCid) lastPlayedTime * 1000 else 0,
                                        fromSeason = false,
                                        isVerticalVideo = videoDetailViewModel.videoDetail!!.pages.find { it.cid == cid }!!.dimension.isVertical,
                                        playerIconIdle = videoDetailViewModel.videoDetail!!.playerIcon?.idle
                                            ?: "",
                                        playerIconMoving = videoDetailViewModel.videoDetail!!.playerIcon?.moving
                                            ?: "",
                                        play = videoDetailViewModel.videoDetail!!.stat.view,
                                        danmaku = videoDetailViewModel.videoDetail!!.stat.danmaku,
                                        upName = videoDetailViewModel.videoDetail!!.author.name,
                                        upId = videoDetailViewModel.videoDetail!!.author.mid,
                                        upFace = videoDetailViewModel.videoDetail!!.author.face,
                                        pubTime = videoDetailViewModel.videoDetail!!.publishDate.formatPubTimeString()
                                    )
                                }
                            )
                        }
                    } else {
                        itemsIndexed(items = videoDetailViewModel.videoDetail?.ugcSeason!!.sections) { index, section ->
                            VideoUgcSeasonRow(
                                title = section.title,
                                episodes = section.episodes,
                                lastPlayedCid = lastPlayedCid,
                                lastPlayedTime = lastPlayedTime,
                                enableUgcListDialog = section.episodes.size > 5,
                                onClickEp = { aid, cid ->
                                    logger.fInfo { "Click ugc season episode: [av:${videoDetailViewModel.videoDetail?.aid}, bv:${videoDetailViewModel.videoDetail?.bvid}, cid:$cid]" }
                                    updateUgcSeasonSectionVideoList(index)
                                    val sectionTitle = videoDetailViewModel.videoDetail?.ugcSeason?.sections?.getOrNull(index)?.title
                                    val episode = section.episodes.find { it.cid == cid }
                                    launchPlayerActivity(
                                        context = context,
                                        avid = aid,
                                        cid = cid,
                                        title = if (sectionTitle == "正片") episode!!.title else sectionTitle ?: videoDetailViewModel.videoDetail?.ugcSeason?.title ?: "",
                                        partTitle = if (sectionTitle == "正片") if (episode!!.pages.size>1) episode.pages.first().title else "" else episode!!.title,
                                        played = if (cid == lastPlayedCid) lastPlayedTime * 1000 else 0,
                                        fromSeason = false,
                                        isVerticalVideo = videoDetailViewModel.videoDetail!!.pages.first().dimension.isVertical,
                                        playerIconIdle = videoDetailViewModel.videoDetail!!.playerIcon?.idle
                                            ?: "",
                                        playerIconMoving = videoDetailViewModel.videoDetail!!.playerIcon?.moving
                                            ?: "",
                                        play = videoDetailViewModel.videoDetail!!.stat.view,
                                        danmaku = videoDetailViewModel.videoDetail!!.stat.danmaku,
                                        upName = videoDetailViewModel.videoDetail!!.author.name,
                                        upId = videoDetailViewModel.videoDetail!!.author.mid,
                                        upFace = videoDetailViewModel.videoDetail!!.author.face,
                                        pubTime = videoDetailViewModel.videoDetail!!.publishDate.formatPubTimeString()
                                    )
                                },
                                onClickEpPart = { episode, cid ->
                                    logger.fInfo { "Click ugc season episode part: [av:${videoDetailViewModel.videoDetail?.aid}, bv:${videoDetailViewModel.videoDetail?.bvid}, cid:$cid]" }
                                    val sectionTitle = videoDetailViewModel.videoDetail?.ugcSeason?.sections?.getOrNull(index)?.title
                                    launchPlayerActivity(
                                        context = context,
                                        avid = episode.aid,
                                        cid = cid,
                                        title = if (!sectionTitle.isNullOrEmpty()) episode.title else videoDetailViewModel.videoDetail!!.title,
                                        partTitle = episode.pages.find { it.cid == cid }!!.title,
                                        played = if (cid == lastPlayedCid) lastPlayedTime * 1000 else 0,
                                        fromSeason = false,
                                        isVerticalVideo = videoDetailViewModel.videoDetail!!.pages.find { it.cid == cid }!!.dimension.isVertical,
                                        playerIconIdle = videoDetailViewModel.videoDetail!!.playerIcon?.idle
                                            ?: "",
                                        playerIconMoving = videoDetailViewModel.videoDetail!!.playerIcon?.moving
                                            ?: "",
                                        play = videoDetailViewModel.videoDetail!!.stat.view,
                                        danmaku = videoDetailViewModel.videoDetail!!.stat.danmaku,
                                        upName = videoDetailViewModel.videoDetail!!.author.name,
                                        upId = videoDetailViewModel.videoDetail!!.author.mid,
                                        upFace = videoDetailViewModel.videoDetail!!.author.face,
                                        pubTime = videoDetailViewModel.videoDetail!!.publishDate.formatPubTimeString()
                                    )
                                }
                            )
                        }
                    }
                    if (videoDetailViewModel.relatedVideos.isNotEmpty()) {
                        item { 
                            VideosRow(
                                header = stringResource(R.string.video_info_related_video_title),
                                videos = videoDetailViewModel.relatedVideos,
                                showMore = {},
                                onOpenSeasonInfo = { videoData ->
                                    SeasonInfoActivity.actionStart(
                                        context = context,
                                        epId = videoData.epId!!,
                                        proxyArea = ProxyArea.checkProxyArea(videoData.title)
                                    )
                                },
                                onOpenVideoInfo = { videoData ->
                                    VideoInfoActivity.actionStart(context, videoData.avid)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    VideoDescriptionDialog(
        show = showDescriptionDialog,
        onHideDialog = { showDescriptionDialog = false },
        description = videoDetailViewModel.videoDetail?.description ?: ""
    )
}

@Composable
fun ArgueTip(
    modifier: Modifier = Modifier,
    text: String
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 36.dp),
        colors = SurfaceDefaults.colors(
            containerColor = Color.Yellow.copy(alpha = 0.2f),
            contentColor = Color.Yellow
        ),
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.Warning,
                contentDescription = null,
                tint = Color.Yellow
            )
            Text(text = text)
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun VideoInfoData(
    modifier: Modifier = Modifier,
    defaultFocusRequester: FocusRequester,
    videoDetail: VideoDetail,
    showFollowButton: Boolean,
    isFollowing: Boolean,
    tags: List<Tag>,
    isFavorite: Boolean,
    userFavoriteFolders: List<FavoriteFolderMetadata> = emptyList(),
    favoriteFolderIds: List<Long> = emptyList(),
    onClickCover: () -> Unit,
    onClickUp: () -> Unit,
    onAddFollow: () -> Unit,
    onDelFollow: () -> Unit,
    onClickTip: (Tag) -> Unit,
    onAddToDefaultFavoriteFolder: () -> Unit,
    onUpdateFavoriteFolders: (List<Long>) -> Unit,
    isLike: Boolean,
    onAddLike: () -> Unit = {},
    onDelLike: () -> Unit = {},
    isCoin: Boolean = false,
    onAddCoin: () -> Unit = {},
    onShowDescription: () -> Unit = {}
) {
//    val localDensity = LocalDensity.current
//    var heightIs by remember { mutableStateOf(0.dp) }
    val isLogin by remember { mutableStateOf(Prefs.isLogin) }
    var coverHasFocus by remember { mutableStateOf(false) }
    val videoDuration = videoDetail.pages.sumOf { it.duration }.takeIf { videoDetail.pages.isNotEmpty() } ?: 0

    Row(
        modifier = modifier
            .padding(start = 36.dp, end = 36.dp, top = 12.dp, bottom = 18.dp),
    ) {
        Surface(
            modifier = Modifier
                .focusRequester(defaultFocusRequester)
                .width(260.dp)
                .aspectRatio(1.6f)
//                .onGloballyPositioned { coordinates ->
//                    heightIs = with(localDensity) { coordinates.size.height.toDp() }
//                }
                .onFocusChanged { coverHasFocus = it.hasFocus }
                .padding(4.dp),
            onClick = onClickCover,
            shape = ClickableSurfaceDefaults.shape(
                shape = MaterialTheme.shapes.medium,
            ),
            scale = ClickableSurfaceDefaults.scale(scale = 1f, focusedScale = 1.05f),
            border = ClickableSurfaceDefaults.border(
                focusedBorder = Border(
                    border = BorderStroke(
                        width = 3.dp,
                        color = MaterialTheme.colorScheme.border
                    ),
                    shape = MaterialTheme.shapes.medium
                )
            )
        ) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize(),
                // model = if (videoDetail.ugcSeason != null) videoDetail.ugcSeason!!.cover else videoDetail.cover,
                model = videoDetail.cover,
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 0.dp,
                            topEnd = 0.dp,
                            bottomStart = 12.dp,
                            bottomEnd = 12.dp
                        )
                    )
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.8f)
                            )
                        )
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start=16.dp, end=16.dp, bottom = 12.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ){
                    Icon(
                        modifier = Modifier,
                        painter = painterResource(id = R.drawable.ic_play_count),
                        contentDescription = null,
                        tint = Color.White
                    )
                    Text(
                        text = with(videoDetail.stat.view){if (this >= 10000) "${this / 10000}万" else "$this"},
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        modifier = Modifier,
                        painter = painterResource(id = R.drawable.ic_danmaku_count),
                        contentDescription = null,
                        tint = Color.White
                    )
                    Text(
                        text = with(videoDetail.stat.danmaku){if (this >= 10000) "${this / 10000}万" else "$this"},
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = (videoDuration * 1000L).formatHourMinSec(),
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
        Spacer(modifier = Modifier.width(24.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth(),
//                .height(heightIs),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 基本信息
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = videoDetail.title,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    CompositionLocalProvider(
                        LocalTextStyle provides MaterialTheme.typography.labelMedium
                    ) {
                        Text(text = "${videoDetail.stat.like} 点赞")
                        Text(text = "·")
                        Text(text = "${videoDetail.stat.coin} 投币")
                        Text(text = "·")
                        Text(text = "${videoDetail.stat.favorite} 收藏")
                        Text(text = "·")
                        Text(text = videoDetail.publishDate.formatPubTimeString())
                    }
                }
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(x=(-3).dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    if (isLogin) {
                        item {
                            LikeButton(
                                modifier = Modifier
                                    .height(32.dp), // 设置高度
                                isLike = isLike,
                                onToggleLike = {
                                    if (isLike) {
                                        onDelLike()
                                    } else {
                                        onAddLike()
                                    }
                                }
                            )
                        }
                        item {
                            FavoriteButton(
                                modifier = Modifier
                                    .height(32.dp), // 设置高度
                                isFavorite = isFavorite,
                                userFavoriteFolders = userFavoriteFolders,
                                favoriteFolderIds = favoriteFolderIds,
                                onAddToDefaultFavoriteFolder = onAddToDefaultFavoriteFolder,
                                onUpdateFavoriteFolders = onUpdateFavoriteFolders
                            )
                        }
                        item {
                            CoinButton(
                                modifier = Modifier
                                    .height(32.dp), // 设置高度
                                isCoin = isCoin,
                                onAddCoin = {
                                     onAddCoin()
                                }
                            )
                        }
                    }
                    item {
                        UpButton(
                            name = videoDetail.author.name,
                            followed = isFollowing,
                            showFollowButton = showFollowButton,
                            onClickUp = onClickUp,
                            onAddFollow = onAddFollow,
                            onDelFollow = onDelFollow
                        )
                    }

                    // 简介按钮
                    if (videoDetail.description.isNotBlank()) {
                        item {
                            Row(
                                modifier = Modifier
                                    .clip(MaterialTheme.shapes.small)
                                    .background(Color.White.copy(alpha = 0.2f))
                                    .focusedBorder(MaterialTheme.shapes.small)
                                    .padding(horizontal = 4.dp)
                                    .clickable { onShowDescription() }
                                    .height(30.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "简介>>",
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
            // 标签列表
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(x=(-2).dp, y = (-2).dp),
                contentPadding = PaddingValues(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(items = tags) { tag ->
                    SuggestionChip(onClick = {
                        onClickTip(tag)
                    }) {
                        Text(text = tag.name)
                    }
                }
            }
        }
    }
}

@Composable
private fun UpButton(
    modifier: Modifier = Modifier,
    name: String,
    followed: Boolean,
    showFollowButton: Boolean = false,
    onClickUp: () -> Unit,
    onAddFollow: () -> Unit,
    onDelFollow: () -> Unit
) {
    val view = LocalView.current
    val isLogin by remember { mutableStateOf(if (!view.isInEditMode) Prefs.isLogin else true) }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .background(Color.White.copy(alpha = 0.2f))
                .focusedBorder(MaterialTheme.shapes.small)
                .padding(4.dp)
                .clickable { onClickUp() },
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            UpIcon(color = Color.White)
            Text(text = name, color = Color.White)
        }
        if(isLogin && showFollowButton) {
            Row(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .background(Color.White.copy(alpha = 0.2f))
                    .focusedBorder(MaterialTheme.shapes.small)
                    .padding(horizontal = 4.dp, vertical = 3.dp)
                    .clickable { if (followed) onDelFollow() else onAddFollow() }
                    .animateContentSize(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (followed) {
                    Icon(
                        imageVector = Icons.Rounded.Done,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Text(
                        text = stringResource(R.string.video_info_followed),
                        color = Color.White
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Text(text = stringResource(R.string.video_info_follow), color = Color.White)
                }
            }
        }
    }
}

@Composable
fun VideoDescriptionDialog(
    modifier: Modifier = Modifier,
    show: Boolean,
    onHideDialog: () -> Unit,
    description: String
) {
    val state = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(show) {
        if (show) {
            focusRequester.requestFocus()
        }
    }

    if (show) {
        TvAlertDialog(
            modifier = modifier
                .fillMaxWidth(0.8f),
            onDismissRequest = { onHideDialog() },
            properties = DialogProperties(usePlatformDefaultWidth = false),
            title = {
                Text(
                    text = stringResource(R.string.video_info_description_title),
                    color = Color.White
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .heightIn(max = 320.dp)
                        .focusable()
                        .focusRequester(focusRequester)
                        .onKeyEvent { event ->
                            if (event.type == KeyEventType.KeyDown) {
                                when (event.key) {
                                    Key.DirectionUp -> {
                                        scope.launch { state.animateScrollBy(-state.layoutInfo.viewportSize.height / 3f) }
                                        true
                                    }

                                    Key.DirectionDown -> {
                                        scope.launch { state.animateScrollBy(state.layoutInfo.viewportSize.height / 3f) }
                                        true
                                    }

                                    else -> false
                                }
                            } else {
                                false
                            }
                        },
                    state = state
                ) {
                    item {
                        Text(text = description)
                    }
                }
            },
            confirmButton = {}
        )
    }
}

@Composable
private fun VideoPartButton(
    modifier: Modifier = Modifier,
    index: Int,
    title: String,
    duration: Int,
    played: Int = 0,
    type: VideoPartType = VideoPartType.Part,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier,
        colors = ClickableSurfaceDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedContainerColor = MaterialTheme.colorScheme.inverseSurface,
            pressedContainerColor = MaterialTheme.colorScheme.inverseSurface
        ),
        shape = ClickableSurfaceDefaults.shape(shape = MaterialTheme.shapes.medium),
        onClick = { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(200.dp, 64.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.2f))
                    .fillMaxHeight()
                    .fillMaxWidth(if (played < 0) 1f else (played / duration.toFloat()))
            ) {}
            Text(
                modifier = Modifier
                    .padding(8.dp),
                text = when (type) {
                    VideoPartType.Episode -> "EP"
                    VideoPartType.Part -> "P"
                } + "$index $title",
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private enum class VideoPartType {
    Episode, Part
}

@Composable
private fun VideoPartRowButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.height(64.dp),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedContainerColor = MaterialTheme.colorScheme.inverseSurface,
            pressedContainerColor = MaterialTheme.colorScheme.inverseSurface
        ),
        shape = ClickableSurfaceDefaults.shape(shape = MaterialTheme.shapes.medium),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier
                    .size(48.dp)
                    .rotate(90f),
                imageVector = Icons.Rounded.ViewModule,
                contentDescription = null
            )
        }
    }
}

@Composable
fun VideoPartRow(
    modifier: Modifier = Modifier,
    pages: List<VideoPage>,
    lastPlayedCid: Long = 0,
    lastPlayedTime: Int = 0,
    enablePartListDialog: Boolean = false,
    nested: Boolean = false,
    subtitle: String = "",
    onClick: (cid: Long) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    var hasFocus by remember { mutableStateOf(false) }
    var showPartListDialog by remember { mutableStateOf(false) }
    val titleFontSize by animateFloatAsState(
        targetValue = if (hasFocus) 30f else 14f,
        label = "title font size",
        animationSpec = tween(
            durationMillis = 120
        )
    )

    Column(
        modifier = modifier
            .ifElse(!nested, Modifier.padding(start = 26.dp))
            .onFocusChanged { hasFocus = it.hasFocus },
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier
                .padding(start = 10.dp),
            text = stringResource(R.string.video_info_part_row_title)
                    + (" - $subtitle".takeIf { subtitle.isNotBlank() } ?: ""),
            fontSize = titleFontSize.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        LazyRow(
            modifier = Modifier
                .padding(top = 4.dp)
                .focusRestorer(focusRequester),
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (enablePartListDialog) {
                item {
                    VideoPartRowButton(
                        onClick = { showPartListDialog = true }
                    )
                }
            }
            itemsIndexed(items = pages, key = { _, page -> page.cid }) { index, page ->
                VideoPartButton(
                    modifier = Modifier
                        .ifElse(index == 0, Modifier.focusRequester(focusRequester)),
                    index = index + 1,
                    title = page.title,
                    played = if (page.cid == lastPlayedCid) lastPlayedTime else 0,
                    duration = page.duration,
                    onClick = { onClick(page.cid) }
                )
            }
        }
    }

    VideoPartListDialog(
        show = showPartListDialog,
        onHideDialog = { showPartListDialog = false },
        pages = pages,
        title = "分 P 列表",
        onClick = onClick
    )
}

@Composable
fun VideoUgcSeasonRow(
    modifier: Modifier = Modifier,
    title: String,
    episodes: List<Episode>,
    lastPlayedCid: Long = 0,
    lastPlayedTime: Int = 0,
    enableUgcListDialog: Boolean = false,
    onClickEp: (avid: Long, cid: Long) -> Unit,
    onClickEpPart: (episode: Episode, cid: Long) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    var hasFocus by remember { mutableStateOf(false) }
    var showUgcListDialog by remember { mutableStateOf(false) }
    val titleFontSize by animateFloatAsState(
        targetValue = if (hasFocus) 30f else 14f,
        label = "title font size",
        animationSpec = tween(
            durationMillis = 120
        )
    )
    var focusingEpisode by remember { mutableStateOf<Episode?>(null) }

    Column(
        modifier = modifier
            .padding(start = 26.dp)
            .onFocusChanged { hasFocus = it.hasFocus },
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier
                .padding(start = 10.dp),
            text = title,
            fontSize = titleFontSize.sp
        )

        LazyRow(
            modifier = Modifier
                .padding(top = 4.dp)
                .focusRestorer(focusRequester),
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (enableUgcListDialog) {
                item {
                    VideoPartRowButton(
                        onClick = { showUgcListDialog = true }
                    )
                }
            }
            itemsIndexed(items = episodes) { index, episode ->
                VideoPartButton(
                    modifier = Modifier
                        .ifElse(index == 0, Modifier.focusRequester(focusRequester))
                        .onFocusChanged { if (it.hasFocus) focusingEpisode = episode },
                    index = index + 1,
                    title = episode.title,
                    played = if (episode.cid == lastPlayedCid) lastPlayedTime else 0,
                    duration = episode.duration,
                    type = VideoPartType.Episode,
                    onClick = { onClickEp(episode.aid, episode.cid) }
                )
            }
        }

        AnimatedVisibility((focusingEpisode?.pages?.size ?: 0) > 1) {
            VideoPartRow(
                modifier = Modifier.padding(top = 8.dp),
                pages = focusingEpisode!!.pages,
                lastPlayedCid = lastPlayedCid,
                lastPlayedTime = lastPlayedTime,
                enablePartListDialog = (focusingEpisode?.pages?.size ?: 0) > 5,
                nested = true,
                onClick = { onClickEpPart(focusingEpisode!!, it) },
                subtitle = focusingEpisode!!.title
            )
        }
    }

    VideoUgcListDialog(
        show = showUgcListDialog,
        onHideDialog = { showUgcListDialog = false },
        episodes = episodes,
        title = "合集列表",
        onClick = onClickEp
    )
}

@Composable
private fun VideoPartListDialog(
    modifier: Modifier = Modifier,
    show: Boolean,
    title: String,
    pages: List<VideoPage>,
    onHideDialog: () -> Unit,
    onClick: (cid: Long) -> Unit
) {
    val scope = rememberCoroutineScope()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabCount by remember { mutableIntStateOf(ceil(pages.size / 20.0).toInt()) }
    val selectedVideoPart = remember { mutableStateListOf<VideoPage>() }

    val tabFocusRequester = remember { FocusRequester() }
    val tabRowFocusRequester = remember { FocusRequester() }
    val videoListFocusRequester = remember { FocusRequester() }
    val listState = rememberLazyGridState()

    LaunchedEffect(selectedTabIndex) {
        val fromIndex = selectedTabIndex * 20
        var toIndex = (selectedTabIndex + 1) * 20
        if (toIndex >= pages.size) {
            toIndex = pages.size
        }
        selectedVideoPart.swapListWithMainContext(pages.subList(fromIndex, toIndex))
    }

    LaunchedEffect(show) {
        if (show && tabCount > 1) tabFocusRequester.requestFocus(scope)
        if (show && tabCount == 1) videoListFocusRequester.requestFocus(scope)
    }

    if (show) {
        TvAlertDialog(
            modifier = modifier,
            title = { Text(text = title) },
            onDismissRequest = { onHideDialog() },
            confirmButton = {},
            properties = DialogProperties(usePlatformDefaultWidth = false),
            text = {
                Column(
                    modifier = Modifier.size(600.dp, 330.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TabRow(
                        modifier = Modifier
                            .onFocusChanged {
                                if (it.hasFocus) {
                                    scope.launch(Dispatchers.Main) {
                                        listState.scrollToItem(0)
                                    }
                                }
                            }
                            .focusRestorer()
                            .focusRequester(tabRowFocusRequester),
                        selectedTabIndex = selectedTabIndex,
                        separator = { Spacer(modifier = Modifier.width(12.dp)) },
                    ) {
                        for (i in 0 until tabCount) {
                            Tab(
                                modifier = if (i == 0) Modifier.focusRequester(
                                    tabFocusRequester
                                ) else Modifier,
                                selected = i == selectedTabIndex,
                                onFocus = { selectedTabIndex = i },
                            ) {
                                Text(
                                    text = "P${i * 20 + 1}-${(i + 1) * 20}",
                                    fontSize = 12.sp,
                                    color = LocalContentColor.current,
                                    modifier = Modifier.padding(
                                        horizontal = 16.dp,
                                        vertical = 6.dp
                                    )
                                )
                            }
                        }
                    }

                    LazyVerticalGrid(
                        modifier = Modifier
                            .onBackPressed {
                                if (tabCount > 1) tabRowFocusRequester.requestFocus() else onHideDialog()
                            },
                        state = listState,
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(
                            items = selectedVideoPart,
                            key = { _, video -> video.cid }
                        ) { index, page ->
                            val buttonModifier =
                                if (index == 0) Modifier.focusRequester(videoListFocusRequester) else Modifier

                            VideoPartButton(
                                modifier = buttonModifier,
                                index = page.index,
                                title = page.title,
                                played = 0,
                                duration = page.duration,
                                onClick = { onClick(page.cid) }
                            )
                        }
                    }
                }
            }
        )
    }
}

@Composable
private fun VideoUgcListDialog(
    modifier: Modifier = Modifier,
    show: Boolean,
    title: String,
    episodes: List<Episode>,
    onHideDialog: () -> Unit,
    onClick: (avid: Long, cid: Long) -> Unit
) {
    val scope = rememberCoroutineScope()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabCount by remember { mutableIntStateOf(ceil(episodes.size / 20.0).toInt()) }
    val selectedVideoPart = remember { mutableStateListOf<Episode>() }

    val tabFocusRequester = remember { FocusRequester() }
    val tabRowFocusRequester = remember { FocusRequester() }
    val videoListFocusRequester = remember { FocusRequester() }
    val listState = rememberLazyGridState()

    LaunchedEffect(selectedTabIndex) {
        val fromIndex = selectedTabIndex * 20
        var toIndex = (selectedTabIndex + 1) * 20
        if (toIndex >= episodes.size) {
            toIndex = episodes.size
        }
        selectedVideoPart.swapListWithMainContext(episodes.subList(fromIndex, toIndex))
    }

    LaunchedEffect(show) {
        if (show && tabCount > 1) tabFocusRequester.requestFocus(scope)
        if (show && tabCount == 1) videoListFocusRequester.requestFocus(scope)
    }

    if (show) {
        TvAlertDialog(
            modifier = modifier,
            title = { Text(text = title) },
            onDismissRequest = { onHideDialog() },
            confirmButton = {},
            properties = DialogProperties(usePlatformDefaultWidth = false),
            text = {
                Column(
                    modifier = Modifier.size(600.dp, 330.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TabRow(
                        modifier = Modifier
                            .onFocusChanged {
                                if (it.hasFocus) {
                                    scope.launch(Dispatchers.Main) {
                                        listState.scrollToItem(0)
                                    }
                                }
                            }
                            .focusRestorer()
                            .focusRequester(tabRowFocusRequester),
                        selectedTabIndex = selectedTabIndex,
                        separator = { Spacer(modifier = Modifier.width(12.dp)) },
                    ) {
                        for (i in 0 until tabCount) {
                            Tab(
                                modifier = if (i == 0) Modifier.focusRequester(
                                    tabFocusRequester
                                ) else Modifier,
                                selected = i == selectedTabIndex,
                                onFocus = { selectedTabIndex = i },
                            ) {
                                Text(
                                    text = "EP${i * 20 + 1}-${(i + 1) * 20}",
                                    fontSize = 12.sp,
                                    color = LocalContentColor.current,
                                    modifier = Modifier.padding(
                                        horizontal = 16.dp,
                                        vertical = 6.dp
                                    )
                                )
                            }
                        }
                    }

                    LazyVerticalGrid(
                        modifier = Modifier
                            .onBackPressed {
                                if (tabCount > 1) tabRowFocusRequester.requestFocus() else onHideDialog()
                            },
                        state = listState,
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(
                            items = selectedVideoPart,
                            key = { _, video -> video.cid }
                        ) { index, episode ->
                            val buttonModifier =
                                if (index == 0) Modifier.focusRequester(videoListFocusRequester) else Modifier

                            VideoPartButton(
                                modifier = buttonModifier,
                                index = selectedTabIndex * 20 + index + 1,
                                type = VideoPartType.Episode,
                                title = episode.title,
                                played = 0,
                                duration = episode.duration,
                                onClick = { onClick(episode.aid, episode.cid) }
                            )
                        }
                    }
                }
            }
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun VideoPartButtonShortTextPreview() {
    BVTheme {
        VideoPartButton(
            index = 2,
            title = "这是一段短文字",
            duration = 100,
            onClick = {}
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun VideoPartButtonLongTextPreview() {
    BVTheme {
        VideoPartButton(
            index = 2,
            title = "这可能是我这辈子距离梅西最近的一次",
            played = 23333,
            duration = 100,
            onClick = {}
        )
    }
}


@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun VideoPartRowPreview() {
    val pages = remember { mutableStateListOf<VideoPage>() }
    for (i in 0..10) {
        pages.add(
            VideoPage(
                cid = 1000L + i,
                index = i,
                title = "这可能是我这辈子距离梅西最近的一次",
                duration = 10,
                dimension = Dimension(0, 0)
            )
        )
    }
    BVTheme {
        VideoPartRow(pages = pages, onClick = {})
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun UpButtonPreview() {
    var followed by remember { mutableStateOf(false) }
    BVTheme {
        UpButton(
            name = "12435678",
            followed = followed,
            onClickUp = { followed = !followed },
            onAddFollow = {},
            onDelFollow = {}
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CoverPreview() {
    Box{
        AsyncImage(
            modifier = Modifier
                .fillMaxSize(),
            // model = if (videoDetail.ugcSeason != null) videoDetail.ugcSeason!!.cover else videoDetail.cover,
            model = "http://i2.hdslb.com/bfs/archive/af17fc07b8f735e822563cc45b7b5607a491dfff.jpg",
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(48.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 0.dp,
                        bottomStart = 16.dp,
                        bottomEnd = 16.dp
                    )
                )
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.8f)
                        )
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start=16.dp, end=16.dp, bottom = 12.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ){
                Icon(
                    modifier = Modifier,
                    painter = painterResource(id = R.drawable.ic_play_count),
                    contentDescription = null,
                    tint = Color.White
                )
                Text(
                    text = "3009",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    modifier = Modifier,
                    painter = painterResource(id = R.drawable.ic_danmaku_count),
                    contentDescription = null,
                    tint = Color.White
                )
                Text(
                    text = "1099",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White
                )

                Spacer(Modifier.weight(1f))
                Text(
                    text = "12:34",
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}