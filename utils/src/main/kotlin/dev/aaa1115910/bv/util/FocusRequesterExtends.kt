package dev.aaa1115910.bv.util

import androidx.compose.ui.focus.FocusRequester
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 改进的请求焦点的方法，失败后等待 100ms 后重试
 *
 * 必须运行在主线程上：`FocusRequester.requestFocus()` 会读写 Compose 的快照
 * (snapshot) 状态，而快照状态只能在创建 `SnapshotStateObserver` 的线程（即主线程）
 * 上访问，否则会抛出
 * `IllegalArgumentException: Detected multithreaded access to SnapshotStateObserver`
 * 直接崩溃（见 #132：在设置页按左右键有概率闪退）。
 *
 * 因此这里不再显式指定 `Dispatchers.Default`，而是沿用传入 `scope` 的调度器
 * （调用方基本都是 `rememberCoroutineScope()`，即主线程）。
 */
fun FocusRequester.requestFocus(scope: CoroutineScope) {
    scope.launch {
        runCatching {
            requestFocus()
        }.onFailure {
            delay(100)
            runCatching { requestFocus() }
        }
    }
}