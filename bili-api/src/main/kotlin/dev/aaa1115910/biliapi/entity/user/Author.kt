package dev.aaa1115910.biliapi.entity.user

import dev.aaa1115910.biliapi.http.entity.video.VideoOwner

data class Author(
    val mid: Long,
    val name: String,
    val face: String
) {
    companion object {
        fun fromVideoOwner(videoOwner: VideoOwner) = Author(
            mid = videoOwner.mid,
            name = videoOwner.name,
            face = videoOwner.face
        )

        fun fromAuthor(author: bilibili.app.archive.v1.Author) = Author(
            mid = author.mid,
            name = author.name,
            face = author.face
        )
    }
}