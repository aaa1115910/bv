package dev.aaa1115910.biliapi.http.entity.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 头像框
 *
 * @param pid 头像框id
 * @param name 头像框名称
 * @param image 头像框图片url
 * @param expire 过期时间 此接口返回恒为0
 * @param imageEnhance 头像框图片url
 * @param imageEnhanceFrame 头像框图片逐帧序列url
 */
@Serializable
data class Pendant(
    val pid: Int,
    val name: String,
    val image: String,
    val expire: Int = 0,
    @SerialName("image_enhance")
    val imageEnhance: String? = null,
    @SerialName("image_enhance_frame")
    val imageEnhanceFrame: String? = null
)