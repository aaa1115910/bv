package dev.aaa1115910.bv.util

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.focus.FocusRequester
import androidx.core.text.HtmlCompat
import dev.aaa1115910.bv.BVApp
import dev.aaa1115910.bv.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit

fun String.toast(context: Context, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, this, duration).show()
}

fun Int.toast(context: Context, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, context.getText(this), duration).show()
}

fun <T> SnapshotStateList<T>.swapList(newList: List<T>) {
    clear()
    addAll(newList)
}

suspend fun <T> SnapshotStateList<T>.swapList(
    newList: List<T>,
    delay: Long,
    afterSwap: () -> Unit
) {
    withContext(Dispatchers.Main) {
        this@swapList.swapList(newList)
    }
    delay(delay)
    afterSwap()
}

fun <K, V> SnapshotStateMap<K, V>.swapMap(newMap: Map<K, V>) {
    clear()
    putAll(newMap)
}

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

fun Long.formatMinSec(): String {
    return if (this < 0L) {
        "..."
    } else {
        String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(this),
            TimeUnit.MILLISECONDS.toSeconds(this) -
                    TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(this)
                    )
        )
    }
}

/**
 * 改进的请求焦点的方法，失败后等待 100ms 后重试
 */
fun FocusRequester.requestFocus(scope: CoroutineScope) {
    scope.launch(Dispatchers.Default) {
        runCatching {
            requestFocus()
        }.onFailure {
            delay(100)
            requestFocus()
        }
    }
}

fun String.removeHtmlTags(): String = HtmlCompat.fromHtml(
    this, HtmlCompat.FROM_HTML_MODE_LEGACY
).toString()