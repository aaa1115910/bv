package dev.aaa1115910.bv.screen.home.anime

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.itemsIndexed
import dev.aaa1115910.biliapi.BiliApi
import dev.aaa1115910.biliapi.entity.video.Timeline
import dev.aaa1115910.biliapi.entity.video.TimelineType
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.activities.video.SeasonInfoActivity
import dev.aaa1115910.bv.component.videocard.SeasonCard
import dev.aaa1115910.bv.entity.carddata.SeasonCardData
import dev.aaa1115910.bv.util.ImageSize
import dev.aaa1115910.bv.util.resizedImageUrl
import dev.aaa1115910.bv.util.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimeTimelineScreen(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var currentTimelineIndex by remember { mutableIntStateOf(0) }
    var currentEpisodeIndex by remember { mutableIntStateOf(0) }
    val showLargeTitle by remember {
        derivedStateOf {
            currentTimelineIndex == 0 && currentEpisodeIndex < 1
        }
    }
    val titleFontSize by animateFloatAsState(targetValue = if (showLargeTitle) 48f else 24f)

    val defaultFocusRequester = remember { FocusRequester() }
    val timelines = remember { mutableStateListOf<Timeline>() }

    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.Default) {
            runCatching {
                timelines.addAll(
                    BiliApi.getTimeline(
                        type = TimelineType.Anime,
                        before = 0,
                        after = 7
                    ).getResponseData()
                )
                delay(200)
                defaultFocusRequester.requestFocus()
            }.onFailure {
                withContext(Dispatchers.Main) {
                    "获取放送时间表失败: ${it.message}".toast(context)
                }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            Box(
                modifier = Modifier.padding(start = 48.dp, top = 24.dp, bottom = 8.dp, end = 48.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.title_activity_anime_timeline),
                    fontSize = titleFontSize.sp,
                )
            }
        }
    ) { innerPadding ->
        TvLazyColumn(
            modifier = Modifier.padding(innerPadding),
            contentPadding = PaddingValues(bottom = 48.dp, start = 48.dp, end = 48.dp)
        ) {
            itemsIndexed(items = timelines) { index, timeline ->
                val defaultModifier = if (timeline.isToday) {
                    Modifier.focusRequester(defaultFocusRequester)
                } else {
                    Modifier
                }
                TimelinePerDay(
                    modifier = defaultModifier,
                    timeline = timeline,
                    onFocusChange = { episodeIndex ->
                        currentTimelineIndex = index
                        currentEpisodeIndex = episodeIndex
                    },
                    onClick = { seasonId ->
                        SeasonInfoActivity.actionStart(
                            context = context,
                            seasonId = seasonId
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun TimelinePerDay(
    modifier: Modifier = Modifier,
    timeline: Timeline,
    onFocusChange: (index: Int) -> Unit = {},
    onClick: (seasonId: Int) -> Unit = {},
) {
    var hasFocus by remember { mutableStateOf(false) }
    val titleColor = if (hasFocus) Color.White else Color.White.copy(alpha = 0.6f)
    val titleFontSize by animateFloatAsState(if (hasFocus) 30f else 20f)
    val episodeChunkedList = timeline.episodes.chunked(5)

    val getWeekString: (Int) -> String = { dayOfWeek ->
        when (dayOfWeek) {
            1 -> "周一"
            2 -> "周二"
            3 -> "周三"
            4 -> "周四"
            5 -> "周五"
            6 -> "周六"
            7 -> "周日"
            else -> "未知"
        }
    }

    Column(
        modifier = modifier
            .padding(top = 24.dp)
            .onFocusChanged { hasFocus = it.hasFocus },
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "${timeline.date} ${getWeekString(timeline.dayOfWeek)}",
            fontSize = titleFontSize.sp,
            color = titleColor
        )

        episodeChunkedList.forEachIndexed { index, episodes ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                episodes.forEach { episode ->
                    SeasonCard(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        data = SeasonCardData(
                            title = episode.title,
                            cover = episode.cover.resizedImageUrl(ImageSize.SeasonCoverThumbnail),
                            seasonId = episode.seasonId,
                            rating = null
                        ),
                        onFocus = { onFocusChange(index) },
                        onClick = { onClick(episode.seasonId) }
                    )
                }
                repeat(5 - episodes.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}