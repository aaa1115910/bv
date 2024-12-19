// package dev.aaa1115910.biliapi.http.entity.history
package dev.aaa1115910.biliapi.http.entity.toview

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ToViewData(
    // val cursor: Cursor,
    // val tab: List<TabItem>,
    val list: List<ToViewItem>
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

@Serializable
data class ToViewItem(
    var aid: Long,
    var bvid: String,
    var cid: Long,
    var owner: Owner,
    val title: String,
    // @SerialName("long_title")
    // val longTitle: String,
    val pic: String,
    // val cover: String,
    // val covers: List<String>? = null,
    // val uri: String,
    // val history: HistoryInfo,
    val videos: Int,
    // @SerialName("author_name")
    // val authorName: String,
    // @SerialName("author_face")
    // val authorFace: String,
    // @SerialName("author_mid")
    // val authorMid: Long,
    // @SerialName("view_at")
    // val viewAt: Int,
    val progress: Int,
    // val badge: String,
    // @SerialName("show_title")
    // val showTitle: String,
    val duration: Int,
    // val current: String,
    // val total: Int,
    // @SerialName("new_desc")
    // val newDesc: String,
    // @SerialName("is_finish")
    // val isFinish: Int,
    // @SerialName("is_fav")
    // val isFav: Int,
    // val kid: Int,
) {
    @Serializable
    data class Owner(
        val name: String
    )
    // @Serializable
    // data class HistoryInfo(
    //     val oid: Long,
    //     val epid: Int,
    //     val bvid: String,
    //     val page: Int,
    //     val cid: Long,
    //     val part: String,
    //     val business: String,
    //     val dt: Int
    // )
}