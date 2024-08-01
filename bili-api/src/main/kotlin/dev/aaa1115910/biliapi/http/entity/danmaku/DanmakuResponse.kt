package dev.aaa1115910.biliapi.http.entity.danmaku


data class DanmakuResponse(
    val chatserver: String,
    val chatId: Long,
    val maxLimit: Int,
    val state: Int,
    val realName: Int,
    val source: String,
    val data: List<DanmakuData> = emptyList()
)

data class DanmakuData(
    val time: Float,
    val type: Int,
    val size: Int,
    val color: Int,
    val timestamp: Int,
    val pool: Int,
    val midHash: String,
    val dmid: Long,
    val level: Int,
    val text: String
) {
    companion object {
        fun fromString(p: String, text: String): DanmakuData {
            val data = p.split(",")
            return DanmakuData(
                time = data[0].toFloat(),
                type = data[1].toInt(),
                size = data[2].toInt(),
                color = data[3].toInt(),
                timestamp = data[4].toInt(),
                pool = data[5].toInt(),
                midHash = data[6],
                dmid = data[7].toLong(),
                level = data[8].toInt(),
                text = text
            )
        }
    }
}