package dev.aaa1115910.biliapi.http.entity.video

import dev.aaa1115910.biliapi.http.entity.user.UserCardData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject

@Serializable
data class VideoDetail(
    @SerialName("View")
    val view: VideoInfo,
    @SerialName("Card")
    val card: UserCardData,
    @SerialName("Tags")
    val tags: List<Tag>,
    //TODO 评论
    //@SerialName("Reply")
    //val reply:Any
    @SerialName("Related")
    val related: List<RelatedVideoInfo>?,
    @SerialName("Spec")
    val spec: JsonObject? = null,
    @SerialName("hot_share")
    val hotShare: HotShare,
    val elec: JsonObject? = null,
    val recommend: JsonObject? = null,
    @SerialName("view_addit")
    val viewAddit: JsonObject,
    val guide: JsonObject? = null,
    @SerialName("query_tags")
    val queryTags: JsonObject? = null,
    //@SerialName("is_old_user")
    //val isOldUser: Boolean
) {
    @Serializable
    data class HotShare(
        val show: Boolean,
        val list: JsonArray
    )

    /**
     * 视频 TAG
     *
     * @param tagId TAG ID 当存在[musicId]时可能为 0
     * @param tagName TAG名称
     * @param musicId 音乐ID
     * @param tagType TAG类型 bgm old_channel topic
     * @param jumpUrl 跳转链接
     */
    @Serializable
    data class Tag(
        @SerialName("tag_id")
        val tagId: Int,
        @SerialName("tag_name")
        val tagName: String,
        @SerialName("music_id")
        val musicId: String,
        @SerialName("tag_type")
        val tagType: String,
        @SerialName("jump_url")
        val jumpUrl: String
    )
}
