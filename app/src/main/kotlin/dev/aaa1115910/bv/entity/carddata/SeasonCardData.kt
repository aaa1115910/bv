package dev.aaa1115910.bv.entity.carddata

import dev.aaa1115910.biliapi.entity.search.SearchMediaResult

data class SeasonCardData(
    val seasonId: Int,
    val title: String,
    val cover: String,
    val badge: SearchMediaResult.Badge? = null,
)
