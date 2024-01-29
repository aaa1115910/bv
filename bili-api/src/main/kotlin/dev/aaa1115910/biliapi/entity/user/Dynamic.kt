package dev.aaa1115910.biliapi.entity.user

import bilibili.app.dynamic.v2.DynModuleType
import bilibili.app.dynamic.v2.Module
import bilibili.app.dynamic.v2.ModuleDynamic.ModuleItemCase
import bilibili.app.dynamic.v2.Paragraph
import io.github.oshai.kotlinlogging.KotlinLogging

data class DynamicData(
    val dynamics: List<DynamicItem>,
    val hasMore: Boolean,
    val historyOffset: String,
    val updateBaseline: String
) {
    companion object {
        private val logger = KotlinLogging.logger { }
        private val availableWebDynamicTypes = listOf(
            DynamicType.Av,
            DynamicType.Draw,
            DynamicType.Word
        ).map { it.string }
        private val availableAppDynamicTypes = listOf(
            bilibili.app.dynamic.v2.DynamicType.av,
            bilibili.app.dynamic.v2.DynamicType.draw,
            bilibili.app.dynamic.v2.DynamicType.word
        )

        fun fromDynamicData(data: dev.aaa1115910.biliapi.http.entity.dynamic.DynamicData) =
            DynamicData(
                dynamics = data.items
                    .mapNotNull {
                        if (!availableWebDynamicTypes.contains(it.type)) {
                            logger.warn { "unknown dynamic type ${it.type}, up: ${it.modules.moduleAuthor.name}, date: ${it.modules.moduleAuthor.pubTime}" }
                            return@mapNotNull null
                        }
                        DynamicItem.fromDynamicItem(it)
                    },
                hasMore = data.hasMore,
                historyOffset = data.offset,
                updateBaseline = data.updateBaseline
            ).also {
                logger.info { "updateBaseline: ${data.updateBaseline}" }
                logger.info { "offset: ${data.offset}" }
            }

        fun fromDynamicData(data: bilibili.app.dynamic.v2.DynAllReply) = DynamicData(
            dynamics = data.dynamicList.listList
                .mapNotNull {
                    if (!availableAppDynamicTypes.contains(it.cardType)) {
                        logger.warn { "unknown dynamic type ${it.cardType.name}, up: ${it.getAuthorModule()?.author?.name}, date: ${it.getAuthorModule()?.ptimeLabelText}" }
                        return@mapNotNull null
                    }
                    DynamicItem.fromDynamicItem(it)
                },
            hasMore = data.dynamicList.hasMore,
            historyOffset = data.dynamicList.historyOffset,
            updateBaseline = data.dynamicList.updateBaseline
        ).also {
            logger.info { "updateBaseline: ${data.dynamicList.updateBaseline}" }
            logger.info { "historyOffset: ${data.dynamicList.historyOffset}" }
        }
    }
}

