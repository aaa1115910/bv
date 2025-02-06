package com.origeek.imageViewer.previewer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import com.origeek.imageViewer.viewer.ImageViewerState
import com.origeek.imageViewer.viewer.rememberViewerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.withContext

/**
 * @program: ImageViewer
 *
 * @description:
 *
 * @author: JVZIYAOYAO
 *
 * @create: 2022-10-17 14:45
 **/

internal class ViewerContainerState(
    // 协程作用域
    var scope: CoroutineScope = MainScope(),
    // 转换图层的状态
    var transformState: TransformContentState = TransformContentState(),
    // viewer的状态
    var imageViewerState: ImageViewerState = ImageViewerState(),
    // 默认动画窗格
    var defaultAnimationSpec: AnimationSpec<Float> = DEFAULT_SOFT_ANIMATION_SPEC
) {

    /**
     *   +-------------------+
     *         INTERNAL
     *   +-------------------+
     */

    // 转换图层transformContent透明度
    internal var transformContentAlpha = Animatable(0F)

    // viewer容器的透明度
    internal var viewerContainerAlpha = Animatable(1F)

    // 是否允许界面显示loading
    internal var allowLoading by mutableStateOf(true)

    // 打开图片后到加载成功过程的协程任务
    internal var openTransformJob: Deferred<Unit>? = null

    /**
     * 取消打开动作
     */
    internal fun cancelOpenTransform() {
        openTransformJob?.cancel()
        openTransformJob = null
    }

    /**
     * 等待挂载成功
     */
    internal suspend fun awaitOpenTransform() {
        // 这里需要等待viewer挂载，显示loading界面
        openTransformJob = scope.async {
            // 等待viewer加载
            awaitViewerLoading()
            // viewer加载成功后显示viewer
            transformSnapToViewer(true)
        }
        openTransformJob?.await()
        openTransformJob = null
    }

    /**
     * 等待viewer挂载成功
     */
    internal suspend fun awaitViewerLoading() {
        imageViewerState.mountedFlow.apply {
            withContext(Dispatchers.Default) {
                takeWhile { !it }.collect()
            }
        }
    }

    /**
     * 转换图层转viewer图层，true显示viewer，false显示转换图层
     * @param isViewer Boolean
     */
    internal suspend fun transformSnapToViewer(isViewer: Boolean) {
        if (isViewer) {
            transformContentAlpha.snapTo(0F)
            viewerContainerAlpha.snapTo(1F)
        } else {
            transformContentAlpha.snapTo(1F)
            viewerContainerAlpha.snapTo(0F)
        }
    }

    /**
     * 将viewer容器的位置大小复制给transformContent
     */
    internal suspend fun copyViewerContainerStateToTransformState() {
        transformState.apply {
            val targetScale = scale.value * fitScale
            graphicScaleX.snapTo(targetScale)
            graphicScaleY.snapTo(targetScale)
            val centerOffsetY = (containerSize.height - realSize.height).div(2)
            val centerOffsetX = (containerSize.width - realSize.width).div(2)
            offsetY.snapTo(centerOffsetY + this@ViewerContainerState.offsetY.value)
            offsetX.snapTo(centerOffsetX + this@ViewerContainerState.offsetX.value)
        }
    }

    /**
     * 将viewer的位置大小等信息复制给transformContent
     * @param itemState TransformItemState
     */
    internal suspend fun copyViewerPosToContent(itemState: TransformItemState) {
        transformState.apply {
            // 更新itemState，确保itemState一致
            this@apply.itemState = itemState
            // 确保viewer的容器大小与transform的容器大小一致
            containerSize = imageViewerState.containerSize
            val scale = imageViewerState.scale
            val offsetX = imageViewerState.offsetX
            val offsetY = imageViewerState.offsetY
            // 计算transform的实际大小
            val rw = fitSize.width * scale.value
            val rh = fitSize.height * scale.value
            // 计算目标平移量
            val goOffsetX =
                (containerSize.width - rw).div(2) + offsetX.value
            val goOffsetY =
                (containerSize.height - rh).div(2) + offsetY.value
            // 计算缩放率
            val fixScale = fitScale * scale.value

            // 更新值
            graphicScaleX.snapTo(fixScale)
            graphicScaleY.snapTo(fixScale)
            displayWidth.snapTo(displayRatioSize.width)
            displayHeight.snapTo(displayRatioSize.height)
            this@apply.offsetX.snapTo(goOffsetX)
            this@apply.offsetY.snapTo(goOffsetY)
        }
    }

    // 容器大小
    var containerSize: IntSize by mutableStateOf(IntSize.Zero)

    // 容器的偏移量X
    var offsetX = Animatable(0F)

    // 容器的偏移量Y
    var offsetY = Animatable(0F)

    // 容器缩放
    var scale = Animatable(1F)

    /**
     * 重置回原来的状态
     * @param animationSpec AnimationSpec<Float>
     */
    suspend fun reset(animationSpec: AnimationSpec<Float> = defaultAnimationSpec) {
        scope.apply {
            listOf(
                async {
                    offsetX.animateTo(0F, animationSpec)
                },
                async {
                    offsetY.animateTo(0F, animationSpec)
                },
                async {
                    scale.animateTo(1F, animationSpec)
                },
            ).awaitAll()
        }
    }

    /**
     * 立刻重置
     */
    suspend fun resetImmediately() {
        offsetX.snapTo(0F)
        offsetY.snapTo(0F)
        scale.snapTo(1F)
    }

    companion object {
        val Saver: Saver<ViewerContainerState, *> = mapSaver(
            save = {
                mapOf<String, Any>(
                    it::offsetX.name to it.offsetX.value,
                    it::offsetY.name to it.offsetY.value,
                    it::scale.name to it.scale.value,
                )
            },
            restore = {
                val viewerContainerState = ViewerContainerState()
                viewerContainerState.offsetX =
                    Animatable(it[viewerContainerState::offsetX.name] as Float)
                viewerContainerState.offsetY =
                    Animatable(it[viewerContainerState::offsetY.name] as Float)
                viewerContainerState.scale =
                    Animatable(it[viewerContainerState::scale.name] as Float)
                viewerContainerState
            }
        )
    }
}

