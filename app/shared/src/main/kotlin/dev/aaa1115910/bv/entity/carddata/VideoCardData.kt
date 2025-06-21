package dev.aaa1115910.bv.entity.carddata

import dev.aaa1115910.bv.util.formatHourMinSec

data class VideoCardData(
    val avid: Long,
    val title: String,
    val cover: String,
    val upName: String,
    val reason: String = "",
    val play: Int? = null,
    var playString: String = "",
    val danmaku: Int? = null,
    var danmakuString: String = "",
    val time: Long? = null,
    var timeString: String = "",
    val jumpToSeason: Boolean = false,
    val epId: Int? = null
) {
    init {
        play?.let {
            playString = if (it >= 10000) "${it / 10000}万" else "$it"
        }
        danmaku?.let {
            danmakuString = if (it >= 10000) "${it / 10000}万" else "$it"
        }
        time?.let {
            timeString = if (it > 0) it.formatHourMinSec() else ""
        }
    }
}
