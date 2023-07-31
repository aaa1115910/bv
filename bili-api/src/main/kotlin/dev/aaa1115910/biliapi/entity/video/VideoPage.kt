package dev.aaa1115910.biliapi.entity.video

import bilibili.app.view.v1.ViewPage

data class VideoPage(
    var cid: Int,
    val index: Int,
    val title: String,
    val duration: Int,
    val dimension: Dimension
) {
    companion object {
        fun fromViewPage(viewPage: ViewPage) = VideoPage(
            cid = viewPage.page.cid.toInt(),
            index = viewPage.page.page,
            title = viewPage.page.part,
            duration = viewPage.page.duration.toInt(),
            dimension = Dimension.fromDimension(viewPage.page.dimension)
        )

        fun fromVideoPage(videoPage: dev.aaa1115910.biliapi.http.entity.video.VideoPage) =
            VideoPage(
                cid = videoPage.cid,
                index = videoPage.page,
                title = videoPage.part,
                duration = videoPage.duration,
                dimension = Dimension.fromDimension(videoPage.dimension)
            )
    }
}
