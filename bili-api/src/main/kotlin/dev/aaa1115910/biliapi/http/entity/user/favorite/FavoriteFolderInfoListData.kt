package dev.aaa1115910.biliapi.http.entity.user.favorite

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 收藏夹信息及内容
 *
 * @param info 收藏夹元数据
 * @param medias 收藏夹内容
 * @param hasMore 还有更多数据
 */
@Serializable
data class FavoriteFolderInfoListData(
    val info: FavoriteFolderInfo,
    val medias: List<FavoriteItem> = emptyList(),
    @SerialName("has_more")
    val hasMore: Boolean
)
