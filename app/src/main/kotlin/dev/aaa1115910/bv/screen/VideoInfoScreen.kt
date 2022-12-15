package dev.aaa1115910.bv.screen

import android.app.Activity
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.TvLazyRow
import androidx.tv.foundation.lazy.list.itemsIndexed
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.BlurTransformation
import dev.aaa1115910.biliapi.BiliApi
import dev.aaa1115910.biliapi.entity.video.Dimension
import dev.aaa1115910.biliapi.entity.video.VideoInfo
import dev.aaa1115910.biliapi.entity.video.VideoPage
import dev.aaa1115910.bv.activities.video.VideoPlayerActivity
import dev.aaa1115910.bv.component.FavoriteButton
import dev.aaa1115910.bv.component.UpIcon
import dev.aaa1115910.bv.component.videocard.VideosRow
import dev.aaa1115910.bv.entity.VideoCardData
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.fException
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.formatPubTimeString
import dev.aaa1115910.bv.util.swapList
import dev.aaa1115910.bv.util.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import java.util.Date


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoInfoScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val intent = (context as Activity).intent

    var videoInfo: VideoInfo? by remember { mutableStateOf(null) }
    val relatedVideos = remember { mutableStateListOf<VideoCardData>() }
    val logger = KotlinLogging.logger { }

    LaunchedEffect(Unit) {
        if (intent.hasExtra("aid")) {
            val aid = intent.getIntExtra("aid", 170001)
            //获取视频信息
            scope.launch(Dispatchers.Default) {
                runCatching {
                    val response = BiliApi.getVideoInfo(av = aid, sessData = Prefs.sessData)
                    videoInfo = response.data
                }.onFailure {
                    withContext(Dispatchers.Main) {
                        "${it.message}".toast(context)
                    }
                    logger.fException(it) { "Get video info failed" }
                }
            }
            //获取相关视频
            scope.launch(Dispatchers.Default) {
                runCatching {
                    val response = BiliApi.getRelatedVideos(avid = aid.toLong())
                    relatedVideos.swapList(response.data.map {
                        VideoCardData(
                            avid = it.aid,
                            title = it.title,
                            cover = it.pic,
                            upName = it.owner.name,
                            time = it.duration * 1000L,
                            play = it.stat.view,
                            danmaku = it.stat.danmaku
                        )
                    })
                }.onFailure {
                    withContext(Dispatchers.Main) {
                        "获取相关视频失败：${it.localizedMessage}".toast(context)
                    }
                    logger.fException(it) { "Get related videos failed" }
                }
            }
        }
    }

    if (videoInfo == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "Loading"
            )
        }
    } else {
        Scaffold(
            containerColor = Color.Black
        ) { innerPadding ->
            Box(
                modifier.padding(innerPadding)
            ) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(videoInfo!!.pic)
                            .transformations(BlurTransformation(LocalContext.current, 20f, 5f))
                            .build()
                    ),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    alpha = 0.6f
                )
                TvLazyColumn(
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        VideoInfoData(
                            videoInfo = videoInfo!!,
                            onClickCover = {
                                logger.fInfo { "Click video cover" }
                                VideoPlayerActivity.actionStart(
                                    context = context,
                                    avid = videoInfo!!.aid,
                                    cid = videoInfo!!.pages.first().cid,
                                    title = videoInfo!!.title,
                                    partTitle = videoInfo!!.pages.first().part
                                )
                            }
                        )
                    }
                    item {
                        VideoDescription(
                            description = videoInfo?.desc ?: "no desc"
                        )
                    }
                    item {
                        VideoPartRow(
                            pages = videoInfo?.pages ?: emptyList(),
                            onClick = { cid ->
                                logger.fInfo { "Click video part: [av:${videoInfo?.aid}, bv:${videoInfo?.bvid}, cid:$cid]" }
                                VideoPlayerActivity.actionStart(
                                    context = context,
                                    avid = videoInfo!!.aid,
                                    cid = cid,
                                    title = videoInfo!!.title,
                                    partTitle = videoInfo!!.pages.find { it.cid == cid }!!.part
                                )
                            }
                        )
                    }
                    item {
                        VideosRow(
                            header = "视频推荐",
                            videos = relatedVideos,
                            showMore = {}
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VideoInfoData(
    modifier: Modifier = Modifier,
    videoInfo: VideoInfo,
    onClickCover: () -> Unit
) {
    val localDensity = LocalDensity.current
    val focusRequester = remember { FocusRequester() }
    var hasFocus by remember { mutableStateOf(false) }
    val borderAlpha by animateFloatAsState(if (hasFocus) 1f else 0f)

    var heightIs by remember { mutableStateOf(0.dp) }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Row(
        modifier = modifier
            .padding(horizontal = 50.dp, vertical = 16.dp),
    ) {
        AsyncImage(
            modifier = Modifier
                .onFocusChanged { hasFocus = it.hasFocus }
                .focusRequester(focusRequester)
                .weight(3f)
                .aspectRatio(1.6f)
                .clip(MaterialTheme.shapes.large)
                .onGloballyPositioned { coordinates ->
                    heightIs = with(localDensity) { coordinates.size.height.toDp() }
                }
                .border(
                    width = 2.dp,
                    color = Color.White.copy(alpha = borderAlpha),
                    shape = MaterialTheme.shapes.large
                )
                .clickable { onClickCover() },
            model = videoInfo.pic,
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier
                .weight(7f)
                .height(heightIs),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = videoInfo.title,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color.White
            )
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    UpIcon(color = Color.White)
                    Text(
                        text = videoInfo.owner.name,
                        color = Color.White
                    )
                }
                Row(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "投稿时间：",
                        color = Color.White
                    )
                    Text(
                        text = Date(videoInfo.ctime.toLong() * 1000).formatPubTimeString(),
                        maxLines = 1,
                        color = Color.White
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    Box(modifier = Modifier.focusable(true)) {}
                    FavoriteButton(
                        isFavorite = false,
                        onClick = {}
                    )
                    Box(modifier = Modifier.focusable(true)) {}
                }
                Text(
                    text = "点赞：${videoInfo.stat.like}",
                    color = Color.White
                )
                Text(
                    text = "投币：${videoInfo.stat.coin}",
                    color = Color.White
                )
                Text(
                    text = "收藏：${videoInfo.stat.favorite}",
                    color = Color.White
                )
            }
            Row {
                Text(
                    text = "标签",
                    color = Color.White
                )
                TvLazyRow {

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
    val titleColor = if (hasFocus) Color.White else Color.Gray
    val titleFontSize by animateFloatAsState(if (hasFocus) 30f else 14f)
    var showDescriptionDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .padding(horizontal = 50.dp),
    ) {
        Text(
            text = "视频简介",
            fontSize = titleFontSize.sp,
            color = titleColor
        )
        Box(
            modifier = Modifier
                .padding(top = 15.dp)
                .onFocusChanged { hasFocus = it.hasFocus }
                .clip(MaterialTheme.shapes.medium)
                .border(
                    width = 2.dp,
                    color = if (hasFocus) Color.White else Color.Transparent,
                    shape = MaterialTheme.shapes.medium
                )
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
    if (show) {
        AlertDialog(
            modifier = modifier,
            onDismissRequest = { onHideDialog() },
            title = {
                Text(
                    text = "视频简介",
                    color = Color.White
                )
            },
            text = {
                TvLazyColumn {
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
fun VideoPartButton(
    modifier: Modifier = Modifier,
    title: String,
    onClick: () -> Unit
) {
    var hasFocus by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .widthIn(max = 200.dp)
            .border(
                width = 2.dp,
                color = if (hasFocus) Color.White else Color.Transparent,
                shape = MaterialTheme.shapes.medium
            )
            .onFocusChanged { hasFocus = it.hasFocus }
            .clickable { onClick() },
        color = MaterialTheme.colorScheme.primary,
        shape = MaterialTheme.shapes.medium,
    ) {
        Text(
            modifier = Modifier
                .padding(8.dp),
            text = title,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun VideoPartRow(
    modifier: Modifier = Modifier,
    pages: List<VideoPage>,
    onClick: (cid: Int) -> Unit
) {
    var hasFocus by remember { mutableStateOf(false) }
    val titleColor = if (hasFocus) Color.White else Color.Gray
    val titleFontSize by animateFloatAsState(if (hasFocus) 30f else 14f)

    Column(
        modifier = modifier
            .padding(start = 50.dp)
            .onFocusChanged { hasFocus = it.hasFocus },
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "视频分 P",
            fontSize = titleFontSize.sp,
            color = titleColor
        )

        TvLazyRow(
            modifier = Modifier
                .padding(top = 15.dp),
            contentPadding = PaddingValues(0.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(items = pages, key = { _, page -> page.cid }) { index, page ->
                VideoPartButton(
                    title = page.part,
                    onClick = { onClick(page.cid) }
                )
            }
        }
    }
}


@Preview
@Composable
fun VideoPartButtonPreview() {
    VideoPartButton(
        title = "这可能是我这辈子距离梅西最近的一次",
        onClick = {}
    )
}

@Preview
@Composable
fun VideoPartRowPreview() {
    val pages = remember { mutableStateListOf<VideoPage>() }
    for (i in 0..10) {
        pages.add(
            VideoPage(
                1000 + i, 0, "", "这可能是我这辈子距离梅西最近的一次p$i", 0,
                "", "", Dimension(0, 0, 0)
            )
        )
    }
    MaterialTheme {
        Surface(
            color = Color(0xFFFF69B4)
        ) {
            VideoPartRow(pages = pages, onClick = {})
        }
    }
}

@Preview
@Composable
fun VideoDescriptionPreview() {
    MaterialTheme {
        Surface(
            color = Color(0xFFFF69B4)
        ) {
            VideoDescription(description = "12435678")
        }
    }
}
