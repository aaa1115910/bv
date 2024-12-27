package dev.aaa1115910.bv.screen

import android.app.Activity
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ViewModule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
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
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.Glow
import androidx.tv.material3.Icon
import androidx.tv.material3.LocalContentColor
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Tab
import androidx.tv.material3.TabRow
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import dev.aaa1115910.biliapi.entity.ApiType
import dev.aaa1115910.biliapi.entity.video.season.Episode
import dev.aaa1115910.biliapi.entity.video.season.PgcSeason
import dev.aaa1115910.biliapi.entity.video.season.SeasonDetail
import dev.aaa1115910.biliapi.repositories.UserRepository
import dev.aaa1115910.biliapi.repositories.VideoDetailRepository
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.activities.video.VideoInfoActivity
import dev.aaa1115910.bv.component.buttons.SeasonInfoButtons
import dev.aaa1115910.bv.component.createCustomInitialFocusRestorerModifiers
import dev.aaa1115910.bv.component.ifElse
import dev.aaa1115910.bv.entity.proxy.ProxyArea
import dev.aaa1115910.bv.repository.VideoInfoRepository
import dev.aaa1115910.bv.repository.VideoListItem
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.util.ImageSize
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.focusedScale
import dev.aaa1115910.bv.util.launchPlayerActivity
import dev.aaa1115910.bv.util.requestFocus
import dev.aaa1115910.bv.util.resizedImageUrl
import dev.aaa1115910.bv.util.swapList
import dev.aaa1115910.bv.util.toast
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.compose.getKoin
import kotlin.math.ceil

