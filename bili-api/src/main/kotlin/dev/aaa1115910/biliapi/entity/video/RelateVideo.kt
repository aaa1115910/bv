package dev.aaa1115910.biliapi.entity.video

import dev.aaa1115910.biliapi.entity.user.Author

data class RelateVideo(
    val aid: Int,
    val cover: String,
    val title: String,
    val duration: Int,
    val author: Author?,
    val goToBangumiEp: Boolean,
    val epid: Int?
) {
    companion object {
        fun fromRelate(relate: bilibili.app.view.v1.Relate) = RelateVideo(
            aid = relate.aid.toInt(),
            cover = relate.pic,
            title = relate.title,
            duration = relate.duration.toInt(),
            author = relate.author?.let { Author.fromAuthor(it) },
            goToBangumiEp = relate.goto == "bangumi_ep",
            epid = if (relate.goto == "bangumi_ep") relate.uri.substringBeforeLast("?")
                .substringAfterLast("/ep").toInt() else null
        )

        fun fromRelate(relate: dev.aaa1115910.biliapi.http.entity.video.RelatedVideoInfo) =
            RelateVideo(
                aid = relate.aid,
                cover = relate.pic,
                title = relate.title,
                duration = relate.duration,
                author = relate.owner.let { Author.fromVideoOwner(it) },
                goToBangumiEp = false,
                epid = null
            )
    }
}
