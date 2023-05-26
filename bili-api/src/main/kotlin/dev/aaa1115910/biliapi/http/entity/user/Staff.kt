package dev.aaa1115910.biliapi.http.entity.user

import kotlinx.serialization.Serializable

/**
 * 创作者个人信息
 *
 * @param mid 成员mid
 * @param title 成员名称
 * @param name 成员昵称
 * @param face 成员头像url
 * @param vip 成员大会员状态
 * @param official 成员认证信息
 * @param follower 成员粉丝数
 */
@Serializable
data class Staff(
    val mid: Long,
    val title: String,
    val name: String,
    val face: String,
    val vip: Vip,
    val official: Official,
    val follower: Int
)