data class DynamicItem(
    var type: DynamicType,
    val author: DynamicAuthorModule,
    var video: DynamicVideoModule? = null,
    var draw: DynamicDrawModule? = null,
    var word: DynamicWordModule? = null,
    val footer: DynamicFooterModule,
) {
    companion object {
        fun fromDynamicItem(item: dev.aaa1115910.biliapi.http.entity.dynamic.DynamicItem): DynamicItem {
            val dynamicType = DynamicType.fromString(item.type)
                ?: throw IllegalArgumentException("unknown type ${item.type}")
            val dynamicItem = DynamicItem(
                type = dynamicType,
                author = DynamicAuthorModule.fromModuleAuthor(item.modules.moduleAuthor),
                footer = DynamicFooterModule.fromModuleStat(item.modules.moduleStat)
            )
            when (dynamicType) {
                DynamicType.Av -> dynamicItem.video =
                    DynamicVideoModule.fromModuleArchive(item.modules.moduleDynamic.major!!.archive!!)

                DynamicType.UgcSeason -> TODO()
                DynamicType.Forward -> TODO()
                DynamicType.Word -> dynamicItem.word =
                    DynamicWordModule.fromModuleDynamic(item.modules.moduleDynamic)

                DynamicType.Draw -> dynamicItem.draw =
                    DynamicDrawModule.fromModuleDynamic(item.modules.moduleDynamic)
            }
            return dynamicItem
        }

        fun fromDynamicItem(item: bilibili.app.dynamic.v2.DynamicItem): DynamicItem {
            val dynamicType = when (item.cardType) {
                bilibili.app.dynamic.v2.DynamicType.av -> DynamicType.Av
                bilibili.app.dynamic.v2.DynamicType.draw -> DynamicType.Draw
                bilibili.app.dynamic.v2.DynamicType.word -> DynamicType.Word
                else -> throw IllegalArgumentException("unknown type ${item.cardType.name}")
            }
            val dynamicItem = DynamicItem(
                type = dynamicType,
                author = DynamicAuthorModule.fromModuleAuthor(item.getAuthorModule()!!),
                video = item.getDynamicModule()?.let {
                    DynamicVideoModule.fromModuleArchive(it.dynArchive).apply {
                        text = item.getDescModule()?.text ?: ""
                    }
                },
                footer = DynamicFooterModule.fromModuleStat(item.getStatModule()!!)
            )

            when (dynamicType) {
                DynamicType.Av -> dynamicItem.video = item.getDynamicModule()?.let {
                    DynamicVideoModule.fromModuleArchive(it.dynArchive).apply {
                        text = item.getDescModule()?.text ?: ""
                    }
                }

                DynamicType.Draw -> dynamicItem.draw =
                    DynamicDrawModule.fromModuleOpusSummaryAndModuleDynamic(
                        item.getOpusSummaryModule()!!,
                        item.getDynamicModule()!!
                    )

                DynamicType.Word -> dynamicItem.word =
                    DynamicWordModule.fromModuleOpusSummary(item.getOpusSummaryModule()!!)

                else -> TODO()
            }

            return dynamicItem
        }
    }

    data class DynamicAuthorModule(
        val author: String,
        val avatar: String,
        val mid: Long,
        val pubTime: String,
        val pubAction: String
    ) {
        companion object {
            fun fromModuleAuthor(moduleAuthor: dev.aaa1115910.biliapi.http.entity.dynamic.DynamicItem.Modules.Author) =
                DynamicAuthorModule(
                    author = moduleAuthor.name,
                    avatar = moduleAuthor.face,
                    mid = moduleAuthor.mid,
                    pubTime = moduleAuthor.pubTime,
                    pubAction = moduleAuthor.pubAction
                )

            fun fromModuleAuthor(moduleAuthor: bilibili.app.dynamic.v2.ModuleAuthor) =
                DynamicAuthorModule(
                    author = moduleAuthor.author.name,
                    avatar = moduleAuthor.author.face,
                    mid = moduleAuthor.author.mid,
                    pubTime = moduleAuthor.ptimeLabelText,
                    pubAction = ""
                )
        }
    }

    data class DynamicVideoModule(
        val aid: Int,
        val bvid: String? = null,
        val cid: Int,
        val epid: Int? = null,
        val seasonId: Int? = null,
        val title: String,
        var text: String,
        val cover: String,
        val duration: String,
        val play: String,
        val danmaku: String
    ) {
        companion object {
            fun fromModuleArchive(moduleArchive: dev.aaa1115910.biliapi.http.entity.dynamic.DynamicItem.Modules.Dynamic.Major.Archive) =
                DynamicVideoModule(
                    aid = moduleArchive.aid.toInt(),
                    bvid = moduleArchive.bvid,
                    cid = 0,
                    title = moduleArchive.title,
                    text = moduleArchive.desc,
                    cover = moduleArchive.cover,
                    duration = moduleArchive.durationText,
                    play = moduleArchive.stat.play,
                    danmaku = moduleArchive.stat.danmaku,
                )

            fun fromModuleArchive(moduleArchive: bilibili.app.dynamic.v2.MdlDynArchive) =
                DynamicVideoModule(
                    aid = moduleArchive.avid.toInt(),
                    bvid = moduleArchive.bvid,
                    cid = moduleArchive.cid.toInt(),
                    epid = moduleArchive.episodeId.toInt(),
                    seasonId = moduleArchive.pgcSeasonId.toInt(),
                    title = moduleArchive.title,
                    text = "",
                    cover = moduleArchive.cover,
                    duration = moduleArchive.coverLeftText1,
                    play = moduleArchive.coverLeftText2,
                    danmaku = moduleArchive.coverLeftText3
                )
        }
    }

    data class DynamicFooterModule(
        val like: Int,
        val comment: Int,
        val share: Int
    ) {
        companion object {
            fun fromModuleStat(moduleStat: dev.aaa1115910.biliapi.http.entity.dynamic.DynamicItem.Modules.Stat) =
                DynamicFooterModule(
                    like = moduleStat.like.count,
                    comment = moduleStat.comment.count,
                    share = moduleStat.forward.count
                )

            fun fromModuleStat(moduleStat: bilibili.app.dynamic.v2.ModuleStat) =
                DynamicFooterModule(
                    like = moduleStat.like.toInt(),
                    comment = moduleStat.reply.toInt(),
                    share = moduleStat.repost.toInt()
                )
        }
    }

    data class DynamicDrawModule(
        val text: String,
        val images: List<String>
    ) {
        companion object {
            fun fromModuleDynamic(moduleDynamic: dev.aaa1115910.biliapi.http.entity.dynamic.DynamicItem.Modules.Dynamic) =
                DynamicDrawModule(
                    text = moduleDynamic.desc!!.text,
                    images = moduleDynamic.major!!.draw!!.items.map { it.src }.distinct()
                )

            fun fromModuleOpusSummaryAndModuleDynamic(
                moduleOpusSummary: bilibili.app.dynamic.v2.ModuleOpusSummary,
                moduleDynamic: bilibili.app.dynamic.v2.ModuleDynamic
            ): DynamicDrawModule {
                var text = ""
                val images = mutableListOf<String>()

                when (val summaryContentType = moduleOpusSummary.summary.contentCase) {
                    Paragraph.ContentCase.TEXT -> text = moduleOpusSummary.summary.text.nodesList
                        .joinToString("") { it.rawText }

                    else -> println("not implemented: ModuleOpusSummary summaryContentType: $summaryContentType")
                }

                when (val dynamicItemType = moduleDynamic.moduleItemCase) {
                    ModuleItemCase.DYN_DRAW -> images.addAll(moduleDynamic.dynDraw.itemsList.map { it.src })
                    else -> println("not implemented: ModuleOpusSummary dynamicItemType $dynamicItemType")
                }

                return DynamicDrawModule(
                    text = text,
                    images = images.distinct()
                )
            }
        }
    }

    data class DynamicWordModule(
        val text: String
    ) {
        companion object {
            fun fromModuleDynamic(moduleDynamic: dev.aaa1115910.biliapi.http.entity.dynamic.DynamicItem.Modules.Dynamic) =
                DynamicWordModule(
                    text = moduleDynamic.desc!!.text
                )

            fun fromModuleOpusSummary(moduleOpusSummary: bilibili.app.dynamic.v2.ModuleOpusSummary) =
                DynamicWordModule(
                    text = moduleOpusSummary.summary.text.nodesList
                        .joinToString("") { it.rawText }
                )
        }
    }
}

