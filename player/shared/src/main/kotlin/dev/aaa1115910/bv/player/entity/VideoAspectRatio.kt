package dev.aaa1115910.bv.player.entity

import android.content.Context
import dev.aaa1115910.bv.player.shared.R

enum class VideoAspectRatio(private val strRes: Int) {
    Default(R.string.video_aspect_ratio_default),
    FourToThree(R.string.video_aspect_ratio_four_to_three),
    SixteenToNine(R.string.video_aspect_ratio_sixteen_to_nine);

    fun getDisplayName(context: Context) = context.getString(strRes)
}