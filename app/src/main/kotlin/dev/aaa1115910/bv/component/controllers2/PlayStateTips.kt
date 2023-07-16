package dev.aaa1115910.bv.component.controllers2

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.NonInteractiveSurfaceDefaults
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import dev.aaa1115910.bv.ui.theme.BVTheme

@Composable
fun PlayStateTips(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
    isBuffering: Boolean,
    isError: Boolean,
    exception: Exception? = null
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        if (!isPlaying && !isBuffering && !isError) {
            PauseIcon(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp)
            )
        }
        if (isBuffering && !isError) {
            BufferingTip(
                modifier = Modifier
                    .align(Alignment.Center),
                speed = ""
            )
        }
        if (isError) {
            PlayErrorTip(
                modifier = Modifier.align(Alignment.Center),
                exception = exception!!
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun PauseIcon(
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        colors = NonInteractiveSurfaceDefaults.colors(
            containerColor = Color.Black.copy(0.5f)
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Icon(
            modifier = Modifier
                .padding(12.dp, 4.dp)
                .size(50.dp),
            imageVector = Icons.Rounded.Pause,
            contentDescription = null,
            tint = Color.White
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun BufferingTip(
    modifier: Modifier = Modifier,
    speed: String
) {
    Surface(
        modifier = modifier,
        colors = NonInteractiveSurfaceDefaults.colors(
            containerColor = Color.Black.copy(0.5f)
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(16.dp, 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(36.dp)
                    .padding(8.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
            Text(
                modifier = Modifier,
                text = "缓冲中...$speed",
                fontSize = 24.sp
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun PlayErrorTip(
    modifier: Modifier = Modifier,
    exception: Exception
) {
    Surface(
        modifier = modifier,
        colors = NonInteractiveSurfaceDefaults.colors(
            containerColor = Color.Black.copy(0.5f)
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp, 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "播放器正在抽风",
                style = MaterialTheme.typography.titleLarge
            )
            Text(text = " _(:з」∠)_")
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "错误信息：${exception.message}")
        }
    }
}

@Preview
@Composable
private fun PauseIconPreview() {
    BVTheme {
        Box(modifier = Modifier.padding(10.dp)) {
            PauseIcon()
        }
    }
}

@Preview
@Composable
private fun BufferingTipPreview() {
    BVTheme {
        BufferingTip(
            modifier = Modifier.padding(10.dp),
            speed = ""
        )
    }
}

@Preview
@Composable
private fun PlayErrorTipPreview() {
    BVTheme {
        PlayErrorTip(exception = Exception("This is a test exception."))
    }
}