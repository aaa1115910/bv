package dev.aaa1115910.biliapi.entity.search

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class HotwordResponse(
    @SerialName("exp_str")
    val expStr: String,
    val code: Int,
    val cost: SearchCost,
    val seid: String,
    val timestamp: Int,
    val message: String,
    val list: List<Hotword> = emptyList()
) {
    /**
     * 热门关键词
     *
     * @param status
     * @param callReason
     * @param heatLayer 热门等级 (A,B,C...)
     * @param hotId 热词id
     * @param keyword 关键词
     * @param resourceId
     * @param gotoType
     * @param showName 完整关键词
     * @param pos 名次 1-10
     * @param wordType 条目属性 均返回8
     * @param id 名次 1-10
     * @param gotoValue
     * @param statDatas
     * @param liveId
     * @param nameType
     * @param icon 图标url
     */
    @Serializable
    data class Hotword(
        val status: String,
        @SerialName("call_reason")
        val callReason: Int,
        @SerialName("heat_layer")
        val heatLayer: String,
        @SerialName("hot_id")
        val hotId: Int,
        val keyword: String,
        @SerialName("resource_id")
        val resourceId: Int,
        @SerialName("goto_type")
        val gotoType: Int,
        @SerialName("show_name")
        val showName: String,
        val pos: Int,
        @SerialName("word_type")
        val wordType: Int,
        val id: Int,
        val score: Double,
        @SerialName("goto_value")
        val gotoValue: String,
        @SerialName("stat_datas")
        val statDatas: StatDatas,
        @SerialName("live_id")
        val liveId: List<JsonElement> = emptyList(),
        @SerialName("name_type")
        val nameType: String,
        val icon: String
    ) {
        @Serializable
        data class StatDatas(
            @SerialName("is_commercial")
            val isCommercial: String
        )
    }
}