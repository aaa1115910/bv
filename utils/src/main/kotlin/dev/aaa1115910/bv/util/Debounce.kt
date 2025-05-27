package dev.aaa1115910.bv.util

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember

/**
 * 防抖函数类，用于限制函数的执行频率
 * 在指定的延迟时间内，如果多次调用，只执行最后一次
 * 
 * 用法示例：
 * ```kotlin
 * val debouncer = remember { Debouncer(500L) }
 * debouncer.debounce(scope) {
 *     // 执行操作
 * }
 * ```
 * 
 * @param delayTime 防抖延迟时间（毫秒）
 */
class Debouncer(
    private val delayTime: Long
) {
    private var debounceJob: Job? = null

    /**
     * 执行防抖操作
     * 
     * @param scope 协程作用域
     * @param action 要执行的操作
     */
    fun debounce(scope: CoroutineScope, action: suspend () -> Unit) {
        debounceJob?.cancel()
        debounceJob = scope.launch {
            delay(delayTime)
            action()
        }
    }

    /**
     * 取消当前的防抖任务
     */
    fun cancel() {
        debounceJob?.cancel()
        debounceJob = null
    }
}

/**
 * 创建一个防抖函数
 * 
 * 用法示例：
 * ```kotlin
 * val debounceAction = createDebouncer(500L) { scope ->
 *    // 执行操作
 * }
 * debounceAction(scope)
 * ```
 * 
 * @param delayTime 防抖延迟时间（毫秒）
 * @param action 要执行的操作
 * @return 返回一个可以被调用的防抖函数
 */
fun createDebouncer(
    delayTime: Long,
    action: suspend () -> Unit
): (CoroutineScope) -> Unit {
    val debouncer = Debouncer(delayTime)
    return { scope ->
        debouncer.debounce(scope, action)
    }
}

/**
 * 带参数的防抖函数
 * 
 * 用法示例：
 * ```kotlin
 * val debouncer = remember { ParameterizedDebouncer<String>(500L) }
 * debouncer.debounce(scope, "parameter") { parameter ->
 *    // 执行操作
 *    performSearch(parameter)
 * }
 * ```
 * 
 * @param T 参数类型
 * @param delayTime 防抖延迟时间（毫秒）
 */
class ParameterizedDebouncer<T>(
    private val delayTime: Long
) {
    private var debounceJob: Job? = null

    /**
     * 执行防抖操作
     * 
     * @param scope 协程作用域
     * @param parameter 传递给action的参数
     * @param action 要执行的操作
     */
    fun debounce(scope: CoroutineScope, parameter: T, action: suspend (T) -> Unit) {
        debounceJob?.cancel()
        debounceJob = scope.launch {
            delay(delayTime)
            action(parameter)
        }
    }

    /**
     * 取消当前的防抖任务
     */
    fun cancel() {
        debounceJob?.cancel()
        debounceJob = null
    }
}

/**
 * 创建一个带参数的防抖函数
 * 
 * 用法示例：
 * ```kotlin
 * val debounceAction = createParameterizedDebouncer<String>(500L) { parameter ->
 *    // 执行操作
 *    performSearch(parameter)
 * }
 * debounceAction(scope, "searchQuery")
 * ```
 * 
 * @param T 参数类型
 * @param delayTime 防抖延迟时间（毫秒）
 * @param action 要执行的操作
 * @return 返回一个可以被调用的防抖函数
 */
fun <T> createParameterizedDebouncer(
    delayTime: Long,
    action: suspend (T) -> Unit
): (CoroutineScope, T) -> Unit {
    val debouncer = ParameterizedDebouncer<T>(delayTime)
    return { scope, parameter ->
        debouncer.debounce(scope, parameter, action)
    }
}

/**
 * Compose 专用的防抖器创建函数，自动处理生命周期管理
 * 在组件销毁时自动清理防抖任务
 * 
 * 用法示例：
 * ```kotlin
 * @Composable
 * fun MyScreen() {
 *     val scope = rememberCoroutineScope()
 *     val debouncer = rememberDebouncer<String>(500L)
 *     
 *     debouncer.debounce(scope, "parameter") { parameter ->
 *         // 执行操作
 *     }
 * }
 * ```
 * 
 * @param T 参数类型
 * @param delayTime 防抖延迟时间（毫秒）
 * @return 返回一个自动管理生命周期的防抖器
 */
@Composable
fun <T> rememberDebouncer(delayTime: Long): ParameterizedDebouncer<T> {
    val debouncer = remember { ParameterizedDebouncer<T>(delayTime) }
    
    // 在组件销毁时自动清理
    DisposableEffect(debouncer) {
        onDispose {
            debouncer.cancel()
        }
    }
    
    return debouncer
}

/**
 * Compose 专用的简单防抖器创建函数
 * 
 * 用法示例：
 * ```kotlin
 * @Composable
 * fun MyScreen() {
 *     val scope = rememberCoroutineScope()
 *     val debouncer = rememberSimpleDebouncer(500L)
 *     
 *     debouncer.debounce(scope) {
 *         // 执行操作
 *     }
 * }
 * ```
 * 
 * @param delayTime 防抖延迟时间（毫秒）
 * @return 返回一个自动管理生命周期的防抖器
 */
@Composable
fun rememberSimpleDebouncer(delayTime: Long): Debouncer {
    val debouncer = remember { Debouncer(delayTime) }
    
    // 在组件销毁时自动清理
    DisposableEffect(debouncer) {
        onDispose {
            debouncer.cancel()
        }
    }
    
    return debouncer
}
