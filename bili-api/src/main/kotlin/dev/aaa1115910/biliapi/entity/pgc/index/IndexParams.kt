package dev.aaa1115910.biliapi.entity.pgc.index

import dev.aaa1115910.biliapi.entity.pgc.PgcType

interface PgcIndexParam

/**
 * 排序
 */
enum class IndexOrder(val id: Int) : PgcIndexParam {
    UpdateTime(0),      // 更新时间
    DanmakuCount(1),    // 弹幕数量
    PlayCount(2),       // 播放数量
    FollowCount(3),     // 追番人数
    Score(4),           // 最高评分
    StartTime(5),       // 开播时间
    PublishTime(6);     // 上映时间

    companion object {
        fun getList(pgcType: PgcType): List<IndexOrder> = when (pgcType) {
            PgcType.Anime -> listOf(FollowCount, UpdateTime, Score, PlayCount, StartTime)
            PgcType.GuoChuang -> listOf(FollowCount, UpdateTime, Score, PlayCount, StartTime)
            PgcType.Movie -> listOf(PlayCount, UpdateTime, PublishTime, Score)
            PgcType.Documentary -> listOf(PlayCount, Score, UpdateTime, PublishTime, DanmakuCount)
            PgcType.Tv -> listOf(PlayCount, UpdateTime, DanmakuCount, Score, FollowCount)
            PgcType.Variety -> listOf(PlayCount, UpdateTime, PublishTime, Score, DanmakuCount)
        }
    }
}

enum class IndexOrderType(val id: Int) : PgcIndexParam {
    Desc(0),            // 降序
    Asc(1);             // 升序
}

/**
 * 类型
 */
enum class SeasonVersion(val id: Int) : PgcIndexParam {
    All(-1),            // 全部
    FeatureFilm(1),     // 正片
    Movies(2),          // 电影
    Other(3);           // 其他

    companion object {
        fun getList(pgcType: PgcType): List<SeasonVersion> {
            return when (pgcType) {
                PgcType.Anime, PgcType.GuoChuang -> listOf(All, FeatureFilm, Movies, Other)
                else -> emptyList()
            }
        }
    }
}

/**
 * 配音
 */
enum class SpokenLanguage(val id: Int) : PgcIndexParam {
    All(-1),            // 全部
    OriginalSoundtrack(1),  // 原声
    ChineseDubbing(2);  // 中文配音

    companion object {
        fun getList(pgcType: PgcType) = when (pgcType) {
            PgcType.Anime -> listOf(All, OriginalSoundtrack, ChineseDubbing)
            else -> emptyList()
        }
    }
}

/**
 * 地区
 */
enum class Area(val id: Int) : PgcIndexParam {
    All(-1),            // 全部
    MainlandChina(1),   // 中国大陆
    Japan(2),           // 日本
    America(3),         // 美国
    Britain(4),         // 英国
    Other(5),           // 其他
    ChinaHongKongTaiwan(6), // 中国港台 6,7
    Korea(8),           // 韩国
    France(9),          // 法国
    Thailand(10),       // 泰国
    Spain(13),          // 西班牙
    Germany(15),        // 德国
    Italy(35);          // 意大利

    companion object {
        fun getList(pgcType: PgcType) = when (pgcType) {
            PgcType.Anime -> listOf(All, Japan, America, Other)
            PgcType.Movie -> listOf(
                All, MainlandChina, ChinaHongKongTaiwan, America, Japan, Korea, France,
                Britain, Germany, Thailand, Italy, Spain, Other
            )

            PgcType.Tv -> listOf(All, MainlandChina, Japan, America, Britain, Other)
            else -> emptyList()
        }
    }
}

/**
 * 状态（完结状态）
 */
enum class IsFinish(val id: Int) : PgcIndexParam {
    All(-1),            // 全部
    Finished(1),        // 完结
    Serialization(0);   // 连载

    companion object {
        fun getList(pgcType: PgcType) = when (pgcType) {
            PgcType.Anime, PgcType.GuoChuang -> listOf(All, Finished, Serialization)
            else -> emptyList()
        }
    }
}

/**
 * 版权
 */
