package dev.aaa1115910.biliapi.entity.anime

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * 动画首页数据（旧版）
 *
 * @param ver
 * @param pageType
 * @param carouselList 轮播推荐
 * @param handPickList 话题精选
 * @param handPickRecom 话题精选
 * @param tid
 * @param showBv
 */
@Serializable
data class AnimeHomepageDataV1(
    var ver: JsonElement?,
    val pageType: Int,
    val carouselList: List<AnimeHomepageDataItem>,
    val handPickList: List<AnimeHomepageDataItem>,
    val handPickRecom: List<AnimeHomepageDataItem>,
    val tid: Int,
    val showBv: Boolean
)

@Serializable
data class AnimeHomepageDataItem(
    val badge: String? = null,
    val blink: String,
    val gif: String,
    val id: Int,
    val img: String,
    val index: Int? = null,
    @SerialName("index_type")
    val indexType: Int? = null,
    @SerialName("index_value")
    val indexValue: Int? = null,
    val link: String,
    val simg: String,
    val title: String,
    val wid: Int? = null
)