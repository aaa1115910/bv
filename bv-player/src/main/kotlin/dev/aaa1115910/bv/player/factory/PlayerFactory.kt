package dev.aaa1115910.bv.player.factory

import android.content.Context
import dev.aaa1115910.bv.player.AbstractVideoPlayer

abstract class PlayerFactory<T : AbstractVideoPlayer> {
    abstract fun create(context: Context): T
}