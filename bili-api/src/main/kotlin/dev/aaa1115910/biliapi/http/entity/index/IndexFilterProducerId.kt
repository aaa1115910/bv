package dev.aaa1115910.biliapi.http.entity.index

object IndexFilterProducerId {
    private val producerIdFilter = mapOf(
        -1 to "全部",
        1 to "BBC",
        2 to "NHK",
        3 to "SKY",
        4 to "央视",
        5 to "ITV",
        6 to "历史频道",
        7 to "探索频道",
        8 to "卫视",
        9 to "自制",
        10 to "ZDF",
        11 to "合作机构",
        12 to "国内其他",
        13 to "国外其他",
        14 to "国家地理",
        15 to "索尼",
        16 to "环球",
        17 to "派拉蒙",
        18 to "华纳",
        19 to "迪士尼",
        20 to "HBO",

        )

    private val producerIdIds = listOf(
        -1, 4, 1, 7, 14, 2, 6, 8, 9, 5, 3, 10, 11, 12, 13, 15, 16, 17, 18, 19, 20
    )

    val producerIds by lazy { producerIdIds.associateWith { producerIdFilter[it]!! } }
}