data class DynamicVideoData(
    val videos: List<DynamicVideo>,
    val hasMore: Boolean,
    val historyOffset: String,
    val updateBaseline: String
) {
    companion object {
        private val logger = KotlinLogging.logger { }
        fun fromDynamicData(data: dev.aaa1115910.biliapi.http.entity.dynamic.DynamicData) =
            DynamicVideoData(
                videos = data.items.map { DynamicVideo.fromDynamicVideoItem(it) },
                hasMore = data.hasMore,
                historyOffset = data.offset,
                updateBaseline = data.updateBaseline
            ).also {
                logger.info { "updateBaseline: ${data.updateBaseline}" }
                logger.info { "offset: ${data.offset}" }
            }

        fun fromDynamicData(data: bilibili.app.dynamic.v2.DynVideoReply) = DynamicVideoData(
            videos = data.dynamicList.listList.map { DynamicVideo.fromDynamicVideoItem(it) },
            hasMore = data.dynamicList.hasMore,
            historyOffset = data.dynamicList.historyOffset,
            updateBaseline = data.dynamicList.updateBaseline
        ).also {
            logger.info { "updateBaseline: ${data.dynamicList.updateBaseline}" }
            logger.info { "historyOffset: ${data.dynamicList.historyOffset}" }
        }
    }
}

