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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
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
import dev.aaa1115910.bv.activities.video.VideoPlayerActivity
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.focusedBorder
import dev.aaa1115910.bv.util.focusedScale
import dev.aaa1115910.bv.util.swapList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mu.KotlinLogging
import kotlin.math.ceil

@Composable
fun SeasonInfoScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val intent = (context as Activity).intent
    val logger = KotlinLogging.logger { }

    var seasonData: SeasonData? by remember { mutableStateOf(null) }

    var lastPlayProgress: SeasonData.UserStatus.Progress? by remember { mutableStateOf(null) }

    var tip by remember { mutableStateOf("Loading") }

    val defaultFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        if (intent.hasExtra("epid")) {
            val epid = intent.getIntExtra("epid", 0)
            scope.launch(Dispatchers.Default) {
                runCatching {
                    val response = BiliApi.getSeasonInfo(
                        epId = epid,
                        sessData = Prefs.sessData
                    )
                    seasonData = response.getResponseData()
                }.onFailure {
                    tip = it.localizedMessage ?: "未知错误"
                    logger.fInfo { "Get season info failed: ${it.stackTraceToString()}" }
                }
            }
        }
    }

    LaunchedEffect(seasonData) {
        seasonData?.let {
            lastPlayProgress = it.userStatus.progress
            //请求默认焦点到剧集封面上
            defaultFocusRequester.requestFocus()
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
                    evaluate = seasonData!!.evaluate
                )
            }
            item {
                SeasonEpisodeRow(
                    title = "正片",
                    episodes = seasonData?.episodes ?: emptyList(),
                    lastPlayedId = lastPlayProgress?.lastEpId ?: 0,
                    lastPlayedTime = lastPlayProgress?.lastTime ?: 0,
                    onClick = { avid, cid, episodeTitle, startTime ->
                        VideoPlayerActivity.actionStart(
                            context = context,
                            avid = avid,
                            cid = cid,
                            title = seasonData!!.title,
                            partTitle = episodeTitle,
                            played = startTime
                        )
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
                        onClick = { avid, cid, episodeTitle, startTime ->
                            VideoPlayerActivity.actionStart(
                                context = context,
                                avid = avid,
                                cid = cid,
                                title = seasonData!!.title,
                                partTitle = episodeTitle,
                                played = startTime
                            )
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
    evaluate: String
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
        Text(text = "假装我是个追剧按钮")
    }
}

@Composable
fun SeasonInfoPart(
    modifier: Modifier = Modifier,
    title: String,
    cover: String,
    newEpDesc: String,
    evaluate: String
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
            evaluate = evaluate
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
                    model = "$cover@180h_288w_1c.webp",
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
    onClick: (avid: Int, cid: Int, episodeTitle: String, startTime: Int) -> Unit
) {
    val scope = rememberCoroutineScope()

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabCount by remember { mutableStateOf(ceil(episodes.size / 20.0).toInt()) }
    val selectedEpisodes = remember { mutableStateListOf<Episode>() }

    val tabRowFocusRequester = remember { FocusRequester() }
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
        if (show && tabCount > 1) tabRowFocusRequester.requestFocus()
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
                            key = { _, episode -> episode.cid }) { index, episode ->
                            val episodeTitle by remember { mutableStateOf(if (episode.longTitle != "") episode.longTitle else episode.title) }
                            SeasonEpisodeButton(
                                modifier = Modifier
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
    onClick: (avid: Int, cid: Int, episodeTitle: String, startTime: Int) -> Unit
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
            itemsIndexed(items = episodes, key = { _, episode -> episode.cid }) { index, episode ->
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
            evaluate = "由bilibili和旗帜传媒联合出品的《人生一串》是国内首档汇聚民间烧烤美食，呈现国人烧烤情结的深夜美食纪录片，本片将镜头伸向街头巷尾，讲述平民美食和市井传奇，以最独特的视角真实展现烧烤美食背后的独特情感。作为一档接地气的美食节目，《串》旨在展现每一串烧烤的魅力往事，和最真实的美味体验。"
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
            onClick = { _, _, _, _ -> }
        )
    }
}