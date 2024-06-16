package dev.aaa1115910.biliapi.entity.video

import bilibili.app.view.v1.ReqUser
import bilibili.app.view.v1.ViewReply
import bilibili.app.view.v1.ugcSeasonOrNull
import dev.aaa1115910.biliapi.entity.user.Author
import dev.aaa1115910.biliapi.entity.video.season.UgcSeason
import dev.aaa1115910.biliapi.http.entity.video.VideoStat
import java.util.Date

data class VideoDetail(
    val bvid: String,
    val aid: Long,
    val cid: Long,
    val cover: String,
    val title: String,
    val publishDate: Date,
    val description: String,
    val stat: Stat,
    val author: Author,
    val pages: List<VideoPage>,
    val ugcSeason: UgcSeason?,
    val relatedVideos: List<RelatedVideo>,
    val redirectToEp: Boolean,
    val epid: Int?,
    val argueTip: String?,
    val tags: List<Tag>,
    val userActions: UserActions,
    var history: History,
    var playerIcon: PlayerIcon? = null
) {
    companion object {
        fun fromViewReply(viewReply: ViewReply): VideoDetail {
            if (!viewReply.hasActivitySeason()) {
                return VideoDetail(
                    bvid = viewReply.bvid,
                    aid = viewReply.arc.aid,
                    cid = viewReply.arc.firstCid,
                    cover = viewReply.arc.pic,
                    title = viewReply.arc.title,
                    publishDate = Date(viewReply.arc.pubdate * 1000L),
                    description = viewReply.arc.desc,
                    stat = Stat.fromStat(viewReply.arc.stat),
                    author = Author.fromAuthor(viewReply.arc.author),
                    pages = viewReply.pagesList.map { VideoPage.fromViewPage(it) },
                    ugcSeason = viewReply.ugcSeasonOrNull?.let { UgcSeason.fromUgcSeason(it) },
                    relatedVideos = viewReply.relatesList.map { RelatedVideo.fromRelate(it) },
                    redirectToEp = viewReply.arc.redirectUrl.contains("ep"),
                    epid = runCatching {
                        viewReply.arc.redirectUrl.split("ep", "?")[1].toInt()
                    }.getOrNull(),
                    argueTip = viewReply.argueMsg.takeIf { it.isNotEmpty() },
                    tags = viewReply.tagList.map { Tag.fromTag(it) },
                    userActions = UserActions.fromReqUser(viewReply.reqUser),
                    history = History.fromHistory(viewReply.history),
                    playerIcon = viewReply.playerIcon?.let { PlayerIcon.fromPlayerIcon(it) }
                )
            } else {
                return VideoDetail(
                    bvid = viewReply.activitySeason.bvid,
                    aid = viewReply.activitySeason.arc.aid,
                    cid = viewReply.activitySeason.arc.firstCid,
                    cover = viewReply.activitySeason.arc.pic,
                    title = viewReply.activitySeason.arc.title,
                    publishDate = Date(viewReply.activitySeason.arc.pubdate * 1000L),
                    description = viewReply.activitySeason.arc.desc,
                    stat = Stat.fromStat(viewReply.activitySeason.arc.stat),
                    author = Author.fromAuthor(viewReply.activitySeason.arc.author),
                    pages = viewReply.activitySeason.pagesList.map { VideoPage.fromViewPage(it) },
                    ugcSeason = viewReply.activitySeason.ugcSeasonOrNull?.let {
                        UgcSeason.fromUgcSeason(
                            it
                        )
                    },
                    relatedVideos = viewReply.relatesList.map { RelatedVideo.fromRelate(it) },
                    redirectToEp = viewReply.activitySeason.arc.redirectUrl.contains("ep"),
                    epid = runCatching {
                        viewReply.activitySeason.arc.redirectUrl.split("ep", "?")[1].toInt()
                    }.getOrNull(),
                    argueTip = viewReply.activitySeason.argueMsg.takeIf { it.isNotEmpty() },
                    tags = viewReply.tagList.map { Tag.fromTag(it) },
                    userActions = UserActions.fromReqUser(viewReply.activitySeason.reqUser),
                    history = History.fromHistory(viewReply.activitySeason.history),
                    playerIcon = viewReply.activitySeason.playerIcon?.let {
                        PlayerIcon.fromPlayerIcon(
                            it
                        )
                    }
                )
            }
        }

        fun fromVideoDetail(videoDetail: dev.aaa1115910.biliapi.http.entity.video.VideoDetail) =
            VideoDetail(
                bvid = videoDetail.view.bvid,
                aid = videoDetail.view.aid,
                cid = videoDetail.view.cid,
                cover = videoDetail.view.pic,
                title = videoDetail.view.title,
                publishDate = Date(videoDetail.view.pubdate * 1000L),
                description = videoDetail.view.desc,
                stat = Stat.fromVideoStat(videoDetail.view.stat),
                author = Author.fromVideoOwner(videoDetail.view.owner),
                pages = videoDetail.view.pages.map { VideoPage.fromVideoPage(it) },
                ugcSeason = videoDetail.view.ugcSeason?.let { UgcSeason.fromUgcSeason(it) },
                relatedVideos = videoDetail.related.map { RelatedVideo.fromRelate(it) },
                redirectToEp = videoDetail.view.redirectUrl?.contains("ep") ?: false,
                epid = videoDetail.view.redirectUrl?.split("ep", "?")?.get(1)?.toInt(),
                argueTip = videoDetail.view.stat.argueMsg.takeIf { it.isNotEmpty() },
                tags = videoDetail.tags.map { Tag.fromTag(it) },
                userActions = UserActions(),
                history = History(0, 0),
                playerIcon = null
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

    data class History(
        val progress: Int,
        val lastPlayedCid: Long
    ) {
        companion object {
            fun fromHistory(history: bilibili.app.view.v1.History) = History(
                progress = history.progress.toInt(),
                lastPlayedCid = history.cid
            )
        }
    }

    data class PlayerIcon(
        val idle: String,
        val moving: String
    ) {
        companion object {
            fun fromPlayerIcon(playerIcon: dev.aaa1115910.biliapi.http.entity.video.VideoMoreInfo.PlayerIcon?) =
                playerIcon?.let {
                    PlayerIcon(
                        idle = playerIcon.url2,
                        moving = playerIcon.url1
                    )
                }

            fun fromPlayerIcon(playerIcon: bilibili.app.view.v1.PlayerIcon) = PlayerIcon(
                idle = playerIcon.url2,
                moving = playerIcon.url1
            )
        }
    }
}

data class UserActions(
    var like: Boolean = false,
    var favorite: Boolean = false,
    var coin: Boolean = false,
    var dislike: Boolean = false
) {
    companion object {
        fun fromReqUser(reqUser: ReqUser): UserActions {
            return UserActions(
                like = reqUser.like == 1,
                favorite = reqUser.favorite == 1,
                coin = reqUser.coin == 1,
                dislike = reqUser.dislike == 1
            )
        }
    }
}