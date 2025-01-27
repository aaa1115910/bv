package dev.aaa1115910.bv.player.entity

import android.content.Context
import dev.aaa1115910.bv.player.shared.R

enum class Audio(val code: Int, private val strRes: Int) {
    A64K(30216, R.string.audio_64k),
    A132K(30232, R.string.audio_132k),
    A192K(30280, R.string.audio_192k),
    ADolbyAtoms(30250, R.string.audio_dolby_atoms),
    AHiRes(30251, R.string.audio_hi_res);

    companion object {
        fun fromCode(code: Int) = runCatching {
            entries.find { it.code == code }
        }.getOrDefault(A64K)
    }

    fun getDisplayName(context: Context) = context.getString(strRes)
}