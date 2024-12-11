package dev.aaa1115910.biliapi.util

import io.ktor.http.Url

object UrlUtil {
    fun isVideoUrl(url: String): Boolean {
        return url.startsWith("bilibili://video/")
                || url.startsWith("https://www.bilibili.com/video/")
    }

    fun parseAidFromUrl(url: String): Long {
        if (url.startsWith("bilibili://video/")) {
            return url.split("/").last().toLong()
        } else {
            val pathSegments = Url(url).rawSegments
            val videoSegmentIndex = pathSegments.indexOf("video")
            val videoId = pathSegments[videoSegmentIndex + 1]
            return if (videoId.startsWith("BV")) {
                AvBvConverter.bv2av(videoId)
            } else {
                videoId.drop(2).toLong()
            }
        }
    }

    fun parseBvidFromUrl(url: String) = parseAidFromUrl(url).toBv()
}
