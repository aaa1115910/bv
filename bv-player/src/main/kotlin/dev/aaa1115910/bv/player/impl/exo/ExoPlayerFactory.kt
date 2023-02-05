package dev.aaa1115910.bv.player.impl.exo

import android.content.Context
import dev.aaa1115910.bv.player.factory.PlayerFactory

class ExoPlayerFactory : PlayerFactory<ExoMediaPlayer>() {
    override fun create(context: Context): ExoMediaPlayer {
        return ExoMediaPlayer(context)
    }
}