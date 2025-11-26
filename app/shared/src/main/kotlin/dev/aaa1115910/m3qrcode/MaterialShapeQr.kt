package dev.aaa1115910.m3qrcode

import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.graphics.withTranslation
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
import dev.aaa1115910.bv.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.min

@Composable
fun MaterialShapeQr(
    modifier: Modifier = Modifier,
    content: String,
    ecLevel: MaterialShapeQrErrorCorrectionLevel = MaterialShapeQrErrorCorrectionLevel.M,
    colorScheme: ColorScheme? = null
) {
    val context = LocalContext.current
    val view = LocalView.current
    val state = rememberMaterialShapeQrState(
        colorScheme = colorScheme ?: if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            androidx.compose.material3.dynamicLightColorScheme(context)
        } else {
            androidx.compose.material3.lightColorScheme()
        }
    )
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.lottie_qrcode_background))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    val lottieDynamicProperties = rememberMaterialShapeQrLottieDynamicProperties(state.colorMap)

    LaunchedEffect(content, ecLevel) {
        state.updateContent(content, ecLevel.level)
    }

    // 每 16ms 更新一次（近似 60fps），当还未生成 QR 时降低频率为 50ms
    LaunchedEffect(Unit) {
        while (isActive) {
            if (state.qrcodeLineCount == 0) {
                delay(50)
                continue
            }
            state.frameElapsed = System.currentTimeMillis() - state.qrStartTime
            delay(16)
        }
    }

    Box(
        modifier = modifier
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            modifier = Modifier.scale(state.lottieScale),
            composition = composition,
            progress = { progress },
            dynamicProperties = lottieDynamicProperties
        )
        if (view.isInEditMode) {
            LottieAnimation(
                composition = composition,
                progress = { progress },
                dynamicProperties = lottieDynamicProperties
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize(0.8f)
        ) {

            val nonFinderFrameElapsed by remember {
                derivedStateOf {
                    if (state.hasFinalDataImage) 3000L else state.frameElapsed
                }
            }

            NonFinderPatternsCanvas(
                qrcodeLineCount = state.qrcodeLineCount,
                qrcodeSize = state.qrcodeSize,
                frameElapsed = nonFinderFrameElapsed,
                dataModuleShapeList = state.dataModuleShapeList,
                backgroundModuleShapeList = state.backgroundModuleShapeList,
                hasFinalDataImage = state.hasFinalDataImage,
                onHasFinalDataImageChange = { state.hasFinalDataImage = it },
                onDrawAnimationBackground = { elapsedMs ->
                    state.drawAnimationBackground(elapsedMs)
                }
            )
            FinderPatternsCanvas(
                qrcodeLineCount = state.qrcodeLineCount,
                qrcodeSize = state.qrcodeSize,
                frameElapsed = state.frameElapsed,
                finderPatternShapeList = state.finderPatternShapeList
            )
        }
    }
}

@Composable
fun NonFinderPatternsCanvas(
    modifier: Modifier = Modifier,
    qrcodeLineCount: Int,
    qrcodeSize: Int,
    frameElapsed: Long,
    dataModuleShapeList: List<MaterialShapeRenderer>,
    backgroundModuleShapeList: List<MaterialShapeRenderer>,
    hasFinalDataImage: Boolean = false,
    onHasFinalDataImageChange: (Boolean) -> Unit,
    onDrawAnimationBackground: (Long) -> Unit
) {
    Canvas(
        modifier = modifier.fillMaxSize()
    ) {
        val native = drawContext.canvas.nativeCanvas

        if (qrcodeLineCount == 0) return@Canvas

        val canvasWidth = size.width
        val canvasHeight = size.height

        val scale = min(canvasWidth / qrcodeSize.toFloat(), canvasHeight / qrcodeSize.toFloat())
        val scaledWidth = qrcodeSize * scale
        val scaledHeight = qrcodeSize * scale

        val tx = (canvasWidth - scaledWidth) / 2f
        val ty = (canvasHeight - scaledHeight) / 2f

        val elapsed = frameElapsed
        if (elapsed > 3000L && !hasFinalDataImage) {
            onHasFinalDataImageChange(true)
        }
        val drawElapsed = if (hasFinalDataImage) 3000L else elapsed

        native.withTranslation(tx, ty) {
            scale(scale, scale)
            drawShapeListOnCanvas(dataModuleShapeList, this, drawElapsed)
            drawShapeListOnCanvas(backgroundModuleShapeList, this, drawElapsed)
        }

        if (!hasFinalDataImage) {
            onDrawAnimationBackground(drawElapsed)
        }
    }
}

@Composable
fun FinderPatternsCanvas(
    modifier: Modifier = Modifier,
    qrcodeLineCount: Int,
    qrcodeSize: Int,
    frameElapsed: Long,
    finderPatternShapeList: List<MaterialShapeRenderer>
) {
    Canvas(
        modifier = modifier.fillMaxSize()
    ) {
        val native = drawContext.canvas.nativeCanvas

        if (qrcodeLineCount == 0) return@Canvas

        val canvasWidth = size.width
        val canvasHeight = size.height

        val scale = min(canvasWidth / qrcodeSize.toFloat(), canvasHeight / qrcodeSize.toFloat())
        val scaledWidth = qrcodeSize * scale
        val scaledHeight = qrcodeSize * scale

        val tx = (canvasWidth - scaledWidth) / 2f
        val ty = (canvasHeight - scaledHeight) / 2f

        val elapsed = frameElapsed

        native.withTranslation(tx, ty) {
            scale(scale, scale)
            drawShapeListOnCanvas(finderPatternShapeList, this, elapsed)
        }
    }
}

@Composable
private fun rememberMaterialShapeQrLottieDynamicProperties(
    colorMap: Map<String, Int>
): com.airbnb.lottie.compose.LottieDynamicProperties {
    val properties = colorMap.map { (key, color) ->
        val filter = remember(color) { PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN) }
        rememberLottieDynamicProperty(
            property = LottieProperty.COLOR_FILTER,
            keyPath = arrayOf("**", key, "**"),
        ) {
            filter
        }
    }.toTypedArray()
    return rememberLottieDynamicProperties(*properties)
}

private fun drawShapeListOnCanvas(
    list: List<MaterialShapeRenderer>,
    canvas: Canvas,
    elapsedMs: Long
) {
    list.forEach { it.draw(canvas, elapsedMs) }
}
