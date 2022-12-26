package dev.aaa1115910.bilisubtitle.entity

import kotlinx.serialization.Serializable

@Serializable
data class SrtSubtitle(
    val content: List<SrtSubtitleItem> = emptyList()
)

@Serializable
data class SrtSubtitleItem(
    val index: Int,
    val from: String,
    val to: String,
    val content: String
) {
    fun toRaw() = """
        $index
        $from --> $to
        $content
        
    """.trimIndent()
}
