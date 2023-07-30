package dev.aaa1115910.biliapi.http.entity.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 用户信息
 *
 * @param mid 用户mid
 * @param name 昵称
 * @param sex 性别 男/女/保密
 * @param face 头像链接
 * @param faceNft 是否为 nft 头像 0不是nft头像 1是 nft 头像
 * @param faceNftType 0,1
 * @param sign 签名
 * @param rank 用户权限等级 目前应该无任何作用 5000：0级未答题 10000：普通会员 20000：字幕君 25000：VIP 30000：真·职人 32000：管理员
 * @param level 当前等级 0-6级
 * @param jointime 注册时间 此接口返回恒为0
 * @param moral 节操值 此接口返回恒为0
 * @param silence 封禁状态 0：正常 1：被封
 * @param coins 硬币数 需要登录(Cookie) 只能查看自己的 默认为0
 * @param fansBadge 是否具有粉丝勋章 false：无 true：有
 * @param fansMedal 粉丝勋章信息
 * @param official 认证信息
 * @param vip 会员信息
 * @param pendant 头像框信息
 * @param nameplate 勋章信息
 * @param userHonourInfo
 * @param isFollowed 是否关注此用户 true：已关注 false：未关注 需要登录(Cookie) 未登录恒为false
 * @param topPhoto 主页头图链接
 * @param theme 空 作用尚不明确
 * @param sys_notice 系统通知 无内容则为空对象 主要用于展示如用户争议、纪念账号等等
 * @param live_room 直播间信息
 * @param birthday 生日 MM-DD 如设置隐私为空
 * @param school 学校
 * @param profession 专业资质信息
 * @param tags 个人标签
 * @param series
 * @param isSeniorMember 是否为硬核会员 0：否 1：是
 * @param mcn_info
 * @param gaiaResType
 * @param gaia_data
 * @param isRisk
 * @param elec 充电信息
 * @param contract 是否显示老粉计划
 */
