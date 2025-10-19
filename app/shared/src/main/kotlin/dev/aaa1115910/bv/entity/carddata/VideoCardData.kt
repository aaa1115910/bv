package dev.aaa1115910.bv.entity.carddata

import dev.aaa1115910.bv.util.formatHourMinSec
import java.text.SimpleDateFormat

data class VideoCardData(
    val avid: Long,
    val title: String,
    val cover: String,
    val upName: String,
    val upId: Long = 0,
    val upFace: String = "",
    val reason: String = "",
    val play: Long? = null,
    var playString: String = "",
    val danmaku: Int? = null,
    var danmakuString: String = "",
    val time: Long? = null,
    var timeString: String = "",
    val jumpToSeason: Boolean = false,
    val epId: Int? = null,
    val pubTime: String? = null,
    // var pubTimeString: String = "",
) {
    init {
        play?.let {
            playString = if (it < 0) "" else if (it >= 10000) "${it / 10000}万" else "$it"
        }
        danmaku?.let {
            danmakuString = if (it < 0) "" else if (it >= 10000) "${it / 10000}万" else "$it"
        }
        time?.let {
            timeString = if (it > 0) it.formatHourMinSec() else ""
        }
        // pubTime?.let {
        //     pubTimeString =
        //         if (it > 0) SimpleDateFormat("yyyy-MM-dd").format(java.util.Date(it * 1000L)) else ""
        // }
    }
}
