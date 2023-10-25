package dev.aaa1115910.biliapi.entity.reply

import java.util.UUID

data class CommentsData(
    val comments: List<Comment> = emptyList(),
    val nextPage: CommentPage = CommentPage(),
    val hasNext: Boolean
) {
    companion object {
        fun fromCommentData(commentData: dev.aaa1115910.biliapi.http.entity.reply.CommentData): CommentsData {
            val nextOffset = commentData.cursor.paginationReply?.nextOffset
            return CommentsData(
                comments = commentData.replies.map { Comment.fromReply(it) },
                nextPage = CommentPage(
                    nextWebPage = commentData.cursor.paginationReply?.nextOffset ?: ""
                ),
                hasNext = commentData.cursor.isEnd.not() && nextOffset != null
            )
        }

        fun fromMainListReply(mainListReply: bilibili.main.community.reply.v1.MainListReply): CommentsData {
            return CommentsData(
                comments = mainListReply.repliesList.map { Comment.fromReplyInfo(it) },
                nextPage = CommentPage(
                    nextAppPage = mainListReply.paginationReply.nextOffset
                ),
                hasNext = mainListReply.cursor.isEnd.not()
            )
        }
    }
}

data class Comment(
    val rpid: Long,
    val mid: Long,
    val oid: Long,
    val parent: Long,
    val content: List<String>,
    val member: Member,
    val timeDesc: String,
    val emotes: List<Emote>,
    val pictures: List<Picture>,
    val replies: List<Comment>,
    val repliesCount: Int,
) {
    companion object {
        fun fromReply(reply: dev.aaa1115910.biliapi.http.entity.reply.CommentData.Reply): Comment {
            return Comment(
                rpid = reply.rpid,
                mid = reply.mid,
                oid = reply.oid.toLong(),
                parent = reply.parent,
                content = reply.content.message.splitWithEmotes(*reply.content.emote.keys.toTypedArray()),
                member = Member(
                    mid = reply.mid,
                    avatar = reply.member.avatar,
                    name = reply.member.uname
                ),
                timeDesc = reply.replyControl.timeDesc,
                emotes = reply.content.emote.values.map { Emote.fromEmote(it) },
                pictures = reply.content.pictures.map { Picture.fromPicture(it) },
                replies = reply.replies.map { fromReply(it) },
                repliesCount = reply.count
            )
        }

        fun fromReplyInfo(reply: bilibili.main.community.reply.v1.ReplyInfo): Comment {
            return Comment(
                rpid = reply.id,
                mid = reply.mid,
                oid = reply.oid,
                parent = reply.parent,
                content = reply.content.message.splitWithEmotes(*reply.content.emoteMap.keys.toTypedArray()),
                member = Member(
                    mid = reply.mid,
                    avatar = reply.member.face,
                    name = reply.member.name
                ),
                timeDesc = reply.replyControl.timeDesc,
                emotes = reply.content.emoteMap.values.map { Emote.fromEmote(it) },
                pictures = reply.content.picturesList.map { Picture.fromPicture(it) },
                replies = reply.repliesList.map { fromReplyInfo(it) },
                repliesCount = reply.repliesCount
            )
        }
    }

    data class Member(
        val mid: Long,
        val avatar: String,
        val name: String
    )

    data class Emote(
        val text: String,
        val url: String,
        val size: EmoteSize
    ) {
        companion object {
            fun fromEmote(emote: dev.aaa1115910.biliapi.http.entity.reply.CommentData.Reply.Content.Emote): Emote {
                return Emote(
                    text = emote.text,
                    url = emote.url,
                    size = if (emote.meta.size == 1) EmoteSize.Small else EmoteSize.Large
                )
            }

            fun fromEmote(emote: bilibili.main.community.reply.v1.Emote): Emote {
                return Emote(
                    text = emote.text,
                    url = emote.url,
                    size = if (emote.size == 1L) EmoteSize.Small else EmoteSize.Large
                )
            }
        }
    }

    /**
     * 评论图片
     *
     * @param url 图片链接
     * @param width 图片宽度
     * @param height 图片高度
     * @param key 使用 [com.origeek.imageViewer.previewer.TransformImageView] [com.origeek.imageViewer.previewer.ImagePreviewer] 浏览图片缩放时需要用到的 key
     */
    data class Picture(
        val url: String,
        val width: Int,
        val height: Int,
        val key: String
    ) {
        companion object {
            fun fromPicture(picture: dev.aaa1115910.biliapi.http.entity.reply.CommentData.Reply.Content.Picture): Picture {
                return Picture(
                    url = picture.imgSrc,
                    width = picture.imgWidth,
                    height = picture.imgHeight,
                    key = UUID.randomUUID().toString()
                )
            }

            fun fromPicture(picture: bilibili.main.community.reply.v1.Picture): Picture {
                return Picture(
                    url = picture.imgSrc,
                    width = picture.imgWidth.toInt(),
                    height = picture.imgHeight.toInt(),
                    key = UUID.randomUUID().toString()
                )
            }
        }
    }
}

enum class EmoteSize(val fontSize: Int) {
    Small(20), Large(20)
}

private fun String.splitWithEmotes(vararg emotes: String): List<String> {
    val delimiter = emotes.joinToString("|").replace("[", "\\[").replace("]", "\\]")
    val regex = Regex("(?=$delimiter)|(?<=$delimiter)")
    return this.split(regex)
}