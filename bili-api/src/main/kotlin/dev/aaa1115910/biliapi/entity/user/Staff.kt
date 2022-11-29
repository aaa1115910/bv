package dev.aaa1115910.biliapi.entity.user

import kotlinx.serialization.SerialName
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
    val mid: Int,
    val title: String,
    val name: String,
    val face: String,
    val vip: Vip,
    val official: Official,
    val follower: Int
)

/**
 * 会员信息
 *
 * @param type 成员会员类型 0：无 1：月会员 2：年会员
 * @param status 会员状态 0：无 1：有
 * @param themeType num 0
 */
@Serializable
data class Vip(
    val type: Int,
    val status: Int,
    @SerialName("theme_type")
    val themeType: Int
)

/**
 * 认证信息
 *
 * @param role 成员认证级别 0：无 1 2 7：个人认证 3 4 5 6：机构认证
 * @param title 成员认证名 无为空
 * @param desc  成员认证备注 无为空
 * @param type 成员认证类型 -1：无 0：有
 */
@Serializable
data class Official(
    val role: Int,
    val title: String,
    val desc: String,
    val type: Int
)