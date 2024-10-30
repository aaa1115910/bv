package dev.aaa1115910.biliapi.http.entity.index

enum class IndexOrder(val id: Int) {
    UpdateTime(0), DanmakuCount(1), PlayCount(2), FollowCount(3),
    Score(4), StartTime(5), PublishTime(6)
}

private val animeIds = listOf(3, 0, 4, 2, 5)
private val guochuangIds = listOf(3, 0, 4, 2, 5)
private val varietyIds = listOf(2, 0, 6, 4, 1)
private val tvIds = listOf(2, 0, 1, 3, 4)
private val movieIds = listOf(2, 0, 6, 4)
private val documentaryIds = listOf(2, 4, 0, 6, 1)

val animeIndexOrders by lazy { animeIds.map { IndexOrder.entries[it] } }
val guochuangIndexOrders by lazy { guochuangIds.map { IndexOrder.entries[it] } }
val varietyIndexOrders by lazy { varietyIds.map { IndexOrder.entries[it] } }
val tvIndexOrders by lazy { tvIds.map { IndexOrder.entries[it] } }
val movieIndexOrders by lazy { movieIds.map { IndexOrder.entries[it] } }
val documentaryIndexOrders by lazy { documentaryIds.map { IndexOrder.entries[it] } }

