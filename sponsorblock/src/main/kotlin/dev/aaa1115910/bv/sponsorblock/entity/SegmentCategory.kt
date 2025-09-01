package dev.aaa1115910.bv.sponsorblock.entity

enum class SegmentCategory(val title: String, val description: String) {
    Sponsor("sponsor", "广告"),
    Selfpromo("selfpromo", "无偿/自我推广"),
    Interaction("interaction", "三连/订阅提醒"),
    Intro("intro", "过场/开场动画"),
    Outro("outro", "鸣谢/结束画面"),
    Preview("preview", "回顾/概要"),
    MusicOfftopic("music_offtopic", "音乐:非音乐部分"),
    PoiHighlight("poi_highlight", "精彩时刻/重点"),
    Filler("filler", "离题闲聊/玩笑");

    companion object {
        fun fromCategoryName(name: String) =
            entries.find { it.name.equals(name, ignoreCase = true) }
    }
}
