package dev.aaa1115910.bv.util

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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

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
        initialValue = Color.White.copy(alpha = 1f),
        targetValue = Color.White.copy(alpha = 0.1f),
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "focused border animate color"
    )
    val borderColor = if (hasFocus) {
        if (animate) animateColor else Color.White
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
