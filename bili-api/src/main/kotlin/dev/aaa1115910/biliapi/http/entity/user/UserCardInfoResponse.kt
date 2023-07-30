package dev.aaa1115910.biliapi.http.entity.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 用户卡片信息
 *
 * @param card 详细信息
 * @param space 主页头图
 * @param following 是否关注此用户 true：已关注 false：未关注 需要登录(Cookie) 未登录为false
 * @param archiveCount 用户稿件数
 * @param articleCount 0 作用尚不明确
 * @param follower 粉丝数
 * @param likeNum 获赞数
 */
@Serializable
data class UserCardData(
    val card: UserCardInfo,
    val space: Space? = null,
    val following: Boolean,
    @SerialName("archive_count")
    val archiveCount: Int,
    @SerialName("article_count")
    val articleCount: Int,
    val follower: Int,
    @SerialName("like_num")
    val likeNum: Int
) {
    /**
     * 用户卡片详细信息
     *
     * @param mid 用户mid
     * @param name 昵称
     * @param approve
     * @param sex 性别 男/女/保密
     * @param rank 用户权限等级 目前应该无任何作用 5000：0级未答题 10000：普通会员 20000：字幕君 25000：VIP 30000：真·职人 32000：管理员
     * @param face 头像链接
     * @param faceNft 是否为 nft 头像 0不是nft头像 1是 nft 头像
     * @param faceNftType 0,1
     * @param displayRank
     * @param regtime
     * @param spacesta
     * @param birthday
     * @param place
     * @param description
     * @param article
     * @param attentions
     * @param fans 粉丝数
     * @param friend 粉丝数
     * @param attention 粉丝数
     * @param sign 签名
     * @param levelInfo 等级
     * @param pendant 头像框信息
     * @param nameplate 勋章信息
     * @param official 认证信息
     * @param officialVerify 认证信息
     * @param vip 大会员信息
     * @param isSeniorMember 是否为硬核会员 0：否 1：是
     */
    @Serializable
    data class UserCardInfo(
        val mid: String,
        val name: String,
        val approve: Boolean,
        val sex: String,
        val rank: Int,
        val face: String,
        @SerialName("face_nft")
        val faceNft: Int,
        @SerialName("face_nft_type")
        val faceNftType: Int,
        @SerialName("DisplayRank")
        val displayRank: String,
        val regtime: Int,
        val spacesta: Int,
        val birthday: String,
        val place: String,
        val description: String,
        val article: Int,
        //val attentions: List<Any> = emptyList(),
        val fans: Int,
        val friend: Int,
        val attention: Int,
        val sign: String,
        @SerialName("level_info")
        val levelInfo: LevelInfo,
        val pendant: Pendant,
        val nameplate: Nameplate,
        @SerialName("Official")
        val official: Official,
        @SerialName("official_verify")
        val officialVerify: OfficialVerify,
        val vip: Vip,
        @SerialName("is_senior_member")
        val isSeniorMember: Int
    )

    /**
     * 主页头图
     *
     * @param sImg 主页头图url 小图
     * @param lImg 主页头图url 正常
     */
    @Serializable
    data class Space(
        @SerialName("s_img")
        val sImg: String,
        @SerialName("l_img")
        val lImg: String
    )
}