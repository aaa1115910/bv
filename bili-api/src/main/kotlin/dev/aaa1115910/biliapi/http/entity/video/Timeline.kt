package dev.aaa1115910.biliapi.http.entity.video

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * 放送时间表
 *
 * @param date 当日日期
 * @param dateTs 当日日期时间戳
 * @param dayOfWeek 一周中第几天 ∈N∩[1,7]
 * @param episodes 剧集列表
 * @param isToday 是否今日
 */
@Serializable
data class Timeline(
    val date: String,
    @SerialName("date_ts")
    val dateTs: Int,
    @SerialName("day_of_week")
    val dayOfWeek: Int,
    val episodes: List<Episode> = emptyList(),
    @SerialName("is_today")
    private val _isToday: Int,
    @Transient val isToday: Boolean = _isToday == 1
) {
    /**
     * 时间表剧集信息
     *
     * @param cover 封面图url
     * @param delay 是否推迟
     * @param delayId 推迟一话epid
     * @param delayIndex 推迟一话名称
     * @param delayReason 推迟原因
     * @param epCover 最新一话图url
     * @param episodeId 最新一话的epid
     * @param pubIndex 最新一话名称
     * @param pubTime 发布时间
     * @param pubTs 发布时间戳
     * @param published 是否已发布
     * @param follows
     * @param plays
     * @param seasonId 剧集ssid
     * @param squareCover 缩略图url
     * @param title 剧集标题
     */
    @Serializable
    data class Episode(
        val cover: String,
        val delay: Int,
        @SerialName("delay_id")
        val delayId: Int,
        @SerialName("delay_index")
        val delayIndex: String,
        @SerialName("delay_reason")
        val delayReason: String,
        @SerialName("ep_cover")
        val epCover: String,
        @SerialName("episode_id")
        val episodeId: Int,
        val follows: String,
        val plays: String,
        @SerialName("pub_index")
        val pubIndex: String,
        @SerialName("pub_time")
        val pubTime: String,
        @SerialName("pub_ts")
        val pubTs: Int,
        val published: Int,
        @SerialName("season_id")
        val seasonId: Int,
        @SerialName("square_cover")
        val squareCover: String,
        val title: String
    )
}

enum class TimelineType(val id: Int) {
    Anime(1), Movie(3), GuoChuang(4)
}