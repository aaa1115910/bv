package dev.aaa1115910.biliapi.entity.video.season

import dev.aaa1115910.biliapi.entity.video.Dimension

/**
 * 剧集视频
 *
 * @param id 剧集id
 * @param aid av号
 * @param bvid bv号
 * @param cid cid
 * @param epid epid
 * @param title 标题 在投稿视频中显示为分 p 标题；
 * 在剧集中，如果存在完整标题，则该标题内容为纯数字，用于显示“第 x 话”，若完整标题内容为空时，显示该分集标题，例如“正片”“中文”
 * @param longTitle 完整标题，仅在剧集中存在，如果不存在完整标题，则该标题为空
 * @param cover 封面
 * @param duration 时长
 * @param dimension 分辨率
 */
data class Episode(
    val id: Int,
    val aid: Long,
    val bvid: String,
    val cid: Long,
    val epid: Int? = null,
    val title: String,
    val longTitle: String,
    val cover: String,
    val duration: Int,
    val dimension: Dimension?
) {
    companion object {
        fun fromEpisode(episode: bilibili.app.view.v1.Episode) = Episode(
            id = episode.id.toInt(),
            aid = episode.aid,
            bvid = episode.bvid,
            cid = episode.cid,
            title = episode.title,
            longTitle = episode.title,
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
                longTitle = episode.title,
                cover = episode.arc.pic,
                duration = episode.arc.duration,
                dimension = Dimension.fromDimension(episode.page.dimension)
            )

        fun fromEpisode(episode: dev.aaa1115910.biliapi.http.entity.season.Episode) = Episode(
            id = episode.id,
            aid = episode.aid,
            cid = episode.cid,
            bvid = episode.bvid,
            cover = episode.cover,
            title = episode.title,
            longTitle = episode.longTitle,
            epid = episode.epId,
            duration = episode.duration,
            dimension = episode.dimension?.let { Dimension.fromDimension(it) }
        )
    }
}
