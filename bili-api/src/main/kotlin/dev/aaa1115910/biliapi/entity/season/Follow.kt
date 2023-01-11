package dev.aaa1115910.biliapi.entity.season

import kotlinx.serialization.Serializable

@Serializable
data class FollowData(
    val fmid: Int,
    val relation: Boolean,
    val status: Int,
    val toast: String
)