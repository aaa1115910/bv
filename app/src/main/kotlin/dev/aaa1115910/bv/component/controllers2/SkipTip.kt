package dev.aaa1115910.bv.component.controllers2

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import dev.aaa1115910.bv.component.SurfaceWithoutClickable


// TODO 跳转历史记录
@Composable
fun BackToHistoryTip() {

}


// TODO 跳过片头
@Composable
fun SkipOpTip(
    modifier: Modifier = Modifier,
    show: Boolean,
    onConfirm: () -> Unit
) {
    SkipTip(
        modifier = modifier,
        show = show,
        text = "跳过片头",
        onConfirm = onConfirm
    )
}

// TODO 跳过片尾
@Composable
fun SkipEdTip(
    modifier: Modifier = Modifier,
    show: Boolean,
    onConfirm: () -> Unit
) {
    SkipTip(
        modifier = modifier,
        show = show,
        text = "跳过片尾",
        onConfirm = onConfirm
    )
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SkipTip(
    modifier: Modifier = Modifier,
    show: Boolean,
    text: String,
    onConfirm: () -> Unit
) {
    AnimatedVisibility(
        visible = show,
        enter = expandHorizontally(),
        exit = shrinkHorizontally()
    ) {
        Box(
            modifier = modifier.fillMaxSize()
        ) {
            SurfaceWithoutClickable(
                modifier = modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = 32.dp)
                    .clickable { onConfirm() },
                color = Color.Black.copy(alpha = 0.6f),
                shape = MaterialTheme.shapes.medium.copy(
                    topStart = CornerSize(0.dp), bottomStart = CornerSize(0.dp)
                )
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = text,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}