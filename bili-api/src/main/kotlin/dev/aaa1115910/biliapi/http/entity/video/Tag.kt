package dev.aaa1115910.biliapi.http.entity.video

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * 视频标签
 *
 * @param tagId
 * @param tagName TAG名称
 * @param cover TAG图片url
 * @param headCover TAG页面头图url
 * @param content TAG介绍
 * @param shortContent TAG简介
 * @param type
 * @param state
 * @param ctime 创建时间 时间戳
 * @param count 状态数
 * @param isAtten 是否关注 0：未关注 1：已关注 需要登录(Cookie) 未登录为0
 * @param likes
 * @param hates
 * @param attribute
 * @param liked 是否已经点赞 0：未点赞 1：已点赞 需要登录(Cookie) 未登录为0
 * @param hated 是否已经点踩 0：未点踩 1：已点踩 需要登录(Cookie) 未登录为0
 * @param extraAttr
 */
@Serializable
data class Tag(
    @SerialName("tag_id")
    val tagId: Int,
    @SerialName("tag_name")
    val tagName: String,
    val cover: String,
    @SerialName("head_cover")
    val headCover: String,
    val content: String,
    @SerialName("short_content")
    val shortContent: String,
    val type: Int,
    val state: Int,
    val ctime: Int,
    val count: Count,
    @SerialName("is_atten")
    val isAtten: Int,
    val likes: Int,
    val hates: Int,
    val attribute: Int,
    val liked: Int,
    val hated: Int,
    @SerialName("extra_attr")
    val extraAttr: Int
) {
    /**
     * Tag 状态数
     *
     * @param view
     * @param use 视频添加TAG数
     * @param atten TAG关注
     */
    @Serializable
    data class Count(
        val view: Int,
        val use: Int,
        val atten: Int
    )
}

/**
 * Tag 详情，包含相似的 Tag 和最新的视频
 *
 * @param info Tag 信息
 * @param similar 相似的 Tag
 * @param news 最新的 Tag 的视频
 */
@Serializable
data class TagDetail(
    val info: Tag,
    val similar: List<SimilarTag>,
    val news: NewTags
) {
    /**
     * 相似的 Tag
     */
    @Serializable
    data class SimilarTag(
        val rid: Int,
        val rname: String,
        val tid: Int,
        val cover: String,
        val atten: Int,
        val tname: String
    )

    /**
     * 最新的 Tag 的视频
     */
    @Serializable
    data class NewTags(
        val count: Int,
        val archives: List<VideoInfo>
    )
}

@Serializable
data class TagTopVideosResponse(
    val code: Int,
    val message: String,
    val total: Int,
    val data: List<VideoInfo>
)