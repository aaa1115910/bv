package dev.aaa1115910.bv.player.entity

enum class DanmakuSize(val scale: Float) {
    S1(0.25f), S2(0.5f), S3(0.6f), S4(0.7f), S5(0.8f), S6(0.9f), S7(1f),
    S8(1.1f), S9(1.2f), S10(1.3f), S11(1.4f), S12(1.5f), S13(2f);

    companion object {
        fun fromOrdinal(ordinal: Int) = runCatching { entries[ordinal] }
            .getOrDefault(S2)
    }
}