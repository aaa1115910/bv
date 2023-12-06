package dev.aaa1115910.biliapi.http.entity.index

object IndexFilterArea {
    private val areaFilter = mapOf(
        -1 to "全部",
        1 to "中国大陆",
        2 to "日本",
        3 to "美国",
        4 to "英国",
        5 to "其他",
        6 to "中国港台",
        8 to "韩国",
        9 to "法国",
        10 to "泰国",
        13 to "西班牙",
        15 to "德国",
        35 to "意大利",
    )

    private val movieAreaIds = listOf(
        -1, 1, 6, 3, 28, 9, 4, 15, 10, 35, 13, 5
    )
    private val tvAreaIds = listOf(
        -1, 1, 2, 3, 4, 10, 5
    )

    val movieAreas by lazy { movieAreaIds.associateWith { areaFilter[it]!! } }
    val tvAreas by lazy { tvAreaIds.associateWith { areaFilter[it]!! } }
}