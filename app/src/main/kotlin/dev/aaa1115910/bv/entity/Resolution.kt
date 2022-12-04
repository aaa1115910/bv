package dev.aaa1115910.bv.entity

import android.content.Context
import dev.aaa1115910.bv.R

enum class Resolution(val code: Int, private val strRes: Int) {
    R240P(6, R.string.resolution_240p),
    R360P(16, R.string.resolution_360p),
    R480P(32, R.string.resolution_480p),
    R720P(64, R.string.resolution_720p),
    R720P60(74, R.string.resolution__720p_60),
    R1080P(80, R.string.resolution_1080p),
    R1080PPlus(112, R.string.resolution_1080p_plus),
    R1080P60(116, R.string.resolution_1080p_60),
    R4K(120, R.string.resolution_4k),
    RHdr(125, R.string.resolution_hdr),
    RDolby(126, R.string.resolution_dolby_vision),
    R8K(127, R.string.resolution_8k);

    companion object {
        fun fromCode(code: Int) = runCatching {
            Resolution.values().find { it.code == code }
        }.getOrDefault(R1080P)
    }

    fun getDisplayName(context: Context) = context.getString(this.strRes)
}