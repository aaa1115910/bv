package dev.aaa1115910.bv.mobile.component.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
import dev.aaa1115910.biliapi.entity.video.Dimension
import dev.aaa1115910.biliapi.entity.video.VideoPage
import dev.aaa1115910.biliapi.entity.video.season.Episode
import dev.aaa1115910.biliapi.entity.video.season.Section
import dev.aaa1115910.biliapi.entity.video.season.UgcSeason
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.mobile.theme.BVMobileTheme
import dev.aaa1115910.bv.util.formatHourMinSec

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoPlayerPages(
    modifier: Modifier = Modifier,
    currentCid: Long,
    pages: List<VideoPage>,
    ugcSeason: UgcSeason?,
    pgcSections: List<Section>,
    onClickPage: (VideoPage) -> Unit,
    onClickEpisode: (sectionIndex: Int, episode: Episode) -> Unit
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { sheetValue ->
            println("confirmValueChange: $sheetValue")
            true
        }
    )
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }

    var currentSection by remember { mutableStateOf<Section?>(null) }

    LaunchedEffect(currentCid) {
        if (pgcSections.isNotEmpty()) {
            currentSection =
                pgcSections.find { it.episodes.any { episode -> episode.cid == currentCid } }
        } else if (ugcSeason != null) {
            currentSection = ugcSeason.sections.find {
                it.episodes.any { episode ->
                    episode.cid == currentCid || episode.pages.any { page -> page.cid == currentCid }
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        if (pgcSections.isNotEmpty()) {
            // TODO pgc
        } else if (ugcSeason != null) {
            // TODO ugc
            if (currentSection != null) {
                //VideoPlayerUgcSectionsFilter(
                //    sections = ugcSeason.sections,
                //    currentSection = currentSection!!,
                //    onSectionChange = { currentSection = it }
                //)
                VideoPlayerEpisodesRow(
                    //title = currentSection!!.title,
                    episodes = currentSection!!.episodes,
                    onClickMore = { openBottomSheet = !openBottomSheet },
                    onClickEpisode = { episode ->
                        onClickEpisode(
                            ugcSeason.sections.indexOf(currentSection), episode
                        )
                    },
                    currentCid = currentCid
                )
            }
        } else if (pages.size > 1) {
            VideoPlayerPagesRow(
                //title = "视频分 P",
                pages = pages,
                onClickMore = { openBottomSheet = !openBottomSheet },
                onClickPage = onClickPage,
                currentCid = currentCid
            )
        }
    }


    if (openBottomSheet) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { openBottomSheet = false },
            contentWindowInsets = { WindowInsets(0, 0, 0, 0) }
        ) {
            VideoPlayerPartSheetContent(
                currentCid = currentCid,
                pages = pages,
                ugcSeason = ugcSeason,
                pgcSections = pgcSections,
                onClickPage = onClickPage,
                onClickEpisode = { episode ->
                    onClickEpisode(
                        ugcSeason!!.sections.indexOf(currentSection), episode
                    )
                }
            )
        }
    }
}

@Composable
private fun VideoPlayerUgcSectionsFilter(
    modifier: Modifier = Modifier,
    sections: List<Section>,
    currentSection: Section,
    onSectionChange: (Section) -> Unit = {}
) {
    LazyRow {
        items(sections) { section ->
            VideoPlayerUgcSectionsFilterChip(
                modifier = modifier,
                section = section,
                selected = section == currentSection,
                onClick = { onSectionChange(section) }
            )
        }
    }
}

@Composable
fun VideoPlayerUgcSectionsFilterChip(
    modifier: Modifier = Modifier,
    section: Section,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        modifier = modifier,
        onClick = onClick,
        label = {
            Text(
                text = section.title,
                style = MaterialTheme.typography.titleSmall
            )
        },
        selected = selected,
        leadingIcon = (@Composable {
            Icon(
                imageVector = Icons.Filled.Done,
                contentDescription = null,
                modifier = Modifier.size(FilterChipDefaults.IconSize)
            )
        }).takeIf { selected }
    )
}

