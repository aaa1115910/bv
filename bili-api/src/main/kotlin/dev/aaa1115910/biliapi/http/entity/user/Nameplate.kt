package dev.aaa1115910.biliapi.http.entity.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 勋章
 *
 * @param nid 勋章id
 * @param name 勋章名称
 * @param image 勋章图标
 * @param imageSmall 勋章图标（小）
 * @param level 勋章等级
 * @param condition 获取条件
 */
@Serializable
data class Nameplate(
    val nid: Int,
    val name: String,
    val image: String,
    @SerialName("image_small")
    val imageSmall: String,
    val level: String,
    val condition: String
)