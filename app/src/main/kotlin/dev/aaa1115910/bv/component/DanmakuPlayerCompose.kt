package dev.aaa1115910.bv.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.kuaishou.akdanmaku.ui.DanmakuPlayer
import com.kuaishou.akdanmaku.ui.DanmakuView

@Composable
fun DanmakuPlayerCompose(
    modifier: Modifier = Modifier,
    danmakuPlayer: DanmakuPlayer
) {
    val context = LocalContext.current

    DisposableEffect(key1 = Unit) {
        onDispose {
            danmakuPlayer.release()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                DanmakuView(context).apply {
                    danmakuPlayer.bindView(this)
                }
            }
        )
    }
}
