package dev.aaa1115910.bv.util

object PartitionUtil {
    val partitions = listOf(
        Partition(
            1, "douga", "动画", listOf(
                Partition(24, "mad", "MAD·AMV"),
                Partition(25, "mmd", "MMD·3D"),
                Partition(47, "voice", "短片·手书·配音"),
                Partition(210, "garage_kit", "手办·模玩"),
                Partition(86, "tokusatsu", "特摄"),
                Partition(253, "acgntalks", "动漫杂谈"),
                Partition(27, "other", "综合")
            )
        ),
        Partition(
            13, "anime", "番剧", listOf(
                Partition(32, "finish", "完结动画"),
                Partition(33, "serial", "连载动画"),
                Partition(51, "information", "资讯"),
                Partition(152, "offical", "官方延伸")
            )
        ),
        Partition(
            167, "guochuang", "国创", listOf(
                Partition(153, "chinese", "国产动画"),
                Partition(168, "original", "国产原创相关"),
                Partition(169, "puppetry", "布袋戏"),
                Partition(195, "motioncomic", "动态漫·广播剧"),
                Partition(170, "information", "资讯")
            )
        ),
        Partition(
            3, "music", "音乐", listOf(
                Partition(28, "original", "原创音乐"),
                Partition(31, "cover", "翻唱"),
                Partition(59, "perform", "演奏"),
                Partition(30, "vocaloid", "VOCALOID·UTAU"),
                Partition(29, "live", "音乐现场"),
                Partition(193, "mv", "MV"),
                Partition(243, "commentary", "乐评盘点"),
                Partition(244, "tutorial", "音乐教学"),
                Partition(130, "other", "音乐综合")
            )
        ),
        Partition(
            129, "dance", "舞蹈", listOf(
                Partition(20, "otaku", "宅舞"),
                Partition(198, "hiphop", "街舞"),
                Partition(199, "star", "明星舞蹈"),
                Partition(200, "china", "中国舞"),
                Partition(154, "three_d", "舞蹈综合"),
                Partition(156, "demo", "舞蹈教程")
            )
        ),
        Partition(
            4, "game", "游戏", listOf(
                Partition(17, "stand_alone", "单机游戏"),
                Partition(171, "esports", "电子竞技"),
                Partition(172, "mobile", "手机游戏"),
                Partition(65, "online", "网络游戏"),
                Partition(173, "board", "桌游棋牌"),
                Partition(121, "gmv", "GMV"),
                Partition(136, "music", "音游"),
                Partition(19, "mugen", "Mugen")
            )
        ),
        Partition(
            36, "knowledge", "知识", listOf(
                Partition(201, "science", "科学科普"),
                Partition(124, "social_science", "社科·法律·心理"),
                Partition(228, "humanity_history", "人文历史"),
                Partition(207, "business", "财经商业"),
                Partition(208, "campus", "校园学习"),
                Partition(209, "career", "职业职场"),
                Partition(229, "design", "设计·创意"),
                Partition(122, "skill", "野生技术协会")
            )
        ),
        Partition(
            188, "tech", "科技", listOf(
                Partition(95, "digital", "数码"),
                Partition(230, "application", "软件应用"),
                Partition(231, "computer_tech", "计算机技术"),
                Partition(232, "industry", "科工机械"),
                Partition(233, "diy", "极客DIY")
            )
        ),
        Partition(
            234, "sports", "运动", listOf(
                Partition(235, "basketball", "篮球"),
                Partition(249, "football", "足球"),
                Partition(164, "aerobics", "健身"),
                Partition(236, "athletic", "竞技体育"),
                Partition(237, "culture", "运动文化"),
                Partition(238, "comprehensive", "运动综合")
            )
        ),
        Partition(
            223, "car", "汽车", listOf(
                Partition(245, "racing", "赛车"),
                Partition(246, "modifiedvehicle", "改装玩车"),
                Partition(247, "newenergyvehicle", "新能源车"),
                Partition(248, "touringcar", "房车"),
                Partition(240, "motorcycle", "摩托车"),
                Partition(227, "strategy", "购车攻略"),
                Partition(176, "life", "汽车生活")
            )
        ),
        Partition(
            160, "life", "生活", listOf(
                Partition(138, "funny", "搞笑"),
                Partition(250, "travel", "出行"),
                Partition(251, "rurallife", "三农"),
                Partition(239, "home", "家居房产"),
                Partition(161, "handmake", "手工"),
                Partition(162, "painting", "绘画"),
                Partition(21, "daily", "日常")
            )
        ),
        Partition(
            211, "food", "美食", listOf(
                Partition(76, "make", "美食制作"),
                Partition(212, "detective", "美食侦探"),
                Partition(213, "measurement", "美食测评"),
                Partition(214, "rural", "田园美食"),
                Partition(215, "record", "美食记录")
            )
        ),
        Partition(
            217, "animal", "动物圈", listOf(
                Partition(218, "cat", "喵星人"),
                Partition(219, "dog", "汪星人"),
                Partition(222, "reptiles", "小宠异宠"),
                Partition(221, "wild_animal", "野生动物"),
                Partition(220, "second_edition", "动物二创"),
                Partition(75, "animal_composite", "动物综合")
            )
        ),
        Partition(
            119, "kichiku", "鬼畜", listOf(
                Partition(22, "guide", "鬼畜调教"),
                Partition(26, "mad", "音MAD"),
                Partition(126, "manual_vocaloid", "人力VOCALOID"),
                Partition(216, "theatre", "鬼畜剧场"),
                Partition(127, "course", "教程演示")
            )
        ),
        Partition(
            155, "fashion", "时尚", listOf(
                Partition(157, "makeup", "美妆护肤"),
                Partition(252, "cos", "仿妆cos"),
                Partition(158, "clothing", "穿搭"),
                Partition(159, "catwalk", "时尚潮流")
            )
        ),
        Partition(
            202, "information", "资讯", listOf(
                Partition(203, "hotspot", "热点"),
                Partition(204, "global", "环球"),
                Partition(205, "social", "社会"),
                Partition(206, "multiple", "综合")
            )
        ),
        Partition(
            5, "ent", "娱乐", listOf(
                Partition(71, "variety", "综艺"),
                Partition(241, "talker", "娱乐杂谈"),
                Partition(242, "fans", "粉丝创作"),
                Partition(137, "celebrity", "明星综合")
            )
        ),
        Partition(
            181, "cinephile", "影视", listOf(
                Partition(182, "cinecism", "影视杂谈"),
                Partition(183, "montage", "影视剪辑"),
                Partition(85, "shortfilm", "小剧场"),
                Partition(184, "trailer_info", "预告·资讯")
            )
        ),
        Partition(
            177, "documentary", "纪录片", listOf(
                Partition(37, "history", "人文·历史"),
                Partition(178, "science", "科学·探索·自然"),
                Partition(179, "military", "军事"),
                Partition(180, "travel", "社会·美食·旅行")
            )
        ),
        Partition(
            23, "movie", "电影", listOf(
                Partition(147, "chinese", "华语电影"),
                Partition(145, "west", "欧美电影"),
                Partition(146, "japan", "日本电影"),
                Partition(83, "movie", "其他国家")
            )
        ),
        Partition(
            11, "电视剧", "电视剧", listOf(
                Partition(185, "mainland", "国产剧"),
                Partition(187, "overseas", "海外剧")
            )
        )
    )
}

data class Partition(
    val tid: Int,
    val code: String,
    val strRes: String,
    val children: List<Partition> = emptyList()
)
