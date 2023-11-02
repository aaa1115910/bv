package dev.aaa1115910.biliapi.http.entity.search

import dev.aaa1115910.biliapi.http.entity.search.KeywordSuggest.Result
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonElement

/**
 * @param result 当搜索词为空时为null，当有搜索建议时为[Result]，当有搜索词但无搜索建议时为[List]
 */
@Serializable
data class KeywordSuggest(
    @SerialName("exp_str")
    val expStr: String,
    val code: Int,
    //val cost: Cost,
    val msg: String? = null,
    val result: JsonElement? = null,
    @Transient
    val suggests: MutableList<Result.Tag> = mutableListOf(),
    //@SerialName("page caches")
    //val pageCaches: PageCaches,
    //val sengine: Sengine,
    val stoken: String
) {
    @Serializable
    data class Cost(
        val about: SearchCost
    )

    @Serializable
    data class Result(
        val tag: List<Tag>
    ) {
        /**
         * @param value 关键词内容
         * @param term
         * @param ref 0
         * @param name 显示内容 在无高亮显示时与value相同 有高亮显示时带有<em class="suggest_high_light">的xml标签
         * @param spid
         */
        @Serializable
        data class Tag(
            val value: String,
            val term: String,
            val ref: Int,
            val name: String,
            val spid: Int
        )
    }

    @Serializable
    data class PageCaches(
        @SerialName("save cache")
        val saveCache: String
    )

    @Serializable
    data class Sengine(
        val usage: Int
    )
}