package dev.aaa1115910.bv

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
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
import dev.aaa1115910.bv.component.FavoriteButton
import dev.aaa1115910.bv.component.UpIcon
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import java.util.Date

class VideoInfoActivity : ComponentActivity() {
    companion object {
        fun actionStart(context: Context, aid: Int) {
            context.startActivity(
                Intent(context, VideoInfoActivity::class.java).apply {
                    putExtra("aid", aid)
                }
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BVTheme {
                ProvideTextStyle(TextStyle(color = Color.White)) {
                    VideoInfoScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoInfoScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val intent = (context as Activity).intent

    var videoInfo: VideoInfo? by remember { mutableStateOf(null) }
    val logger = KotlinLogging.logger { }

    LaunchedEffect(Unit) {
        if (intent.hasExtra("aid")) {
            val aid = intent.getIntExtra("aid", 170001)
            scope.launch(Dispatchers.Default) {
                runCatching {
                    val response = BiliApi.getVideoInfo(av = aid, sessData = Prefs.sessData)
                    videoInfo = response.data
                }.onFailure {
                    withContext(Dispatchers.Main) {
                        "${it.message}".toast(context)
                    }
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
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        VideoInfoData(
                            videoInfo = videoInfo!!
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
                                logger.info { "Click video part: [av:${videoInfo?.aid}, bv:${videoInfo?.bvid}, cid:$cid]" }
                                PlayerActivity.actionStart(context, videoInfo!!.aid, cid)
                            }
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
    videoInfo: VideoInfo
) {
    Row(
        modifier = modifier
            .padding(16.dp),
    ) {
        AsyncImage(
            modifier = Modifier
                .width(240.dp)
                .height(150.dp)
                .clip(MaterialTheme.shapes.large),
            model = videoInfo.pic,
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.height(150.dp),
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
                    modifier = Modifier.weight(1f)
                ) {
                    UpIcon(color = Color.White)
                    Text(text = videoInfo.owner.name)
                }
                Row(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "投稿时间：")
                    Text(
                        text = "${Date(videoInfo.ctime.toLong() * 1000)}",
                        maxLines = 1
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
                Text(text = "点赞：${videoInfo.stat.like}")
                Text(text = "投币：${videoInfo.stat.coin}")
                Text(text = "收藏：${videoInfo.stat.favorite}")
            }
            Row {
                Text(text = "标签")
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
    val titleFontSize by animateFloatAsState(if (hasFocus) 18f else 16f)
    var showDescriptionDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .padding(horizontal = 16.dp),
    ) {
        Box(
            modifier = Modifier.height(36.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = "视频简介",
                style = MaterialTheme.typography.titleLarge,
                fontSize = titleFontSize.sp,
                color = Color.White
            )
        }
        Surface(
            modifier = Modifier
                .border(
                    width = 2.dp,
                    color = if (hasFocus) Color.White else Color.Transparent,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(8.dp)
                .onFocusChanged { hasFocus = it.hasFocus }
                .clickable {
                    showDescriptionDialog = true
                },
            color = Color.Transparent
        ) {
            Text(
                text = "${hasFocus}" + description,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }

    ProvideTextStyle(TextStyle(color = Color.Unspecified)) {
        VideoDescriptionDialog(
            show = showDescriptionDialog,
            onHideDialog = { showDescriptionDialog = false },
            description = description
        )
    }
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun VideoPartRow(
    modifier: Modifier = Modifier,
    pages: List<VideoPage>,
    onClick: (cid: Int) -> Unit
) {
    var hasFocus by remember { mutableStateOf(false) }
    val titleFontSize by animateFloatAsState(if (hasFocus) 18f else 16f)

    Column(
        modifier = modifier
            .onFocusChanged { hasFocus = it.hasFocus }
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier.height(36.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = "视频分 P",
                style = MaterialTheme.typography.titleLarge,
                fontSize = titleFontSize.sp,
                color = Color.White
            )
        }
        Row {
            //在 index==0 时再按左键会崩溃，但是运行 preview 就不会出现这个问题，加上这个 Box 可以通过玄学解决
            //但加上这个 Box 后，如果按上键并且在上方没有可供转移焦点的组件，则也会出现一样的崩溃
            Box(modifier = Modifier.focusable(true)) {}

            var isFocusingFirst by remember { mutableStateOf(false) }
            TvLazyRow(
                modifier = modifier,
                contentPadding = PaddingValues(0.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(items = pages, key = { _, page -> page.cid }) { index, page ->
                    val firstModifier = Modifier
                        .onFocusChanged {
                            if (it.hasFocus) {
                                isFocusingFirst = it.hasFocus
                            }
                        }
                        .onKeyEvent {
                            it.key == Key.DirectionLeft
                        }
                    VideoPartButton(
                        modifier = if (index == 0) firstModifier else Modifier,
                        title = page.part,
                        onClick = { onClick(page.cid) }
                    )
                }
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
