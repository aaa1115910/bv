package dev.aaa1115910.bv.player.tv.controller

import android.graphics.BitmapFactory
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import androidx.tv.material3.Text
import androidx.tv.material3.darkColorScheme
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerPaymentData
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerStateData
import io.github.g0dkar.qrcode.QRCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

@Composable
fun PlayStateTips(
    modifier: Modifier = Modifier
) {
    val videoPlayerStateData = LocalVideoPlayerStateData.current
    val videoPlayerPaymentData = LocalVideoPlayerPaymentData.current

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        if (!videoPlayerStateData.isPlaying && !videoPlayerStateData.isBuffering && !videoPlayerStateData.isError) {
            PauseIcon(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp)
            )
        }
        if (videoPlayerStateData.isBuffering && !videoPlayerStateData.isError) {
            BufferingTip(
                modifier = Modifier
                    .align(Alignment.Center),
                speed = ""
            )
        }
        if (videoPlayerStateData.isError) {
            PlayErrorTip(
                modifier = Modifier.align(Alignment.Center),
                exception = videoPlayerStateData.exception!!
            )
        }
        if (videoPlayerPaymentData.needPay) {
            PaidRequireTip(
                modifier = Modifier.align(Alignment.Center),
                epid = videoPlayerPaymentData.epid
            )
        }
    }
}

@Composable
fun PauseIcon(
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        colors = SurfaceDefaults.colors(
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

@Composable
fun BufferingTip(
    modifier: Modifier = Modifier,
    speed: String
) {
    Surface(
        modifier = modifier,
        colors = SurfaceDefaults.colors(
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

@Composable
fun PlayErrorTip(
    modifier: Modifier = Modifier,
    exception: Exception
) {
    Surface(
        modifier = modifier,
        colors = SurfaceDefaults.colors(
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

@Composable
fun PaidRequireTip(
    modifier: Modifier = Modifier,
    epid: Int,
) {
    val scope = rememberCoroutineScope()
    var qrImage by remember { mutableStateOf<ImageBitmap?>(null) }
    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.IO) {
            val output = ByteArrayOutputStream()
            val url = "https://b23.tv/ep$epid"
            QRCode(url)
                .render(
                    darkColor = android.graphics.Color.WHITE,
                    brightColor = android.graphics.Color.BLACK
                )
                .writeImage(output)
            val input = ByteArrayInputStream(output.toByteArray())
            qrImage = BitmapFactory.decodeStream(input).asImageBitmap()
        }
    }
    Surface(
        modifier = modifier,
        colors = SurfaceDefaults.colors(
            containerColor = Color.Black.copy(0.5f)
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp, 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "请先购买影片",
                style = MaterialTheme.typography.titleLarge
            )
            // TODO 使用颜文字显示影片价格
            Text(text = "(・∀・)つ㊿")
            Spacer(modifier = Modifier.height(12.dp))
            AnimatedVisibility(visible = qrImage != null) {
                Image(bitmap = qrImage!!, contentDescription = "EP$epid QR Code")
            }
        }
    }
}

@Preview
@Composable
private fun PauseIconPreview() {
    MaterialTheme(
        colorScheme = darkColorScheme()
    ) {
        Box(modifier = Modifier.padding(10.dp)) {
            PauseIcon()
        }
    }
}

@Preview
@Composable
private fun BufferingTipPreview() {
    MaterialTheme(
        colorScheme = darkColorScheme()
    ) {
        BufferingTip(
            modifier = Modifier.padding(10.dp),
            speed = ""
        )
    }
}

@Preview
@Composable
private fun PlayErrorTipPreview() {
    MaterialTheme(
        colorScheme = darkColorScheme()
    ) {
        PlayErrorTip(exception = Exception("This is a test exception."))
    }
}

@Preview
@Composable
private fun PaidRequireTipPreview() {
    MaterialTheme(
        colorScheme = darkColorScheme()
    ) {
        PaidRequireTip(epid = 752900)
    }
}