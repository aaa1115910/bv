package dev.aaa1115910.bv.player.impl.vlc

import android.content.Context
import dev.aaa1115910.bv.player.VideoPlayerOptions
import dev.aaa1115910.bv.player.factory.PlayerFactory

class VlcPlayerFactory : PlayerFactory<VlcMediaPlayer>() {
    override fun create(context: Context, options: VideoPlayerOptions): VlcMediaPlayer {
        return VlcMediaPlayer(context, options)
    }
}