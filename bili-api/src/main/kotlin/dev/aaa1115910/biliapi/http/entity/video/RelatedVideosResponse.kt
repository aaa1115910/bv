package dev.aaa1115910.biliapi.http.entity.video

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 获取单视频推荐列表
 */
@Serializable
data class RelatedVideosResponse(
    val code: Int,
    val message: String,
    val data: List<RelatedVideoInfo> = emptyList()
)

/**
 * 视频详细信息
 *
 * @param bvid 稿件bvid
 * @param aid 稿件avid
 * @param videos 稿件分P总数 默认为1
 * @param tid 分区tid
 * @param tname 子分区名称
 * @param copyright 视频类型 1：原创 2：转载
 * @param pic 稿件封面图片url
 * @param title 稿件标题
 * @param pubdate 稿件发布时间 秒级时间戳
 * @param ctime 用户投稿时间 秒级时间戳
 * @param desc 视频简介
 * @param state 视频状态
 * @param duration 稿件总时长(所有分P) 单位为秒
 * @param rights 视频属性标志
 * @param owner 视频UP主信息
 * @param stat 视频状态数
 * @param dynamic 视频同步发布的的动态的文字内容
 * @param cid 视频1P cid
 * @param dimension 视频1P分辨率
 * @param shortLink
 * @param shortLinkV2
 * @param seasonType
 * @param isOgv
 * @param ogvInfo
 * @param rcmdReason 热门推荐理由
 */
@Serializable
data class RelatedVideoInfo(
    val bvid: String,
    val aid: Long,
    val videos: Int,
    val tid: Int,
    val tname: String,
    val copyright: Int,
    val pic: String,
    val title: String,
    val pubdate: Int,
    val ctime: Int,
    val desc: String,
    val state: Int,
    val duration: Int,
    val rights: VideoRights,
    val owner: VideoOwner,
    val stat: VideoStat,
    val dynamic: String,
    val cid: Long,
    val dimension: Dimension,
    @SerialName("short_link")
    val shortLink: String? = null,
    @SerialName("short_link_v2")
    val shortLinkV2: String? = null,
    @SerialName("season_type")
    val seasonType: Int? = null,
    @SerialName("is_ogv")
    val isOgv: Boolean = false,
    @SerialName("ogv_info")
    val ogvInfo: String? = null,
    @SerialName("rcmd_reason")
    val rcmdReason: String
)