package dev.aaa1115910.biliapi.entity.rank

data class PopularVideoData(
    val list: List<PopularVideo>,
    val nextPage: PopularVideoPage,
    val noMore: Boolean
)

data class PopularVideoPage(
    val nextWebPageSize: Int = 20,
    val nextWebPageNumber: Int = 1,
    val nextAppIndex: Int = 0,
)

data class PopularVideo(
    val aid: Int,
    val title: String,
    val duration: Int,
    val author: String,
    val cover: String,
    val play: Int,
    val danmaku: Int,
    val idx: Int,
) {
    companion object {
        fun fromVideoInfo(videoInfo: dev.aaa1115910.biliapi.http.entity.video.VideoInfo) =
            PopularVideo(
                aid = videoInfo.aid,
                title = videoInfo.title,
                duration = videoInfo.duration,
                author = videoInfo.owner.name,
                cover = videoInfo.pic,
                play = videoInfo.stat.view,
                danmaku = videoInfo.stat.danmaku,
                idx = -1
            )

        fun fromSmallCoverV5(card: bilibili.app.card.v1.SmallCoverV5) =
            PopularVideo(
                aid = card.base.param.toInt(),
                title = card.base.title,
                duration = convertStringTimeToSeconds(card.coverRightText1),
                author = card.rightDesc1,
                cover = card.base.cover,
                play = -1,
                danmaku = -1,
                idx = card.base.idx.toInt()
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