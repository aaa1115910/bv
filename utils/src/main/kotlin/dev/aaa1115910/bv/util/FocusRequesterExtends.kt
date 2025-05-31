package dev.aaa1115910.bv.util

import androidx.compose.ui.focus.FocusRequester
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 改进的请求焦点的方法，确保线程安全
 * 使用Immediate dispatcher避免不必要的调度延迟
 */
fun FocusRequester.requestFocus(scope: CoroutineScope) {
    scope.launch(Dispatchers.Main.immediate) {
        try {
            requestFocus()
        } catch (e: Exception) {
            // 如果第一次失败，等待一帧再重试
            delay(16) // 约1帧的时间
            try {
                requestFocus()
            } catch (retryException: Exception) {
                // 记录日志而不是忽略异常
                println("Focus request failed after retry: ${retryException.message}")
            }
        }
    }
}
