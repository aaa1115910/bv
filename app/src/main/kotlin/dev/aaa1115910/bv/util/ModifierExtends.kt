package dev.aaa1115910.bv.util

import android.graphics.Bitmap
import android.graphics.Canvas
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toIntSize
import com.caverock.androidsvg.SVG
import dev.aaa1115910.biliapi.entity.danmaku.DanmakuMask
import dev.aaa1115910.biliapi.entity.danmaku.DanmakuMaskFrame
import dev.aaa1115910.biliapi.entity.danmaku.DanmakuMobMaskFrame
import dev.aaa1115910.biliapi.entity.danmaku.DanmakuWebMaskFrame

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

fun Modifier.bitmapMask(
    bitmap: Bitmap
): Modifier = composed {
    drawWithContent {
        drawIntoCanvas { canvas ->
            canvas.saveLayer(Rect(Offset.Zero, size), Paint())
            drawContent()
            drawImage(
                image = bitmap.asImageBitmap(),
                dstSize = size.toIntSize(),
                blendMode = BlendMode.DstIn
            )
            canvas.restore()
        }
    }
}

fun Modifier.danmakuWebMask(
    frame: DanmakuWebMaskFrame
): Modifier = composed {
    val svgObj = runCatching {
        SVG.getFromString(frame.svg)
    }.getOrNull() ?: return@composed this

    val svgWidth = svgObj.documentWidth.toInt()
    val svgHeight = svgObj.documentHeight.toInt()

    val bitmap = Bitmap.createBitmap(svgWidth, svgHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    svgObj.renderToCanvas(canvas)

    bitmapMask(bitmap)
}

fun Modifier.danmakuMobMask(
    frame: DanmakuMobMaskFrame
): Modifier = composed {
    val binaryBitmap= Bitmap.createBitmap(40, 180, Bitmap.Config.ARGB_8888)
    frame.image.forEachIndexed { index, byte ->
        val y= index / 40
        val x= index % 40
        binaryBitmap.setPixel(x, y, if (byte.toInt() == 0) android.graphics.Color.BLACK else android.graphics.Color.TRANSPARENT)
    }

    bitmapMask(binaryBitmap)
}

fun Modifier.danmakuMask(
    frame: DanmakuMaskFrame?
): Modifier = composed {
    if (frame == null) return@composed this

    when(frame){
        is DanmakuWebMaskFrame -> danmakuWebMask(frame)
        is DanmakuMobMaskFrame -> danmakuMobMask(frame)
    }
}