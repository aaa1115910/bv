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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.TvLazyRow
import androidx.tv.foundation.lazy.list.itemsIndexed
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mu.KotlinLogging

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
        lastPlayProgress = seasonData?.userStatus?.progress
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
    focusRequester: FocusRequester,
    cover: String,
    onClick: () -> Unit
) {
    val isPreview = LocalInspectionMode.current

    AsyncImage(
        modifier = modifier
            .focusRequester(focusRequester)
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
    val coverFocusRequester = remember { FocusRequester() }
    Row(
        modifier = modifier
            .padding(horizontal = 50.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SeasonCover(
            cover = cover,
            focusRequester = coverFocusRequester,
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
    index: Int,
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
                    .size(160.dp, 80.dp)
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
                        Text(
                            text = "P$index"
                        )
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
            itemsIndexed(items = episodes, key = { _, episode -> episode.cid }) { index, episode ->
                val episodeTitle by remember { mutableStateOf(if (episode.longTitle != "") episode.longTitle else episode.title) }
                SeasonEpisodeButton(
                    modifier = Modifier
                        .focusedScale(0.9f),
                    index = index + 1,
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

            )
    }
}

@Preview
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