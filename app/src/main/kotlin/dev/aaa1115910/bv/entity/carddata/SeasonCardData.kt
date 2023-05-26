package dev.aaa1115910.bv.entity.carddata

import dev.aaa1115910.biliapi.http.entity.search.SearchMediaResult

data class SeasonCardData(
    val seasonId: Int,
    val title: String,
    val subTitle: String? = null,
    val cover: String,
    val rating: String? = null,
    val badge: SearchMediaResult.Badge? = null,
)
