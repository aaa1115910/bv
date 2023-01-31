package dev.aaa1115910.bv.screen

import android.app.Activity
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ViewModule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.tv.foundation.lazy.grid.TvGridCells
import androidx.tv.foundation.lazy.grid.TvLazyVerticalGrid
import androidx.tv.foundation.lazy.grid.itemsIndexed
import androidx.tv.foundation.lazy.grid.rememberTvLazyGridState
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.TvLazyRow
import androidx.tv.foundation.lazy.list.itemsIndexed
import androidx.tv.material.LocalContentColor
import androidx.tv.material.Tab
import androidx.tv.material.TabRow
import coil.compose.AsyncImage
import dev.aaa1115910.biliapi.BiliApi
import dev.aaa1115910.biliapi.entity.season.Episode
import dev.aaa1115910.biliapi.entity.season.SeasonData
import dev.aaa1115910.biliapi.entity.video.Dimension
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.activities.video.VideoInfoActivity
import dev.aaa1115910.bv.activities.video.VideoPlayerActivity
import dev.aaa1115910.bv.component.buttons.SeasonInfoButtons
import dev.aaa1115910.bv.repository.VideoInfoRepository
import dev.aaa1115910.bv.repository.VideoListItem
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.util.ImageSize
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.focusedBorder
import dev.aaa1115910.bv.util.focusedScale
import dev.aaa1115910.bv.util.requestFocus
import dev.aaa1115910.bv.util.resizedImageUrl
import dev.aaa1115910.bv.util.swapList
import dev.aaa1115910.bv.util.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.koin.androidx.compose.getKoin
import kotlin.math.ceil

