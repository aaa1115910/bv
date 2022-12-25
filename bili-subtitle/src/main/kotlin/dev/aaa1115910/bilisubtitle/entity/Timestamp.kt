package dev.aaa1115910.bilisubtitle.entity

data class Timestamp(
    val hours: Int,
    val minutes: Int,
    val seconds: Int,
    val milliSeconds: Int,
    var totalMills: Long = 0
) {
    companion object {
        fun fromSrtString(srtTime: String): Timestamp {
            val parts = srtTime.split(":", ",")
            return Timestamp(
                hours = parts[0].toInt(),
                minutes = parts[1].toInt(),
                seconds = parts[2].toInt(),
                milliSeconds = parts[3].toInt()
            )
        }

        fun fromBccString(bccTime: Float): Timestamp {
            val mils = (bccTime * 1000).toInt()
            val hours = mils / (1000 * 60 * 60)
            val minutes = (mils % (1000 * 60 * 60)) / (1000 * 60)
            val seconds = (mils % (1000 * 60)) / (1000)
            val milliSeconds = mils % 1000
            return Timestamp(hours, minutes, seconds, milliSeconds)
        }
    }

    init {
        totalMills = hours * 60 * 60 * 1000L + minutes * 60 * 1000L + seconds * 1000L + milliSeconds
    }

    fun getBccTime(): Float =
        hours * 60 * 60 + minutes * 60 + seconds + milliSeconds / 1000f

    fun getSrtTime(): String {
        val h = String.format("%02d", hours)
        val m = String.format("%02d", minutes)
        val s = String.format("%02d", seconds)
        var ms = "$milliSeconds"
        while (ms.length < 3) {
            ms += "0"
        }
        return "$h:$m:$s,$ms"
    }
}