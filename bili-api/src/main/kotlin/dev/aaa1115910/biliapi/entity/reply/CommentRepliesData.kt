package dev.aaa1115910.biliapi.entity.reply

data class CommentRepliesData(
    val rootComment: Comment,
    val replies: List<Comment>,
    val nextPage: CommentReplyPage = CommentReplyPage(),
    val hasNext: Boolean
) {
    companion object {
        fun fromCommentReplyData(commentReplyData: dev.aaa1115910.biliapi.http.entity.reply.CommentReplyData): CommentRepliesData {
            val nextOffset = commentReplyData.page.num * commentReplyData.page.size
            return CommentRepliesData(
                rootComment = Comment.fromReply(commentReplyData.root),
                replies = commentReplyData.replies.map { Comment.fromReply(it) },
                nextPage = CommentReplyPage(
                    nextWebPage = commentReplyData.page.num + 1
                ),
                hasNext = commentReplyData.page.count > nextOffset
            )
        }

        fun fromCommentReplyList(detailListReply: bilibili.main.community.reply.v1.DetailListReply): CommentRepliesData {
            return CommentRepliesData(
                rootComment = Comment.fromReplyInfo(detailListReply.root),
                replies = detailListReply.root.repliesList.map { Comment.fromReplyInfo(it) },
                nextPage = CommentReplyPage(
                    nextAppPage = detailListReply.paginationReply.nextOffset
                ),
                hasNext = detailListReply.cursor.isEnd.not()
            )
        }
    }
}
