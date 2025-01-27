package dev.aaa1115910.bv.player.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.toIntSize
import com.caverock.androidsvg.SVG
import dev.aaa1115910.biliapi.entity.danmaku.DanmakuMaskFrame
import dev.aaa1115910.biliapi.entity.danmaku.DanmakuMobMaskFrame
import dev.aaa1115910.biliapi.entity.danmaku.DanmakuWebMaskFrame

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
    val binaryBitmap = Bitmap.createBitmap(40, 180, Bitmap.Config.ARGB_8888)
    frame.image.forEachIndexed { index, byte ->
        val y = index / 40
        val x = index % 40
        binaryBitmap.setPixel(x, y, if (byte.toInt() == 0) Color.BLACK else Color.TRANSPARENT)
    }

    bitmapMask(binaryBitmap)
}

fun Modifier.danmakuMask(
    frame: DanmakuMaskFrame?
): Modifier = composed {
    if (frame == null) return@composed this

    when (frame) {
        is DanmakuWebMaskFrame -> danmakuWebMask(frame)
        is DanmakuMobMaskFrame -> danmakuMobMask(frame)
    }
}