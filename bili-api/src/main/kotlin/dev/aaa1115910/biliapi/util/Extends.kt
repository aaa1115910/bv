package dev.aaa1115910.biliapi.util

fun String.convertStringTimeToSeconds(): Int {
    // 部分稿件没有时长：Web 接口返回 "NaN:NaN:NaN"，App 接口返回空字符串
    if (isBlank() || startsWith("NaN")) return 0
    val parts = this.split(":")
    if (parts.size < 2) return 0
    val hours = if (parts.size == 3) parts[0].toIntOrNull() ?: 0 else 0
    val minutes = parts[parts.size - 2].toIntOrNull() ?: 0
    val seconds = parts[parts.size - 1].toIntOrNull() ?: 0
    return (hours * 3600) + (minutes * 60) + seconds
}

fun Long.toBv(): String = AvBvConverter.av2bv(this)
fun String.toAv(): Long = AvBvConverter.bv2av(this)