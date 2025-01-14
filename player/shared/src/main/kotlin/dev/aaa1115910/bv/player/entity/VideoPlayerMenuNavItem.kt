package dev.aaa1115910.bv.player.entity

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ClearAll
import androidx.compose.material.icons.outlined.ClosedCaption
import androidx.compose.material.icons.outlined.Image
import androidx.compose.ui.graphics.vector.ImageVector
import dev.aaa1115910.bv.player.shared.R

enum class VideoPlayerMenuNavItem(private val strRes: Int, val icon: ImageVector) {
    Picture(R.string.video_player_menu_nav_picture, Icons.Outlined.Image),
    Danmaku(R.string.video_player_menu_nav_danmaku, Icons.Outlined.ClearAll),
    ClosedCaption(R.string.video_player_menu_nav_subtitle, Icons.Outlined.ClosedCaption);

    fun getDisplayName(context: Context) = context.getString(strRes)
}