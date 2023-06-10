package dev.aaa1115910.biliapi.entity.video

import bilibili.app.view.v1.ViewReply
import bilibili.app.view.v1.ugcSeasonOrNull
import dev.aaa1115910.biliapi.entity.user.Author
import dev.aaa1115910.biliapi.entity.video.season.UgcSeason
import dev.aaa1115910.biliapi.http.entity.video.VideoInfo
import dev.aaa1115910.biliapi.http.entity.video.VideoStat
import java.util.Date

data class VideoDetail(
    val bvid: String,
    val aid: Int,
    val cid: Int,
    val cover: String,
    val title: String,
    val publishDate: Date,
    val description: String,
    val stat: Stat,
    val author: Author,
    val pages: List<VideoPage>,
    val ugcSeason: UgcSeason?,
    val relateVideos: MutableList<RelateVideo>,
    val redirectToEp: Boolean,
    val epid: Int?,
    val argueTip: String?
) {
    companion object {
        fun fromViewReply(viewReply: ViewReply) = VideoDetail(
            bvid = viewReply.bvid,
            aid = viewReply.arc.aid.toInt(),
            cid = viewReply.arc.firstCid.toInt(),
            cover = viewReply.arc.pic,
            title = viewReply.arc.title,
            publishDate = Date(viewReply.arc.pubdate * 1000L),
            description = viewReply.arc.desc,
            stat = Stat.fromStat(viewReply.arc.stat),
            author = Author.fromAuthor(viewReply.arc.author),
            pages = viewReply.pagesList.map { VideoPage.fromViewPage(it) },
            ugcSeason = viewReply.ugcSeasonOrNull?.let { UgcSeason.fromUgcSeason(it) },
            relateVideos = viewReply.relatesList.map { RelateVideo.fromRelate(it) }.toMutableList(),
            redirectToEp = viewReply.arc.redirectUrl.contains("ep"),
            epid = viewReply.arc.redirectUrl.split("ep")[1].toInt(),
            argueTip = viewReply.argueMsg.takeIf { it.isNotEmpty() }
        )

        fun fromVideoInfo(videoInfo: VideoInfo) = VideoDetail(
            bvid = videoInfo.bvid,
            aid = videoInfo.aid,
            cid = videoInfo.cid,
            cover = videoInfo.pic,
            title = videoInfo.title,
            publishDate = Date(videoInfo.pubdate * 1000L),
            description = videoInfo.desc,
            stat = Stat.fromVideoStat(videoInfo.stat),
            author = Author.fromVideoOwner(videoInfo.owner),
            pages = videoInfo.pages.map { VideoPage.fromVideoPage(it) },
            ugcSeason = videoInfo.ugcSeason?.let { UgcSeason.fromUgcSeason(it) },
            relateVideos = ArrayList(),
            redirectToEp = videoInfo.redirectUrl?.contains("ep") ?: false,
            epid = videoInfo.redirectUrl?.split("ep")?.get(1)?.toInt(),
            argueTip = videoInfo.stat.argueMsg.takeIf { it.isNotEmpty() }
        )
    }

    data class Stat(
        val view: Int,
        val danmaku: Int,
        val reply: Int,
        val favorite: Int,
        val coin: Int,
        val share: Int,
        val like: Int,
        val historyRank: Int
    ) {
        companion object {
            fun fromStat(stat: bilibili.app.archive.v1.Stat) = Stat(
                view = stat.view,
                danmaku = stat.danmaku,
                reply = stat.reply,
                favorite = stat.fav,
                coin = stat.coin,
                share = stat.share,
                like = stat.like,
                historyRank = stat.hisRank
            )

            fun fromVideoStat(videoStat: VideoStat) = Stat(
                view = videoStat.view,
                danmaku = videoStat.danmaku,
                reply = videoStat.reply,
                favorite = videoStat.favorite,
                coin = videoStat.coin,
                share = videoStat.share,
                like = videoStat.like,
                historyRank = videoStat.hisRank
            )
        }
    }
}