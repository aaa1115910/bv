package dev.aaa1115910.bv.player.mobile.util

import androidx.compose.runtime.compositionLocalOf

data class MobileVideoPlayerData(
    val currentResolutionCode: Int = 32,
    val currentSpeed: Float = 1.0f,
    val availableResolutionMap: Map<Int, String> = mapOf()
)

val LocalMobileVideoPlayerData = compositionLocalOf { MobileVideoPlayerData() }
