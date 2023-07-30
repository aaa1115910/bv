package dev.aaa1115910.biliapi.http.entity.video

import kotlinx.serialization.Serializable

/**
 * 投币时顺便点赞的结果
 *
 * @param like 是否点赞成功 true：成功 false：失败 已赞过则附加点赞失败
 */
@Serializable
data class AddCoin(
    val like: Boolean
)

/**
 * 检查是否投币
 *
 * @param multiply 投币枚数 未投币为0
 */
@Serializable
data class CheckSentCoin(
    val multiply:Int
)