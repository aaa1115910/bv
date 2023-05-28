package dev.aaa1115910.biliapi.entity.login

/**
 * 人机数据
 *
 * @param token 登录 API token
 * @param gt 极验id	一般为固定值
 * @param challenge 极验KEY	由B站后端产生用于人机验证
 */
data class Captcha(
    val token: String,
    val challenge: String,
    val gt: String
)