package dev.aaa1115910.bv.player.mobile.util

import androidx.compose.runtime.compositionLocalOf
import dev.aaa1115910.bv.player.mobile.component.DanmakuType

data class MobileVideoPlayerData(
    val currentResolutionCode: Int = 32,
    val currentSpeed: Float = 1.0f,
    val availableResolutionMap: Map<Int, String> = mapOf(),
    val enabledDanmaku: Boolean = false,
    val currentDanmakuTypes: List<DanmakuType> = listOf(DanmakuType.All),
    val currentDanmakuScale: Float = 1f,
    val currentDanmakuOpacity: Float = 1f,
    val currentDanmakuArea: Float = 1f
)

val LocalMobileVideoPlayerData = compositionLocalOf { MobileVideoPlayerData() }
