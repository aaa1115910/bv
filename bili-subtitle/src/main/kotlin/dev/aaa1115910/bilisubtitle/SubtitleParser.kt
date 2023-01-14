package dev.aaa1115910.bilisubtitle

import dev.aaa1115910.bilisubtitle.entity.BiliSubtitle
import dev.aaa1115910.bilisubtitle.entity.SubtitleItem
import dev.aaa1115910.bilisubtitle.entity.Timestamp
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

object SubtitleParser {
    fun fromBccString(bcc: String): List<SubtitleItem> {
        val result = mutableListOf<SubtitleItem>()
        val bccResult = runCatching {
            Json.decodeFromString<BiliSubtitle>(bcc)
        }.getOrNull()
        bccResult?.body?.forEach { bccItem ->
            result.add(
                SubtitleItem(
                    from = Timestamp.fromBccString(bccItem.from),
                    to = Timestamp.fromBccString(bccItem.to),
                    content = bccItem.content
                )
            )
        }
        return result
    }

    fun fromSrtString(srt: String): List<SubtitleItem> {
        val result = mutableListOf<SubtitleItem>()
        val lines = srt.lines()
        var position = 0
        while (position < lines.size) {
            @Suppress("UNUSED_VARIABLE")
            val index = lines[position++]
            val timeLines = lines[position++]
            val content = lines[position++]
            position++
            val times = timeLines.split(" ")
            val from = times.first()
            val to = times.last()
            result.add(
                SubtitleItem(
                    from = Timestamp.fromSrtString(from),
                    to = Timestamp.fromSrtString(to),
                    content = content
                )
            )
        }
        return result
    }
}