@Composable
fun SeasonInfoScreen(
    modifier: Modifier = Modifier,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    videoInfoRepository: VideoInfoRepository = getKoin().get()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val intent = (context as Activity).intent
    val logger = KotlinLogging.logger { }

    var seasonData: SeasonData? by remember { mutableStateOf(null) }
    var lastPlayProgress: SeasonData.UserStatus.Progress? by remember { mutableStateOf(null) }
    var isFollowing by remember { mutableStateOf(false) }
    var tip by remember { mutableStateOf("Loading") }
    var paused by remember { mutableStateOf(false) }

    val defaultFocusRequester = remember { FocusRequester() }

    val onClickVideo: (avid: Int, cid: Int, epid: Int, episodeTitle: String, startTime: Int) -> Unit =
        { avid, cid, epid, episodeTitle, startTime ->
            if (cid != 0) {
                VideoPlayerActivity.actionStart(
                    context = context,
                    avid = avid,
                    cid = cid,
                    title = seasonData!!.title,
                    partTitle = episodeTitle,
                    played = startTime * 100,
                    fromSeason = true,
                    subType = seasonData?.type,
                    epid = epid,
                    seasonId = seasonData?.seasonId
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

    val updateUserStatus: () -> Unit = {
        scope.launch(Dispatchers.Default) {
            runCatching {
                val userStatus = BiliApi.getSeasonUserStatus(
                    seasonId = seasonData!!.seasonId,
                    sessData = Prefs.sessData
                ).getResponseData()
                logger.info { "User status: $userStatus" }
                isFollowing = userStatus.follow != 0
                lastPlayProgress = userStatus.progress
            }.onFailure {
                logger.fInfo { "Get season user status failed: ${it.stackTraceToString()}" }
            }
        }
    }

    LaunchedEffect(Unit) {
        val epId = intent.getIntExtra("epid", 0)
        val seasonId = intent.getIntExtra("seasonid", 0)
        logger.fInfo { "Read extras from content: [epId=$epId, seasonId=$seasonId]" }
        if (epId > 0 || seasonId > 0) {
            scope.launch(Dispatchers.Default) {
                runCatching {
                    val seasonResponse = BiliApi.getSeasonInfo(
                        seasonId = if (seasonId > 0) seasonId else null,
                        epId = if (epId > 0) epId else null,
                        sessData = Prefs.sessData
                    )
                    seasonData = seasonResponse.getResponseData()
                    updateUserStatus()
                }.onFailure {
                    tip = it.localizedMessage ?: "未知错误"
                    logger.fInfo { "Get season info failed: ${it.stackTraceToString()}" }
                }
            }
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
                if (paused) updateUserStatus()
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
        TvLazyColumn(
            modifier = modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                SeasonInfoPart(
                    modifier = Modifier.focusRequester(defaultFocusRequester),
                    title = seasonData!!.title,
                    cover = seasonData!!.cover,
                    newEpDesc = seasonData!!.newEp.desc,
                    evaluate = seasonData!!.evaluate,
                    lastPlayedIndex = lastPlayProgress?.lastEpId ?: -1,
                    lastPlayedTitle = lastPlayProgress?.lastEpIndex ?: "Unknown",
                    following = isFollowing,
                    onPlay = {
                        logger.fInfo { "Click play button" }
                        var playAid = -1
                        var playCid = -1
                        val playEpid: Int
                        var episodeList: List<Episode> = emptyList()
                        if (lastPlayProgress == null) {
                            logger.fInfo { "Didn't find any play record" }
                            //未登录或无播放记录，此时lastPlayProgress==null，默认播放第一集正片
                            playAid = seasonData?.episodes?.first()?.aid?.toInt() ?: -1
                            playCid = seasonData?.episodes?.first()?.cid ?: -1
                            playEpid = seasonData?.episodes?.first()?.id ?: -1
                            if (playCid == -1) {
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
                                    playAid = it.aid.toInt()
                                    playCid = it.cid
                                    episodeList = seasonData?.episodes ?: emptyList()
                                }
                            }
                            if (playCid == -1) {
                                seasonData?.section?.forEach { section ->
                                    section.episodes.forEach {
                                        if (it.id == playEpid) {
                                            playAid = it.aid.toInt()
                                            playCid = it.cid
                                            episodeList = section.episodes
                                        }
                                    }
                                }
                            }
                            if (playCid == -1) {
                                logger.fInfo { "Can't find cid" }
                                "无法判断最后播放的剧集".toast(context)
                            }
                        }

                        logger.fInfo { "Play aid: $playAid, cid: $playCid" }

                        if (playCid != -1) {
                            onClickVideo(
                                playAid,
                                playCid,
                                playEpid,
                                lastPlayProgress?.lastEpIndex ?: "",
                                lastPlayProgress?.lastTime ?: 0
                            )

                            val partVideoList = episodeList.mapIndexed { index, episode ->
                                VideoListItem(
                                    aid = episode.aid.toInt(),
                                    cid = episode.cid,
                                    title = episode.longTitle,
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
                                    val resultToast = BiliApi.delSeasonFollow(
                                        seasonId = seasonData?.seasonId ?: return@launch,
                                        csrf = Prefs.biliJct,
                                        sessData = Prefs.sessData
                                    ).getResponseData().toast
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
                                    val resultToast = BiliApi.addSeasonFollow(
                                        seasonId = seasonData?.seasonId ?: return@launch,
                                        csrf = Prefs.biliJct,
                                        sessData = Prefs.sessData
                                    ).getResponseData().toast
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
                    }
                )
            }
            item {
                SeasonEpisodeRow(
                    title = stringResource(R.string.season_feature_film),
                    episodes = seasonData?.episodes ?: emptyList(),
                    lastPlayedId = lastPlayProgress?.lastEpId ?: 0,
                    lastPlayedTime = lastPlayProgress?.lastTime ?: 0,
                    onClick = { avid, cid, epid, episodeTitle, startTime ->
                        onClickVideo(avid, cid, epid, episodeTitle, startTime)

                        val partVideoList = seasonData?.episodes?.mapIndexed { index, episode ->
                            VideoListItem(
                                aid = episode.aid.toInt(),
                                cid = episode.cid,
                                title = episode.longTitle,
                                index = index,
                                isEpisode = true
                            )
                        } ?: emptyList()
                        videoInfoRepository.videoList.clear()
                        videoInfoRepository.videoList.addAll(partVideoList)
                    }
                )
            }
            seasonData?.section?.forEach { section ->
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
                                    aid = episode.aid.toInt(),
                                    cid = episode.cid,
                                    title = episode.longTitle,
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

@Composable
fun SeasonCover(
    modifier: Modifier = Modifier,
    cover: String,
    onClick: () -> Unit
) {
    val isPreview = LocalInspectionMode.current

    AsyncImage(
        modifier = modifier
            .height(260.dp)
            .aspectRatio(0.75f)
            .clip(MaterialTheme.shapes.large)
            .background(if (isPreview) Color.White else Color.Transparent)
            .focusedBorder(MaterialTheme.shapes.large)
            .clickable { onClick() },
        model = cover,
        contentDescription = null,
        contentScale = ContentScale.FillHeight
    )
}

@Composable
fun SeasonBaseInfo(
    modifier: Modifier = Modifier,
    title: String,
    newEpDesc: String,
    evaluate: String,
    lastPlayedIndex: Int,
    lastPlayedTitle: String = "",
    following: Boolean,
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
            Text(text = evaluate)
        }
        Spacer(modifier = Modifier.height(12.dp))
        SeasonInfoButtons(
            lastPlayedIndex = lastPlayedIndex,
            lastPlayedTitle = lastPlayedTitle,
            following = following,
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
    evaluate: String,
    lastPlayedIndex: Int,
    lastPlayedTitle: String = "",
    following: Boolean,
    onPlay: () -> Unit,
    onClickFollow: (follow: Boolean) -> Unit,
) {
    Row(
        modifier = modifier
            .padding(horizontal = 50.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SeasonCover(
            cover = cover,
            onClick = {}
        )
        SeasonBaseInfo(
            title = title,
            newEpDesc = newEpDesc,
            evaluate = evaluate,
            lastPlayedIndex = lastPlayedIndex,
            lastPlayedTitle = lastPlayedTitle,
            following = following,
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
        modifier = modifier
            .focusedBorder(MaterialTheme.shapes.medium)
            .clickable { onClick() },
        color = MaterialTheme.colorScheme.primary,
        shape = MaterialTheme.shapes.medium,
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SeasonEpisodesDialog(
    modifier: Modifier = Modifier,
    show: Boolean,
    title: String,
    episodes: List<Episode>,
    lastPlayedId: Int = 0,
    lastPlayedTime: Int = 0,
    onHideDialog: () -> Unit,
    onClick: (avid: Int, cid: Int, epid: Int, episodeTitle: String, startTime: Int) -> Unit
) {
    val scope = rememberCoroutineScope()

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabCount by remember { mutableStateOf(ceil(episodes.size / 20.0).toInt()) }
    val selectedEpisodes = remember { mutableStateListOf<Episode>() }

    val tabRowFocusRequester = remember { FocusRequester() }
    val videoListFocusRequester = remember { FocusRequester() }
    val listState = rememberTvLazyGridState()

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

                    TvLazyVerticalGrid(
                        state = listState,
                        columns = TvGridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
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
                                        episode.aid.toInt(),
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
    onClick: (avid: Int, cid: Int, epid: Int, episodeTitle: String, startTime: Int) -> Unit
) {
    var hasFocus by remember { mutableStateOf(false) }
    val titleColor = if (hasFocus) Color.White else Color.Gray
    val titleFontSize by animateFloatAsState(if (hasFocus) 30f else 14f)

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

        TvLazyRow(
            modifier = Modifier
                .padding(top = 15.dp),
            contentPadding = PaddingValues(horizontal = 50.dp)
        ) {
            item {
                Surface(
                    modifier = modifier
                        .size(60.dp, 80.dp)
                        .focusedScale(0.9f)
                        .focusedBorder(MaterialTheme.shapes.medium)
                        .clickable { showEpisodesDialog = true },
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.medium
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
            itemsIndexed(
                items = episodes,
                key = { _, episode -> episode.aid + episode.cid }
            ) { index, episode ->
                val episodeTitle by remember { mutableStateOf(if (episode.longTitle != "") episode.longTitle else episode.title) }
                SeasonEpisodeButton(
                    modifier = Modifier
                        .focusedScale(0.9f),
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
                            episode.aid.toInt(),
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


@Preview(device = "id:tv_1080p")
@Composable
fun SeasonInfoPartPreview() {
    BVTheme {
        SeasonInfoPart(
            modifier = Modifier.fillMaxWidth(),
            title = "人生一串",
            cover = "http://i0.hdslb.com/bfs/bangumi/7a790c64ff70f12c11888be0532b6981a923afd5.jpg",
            newEpDesc = "已完结, 全8集",
            evaluate = "由bilibili和旗帜传媒联合出品的《人生一串》是国内首档汇聚民间烧烤美食，呈现国人烧烤情结的深夜美食纪录片，本片将镜头伸向街头巷尾，讲述平民美食和市井传奇，以最独特的视角真实展现烧烤美食背后的独特情感。作为一档接地气的美食节目，《串》旨在展现每一串烧烤的魅力往事，和最真实的美味体验。",
            lastPlayedIndex = 3,
            lastPlayedTitle = "拯救灵依计划",
            following = false,
            onPlay = {},
            onClickFollow = {}
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
                0, "", Episode.BadgeInfo("", "", ""), 0,
                "", 1000 + i, "", Dimension(0, 0, 0), 1, "",
                0, false, "", "这可能是我这辈子距离梅西最近的一次", 0, 0,
                "", Episode.EpisodeRights(0, 0, 0, 0),
                "", "", "", Episode.Skip(Episode.Skip.SkipTime(0, 0), Episode.Skip.SkipTime(0, 0)),
                0, "", "", ""
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