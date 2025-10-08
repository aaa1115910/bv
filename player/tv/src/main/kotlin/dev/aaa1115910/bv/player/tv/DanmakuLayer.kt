package dev.aaa1115910.bv.player.tv

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import dev.aaa1115910.biliapi.entity.danmaku.DanmakuMaskFrame
import dev.aaa1115910.bv.player.AkDanmakuPlayer
import dev.aaa1115910.bv.player.util.danmakuMask
import com.kuaishou.akdanmaku.ui.DanmakuPlayer

/**
 * 可稳定引用的弹幕层句柄，父级持有该句柄后，只要内部字段变化，
 * 仅弹幕层自身重组，避免父级（包含日志/时钟等频繁变化状态）级联重组。
 */
@Stable
class DanmakuLayerHandle(
    initialDanmakuPlayer: DanmakuPlayer? = null
) {
    // 内部保存引用，对外通过 updateDanmakuPlayer 更新，避免与 JVM setter 同名冲突
    var danmakuPlayer: DanmakuPlayer? by mutableStateOf(initialDanmakuPlayer)
        private set

    // 弹幕显示区域高度占比 (0f..1f)
    var areaFraction by mutableFloatStateOf(1f)
        private set

    // 透明度
    var opacity by mutableFloatStateOf(1f)
        private set

    // 当前蒙版帧（null 表示无蒙版）
    var maskFrame: DanmakuMaskFrame? by mutableStateOf(null)
        private set

    // 是否显示弹幕
    var visible by mutableStateOf(true)
        private set

    fun updateDanmakuPlayer(player: DanmakuPlayer?) {
        if (danmakuPlayer !== player) danmakuPlayer = player
    }

    fun update(
        area: Float? = null,
        opacity: Float? = null,
        mask: DanmakuMaskFrame? = maskFrame,
        visible: Boolean? = null
    ) {
        area?.let { if (areaFraction != it) areaFraction = it }
        opacity?.let { if (this.opacity != it) this.opacity = it }
        if (maskFrame !== mask) maskFrame = mask
        visible?.let { if (this.visible != it) this.visible = it }
    }
}

@Composable
fun DanmakuLayer(
    modifier: Modifier = Modifier,
    handle: DanmakuLayerHandle,
) {
    val player = handle.danmakuPlayer
    if (player == null || !handle.visible) return

    // 根据 maskFrame 动态应用蒙版 Modifier（避免无意义的额外 Modifier 组合）
    val maskModifier = if (handle.maskFrame != null) {
        Modifier.danmakuMask(handle.maskFrame)
    } else Modifier

    Box(
        modifier
            .fillMaxWidth()
            .fillMaxHeight(handle.areaFraction)
            .alpha(handle.opacity)
            .then(maskModifier)
    ) {
        AkDanmakuPlayer(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(),
            danmakuPlayer = player
        )
    }
}
