package com.origeek.imageViewer.previewer

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputScope
import com.origeek.imageViewer.gallery.ImageGalleryState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

/**
 * @program: ImageViewer
 *
 * @description:
 *
 * @author: JVZIYAOYAO
 *
 * @create: 2022-10-17 14:42
 **/

// 默认下拉关闭缩放阈值
const val DEFAULT_SCALE_TO_CLOSE_MIN_VALUE = 0.9F

enum class VerticalDragType {
    // 不开启垂直手势
    None,

    // 仅开启下拉手势
    Down,

    // 支持上下拉手势
    UpAndDown,
    ;
}

/**
 * 增加垂直方向拖拽的能力
 */
open class PreviewerVerticalDragState(
    // 协程作用域
    scope: CoroutineScope = MainScope(),
    // 默认动画窗格
    defaultAnimationSpec: AnimationSpec<Float> = DEFAULT_SOFT_ANIMATION_SPEC,
    // 开启垂直手势的类型
    verticalDragType: VerticalDragType = VerticalDragType.None,
    // 下拉关闭的缩小的阈值
    scaleToCloseMinValue: Float = DEFAULT_SCALE_TO_CLOSE_MIN_VALUE,
    // 预览状态
    galleryState: ImageGalleryState,
) : PreviewerTransformState(scope, defaultAnimationSpec, galleryState) {

    /**
     * viewer容器缩小关闭
     */
    private suspend fun viewerContainerShrinkDown() {
        // 标记动作开始
        stateCloseStart()
        listOf(
            // 缩小容器
            scope.async {
                viewerContainerState?.scale?.animateTo(0F, animationSpec = defaultAnimationSpec)
            },
            // 关闭UI
            scope.async {
                uiAlpha.animateTo(0F, animationSpec = defaultAnimationSpec)
            }
        ).awaitAll()
        // 等待下一帧
        ticket.awaitNextTicket()
        // 关闭动画组件
        animateContainerVisibleState = MutableTransitionState(false)
        // 等待下一帧
        ticket.awaitNextTicket()
        // 重置container
        viewerContainerState?.reset(defaultAnimationSpec)
        // 将transform标记为退出
        transformState?.setExitState()
        // 标记动作结束
        stateCloseEnd()
    }

    /**
     * 响应下拉关闭
     */
    private suspend fun dragDownClose() {
        // 刷新transform的pos
        transformState?.notifyEnterChanged()
        // 关闭loading
        viewerContainerState?.allowLoading = false
        // 等待下一帧，确保transform的pos刷新成功
        ticket.awaitNextTicket()
        // 将container的pos复制给transform
        viewerContainerState?.copyViewerContainerStateToTransformState()
        // container重置
        viewerContainerState?.resetImmediately()
        // 切换到transform
        transformSnapToViewer(false)
        // 等待下一帧
        ticket.awaitNextTicket()
        // 执行转换关闭
        closeTransform(defaultAnimationSpec)
        // 解除loading限制
        viewerContainerState?.allowLoading = true
    }

    /**
     * 设置下拉手势的方法
     * @param pointerInputScope PointerInputScope
     */
    internal suspend fun verticalDrag(pointerInputScope: PointerInputScope) {
        pointerInputScope.apply {
            // 记录开始时的位置
            var vStartOffset by mutableStateOf<Offset?>(null)
            // 标记是否为下拉关闭
            var vOrientationDown by mutableStateOf<Boolean?>(null)
            // 如果getKay不为空才开始检测手势
            if (verticalDragType != VerticalDragType.None) detectVerticalDragGestures(
                onDragStart = OnDragStart@{
                    // 如果imageViewerState不存在，无法进行下拉手势
                    if (imageViewerState == null) return@OnDragStart
                    var transformItemState: TransformItemState? = null
                    // 查询当前transformItem
                    getKey?.apply {
                        findTransformItem(invoke(currentPage))?.apply {
                            transformItemState = this
                        }
                    }
                    // 判断是否允许变换退出，如果允许就标记动作开始
                    // setExitState后，在下拉过程中，itemState不会从界面上消失
                    if (canTransformOut) {
                        transformState?.setEnterState()
                    } else {
                        transformState?.setExitState()
                    }
                    // 更新当前transformItem
                    transformState?.itemState = transformItemState
                    // 只有viewer的缩放率为1时才允许下拉手势
                    if (imageViewerState?.scale?.value == 1F) {
                        vStartOffset = it
                        // 进入下拉手势时禁用viewer的手势
                        imageViewerState?.allowGestureInput = false
                    }
                },
                onDragEnd = OnDragEnd@{
                    // 如果开始位置为空，就退出
                    if (vStartOffset == null) return@OnDragEnd
                    // 如果containerState为空，就退出
                    if (viewerContainerState == null) return@OnDragEnd
                    // 重置开始位置和方向
                    vStartOffset = null
                    vOrientationDown = null
                    // 解除viewer的手势输入限制
                    imageViewerState?.allowGestureInput = true
                    // 缩放小于阈值，执行关闭动画，大于就恢复原样
                    if (viewerContainerState!!.scale.value < scaleToCloseMinValue) {
                        scope.launch {
                            if (getKey != null && canTransformOut) {
                                val key = getKey!!.invoke(currentPage)
                                val transformItem = findTransformItem(key)
                                // 如果item在画面内，就执行变换关闭，否则缩小关闭
                                if (transformItem != null) {
                                    dragDownClose()
                                } else {
                                    viewerContainerShrinkDown()
                                }
                            } else {
                                viewerContainerShrinkDown()
                            }
                            // 结束动画后需要把关闭的UI打开
                            uiAlpha.snapTo(1F)
                        }
                    } else {
                        scope.launch {
                            uiAlpha.animateTo(1F, defaultAnimationSpec)
                        }
                        scope.launch {
                            viewerContainerState?.reset(defaultAnimationSpec)
                        }
                    }
                },
                onVerticalDrag = OnVerticalDrag@{ change, dragAmount ->
                    if (imageViewerState == null) return@OnVerticalDrag
                    if (viewerContainerState == null) return@OnVerticalDrag
                    if (vStartOffset == null) return@OnVerticalDrag
                    if (vOrientationDown == null) vOrientationDown = dragAmount > 0
                    if (vOrientationDown == true || verticalDragType == VerticalDragType.UpAndDown) {
                        val offsetY = change.position.y - vStartOffset!!.y
                        val offsetX = change.position.x - vStartOffset!!.x
                        val containerHeight = viewerContainerState!!.containerSize.height
                        val scale = (containerHeight - offsetY.absoluteValue).div(
                            containerHeight
                        )
                        scope.launch {
                            uiAlpha.snapTo(scale)
                            viewerContainerState?.offsetX?.snapTo(offsetX)
                            viewerContainerState?.offsetY?.snapTo(offsetY)
                            viewerContainerState?.scale?.snapTo(scale)
                        }
                    } else {
                        // 如果不是向上，就返还输入权，以免页面卡顿
                        imageViewerState?.allowGestureInput = true
                    }
                }
            )
        }
    }

    /**
     * 开启垂直手势的类型
     */
    var verticalDragType by mutableStateOf(verticalDragType)

    /**
     * 下拉关闭的缩放的阈值，当scale小于这个值，就关闭，否则还原
     */
    var scaleToCloseMinValue by mutableStateOf(scaleToCloseMinValue)

}