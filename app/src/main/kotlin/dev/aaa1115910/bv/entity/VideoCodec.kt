package dev.aaa1115910.bv.entity

import android.content.Context
import dev.aaa1115910.bv.R

enum class VideoCodec(private val strRes: Int) {
    AV1(R.string.video_codec_av1),
    AVC(R.string.video_codec_avc),
    HEVC(R.string.video_codec_hevc),
    DVH1(R.string.video_codec_dvh1);

    companion object {
        fun fromCode(code: Int?) = runCatching {
            values().find { it.ordinal == code }!!
        }.getOrDefault(AVC)
    }

    fun getDisplayName(context: Context) = context.getString(strRes)
}