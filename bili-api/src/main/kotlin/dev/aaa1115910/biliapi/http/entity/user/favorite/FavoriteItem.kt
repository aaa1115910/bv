package dev.aaa1115910.biliapi.http.entity.user.favorite

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 收藏夹内容
 *
 * @param id 内容id 视频稿件：视频稿件avid 音频：音频auid 视频合集：视频合集id
 * @param type 内容类型 2：视频稿件 12：音频 21：视频合集
 * @param title 标题
 * @param cover 封面url
 * @param intro 简介
 * @param page 视频分P数
 * @param duration 音频/视频时长
 * @param upper UP主信息
 * @param attr 属性位（？）
 * @param cntInfo 状态数
 * @param link 跳转uri
 * @param ctime 投稿时间	时间戳
 * @param pubtime 发布时间 时间戳
 * @param favTime 收藏时间 时间戳
 * @param bvid 视频稿件bvid
 */
@Serializable
data class FavoriteItem(
    val id: Long,
    val type: Int,
    val title: String,
    val cover: String,
    val intro: String,
    val page: Int,
    val duration: Int,
    val upper: Upper,
    val attr: Int,
    @SerialName("cnt_info")
    val cntInfo: CntInfo,
    val link: String,
    val ctime: Long,
    val pubtime: Long,
    @SerialName("fav_time")
    val favTime: Long,
    val bvid: String,
)

@Serializable
data class FavoriteItemIdListResponse(
    val code: Int,
    val message: String,
    val data: List<FavoriteItemId>? = null
)

/**
 * 收藏夹内容 ID
 *
 * @param id 内容id 视频稿件：视频稿件avid 音频：音频auid 视频合集：视频合集id
 * @param type 内容类型 2：视频稿件 12：音频 21：视频合集
 * @param bvid 视频稿件bvid
 */
@Serializable
data class FavoriteItemId(
    val id: Long,
    val type: Int,
    val bvid: String
)