package dev.aaa1115910.bv.component.controllers2

import android.os.CountDownTimer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.aaa1115910.bv.component.controllers.BottomControls
import dev.aaa1115910.bv.component.controllers.TopController
import dev.aaa1115910.bv.component.controllers.info.VideoPlayerInfoData

@Composable
fun ControllerVideoInfo(
    modifier: Modifier = Modifier,
    show: Boolean,
    infoData: VideoPlayerInfoData,
    title: String,
    secondTitle: String,
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
            exit = shrinkVertically()
        ) {
            // TODO 重做顶部信息UI
            TopController(
                modifier = Modifier.align(Alignment.TopCenter),
                title = title
            )
        }
        AnimatedVisibility(
            modifier = Modifier.align(Alignment.BottomCenter),
            visible = show,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            // TODO 重做底部信息UI
            BottomControls(
                infoData = infoData,
                partTitle = secondTitle
            )
        }
    }
}