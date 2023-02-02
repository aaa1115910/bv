package dev.aaa1115910.bv.entity.carddata

import dev.aaa1115910.bv.util.formatMinSec

data class VideoCardData(
    val avid: Int,
    val title: String,
    val cover: String,
    val upName: String,
    val reason: String = "",
    val play: Int? = null,
    var playString: String = "",
    val danmaku: Int? = null,
    var danmakuString: String = "",
    val time: Long? = null,
    var timeString: String = ""
) {
    init {
        play?.let {
            if (it >= 10000) playString = "${it / 10000}万"
        }
        danmaku?.let {
            if (it >= 10000) danmakuString = "${it / 10000}万"
        }
        time?.let {
            timeString = it.formatMinSec()
        }
    }
}
