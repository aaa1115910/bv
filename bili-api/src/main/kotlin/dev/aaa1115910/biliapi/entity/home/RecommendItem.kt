package dev.aaa1115910.biliapi.entity.home

import dev.aaa1115910.biliapi.util.convertStringTimeToSeconds

data class RecommendItem(
    val aid: Long,
    val bvid: String,
    val title: String,
    val cover: String,
    val author: String,
    val play: Int,
    val danmaku: Int,
    val duration: Int,
    val idx: Int
) {
    companion object {
        fun fromRcmdItem(rcmdItem: dev.aaa1115910.biliapi.http.entity.home.RcmdIndexData.RcmdItem) =
            RecommendItem(
                aid = rcmdItem.args.aid ?: 0,
                bvid = "",
                title = rcmdItem.title,
                cover = rcmdItem.cover,
                author = rcmdItem.args.upName ?: "",
                play = with(rcmdItem.coverLeftText1) {
                    runCatching {
                        if (this.endsWith("万")) {
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

        fun fromRcmdItem(rcmdItem: dev.aaa1115910.biliapi.http.entity.home.RcmdTopData.RcmdItem) =
            RecommendItem(
                aid = rcmdItem.id,
                bvid = rcmdItem.bvid,
                title = rcmdItem.title,
                cover = rcmdItem.pic,
                author = rcmdItem.owner?.name ?: "",
                play = rcmdItem.stat?.view ?: -1,
                danmaku = rcmdItem.stat?.danmaku ?: -1,
                duration = rcmdItem.duration,
                idx = -1
            )
    }
}
