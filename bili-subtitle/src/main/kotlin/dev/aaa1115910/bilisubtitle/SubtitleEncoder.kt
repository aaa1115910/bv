package dev.aaa1115910.bilisubtitle

import dev.aaa1115910.bilisubtitle.entity.BiliSubtitle
import dev.aaa1115910.bilisubtitle.entity.BiliSubtitleItem
import dev.aaa1115910.bilisubtitle.entity.SrtSubtitleItem
import dev.aaa1115910.bilisubtitle.entity.SubtitleItem
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object SubtitleEncoder {
    fun encodeToBcc(subtitles: List<SubtitleItem>): String {
        val bccSubtitles = mutableListOf<BiliSubtitleItem>()
        subtitles.forEach {
            bccSubtitles.add(
                BiliSubtitleItem(
                    from = it.from.getBccTime(),
                    to = it.to.getBccTime(),
                    location = 2,
                    content = it.content
                )
            )
        }
        val bccSubtitle = BiliSubtitle(
            fontSize = 0.4f,
            fontColor = "#FFFFFF",
            backgroundAlpha = 0.5f,
            backgroundColor = "#9C27B0",
            stroke = "none",
            body = bccSubtitles
        )
        return Json.encodeToString(bccSubtitle)
    }

    fun encodeToSrt(subtitles: List<SubtitleItem>): String {
        var result = ""
        subtitles.forEachIndexed { index, data ->
            val srtSubtitleItem = SrtSubtitleItem(
                index = index + 1,
                from = data.from.getSrtTime(),
                to = data.to.getSrtTime(),
                content = data.content.replace("\n","\\n")
            )
            if (index != 0) result += "\n"
            result += srtSubtitleItem.toRaw()
        }
        return result
    }
}