package dev.aaa1115910.bv.util

import androidx.compose.ui.focus.FocusRequester
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 改进的请求焦点的方法，失败后等待 100ms 后重试
 */
fun FocusRequester.requestFocus(scope: CoroutineScope) {
    scope.launch(Dispatchers.Default) {
        runCatching {
            requestFocus()
        }.onFailure {
            delay(100)
            runCatching { requestFocus() }
        }
    }
}