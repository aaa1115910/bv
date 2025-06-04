package dev.aaa1115910.bv.util

import androidx.compose.runtime.snapshots.SnapshotStateList
import java.util.ArrayDeque

/**
 * 高效地替换列表内容，尽量复用现有对象以减少内存分配
 * @param newList 新列表内容
 */
fun <T> SnapshotStateList<T>.swapList(newList: List<T>) {
    if (this.isEmpty()) {
        // 空列表直接添加全部
        addAll(newList)
        return
    }
    
    if (newList.isEmpty()) {
        // 新列表为空则清空
        clear()
        return
    }

    // 计算需要实际更新的部分
    val currentSize = this.size
    val newSize = newList.size
    val commonSize = minOf(currentSize, newSize)
    
    // 1. 更新共同部分（复用已有对象）
    for (i in 0 until commonSize) {
        this[i] = newList[i]
    }
    
    // 2. 如果新列表更长，添加额外项
    if (newSize > currentSize) {
        addAll(newList.subList(currentSize, newSize))
    }
    // 3. 如果旧列表更长，移除多余项
    else if (currentSize > newSize) {
        repeat(currentSize - newSize) {
            this.removeAt(newSize)
        }
    }
}
