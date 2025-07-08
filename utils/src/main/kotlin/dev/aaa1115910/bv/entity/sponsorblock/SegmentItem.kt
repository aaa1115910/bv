package dev.aaa1115910.bv.entity.sponsorblock

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class SegmentItem(
    @SerialName("segment")
    val segmentTimeSeconds: List<Float>, // [startTime, endTime] in seconds
    @SerialName("UUID")
    val uuid: String,
    val category: String,
    // videoDuration from API can be int or string "0", coerce to Float
    @SerialName("videoDuration")
    val videoDuration: Float = 0f,
    val actionType: String, // "skip", "mute", "full", "poi", "chapter"
    val locked: Int = 0,
    val votes: Int = 0,
    val cid: Long? = null // Can be null if cid is provided in request
) {
    @Transient
    val startTimeSeconds: Float = segmentTimeSeconds.getOrNull(0) ?: 0f
    @Transient
    val endTimeSeconds: Float = segmentTimeSeconds.getOrNull(1) ?: 0f

    @Transient
    val startTimeMillis: Long = (startTimeSeconds * 1000).toLong()
    @Transient
    val endTimeMillis: Long = (endTimeSeconds * 1000).toLong()
}
