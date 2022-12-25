package dev.aaa1115910.bilisubtitle

import kotlin.test.Test

class SubtitleParserTest {
    @Test
    fun `read bcc subtitle`() {
        val fileContent = this::class.java.getResource("/example.bcc")?.readText()!!
        val result = SubtitleParser.fromBccString(fileContent)
        println(result)
    }

    @Test
    fun `read srt subtitle`() {
        val fileContent = this::class.java.getResource("/example.srt")?.readText()!!
        val result = SubtitleParser.fromSrtString(fileContent)
        println(result)
    }
}