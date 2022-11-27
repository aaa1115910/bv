package dev.aaa1115910.bv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.foundation.lazy.grid.TvGridCells
import androidx.tv.foundation.lazy.grid.TvGridItemSpan
import androidx.tv.foundation.lazy.grid.TvLazyVerticalGrid
import androidx.tv.foundation.lazy.grid.items
import androidx.tv.foundation.lazy.list.TvLazyRow
import coil.compose.AsyncImage
import dev.aaa1115910.biliapi.BiliApi
import dev.aaa1115910.biliapi.entity.video.VideoInfo
import dev.aaa1115910.bv.component.HomeCarousel
import dev.aaa1115910.bv.component.TopNav
import dev.aaa1115910.bv.ui.theme.BVTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BVTheme {
                BvApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BvApp() {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var data = remember { mutableStateListOf<VideoInfo>() }
    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.Default) {
            data.clear()
            data.addAll(BiliApi.getPopularVideoData().data.list)
        }
    }
    Scaffold(
        topBar = {
            TopNav()
        },
        containerColor = Color.DarkGray
    ) { innerPadding ->
        TvLazyVerticalGrid(
            modifier = Modifier.padding(innerPadding),
            columns = TvGridCells.Adaptive(200.dp),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(
                span = { TvGridItemSpan(4) }
            ) {
                HomeCarousel()
            }
            items(data.toList()) { video ->
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    VideoCard(
                        video = video,
                        onClick = {
                            VideoInfoActivity.actionStart(context, video.aid)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun VideosRow(
    header: String,
    modifier: Modifier = Modifier,
    itemCount: Int = 5
) {
    var hasFocus by remember { mutableStateOf(false) }
    val titleColor = if (hasFocus) Color.White else Color.Gray
    val titleFontSize by animateFloatAsState(if (hasFocus) 30f else 14f)

    Column(
        modifier = modifier
            .padding(start = 50.dp)
            .onFocusChanged { hasFocus = it.hasFocus }
    ) {
        Text(
            text = header,
            fontSize = titleFontSize.sp,
            color = titleColor
        )
        TvLazyRow(
            horizontalArrangement = Arrangement.spacedBy(25.dp),
            contentPadding = PaddingValues(end = 50.dp),
            modifier = Modifier
                .padding(top = 15.dp)
        ) {
            for (i in 0..5) {
                //item { VideoCard(title = "$i") }
            }
        }
    }
}

@Composable
fun VideoCard(
    modifier: Modifier = Modifier,
    video: VideoInfo,
    onClick: () -> Unit = {}
) {
    var isFocussed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isFocussed) 1f else 0.9f)

    Card(
        modifier = modifier
            .width(240.dp)
            .height(200.dp)
            .scale(scale)
            .onFocusChanged { focusState -> isFocussed = focusState.isFocused }
            .focusable()
            .clickable { onClick() },
        shape = MaterialTheme.shapes.large
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            AsyncImage(
                modifier = Modifier
                    .width(240.dp)
                    .height(150.dp)
                    .clip(MaterialTheme.shapes.large),
                model = video.pic,
                contentDescription = null,
                contentScale = ContentScale.FillBounds
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp),

                ) {
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "UP: ${video.owner.name}",
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

