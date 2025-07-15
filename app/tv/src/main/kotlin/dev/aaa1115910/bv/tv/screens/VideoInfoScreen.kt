package dev.aaa1115910.bv.tv.screens

import android.app.Activity
import android.content.res.Configuration
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
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
import androidx.tv.material3.Glow
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
import coil.compose.rememberAsyncImagePainter
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
import dev.aaa1115910.biliapi.repositories.FavoriteRepository
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
import dev.aaa1115910.bv.tv.component.TvAlertDialog
import dev.aaa1115910.bv.tv.component.UpIcon
import dev.aaa1115910.bv.tv.component.buttons.FavoriteButton
import dev.aaa1115910.bv.tv.component.videocard.VideosRow
import dev.aaa1115910.bv.tv.util.launchPlayerActivity
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.fDebug
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.fWarn
import dev.aaa1115910.bv.util.focusedBorder
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.getKoin
import kotlin.math.ceil

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun VideoInfoScreen(
    modifier: Modifier = Modifier,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    videoInfoRepository: VideoInfoRepository = getKoin().get(),
    videoDetailViewModel: VideoDetailViewModel = koinViewModel(),
    userRepository: UserRepository = getKoin().get(),
    favoriteRepository: FavoriteRepository = getKoin().get(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val intent = (context as Activity).intent
    val logger = KotlinLogging.logger { }
    val defaultFocusRequester = remember { FocusRequester() }

    var showFollowButton by remember { mutableStateOf(false) }
    var isFollowing by remember { mutableStateOf(false) }

    var lastPlayedCid by remember { mutableLongStateOf(0) }
    var lastPlayedTime by remember { mutableIntStateOf(0) }

    var tip by remember { mutableStateOf("Loading") }
    var fromSeason by remember { mutableStateOf(false) }
    var paused by remember { mutableStateOf(false) }
    var proxyArea by remember { mutableStateOf(ProxyArea.MainLand) }

    val containsVerticalScreenVideo by remember {
        derivedStateOf {
            videoDetailViewModel.videoDetail?.pages?.any { it.dimension.isVertical } ?: false
                    || videoDetailViewModel.videoDetail?.ugcSeason?.sections?.any { section -> section.episodes.any { it.dimension!!.isVertical } } ?: false
        }
    }

    var favorited by remember { mutableStateOf(false) }
    val favoriteFolderMetadataList = remember { mutableStateListOf<FavoriteFolderMetadata>() }
    val videoInFavoriteFolderIds = remember { mutableStateListOf<Long>() }

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
            setHistory()
        }
    }


    val updateFollowingState: () -> Unit = {
        scope.launch(Dispatchers.IO) {
            val userMid = videoDetailViewModel.videoDetail?.author?.mid ?: -1
            logger.fInfo { "Checking is following user $userMid" }
            val success = userRepository.checkIsFollowing(
                mid = userMid,
                preferApiType = Prefs.apiType
            )
            logger.fInfo { "Following user result: $success" }
            withContext(Dispatchers.Main) {
                showFollowButton = success != null
                isFollowing = success == true
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
            afterModify(success)
        }
    }

    val fetchFavoriteData: (Long) -> Unit = { avid ->
        scope.launch(Dispatchers.IO) {
            runCatching {
                val favoriteFolderMetadataListResult =
                    favoriteRepository.getAllFavoriteFolderMetadataList(
                        mid = Prefs.uid,
                        rid = avid,
                        preferApiType = Prefs.apiType
                    )
                favoriteFolderMetadataList.swapListWithMainContext(favoriteFolderMetadataListResult)

                val videoInFavoriteFolderIdsResult = favoriteFolderMetadataListResult
                    .filter { it.videoInThisFav }
                videoInFavoriteFolderIds.swapListWithMainContext(videoInFavoriteFolderIdsResult.map { it.id })

                logger.fDebug { "Update favoriteFolders: ${favoriteFolderMetadataList.map { it.title }}" }
                logger.fDebug { "Update videoInFavoriteFolderIds: ${videoInFavoriteFolderIdsResult.map { it.title }}" }
            }
        }
    }

    val updateVideoFavoriteData: (List<Long>) -> Unit = { folderIds ->
        scope.launch(Dispatchers.IO) {
            runCatching {
                require(favoriteFolderMetadataList.isNotEmpty()) { "Not found favorite folder" }
                require(videoDetailViewModel.videoDetail?.aid != null) { "Video info is null" }
                logger.info { "Update video av${videoDetailViewModel.videoDetail?.aid} to favorite folder $folderIds" }

                favoriteRepository.updateVideoToFavoriteFolder(
                    aid = videoDetailViewModel.videoDetail!!.aid,
                    addMediaIds = folderIds,
                    delMediaIds = favoriteFolderMetadataList.map { it.id } - folderIds.toSet()
                )
            }.onFailure {
                logger.fInfo { "Update video to favorite folder failed: ${it.stackTraceToString()}" }
                withContext(Dispatchers.Main) {
                    it.message ?: "unknown error".toast(context)
                }
            }.onSuccess {
                logger.fInfo { "Update video to favorite folder success" }
                videoInFavoriteFolderIds.swapList(folderIds)
            }
        }
    }

    val addVideoToDefaultFavoriteFolder: () -> Unit = {
        runCatching {
            val defaultFavoriteFolder =
                favoriteFolderMetadataList.firstOrNull { it.title == "默认收藏夹" }
            require(defaultFavoriteFolder != null) { "Not found default favorite folder" }
            updateVideoFavoriteData(listOf(defaultFavoriteFolder.id))
        }.onFailure {
            logger.fInfo { "Add video to default favorite folder failed: ${it.stackTraceToString()}" }
            it.message ?: "unknown error".toast(context)
        }
    }

    val updateVideoIsFavoured = {
        favorited = videoDetailViewModel.videoDetail?.userActions?.favorite ?: false
    }

    val updateUgcSeasonSectionVideoList: (Int) -> Unit = { sectionIndex ->
        val partVideoList = mutableListOf<VideoListItem>()
        videoDetailViewModel.videoDetail!!.ugcSeason!!.sections[sectionIndex].episodes.mapIndexed { epIndex, episode ->
            if (episode.pages.size == 1) {
                episode.pages.mapIndexed { pageInd, videoPage ->
                    partVideoList.add(
                        VideoListUgcEpisode(
                            aid = episode.aid,
                            cid = videoPage.cid,
                            title = videoPage.title,
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
                            title = videoPage.title,
                            index = pageIndex,
                        )
                    )
                }
            }
        }
        videoInfoRepository.videoList.clear()
        videoInfoRepository.videoList.addAll(partVideoList)
    }

    LaunchedEffect(Unit) {
        if (intent.hasExtra("aid")) {
            val aid = intent.getLongExtra("aid", 170001)
            fromSeason = intent.getBooleanExtra("fromSeason", false)
            proxyArea = ProxyArea.entries[intent.getIntExtra("proxyArea", 0)]
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
                    updateVideoIsFavoured()
                    setHistory()
                    if (Prefs.isLogin) fetchFavoriteData(aid)

                    //如果是从剧集跳转过来的，就直接播放 P1
                    if (fromSeason) {
                        val playPart = videoDetailViewModel.videoDetail!!.pages.first()
                        launchPlayerActivity(
                            context = context,
                            avid = videoDetailViewModel.videoDetail!!.aid,
                            cid = playPart.cid,
                            title = videoDetailViewModel.videoDetail!!.title,
                            partTitle = videoDetailViewModel.videoDetail!!.pages.find { it.cid == playPart.cid }!!.title,
                            played = if (playPart.cid == lastPlayedCid) lastPlayedTime * 1000 else 0,
                            fromSeason = true,
                            isVerticalVideo = containsVerticalScreenVideo,
                            playerIconIdle = videoDetailViewModel.videoDetail!!.playerIcon?.idle
                                ?: "",
                            playerIconMoving = videoDetailViewModel.videoDetail!!.playerIcon?.moving
                                ?: ""
                        )
                        context.finish()
                    }
                }.onFailure {
                    val errorMessage = it.localizedMessage
                    val isVideoNotFound = when (Prefs.apiType) {
                        ApiType.Web -> errorMessage == "啥都木有"
                        ApiType.App -> errorMessage == "访问权限不足"
                    }

                    logger.fInfo { "Get video info failed: ${it.stackTraceToString()}" }
                    if (!isVideoNotFound || !Prefs.enableProxy) {
                        tip = it.localizedMessage ?: "未知错误"
                        return@onFailure
                    }
                    videoDetailViewModel.state = VideoInfoState.Loading

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
                            tip = "视频不存在"
                            videoDetailViewModel.state = VideoInfoState.Error
                        }
                    }.onFailure { e ->
                        logger.fWarn { "Redirect failed: ${e.stackTraceToString()}" }
                        tip = e.localizedMessage ?: "未知错误"
                        videoDetailViewModel.state = VideoInfoState.Error
                    }
                }
            }
        }
    }

    LaunchedEffect(videoDetailViewModel.videoDetail) {
        //如果是从剧集页跳转回来的，那就不需要再跳转到剧集页了
        if (fromSeason) return@LaunchedEffect

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

            if (!fromSeason) updateFollowingState()
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

    if (videoDetailViewModel.videoDetail == null || videoDetailViewModel.videoDetail?.redirectToEp == true || fromSeason) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            if (tip == "Loading") {
                LoadingIndicator(
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.Center),
                )
            } else {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = tip
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
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(if (videoDetailViewModel.videoDetail?.ugcSeason != null) videoDetailViewModel.videoDetail!!.ugcSeason!!.cover else videoDetailViewModel.videoDetail!!.cover)
                            .transformations(BlurTransformation(LocalContext.current, 20f, 5f))
                            .build()
                    ),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    alpha = 0.6f
                )
                LazyColumn(
                    contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
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
                                                .indexOfFirst { section -> section.episodes.any { it.cid == cid } }
                                        updateUgcSeasonSectionVideoList(sectionIndex)
                                    }
                                } else {
                                    // 分 p
                                    val partVideoList =
                                        videoDetailViewModel.videoDetail!!.pages.mapIndexed { index, videoPage ->
                                            VideoListPart(
                                                aid = videoDetailViewModel.videoDetail!!.aid,
                                                cid = videoPage.cid,
                                                title = videoPage.title,
                                                index = index,
                                            )
                                        }
                                    videoInfoRepository.videoList.clear()
                                    videoInfoRepository.videoList.addAll(partVideoList)
                                }

                                launchPlayerActivity(
                                    context = context,
                                    avid = videoDetailViewModel.videoDetail!!.aid,
                                    cid = videoDetailViewModel.videoDetail!!.pages.first().cid,
                                    title = videoDetailViewModel.videoDetail!!.title,
                                    partTitle = videoDetailViewModel.videoDetail!!.pages.first().title,
                                    played = if (videoDetailViewModel.videoDetail!!.cid == lastPlayedCid) lastPlayedTime * 1000 else 0,
                                    fromSeason = false,
                                    isVerticalVideo = containsVerticalScreenVideo,
                                    playerIconIdle = videoDetailViewModel.videoDetail!!.playerIcon?.idle
                                        ?: "",
                                    playerIconMoving = videoDetailViewModel.videoDetail!!.playerIcon?.moving
                                        ?: ""
                                )
                            },
                            onClickUp = {
                                UpInfoActivity.actionStart(
                                    context,
                                    mid = videoDetailViewModel.videoDetail!!.author.mid,
                                    name = videoDetailViewModel.videoDetail!!.author.name
                                )
                            },
                            onAddFollow = {
                                addFollow {
                                    updateFollowingState()
                                }
                            },
                            onDelFollow = {
                                delFollow {
                                    updateFollowingState()
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
                            },
                            onUpdateFavoriteFolders = {
                                updateVideoFavoriteData(it)
                                favorited = it.isNotEmpty()
                                videoInFavoriteFolderIds.swapList(it)
                            }
                        )
                    }
                    if ((videoDetailViewModel.videoDetail?.description ?: "").isNotBlank()) {
                        item {
                            VideoDescription(
                                description = videoDetailViewModel.videoDetail?.description
                                    ?: "no desc"
                            )
                        }
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
                                        isVerticalVideo = containsVerticalScreenVideo,
                                        playerIconIdle = videoDetailViewModel.videoDetail!!.playerIcon?.idle
                                            ?: "",
                                        playerIconMoving = videoDetailViewModel.videoDetail!!.playerIcon?.moving
                                            ?: ""
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
                                    launchPlayerActivity(
                                        context = context,
                                        avid = aid,
                                        cid = cid,
                                        title = videoDetailViewModel.videoDetail?.ugcSeason!!.title,
                                        partTitle = section.episodes.find { it.cid == cid }!!.title,
                                        played = if (cid == lastPlayedCid) lastPlayedTime * 1000 else 0,
                                        fromSeason = false,
                                        isVerticalVideo = containsVerticalScreenVideo,
                                        playerIconIdle = videoDetailViewModel.videoDetail!!.playerIcon?.idle
                                            ?: "",
                                        playerIconMoving = videoDetailViewModel.videoDetail!!.playerIcon?.moving
                                            ?: ""
                                    )
                                },
                                onClickEpPart = { episode, cid ->
                                    logger.fInfo { "Click ugc season episode part: [av:${videoDetailViewModel.videoDetail?.aid}, bv:${videoDetailViewModel.videoDetail?.bvid}, cid:$cid]" }
                                    launchPlayerActivity(
                                        context = context,
                                        avid = episode.aid,
                                        cid = cid,
                                        title = videoDetailViewModel.videoDetail!!.title,
                                        partTitle = episode.pages.find { it.cid == cid }!!.title,
                                        played = if (cid == lastPlayedCid) lastPlayedTime * 1000 else 0,
                                        fromSeason = false,
                                        isVerticalVideo = containsVerticalScreenVideo,
                                        playerIconIdle = videoDetailViewModel.videoDetail!!.playerIcon?.idle
                                            ?: "",
                                        playerIconMoving = videoDetailViewModel.videoDetail!!.playerIcon?.moving
                                            ?: ""
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
}

@Composable
fun ArgueTip(
    modifier: Modifier = Modifier,
    text: String
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 50.dp),
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
    onUpdateFavoriteFolders: (List<Long>) -> Unit
) {
    val localDensity = LocalDensity.current
    var heightIs by remember { mutableStateOf(0.dp) }

    Row(
        modifier = modifier
            .padding(horizontal = 50.dp, vertical = 16.dp),
    ) {
        Surface(
            modifier = Modifier
                .focusRequester(defaultFocusRequester)
                .weight(3f)
                .aspectRatio(1.6f)
                .onGloballyPositioned { coordinates ->
                    heightIs = with(localDensity) { coordinates.size.height.toDp() }
                },
            onClick = onClickCover,
            shape = ClickableSurfaceDefaults.shape(
                shape = MaterialTheme.shapes.large,
            ),
            glow = ClickableSurfaceDefaults.glow(
                focusedGlow = Glow(
                    elevationColor = MaterialTheme.colorScheme.inverseSurface,
                    elevation = 16.dp
                )
            ),
            border = if (Build.VERSION.SDK_INT < 28) {
                ClickableSurfaceDefaults.border(
                    focusedBorder = Border(
                        border = BorderStroke(
                            width = 3.dp,
                            color = MaterialTheme.colorScheme.border
                        ),
                        shape = MaterialTheme.shapes.large
                    )
                )
            } else {
                ClickableSurfaceDefaults.border()
            }
        ) {
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = if (videoDetail.ugcSeason != null) videoDetail.ugcSeason!!.cover else videoDetail.cover,
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.width(24.dp))
        Column(
            modifier = Modifier
                .weight(7f)
                .height(heightIs),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
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
                    modifier = Modifier.fillMaxWidth(),
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
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    UpButton(
                        name = videoDetail.author.name,
                        followed = isFollowing,
                        showFollowButton = showFollowButton,
                        onClickUp = onClickUp,
                        onAddFollow = onAddFollow,
                        onDelFollow = onDelFollow
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.End),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FavoriteButton(
                    isFavorite = isFavorite,
                    userFavoriteFolders = userFavoriteFolders,
                    favoriteFolderIds = favoriteFolderIds,
                    onAddToDefaultFavoriteFolder = onAddToDefaultFavoriteFolder,
                    onUpdateFavoriteFolders = onUpdateFavoriteFolders
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
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
        horizontalArrangement = Arrangement.spacedBy(2.dp)
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
        AnimatedVisibility(visible = isLogin && showFollowButton) {
            Row(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .background(Color.White.copy(alpha = 0.2f))
                    .focusedBorder(MaterialTheme.shapes.small)
                    .padding(horizontal = 4.dp, vertical = 3.dp)
                    .clickable { if (followed) onDelFollow() else onAddFollow() }
                    .animateContentSize()
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
fun VideoDescription(
    modifier: Modifier = Modifier,
    description: String
) {
    var hasFocus by remember { mutableStateOf(false) }
    val titleColor = if (hasFocus) Color.White else Color.White.copy(alpha = 0.6f)
    val titleFontSize by animateFloatAsState(
        targetValue = if (hasFocus) 30f else 14f,
        label = "title font size"
    )
    var showDescriptionDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .padding(horizontal = 50.dp),
    ) {
        Text(
            text = stringResource(R.string.video_info_description_title),
            fontSize = titleFontSize.sp,
            color = titleColor
        )
        Box(
            modifier = Modifier
                .padding(top = 15.dp)
                .onFocusChanged { hasFocus = it.hasFocus }
                .clip(MaterialTheme.shapes.medium)
                .focusedBorder(MaterialTheme.shapes.medium)
                .padding(8.dp)
                .clickable {
                    showDescriptionDialog = true
                }
        ) {
            Text(
                text = description,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = Color.White
            )
        }
    }

    VideoDescriptionDialog(
        show = showDescriptionDialog,
        onHideDialog = { showDescriptionDialog = false },
        description = description
    )
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
    val titleColor = if (hasFocus) Color.White else Color.White.copy(alpha = 0.6f)
    val titleFontSize by animateFloatAsState(
        targetValue = if (hasFocus) 30f else 14f,
        label = "title font size"
    )

    Column(
        modifier = modifier
            .ifElse(!nested, Modifier.padding(start = 50.dp))
            .onFocusChanged { hasFocus = it.hasFocus },
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(R.string.video_info_part_row_title)
                    + (" - $subtitle".takeIf { subtitle.isNotBlank() } ?: ""),
            fontSize = titleFontSize.sp,
            color = titleColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        LazyRow(
            modifier = Modifier
                .padding(top = 15.dp)
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
    val titleColor = if (hasFocus) Color.White else Color.White.copy(alpha = 0.6f)
    val titleFontSize by animateFloatAsState(
        targetValue = if (hasFocus) 30f else 14f,
        label = "title font size"
    )
    var focusingEpisode by remember { mutableStateOf<Episode?>(null) }

    Column(
        modifier = modifier
            .padding(start = 50.dp)
            .onFocusChanged { hasFocus = it.hasFocus },
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            fontSize = titleFontSize.sp,
            color = titleColor
        )

        LazyRow(
            modifier = Modifier
                .padding(top = 15.dp)
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
        selectedVideoPart.swapList(pages.subList(fromIndex, toIndex))
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
        selectedVideoPart.swapList(episodes.subList(fromIndex, toIndex))
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
fun VideoDescriptionPreview() {
    BVTheme {
        VideoDescription(description = "12435678")
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