package dev.aaa1115910.biliapi.entity.user

import dev.aaa1115910.biliapi.entity.video.VideoStat
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * 空间投稿视频
 *
 * @param list 列表信息
 * @param page 页面信息
 * @param episodicButton “播放全部“按钮
 * @param isRisk
 * @param gaiaResType
 * @param gaiaData
 */
@Serializable
data class SpaceVideoData(
    val list: SpaceVideoListItem,
    val page: Page,
    @SerialName("episodic_button")
    val episodicButton: EpisodicButton? = null,
    @SerialName("is_risk")
    val isRisk: Boolean,
    @SerialName("gaia_res_type")
    val gaiaResType: Int,
    @SerialName("gaia_data")
    val gaiaData: JsonElement? = null
) {
    /**
     * @param tlist 投稿视频分区索引
     * @param vlist 投稿视频列表
     */
    @Serializable
    data class SpaceVideoListItem(
        val tlist: Map<String, Tid>? = null,
        val vlist: List<VListItem>
    ) {
        /**
         * @param count 投稿至该分区的视频数
         * @param name 该分区名称
         * @param tid 该分区tid
         */
        @Serializable
        data class Tid(
            var tid: Int,
            val count: Int,
            val name: String
        )

        /**
         * @param aid 稿件avid
         * @param author 视频UP主 不一定为目标用户（合作视频）
         * @param bvid 稿件bvid
         * @param comment 视频评论数
         * @param copyright
         * @param created 投稿时间 时间戳
         * @param description 视频简介
         * @param hideClick
         * @param isPay
         * @param isUnionVideo 是否为合作视频 0：否 1：是
         * @param length 视频长度 MM:SS
         * @param mid 视频UP主mid 不一定为目标用户（合作视频）
         * @param pic 视频封面
         * @param play 视频播放次数 如果视频基本信息API对应的状态为-403视频访问权限不足，数据类型将变为str，如（"play": "--",），于mid79发表的av5132474可见
         * @param review 作用尚不明确
         * @param subtitle
         * @param title 视频标题
         * @param typeid 视频分区tid
         * @param videoReview 视频弹幕数
         * @param isSteinsGate
         * @param isLivePlayback
         * @param meta 合集信息
         * @param isAvoided
         * @param attribute
         */
        @Serializable
        data class VListItem(
            val aid: Long,
            val bvid: String,
            val author: String,
            val comment: Int,
            val copyright: String,
            val created: Long,
            val description: String,
            @SerialName("hide_click")
            val hideClick: Boolean,
            @SerialName("is_pay")
            val isPay: Int,
            @SerialName("is_union_video")
            val isUnionVideo: Int,
            val length: String,
            val mid: Long,
            val pic: String,
            val play: Int,
            val review: Int,
            val subtitle: String,
            val title: String,
            val typeid: Int,
            @SerialName("video_review")
            val videoReview: Int,
            @SerialName("is_steins_gate")
            val isSteinsGate: Int,
            @SerialName("is_live_playback")
            val isLivePlayback: Int,
            val meta: Meta? = null,
            @SerialName("is_avoided")
            private val _isAvoided: Int,
            @SerialName("attribute")
            val attribute: Int
        ) {
            val isAvoided: Boolean = _isAvoided == 1

            /**
             * 合集信息
             */
            @Serializable
            data class Meta(
                val id: Int,
                val title: String,
                val cover: String,
                val mid: Long,
                val intro: String,
                @SerialName("sign_state")
                val signState: Int,
                val attribute: Int,
                val stat: VideoStat,
                @SerialName("ep_count")
                val epCount: Int,
                @SerialName("first_aid")
                val firstAid: Int,
                val ptime: Int,
                @SerialName("ep_num")
                val epNum: Int
            )
        }
    }

    /**
     * @param pageNumber 当前页码
     * @param pageSize 每页项数
     * @param count 总计稿件数
     */
    @Serializable
    data class Page(
        @SerialName("pn")
        val pageNumber: Int,
        @SerialName("ps")
        val pageSize: Int,
        val count: Int,
    )

    /**
     * 全部播放按钮
     *
     * @param text 按钮文字
     * @param uri 全部播放页url
     */
    @Serializable
    data class EpisodicButton(
        val text: String,
        val uri: String
    )
}
