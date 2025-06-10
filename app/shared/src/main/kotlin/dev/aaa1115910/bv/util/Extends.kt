package dev.aaa1115910.bv.util

import android.content.Context
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.core.text.HtmlCompat
import dev.aaa1115910.bv.BVApp
import dev.aaa1115910.bv.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit

/**
 * 高效更新列表，在主线程上下文执行
 * 使用差分更新算法，减少内存分配和GC压力
 * @param newList 新列表内容
 */
suspend fun <T> SnapshotStateList<T>.swapListWithMainContext(newList: List<T>) =
    withContext(Dispatchers.Main) {
        // 使用差分更新算法替代简单的clear+addAll
        if (this@swapListWithMainContext.isEmpty()) {
            // 当前列表为空，直接添加所有元素
            addAll(newList)
        } else if (newList.isEmpty()) {
            // 新列表为空，清空当前列表
            clear()
        } else {
            // 复用原有对象，只更新必要的部分
            val currentSize = size
            val newSize = newList.size
            val commonSize = minOf(currentSize, newSize)

            // 1. 更新共同部分，复用已有对象
            for (i in 0 until commonSize) {
                this@swapListWithMainContext[i] = newList[i]
            }

            // 2. 处理大小差异
            if (newSize > currentSize) {
                // 添加新元素
                addAll(newList.subList(currentSize, newSize))
            } else if (currentSize > newSize) {
                // 移除多余元素
                repeat(currentSize - newSize) {
                    removeAt(newSize)
                }
            }
        }
    }

/**
 * 在主线程上下文执行列表更新，并且支持延迟后回调
 * 使用优化的列表更新算法
 */
suspend fun <T> SnapshotStateList<T>.swapListWithMainContext(
    newList: List<T>,
    delay: Long,
    afterSwap: () -> Unit
) {
    this@swapListWithMainContext.swapListWithMainContext(newList)
    delay(delay)
    afterSwap()
}

/**
 * 高效批量添加列表元素，在主线程上下文执行
 * 使用分批处理减少UI阻塞和GC压力
 */
suspend fun <T> SnapshotStateList<T>.addAllWithMainContext(newList: List<T>) =
    withContext(Dispatchers.Main) {
        if (newList.isEmpty()) return@withContext

        // 如果列表过大，分批添加以减少UI阻塞
        if (newList.size > 100) {
            newList.chunked(50).forEach { chunk ->
                addAll(chunk)
                delay(10) // 给UI线程呼吸的时间
            }
        } else {
            addAll(newList)
        }
    }

/**
 * 高效批量添加列表元素，接受延迟块参数
 * 使用已优化的addAllWithMainContext方法实现
 */
suspend fun <T> SnapshotStateList<T>.addAllWithMainContext(newListBlock: suspend () -> List<T>) {
    val newList = newListBlock()
    addAllWithMainContext(newList) // 使用优化版本的批量添加
}

suspend fun <T> SnapshotStateList<T>.addWithMainContext(item: T) =
    withContext(Dispatchers.Main) { add(item) }


fun <K, V> SnapshotStateMap<K, V>.swapMap(newMap: Map<K, V>) {
    clear()
    putAll(newMap)
}

suspend fun <K, V> SnapshotStateMap<K, V>.swapMapWithMainContext(newMap: Map<K, V>) =
    withContext(Dispatchers.Main) { this@swapMapWithMainContext.swapMap(newMap) }

fun <K, V> SnapshotStateMap<K, V>.swapMap(newMap: Map<K, V>, afterSwap: () -> Unit) {
    this.swapMap(newMap)
    afterSwap()
}

fun Date.formatPubTimeString(context: Context = BVApp.context): String {
    val temp = System.currentTimeMillis() - time
    return when {
        temp > 1000L * 60 * 60 * 24 -> SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(this)
        temp > 1000L * 60 * 60 -> context.getString(
            R.string.date_format_hours_age, temp / (1000 * 60 * 60)
        )

        temp > 1000L * 60 -> context.getString(
            R.string.date_format_minutes_age, temp / (1000 * 60)
        )

        else -> context.getString(R.string.date_format_just_now)
    }
}

fun Long.formatHourMinSec(): String {
    return if (this < 0L) {
        ""
    } else {
        val hours = TimeUnit.MILLISECONDS.toHours(this)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(this) - TimeUnit.HOURS.toMinutes(hours)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(this) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(this))

        if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }
}

fun String.removeHtmlTags(): String = HtmlCompat.fromHtml(
    this, HtmlCompat.FROM_HTML_MODE_LEGACY
).toString()

fun KeyEvent.isKeyDown(): Boolean = type == KeyEventType.KeyDown
fun KeyEvent.isKeyUp(): Boolean = type == KeyEventType.KeyUp
fun KeyEvent.isDpadUp(): Boolean = key == Key.DirectionUp
fun KeyEvent.isDpadDown(): Boolean = key == Key.DirectionDown
fun KeyEvent.isDpadLeft(): Boolean = key == Key.DirectionLeft
fun KeyEvent.isDpadRight(): Boolean = key == Key.DirectionRight

fun Int.stringRes(context: Context): String = context.getString(this)

fun LazyListState.isScrolledToEnd() =
    canScrollForward || firstVisibleItemIndex == 0 && firstVisibleItemScrollOffset == 0

fun LazyStaggeredGridState.isScrolledToEnd() =
    canScrollForward || firstVisibleItemIndex == 0 && firstVisibleItemScrollOffset == 0

fun LazyStaggeredGridState.getLane() =
    layoutInfo.visibleItemsInfo.maxOfOrNull { it.lane + 1 }

@Composable
fun LazyListState.OnBottomReached(
    loading: Boolean = false,
    loadMore: () -> Unit
) {
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
                ?: return@derivedStateOf true

            lastVisibleItem.index >= layoutInfo.totalItemsCount - 5
        }
    }

    LaunchedEffect(shouldLoadMore, loading) {
        snapshotFlow { shouldLoadMore.value }
            .collect {
                if (it && !loading) loadMore()
            }
    }
}

@Composable
fun LazyGridState.OnBottomReached(
    loading: Boolean = false,
    loadMore: () -> Unit
) {
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
                ?: return@derivedStateOf true

            lastVisibleItem.index >= layoutInfo.totalItemsCount - 5
        }
    }

    LaunchedEffect(shouldLoadMore, loading) {
        snapshotFlow { shouldLoadMore.value }
            .collect {
                if (it && !loading) loadMore()
            }
    }
}

@Composable
fun LazyStaggeredGridState.OnBottomReached(
    loading: Boolean = false,
    loadMore: () -> Unit
) {
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
                ?: return@derivedStateOf true

            lastVisibleItem.index >= layoutInfo.totalItemsCount - 5
        }
    }

    LaunchedEffect(shouldLoadMore, loading) {
        snapshotFlow { shouldLoadMore.value }
            .collect {
                if (it && !loading) loadMore()
            }
    }
}
