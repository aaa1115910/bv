package dev.aaa1115910.biliapi.http.entity.user.favorite

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 收藏夹上传者
 *
 * @param mid 创建者mid
 * @param name 创建者昵称
 * @param face 创建者头像url
 * @param followed 是否已关注创建者
 * @param vipType 会员类别 0：无 1：月大会员 2：年度及以上大会员
 * @param vipStatue 会员开通状态 0：无 1：有
 */
@Serializable
data class Upper(
    val mid: Long,
    val name: String,
    val face: String,
    val followed: Boolean = false,
    @SerialName("vip_type")
    val vipType: Int = 0,
    @SerialName("vip_statue")
    val vipStatue: Int = 0
)