package dev.aaa1115910.bv.player.impl.exo

import android.content.Context
import dev.aaa1115910.bv.player.VideoPlayerOptions
import dev.aaa1115910.bv.player.factory.PlayerFactory

class ExoPlayerFactory : PlayerFactory<ExoMediaPlayer>() {
    override fun create(context: Context, options: VideoPlayerOptions): ExoMediaPlayer {
        return ExoMediaPlayer(context, options)
    }
}