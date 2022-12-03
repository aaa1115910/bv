package dev.aaa1115910.bv.screen.user

import android.content.Intent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.foundation.lazy.list.TvLazyColumn
import coil.compose.AsyncImage
import dev.aaa1115910.biliapi.BiliApi
import dev.aaa1115910.bv.HistoryActivity
import dev.aaa1115910.bv.component.videocard.VideosRow
import dev.aaa1115910.bv.entity.VideoCardData
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.toast
import dev.aaa1115910.bv.viewmodel.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserInfoScreen(
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }
    var showLargeTitle by remember { mutableStateOf(true) }

    val titleFontSize by animateFloatAsState(targetValue = if (showLargeTitle) 48f else 24f)
    val title by remember {
        mutableStateOf(
            listOf(
                "吾",
                "秘密基地",
                "熟悉的地方",
                "你来啦",
                "I Need More Power!!!",
                "别看了",
                "我的",
                "BUG 满天飞 ~",
                "你说得对，但是"
            ).random()
        )
    }

    val histories = remember { mutableStateListOf<VideoCardData>() }
    val anime = remember { mutableStateListOf<VideoCardData>() }
    val favorites = remember { mutableStateListOf<VideoCardData>() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()

        //update histories
        scope.launch(Dispatchers.Default) {
            runCatching {
                val responseData = BiliApi.getHistories(sessData = Prefs.sessData).getResponseData()
                responseData.list.forEach { historyItem ->
                    if (historyItem.history.business != "archive") return@forEach
                    histories.add(
                        VideoCardData(
                            avid = historyItem.history.oid,
                            title = historyItem.title,
                            cover = historyItem.cover,
                            upName = historyItem.authorName,
                            time = historyItem.duration.toLong()
                        )
                    )
                }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            Box(
                modifier = Modifier.padding(start = 48.dp, top = 24.dp, bottom = 8.dp)
            ) {
                Text(
                    text = title,
                    fontSize = titleFontSize.sp
                )
            }

        }
    ) { innerPadding ->
        TvLazyColumn(
            modifier = Modifier.padding(innerPadding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                UserInfo(
                    modifier = Modifier
                        .focusRequester(focusRequester),
                    face = userViewModel.face,
                    username = userViewModel.username,
                    onFocusChange = { hasFocus ->
                        //当焦点在此项时，显示大标题
                        showLargeTitle = hasFocus
                    }
                )
            }
            item {
                RecentVideosRow(
                    videos = histories,
                    showMore = {
                        context.startActivity(Intent(context, HistoryActivity::class.java))
                    }
                )
            }
            item {
                AnimeVideosRow(
                    showMore = {
                        "还没写呢！！！".toast(context)
                    }
                )
            }
            item {
                FavoriteVideosRow(
                    showMore = {
                        "还没写呢！！！".toast(context)
                    }
                )
            }
        }
    }
}

@Composable
private fun UserInfo(
    modifier: Modifier = Modifier,
    face: String,
    username: String,
    onFocusChange: (hasFocus: Boolean) -> Unit
) {
    var hasFocus by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .onFocusChanged {
                hasFocus = it.isFocused
                onFocusChange(it.hasFocus)
            }
            .padding(horizontal = 50.dp, vertical = 28.dp)
            .size(400.dp, 140.dp)
            .focusable()
            .border(
                width = 2.dp,
                color = if (hasFocus) Color.White else Color.Transparent,
                shape = MaterialTheme.shapes.large
            ),
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .padding(start = 24.dp, end = 8.dp)
                    .size(80.dp)
                    .clip(CircleShape),
                color = Color.White
            ) {
                AsyncImage(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape),
                    model = face,
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = 6.dp,
                        top = 24.dp,
                        end = 24.dp,
                        bottom = 24.dp
                    ),
            ) {
                val startPaddingValue = 6.dp
                Text(
                    modifier = Modifier.padding(start = startPaddingValue),
                    text = username,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    modifier = Modifier.padding(start = startPaddingValue),
                    text = "xxxxx"
                )
                Slider(
                    enabled = false,
                    value = 0.8f,
                    onValueChange = {},
                    colors = SliderDefaults.colors(
                        disabledThumbColor = Color.Transparent
                    )
                )
            }
        }
    }
}

@Composable
private fun RecentVideosRow(
    modifier: Modifier = Modifier,
    videos: List<VideoCardData>,
    showMore: () -> Unit
) {
    VideosRow(
        modifier = modifier
            .padding(vertical = 8.dp),
        header = "最近播放记录",
        hideShowMore = false,
        showMore = showMore,
        videos = videos
    )
}

@Composable
private fun AnimeVideosRow(
    modifier: Modifier = Modifier,
    showMore: () -> Unit
) {
    VideosRow(
        modifier = modifier
            .padding(vertical = 8.dp),
        header = "我追的番",
        hideShowMore = false,
        showMore = showMore,
        videos = listOf()
    )
}

@Composable
private fun FavoriteVideosRow(
    modifier: Modifier = Modifier,
    showMore: () -> Unit
) {
    VideosRow(
        modifier = modifier
            .padding(vertical = 8.dp),
        header = "私人藏品",
        hideShowMore = false,
        showMore = showMore,
        videos = listOf()
    )
}