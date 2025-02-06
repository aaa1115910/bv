package com.origeek.imageViewer.viewer

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import com.origeek.imageViewer.previewer.DEFAULT_CROSS_FADE_ANIMATE_SPEC
import kotlinx.coroutines.launch

class RawGesture(
    val onTap: (Offset) -> Unit = {},
    val onDoubleTap: (Offset) -> Unit = {},
    val onLongPress: (Offset) -> Unit = {},
    val gestureStart: () -> Unit = {},
    val gestureEnd: (transformOnly: Boolean) -> Unit = {},
    val onGesture: (centroid: Offset, pan: Offset, zoom: Float, rotation: Float, event: PointerEvent) -> Boolean = { _, _, _, _, _ -> true },
)

data class SizeChangeContent(
    val defaultSize: IntSize,
    val containerSize: IntSize,
    val maxScale: Float,
)

@Composable
fun ImageComposeOrigin(
    modifier: Modifier = Modifier,
    model: Any,
    scale: Float = DEFAULT_SCALE,
    offsetX: Float = DEFAULT_OFFSET_X,
    offsetY: Float = DEFAULT_OFFSET_Y,
    rotation: Float = DEFAULT_ROTATION,
    gesture: RawGesture = RawGesture(),
    onMounted: () -> Unit = {},
    onSizeChange: suspend (SizeChangeContent) -> Unit = {},
    crossfadeAnimationSpec: AnimationSpec<Float> = DEFAULT_CROSS_FADE_ANIMATE_SPEC,
    boundClip: Boolean = true,
) {
    val scope = rememberCoroutineScope()
    // 容器大小
    var bSize by remember { mutableStateOf(IntSize(0, 0)) }
    // 容器比例
    val bRatio by remember { derivedStateOf { bSize.width.toFloat() / bSize.height.toFloat() } }
    // 图片原始大小
    var oSize by remember { mutableStateOf(IntSize(0, 0)) }
    // 图片原始比例
    val oRatio by remember { derivedStateOf { oSize.width.toFloat() / oSize.height.toFloat() } }
    // 是否宽度与容器大小一致
    var widthFixed by remember { mutableStateOf(false) }
    // 长宽是否均超出容器长宽
    val superSize by remember {
        derivedStateOf {
            oSize.height > bSize.height && oSize.width > bSize.width
        }
    }
    // 显示大小
    val uSize by remember {
        derivedStateOf {
            if (oRatio > bRatio) {
                // 宽度一致
                val uW = bSize.width
                val uH = uW / oRatio
                widthFixed = true
                IntSize(uW, uH.toInt())
            } else {
                // 高度一致
                val uH = bSize.height
                val uW = uH * oRatio
                widthFixed = false
                IntSize(uW.toInt(), uH)
            }
        }
    }
    // 图片显示的真实大小
    val rSize by remember {
        derivedStateOf {
            IntSize(
                (uSize.width * scale).toInt(),
                (uSize.height * scale).toInt()
            )
        }
    }

    LaunchedEffect(key1 = oSize, key2 = bSize, key3 = rSize) {
        val maxScale = when {
            superSize -> {
                oSize.width.toFloat() / uSize.width.toFloat()
            }

            widthFixed -> {
                bSize.height.toFloat() / uSize.height.toFloat()
            }

            else -> {
                bSize.width.toFloat() / uSize.width.toFloat()
            }
        }
        onSizeChange(
            SizeChangeContent(
                defaultSize = uSize,
                containerSize = bSize,
                maxScale = maxScale
            )
        )
    }

    // 图片是否加载成功
    var imageSpecified by remember { mutableStateOf(false) }

    // 承载容器的透明度，主要用来控制图片加载成功后的渐变效果
    val viewerAlpha = remember { Animatable(0F) }

    /**
     * mounted回调
     */
    fun goMounted() {
        scope.launch {
            viewerAlpha.animateTo(1F, crossfadeAnimationSpec)
            onMounted()
        }
    }

    when (model) {
        is Painter -> {
            var isMounted by remember { mutableStateOf(false) }
            imageSpecified = model.intrinsicSize.isSpecified
            LaunchedEffect(key1 = model.intrinsicSize, block = {
                if (imageSpecified) {
                    oSize = IntSize(
                        model.intrinsicSize.width.toInt(),
                        model.intrinsicSize.height.toInt()
                    )
                    if (!isMounted) {
                        isMounted = true
                        goMounted()
                    }
                }
            })
        }

        is ImageVector -> {
            imageSpecified = true
            LocalDensity.current.run {
                oSize = IntSize(
                    model.defaultWidth.toPx().toInt(),
                    model.defaultHeight.toPx().toInt(),
                )
                goMounted()
            }
        }

        is ImageBitmap -> {
            imageSpecified = true
            oSize = IntSize(
                model.width,
                model.height
            )
            goMounted()
        }

        is ComposeModel -> {
            imageSpecified = true
            LaunchedEffect(key1 = model.intrinsicSize, block = {
                oSize = if (model.intrinsicSize == IntSize.Zero) {
                    bSize
                } else {
                    model.intrinsicSize
                }
            })
            goMounted()
        }

        else -> throw Exception("This model type is not supported!")
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer {
                // 图片位移时会超出容器大小，需要在这个地方指定是否裁切
                clip = boundClip
                alpha = viewerAlpha.value
            }
            .onSizeChanged {
                bSize = it
            }
            .pointerInput(Unit) {
                detectTapGestures(onLongPress = gesture.onLongPress)
            }
            .pointerInput(key1 = imageSpecified) {
                if (imageSpecified) detectTransformGestures(
                    onTap = gesture.onTap,
                    onDoubleTap = gesture.onDoubleTap,
                    gestureStart = gesture.gestureStart,
                    gestureEnd = gesture.gestureEnd,
                    onGesture = gesture.onGesture,
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        val imageModifier = Modifier
            .graphicsLayer {
                if (imageSpecified) {
                    scaleX = scale
                    scaleY = scale
                    translationX = offsetX
                    translationY = offsetY
                    rotationZ = rotation
                }
            }
            .size(
                LocalDensity.current.run { uSize.width.toDp() },
                LocalDensity.current.run { uSize.height.toDp() }
            )
        when (model) {
            is Painter -> {
                Image(
                    painter = model,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = imageModifier,
                )
            }

            is ImageVector -> {
                Image(
                    imageVector = model,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = imageModifier,
                )
            }

            is ImageBitmap -> {
                Image(
                    bitmap = model,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = imageModifier,
                )
            }

            is ComposeModel -> {
                Box(modifier = imageModifier, contentAlignment = Alignment.Center) {
                    model.PoseContent()
                }
            }
        }

    }
}