@Serializable
data class UserInfoData(
    val mid: Long,
    val name: String,
    val sex: String,
    val face: String,
    @SerialName("face_nft")
    val faceNft: Int,
    @SerialName("face_nft_type")
    val faceNftType: Int,
    val sign: String,
    val rank: Int,
    val level: Int,
    val jointime: Int,
    val moral: Int,
    val silence: Int,
    val coins: Float,
    @SerialName("fans_badge")
    val fansBadge: Boolean,
    @SerialName("fans_medal")
    val fansMedal: FansMedal,
    val official: Official,
    val vip: Vip,
    val pendant: Pendant,
    val nameplate: Nameplate,
    @SerialName("user_honour_info")
    val userHonourInfo: UserHonours,
    @SerialName("is_followed")
    val isFollowed: Boolean,
    @SerialName("top_photo")
    val topPhoto: String,
    //val theme: Any? = null,
    val sys_notice: SysNotice,
    val live_room: LiveRoom,
    val birthday: String,
    val school: School? = null,
    val profession: Profession,
    //val tags: Any? = null,
    val series: Series,
    @SerialName("is_senior_member")
    val isSeniorMember: Int,
    //val mcn_info: Any? = null,
    @SerialName("gaia_res_type")
    val gaiaResType: Int,
    //val gaia_data: Any? = null,
    @SerialName("is_risk")
    val isRisk: Boolean,
    val elec: Elec,
    val contract: Contract? = null
) {
    @Serializable
    data class FansMedal(
        val show: Boolean,
        val wear: Boolean,
        val medal: Medal? = null
    ) {
        /**
         * 粉丝勋章
         *
         * @param uid 此用户mid
         * @param targetId 粉丝勋章所属UP的mid
         * @param medalId 粉丝勋章id
         * @param level 粉丝勋章等级
         * @param medalName 粉丝勋章名称
         * @param medalColor 颜色
         * @param intimacy 当前亲密度
         * @param nextIntimacy 下一等级所需亲密度
         * @param dayLimit 每日亲密度获取上限
         * @param todayFeed 今日已获得亲密度
         * @param medalColorStart 粉丝勋章颜色 十进制数，可转为十六进制颜色代码
         * @param medalColorEnd 粉丝勋章颜色 十进制数，可转为十六进制颜色代码
         * @param medalColorBorder 粉丝勋章边框颜色 十进制数，可转为十六进制颜色代码
         * @param isLighted
         * @param lightStatus
         * @param wearingStatus 当前是否佩戴 0：未佩戴 1：已佩戴
         * @param score
         */
        @Serializable
        data class Medal(
            val uid: Long,
            @SerialName("target_id")
            val targetId: Long,
            @SerialName("medal_id")
            val medalId: Int,
            val level: Long,
            @SerialName("medal_name")
            val medalName: String,
            @SerialName("medal_color")
            val medalColor: Int,
            val intimacy: Int,
            @SerialName("next_intimacy")
            val nextIntimacy: Int,
            @SerialName("day_limit")
            val dayLimit: Int,
            @SerialName("today_feed")
            val todayFeed: Int,
            @SerialName("medal_color_start")
            val medalColorStart: Int,
            @SerialName("medal_color_end")
            val medalColorEnd: Int,
            @SerialName("medal_color_border")
            val medalColorBorder: Int,
            @SerialName("is_lighted")
            val isLighted: Int,
            @SerialName("light_status")
            val lightStatus: Int,
            @SerialName("wearing_status")
            val wearingStatus: Int,
            val score: Int
        )
    }


    /**
     * 系统提示
     * 如用户争议、纪念账号等等
     *
     * @param id id
     * @param content 显示文案
     * @param url 跳转地址
     * @param noticeType 提示类型 1,2
     * @param icon 前缀图标
     * @param textColor 文字颜色
     * @param bgColor 背景颜色
     */
    @Serializable
    data class SysNotice(
        val id: Int? = null,
        val content: String? = null,
        val url: String? = null,
        @SerialName("notice_type")
        val noticeType: Int? = null,
        val icon: String? = null,
        @SerialName("text_color")
        val textColor: String? = null,
        @SerialName("bg_color")
        val bgColor: String? = null
    )

    /**
     *
     * @param roomStatus 直播间状态 0：无房间 1：有房间
     * @param liveStatus 直播状态 0：未开播 1：直播中
     * @param url 直播间网页 url
     * @param title 直播间标题
     * @param cover 直播间封面 url
     * @param watchedShow
     * @param roomId 直播间 id(短号)
     * @param roundStatus 轮播状态 0：未轮播 1：轮播
     * @param broadcastType 0
     */
    @Serializable
    data class LiveRoom(
        val roomStatus: Int,
        val liveStatus: Int,
        val url: String,
        val title: String,
        val cover: String,
        @SerialName("watched_show")
        val watchedShow: WatchedShow,
        @SerialName("roomid")
        val roomId: Int,
        val roundStatus: Int,
        @SerialName("broadcast_type")
        val broadcastType: Int
    ) {
        @Serializable
        data class WatchedShow(
            val switch: Boolean,
            val num: Int,
            @SerialName("text_small")
            val textSmall: String,
            @SerialName("text_large")
            val textLarge: String,
            val icon: String,
            @SerialName("icon_location")
            val iconLocation: String,
            @SerialName("icon_web")
            val iconWeb: String
        )
    }

    /**
     * 学校
     *
     * @param name 就读大学名称
     */
    @Serializable
    data class School(
        val name: String
    )

    @Serializable
    data class Series(
        @SerialName("user_upgrade_status")
        val userUpgradeStatus: Int,
        @SerialName("show_upgrade_window")
        val showUpgradeWindow: Boolean
    )

    /**
     * 充电
     *
     * @param showInfo 显示信息
     */
    @Serializable
    data class Elec(
        @SerialName("show_info")
        val showInfo: ElecShowInfo
    ) {
        /**
         * 充电显示信息
         *
         * @param show 是否开通了充电
         * @param state 状态 -1：未开通 1：已开通
         * @param title 空串
         * @param icon 空串
         * @param jumpUrl 空串
         */
        @Serializable
        data class ElecShowInfo(
            val show: Boolean,
            val state: Int,
            val title: String,
            val icon: String,
            @SerialName("jump_url")
            val jumpUrl: String
        )
    }

    /**
     * 老粉计划
     *
     * @param isDisplay true/false 在页面中未使用此字段
     * @param isFollowDisplay 是否在显示老粉计划 true：显示 false：不显示
     */
    @Serializable
    data class Contract(
        @SerialName("is_display")
        val isDisplay: Boolean,
        @SerialName("is_follow_display")
        val isFollowDisplay: Boolean
    )
}
