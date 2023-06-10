package dev.aaa1115910.biliapi.entity.video.season

import dev.aaa1115910.biliapi.entity.video.Dimension

data class Episode(
    val id: Int,
    val aid: Int,
    val bvid: String,
    val cid: Int,
    val title: String,
    val cover: String,
    val duration: Int,
    val dimension: Dimension
) {
    companion object {
        fun fromEpisode(episode: bilibili.app.view.v1.Episode) = Episode(
            id = episode.id.toInt(),
            aid = episode.aid.toInt(),
            bvid = episode.bvid,
            cid = episode.cid.toInt(),
            title = episode.title,
            cover = episode.cover,
            duration = episode.page.duration.toInt(),
            dimension = Dimension.fromDimension(episode.page.dimension)
        )

        fun fromEpisode(episode: dev.aaa1115910.biliapi.http.entity.video.UgcSeason.Section.Episode) =
            Episode(
                id = episode.id,
                aid = episode.aid,
                bvid = episode.bvid,
                cid = episode.cid,
                title = episode.title,
                cover = episode.arc.pic,
                duration = episode.arc.duration,
                dimension = Dimension.fromDimension(episode.page.dimension)
            )
    }
}
