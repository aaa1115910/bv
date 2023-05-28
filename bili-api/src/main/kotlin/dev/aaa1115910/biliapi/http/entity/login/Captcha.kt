package dev.aaa1115910.biliapi.http.entity.login

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 申请 captcha 验证码结果
 *
 * @param type 验证方式 用于判断使用哪一种验证方式，目前所见只有极验 geetest：极验
 * @param token 登录 API token  与 captcha 无关，与登录接口有关
 * @param geetest 极验 captcha 数据
 * @param tencent 作用尚不明确
 */
@Serializable
data class CaptchaData(
    val type: String,
    val token: String,
    val geetest: Geetest,
    val tencent: Tencent
) {
    /**
     * 极验captcha数据
     *
     * @param gt 极验id	一般为固定值
     * @param challenge 极验KEY	由B站后端产生用于人机验证
     */
    @Serializable
    data class Geetest(
        val challenge: String,
        val gt: String
    )

    @Serializable
    data class Tencent(
        @SerialName("appid")
        val appId: String
    )
}