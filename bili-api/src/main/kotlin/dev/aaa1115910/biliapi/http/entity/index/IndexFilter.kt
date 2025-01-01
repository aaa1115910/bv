package dev.aaa1115910.biliapi.http.entity.index

val indexFilterSeasonVersion = mapOf(
    -1 to "全部",
    1 to "正片",
    2 to "电影",
    3 to "其他",
)

val indexFilterSpokenLanguageType = mapOf(
    -1 to "全部",
    1 to "原声",
    2 to "中文配音",
)

val indexFilterArea = mapOf(
    -1 to "全部",
    2 to "日本",
    3 to "美国",
    4 to "其他",
)

val indexFilterIsFinish = mapOf(
    -1 to "全部",
    1 to "完结",
    0 to "连载",
)

val indexFilterCopyright = mapOf(
    -1 to "全部",
    3 to "独家",
    1 to "其他",
)

val indexFilterSeasonStatus = mapOf(
    -1 to "全部",
    1 to "免费",
    2 to "付费",
    4 to "大会员",
)

val indexFilterSeasonMonth = mapOf(
    -1 to "全部",
    1 to "1月",
    4 to "4月",
    7 to "7月",
    10 to "10月",
)

val indexFilterYear = mapOf(
    "-1" to "全部",
    "[2024,2025)" to "2024",
    "[2023,2024)" to "2023",
    "[2022,2023)" to "2022",
    "[2021,2022)" to "2021",
    "[2020,2021)" to "2020",
    "[2019,2020)" to "2019",
    "[2018,2019)" to "2018",
    "[2017,2018)" to "2017",
    "[2016,2017)" to "2016",
    "[2015,2016)" to "2015",
    "[2010,2015)" to "2015-2010",
    "[2005,2010)" to "2009-2005",
    "[2000,2005)" to "2004-2000",
    "[1990,2000)" to "90年代",
    "[1980,1990)" to "80年代",
    "[,1980)" to "更早",
)

val indexFilterStyleIdsAnime get() = IndexFilterStyle.animeStyles
val indexFilterStyleIdsMovie get() = IndexFilterStyle.movieStyles
val indexFilterStyleIdsDocumentary get() = IndexFilterStyle.documentaryStyles
val indexFilterStyleIdsTV get() = IndexFilterStyle.tvStyles
val indexFilterStyleIdsGuochuang get() = IndexFilterStyle.guochuangStyles
val indexFilterStyleIdsVariety get() = IndexFilterStyle.varietyStyles

val indexFilterAreaMovie get() = IndexFilterArea.movieAreas
val indexFilterAreaTV get() = IndexFilterArea.tvAreas

val indexFilterProducerId get() = IndexFilterProducerId.producerIds

val indexFilterReleaseDate = mapOf(
    "-1" to "全部",
    "[2024-01-01 00:00:00,2025-01-01 00:00:00)" to "2024",
    "[2023-01-01 00:00:00,2024-01-01 00:00:00)" to "2023",
    "[2022-01-01 00:00:00,2023-01-01 00:00:00)" to "2022",
    "[2021-01-01 00:00:00,2022-01-01 00:00:00)" to "2021",
    "[2020-01-01 00:00:00,2021-01-01 00:00:00)" to "2020",
    "[2019-01-01 00:00:00,2020-01-01 00:00:00)" to "2019",
    "[2018-01-01 00:00:00,2019-01-01 00:00:00)" to "2018",
    "[2017-01-01 00:00:00,2018-01-01 00:00:00)" to "2017",
    "[2016-01-01 00:00:00,2017-01-01 00:00:00)" to "2016",
    "[2010-01-01 00:00:00,2016-01-01 00:00:00)" to "2015-2010",
    "[2005-01-01 00:00:00,2010-01-01 00:00:00)" to "2009-2005",
    "[2000-01-01 00:00:00,2005-01-01 00:00:00)" to "2004-2000",
    "[1990-01-01 00:00:00,2000-01-01 00:00:00)" to "90年代",
    "[1980-01-01 00:00:00,1990-01-01 00:00:00)" to "80年代",
    "[,1980-01-01 00:00:00)" to "更早",
)
