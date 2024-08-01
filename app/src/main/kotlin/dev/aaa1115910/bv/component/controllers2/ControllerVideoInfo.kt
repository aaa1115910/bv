package dev.aaa1115910.bv.component.controllers2

import android.os.CountDownTimer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import dev.aaa1115910.bv.component.controllers.info.VideoPlayerInfoData
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.util.formatMinSec

@Composable
fun ControllerVideoInfo(
    modifier: Modifier = Modifier,
    show: Boolean,
    infoData: VideoPlayerInfoData,
    title: String,
    secondTitle: String,
    clock: Triple<Int, Int, Int>,
    idleIcon: String,
    movingIcon: String,
    onHideInfo: () -> Unit
) {
    var seekHideTimer: CountDownTimer? by remember { mutableStateOf(null) }
    val setCloseInfoTimer: () -> Unit = {
        if (show) {
            seekHideTimer?.cancel()
            seekHideTimer = object : CountDownTimer(5000, 1000) {
                override fun onTick(millisUntilFinished: Long) {}
                override fun onFinish() = onHideInfo()
            }
            seekHideTimer?.start()
        } else {
            seekHideTimer?.cancel()
            seekHideTimer = null
        }
    }

    LaunchedEffect(Unit) {
        setCloseInfoTimer()
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        AnimatedVisibility(
            modifier = Modifier.align(Alignment.TopCenter),
            visible = show,
            enter = expandVertically(),
            exit = shrinkVertically(),
            label = "ControllerTopVideoInfo"
        ) {
            ControllerVideoInfoTop(
                modifier = Modifier.align(Alignment.TopCenter),
                title = title,
                clock = clock
            )
        }
        AnimatedVisibility(
            modifier = Modifier.align(Alignment.BottomCenter),
            visible = show,
            enter = expandVertically(),
            exit = shrinkVertically(),
            label = "ControllerBottomVideoInfo"
        ) {
            ControllerVideoInfoBottom(
                infoData = infoData,
                partTitle = secondTitle,
                idleIcon = idleIcon,
                movingIcon = movingIcon
            )
        }
    }
}

@Composable
fun ControllerVideoInfoTop(
    modifier: Modifier = Modifier,
    title: String,
    clock: Triple<Int, Int, Int>
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(
                MaterialTheme.shapes.large
                    .copy(topStart = CornerSize(0.dp), topEnd = CornerSize(0.dp))
            )
            .background(Color.Black.copy(0.5f))
            .padding(horizontal = 32.dp, vertical = 16.dp),
    ) {
        Box {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 100.dp),
                text = title,
                style = MaterialTheme.typography.displaySmall,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Clock(
                modifier = Modifier.align(Alignment.CenterEnd),
                hour = clock.first,
                minute = clock.second,
                second = clock.third
            )
        }
    }
}

@Composable
fun ControllerVideoInfoBottom(
    modifier: Modifier = Modifier,
    partTitle: String,
    infoData: VideoPlayerInfoData,
    idleIcon: String,
    movingIcon: String
) {
    Column(
        modifier = modifier
            .clip(
                MaterialTheme.shapes.large
                    .copy(bottomStart = CornerSize(0.dp), bottomEnd = CornerSize(0.dp))
            )
            .background(Color.Black.copy(0.5f))
            .padding(bottom = 12.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .focusable(false),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                modifier = Modifier
                    .padding(horizontal = 28.dp)
                    .fillMaxWidth(0.7f),
                text = partTitle,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.displaySmall.copy(
                    fontSize = (MaterialTheme.typography.displaySmall.fontSize.value - 10).sp
                ),
            )
            Text(
                modifier = Modifier.padding(top = 16.dp, bottom = 0.dp, end = 40.dp),
                text = "${infoData.currentTime.formatMinSec()} / ${infoData.totalDuration.formatMinSec()}",
                color = Color.White
            )
        }
        VideoProgressSeek(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            duration = infoData.totalDuration,
            position = infoData.currentTime,
            bufferedPercentage = infoData.bufferedPercentage,
            moveState = SeekMoveState.Idle,
            idleIcon = idleIcon,
            movingIcon = movingIcon
        )
    }
}

@Composable
private fun Clock(
    modifier: Modifier = Modifier,
    hour: Int,
    minute: Int,
    second: Int
) {
    Text(
        modifier = modifier,
        color = Color.White,
        fontWeight = FontWeight.Bold,
        text = buildAnnotatedString {
            withStyle(SpanStyle(fontSize = 32.sp)) {
                append("$hour".padStart(2, '0'))
                append(":")
                append("$minute".padStart(2, '0'))
            }
            withStyle(SpanStyle(fontSize = 18.sp)) {
                append(":")
                append("$second".padStart(2, '0'))
            }
        }
    )
}

@Preview
@Composable
private fun ClockPreview() {
    val clock = Triple(12, 30, 30)
    BVTheme {
        Clock(
            hour = clock.first,
            minute = clock.second,
            second = clock.third
        )
    }
}

@Preview(device = "id:tv_1080p")
@Composable
private fun ControllerVideoInfoPreview() {
    var show by remember { mutableStateOf(true) }

    BVTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Button(onClick = { show = !show }) {
                Text(text = "Switch")
            }
        }
        ControllerVideoInfo(
            modifier = Modifier.fillMaxSize(),
            show = show,
            infoData = VideoPlayerInfoData(
                totalDuration = 100,
                currentTime = 33,
                bufferedPercentage = 66,
                resolutionWidth = 0,
                resolutionHeight = 0,
                codec = ""
            ),
            title = "【A320】民航史上最佳逆袭！A320的前世今生！民航史上最佳逆袭！A320的前世今生！",
            secondTitle = "2023车队车手介绍分析预测",
            clock = Triple(12, 30, 30),
            onHideInfo = {},
            idleIcon = "",
            movingIcon = ""
        )
    }
}