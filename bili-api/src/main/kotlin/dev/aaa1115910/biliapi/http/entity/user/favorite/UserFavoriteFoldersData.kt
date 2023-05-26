package dev.aaa1115910.biliapi.http.entity.user.favorite

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 指定用户创建的所有收藏夹信息
 *
 * @param count 创建的收藏夹总数
 * @param list 创建的收藏夹列表
 */
@Serializable
data class UserFavoriteFoldersData(
    val count: Int,
    val list: List<UserFavoriteFolder> = emptyList()
) {
    /**
     * 用户收藏夹信息
     *
     * @param id 收藏夹mlid（完整id） 收藏夹原始id+创建者mid尾号2位
     * @param fid 收藏夹原始id
     * @param mid 创建者mid
     * @param attr 属性位（？）
     * @param title 收藏夹标题
     * @param favState 目标id是否存在于该收藏夹 存在于该收藏夹：1 不存在于该收藏夹：0
     * @param mediaCount 收藏夹内容数量
     */
    @Serializable
    data class UserFavoriteFolder(
        val id: Long,
        val fid: Long,
        val mid: Long,
        val attr: Int,
        val title: String,
        @SerialName("fav_state")
        val favState: Int,
        @SerialName("media_count")
        val mediaCount: Int
    )
}
