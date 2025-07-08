package dev.aaa1115910.bv.entity.sponsorblock

object SponsorBlockCategories {
    // Based on https://github.com/hanydd/BilibiliSponsorBlock/wiki/API and common SponsorBlock types
    const val SPONSOR = "sponsor"
    const val INTRO = "intro"
    const val OUTRO = "outro"
    const val INTERACTION = "interaction" // e.g., "三连" (like, coin, favorite)
    const val SELF_PROMO = "selfpromo"    // Creator's own promotion
    const val MUSIC_OFFTOPIC = "music_offtopic" // Unrelated music / long outros with music
    const val PREVIEW = "preview"         // Preview/recap of content
    const val HIGHLIGHT = "poi_highlight" // Point of Interest Highlight
    const val FILLER = "filler"           // Meaningless filler content (e.g., extended recaps)
    const val EXCLUSIVE_ACCESS = "exclusive_access" // Paid/membership content promotion
    const val CHAPTER = "chapter"         // Chapter marker (not for skipping, just marking)

    val ALL_CATEGORIES_ORDERED = listOf(
        SPONSOR,
        SELF_PROMO,
        EXCLUSIVE_ACCESS,
        INTERACTION,
        HIGHLIGHT,
        INTRO,
        OUTRO,
        PREVIEW,
        FILLER,
        MUSIC_OFFTOPIC,
        CHAPTER
    )

    // Default actions for each category
    val DEFAULT_ACTIONS: Map<String, SponsorBlockActionType> = mapOf(
        SPONSOR to SponsorBlockActionType.AUTO_SKIP,           // 广告：自动跳过
        INTRO to SponsorBlockActionType.AUTO_SKIP,             // 过场/开场动画：自动跳过
        OUTRO to SponsorBlockActionType.MANUAL_SKIP,           // 鸣谢/结束画面：手动跳过
        INTERACTION to SponsorBlockActionType.MANUAL_SKIP,     // 三连/订阅提醒：手动跳过
        SELF_PROMO to SponsorBlockActionType.MANUAL_SKIP,      // 无偿/自我推广：手动跳过
        MUSIC_OFFTOPIC to SponsorBlockActionType.AUTO_SKIP,    // 音乐非音乐部分：自动跳过
        PREVIEW to SponsorBlockActionType.SHOW_MARK_ONLY,      // 回顾/概要：在进度条中显示
        HIGHLIGHT to SponsorBlockActionType.SHOW_MARK_ONLY,    // 精彩时刻/重点：在进度条中显示
        FILLER to SponsorBlockActionType.DO_NOTHING,           // 离题闲聊/玩笑：禁用
        EXCLUSIVE_ACCESS to SponsorBlockActionType.SHOW_MARK_ONLY, // 亲情推广/品牌合作：显示标记
        CHAPTER to SponsorBlockActionType.SHOW_MARK_ONLY       // 章节标记：显示标记
    )

    // Get display name for category
    fun getDisplayName(category: String): String {
        return when (category) {
            SPONSOR -> "广告"
            SELF_PROMO -> "无偿/自我推广"
            EXCLUSIVE_ACCESS -> "亲情推广/品牌合作"
            INTERACTION -> "三连/订阅提醒"
            HIGHLIGHT -> "精彩时刻/重点"
            INTRO -> "过场/开场动画"
            OUTRO -> "鸣谢/结束画面"
            PREVIEW -> "回顾/概要"
            FILLER -> "离题闲聊/玩笑"
            MUSIC_OFFTOPIC -> "音乐非音乐部分"
            CHAPTER -> "章节标记"
            else -> category
        }
    }
}
