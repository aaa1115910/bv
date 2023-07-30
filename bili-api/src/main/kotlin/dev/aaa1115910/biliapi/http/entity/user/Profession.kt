package dev.aaa1115910.biliapi.http.entity.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 专业资质信息
 *
 * @param name 资质名称
 * @param department 职位
 * @param title 所属机构
 * @param isShow 是否显示 0：不显示 1：显示
 */
@Serializable
data class Profession(
    val name: String,
    val department: String,
    val title: String,
    @SerialName("is_show")
    val isShow: Int
)