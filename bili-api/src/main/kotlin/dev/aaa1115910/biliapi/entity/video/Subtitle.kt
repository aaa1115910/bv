package dev.aaa1115910.biliapi.entity.video

//TODO 将 lanDoc 内括号的内容分离出来，将其作为 Badge 显示
/**
 * 字幕
 *
 * @param id 字幕 id
 * @param lang 字幕语言，例如 zh-Hant ai-zh ai-en
 * @param langDoc 字幕语言名称
 * @param url 字幕地址
 * @param type 字幕类型 人工/AI
 * @param aiType AI 字幕类型 生成/翻译
 * @param aiStatus AI 字幕状态
 */
data class Subtitle(
    val id: Long,
    val lang: String,
    val langDoc: String,
    val url: String,
    var type: SubtitleType,
    val aiType: SubtitleAiType,
    var aiStatus: SubtitleAiStatus
) {
    companion object {
        fun fromSubtitleItem(data: dev.aaa1115910.biliapi.http.entity.video.VideoMoreInfo.SubtitleItem) =
            Subtitle(
                id = data.id,
                lang = data.lan,
                langDoc = data.lanDoc,
                url = data.subtitleUrl,
                type = when (data.type) {
                    0 -> SubtitleType.CC
                    1 -> SubtitleType.AI
                    else -> SubtitleType.CC
                },
                aiType = when (data.aiType) {
                    0 -> SubtitleAiType.Normal
                    1 -> SubtitleAiType.Translate
                    else -> SubtitleAiType.Normal
                },
                aiStatus = when (data.aiStatus) {
                    0 -> SubtitleAiStatus.None
                    1 -> SubtitleAiStatus.Exposure
                    2 -> SubtitleAiStatus.Assist
                    else -> SubtitleAiStatus.None
                }
            )

        fun fromSubtitleItem(data: bilibili.community.service.dm.v1.SubtitleItem) =
            Subtitle(
                id = data.id,
                lang = data.lan,
                langDoc = data.lanDoc,
                url = data.subtitleUrl,
                type = when (data.type) {
                    bilibili.community.service.dm.v1.SubtitleType.CC -> SubtitleType.CC
                    bilibili.community.service.dm.v1.SubtitleType.AI -> SubtitleType.AI
                    else -> SubtitleType.CC
                },
                aiType = when (data.aiType) {
                    bilibili.community.service.dm.v1.SubtitleAiType.Normal -> SubtitleAiType.Normal
                    bilibili.community.service.dm.v1.SubtitleAiType.Translate -> SubtitleAiType.Translate
                    else -> SubtitleAiType.Normal
                },
                aiStatus = when (data.aiStatus) {
                    bilibili.community.service.dm.v1.SubtitleAiStatus.None -> SubtitleAiStatus.None
                    bilibili.community.service.dm.v1.SubtitleAiStatus.Exposure -> SubtitleAiStatus.Exposure
                    bilibili.community.service.dm.v1.SubtitleAiStatus.Assist -> SubtitleAiStatus.Assist
                    else -> SubtitleAiStatus.None
                }
            )
    }
}

enum class SubtitleType {
    CC, AI
}

enum class SubtitleAiType {
    Normal, Translate
}

enum class SubtitleAiStatus {
    None, Exposure, Assist
}