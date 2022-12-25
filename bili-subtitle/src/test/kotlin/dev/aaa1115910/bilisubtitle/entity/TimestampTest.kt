package dev.aaa1115910.bilisubtitle.entity

import kotlin.test.Test

class TimestampTest {
    @Test
    fun `parse bcc time`() {
        val time = 7530.2f
        println(Timestamp.fromBccString(time))
    }

    @Test
    fun `parse srt time`() {
        val time = "01:03:17,775"
        println(Timestamp.fromSrtString(time))
    }
}