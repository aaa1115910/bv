package dev.aaa1115910.bv.player.entity

import android.content.Context
import dev.aaa1115910.bv.R

/**
 * 播放完成后加载下一个的处理策略
 */
enum class PlayerLoadNextAction(val value: Int) {
    /** 什么都不做 */
    DoNothing(0),
    /** 播放推荐视频 */
    PlayRecommend(1),
    /** 播放剧集和分P的下一个 */
    PlayNextPart(2),
    /** 播放剧集和分P的下一个或者推荐视频（没有下一个时播放推荐视频） */
    PlayNextPartOrRecommend(3);

    fun displayName(context: Context): String = when (this) {
        DoNothing -> context.getString(R.string.settings_player_load_next_action_do_nothing)
        PlayRecommend -> context.getString(R.string.settings_player_load_next_action_play_recommend)
        PlayNextPart -> context.getString(R.string.settings_player_load_next_action_play_next_part)
        PlayNextPartOrRecommend -> context.getString(R.string.settings_player_load_next_action_play_next_part_or_recommend)
    }

    companion object {
        fun fromValue(value: Int): PlayerLoadNextAction = entries.find { it.value == value } ?: DoNothing
    }
}
