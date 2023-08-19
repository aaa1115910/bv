package dev.aaa1115910.biliapi.http.entity.subtitle

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * 字幕信息
 * @param allowSubmit 是否允许提交字幕
 * @param list 字幕列表
 */
@Serializable
data class Subtitle(
    val allowSubmit: Boolean = false,
    val list: List<SubtitleListItem> = emptyList()
)

/**
 * 字幕列表项
 *
 * @param id 字幕id
 * @param lan 字幕语言
 * @param lanDoc 字幕语言名称
 * @param isLock 是否锁定
 * @param authorMid 字幕上传者mid
 * @param subtitleUrl json格式字幕文件url
 * @param author 字幕上传者信息
 */
@Serializable
data class SubtitleListItem(
    val id: Long,
    val lan: String,
    @SerialName("lan_doc")
    val lanDoc: String,
    @SerialName("is_lock")
    val isLock: Boolean,
    @SerialName("author_mid")
    val authorMid: Long? = null,
    @SerialName("subtitle_url")
    val subtitleUrl: String,
    val author: SubtitleAuthor
)

/**
 * 字幕作者
 *
 * @param mid 字幕上传者mid
 * @param name 字幕上传者昵称
 * @param sex 字幕上传者性别 男 女 保密
 * @param face 字幕上传者头像url
 * @param sign 字幕上传者签名
 * @param rank 10000 作用尚不明确
 * @param birthday 0 作用尚不明确
 * @param isFakeAccount 0 作用尚不明确
 * @param isDeleted 0 作用尚不明确
 */
@Serializable
data class SubtitleAuthor(
    val mid: Long,
    val name: String,
    val sex: String,
    val face: String,
    val sign: String,
    val rank: Int,
    val birthday: Int,
    @SerialName("is_fake_account")
    val isFakeAccount: Int,
    @SerialName("is_deleted")
    val isDeleted: Int
)