@Composable
fun SeasonInfoScreen(
    modifier: Modifier = Modifier,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    videoInfoRepository: VideoInfoRepository = getKoin().get(),
    videoDetailRepository: VideoDetailRepository = getKoin().get(),
    userRepository: UserRepository = getKoin().get()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val intent = (context as Activity).intent
    val logger = KotlinLogging.logger { }

    var seasonId: Int? by remember { mutableStateOf(null) }
    var epId: Int? by remember { mutableStateOf(null) }
    var proxyArea: ProxyArea by remember { mutableStateOf(ProxyArea.MainLand) }

    var seasonData: SeasonDetail? by remember { mutableStateOf(null) }
    var lastPlayProgress: SeasonDetail.UserStatus.Progress? by remember { mutableStateOf(null) }
    var isFollowing by remember { mutableStateOf(false) }
    var tip by remember { mutableStateOf("Loading") }
    var paused by remember { mutableStateOf(false) }

    var showSeasonSelector by remember { mutableStateOf(false) }

    val defaultFocusRequester = remember { FocusRequester() }

    val onClickVideo: (avid: Long, cid: Long, epid: Int, episodeTitle: String, startTime: Int) -> Unit =
        { avid, cid, epid, episodeTitle, startTime ->
            logger.debug { "onClickVideo: [avid=$avid, cid=$cid, epid=$epid, episodeTitle=$episodeTitle, startTime=$startTime]" }
            if (cid != 0L) {
                launchPlayerActivity(
                    context = context,
                    avid = avid,
                    cid = cid,
                    title = seasonData!!.title,
                    partTitle = episodeTitle,
                    played = startTime * 1000,
                    fromSeason = true,
                    subType = seasonData?.subType,
                    epid = epid,
                    seasonId = seasonData?.seasonId,
                    proxyArea = proxyArea,
                    playerIconIdle = seasonData?.playerIcon?.idle ?: "",
                    playerIconMoving = seasonData?.playerIcon?.moving ?: ""
                )
            } else {
                //如果 cid==0，就需要跳转回 VideoInfoActivity 去获取 cid 再跳转播放器
                VideoInfoActivity.actionStart(
                    context = context,
                    aid = avid,
                    fromSeason = true
                )
            }
        }

    val updateSeasonData: (seasonId: Int?, epId: Int?) -> Unit = { sId, eId ->
        scope.launch(Dispatchers.IO) {
            runCatching {
                val data = videoDetailRepository.getPgcVideoDetail(
                    seasonId = sId,
                    epid = eId,
                    preferApiType = if (proxyArea != ProxyArea.MainLand) ApiType.App else Prefs.apiType
                )
                withContext(Dispatchers.Main) {
                    seasonData = data
                    logger.info { "User status: ${seasonData!!.userStatus}" }
                    isFollowing = seasonData!!.userStatus.follow
                    lastPlayProgress = seasonData!!.userStatus.progress
                }
            }.onFailure {
                tip = it.localizedMessage ?: "未知错误"
                logger.fInfo { "Get season info failed: ${it.stackTraceToString()}" }
            }
        }
    }

    val updateHistoryAfterBack = {
        scope.launch(Dispatchers.IO) {
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
    }

    LaunchedEffect(Unit) {
        val epId1 = intent.getIntExtra("epid", 0)
        val seasonId1 = intent.getIntExtra("seasonid", 0)
        val proxyArea1 = intent.getIntExtra("proxy_area", 0)
        logger.fInfo { "Read extras from content: [epId=$epId1, seasonId=$seasonId1, proxyArea=$proxyArea1]" }

        epId = intent.getIntExtra("epid", 0).takeIf { it > 0 }
        seasonId = intent.getIntExtra("seasonid", 0).takeIf { it > 0 }
        proxyArea = ProxyArea.entries[proxyArea1]
        logger.fInfo { "Read extras from content: [epId=$epId, seasonId=$seasonId, proxyArea=$proxyArea]" }
        if (epId != null || seasonId != null) {
            updateSeasonData(seasonId, epId)
        } else {
            context.finish()
        }
    }

    LaunchedEffect(seasonData) {
        seasonData?.let {
            lastPlayProgress = it.userStatus.progress
            //请求默认焦点到剧集封面上
            defaultFocusRequester.requestFocus(scope)
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                paused = true
            } else if (event == Lifecycle.Event.ON_RESUME) {
                // 如果 pause==true 那可能是从播放页返回回来的，此时更新历史记录
                if (paused) updateHistoryAfterBack()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    if (seasonData == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = tip
            )
        }
    } else {
        Scaffold(
            modifier = modifier
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    SeasonInfoPart(
                        modifier = Modifier.focusRequester(defaultFocusRequester),
                        title = seasonData!!.title,
                        cover = seasonData!!.cover,
                        newEpDesc = seasonData!!.newEpDesc,
                        description = seasonData!!.description,
                        lastPlayedIndex = lastPlayProgress?.lastEpId ?: -1,
                        lastPlayedTitle = lastPlayProgress?.lastEpIndex ?: "Unknown",
                        following = isFollowing,
                        isPublished = seasonData!!.publish.isPublished,
                        publishDate = seasonData!!.publish.publishDate,
                        seasonCount = seasonData!!.seasons.size,
                        onPlay = {
                            logger.fInfo { "Click play button" }
                            var playAid = -1L
                            var playCid = -1L
                            val playEpid: Int
                            var episodeList: List<Episode> = emptyList()
                            if (lastPlayProgress == null) {
                                logger.fInfo { "Didn't find any play record" }
                                //未登录或无播放记录，此时lastPlayProgress==null，默认播放第一集正片
                                playAid = seasonData?.episodes?.first()?.aid ?: -1
                                playCid = seasonData?.episodes?.first()?.cid ?: -1
                                playEpid = seasonData?.episodes?.first()?.id ?: -1
                                if (playCid == -1L) {
                                    R.string.season_no_feature_film.toast(context)
                                } else {
                                    episodeList = seasonData?.episodes ?: emptyList()
                                }
                            } else {
                                //已登录且有播放记录
                                logger.fInfo { "Find play record: $lastPlayProgress" }

                                //懒得去改播放器那边来支持epid，就直接在这边查找cid了
                                playEpid = lastPlayProgress!!.lastEpId
                                seasonData?.episodes?.forEach {
                                    if (it.id == playEpid) {
                                        playAid = it.aid
                                        playCid = it.cid
                                        episodeList = seasonData?.episodes ?: emptyList()
                                    }
                                }
                                if (playCid == -1L) {
                                    seasonData?.sections?.forEach { section ->
                                        section.episodes.forEach {
                                            if (it.id == playEpid) {
                                                playAid = it.aid
                                                playCid = it.cid
                                                episodeList = section.episodes
                                            }
                                        }
                                    }
                                }
                                if (playCid == -1L) {
                                    logger.fInfo { "Can't find cid" }
                                    "无法判断最后播放的剧集".toast(context)
                                }
                            }

                            logger.fInfo { "Play aid: $playAid, cid: $playCid" }

                            if (playCid != -1L) {
                                onClickVideo(
                                    playAid,
                                    playCid,
                                    playEpid,
                                    lastPlayProgress?.lastEpIndex ?: "",
                                    lastPlayProgress?.lastTime ?: 0
                                )

                                val partVideoList = episodeList.mapIndexed { index, episode ->
                                    VideoListItem(
                                        aid = episode.aid,
                                        cid = episode.cid,
                                        epid = episode.id,
                                        seasonId = seasonData?.seasonId,
                                        title = runCatching {
                                            "第 ${episode.title.toInt()} 集"
                                        }.getOrDefault(episode.title) + " " + episode.longTitle,
                                        index = index,
                                        isEpisode = true
                                    )
                                }
                                videoInfoRepository.videoList.clear()
                                videoInfoRepository.videoList.addAll(partVideoList)
                            }
                        },
                        onClickFollow = {
                            if (isFollowing) {
                                scope.launch(Dispatchers.Default) {
                                    runCatching {
                                        val resultToast = userRepository.delSeasonFollow(
                                            seasonId = seasonData?.seasonId ?: return@launch,
                                            preferApiType = Prefs.apiType
                                        )
                                        isFollowing = false
                                        withContext(Dispatchers.Main) {
                                            resultToast.toast(context)
                                        }
                                    }.onFailure {
                                        logger.fInfo { "Del season follow failed: ${it.stackTraceToString()}" }
                                        withContext(Dispatchers.Main) {
                                            R.string.follow_bangumi_disable_fail.toast(context)
                                        }
                                    }
                                }
                            } else {
                                scope.launch(Dispatchers.Default) {
                                    runCatching {
                                        val resultToast = userRepository.addSeasonFollow(
                                            seasonId = seasonData?.seasonId ?: return@launch,
                                            preferApiType = Prefs.apiType
                                        )
                                        isFollowing = true
                                        withContext(Dispatchers.Main) {
                                            resultToast.toast(context)
                                        }
                                    }.onFailure {
                                        logger.fInfo { "Add season follow failed: ${it.stackTraceToString()}" }
                                        withContext(Dispatchers.Main) {
                                            R.string.follow_bangumi_enable_fail.toast(context)
                                        }
                                    }
                                }
                            }
                        },
                        onClickCover = {
                            if (seasonData?.seasons?.isNotEmpty() == true) showSeasonSelector = true
                        }
                    )
                }
                if (seasonData?.episodes?.isNotEmpty() == true) {
                    item {
                        SeasonEpisodeRow(
                            title = stringResource(R.string.season_feature_film),
                            episodes = seasonData?.episodes ?: emptyList(),
                            lastPlayedId = lastPlayProgress?.lastEpId ?: 0,
                            lastPlayedTime = lastPlayProgress?.lastTime ?: 0,
                            onClick = { avid, cid, epid, episodeTitle, startTime ->
                                onClickVideo(avid, cid, epid, episodeTitle, startTime)

                                val partVideoList =
                                    seasonData?.episodes?.mapIndexed { index, episode ->
                                        VideoListItem(
                                            aid = episode.aid,
                                            cid = episode.cid,
                                            epid = episode.id,
                                            seasonId = seasonData?.seasonId,
                                            title = runCatching {
                                                "第 ${episode.title.toInt()} 集"
                                            }.getOrDefault(episode.title) + " " + episode.longTitle,
                                            index = index,
                                            isEpisode = true
                                        )
                                    } ?: emptyList()
                                videoInfoRepository.videoList.clear()
                                videoInfoRepository.videoList.addAll(partVideoList)
                            }
                        )
                    }
                }
                seasonData?.sections?.forEach { section ->
                    item {
                        SeasonEpisodeRow(
                            title = section.title,
                            episodes = section.episodes,
                            lastPlayedId = lastPlayProgress?.lastEpId ?: 0,
                            lastPlayedTime = lastPlayProgress?.lastTime ?: 0,
                            onClick = { avid, cid, epid, episodeTitle, startTime ->
                                onClickVideo(avid, cid, epid, episodeTitle, startTime)

                                val partVideoList = section.episodes.mapIndexed { index, episode ->
                                    VideoListItem(
                                        aid = episode.aid,
                                        cid = episode.cid,
                                        epid = episode.id,
                                        seasonId = seasonData?.seasonId,
                                        title = runCatching {
                                            "第 ${episode.title.toInt()} 集"
                                        }.getOrDefault(episode.title) + " " + episode.longTitle,
                                        index = index,
                                        isEpisode = true
                                    )
                                }
                                videoInfoRepository.videoList.clear()
                                videoInfoRepository.videoList.addAll(partVideoList)
                            }
                        )
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(64.dp))
                }
            }
        }
    }

    SeasonSelector(
        show = showSeasonSelector,
        onHideSelector = {
            showSeasonSelector = false
            runCatching {
                defaultFocusRequester.requestFocus(scope)
            }
        },
        currentSeasonId = seasonId ?: 0,
        seasons = seasonData?.seasons ?: emptyList(),
        onClickSeason = { sid ->
            if ((seasonId ?: 0) != sid) {
                seasonData = null
                updateSeasonData(sid, null)
                seasonId = sid
            }
        }
    )
}

