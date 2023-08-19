package dev.aaa1115910.biliapi.http.entity.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 等级信息
 *
 * @param currentLevel 当前等级 0-6级
 * @param currentMin 当前等级经验最低值
 * @param currentExp 当前经验
 * @param nextExp 升级下一等级需达到的经验
 */
@Serializable
data class LevelInfo(
    @SerialName("current_level")
    val currentLevel: Int,
    @SerialName("current_min")
    val currentMin: Int,
    @SerialName("current_exp")
    val currentExp: Int,
    @SerialName("next_exp")
    val nextExp: Int
)