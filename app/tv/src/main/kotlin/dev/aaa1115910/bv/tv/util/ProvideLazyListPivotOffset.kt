package dev.aaa1115910.bv.tv.util

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.BringIntoViewSpec
import androidx.compose.foundation.gestures.LocalBringIntoViewSpec
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

/**
 * Provides a [BringIntoViewSpec] that calculates the scroll offset for a child item in a LazyList
 * based on a pivot offset.
 *
 * This allows for custom positioning of the child item within the visible area of the LazyList.
 *
 * @param parentFraction The fraction of the parent container that should be visible above the child item.
 * @param childFraction The fraction of the child item that should be visible below the parent container.
 * @param content The content to be placed inside containing the LazyList.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProvideLazyListPivotOffset(
    parentFraction: Float = 0.3f,
    childFraction: Float = 0f,
    content: @Composable () -> Unit,
) {
    val bringIntoViewSpec = object : BringIntoViewSpec {
        override fun calculateScrollDistance(
            offset: Float,
            size: Float,
            containerSize: Float
        ): Float = calculatePivotOffset(
            parentFraction = parentFraction,
            childFraction = childFraction,
            offset = offset,
            size = size,
            containerSize = containerSize
        )
    }
    CompositionLocalProvider(
        LocalBringIntoViewSpec provides bringIntoViewSpec,
        content = content,
    )
}

/**
 * Calculates the offset of the pivot point for an item requesting focus.
 *
 * @param parentFraction The fraction of the parent container that the item requesting focus is located.
 * @param childFraction The fraction of the item requesting focus that is visible within the parent container.
 * @param offset The initial position of the item requesting focus.
 * @param size The size of the item requesting focus.
 * @param containerSize The size of the lazy container.
 * @return The offset of the pivot point.
 */
private fun calculatePivotOffset(
    parentFraction: Float,
    childFraction: Float,
    offset: Float,
    size: Float,
    containerSize: Float
): Float {
    val childSmallerThanParent = size <= containerSize
    val initialTargetForLeadingEdge =
        parentFraction * containerSize - (childFraction * size)
    val spaceAvailableToShowItem = containerSize - initialTargetForLeadingEdge

    val targetForLeadingEdge =
        if (childSmallerThanParent && spaceAvailableToShowItem < size) {
            containerSize - size
        } else {
            initialTargetForLeadingEdge
        }

    return offset - targetForLeadingEdge
}