package dev.aaa1115910.bv.screen.user

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.foundation.lazy.grid.TvGridCells
import androidx.tv.foundation.lazy.grid.TvLazyVerticalGrid
import androidx.tv.foundation.lazy.grid.itemsIndexed
import dev.aaa1115910.bv.VideoInfoActivity
import dev.aaa1115910.bv.component.videocard.SmallVideoCard
import dev.aaa1115910.bv.viewmodel.user.HistoryViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    historyViewModel: HistoryViewModel = koinViewModel()
) {
    val context = LocalContext.current
    var currentIndex by remember { mutableStateOf(0) }
    val showLargeTitle by remember { derivedStateOf { currentIndex < 4 } }
    val titleFontSize by animateFloatAsState(targetValue = if (showLargeTitle) 48f else 24f)

    LaunchedEffect(Unit) {
        historyViewModel.update()
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            Box(
                modifier = Modifier.padding(start = 48.dp, top = 24.dp, bottom = 8.dp, end = 48.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "历史记录",
                        fontSize = titleFontSize.sp
                    )
                    Text(
                        text = "已加载 ${historyViewModel.histories.size} 条数据",
                        color= Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        }
    ) { innerPadding ->
        if (!historyViewModel.showError) {
            TvLazyVerticalGrid(
                modifier = Modifier.padding(innerPadding),
                columns = TvGridCells.Fixed(4),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                itemsIndexed(historyViewModel.histories) { index, history ->
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        SmallVideoCard(
                            data = history,
                            onClick = { VideoInfoActivity.actionStart(context, history.avid) },
                            onFocus = {
                                currentIndex = index
                                //预加载
                                if (index + 20 > historyViewModel.histories.size) {
                                    historyViewModel.update()
                                }
                            }
                        )
                    }
                }
            }
        } else {
            ErrorResponseScreen(
                error = historyViewModel.error,
                onRetry = {
                    historyViewModel.clearData()
                    historyViewModel.update()
                }
            )
        }
    }
}

@Composable
private fun ErrorResponseScreen(
    modifier: Modifier = Modifier,
    error: Throwable?,
    onRetry: () -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column {
            Text(text = "网络请求失败")
            TextButton(onClick = { onRetry() }) {
                Text(text = "点击重试")
            }
        }
    }
}
