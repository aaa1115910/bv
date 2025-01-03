package dev.aaa1115910.biliapi.http.entity.reply

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentReplyData(
    val config: Config,
    val control: Control,
    val page: Page,
    val replies: List<CommentData.Reply> = emptyList(),
    val root: CommentData.Reply,
//    @SerialName("show_text")
//    val showText: String,
//    @SerialName("show_type")
//    val showType: Int,
    val upper: Upper
) {
    @Serializable
    data class Page(
        val count: Int,
        val num: Int,
        val size: Int
    )
}