@Composable
fun SeasonCover(
    modifier: Modifier = Modifier,
    cover: String,
    seasonCount: Int,
    onClick: () -> Unit
) {
    val isPreview = LocalInspectionMode.current
    var hasFocus by remember { mutableStateOf(false) }
    val coverBottomTipHeight by animateDpAsState(
        targetValue = if (hasFocus && seasonCount != 0) 28.dp else 0.dp,
        label = "Cover bottom tip height"
    )

    Card(
        modifier = modifier.onFocusChanged { hasFocus = it.hasFocus },
        onClick = onClick,
        shape = CardDefaults.shape(shape = MaterialTheme.shapes.large),
        glow = CardDefaults.glow(
            focusedGlow = Glow(
                elevationColor = MaterialTheme.colorScheme.inverseSurface,
                elevation = 16.dp
            )
        ),
        border = if (Build.VERSION.SDK_INT < 31) {
            CardDefaults.border()
        } else {
            CardDefaults.border(
                focusedBorder = Border(BorderStroke(0.dp, Color.Transparent))
            )
        }
    ) {
        Box {
            AsyncImage(
                modifier = Modifier
                    .height(260.dp)
                    .aspectRatio(0.75f)
                    .background(if (isPreview) Color.White else Color.Transparent),
                model = cover,
                contentDescription = null,
                contentScale = ContentScale.FillHeight
            )

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .width(195.dp)
                    .height(coverBottomTipHeight)
                    .background(Color.Black.copy(alpha = 0.6f)),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier,
                    text = stringResource(R.string.season_count_tip, seasonCount),
                )
            }
        }
    }
}

