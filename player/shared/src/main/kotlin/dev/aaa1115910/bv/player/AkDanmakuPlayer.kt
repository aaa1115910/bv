package dev.aaa1115910.bv.player

import android.graphics.Color
import android.os.Build
import android.view.View.LAYER_TYPE_HARDWARE
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.kuaishou.akdanmaku.ui.DanmakuPlayer
import com.kuaishou.akdanmaku.ui.DanmakuView

@Composable
fun AkDanmakuPlayer(
    modifier: Modifier = Modifier,
    danmakuPlayer: DanmakuPlayer?
) {
    val context = LocalContext.current
    var danmakuView: DanmakuView? by remember { mutableStateOf(null) }

    DisposableEffect(Unit) {
        onDispose {
            danmakuPlayer?.release()
        }
    }

    LaunchedEffect(danmakuPlayer) {
        danmakuView?.let { view ->
            danmakuPlayer?.bindView(view)
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            danmakuView = DanmakuView(ctx).apply {
                // 透明背景
                setBackgroundColor(Color.TRANSPARENT)
                
                // 启用硬件加速
                setLayerType(LAYER_TYPE_HARDWARE, null)
                
                // 确保View会被绘制
                setWillNotDraw(false)
                
                // 兼容性优化：对于旧版本Android使用绘制缓存
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                    @Suppress("DEPRECATION")
                    isDrawingCacheEnabled = true
                    @Suppress("DEPRECATION")
                    setDrawingCacheBackgroundColor(Color.TRANSPARENT)
                }
            }
            danmakuView!!
        }
    )
}