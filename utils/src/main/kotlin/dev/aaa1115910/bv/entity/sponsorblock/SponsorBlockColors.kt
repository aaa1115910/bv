package dev.aaa1115910.bv.entity.sponsorblock

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt

object SponsorBlockColors {
    // Default colors as HEX strings
    val DefaultCategoryColorsHex: Map<String, String> = mapOf(
        SponsorBlockCategories.SPONSOR to "#4CAF50",          // 广告：绿色
        SponsorBlockCategories.SELF_PROMO to "#FFEB3B",       // 无偿/自我推广：黄色
        SponsorBlockCategories.INTRO to "#00BCD4",            // 过场/开场动画：青色
        SponsorBlockCategories.OUTRO to "#2196F3",            // 鸣谢/结束画面：蓝色
        SponsorBlockCategories.INTERACTION to "#E91E63",      // 三连/订阅提醒：粉色
        SponsorBlockCategories.MUSIC_OFFTOPIC to "#FF9800",   // 音乐非音乐部分：橙色
        SponsorBlockCategories.PREVIEW to "#2196F3",          // 回顾/概要：蓝色
        SponsorBlockCategories.HIGHLIGHT to "#E91E63",        // 精彩时刻/重点：粉色
        SponsorBlockCategories.FILLER to "#9C27B0",           // 离题闲聊/玩笑：紫色
        SponsorBlockCategories.EXCLUSIVE_ACCESS to "#4CAF50", // 亲情推广/品牌合作：绿色
        SponsorBlockCategories.CHAPTER to "#616161"           // 章节标记：灰色 (Chapters are just markers)
    )
    const val FALLBACK_COLOR_HEX = "#78909C" // Blue Grey 400

    // Utility to convert HEX string to Compose Color, with error handling
    fun hexToColor(hex: String?, defaultColor: Color = Color(FALLBACK_COLOR_HEX.toColorInt())): Color {
        if (hex == null) return defaultColor
        return try {
            Color(hex.toColorInt())
        } catch (e: IllegalArgumentException) {
            println("SponsorBlockColors: Invalid hex color string: $hex")
            defaultColor
        }
    }

    // Provides Compose Color objects directly
    val DefaultCategoryColors: Map<String, Color> by lazy {
        DefaultCategoryColorsHex.mapValues { hexToColor(it.value) }
    }
    val FALLBACK_COLOR: Color by lazy { hexToColor(FALLBACK_COLOR_HEX) }
}
