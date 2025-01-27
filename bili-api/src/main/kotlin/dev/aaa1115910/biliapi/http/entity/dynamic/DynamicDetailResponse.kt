package dev.aaa1115910.biliapi.http.entity.dynamic

import kotlinx.serialization.Serializable

@Serializable
data class DynamicDetailData(
    val item: DynamicItem
)
