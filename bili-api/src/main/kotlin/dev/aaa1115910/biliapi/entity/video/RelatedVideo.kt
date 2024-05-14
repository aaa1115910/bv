package dev.aaa1115910.biliapi.entity.video

import bilibili.app.view.v1.authorOrNull
import dev.aaa1115910.biliapi.entity.user.Author

data class RelatedVideo(
    val aid: Long,
    val cover: String,
    val title: String,
    val duration: Int,
    val author: Author?,
    val jumpToSeason: Boolean,
    val epid: Int?,
    val view: Int,
    val danmaku: Int
) {
    companion object {
        fun fromRelate(relate: bilibili.app.view.v1.Relate) = RelatedVideo(
            aid = relate.aid,
            cover = relate.pic,
            title = relate.title,
            duration = relate.duration.toInt(),
            author = relate.authorOrNull?.let { Author.fromAuthor(it) }
                ?: relate.desc?.let { Author(0, it, "") },
            jumpToSeason = relate.goto.needJumpToSeason(),
            epid = if (relate.goto.needJumpToSeason()) relate.uri.substringBeforeLast("?")
                .substringAfterLast("/ep").toInt() else null,
            view = relate.stat.view,
            danmaku = relate.stat.danmaku
        )

        fun fromRelate(relate: dev.aaa1115910.biliapi.http.entity.video.RelatedVideoInfo) =
            RelatedVideo(
                aid = relate.aid,
                cover = relate.pic,
                title = relate.title,
                duration = relate.duration,
                author = relate.owner.let { Author.fromVideoOwner(it) },
                jumpToSeason = false,
                epid = null,
                view = relate.stat.view,
                danmaku = relate.stat.danmaku
            )
    }
}

private fun String.needJumpToSeason() = this.contains("bangumi_ep") || this.contains("special")
