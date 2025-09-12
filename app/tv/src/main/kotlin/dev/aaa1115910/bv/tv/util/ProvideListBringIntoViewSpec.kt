package dev.aaa1115910.bv.tv.util

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.BringIntoViewSpec
import androidx.compose.foundation.gestures.LocalBringIntoViewSpec
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.abs

/**
 * Provides a [BringIntoViewSpec] that calculates the scroll offset for a child item in a LazyList
 * with intelligent positioning logic.
 *
 * The positioning logic:
 * 1. If the focused element is fully visible, don't scroll
 * 2. If the focused element is in the upper, align its top edge with container top
 * 3. If the focused element is in the lower, align its bottom edge with container bottom
 *
 * @param padding 容器上下左右预留的内边距。单位是dp
 *     注意：延迟列表默认只组合可见项，必须留边距露出一点点下一行用来确保将要获得焦点的项已被组合，否则下移的时候焦点会选中下一行的第一个，上移的时候焦点会选中上一行的最后一个（焦点乱跳的问题）
 *     另外，本应用列表用的视频卡片组件有发光效果，不留边距会没显示不全。
 * @param content 包含在 LazyList 中的内容
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProvideListBringIntoViewSpec(
    padding: Dp = 24.dp,
    content: @Composable () -> Unit,
) {
    val paddingPx = LocalDensity.current.run { padding.toPx() }
    val bringIntoViewSpec = object : BringIntoViewSpec {
        override fun calculateScrollDistance(
            offset: Float,
            size: Float,
            containerSize: Float
        ): Float = calculateScrollDistanceMod(
            offset = offset,
            size = size,
            containerSize = containerSize,
            padding = paddingPx
        )
    }
    CompositionLocalProvider(
        LocalBringIntoViewSpec provides bringIntoViewSpec,
        content = content,
    )
}

private fun calculateScrollDistanceMod(
    offset: Float,
    size: Float,
    containerSize: Float,
    padding: Float = 90f // 容器上下左右预留的内边距。
): Float {
    val trailingEdge = offset + size + padding
    @Suppress("UnnecessaryVariable") val leadingEdge = offset - padding
    return when {

        // 如果组件已经完整显示，不滚动
        leadingEdge >= 0 && trailingEdge <= containerSize -> 0f

        // 如果组件可见但比容器大，不滚动
        leadingEdge < 0 && trailingEdge > containerSize -> 0f

        // 找出使其中一条边与容器的边重合所需的最小滚动量
        abs(leadingEdge) < abs(trailingEdge - containerSize) -> leadingEdge
        else -> trailingEdge - containerSize
    }
}
