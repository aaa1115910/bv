package dev.aaa1115910.bv.sponsorblock.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Segment(
    @SerialName("segment")
    val segment: List<Float>,
    @SerialName("UUID")
    val uuid: String,
    val category: String,
    val actionType: String,
    val locked: Int,
    val votes: Int,
    val videoDuration: Int
)