@Composable
fun VideoPlayerEpisodesRow(
    modifier: Modifier = Modifier,
    title: String? = null,
    episodes: List<Episode>,
    currentCid: Long,
    onClickMore: () -> Unit = {},
    onClickEpisode: (Episode) -> Unit = {}
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
    ) {
        if (title != null) {
            Text(
                modifier = Modifier.padding(horizontal = 8.dp),
                text = title,
                style = MaterialTheme.typography.titleSmall
            )
        }
        Box {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(
                    start = 8.dp,
                    end = 68.dp,
                    top = 8.dp,
                    bottom = 8.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(episodes) { index, episode ->
                    VideoPlayerPageItem(
                        modifier = modifier,
                        text = "EP${index + 1} ${episode.title}",
                        onClick = { onClickEpisode(episode) },
                        isPlaying = episode.cid == currentCid
                    )
                }
            }
            MoreButton(
                modifier = Modifier
                    .align(Alignment.CenterEnd),
                onClick = onClickMore
            )
        }
    }
}

@Composable
fun VideoPlayerPagesRow(
    modifier: Modifier = Modifier,
    title: String? = null,
    pages: List<VideoPage>,
    currentCid: Long,
    onClickMore: () -> Unit = {},
    onClickPage: (VideoPage) -> Unit = {}
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
    ) {
        if (title != null) {
            Text(
                modifier = Modifier.padding(horizontal = 8.dp),
                text = title,
                style = MaterialTheme.typography.titleSmall
            )
        }
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            LazyRow(
                contentPadding = PaddingValues(
                    start = 8.dp,
                    end = 68.dp,
                    top = 8.dp,
                    bottom = 8.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(pages) { index, page ->
                    VideoPlayerPageItem(
                        modifier = modifier,
                        text = "P${index + 1} ${page.title}",
                        onClick = { onClickPage(page) },
                        isPlaying = page.cid == currentCid
                    )
                }
            }
            MoreButton(
                modifier = Modifier
                    .align(Alignment.CenterEnd),
                onClick = onClickMore
            )
        }
    }
}

@Composable
private fun MoreButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    var color by remember { mutableStateOf(Color.Red) }
    color = MaterialTheme.colorScheme.surface
    val colorStops = arrayOf(
        0.0f to Color.Transparent,
        0.4f to MaterialTheme.colorScheme.surface,
        1f to MaterialTheme.colorScheme.surface
    )
    Box(
        modifier = modifier
            .width(60.dp)
            .height(80.dp)
            .background(Brush.horizontalGradient(colorStops = colorStops)),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = 8.dp),
            onClick = onClick
        ) {
            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null)
        }
    }
}