/**
 * 记录Viewer容器的状态
 * @return ViewerContainerState
 */
@Composable
internal fun rememberViewerContainerState(
    // 协程作用域
    scope: CoroutineScope = rememberCoroutineScope(),
    // viewer状态
    viewerState: ImageViewerState = rememberViewerState(),
    // 转换content的state
    transformContentState: TransformContentState = rememberTransformContentState(),
    // 动画窗格
    animationSpec: AnimationSpec<Float> = DEFAULT_SOFT_ANIMATION_SPEC,
): ViewerContainerState {
    val viewerContainerState = rememberSaveable(saver = ViewerContainerState.Saver) {
        ViewerContainerState()
    }
    viewerContainerState.scope = scope
    viewerContainerState.imageViewerState = viewerState
    viewerContainerState.transformState = transformContentState
    viewerContainerState.defaultAnimationSpec = animationSpec
    return viewerContainerState
}

/**
 * Viewer容器
 */
@Composable
internal fun ImageViewerContainer(
    // 修改对象
    modifier: Modifier = Modifier,
    // 容器状态
    containerState: ViewerContainerState,
    // 未加载成功时的占位
    placeholder: PreviewerPlaceholder = PreviewerPlaceholder(),
    // viewer主体
    viewer: @Composable () -> Unit,
) {
    containerState.apply {
        Box(
            modifier = modifier
                .fillMaxSize()
                .onGloballyPositioned {
                    containerSize = it.size
                }
                .graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                    translationX = offsetX.value
                    translationY = offsetY.value
                }
        ) {
            // 支持转换效果的图层
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(transformContentAlpha.value)
            ) {
                TransformContentView(transformState)
            }
            // viewer图层
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(viewerContainerAlpha.value)
            ) {
                viewer()
            }
            // 判断viewer是否加载成功
            val viewerMounted by imageViewerState.mountedFlow.collectAsState(
                initial = false
            )
            if (allowLoading) AnimatedVisibility(
                visible = !viewerMounted,
                enter = placeholder.enterTransition,
                exit = placeholder.exitTransition,
            ) {
                placeholder.content()
            }
        }
    }
}