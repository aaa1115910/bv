package dev.aaa1115910.bv.entity.sponsorblock

enum class SponsorBlockActionType {
    AUTO_SKIP,      // 自动跳过
    MANUAL_SKIP,    // 显示并手动跳过 (UI上可能需要一个按钮)
    SHOW_MARK_ONLY, // 仅显示标记
    DO_NOTHING      // 不处理 (等同于隐藏)
}
