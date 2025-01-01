package dev.aaa1115910.biliapi.entity.rank

import dev.aaa1115910.biliapi.entity.ugc.UgcItem

data class PopularVideoData(
    val list: List<UgcItem>,
    val nextPage: PopularVideoPage,
    val noMore: Boolean
)

data class PopularVideoPage(
    val nextWebPageSize: Int = 20,
    val nextWebPageNumber: Int = 1,
    val nextAppIndex: Int = 0,
)
