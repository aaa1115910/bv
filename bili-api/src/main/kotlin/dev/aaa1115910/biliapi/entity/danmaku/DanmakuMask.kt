package dev.aaa1115910.biliapi.entity.danmaku

import io.ktor.util.decodeBase64String
import okio.ByteString.Companion.readByteString
import java.io.ByteArrayInputStream
import java.io.DataInputStream
import java.util.zip.GZIPInputStream

data class DanmakuMaskSegment(
    val range: LongRange,
    val frames: List<DanmakuMaskFrame>,
)

sealed class DanmakuMaskFrame(
    open val range: LongRange
)

data class DanmakuWebMaskFrame(
    override val range: LongRange,
    val svg: String
) : DanmakuMaskFrame(range)

data class DanmakuMobMaskFrame(
    override val range: LongRange,
    val width: Int,
    val height: Int,
    val image: ByteArray
) : DanmakuMaskFrame(range) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DanmakuMobMaskFrame

        if (range != other.range) return false
        if (width != other.width) return false
        if (height != other.height) return false
        if (!image.contentEquals(other.image)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = range.hashCode()
        result = 31 * result + width
        result = 31 * result + height
        result = 31 * result + image.contentHashCode()
        return result
    }
}

data class DanmakuMask(
    val type: DanmakuMaskType,
    val segments: List<DanmakuMaskSegment>
) {
    companion object {
        fun fromBinary(binary: ByteArray, type: DanmakuMaskType): DanmakuMask {
            val times = mutableListOf<Long>()
            val offsets = mutableListOf<Long>()
            val danmakuMaskSegments = mutableListOf<DanmakuMaskSegment>()

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

                val stream =
                    DataInputStream(ByteArrayInputStream(GZIPInputStream(bytes.inputStream()).readBytes()))
                while (stream.available() != 0) {
                    when (type) {
                        DanmakuMaskType.WebMask -> {
                            val svgLength = stream.readInt()
                            val time = stream.readLong()
                            val svg = stream.readByteString(svgLength).string(Charsets.UTF_8)

                            val decodedSvg = svg.split(",")[1]
                                .replace("\n", "")
                                .decodeBase64String()
                            frameList.add(
                                DanmakuWebMaskFrame(
                                    range = lastTime..<time,
                                    svg = decodedSvg
                                )
                            )
                            lastTime = time
                        }

                        DanmakuMaskType.MobMask -> {
                            val width = stream.readShort().toInt()
                            val height = stream.readShort().toInt()
                            val time = stream.readLong()
                            val imageBinary = ByteArray(7200)
                            stream.read(imageBinary)
                            frameList.add(
                                DanmakuMobMaskFrame(
                                    range = lastTime..<time,
                                    width = width,
                                    height = height,
                                    image = imageBinary
                                )
                            )
                            lastTime = time
                        }
                    }
                }

                val startTime = segLastTime
                val endTime = if (i == size - 1) Long.MAX_VALUE else times[i + 1]
                danmakuMaskSegments.add(
                    DanmakuMaskSegment(range = startTime..<endTime, frames = frameList)
                )
                segLastTime = endTime
            }

            return DanmakuMask(type, danmakuMaskSegments)
        }
    }
}

enum class DanmakuMaskType {
    WebMask, MobMask
}