@Composable
fun SeasonBaseInfo(
    modifier: Modifier = Modifier,
    title: String,
    newEpDesc: String,
    description: String,
    lastPlayedIndex: Int,
    lastPlayedTitle: String = "",
    following: Boolean,
    isPublished: Boolean,
    publishDate: String,
    onPlay: () -> Unit,
    onClickFollow: (follow: Boolean) -> Unit,
) {
    Column(
        modifier = modifier
            .heightIn(min = 260.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color.White
            )
            Text(text = newEpDesc)
            Text(text = description)
        }
        Spacer(modifier = Modifier.height(12.dp))
        SeasonInfoButtons(
            lastPlayedIndex = lastPlayedIndex,
            lastPlayedTitle = lastPlayedTitle,
            following = following,
            isPublished = isPublished,
            publishDate = publishDate,
            onPlay = onPlay,
            onClickFollow = onClickFollow
        )
    }
}

@Composable
fun SeasonInfoPart(
    modifier: Modifier = Modifier,
    title: String,
    cover: String,
    newEpDesc: String,
    description: String,
    lastPlayedIndex: Int,
    lastPlayedTitle: String = "",
    following: Boolean,
    isPublished: Boolean,
    publishDate: String,
    seasonCount: Int,
    onPlay: () -> Unit,
    onClickFollow: (follow: Boolean) -> Unit,
    onClickCover: () -> Unit
) {
    Row(
        modifier = modifier
            .padding(horizontal = 50.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SeasonCover(
            cover = cover,
            seasonCount = seasonCount,
            onClick = onClickCover
        )
        SeasonBaseInfo(
            title = title,
            newEpDesc = newEpDesc,
            description = description,
            lastPlayedIndex = lastPlayedIndex,
            lastPlayedTitle = lastPlayedTitle,
            following = following,
            isPublished = isPublished,
            publishDate = publishDate,
            onPlay = onPlay,
            onClickFollow = onClickFollow
        )
    }
}


@Composable
fun SeasonEpisodeButton(
    modifier: Modifier = Modifier,
    partTitle: String = "",
    title: String,
    cover: String,
    duration: Int,
    played: Int = 0,
    onClick: () -> Unit
) {
    val isPreview = LocalInspectionMode.current

    Surface(
        modifier = modifier,
        colors = ClickableSurfaceDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedContainerColor = MaterialTheme.colorScheme.inverseSurface,
            pressedContainerColor = MaterialTheme.colorScheme.inverseSurface
        ),
        shape = ClickableSurfaceDefaults.shape(shape = MaterialTheme.shapes.medium),
        onClick = onClick
    ) {
        Row {
            val coverBackground by remember { mutableStateOf(if (played != 0) Color.Black.copy(alpha = 0.2f) else Color.Transparent) }
            Box(
                modifier = Modifier.background(coverBackground)
            ) {
                AsyncImage(
                    modifier = Modifier
                        .height(80.dp)
                        .aspectRatio(1.6f)
                        .clip(MaterialTheme.shapes.medium)
                        .background(if (isPreview) Color.White else Color.Transparent),
                    model = cover.resizedImageUrl(ImageSize.Cover),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds
                )
            }

            Box(
                modifier = Modifier
                    .size(140.dp, 80.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.2f))
                        .fillMaxHeight()
                        .fillMaxWidth(if (duration == 0) 0f else if (played < 0) 1f else ((played * 1000f) / duration))
                ) {}
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.SpaceAround
                ) {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(text = partTitle)
                    }
                    Box(
                        modifier = Modifier.weight(2f),
                        contentAlignment = Alignment.TopStart
                    ) {
                        Text(
                            text = title,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SeasonEpisodesDialog(
    modifier: Modifier = Modifier,
    show: Boolean,
    title: String,
    episodes: List<Episode>,
    lastPlayedId: Int = 0,
    lastPlayedTime: Int = 0,
    onHideDialog: () -> Unit,
    onClick: (avid: Long, cid: Long, epid: Int, episodeTitle: String, startTime: Int) -> Unit
) {
    val scope = rememberCoroutineScope()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabCount by remember { mutableIntStateOf(ceil(episodes.size / 20.0).toInt()) }
    val selectedEpisodes = remember { mutableStateListOf<Episode>() }

    val tabRowFocusRequester = remember { FocusRequester() }
    val videoListFocusRequester = remember { FocusRequester() }
    val listState = rememberLazyGridState()

    LaunchedEffect(selectedTabIndex) {
        val fromIndex = selectedTabIndex * 20
        var toIndex = (selectedTabIndex + 1) * 20
        if (toIndex >= episodes.size) {
            toIndex = episodes.size
        }
        selectedEpisodes.swapList(episodes.subList(fromIndex, toIndex))
    }

    LaunchedEffect(show) {
        if (show && tabCount > 1) tabRowFocusRequester.requestFocus(scope)
        if (show && tabCount == 1) videoListFocusRequester.requestFocus(scope)
    }

    if (show) {
        AlertDialog(
            modifier = modifier,
            title = { Text(text = title) },
            onDismissRequest = { onHideDialog() },
            confirmButton = {},
            properties = DialogProperties(usePlatformDefaultWidth = false),
            text = {
                Column(
                    modifier = Modifier
                        .size(600.dp, 330.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // TabRow 只有一项 Tab 时会导致崩溃，但如果只有一项 Tab 的时候也没必要显示
                    // https://issuetracker.google.com/issues/264018028
                    if (tabCount > 1) {
                        TabRow(
                            modifier = Modifier
                                .onFocusChanged {
                                    if (it.hasFocus) {
                                        scope.launch(Dispatchers.Main) {
                                            listState.scrollToItem(0)
                                        }
                                    }
                                },
                            selectedTabIndex = selectedTabIndex,
                            separator = { Spacer(modifier = Modifier.width(12.dp)) },
                        ) {
                            for (i in 0 until tabCount) {
                                Tab(
                                    modifier = if (i == 0) Modifier.focusRequester(
                                        tabRowFocusRequester
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
                    }

                    LazyVerticalGrid(
                        state = listState,
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        itemsIndexed(
                            items = selectedEpisodes,
                            key = { _, episode -> episode.aid + episode.cid }
                        ) { index, episode ->
                            val episodeTitle by remember { mutableStateOf(if (episode.longTitle != "") episode.longTitle else episode.title) }
                            val buttonModifier =
                                if (index == 0) Modifier.focusRequester(videoListFocusRequester) else Modifier
                            SeasonEpisodeButton(
                                modifier = buttonModifier
                                    .focusedScale(0.95f),
                                partTitle = if (title == "正片") {
                                    //如果 title 是数字的话，就会返回 "第 x 集"
                                    //如果 title 不是数字的话（例如 SP），就会原样使用 title
                                    runCatching {
                                        "第 ${episode.title.toInt()} 集"
                                    }.getOrDefault(episode.title)
                                } else {
                                    "P${index + 1 + selectedTabIndex * 20}"
                                },
                                title = episodeTitle,
                                cover = episode.cover,
                                played = if (episode.id == lastPlayedId) lastPlayedTime else 0,
                                duration = episode.duration,
                                onClick = {
                                    onClick(
                                        episode.aid,
                                        episode.cid,
                                        episode.id,
                                        episode.longTitle,
                                        if (episode.id == lastPlayedId) lastPlayedTime else 0
                                    )
                                }
                            )
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun SeasonEpisodeRow(
    modifier: Modifier = Modifier,
    title: String,
    episodes: List<Episode>,
    lastPlayedId: Int = 0,
    lastPlayedTime: Int = 0,
    onClick: (avid: Long, cid: Long, epid: Int, episodeTitle: String, startTime: Int) -> Unit
) {
    val focusRestorerModifiers = createCustomInitialFocusRestorerModifiers()
    var hasFocus by remember { mutableStateOf(false) }
    val titleColor = if (hasFocus) Color.White else Color.White.copy(alpha = 0.6f)
    val titleFontSize by animateFloatAsState(
        targetValue = if (hasFocus) 30f else 14f,
        label = "title font size"
    )

    var showEpisodesDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .onFocusChanged { hasFocus = it.hasFocus },
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier.padding(start = 50.dp),
            text = title,
            fontSize = titleFontSize.sp,
            color = titleColor
        )

        LazyRow(
            modifier = Modifier
                .padding(top = 15.dp)
                .then(focusRestorerModifiers.parentModifier),
            contentPadding = PaddingValues(horizontal = 50.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            item {
                Surface(
                    modifier = modifier.size(60.dp, 80.dp),
                    colors = ClickableSurfaceDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedContainerColor = MaterialTheme.colorScheme.inverseSurface,
                        pressedContainerColor = MaterialTheme.colorScheme.inverseSurface
                    ),
                    shape = ClickableSurfaceDefaults.shape(shape = MaterialTheme.shapes.medium),
                    onClick = { showEpisodesDialog = true }
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
            itemsIndexed(items = episodes) { index, episode ->
                val episodeTitle by remember { mutableStateOf(if (episode.longTitle != "") episode.longTitle else episode.title) }
                SeasonEpisodeButton(
                    modifier = Modifier
                        .ifElse(index == 0, focusRestorerModifiers.childModifier),
                    partTitle = if (title == "正片") {
                        //如果 title 是数字的话，就会返回 "第 x 集"
                        //如果 title 不是数字的话（例如 SP），就会原样使用 title
                        runCatching {
                            "第 ${episode.title.toInt()} 集"
                        }.getOrDefault(episode.title)
                    } else {
                        "P${index + 1}"
                    },
                    title = episodeTitle,
                    cover = episode.cover,
                    played = if (episode.id == lastPlayedId) lastPlayedTime else 0,
                    duration = episode.duration,
                    onClick = {
                        onClick(
                            episode.aid,
                            episode.cid,
                            episode.id,
                            episode.longTitle,
                            if (episode.id == lastPlayedId) lastPlayedTime else 0
                        )
                    }
                )
            }
        }
    }

    SeasonEpisodesDialog(
        show = showEpisodesDialog,
        title = title,
        episodes = episodes,
        onHideDialog = { showEpisodesDialog = false },
        onClick = onClick
    )
}

@Composable
fun SeasonSelector(
    modifier: Modifier = Modifier,
    show: Boolean,
    onHideSelector: () -> Unit,
    currentSeasonId: Int,
    seasons: List<PgcSeason>,
    onClickSeason: (Int) -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(show) {
        if (show) {
            focusRequester.requestFocus()
        }
    }

    if (show) {
        SeasonSelectorContent(
            modifier = modifier
                .focusRequester(focusRequester),
            seasons = seasons,
            currentSeasonId = currentSeasonId,
            onClickSeason = { seasonId ->
                onClickSeason(seasonId)
                onHideSelector()
            }
        )
    }

    BackHandler(show) {
        onHideSelector()
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SeasonSelectorContent(
    modifier: Modifier = Modifier,
    currentSeasonId: Int,
    seasons: List<PgcSeason>,
    onClickSeason: (Int) -> Unit
) {
    val scope = rememberCoroutineScope()
    val rowState = rememberLazyListState()
    val logger = KotlinLogging.logger {}
    val currentSeasonFocusRequester = remember { FocusRequester() }
    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    var scrolling by remember { mutableStateOf(false) }
    var currentSeasonIndex by remember { mutableIntStateOf(0) }
    val isCurrentSeasonInScreen by remember {
        derivedStateOf {
            rowState.layoutInfo.visibleItemsInfo.first().index <= currentSeasonIndex
                    && rowState.layoutInfo.visibleItemsInfo.last().index >= currentSeasonIndex
        }
    }

    val scrollToCurrentSeason = {
        currentSeasonIndex = seasons.indexOfFirst { it.seasonId == currentSeasonId }
        logger.info { "Season row scroll to index $currentSeasonIndex" }
        if (currentSeasonIndex != -1) {
            if (isCurrentSeasonInScreen) {
                currentSeasonFocusRequester.requestFocus()
            } else {
                scope.launch {
                    scrolling = true
                    rowState.scrollToItem(currentSeasonIndex)
                }
            }
        }
    }

    LaunchedEffect(rowState.firstVisibleItemScrollOffset) {
        if (scrolling && isCurrentSeasonInScreen) {
            scrolling = false
            currentSeasonFocusRequester.requestFocus()
        }
    }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .onFocusChanged {
                if (it.hasFocus) scrollToCurrentSeason()
            },
        shape = RoundedCornerShape(0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                AsyncImage(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .fillMaxHeight(0.7f)
                        .graphicsLayer { alpha = 0.99f }
                        .drawWithContent {
                            val colors = listOf(
                                Color.Black,
                                Color.Transparent
                            )
                            drawContent()
                            drawRect(
                                brush = Brush.horizontalGradient(colors),
                                blendMode = BlendMode.DstOut
                            )
                            drawRect(
                                brush = Brush.verticalGradient(colors),
                                blendMode = BlendMode.DstIn
                            )
                        },
                    model = seasons[currentSeasonIndex].horizontalCover ?: "",
                    contentDescription = null,
                    contentScale = ContentScale.FillHeight,
                    alpha = 1f
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(
                            start = 48.dp,
                            end = 48.dp,
                            bottom = 300.dp
                        )
                ) {
                    Text(
                        text = seasons[currentSeasonIndex].title
                            ?: seasons[currentSeasonIndex].shortTitle,
                        style = MaterialTheme.typography.displayMedium
                    )
                }
            }

            Box(
                modifier = Modifier.align(Alignment.BottomStart)
            ) {
                LazyRow(
                    modifier = Modifier.padding(bottom = 48.dp),
                    state = rowState,
                    contentPadding = PaddingValues(horizontal = 48.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    itemsIndexed(items = seasons) { index, season ->
                        Card(
                            modifier = Modifier
                                .onFocusChanged {
                                    if (it.hasFocus) currentSeasonIndex = index
                                }
                                .ifElse(
                                    season.seasonId == currentSeasonId,
                                    Modifier.focusRequester(currentSeasonFocusRequester)
                                )
                                .ifElse(
                                    season.seasonId == currentSeasonId,
                                    Modifier.bringIntoViewRequester(bringIntoViewRequester)
                                ),
                            glow = CardDefaults.glow(
                                focusedGlow = Glow(
                                    elevationColor = MaterialTheme.colorScheme.inverseSurface,
                                    elevation = 16.dp
                                )
                            ),
                            border = if (Build.VERSION.SDK_INT < 31) {
                                CardDefaults.border()
                            } else {
                                CardDefaults.border(
                                    focusedBorder = Border(BorderStroke(0.dp, Color.Transparent))
                                )
                            },
                            onClick = {
                                onClickSeason(season.seasonId)
                            }
                        ) {
                            AsyncImage(
                                modifier = Modifier
                                    .width(160.dp)
                                    .aspectRatio(0.75f),
                                model = seasons[index].cover,
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(device = "id:tv_1080p")
@Composable
fun SeasonInfoPartPreview() {
    BVTheme {
        SeasonInfoPart(
            modifier = Modifier.fillMaxWidth(),
            title = "人生一串",
            cover = "http://i0.hdslb.com/bfs/bangumi/7a790c64ff70f12c11888be0532b6981a923afd5.jpg",
            newEpDesc = "已完结, 全8集",
            description = "由bilibili和旗帜传媒联合出品的《人生一串》是国内首档汇聚民间烧烤美食，呈现国人烧烤情结的深夜美食纪录片，本片将镜头伸向街头巷尾，讲述平民美食和市井传奇，以最独特的视角真实展现烧烤美食背后的独特情感。作为一档接地气的美食节目，《串》旨在展现每一串烧烤的魅力往事，和最真实的美味体验。",
            lastPlayedIndex = 3,
            lastPlayedTitle = "拯救灵依计划",
            following = false,
            isPublished = true,
            publishDate = "2021-04-30",
            seasonCount = 0,
            onPlay = {},
            onClickFollow = {},
            onClickCover = {}
        )
    }
}

@Preview(device = "id:tv_1080p")
@Composable
fun SeasonEpisodeRowPreview() {
    val episodes = remember { mutableStateListOf<Episode>() }
    for (i in 0..10) {
        episodes.add(
            Episode(
                id = 0,
                aid = 0,
                bvid = "",
                cid = 0,
                epid = 1000 + i,
                title = "这可能是我这辈子距离梅西最近的一次",
                longTitle = "",
                cover = "",
                duration = 0,
                dimension = null
            )
        )
    }
    BVTheme {
        SeasonEpisodeRow(
            title = "正片",
            episodes = episodes,
            onClick = { _, _, _, _, _ -> }
        )
    }
}

@Preview(device = "id:tv_1080p")
@Composable
private fun SeasonSelectorPreview() {
    val seasons = listOf(
        PgcSeason(
            seasonId = 25210,
            title = "命运之夜  06版",
            shortTitle = "FATE TV",
            cover = "http://i0.hdslb.com/bfs/bangumi/1113d844ad3a9b42af576d80142146cbecc1b7ff.jpg",
            horizontalCover = "http://i0.hdslb.com/bfs/bangumi/e3993240914c3d881d97e4527a52efa2a9dcdeaf.jpg"
        ),
        PgcSeason(
            seasonId = 29006,
            title = "Fate/stay night UNLIMITED BLADE WORKS",
            shortTitle = "UBW 剧场版",
            cover = "http://i0.hdslb.com/bfs/bangumi/image/b7ee578ff3c258f173587db3f687fa2d56e3b8c1.jpg",
            horizontalCover = "http://i0.hdslb.com/bfs/archive/ae6fcc22f6c627a899bfe2d736765cc83cd4e827.png"
        ),
        PgcSeason(
            seasonId = 1586,
            title = "Fate/stay night [Unlimited Blade Works] 第一季",
            shortTitle = "UBW第一季",
            cover = "http://i0.hdslb.com/bfs/bangumi/image/e67e09c9e48a32371a81100e0f65a61b18aabb24.png",
            horizontalCover = "http://i0.hdslb.com/bfs/bangumi/25e9da6dd71e4aaa23a7dc04b6f97a94ea1ddd9d.jpg"
        ),
        PgcSeason(
            seasonId = 25210,
            title = "命运之夜  06版",
            shortTitle = "FATE TV",
            cover = "http://i0.hdslb.com/bfs/bangumi/1113d844ad3a9b42af576d80142146cbecc1b7ff.jpg",
            horizontalCover = "http://i0.hdslb.com/bfs/bangumi/e3993240914c3d881d97e4527a52efa2a9dcdeaf.jpg"
        ),
        PgcSeason(
            seasonId = 29006,
            title = "Fate/stay night UNLIMITED BLADE WORKS",
            shortTitle = "UBW 剧场版",
            cover = "http://i0.hdslb.com/bfs/bangumi/image/b7ee578ff3c258f173587db3f687fa2d56e3b8c1.jpg",
            horizontalCover = "http://i0.hdslb.com/bfs/archive/ae6fcc22f6c627a899bfe2d736765cc83cd4e827.png"
        ),
        PgcSeason(
            seasonId = 1586,
            title = "Fate/stay night [Unlimited Blade Works] 第一季",
            shortTitle = "UBW第一季",
            cover = "http://i0.hdslb.com/bfs/bangumi/image/e67e09c9e48a32371a81100e0f65a61b18aabb24.png",
            horizontalCover = "http://i0.hdslb.com/bfs/bangumi/25e9da6dd71e4aaa23a7dc04b6f97a94ea1ddd9d.jpg"
        ),
    )
    BVTheme {
        SeasonSelectorContent(
            seasons = seasons,
            currentSeasonId = 25210,
            onClickSeason = {}
        )
    }
}