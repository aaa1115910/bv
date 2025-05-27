package dev.aaa1115910.bv.player.entity

import android.content.Context
import dev.aaa1115910.bv.player.shared.R

enum class VideoPlayerPictureMenuItem(private val strRes: Int) {
    PlaySpeed(R.string.video_player_menu_picture_play_speed),
    Resolution(R.string.video_player_menu_picture_resolution),
    Codec(R.string.video_player_menu_picture_codec),
    AspectRatio(R.string.video_player_menu_picture_aspect_ratio),
    Audio(R.string.video_player_menu_picture_audio);

    fun getDisplayName(context: Context) = context.getString(strRes)
}