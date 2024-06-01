package dev.aaa1115910.biliapi.http.entity.history

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 历史记录信息
 *
 * @param cursor 历史记录页面信息
 * @param tab 历史记录筛选类型
 * @param list 分段历史记录列表
 */
@Serializable
data class HistoryData(
    val cursor: Cursor,
    val tab: List<TabItem>,
    val list: List<HistoryItem>
) {
    /**
     * 历史记录页面信息
     *
     * @param max 最后一项目标 id 见请求参数
     * @param viewAt 最后一项时间节点 时间戳
     * @param business 最后一项业务类型 见请求参数
     * @param ps 每页项数
     */
    @Serializable
    data class Cursor(
        val max: Long,
        @SerialName("view_at")
        val viewAt: Long,
        val business: String,
        val ps: Int
    )

    /**
     * 历史记录筛选类型
     *
     * @param type 类型
     * @param name 类型名
     */
    @Serializable
    data class TabItem(
        val type: String,
        val name: String
    )
}

/**
 * 历史记录列表项
 *
 * @param title 条目标题
 * @param longTitle 条目副标题
 * @param cover 条目封面图 url 用于专栏以外的条目
 * @param covers 条目封面图组 仅用于专栏 有效时：array 无效时：null
 * @param uri 重定向 url 仅用于剧集和直播
 * @param history  条目详细信息
 * @param videos 视频分 P 数目 仅用于稿件视频
 * @param authorName UP 主昵称
 * @param authorFace UP 主头像 url
 * @param authorMid UP 主 mid
 * @param viewAt 查看时间 时间戳
 * @param progress 视频观看进度 单位为秒 用于稿件视频或剧集
 * @param badge 角标文案 稿件视频 / 剧集 / 笔记
 * @param showTitle 分 P 标题 用于稿件视频或剧集
 * @param duration 视频总时长 用于稿件视频或剧集
 * @param current (?)
 * @param total 总计分集数 仅用于剧集
 * @param newDesc 最新一话 / 最新一 P 标识 用于稿件视频或剧集
 * @param isFinish 是否已完结	仅用于剧集 0：未完结 1：已完结
 * @param isFav 是否收藏 0：未收藏 1：已收藏
 * @param kid 条目目标 id 详细内容见参数
 * @param tagName 子分区名 用于稿件视频和直播
 * @param liveStatus 直播状态 仅用于直播 0：未开播 1：已开播
 */
@Serializable
data class HistoryItem(
    val title: String,
    @SerialName("long_title")
    val longTitle: String,
    val cover: String,
    val covers: List<String>? = null,
    val uri: String,
    val history: HistoryInfo,
    val videos: Int,
    @SerialName("author_name")
    val authorName: String,
    @SerialName("author_face")
    val authorFace: String,
    @SerialName("author_mid")
    val authorMid: Long,
    @SerialName("view_at")
    val viewAt: Int,
    val progress: Int,
    val badge: String,
    @SerialName("show_title")
    val showTitle: String,
    val duration: Int,
    val current: String,
    val total: Int,
    @SerialName("new_desc")
    val newDesc: String,
    @SerialName("is_finish")
    val isFinish: Int,
    @SerialName("is_fav")
    val isFav: Int,
    val kid: Long,
    @SerialName("tag_name")
    val tagName: String,
    @SerialName("live_status")
    val liveStatus: Int
) {
    /**
     * 历史记录详细信息
     *
     * @param oid 目标id 稿件视频&剧集（当business=archive或business=pgc时）：稿件avid 直播（当business=live时）：直播间id 文章（当business=article时）：文章cvid 文集（当business=article-list时）：文集rlid
     * @param epid 剧集epid 仅用于剧集
     * @param bvid 稿件bvid 仅用于稿件视频
     * @param page 观看到的视频分P数 仅用于稿件视频
     * @param cid 观看到的对象id 稿件视频&剧集（当business=archive或business=pgc时）：视频cid 文集（当business=article-list时）：文章cvid
     * @param part 观看到的视频分 P 标题 仅用于稿件视频
     * @param business 业务类型 见请求参数
     * @param dt 记录查看的平台代码	1 3 5 7：手机端 2：web端 4 6：pad端 33：TV端 0：其他
     */
    @Serializable
    data class HistoryInfo(
        val oid: Long,
        val epid: Int,
        val bvid: String,
        val page: Int,
        val cid: Long,
        val part: String,
        val business: String,
        val dt: Int
    )
}