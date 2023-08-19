package dev.aaa1115910.biliapi.http.entity.user.favorite

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 收藏夹元数据
 *
 * @param id 收藏夹mlid（完整id） 收藏夹原始id+创建者mid尾号2位
 * @param fid 收藏夹原始id
 * @param mid 创建者mid
 * @param attr 属性位（？）
 * @param title 收藏夹标题
 * @param cover 收藏夹封面图片url
 * @param upper 创建者信息
 * @param coverType 封面图类别（？）
 * @param cntInfo 收藏夹状态数
 * @param type 类型（？） 一般是11
 * @param intro 备注
 * @param ctime 创建时间	时间戳
 * @param mtime 收藏时间	时间戳
 * @param state 状态（？） 一般为0
 * @param favState 收藏夹收藏状态 已收藏收藏夹：1 未收藏收藏夹：0 需要登录
 * @param likeState 点赞状态 已点赞：1 未点赞：0 需要登录
 * @param mediaCount 收藏夹内容数量
 */
@Serializable
data class FavoriteFolderInfo(
    val id: Long,
    val fid: Long,
    val mid: Long,
    val attr: Int,
    val title: String,
    val cover: String,
    val upper: Upper,
    @SerialName("cover_type")
    val coverType: Int,
    @SerialName("cnt_info")
    val cntInfo: CntInfo,
    val type: Int,
    val intro: String,
    val ctime: Long,
    val mtime: Long,
    val state: Int,
    @SerialName("fav_state")
    val favState: Int,
    @SerialName("like_state")
    val likeState: Int,
    @SerialName("media_count")
    val mediaCount: Int
)
