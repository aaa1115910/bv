package dev.aaa1115910.biliapi.entity.danmaku

data class DanmakuMaskSegment(
    val range: LongRange,
    val frames: List<DanmakuMaskFrame>,
)

data class DanmakuMaskFrame(
    val range:LongRange,
    val svg: String
)