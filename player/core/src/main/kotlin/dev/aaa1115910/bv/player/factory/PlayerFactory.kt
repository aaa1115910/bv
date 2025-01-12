package dev.aaa1115910.bv.player.factory

import android.content.Context
import dev.aaa1115910.bv.player.AbstractVideoPlayer
import dev.aaa1115910.bv.player.VideoPlayerOptions

abstract class PlayerFactory<T : AbstractVideoPlayer> {
    abstract fun create(context: Context, options: VideoPlayerOptions): T
}