package dev.aaa1115910.bv.util

import androidx.compose.animation.core.animateFloatAsState
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
    shape: Shape = ShapeDefaults.Large
): Modifier = composed {
    var hasFocus by remember { mutableStateOf(false) }
    val borderAlpha by animateFloatAsState(if (hasFocus) 1f else 0f)

    onFocusChanged { hasFocus = it.hasFocus }
        .border(
            width = 2.dp,
            color = Color.White.copy(alpha = borderAlpha),
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
    val scaleValue by animateFloatAsState(if (hasFocus) 1f else scale)

    onFocusChanged { hasFocus = it.hasFocus }
        .scale(scaleValue)
}