package dev.aaa1115910.bv.player.entity

import android.content.Context
import dev.aaa1115910.bv.player.shared.R

enum class VideoRotation(val degrees: Float, private val labelRes: Int) {
    Original(0f, R.string.video_rotation_original),
    Rotate90(90f, R.string.video_rotation_90),
    RotateNeg90(-90f, R.string.video_rotation_negative_90),
    Rotate180(180f, R.string.video_rotation_180);

    fun getDisplayName(context: Context): String = context.getString(labelRes)

    val shouldSwapDimensions: Boolean
        get() = degrees % 180f != 0f

    companion object {
        fun fromDegrees(degrees: Float): VideoRotation = when (degrees.toInt()) {
            90 -> Rotate90
            -90 -> RotateNeg90
            180, -180 -> Rotate180
            else -> Original
        }
    }
}
