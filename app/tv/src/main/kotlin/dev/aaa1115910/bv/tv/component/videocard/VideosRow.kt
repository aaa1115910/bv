package dev.aaa1115910.bv.tv.component.videocard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import dev.aaa1115910.bv.entity.carddata.VideoCardData
import dev.aaa1115910.bv.util.ifElse

@Composable
fun VideosRow(
    modifier: Modifier = Modifier,
    header: String,
    hideShowMore: Boolean = true,
    videos: List<VideoCardData>,
    showMore: () -> Unit,
    onOpenSeasonInfo: (VideoCardData) -> Unit = {},
    onOpenVideoInfo: (VideoCardData) -> Unit = {},
    focusRequester: FocusRequester? = null // 渲染为 播放器-推荐视频 时有值
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val internalFocusRequester = remember { FocusRequester() }
    val activeFocusRequester = focusRequester ?: internalFocusRequester
    var hasFocus by remember { mutableStateOf(false) }
    val titleFontSize by animateFloatAsState(
        targetValue = if (focusRequester != null) 24f else if (hasFocus) 30f else 14f,
        label = "title font size",
        animationSpec = tween(
            durationMillis = 120
        )
    )
    var rowHeight by remember { mutableStateOf(0.dp) }

    Column(
        modifier = modifier
            .onFocusChanged { hasFocus = it.hasFocus }
            .ifElse(focusRequester != null, Modifier.background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.7f)
                    )
                )
            ))
    ) {
        Text(
            modifier = Modifier.padding(start = 32.dp, top = 3.dp, bottom = 3.dp),
            text = header,
            fontSize = titleFontSize.sp
        )
        LazyRow(
            modifier = Modifier
                .padding(vertical = 15.dp)
                .onGloballyPositioned {
                    rowHeight = with(density) {
                        it.size.height.toDp()
                    }
                },
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            contentPadding = PaddingValues(horizontal = 36.dp)
        ) {
            itemsIndexed(items = videos) { index, videoData ->
                SmallVideoCard(
                    modifier = Modifier
                        .width(200.dp)
                        .ifElse(index == 0, Modifier.focusRequester(activeFocusRequester)),
                    data = videoData,
                    onClick = {
                        if (videoData.jumpToSeason) {
                            onOpenSeasonInfo(videoData)
                        } else {
                            onOpenVideoInfo(videoData)
                        }
                    }
                )
            }
            if (!hideShowMore) {
                item {
                    Button(
                        modifier = Modifier.height(rowHeight),
                        shape = ButtonDefaults.shape(shape = MaterialTheme.shapes.large),
                        onClick = showMore
                    ) {
                        Column(
                            modifier = Modifier.fillMaxHeight(),
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text(text = "显示更多")
                        }
                    }
                }
            }
        }
    }
}
