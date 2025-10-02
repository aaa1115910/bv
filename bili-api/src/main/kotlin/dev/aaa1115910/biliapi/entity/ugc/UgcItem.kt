package dev.aaa1115910.biliapi.entity.ugc

import dev.aaa1115910.biliapi.http.entity.home.RcmdIndexData
import dev.aaa1115910.biliapi.http.entity.home.RcmdTopData
import dev.aaa1115910.biliapi.util.convertStringTimeToSeconds
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

data class UgcItem(
    val aid: Long,
    val bvid: String = "",
    val title: String,
    val cover: String,
    val author: String,
    val authorId: Long = 0,
    val authorFace: String = "",
    val play: Int,
    val danmaku: Int,
    val duration: Int,
    val idx: Int = -1,
    val pubTime: String? = null,
) {
    companion object {
        fun fromRcmdItem(rcmdItem: RcmdIndexData.RcmdItem) =
            UgcItem(
                aid = rcmdItem.args.aid ?: 0,
                title = rcmdItem.title!!,
                cover = rcmdItem.cover!!,
                author = rcmdItem.args.upName ?: "",
                authorId = rcmdItem.args.upId ?: 0,
//                authorFace = rcmdItem.args.upFace ?: "",
                play = with(rcmdItem.coverLeftText1) {
                    runCatching {
                        if (this!!.endsWith("万")) {
                            (this.substring(0, this.length - 1).toDouble() * 10000).toInt()
                        } else {
                            this.toInt()
                        }
                    }.getOrDefault(-1)
                },
                danmaku = with(rcmdItem.coverLeftText2) {
                    if (this == null) return@with -1
                    runCatching {
                        if (this.endsWith("万")) {
                            (this.substring(0, this.length - 1).toDouble() * 10000).toInt()
                        } else {
                            this.toInt()
                        }
                    }.getOrDefault(-1)
                },
                duration = rcmdItem.coverRightText?.convertStringTimeToSeconds() ?: 0,
                idx = rcmdItem.idx
            )

        fun fromRcmdItem(rcmdItem: RcmdTopData.RcmdItem) =
            UgcItem(
                aid = rcmdItem.id,
                bvid = rcmdItem.bvid,
                title = rcmdItem.title,
                cover = rcmdItem.pic,
                author = rcmdItem.owner?.name ?: "",
                authorId = rcmdItem.owner?.mid ?: 0,
                authorFace = rcmdItem.owner?.face ?: "",
                play = rcmdItem.stat?.view ?: -1,
                danmaku = rcmdItem.stat?.danmaku ?: -1,
                duration = rcmdItem.duration,
                pubTime = rcmdItem.pubdate.smartDate
            )

        fun fromVideoInfo(videoInfo: dev.aaa1115910.biliapi.http.entity.video.VideoInfo) =
            UgcItem(
                aid = videoInfo.aid,
                title = videoInfo.title,
                duration = videoInfo.duration,
                author = videoInfo.owner.name,
                authorId = videoInfo.owner.mid,
                authorFace = videoInfo.owner.face,
                cover = videoInfo.pic,
                play = videoInfo.stat.view,
                danmaku = videoInfo.stat.danmaku,
                pubTime = videoInfo.pubdate.smartDate
            )

        fun fromSmallCoverV5(card: bilibili.app.card.v1.SmallCoverV5) =
            UgcItem(
                aid = card.base.param.toLong(),
                title = card.base.title,
                duration = convertStringTimeToSeconds(card.coverRightText1),
                author = card.rightDesc1,
//                authorId = card.base.upId,
//                authorFace = card.base.upFace,
                cover = card.base.cover,
                play = -1,
                danmaku = -1,
                idx = card.base.idx.toInt()
            )

        fun fromRegionDynamicListItem(item: dev.aaa1115910.biliapi.http.entity.region.RegionDynamicList.Item) =
            UgcItem(
                aid = item.param.toLong(),
                title = item.title,
                duration = item.duration,
                author = item.name,
//                authorId = item.mid,
                authorFace = item.face,
                cover = item.cover,
                play = item.play ?: -1,
                danmaku = item.danmaku ?: -1,
                pubTime = item.pubDate.smartDate
            )

        fun fromRegionRcmdArchive(archive: dev.aaa1115910.biliapi.http.entity.region.RegionFeedRcmd.Archive) =
            UgcItem(
                aid = archive.aid,
                title = archive.title,
                duration = archive.duration,
                author = archive.author.name,
                authorId = archive.author.mid,
                cover = archive.cover,
                play = archive.stat.view,
                danmaku = archive.stat.danmaku,
                pubTime = archive.pubdate.toSmartDate()
            )
    }
}

private fun convertStringTimeToSeconds(time: String): Int {
    val parts = time.split(":")
    val hours = if (parts.size == 3) parts[0].toInt() else 0
    val minutes = parts[parts.size - 2].toInt()
    val seconds = parts[parts.size - 1].toInt()
    return (hours * 3600) + (minutes * 60) + seconds
}

/**
 * 智能日期格式化 (兼容低版本 Android)
 * @param timeZone 时区 (默认系统时区)
 */
fun Long.toSmartDate(timeZone: TimeZone = TimeZone.getDefault()): String? {
    if (this <= 0) return null
    try {
        // 自动识别秒级或毫秒级时间戳
        // 秒级时间戳通常小于等于10位数，目前直到2286年都是10位数
        // 毫秒级时间戳通常为13位数
        val timeInMillis = if (this < 10000000000L) this * 1000L else this

        // 创建日历实例
        val cal = Calendar.getInstance(timeZone).apply {
            this.timeInMillis = timeInMillis
        }

        // 获取当前年份
        val currentYear = Calendar.getInstance(timeZone).get(Calendar.YEAR)

        // 动态格式选择
        val pattern = if (cal.get(Calendar.YEAR) == currentYear) {
            "M-d H:mm"
        } else {
            "yyyy-M-d"
        }

        // 线程安全的日期格式化
        return SimpleDateFormat(pattern, Locale.CHINESE).apply {
            this.timeZone = timeZone
        }.format(cal.time)
    } catch (e: Exception) {
        return null
    }
}

val Int.smartDate: String?
    get() = this.toLong().toSmartDate()