enum class Copyright(val id: Int) : PgcIndexParam {
    All(-1),            // 全部
    Exclusive(3),       // 独家
    Other(1);           // 其他 1,2,4

    companion object {
        fun getList(pgcType: PgcType) = when (pgcType) {
            PgcType.Anime, PgcType.GuoChuang -> listOf(All, Exclusive, Other)
            else -> emptyList()
        }
    }
}

/**
 * 付费（付费状态）
 */
enum class SeasonStatus(val id: Int) : PgcIndexParam {
    All(-1),            // 全部
    Free(1),            // 免费
    Paid(2),            // 付费 2,6
    Prime(4);           // 大会员 4,6

    companion object {
        fun getList(pgcType: PgcType) = when (pgcType) {
            PgcType.Anime, PgcType.GuoChuang, PgcType.Movie -> listOf(All, Free, Paid, Prime)
            PgcType.Documentary, PgcType.Tv, PgcType.Variety -> listOf(All, Free, Prime)
        }
    }
}

/**
 * 季度
 */
enum class SeasonMonth(val id: Int) : PgcIndexParam {
    All(-1),            // 全部
    January(1),         // 1月
    April(4),           // 4月
    July(7),            // 7月
    October(10);        // 10月

    companion object {
        fun getList(pgcType: PgcType) = when (pgcType) {
            PgcType.Anime -> listOf(All, January, April, July, October)
            else -> emptyList()
        }
    }
}

/**
 * 出品（方）
 */
enum class Producer(val id: Int) : PgcIndexParam {
    All(-1),            // 全部
    BBC(1),             // BBC
    NHK(2),             // NHK
    SKY(3),             // SKY
    CCTV(4),            // 央视
    ITV(5),             // ITV
    HistoryChannel(6),  // 历史频道
    DiscoveryChannel(7),// 探索频道
    SatelliteTV(8),     // 卫视
    SelfMade(9),        // 自制
    ZDF(10),            // ZDF
    Cooperation(11),    // 合作机构
    DomesticOther(12),  // 国内其他
    ForeignOther(13),   // 国外其他
    NationalGeographic(14), // 国家地理
    Sony(15),           // 索尼
    Universal(16),      // 环球
    Paramount(17),      // 派拉蒙
    Warner(18),         // 华纳
    Disney(19),         // 迪士尼
    HBO(20);            // HBO

    companion object {
        fun getList(pgcType: PgcType) = when (pgcType) {
            PgcType.Documentary -> listOf(
                All, CCTV, BBC, DiscoveryChannel, NationalGeographic, NHK, HistoryChannel,
                SatelliteTV, SelfMade, ITV, SKY, ZDF, Cooperation, DomesticOther, ForeignOther,
                Sony, Universal, Paramount, Warner, Disney, HBO
            )

            else -> emptyList()
        }
    }
}

/**
 * 年份（Year）
 */
enum class Year(val str: String) : PgcIndexParam {
    All("-1"),          // 全部
    Year2024("[2024,2025)"), // 2024
    Year2023("[2023,2024)"), // 2023
    Year2022("[2022,2023)"), // 2022
    Year2021("[2021,2022)"), // 2021
    Year2020("[2020,2021)"), // 2020
    Year2019("[2019,2020)"), // 2019
    Year2018("[2018,2019)"), // 2018
    Year2017("[2017,2018)"), // 2017
    Year2016("[2016,2017)"), // 2016
    Year2015("[2015,2016)"), // 2015
    Year2014_2010("[2010,2015)"),   // 2014-2010
    Year2009_2005("[2005,2010)"),   // 2009-2005
    Year2004_2000("[2000,2005)"),   // 2004-2000
    Year199x("[1990,2000)"), // 90年代
    Year198x("[1980,1990)"), // 80年代
    Earlier("[,1980)"); // 更早

    companion object {
        fun getList(pgcType: PgcType) = when (pgcType) {
            PgcType.Anime, PgcType.GuoChuang -> listOf(
                All, Year2024, Year2023, Year2022, Year2021, Year2020, Year2019, Year2018,
                Year2017, Year2016, Year2015, Year2014_2010, Year2009_2005, Year2004_2000,
                Year199x, Year198x, Earlier
            )

            else -> emptyList()
        }
    }
}

