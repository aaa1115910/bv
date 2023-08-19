package dev.aaa1115910.biliapi.http.entity.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 我的信息
 *
 * @param mid 用户mid
 * @param name 昵称
 * @param sex 性别 男/女/保密
 * @param face 头像链接
 * @param sign 签名
 * @param rank 用户权限等级 目前应该无任何作用 5000：0级未答题 10000：普通会员 20000：字幕君 25000：VIP 30000：真·职人 32000：管理员
 * @param level 当前等级 0-6级
 * @param jointime 注册时间 此接口返回恒为0
 * @param moral 节操 默认70
 * @param silence 封禁状态 0：正常 1：被封
 * @param emailStatus 已验证邮箱 0：未验证 1：已验证
 * @param telStatus 已验证手机号	 0：未验证 1：已验证
 * @param identification 1
 * @param vip 会员信息
 * @param pendant 头像框信息
 * @param nameplate 勋章信息
 * @param official 认证信息
 * @param birthday 生日 时间戳
 * @param isTourist 会员信息
 * @param isFakeAccount
 * @param pinPrompting
 * @param isDeleted
 * @param inRegAudit
 * @param isRipUser
 * @param profession 专业资质信息
 * @param faceNft 是否为 nft 头像 0不是nft头像 1是 nft 头像
 * @param faceNftNew
 * @param isSeniorMember 是否为硬核会员 0：否 1：是
 * @param honours
 * @param digitalId
 * @param digitalType
 * @param levelExp
 * @param coins 硬币数
 * @param following 粉丝数
 * @param follower 粉丝数
 */
@Serializable
data class MyInfoData(
    val mid: Long,
    val name: String,
    val sex: String,
    val face: String,
    val sign: String,
    val rank: Int,
    val level: Int,
    val jointime: Int,
    val moral: Int,
    val silence: Int,
    @SerialName("email_status")
    val emailStatus: Int,
    @SerialName("tel_status")
    val telStatus: Int,
    val identification: Int,
    val vip: Vip,
    val pendant: Pendant,
    val nameplate: Nameplate,
    val official: Official,
    val birthday: Long,
    @SerialName("is_tourist")
    val isTourist: Int,
    @SerialName("is_fake_account")
    val isFakeAccount: Int,
    @SerialName("pin_prompting")
    val pinPrompting: Int,
    @SerialName("is_deleted")
    val isDeleted: Int,
    @SerialName("in_reg_audit")
    val inRegAudit: Int,
    @SerialName("is_rip_user")
    val isRipUser: Boolean,
    val profession: Profession,
    @SerialName("face_nft")
    val faceNft: Int,
    @SerialName("face_nft_new")
    val faceNftNew: Int,
    @SerialName("is_senior_member")
    val isSeniorMember: Int,
    val honours: UserHonours,
    @SerialName("digital_id")
    val digitalId: String,
    @SerialName("digital_type")
    val digitalType: Int,
    @SerialName("level_exp")
    val levelExp: LevelInfo,
    val coins: Float,
    val following: Int,
    val follower: Int
)
