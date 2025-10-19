package dev.aaa1115910.biliapi.http.entity.region

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegionFeedRcmd(
    val archives: List<Archive>
) {
    @Serializable
    data class Archive(
        val aid: Long,
        val bvid: String,
        val cid: Long,
        val title: String,
        val cover: String,
        val duration: Int,
        val pubdate: Long,
        val stat: Stat,
        val author: Author,
        val trackid: String,
        val goto: String,
        @SerialName("rec_reason")
        val recReason: String
    ) {
        @Serializable
        data class Stat(
            val view: Long,
            val like: Int,
            val danmaku: Int
        )

        @Serializable
        data class Author(
            val mid: Long,
            val name: String
        )
    }
}