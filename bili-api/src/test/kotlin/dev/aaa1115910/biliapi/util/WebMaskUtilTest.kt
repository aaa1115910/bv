package dev.aaa1115910.biliapi.util

import java.io.File
import kotlin.test.Test

class WebMaskUtilTest {
    @Test
    fun `parse web mask file`() {
        val maskFile = Any::class::class.java.getResource("/3540266_25_2.exp.webmask")!!
        val binary = File(maskFile.toURI()).readBytes()
        val masks = WebMaskUtil.parseBinary(binary)
        println(masks)
    }

    @Test
    fun `parse web mask file and output`() {
        val maskFile = Any::class::class.java.getResource("/3540266_25_2.exp.webmask")!!
        val binary = File(maskFile.toURI()).readBytes()
        val masks = WebMaskUtil.parseBinary(binary)
        val outputDir = File("/home/seele/Documents/output/webmask")
        masks.forEachIndexed { index, danmakuMaskSegment ->
            val dir = File(outputDir, "$index")
            dir.mkdir()
            danmakuMaskSegment.frames.forEachIndexed { frameIndex, danmakuMaskFrame ->
                File(dir, "${danmakuMaskFrame.range}.svg").writeText(danmakuMaskFrame.svg)
            }
        }
    }
}