package dev.aaa1115910.biliapi.http.entity.search

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class WebSearchSquareData(
    val trending: Trending
) {
    @Serializable
    data class Trending(
        val title: String,
        @SerialName("trackid")
        val trackId: String,
        val list: List<Hotword>,
        @SerialName("top_list")
        val topList: JsonElement? = null
    )
}

/**
 * 热门关键词
 *
 * @param keyword 关键词
 * @param showName 完整关键词
 * @param icon 图标 url
 * @param uri
 * @param goto
 */
@Serializable
data class Hotword(
    val keyword: String,
    @SerialName("show_name")
    val showName: String,
    val icon: String,
    val uri: String,
    val goto: String
)

@Serializable
data class AppSearchSquareData(
    val type: String,
    val title: String,
    val data: SquareData? = null,
    @SerialName("search_ranking_meta")
    val searchRankingMeta: SearchRankingMeta? = null,
    @SerialName("history_hotword_display")
    val historyHotwordDisplay: Int
) {
    @Serializable
    data class SquareData(
        @SerialName("trackid")
        val trackId: String,
        val title: String? = null,
        val pages: Int? = null,
        @SerialName("exp_str")
        val expStr: String? = null,
        val list: List<SquareDataItem> = emptyList(),
        @SerialName("hotword_egg_info")
        val hotwordEggInfo: Int? = null
    ) {
        @Serializable
        data class SquareDataItem(
            val keyword: String? = null,
            val status: String? = null,
            @SerialName("name_type")
            val nameType: String? = null,
            @SerialName("show_name")
            val showName: String? = null,
            @SerialName("word_type")
            val wordType: Int? = null,
            val icon: String? = null,
            val position: Int,
            @SerialName("module_id")
            val moduleId: Int? = null,
            @SerialName("resource_id")
            val resourceId: Int? = null,
            @SerialName("live_id")
            val liveId: List<Int>? = null,
            @SerialName("show_live_icon")
            val showLiveIcon: Boolean? = null,
            @SerialName("hot_id")
            val hotId: Int? = null,
            @SerialName("stat_datas")
            val statDatas: StatDatas? = null,
            val title: String? = null,
            val param: String? = null,
            val type: String? = null,
            val id: Long? = null,
            @SerialName("pub_time")
            val pubTime: String? = null,
            @SerialName("is_sug_style_exp")
            val isSugStyleExp: Int? = null,
            @SerialName("more_search_type")
            val moreSearchType: Int? = null,
            @SerialName("share_from")
            val shareFrom: String? = null
        ) {
            @Serializable
            data class StatDatas(
                @SerialName("is_commercial")
                val isCommercial: Int
            )
        }
    }

    @Serializable
    data class SearchRankingMeta(
        @SerialName("open_search_ranking")
        val openSearchRanking: Boolean,
        val text: String,
        val link: String
    )
}

@Serializable
data class SearchTendingData(
    @SerialName("trackid")
    val trackId: String,
    val list: List<Hotword>,
    @SerialName("exp_str")
    val expStr: String? = null,
    @SerialName("hotword_egg_info")
    val hotwordEggInfo: Int
) {
    @Serializable
    data class Hotword(
        val position: Int,
        val keyword: String,
        @SerialName("show_name")
        val showName: String,
        @SerialName("word_type")
        val wordType: Int? = null,
        val icon: String? = null,
        @SerialName("hot_id")
        val hotId: Int,
        @SerialName("is_commercial")
        val isCommercial: Int
    )
}