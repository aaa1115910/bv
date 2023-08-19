package dev.aaa1115910.biliapi.http.entity.user

import kotlinx.serialization.Serializable

/**
 * 认证信息
 *
 * @param role 认证类型 0：无 1 2 7 9：个人认证 3 4 5 6：机构认证
 * @param title 认证信息 无为空
 * @param desc 认证备注 无为空
 * @param type 是否认证 -1：无 0：个人认证 1：机构认证
 */
@Serializable
data class Official(
    val role: Int,
    val title: String,
    val desc: String,
    val type: Int
)

/**
 * 认证信息
 *
 * @param type 是否认证 -1：无 0：认证
 * @param desc 认证信息 无为空
 */
@Serializable
data class OfficialVerify(
    val type: Int,
    val desc: String
)