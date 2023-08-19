package dev.aaa1115910.biliapi.http.entity.video

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * 放送时间表数据（App 端）
 *
 * @param currentTimeText 在当前时间旁边显示的提示语，例如 “会一直在你身边的”，“是追番的friends呢！”
 * @param data 时间表数据
 * @param filter 时间表筛选条件
 * @param isNightMode 是否夜间模式？（我感觉没啥用）
 * @param navigationTitle 导航栏标题
 */
@Serializable
data class TimelineAppData(
    @SerialName("current_time_text")
    val currentTimeText: String,
    val data: List<Timeline>,
    val filter: List<TimelineFilter>,
    @SerialName("is_night_mode")
    val isNightMode: Int,
    @SerialName("navigation_title")
    val navigationTitle: String
)

/**
 * 放送时间表
 *
 * @param date 当日日期
 * @param dateTs 当日日期时间戳
 * @param dayOfWeek 一周中第几天 ∈N∩[1,7]
 * @param dayUpdateText 如果无剧集更新则显示的内容，仅 App 端
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
    @SerialName("day_update_text")
    val dayUpdateText: String? = null,
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
     * @param epCover 最新一话图url，仅 Web 端
     * @param enableVt 仅 Web 端
     * @param episodeId 最新一话的epid
     * @param follows 仅 Web 端
     * @param follow 是否已追剧
     * @param plays 仅 Web 端
     * @param pubIndex 最新一话名称
     * @param pubIndexShow 更新最新一话提示语，仅 App 端，例如 “即将更新 第14话”
     * @param pubTime 发布时间
     * @param pubTs 发布时间戳 秒
     * @param published 是否已发布更新
     * @param report 仅 App 端
     * @param seasonId 剧集ssid
     * @param seasonType 剧集类型，仅 App 端
     * @param squareCover 缩略图url
     * @param tags 剧集标签，仅 App 端
     * @param title 剧集标题
     * @param url 剧集url，仅 App 端
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
        @SerialName("enable_vt")
        val enableVt: Boolean = false,
        @SerialName("ep_cover")
        val epCover: String? = null,
        @SerialName("episode_id")
        val episodeId: Int,
        val follows: String? = null,
        @SerialName("follow")
        private val _follow: Int? = null,
        @Transient
        val follow: Boolean = _follow == 1,
        val plays: String? = null,
        @SerialName("pub_index")
        val pubIndex: String,
        @SerialName("pub_index_show")
        val pubIndexShow: String? = null,
        @SerialName("pub_time")
        val pubTime: String,
        @SerialName("pub_ts")
        val pubTs: Int,
        @SerialName("published")
        private val _published: Int,
        @Transient
        val published: Boolean = _published == 1,
        val report: Report? = null,
        @SerialName("season_id")
        val seasonId: Int,
        @SerialName("season_type")
        val seasonType: Int? = null,
        @SerialName("square_cover")
        val squareCover: String,
        val tags: List<Tag> = emptyList(),
        val title: String,
        val url: String? = null
    ) {
        @Serializable
        data class Report(
            val daynumber: Int,
            @SerialName("ep_id")
            val epId: String,
            @SerialName("is_new")
            val isNew: String,
            @SerialName("is_published")
            val isPublished: String,
            @SerialName("season_id")
            val seasonId: Int
        )

        @Serializable
        data class Tag(
            val text: String,
            val type: Int
        )
    }
}

/**
 * 时间表筛选条件，仅 App 端
 *
 * - 全部: 0
 * - 番剧: 1
 * - 我的追番: 2
 * - 国创: 3
 */
@Serializable
data class TimelineFilter(
    val desc: String,
    val type: Int
)