/**
 * 年份（发布时间）
 */
enum class ReleaseDate(val str: String) : PgcIndexParam {
    All("-1"),          // 全部
    Year2024("[2024-01-01 00:00:00,2025-01-01 00:00:00)"),  // 2024
    Year2023("[2023-01-01 00:00:00,2024-01-01 00:00:00)"),  // 2023
    Year2022("[2022-01-01 00:00:00,2023-01-01 00:00:00)"),  // 2022
    Year2021("[2021-01-01 00:00:00,2022-01-01 00:00:00)"),  // 2021
    Year2020("[2020-01-01 00:00:00,2021-01-01 00:00:00)"),  // 2020
    Year2019("[2019-01-01 00:00:00,2020-01-01 00:00:00)"),  // 2019
    Year2018("[2018-01-01 00:00:00,2019-01-01 00:00:00)"),  // 2018
    Year2017("[2017-01-01 00:00:00,2018-01-01 00:00:00)"),  // 2017
    Year2016("[2016-01-01 00:00:00,2017-01-01 00:00:00)"),  // 2016
    Year2015_2010("[2010-01-01 00:00:00,2015-01-01 00:00:00)"), // 2015-2010
    Year2009_2005("[2005-01-01 00:00:00,2010-01-01 00:00:00)"), // 2009-2005
    Year2004_2000("[2000-01-01 00:00:00,2005-01-01 00:00:00)"), // 2004-2000
    Year199x("[1990-01-01 00:00:00,2000-01-01 00:00:00)"),  // 90年代
    Year198x("[1980-01-01 00:00:00,1990-01-01 00:00:00)"),  // 80年代
    Earlier("[,1980-01-01 00:00:00)");  // 更早

    companion object {
        fun getList(pgcType: PgcType) = when (pgcType) {
            PgcType.Movie, PgcType.Documentary, PgcType.Tv -> listOf(
                All, Year2024, Year2023, Year2022, Year2021, Year2020, Year2019, Year2018,
                Year2017, Year2016, Year2015_2010, Year2009_2005, Year2004_2000,
                Year199x, Year198x, Earlier
            )

            else -> emptyList()
        }
    }
}

/**
 * 风格
 */
enum class Style(val id: Int) : PgcIndexParam {
    All(-1),            // 全部
    Movie(-10),         // 电影

    Original(10010),    // 原创
    Comic(10011),       // 漫画改
    Novel(10012),       // 小说改
    Game(10013),        // 游戏改
    Animation(10014),   // 动态漫
    Puppetry(10015),    // 布袋戏
    HotBlood(10016),    // 热血
    TimeTravel(10017),  // 穿越
    Fantasy(10018),     // 奇幻
    XuanHuan(10019),    // 玄幻

    Fight(10020),       // 战斗
    Funny(10021),       // 搞笑
    Daily(10022),       // 日常
    ScienceFiction(10023),  // 科幻
    Moe(10024),         // 萌系
    Healing(10025),     // 治愈
    School(10026),      // 校园
    Children(10027),    // 少儿
    InstantNoodles(10028),  // 泡面
    InLove(10029),      // 恋爱

    Girl(10030),        // 少女
    Magic(10031),       // 魔法
    Adventure(10032),   // 冒险
    History(10033),     // 历史
    Fiction(10034),     // 架空
    Mecha(10035),       // 机战
    GodDemon(10036),    // 神魔
    VoiceControl(10037),// 声控
    Sports(10038),      // 运动
    Inspirational(10039),   // 励志

    Music(10040),       // 音乐
    Reasoning(10041),   // 推理
    Club(10042),        // 社团
    WisdomFight(10043), // 智斗
    Tearjerker(10044),  // 催泪
    Food(10045),        // 美食
    Idol(10046),        // 偶像
    Maiden(10047),      // 乙女
    Workplace(10048),   // 职场
    AncientStyle(10049),// 古风

