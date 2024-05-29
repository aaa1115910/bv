package dev.aaa1115910.biliapi.util

import dev.aaa1115910.biliapi.entity.danmaku.DanmakuMaskFrame
import dev.aaa1115910.biliapi.entity.danmaku.DanmakuMaskSegment
import io.ktor.util.decodeBase64String
import okio.ByteString.Companion.readByteString
import java.io.ByteArrayInputStream
import java.io.DataInputStream
import java.util.zip.GZIPInputStream

object WebMaskUtil {
    fun parseBinary(binary: ByteArray): List<DanmakuMaskSegment> {
        val times = mutableListOf<Long>()
        val offsets = mutableListOf<Long>()
        val danmakuMaskList = mutableListOf<DanmakuMaskSegment>()

        val inputStream = DataInputStream(ByteArrayInputStream(binary))
        val mask = inputStream.readByteString(4)
        require(mask.string(Charsets.UTF_8) == "MASK") { "Not a mask file" }
        val version = inputStream.readInt()
        val unused = inputStream.readByteString(4)
        val size = inputStream.readInt()

        for (i in 0 until size) {
            times.add(inputStream.readLong())
            offsets.add(inputStream.readLong())
        }

        var lastTime = 0L
        var segLastTime = 0L
        for (i in 0 until size) {
            val frameList = mutableListOf<DanmakuMaskFrame>()

            val bytes = if (i == size - 1) {
                inputStream.readBytes()
            } else {
                val offDiff = (offsets[i + 1] - offsets[i]).toInt()
                val byteArray = ByteArray(offDiff)
                inputStream.read(byteArray)
                byteArray
            }

            val stream = DataInputStream(GZIPInputStream(bytes.inputStream()))
            while (stream.available() != 0) {
                val svgLength = stream.readInt()
                val time = stream.readLong()
                val svg = stream.readByteString(svgLength).string(Charsets.UTF_8)

                val decodedSvg = svg.split(",")[1]
                    .replace("\n", "")
                    .decodeBase64String()
                frameList.add(DanmakuMaskFrame( lastTime..<time, decodedSvg))
                lastTime = time
            }

            val startTime = segLastTime
            val endTime = if (i == size - 1) {
                Long.MAX_VALUE
            } else {
                times[i + 1]
            }
            danmakuMaskList.add(
                DanmakuMaskSegment(range = startTime..<endTime, frames = frameList)
            )
            segLastTime = endTime
        }
        return danmakuMaskList
    }
}