/**
 * 动态视频
 *
 * @property aid 视频av号
 * @property bvid 视频bv号，grpc pgc 没有bv号
 * @property cid 视频cid，仅 grpc 接口
 * @property epid 番剧epid，仅 grpc 接口
 * @property seasonId 番剧seasonId，仅 grpc 接口
 * @property title 视频标题
 * @property cover 视频封面
 * @property author 视频作者
 * @property duration 视频时长，单位秒
 * @property play 视频播放量
 * @property danmaku 视频弹幕数
 * @property avatar 视频作者头像
 */
data class DynamicVideo(
    val aid: Int,
    val bvid: String? = null,
    val cid: Int,
    val epid: Int? = null,
    val seasonId: Int? = null,
    val title: String,
    val cover: String,
    val author: String,
    val duration: Int,
    val play: Int,
    val danmaku: Int,
    val avatar: String,
    val time: Long = 0L,
) {
    companion object {
        fun fromDynamicVideoItem(item: dev.aaa1115910.biliapi.http.entity.dynamic.DynamicItem): DynamicVideo {
            val archive = item.modules.moduleDynamic.major!!.archive!!
            val author = item.modules.moduleAuthor
            return DynamicVideo(
                aid = archive.aid.toInt(),
                bvid = archive.bvid,
                cid = 0,
                title = archive.title
                    .replace("动态视频｜", ""),
                cover = archive.cover,
                author = author.name,
                duration = convertStringTimeToSeconds(archive.durationText),
                play = convertStringPlayCountToNumberPlayCount(archive.stat.play),
                danmaku = convertStringPlayCountToNumberPlayCount(archive.stat.danmaku),
                avatar = author.face,
            )
        }

        fun fromDynamicVideoItem(item: bilibili.app.dynamic.v2.DynamicItem): DynamicVideo {
            val author =
                item.modulesList.first { it.moduleType == DynModuleType.module_author }.moduleAuthor.author
            val dynamic =
                item.modulesList.first { it.moduleType == DynModuleType.module_dynamic }.moduleDynamic
            val desc =
                item.modulesList.firstOrNull { it.moduleType == DynModuleType.module_desc }?.moduleDesc
            val isDynamicVideo = desc?.text?.startsWith("动态视频") ?: false
            when (dynamic.moduleItemCase) {
                ModuleItemCase.DYN_ARCHIVE -> {
                    val archive = dynamic.dynArchive
                    return DynamicVideo(
                        aid = archive.avid.toInt(),
                        bvid = archive.bvid,
                        cid = archive.cid.toInt(),
                        title = if (!isDynamicVideo) archive.title else desc!!.text.substring(5),
                        cover = archive.cover,
                        author = author.name,
                        duration = convertStringTimeToSeconds(archive.coverLeftText1),
                        play = convertStringPlayCountToNumberPlayCount(archive.coverLeftText2),
                        danmaku = convertStringPlayCountToNumberPlayCount(archive.coverLeftText3),
                        avatar = author.face
                    )
                }

                ModuleItemCase.DYN_PGC -> {
                    val pgc = dynamic.dynPgc
                    return DynamicVideo(
                        aid = pgc.aid.toInt(),
                        bvid = null,
                        cid = pgc.cid.toInt(),
                        epid = pgc.cid.toInt(),
                        seasonId = pgc.seasonId.toInt(),
                        title = pgc.title,
                        cover = pgc.cover,
                        author = author.name,
                        duration = convertStringTimeToSeconds(pgc.coverLeftText1),
                        play = convertStringPlayCountToNumberPlayCount(pgc.coverLeftText2),
                        danmaku = convertStringPlayCountToNumberPlayCount(pgc.coverLeftText3),
                        avatar = author.face
                    )
                }

                else -> TODO("还没写")
            }
        }
    }
}

