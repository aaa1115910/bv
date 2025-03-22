package dev.aaa1115910.biliapi.http.entity.season

import dev.aaa1115910.biliapi.http.entity.video.Dimension
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 剧集项
 *
 * @param aid 单集稿件avid
 * @param badge 标签文字 例如会员、限免等
 * @param badgeInfo
 * @param badgeType
 * @param bvid 单集稿件bvid
 * @param cid 视频cid
 * @param cover 单集封面url
 * @param dimension 分辨率信息
 * @param duration 时长
 * @param enableVt
 * @param epId 同 [id]
 * @param from
 * @param id 单集epid
 * @param isViewHide 是否隐藏
 * @param link 单集网页url
 * @param longTitle 单集完整标题
 * @param pubTime 发布时间 时间戳
 * @param pv 0 作用尚不明确
 * @param releaseDate 空 作用尚不明确
 * @param report 仅 App 端
 * @param rights
 * @param shareCopy 《{标题}》+第n话+ 单集完整标题｝
 * @param shareUrl 单集网页url
 * @param shortLink 单集网页url短链接
 * @param skip 跳过片头片尾数据
 * @param stat 视频数据信息，例如播放数、弹幕数等
 * @param statForUnity 视频数据信息，例如播放数、弹幕数等
 * @param status
 * @param subtitle 单集副标题 观看次数文字
 * @param title 单集标题
 * @param vid 单集vid vupload_+{cid}
 */
@Serializable
data class Episode(
    val aid: Long,
    val badge: String,
    @SerialName("badge_info")
    val badgeInfo: BadgeInfo,
    @SerialName("badge_type")
    val badgeType: Int = 0,
    val bvid: String = "",
    val cid: Long,
    val cover: String,
    val dimension: Dimension? = null,
    val duration: Int = 0,
    @SerialName("enable_vt")
    val enableVt: Boolean,
    @SerialName("ep_id")
    val epId: Int = 0,
    val from: String = "",
    val id: Int,
    @SerialName("is_view_hide")
    val isViewHide: Boolean,
    val link: String,
    @SerialName("long_title")
    val longTitle: String = "",
    @SerialName("pub_time")
    val pubTime: Long,
    val pv: Int,
    @SerialName("release_date")
    val releaseDate: String = "",
    val report: Report? = null,
    val rights: EpisodeRights? = null,
    @SerialName("share_copy")
    val shareCopy: String = "",
    @SerialName("share_url")
    val shareUrl: String = "",
    @SerialName("short_link")
    val shortLink: String = "",
    val skip: Skip? = null,
    val stat: Stat? = null,
    @SerialName("stat_for_unity")
    val statForUnity: StatForUnity? = null,
    val status: Int,
    val subtitle: String = "",
    val title: String,
    val vid: String = ""
) {
    /**
     * 标签
     *
     * @param bgColor
     * @param bgColorNight
     * @param text
     */
    @Serializable
    data class BadgeInfo(
        @SerialName("bg_color")
        val bgColor: String,
        @SerialName("bg_color_night")
        val bgColorNight: String,
        val text: String
    )

    /**
     * 剧集版权
     *
     * @param allowDemand
     * @param allowDm
     * @param allowDownload
     * @param areaLimit
     */
    @Serializable
    data class EpisodeRights(
        //@SerialName("allow_demand")
        //val allowDemand: Int,
        @SerialName("allow_dm")
        val allowDm: Int,
        @SerialName("allow_download")
        val allowDownload: Int,
        @SerialName("area_limit")
        val areaLimit: Int
    ) {
        //val isAllowDemand = allowDemand == 1
        val isAllowDm = allowDm == 1
        val isAllowDownload = allowDownload == 1
        val usAreaLimit = areaLimit == 1
    }

    /**
     * 跳过片头片尾
     *
     * @param op OP时间
     * @param ed ED时间
     */
    @Serializable
    data class Skip(
        val op: SkipTime,
        val ed: SkipTime
    ) {
        /**
         * 跳过时间
         *
         * @param start 开始时间
         * @param end 结束时间
         */
        @Serializable
        data class SkipTime(
            val start: Int,
            val end: Int
        )
    }

    @Serializable
    data class Report(
        val aid: String,
        @SerialName("ep_title")
        val epTitle: String,
        val epid: String? = null,
        val position: String,
        @SerialName("season_id")
        val seasonId: String,
        @SerialName("season_type")
        val seasonType: String,
        @SerialName("section_id")
        val sectionId: String,
        @SerialName("section_type")
        val sectionType: String,
        val style: String? = null
    )

    @Serializable
    data class Stat(
        val coin: Int,
        val danmakus: Int,
        val likes: Int,
        val play: Int,
        val reply: Int,
        val vt: Int
    )

    @Serializable
    data class StatForUnity(
        val coin: Int,
        val danmaku: Danmaku,
        val likes: Int,
        val reply: Int,
        val vt: Vt
    ) {
        @Serializable
        data class Danmaku(
            val icon: String,
            @SerialName("pure_text")
            val pureText: String,
            val text: String,
            val value: Int
        )

        @Serializable
        data class Vt(
            val icon: String,
            val text: String,
            val value: Int
        )
    }
}