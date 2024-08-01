package dev.aaa1115910.bv.component.controllers.info

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import androidx.tv.material3.Text
import dev.aaa1115910.bv.util.formatMinSec

@Composable
fun VideoPlayerInfoTip(
    modifier: Modifier = Modifier,
    data: VideoPlayerInfoData
) {
    Surface(
        modifier = modifier
            .padding(8.dp),
        colors = SurfaceDefaults.colors(
            containerColor = Color.Black.copy(alpha = 0.4f)
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(text = "视频时间: ${data.currentTime.formatMinSec()} / ${data.totalDuration.formatMinSec()}")
            Text(text = "缓冲进度: ${data.bufferedPercentage}%")
            Text(text = "分辨率: ${data.resolutionWidth} x ${data.resolutionHeight}")
            Text(text = "视频编码: ${data.codec}")
        }
    }
}

data class VideoPlayerInfoData(
    val totalDuration: Long,
    val currentTime: Long,
    val bufferedPercentage: Int,
    val resolutionWidth: Int,
    val resolutionHeight: Int,
    val codec: String
)