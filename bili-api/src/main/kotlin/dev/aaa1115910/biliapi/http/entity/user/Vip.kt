package dev.aaa1115910.biliapi.http.entity.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 会员信息
 *
 * @param type 成员会员类型 0：无 1：月会员 2：年会员
 * @param status 会员状态 0：无 1：有
 * @param dueDate 会员过期时间 Unix时间戳(毫秒)
 * @param vipPayType 支付类型 0：未支付（常见于官方账号） 1：已支付（以正常渠道获取的大会员均为此值）
 * @param themeType 0
 * @param label 会员标签
 * @param avatarSubscript 是否显示会员图标 0：不显示 1：显示
 * @param nicknameColor 会员昵称颜色 颜色码，一般为#FB7299，曾用于愚人节改变大会员配色
 * @param role 大角色类型 1：月度大会员 3：年度大会员 7：十年大会员 15：百年大会员
 * @param avatarSubscriptUrl 大会员角标地址
 * @param tvVipStatus 电视大会员状态 0：未开通
 * @param tvVipPayType 电视大会员支付类型
 */
@Serializable
data class Vip(
    val type: Int,
    val status: Int,
    @SerialName("due_date")
    val dueDate: Long,
    @SerialName("vip_pay_type")
    val vipPayType: Int = 0,
    @SerialName("theme_type")
    val themeType: Int,
    val label: Label,
    @SerialName("avatar_subscript")
    val avatarSubscript: Int,
    @SerialName("nickname_color")
    val nicknameColor: String,
    val role: Int = 0,
    @SerialName("avatar_subscript_url")
    val avatarSubscriptUrl: String,
    @SerialName("tv_vip_status")
    val tvVipStatus: Int = 0,
    @SerialName("tv_vip_pay_type")
    val tvVipPayType: Int = 0
) {
    /**
     * 大会员标签
     *
     * @param path 空 作用尚不明确
     * @param text 会员类型文案 大会员 年度大会员 十年大会员 百年大会员 最强绿鲤鱼
     * @param labelTheme 会员标签 vip：大会员 annual_vip：年度大会员 ten_annual_vip：十年大会员 hundred_annual_vip：百年大会员 fools_day_hundred_annual_vip：最强绿鲤鱼
     * @param textColor 会员标签
     * @param bgStyle 1
     * @param bgColor 会员标签背景颜色 颜色码，一般为#FB7299，曾用于愚人节改变大会员配色
     * @param borderColor 会员标签边框颜色 未使用
     * @param useImgLabel true
     * @param imgLabelUriHans 空串
     * @param imgLabelUriHant 空串
     * @param imgLabelUriHansStatic 大会员牌子图片 简体版
     * @param imgLabelUriHantStatic 大会员牌子图片 繁体版
     */
    @Serializable
    data class Label(
        val path: String = "",
        val text: String,
        @SerialName("label_theme")
        val labelTheme: String = "",
        @SerialName("text_color")
        val textColor: String,
        @SerialName("bg_style")
        val bgStyle: Int,
        @SerialName("bg_color")
        val bgColor: String,
        @SerialName("border_color")
        val borderColor: String,
        @SerialName("use_img_label")
        val useImgLabel: Boolean = false,
        @SerialName("img_label_uri_hans")
        val imgLabelUriHans: String = "",
        @SerialName("img_label_uri_hant")
        val imgLabelUriHant: String = "",
        @SerialName("img_label_uri_hans_static")
        val imgLabelUriHansStatic: String = "",
        @SerialName("img_label_uri_hant_static")
        val imgLabelUriHantStatic: String = ""
    )
}