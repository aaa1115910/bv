package dev.aaa1115910.biliapi.http.entity.video

import kotlinx.serialization.Serializable

/**
 * 添加/移除收藏
 *
 * @param prompt 是否为未关注用户收藏 false：否 true：是
 */
@Serializable
data class SetVideoFavorite(
    val prompt:Boolean
)

/**
 * 检查是否被收藏
 *
 * @param count 1
 * @param favoured 是否收藏 true：已收藏 false：未收藏
 */
@Serializable
data class CheckVideoFavoured(
    val count:Int,
    val favoured:Boolean
)