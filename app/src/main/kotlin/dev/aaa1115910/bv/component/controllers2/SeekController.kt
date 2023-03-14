package dev.aaa1115910.bv.component.controllers2

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.aaa1115910.bv.component.controllers.info.VideoPlayerInfoData

// TODO 快进快退UI，按下左右/快进快退键时显示，按下确认键确定后才会跳转到指定时间
@Composable
fun SeekController(
    modifier: Modifier = Modifier,
    show: Boolean,
    infoData: VideoPlayerInfoData,
    onGoTime: (time: Int) -> Unit
) {

}