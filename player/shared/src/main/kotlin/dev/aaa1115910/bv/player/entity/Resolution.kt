package dev.aaa1115910.bv.player.entity

import android.content.Context
import dev.aaa1115910.bv.player.shared.R

enum class Resolution(val code: Int, private val strResLong: Int, private val strResShort: Int) {
    R240P(6, R.string.resolution_240p, R.string.resolution_240p_short),
    R360P(16, R.string.resolution_360p, R.string.resolution_360p_short),
    R480P(32, R.string.resolution_480p, R.string.resolution_480p_short),
    R720P(64, R.string.resolution_720p, R.string.resolution_720p_short),
    R720P60(74, R.string.resolution__720p_60, R.string.resolution_720p_60_short),
    R1080P(80, R.string.resolution_1080p, R.string.resolution_1080p_short),
    R1080PPlus(112, R.string.resolution_1080p_plus, R.string.resolution_1080p_plus_short),
    R1080P60(116, R.string.resolution_1080p_60, R.string.resolution_1080p_60_short),
    R4K(120, R.string.resolution_4k, R.string.resolution_4k_short),
    RHdr(125, R.string.resolution_hdr, R.string.resolution_hdr_short),
    RDolby(126, R.string.resolution_dolby_vision, R.string.resolution_dolby_bision_short),
    R8K(127, R.string.resolution_8k, R.string.resolution_8k_short);

    companion object {
        fun fromCode(code: Int) = runCatching {
            entries.find { it.code == code }
        }.getOrDefault(R1080P)
    }

    fun getDisplayName(context: Context) = context.getString(strResLong)
    fun getShortDisplayName(context: Context) = context.getString(strResShort)
}