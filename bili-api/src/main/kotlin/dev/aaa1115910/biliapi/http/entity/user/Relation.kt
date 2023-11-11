package dev.aaa1115910.biliapi.http.entity.user

import dev.aaa1115910.biliapi.http.util.CommonEnumIntSerializer
import dev.aaa1115910.biliapi.http.util.SerialEnum
import dev.aaa1115910.biliapi.http.util.serial
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * 相互关系
 *
 * @param relation 目标用户对于本用户的属性
 * @param beRelation 本用户对于目标用户的属性
 */
@Serializable
data class RelationData(
    val relation: Relation,
    @SerialName("be_relation")
    val beRelation: Relation
)

/**
 * 用户关系
 *
 * @param mid 用户mid
 * @param attribute 关注属性
 * @param mtime 关注对方时间 互关后刷新时间
 * @param tag 分组id null默认分组 array存在至少一个分组
 * @param special 特别关注标志 0：否 1：是
 * @param isSpecialFollowing 是否已特别关注
 */
@Serializable
data class Relation(
    val mid: Long,
    val attribute: RelationType,
    val mtime: Int,
    val tag: List<Int>? = null,
    private val special: Int,
    @Transient
    val isSpecialFollowing: Boolean = special == 1
)

private object RelationTypeSerializer : CommonEnumIntSerializer<RelationType>(
    "RelationType",
    RelationType.entries.toTypedArray(),
    RelationType.entries.toTypedArray().serial()
)

@Serializable(with = RelationTypeSerializer::class)
enum class RelationType(override val serialNumber: Int) : SerialEnum {
    None(0), FollowedQuietly(1), Followed(2), BothFollowed(6), BlackList(128)
}

@Serializable
data class RelationStat(
    val black: Int,
    val follower: Int,
    val following: Int,
    val mid: Long,
    val whisper: Int
)