private fun convertStringTimeToSeconds(time: String): Int {
    //部分稿件可能没有时长，Web 接口返回 NaN:NaN:NaN，App 接口返回空字符串
    if (time.startsWith("NaN") || time.isBlank()) return 0

    val parts = time.split(":")
    val hours = if (parts.size == 3) parts[0].toInt() else 0
    val minutes = parts[parts.size - 2].toInt()
    val seconds = parts[parts.size - 1].toInt()
    return (hours * 3600) + (minutes * 60) + seconds
}

//web 接口获取到的是“xx万”，而 grpc 接口获取到的是“xx.x万播放”
private fun convertStringPlayCountToNumberPlayCount(play: String): Int {
    if (play.startsWith("-")) return 0
    runCatching {
        val number = play
            .replace("弹幕", "")
            .replace("观看", "")
            .replace("播放", "")
            .substringBefore("万").toFloat()
        return (if (play.contains("万")) number * 10000 else number).toInt()
    }.onFailure {
        println("convert play count [$play] failed: ${it.stackTraceToString()}")
    }
    return -1
}

enum class DynamicType(val string: String) {
    Av("DYNAMIC_TYPE_AV"),
    UgcSeason("DYNAMIC_TYPE_UGC_SEASON"),
    Forward("DYNAMIC_TYPE_FORWARD"),
    Word("DYNAMIC_TYPE_WORD"),
    Draw("DYNAMIC_TYPE_DRAW");

    companion object {
        fun fromString(string: String) = entries.firstOrNull { it.string == string }
    }
}

private fun Module.isAuthorModule() = moduleType == DynModuleType.module_author
private fun Module.isDescModule() = moduleType == DynModuleType.module_desc
private fun Module.isDynamicModule() = moduleType == DynModuleType.module_dynamic
private fun Module.isModuleOpusSummary() = moduleType == DynModuleType.module_opus_summary
private fun Module.isStatModule() = moduleType == DynModuleType.module_stat

private fun bilibili.app.dynamic.v2.DynamicItem.getAuthorModule() =
    modulesList.firstOrNull { it.isAuthorModule() }?.moduleAuthor

private fun bilibili.app.dynamic.v2.DynamicItem.getDescModule() =
    modulesList.firstOrNull { it.isDescModule() }?.moduleDesc


private fun bilibili.app.dynamic.v2.DynamicItem.getDynamicModule() =
    modulesList.firstOrNull { it.isDynamicModule() }?.moduleDynamic

private fun bilibili.app.dynamic.v2.DynamicItem.getOpusSummaryModule() =
    modulesList.firstOrNull { it.isModuleOpusSummary() }?.moduleOpusSummary

private fun bilibili.app.dynamic.v2.DynamicItem.getStatModule() =
    modulesList.firstOrNull { it.isStatModule() }?.moduleStat


