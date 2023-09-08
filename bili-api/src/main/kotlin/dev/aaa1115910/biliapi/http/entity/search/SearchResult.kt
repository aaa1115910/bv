package dev.aaa1115910.biliapi.http.entity.search

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * 搜索结果
 */
@Serializable
data class SearchResultData(
    val seid: String,
    val page: Int,
    @SerialName("pagesize")
    val pageSize: Int,
    val numResults: Int,
    val numPages: Int,
    @SerialName("suggest_keyword")
    val suggestKeyword: String,
    @SerialName("rqt_type")
    val rqtType: String,
    @SerialName("cost_time")
    val costTime: SearchCost,
    @SerialName("exp_list")
    val expList: JsonElement? = null,
    @SerialName("egg_hit")
    val eggHit: Int,
    @SerialName("pageinfo")
    val pageInfo: PageInfo? = null,
    @SerialName("top_tlist")
    val topTList: TopTList? = null,
    @SerialName("show_column")
    val showColumn: Int,
    @SerialName("show_module_list")
    val showModuleList: List<String>? = null,
    @SerialName("app_display_option")
    val appDisplayOption: AppDisplayOption? = null,
    @SerialName("in_black_key")
    val inBlackKey: Int,
    @SerialName("in_white_key")
    val inWhiteKey: Int,
    val result: List<JsonElement>,
    @Transient
    val searchAllResults: MutableList<SearchResult<SearchResultItem>> = mutableListOf(),
    @Transient
    val searchTypeResults: MutableList<SearchResultItem> = mutableListOf(),
    @SerialName("is_search_page_grayed")
    val isSearchPageGrayed: Int? = null
) {
    init {
        result.forEach { searchResultJsonElement ->
            val searchResultJsonObject = searchResultJsonElement.jsonObject
            var resultType = searchResultJsonObject["result_type"]?.jsonPrimitive?.content
            val json = Json {
                coerceInputValues = true
                ignoreUnknownKeys = true
                prettyPrint = true
            }
            if (resultType != null) {
                // 综合搜索
                val searchResultDataJsonArray = searchResultJsonObject["data"]!!.jsonArray
                val data = when (resultType) {
                    "activity" -> json.decodeFromJsonElement<List<SearchActivityResult>>(
                        searchResultDataJsonArray
                    )

                    "media_bangumi", "media_ft" -> json.decodeFromJsonElement<List<SearchMediaResult>>(
                        searchResultDataJsonArray
                    )

                    "video" -> json.decodeFromJsonElement<List<SearchVideoResult>>(
                        searchResultDataJsonArray
                    )

                    else -> {
                        listOf()
                    }
                }

                val resultResult = SearchResult(
                    resultType = searchResultJsonObject["result_type"]!!.jsonPrimitive.content,
                    data = data
                )
                searchAllResults.add(resultResult)
            } else {
                // 分类搜索
                resultType = searchResultJsonObject["type"]?.jsonPrimitive?.content
                val data = when (resultType) {
                    "activity" -> json.decodeFromJsonElement<SearchActivityResult>(
                        searchResultJsonObject
                    )

                    "article" -> json.decodeFromJsonElement<SearchArticleResult>(
                        searchResultJsonObject
                    )

                    "bili_user" -> json.decodeFromJsonElement<SearchBiliUserResult>(
                        searchResultJsonObject
                    )

                    // TODO live search result
                    "live" -> return@forEach

                    "media_bangumi", "media_ft" -> json.decodeFromJsonElement<SearchMediaResult>(
                        searchResultJsonObject
                    )

                    "topic" -> json.decodeFromJsonElement<SearchTopicResult>(searchResultJsonObject)
                    "video" -> json.decodeFromJsonElement<SearchVideoResult>(searchResultJsonObject)

                    else -> {
                        return@forEach
                    }
                }

                searchTypeResults.add(data)
            }
        }
    }

    @Serializable
    data class PageInfo(
        @SerialName("live_room")
        val liveRoom: PageInfoData? = null,
        val pgc: PageInfoData? = null,
        @SerialName("operation_card")
        val operationCard: PageInfoData? = null,
        val tv: PageInfoData? = null,
        val movie: PageInfoData? = null,
        @SerialName("bili_user")
        val biliUser: PageInfoData? = null,
        @SerialName("live_master")
        val liveMaster: PageInfoData? = null,
        @SerialName("live_all")
        val liveAll: PageInfoData? = null,
        val topic: PageInfoData? = null,
        @SerialName("upuser")
        val upUser: PageInfoData? = null,
        val live: PageInfoData? = null,
        val video: PageInfoData? = null,
        val user: PageInfoData? = null,
        val bangumi: PageInfoData? = null,
        val activity: PageInfoData? = null,
        @SerialName("media_ft")
        val mediaFt: PageInfoData? = null,
        val article: PageInfoData? = null,
        @SerialName("media_bangumi")
        val mediaBangumi: PageInfoData? = null,
        val special: PageInfoData? = null,
        @SerialName("live_user")
        val liveUser: PageInfoData? = null
    ) {
        @Serializable
        data class PageInfoData(
            var numResults: Int,
            val total: Int,
            val pages: Int
        )
    }

    @Serializable
    data class TopTList(
        @SerialName("live_room")
        val liveRoom: Int,
        val pgc: Int,
        @SerialName("operation_card")
        val operationCard: Int,
        val tv: Int,
        val movie: Int,
        @SerialName("bili_user")
        val biliUser: Int,
        @SerialName("live_master")
        val liveMaster: Int,
        @SerialName("topic")
        val topic: Int,
        @SerialName("upuser")
        val upUser: Int,
        val live: Int,
        val video: Int,
        val user: Int,
        val bangumi: Int,
        val activity: Int,
        @SerialName("media_ft")
        val mediaFt: Int,
        val article: Int,
        @SerialName("media_bangumi")
        val mediaBangumi: Int,
        val special: Int,
        val card: Int,
        @SerialName("live_user")
        val liveUser: Int,
    )

    @Serializable
    data class AppDisplayOption(
        @SerialName("is_search_page_grayed")
        val isSearchPageGrayed: Int
    )
}

@Serializable
data class SearchResult<T>(
    @SerialName("result_type")
    val resultType: String,
    val data: List<T>
)