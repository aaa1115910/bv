package dev.aaa1115910.bv.screen.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.tv.foundation.lazy.grid.TvGridCells
import androidx.tv.foundation.lazy.grid.TvGridItemSpan
import androidx.tv.foundation.lazy.grid.TvLazyGridState
import androidx.tv.foundation.lazy.grid.TvLazyVerticalGrid
import androidx.tv.foundation.lazy.grid.itemsIndexed
import coil.compose.AsyncImage
import dev.aaa1115910.biliapi.entity.dynamic.DynamicItem
import dev.aaa1115910.bv.VideoInfoActivity
import dev.aaa1115910.bv.viewmodel.home.DynamicViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun DynamicsScreen(
    modifier: Modifier = Modifier,
    tvLazyGridState: TvLazyGridState,
    dynamicViewModel: DynamicViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    if (dynamicViewModel.isLogin) {

        TvLazyVerticalGrid(
            modifier = modifier,
            columns = TvGridCells.Fixed(4),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(dynamicViewModel.dynamicList) { index, dynamic ->
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    DynamicVideoCard(
                        dynamic = dynamic,
                        onClick = {
                            VideoInfoActivity.actionStart(
                                context,
                                dynamic.modules.moduleDynamic.major!!.archive!!.aid.toInt()
                            )
                        },
                        onFocus = {
                            if (index + 24 > dynamicViewModel.dynamicList.size) {
                                scope.launch(Dispatchers.Default) { dynamicViewModel.loadMore() }
                            }
                        }
                    )
                }
            }
            if (dynamicViewModel.loading)
                item(
                    span = { TvGridItemSpan(4) }
                ) {
                    Text(
                        text = "Loading",
                        color = Color.White
                    )
                }

            if (!dynamicViewModel.hasMore)
                item(
                    span = { TvGridItemSpan(4) }
                ) {
                    Text(
                        text = "没有更多了捏",
                        color = Color.White
                    )
                }
        }
    } else {
        Box(
            modifier = Modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(text = "请先登录")
        }
    }
}

@Composable
fun DynamicVideoCard(
    modifier: Modifier = Modifier,
    dynamic: DynamicItem,
    onClick: () -> Unit = {},
    onFocus: () -> Unit = {}
) {
    var isFocussed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isFocussed) 1f else 0.9f)
    val borderAlpha by animateFloatAsState(if (isFocussed) 1f else 0f)

    LaunchedEffect(isFocussed) {
        if (isFocussed) onFocus()
    }

    Card(
        modifier = modifier
            .scale(scale)
            .onFocusChanged { focusState -> isFocussed = focusState.isFocused }
            .focusable()
            .border(
                width = 2.dp,
                color = Color.White.copy(alpha = borderAlpha),
                shape = MaterialTheme.shapes.medium
            )
            .clickable { onClick() },
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.6f)
                    .clip(MaterialTheme.shapes.large),
                model = dynamic.modules.moduleDynamic.major!!.archive!!.cover,
                contentDescription = null,
                contentScale = ContentScale.FillBounds
            )
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = dynamic.modules.moduleDynamic.major!!.archive!!.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "UP: ${dynamic.modules.moduleAuthor.name}",
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