@Composable
private fun VideoPlayerPageItem(
    modifier: Modifier = Modifier,
    text: String,
    isPlaying: Boolean,
    onClick: () -> Unit
) {
    val density = LocalDensity.current
    val inlineContentMap = mapOf(
        "playingIcon" to InlineTextContent(
            Placeholder(
                width = with(density) { 20.dp.toSp() },
                height = with(density) { 20.dp.toSp() },
                placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
            )
        ) {
            PlayingIcon()
        }
    )
    val annotatedString = buildAnnotatedString {
        if (isPlaying) appendInlineContent("playingIcon")
        append(text)
    }
    Box(
        modifier = modifier
            .width(160.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable { onClick() }
    ) {
        Text(
            modifier = Modifier.padding(8.dp),
            text = annotatedString,
            maxLines = 2,
            minLines = 2,
            overflow = TextOverflow.Ellipsis,
            inlineContent = inlineContentMap,
            color = if (isPlaying) MaterialTheme.colorScheme.primary else Color.Unspecified
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VideoPlayerPartSheetContent(
    modifier: Modifier = Modifier,
    currentCid: Long,
    pages: List<VideoPage>,
    ugcSeason: UgcSeason?,
    pgcSections: List<Section>,
    onClickPage: (VideoPage) -> Unit,
    onClickEpisode: (Episode) -> Unit
) {
    var currentSection by remember { mutableStateOf(ugcSeason?.sections?.first()) }

    val onClickSectionTab: (Section) -> Unit = { section ->
        currentSection = section
    }

    LaunchedEffect(currentCid) {
        if (pgcSections.isNotEmpty()) {
            currentSection =
                pgcSections.find { it.episodes.any { episode -> episode.cid == currentCid } }
        } else if (ugcSeason != null) {
            currentSection = ugcSeason.sections.find {
                it.episodes.any { episode ->
                    episode.cid == currentCid || episode.pages.any { page -> page.cid == currentCid }
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Row {
            TopAppBar(
                title = {
                    Text(
                        text = if (pgcSections.isNotEmpty()) {
                            "视频选集"
                        } else if (ugcSeason != null) {
                            "视频选集"
                        } else {
                            "视频分 P"
                        },
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                ),
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        }
        //Text("ugcSeason: $ugcSeason")
        if (pgcSections.isNotEmpty()) {
            // TODO pgc
            Text("pgc")
        } else if (ugcSeason != null) {
            // TODO ugc
            if (currentSection != null) {
                if (ugcSeason.sections.size > 1) {
                    SecondaryScrollableTabRow(
                        selectedTabIndex = ugcSeason.sections.indexOf(currentSection!!),
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        divider = {}
                    ) {
                        ugcSeason.sections.forEach { section ->
                            Tab(
                                selected = currentSection == section,
                                onClick = { onClickSectionTab(section) }
                            ) {
                                Box(
                                    modifier = Modifier.height(48.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        text = section.title,
                                        style = MaterialTheme.typography.bodyLarge,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
                HorizontalDivider()
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    itemsIndexed(currentSection!!.episodes) { epIndex, episode ->
                        if (episode.pages.size <= 1) {
                            PageListItem(
                                modifier = modifier,
                                text = "EP${epIndex + 1} ${episode.title}",
                                duration = episode.duration,
                                isPlaying = episode.cid == currentCid,
                                onClick = { onClickEpisode(episode) }
                            )
                        } else {
                            Column {
                                var expand by remember { mutableStateOf(true) }
                                LaunchedEffect(currentSection) { expand = true }
                                PageListItem(
                                    modifier = modifier,
                                    text = "EP${epIndex + 1} ${episode.title}",
                                    duration = null,
                                    isPlaying = episode.pages.any { it.cid == currentCid },
                                    onClick = { expand = !expand }
                                )
                                AnimatedVisibility(
                                    visible = expand
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .padding(start = 16.dp)
                                    ) {
                                        episode.pages.forEachIndexed { pageIndex, page ->
                                            PageListItem(
                                                modifier = modifier,
                                                text = "P${pageIndex + 1} ${page.title}",
                                                duration = page.duration,
                                                isPlaying = page.cid == currentCid,
                                                onClick = { onClickPage(page) }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.navigationBarsPadding()) }
                }
            }
        } else if (pages.size > 1) {
            HorizontalDivider()
            LazyColumn {
                itemsIndexed(pages) { index, page ->
                    PageListItem(
                        modifier = modifier,
                        text = "P${index + 1} ${page.title}",
                        duration = page.duration,
                        isPlaying = page.cid == currentCid,
                        onClick = { onClickPage(page) }
                    )
                }
                item { Spacer(modifier = Modifier.navigationBarsPadding()) }
            }
        }
    }
}


@Composable
private fun PageListItem(
    modifier: Modifier = Modifier,
    text: String,
    duration: Int?,
    isPlaying: Boolean,
    onClick: () -> Unit = {}
) {
    val density = LocalDensity.current
    val inlineContentMap = mapOf(
        "playingIcon" to InlineTextContent(
            Placeholder(
                width = with(density) { 20.dp.toSp() },
                height = with(density) { 20.dp.toSp() },
                placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
            )
        ) {
            PlayingIcon()
        }
    )
    val annotatedString = buildAnnotatedString {
        if (isPlaying) appendInlineContent("playingIcon")
        append(text)
    }
    ListItem(
        modifier = modifier
            .height(40.dp)
            .clip(MaterialTheme.shapes.medium)
            .clickable { onClick() },
        headlineContent = {
            Text(
                text = annotatedString,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                inlineContent = inlineContentMap,
            )
        },
        trailingContent = (@Composable {
            Text(
                text = (1000 * (duration?.toLong() ?: 0)).formatHourMinSec(),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            )
        }).takeIf { duration != null },
        colors = ListItemDefaults.colors(
            headlineColor = if (isPlaying) MaterialTheme.colorScheme.primary else Color.Unspecified,
            containerColor = Color.Transparent
        ),
    )
}

@Composable
private fun PlayingIcon(modifier: Modifier = Modifier) {
    val dynamicProperties = rememberLottieDynamicProperties(
        rememberLottieDynamicProperty(
            property = LottieProperty.COLOR_FILTER,
            value = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                MaterialTheme.colorScheme.primary.hashCode(),
                BlendModeCompat.SRC_ATOP
            ),
            keyPath = arrayOf(
                "**"
            )
        )
    )

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.ic_playing)
    )
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

    LottieAnimation(
        modifier = Modifier
            .size(20.dp)
            .scale(2f),
        composition = composition,
        progress = { progress },
        dynamicProperties = dynamicProperties,
        clipTextToBoundingBox = true
    )
}

@Preview
@Composable
private fun VideoPlayerPageWithoutTitlePreview() {
    BVMobileTheme {
        VideoPlayerPagesRow(
            pages = List(10) {
                VideoPage(
                    cid = it.toLong(),
                    index = it,
                    title = "Page title $it",
                    duration = 1,
                    dimension = Dimension(0, 0)
                )
            },
            currentCid = 0
        )
    }
}

@Preview
@Composable
private fun VideoPlayerPageWithTitlePreview() {
    BVMobileTheme {
        VideoPlayerPagesRow(
            title = "Title",
            pages = List(10) { VideoPage(it.toLong(), it, "Title", 1, Dimension(0, 0)) },
            currentCid = 0
        )
    }
}

@Preview
@Composable
private fun PlayingIconPreview() {
    PlayingIcon()
}

@Preview
@Composable
private fun PageListPreview() {
    BVMobileTheme {
        Surface {
            Column {
                repeat(10) {
                    PageListItem(
                        isPlaying = it == 0,
                        duration = 233,
                        text = "This is  a page list item title"
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun VideoPlayerPartSheetContentPagesPreview() {
    val pages = List(10) {
        VideoPage(
            cid = it.toLong(),
            index = it,
            title = "Page title $it",
            duration = 1,
            dimension = Dimension(0, 0)
        )
    }
    BVMobileTheme {
        VideoPlayerPartSheetContent(
            currentCid = 1,
            pages = pages,
            ugcSeason = null,
            pgcSections = emptyList(),
            onClickPage = {},
            onClickEpisode = {}
        )
    }
}

@Preview
@Composable
private fun VideoPlayerPartSheetContentUgcSeasonPreview() {
    val ugcSeason by remember {
        mutableStateOf(UgcSeason(
            id = 0,
            title = "Ugc Season Title",
            cover = "",
            sections = List(3) { sectionIndex ->
                Section(
                    id = sectionIndex.toLong(),
                    title = "Section $sectionIndex",
                    episodes = List(10) { episodeIndex ->
                        Episode(
                            id = episodeIndex,
                            cid = episodeIndex.toLong(),
                            title = "Section $sectionIndex Episode $episodeIndex",
                            aid = episodeIndex.toLong(),
                            bvid = "",
                            longTitle = "Episode long title $episodeIndex",
                            cover = "",
                            duration = 111,
                            dimension = Dimension(0, 0),
                            pages = if (episodeIndex == 3) {
                                List(10) { pageIndex ->
                                    VideoPage(
                                        cid = 100 + pageIndex.toLong(),
                                        index = pageIndex,
                                        title = "Pages in sections $pageIndex",
                                        duration = 100,
                                        dimension = Dimension(0, 0)
                                    )
                                }
                            } else {
                                emptyList()
                            }
                        )
                    }
                )
            }
        ))
    }
    BVMobileTheme {
        VideoPlayerPartSheetContent(
            currentCid = 102,
            pages = emptyList(),
            ugcSeason = ugcSeason,
            pgcSections = emptyList(),
            onClickPage = {},
            onClickEpisode = {}
        )
    }
}

@Preview
@Composable
private fun VideoPlayerPartSheetContentPgcSectionsPreview() {
    val pages = List(10) {
        VideoPage(
            cid = it.toLong(),
            index = it,
            title = "Page title $it",
            duration = 1,
            dimension = Dimension(0, 0)
        )
    }
    BVMobileTheme {
        VideoPlayerPartSheetContent(
            currentCid = 1,
            pages = pages,
            ugcSeason = null,
            pgcSections = emptyList(),
            onClickPage = {},
            onClickEpisode = {}
        )
    }
}