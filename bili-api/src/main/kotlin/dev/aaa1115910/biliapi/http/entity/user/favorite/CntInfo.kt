package dev.aaa1115910.biliapi.http.entity.user.favorite

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @param collect 收藏数
 * @param play 播放数
 * @param thumbUp 点赞数
 * @param share 分享数
 */
@Serializable
data class CntInfo(
    val collect: Int,
    val play: Int,
    val danmaku: Int = 0,
    @SerialName("thumb_up")
    val thumbUp: Int = 0,
    val share: Int = 0
)