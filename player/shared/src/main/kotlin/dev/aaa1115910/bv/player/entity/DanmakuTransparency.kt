package dev.aaa1115910.bv.player.entity

enum class DanmakuTransparency(val transparency: Float) {
    T1(1f), T2(0.9f), T3(0.8f), T4(0.7f), T5(0.6f),
    T6(0.5f), T7(0.3f), T8(0.2f), T9(0.1f);

    companion object {
        fun fromOrdinal(ordinal: Int) = runCatching { entries[ordinal] }
            .getOrDefault(T1)
    }

}