package dev.aaa1115910.bv.entity

import android.content.Context
import dev.aaa1115910.bv.R

enum class VideoCodec(private val strRes: Int, val prefix: String) {
    AVC(R.string.video_codec_avc, "avc1"),
    HEVC(R.string.video_codec_hevc, "hev1"),
    AV1(R.string.video_codec_av1, "av01"),
    DVH1(R.string.video_codec_dvh1, "dvh1");

    companion object {
        fun fromCode(code: Int?) = runCatching {
            values().find { it.ordinal == code }!!
        }.getOrDefault(AVC)

        fun fromCodecString(codec: String) = runCatching {
            values().forEach {
                if (codec.startsWith(it.prefix)) return@runCatching it
            }
            return@runCatching null
        }.getOrNull()
    }

    fun getDisplayName(context: Context) = context.getString(strRes)
}