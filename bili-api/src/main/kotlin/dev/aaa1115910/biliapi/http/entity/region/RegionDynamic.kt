package dev.aaa1115910.biliapi.http.entity.region

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 分区动态
 * @param banner 轮播图
 * @param card 卡片推荐位
 * @param cBottom 往下滚动页面加载数据使用的参数
 * @param cTop 往上滚动页面加载数据使用的参数
 * @param new 推荐内容，可看作加载分区视频列表的第一页
 */
@Serializable
data class RegionDynamic(
    val banner: Banner? = null,
    val card: List<Card> = emptyList(),
    @SerialName("cbottom")
    val cBottom: Long,
    @SerialName("ctop")
    val cTop: Long,
    val new: List<RegionDynamicList.Item>
) {
    @Serializable
    data class Banner(
        val top: List<Top>
    ) {
        @Serializable
        data class Top(
            @SerialName("client_ip")
            val clientIp: String? = null,
            @SerialName("cm_mark")
            val cmMark: Int,
            val hash: String,
            val id: Int,
            val image: String,
            val index: Int,
            @SerialName("is_ad")
            val isAd: Boolean? = null,
            @SerialName("is_ad_loc")
            val isAdLoc: Boolean? = null,
            @SerialName("request_id")
            val requestId: String,
            @SerialName("resource_id")
            val resourceId: Int,
            @SerialName("server_type")
            val serverType: Int,
            @SerialName("src_id")
            val srcId: Int? = null,
            val title: String,
            val uri: String
        )
    }

    @Serializable
    data class Card(
        val body: List<Item>,
        @SerialName("card_id")
        val cardId: Int,
        val title: String,
        val type: String
    )

    @Serializable
    data class Item(
        val cover: String,
        @SerialName("cover_left_icon_1")
        val coverLeftIcon1: Int? = null,
        @SerialName("cover_left_text_1")
        val coverLeftText1: String? = null,
        val danmaku: Int? = null,
        val duration: Int? = null,
        val face: String? = null,
        val favourite: Int? = null,
        val goto: String,
        val like: Int? = null,
        val name: String? = null,
        val param: String,
        val play: Int? = null,
        @SerialName("pubdate")
        val pubDate: Int,
        val reply: Int? = null,
        val rid: Int? = null,
        @SerialName("rname")
        val rName: String? = null,
        val title: String,
        val uri: String,
        val children: List<Item>? = null
    )
}