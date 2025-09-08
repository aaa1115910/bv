package dev.aaa1115910.bv.util

import android.annotation.SuppressLint
import android.graphics.BlurMaskFilter
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.material3.ShapeDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import dev.aaa1115910.bv.util.rememberDebouncer
import kotlinx.coroutines.Job

/**
 * 获取到焦点时显示白色边框
 */
fun Modifier.focusedBorder(
    shape: Shape = ShapeDefaults.Large,
    animate: Boolean = false
): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "infinite border color transition")
    var hasFocus by remember { mutableStateOf(false) }

    val animateColor by infiniteTransition.animateColor(
        initialValue = MaterialTheme.colorScheme.border.copy(alpha = 1f),
        targetValue = MaterialTheme.colorScheme.border.copy(alpha = 0.1f),
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "focused border animate color"
    )
    val borderColor = if (hasFocus) {
        if (animate) animateColor else MaterialTheme.colorScheme.border
    } else Color.Transparent

    onFocusChanged { hasFocus = it.hasFocus }
        .border(
            width = 3.dp,
            color = borderColor,
            shape = shape
        )
}

/**
 * 在没有获取到焦点的时候缩小，以便在获取到焦点的时候“放大”
 */
fun Modifier.focusedScale(
    scale: Float = 0.9f
): Modifier = composed {
    var hasFocus by remember { mutableStateOf(false) }
    val scaleValue by animateFloatAsState(
        targetValue = if (hasFocus) 1f else scale,
        label = "focused scale"
    )

    onFocusChanged { hasFocus = it.hasFocus }
        .scale(scaleValue)
}

/**
 * 延迟处理焦点变化的Modifier扩展函数
 * 
 * @param delayTime 延迟时间（毫秒）
 * @param action 延迟后要执行的操作
 */
fun Modifier.onDelayFocusChanged(
    delayTime: Long = 200L,
    action: (FocusState) -> Unit
) = composed {
    val scope = rememberCoroutineScope()
    val debouncer = rememberDebouncer<FocusState>(delayTime)
    
    onFocusChanged { focusState ->
        debouncer.debounce(scope, focusState, action)
    }
}

/**
 * 统一 Glow：不依赖系统 RenderEffect，通过 BlurMaskFilter 对形状描边进行模糊，保证真机/模拟器一致。
 *
 * 参数说明:
 * @param enabled 是否启用 glow；false 时直接返回原 Modifier，避免多余开销。
 * @param shape 发光轮廓形状（需与内部内容的裁剪圆角一致，否则会露边）。
 * @param glowColor 发光主色，最终会按 [glowAlpha] 叠加透明度。
 * @param glowRadius 模糊半径(外扩范围)。过大(>48.dp) 可能影响性能；默认 8.dp 可在焦点动画中动态调节。
 * @param glowAlpha 发光整体透明度(0f~1f)。建议 0.4~0.75 之间，过高会显得糊、过低不明显。
 * @param spreadMultiplier 发光线条（stroke）相对模糊半径的扩散倍数；增大使光更“胖”更柔，减小更紧实。典型 0.7~1.1。
 */
@SuppressLint("UnnecessaryComposedModifier")
fun Modifier.unifiedGlow(
    enabled: Boolean,
    shape: Shape,
    glowColor: Color,
    glowRadius: Dp = 8.dp,
    glowAlpha: Float = 0.65f,
    spreadMultiplier: Float = 0.9f
): Modifier = composed {
    if (!enabled || glowRadius <= 0.dp) return@composed this
    val density = LocalDensity.current
    val radiusPx = with(density) { glowRadius.toPx() }
    val spreadPx = radiusPx * spreadMultiplier
    this.drawBehind {
        val outline: Outline = shape.createOutline(size, layoutDirection, this)
        val path: Path = when (outline) {
            is Outline.Rectangle -> Path().apply { addRect(outline.rect) }
            is Outline.Rounded -> Path().apply { addRoundRect(outline.roundRect) }
            is Outline.Generic -> outline.path
        }
        drawIntoCanvas { canvas ->
            val paint = androidx.compose.ui.graphics.Paint()
            val framework = paint.asFrameworkPaint()
            framework.isAntiAlias = true
            framework.style = android.graphics.Paint.Style.STROKE
            framework.strokeWidth = spreadPx * 2f
            framework.color = glowColor.copy(alpha = glowAlpha).toArgb()
            framework.maskFilter = BlurMaskFilter(radiusPx, BlurMaskFilter.Blur.NORMAL)
            canvas.drawPath(path, paint)
        }
    }
}