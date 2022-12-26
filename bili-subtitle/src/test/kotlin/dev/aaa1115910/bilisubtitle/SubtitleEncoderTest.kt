package dev.aaa1115910.bilisubtitle

import java.io.File
import kotlin.test.Test

class SubtitleEncoderTest {

    @Test
    fun `encode to bcc`() {
        val fileContent = this::class.java.getResource("/example.srt")?.readText()!!
        val data = SubtitleParser.fromSrtString(fileContent)
        val result = SubtitleEncoder.encodeToBcc(data)
        val outputFile = File("build/output.bcc")
        outputFile.createNewFile()
        outputFile.writeText(result)
    }

    @Test
    fun `encode to srt`() {
        val fileContent = this::class.java.getResource("/example.bcc")?.readText()!!
        val data = SubtitleParser.fromBccString(fileContent)
        val result = SubtitleEncoder.encodeToSrt(data)
        val outputFile = File("build/output.srt")
        outputFile.createNewFile()
        outputFile.writeText(result)
    }
}