    Plot(10050),        // 剧情
    Comedy(10051),      // 喜剧
    Love(10052),        // 爱情
    Action(10053),      // 动作
    Terror(10054),      // 恐怖
    Offense(10055),     // 犯罪
    Thriller(10056),    // 惊悚
    Suspense(10057),    // 悬疑
    War(10058),         // 战争
    // 10059

    Biography(10060),   // 传记
    Family(10061),      // 家庭
    Opera(10062),       // 歌剧
    Documentary(10063), // 纪实
    Disaster(10064),    // 灾难
    Humanities(10065),  // 人文
    Technology(10066),  // 科技
    Explore(10067),     // 探险
    Universal(10068),   // 通用
    CutePet(10069),     // 萌宠

    Social(10070),      // 社会
    Animal(10071),      // 动物
    Nature(10072),      // 自然
    Medical(10073),     // 医疗
    Military(10074),    // 军事
    Crime(10075),       // 罪案
    Mystery(10076),     // 神秘
    Travel(10077),      // 旅行
    MartialArts(10078), // 武侠
    Youth(10079),       // 青春

    City(10080),        // 都市
    AncientCostume(10081),  // 古装
    SpyWar(10082),      // 谍战
    Classic(10083),     // 经典
    Emotion(10084),     // 情感
    Myth(10085),        // 神话
    Age(10086),         // 年代
    Rural(10087),       // 农村
    CriminalInvestigation(10088), // 刑侦
    MilitaryLife(10089),// 军旅

    Interview(10090),   // 访谈
    TalkShow(10091),    // 脱口秀
    RealityShow(10092), // 真人秀

    //10093
    Selection(10094),   // 选秀
    Tourism(10095),     // 旅游
    Concert(10096),     // 演唱会
    ParentChild(10097), // 亲子
    EveningParty(10098),// 晚会
    Cultivate(10099),   // 养成

    Culture(10100),     // 文化

    //10101
    SpecialEffects(10102),  // 特摄
    ShortPlay(10103),    // 短剧
    ShortFilm(10104);    // 短片

    companion object {
        fun getList(pgcType: PgcType) = when (pgcType) {
            PgcType.Anime -> listOf(
                All, Original, Comic, Novel, Game, SpecialEffects, Puppetry, HotBlood, TimeTravel,
                Fantasy, Fight, Funny, Daily, ScienceFiction, Moe, Healing, School, Children,
                InstantNoodles, InLove, Girl, Magic, Adventure, History, Fiction, Mecha, GodDemon,
                VoiceControl, Sports, Inspirational, Music, Reasoning, Club, WisdomFight,
                Tearjerker, Food, Idol, Maiden, Workplace
            )

            PgcType.GuoChuang -> listOf(
                All, Original, Comic, Novel, Game, Animation, Puppetry, HotBlood, Fantasy, XuanHuan,
                Fight, Funny, MartialArts, Daily, ScienceFiction, Moe, Healing, Suspense, School,
                Children, InstantNoodles, InLove, Girl, Magic, History, Mecha, GodDemon,
                VoiceControl, Sports, Inspirational, Music, Reasoning, Club, WisdomFight,
                Tearjerker, Food, Idol, Maiden, Workplace, AncientStyle
            )

            PgcType.Movie -> listOf(
                All, ShortFilm, Plot, Comedy, Love, Action, Terror, ScienceFiction, Offense,
                Thriller, Suspense, Fantasy, War, Animation, Biography, Family, Opera, History,
                Adventure, Documentary, Disaster, Comic, Novel
            )

            PgcType.Documentary -> listOf(
                All, History, Food, Humanities, Technology, Explore, Universal, CutePet, Social,
                Animal, Nature, Medical, Military, Disaster, Crime, Mystery, Travel, Sports, Movie
            )

            PgcType.Variety -> listOf(
                All, Music, Interview, TalkShow, RealityShow, Selection, Food, Tourism,
                EveningParty, Concert, Emotion, Comedy, ParentChild, Culture, Workplace,
                CutePet, Cultivate
            )

            PgcType.Tv -> listOf(
                All, Plot, Emotion, Funny, Suspense, City, Family, AncientCostume, History,
                Fantasy, Youth, War, MartialArts, Inspirational, ShortPlay, ScienceFiction

            )
        }
    }
}