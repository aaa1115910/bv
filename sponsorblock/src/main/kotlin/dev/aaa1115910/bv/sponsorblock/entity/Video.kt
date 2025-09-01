package dev.aaa1115910.bv.sponsorblock.entity

import kotlinx.serialization.Serializable

@Serializable
data class Video(
    val videoID: String,
    val segments: List<Segment>
)
