package dev.aaa1115910.bv.component.controllers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.items
import dev.aaa1115910.bv.repository.VideoListItem
import dev.aaa1115910.bv.util.requestFocus

@Composable
fun VideoListController(
    modifier: Modifier = Modifier,
    onVideoSwitch: (VideoListItem) -> Unit
) {
    val scope = rememberCoroutineScope()
    val videoPlayerControllerData = LocalVideoPlayerControllerData.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus(scope)
    }

    Surface(
        modifier = modifier,
        color = Color.Black.copy(alpha = 0.5f)
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .width(200.dp)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            TvLazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 120.dp)
            ) {
                items(items = videoPlayerControllerData.availableVideoList) { video ->
                    val isSelected = video.cid == videoPlayerControllerData.currentVideoCid
                    val itemModifier = if (isSelected) {
                        Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                    } else {
                        Modifier.fillMaxWidth()
                    }
                    MenuListItem(
                        modifier = itemModifier,
                        text = "P${video.index} ${video.title}",
                        selected = isSelected
                    ) { onVideoSwitch(video) }
                }
            }
        }
    }
}