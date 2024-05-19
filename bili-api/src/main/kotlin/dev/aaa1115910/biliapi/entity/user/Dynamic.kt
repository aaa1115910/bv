package dev.aaa1115910.biliapi.entity.user

import bilibili.app.dynamic.v2.DynModuleType
import bilibili.app.dynamic.v2.Module
import bilibili.app.dynamic.v2.ModuleDynamic.ModuleItemCase
import bilibili.app.dynamic.v2.Paragraph
import dev.aaa1115910.biliapi.entity.Picture
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import okhttp3.internal.toLongOrDefault

data class DynamicData(
    val dynamics: List<DynamicItem>,
    val hasMore: Boolean,
    val historyOffset: String,
    val updateBaseline: String
) {
    companion object {
        private val logger = KotlinLogging.logger { }
        private val availableDynamicTypes = listOf(
            DynamicType.Av,
            DynamicType.Draw,
            DynamicType.Forward,
            DynamicType.Word,
            DynamicType.LiveRcmd
        )
        private val availableWebDynamicTypes = availableDynamicTypes.map { it.webValue }
        private val availableAppDynamicTypes = availableDynamicTypes.map { it.appValue }

        fun fromDynamicData(data: dev.aaa1115910.biliapi.http.entity.dynamic.DynamicData) =
            DynamicData(
                dynamics = data.items
                    .mapNotNull {
                        if (!availableWebDynamicTypes.contains(it.type)) {
                            logger.warn { "unknown dynamic type ${it.type}, up: ${it.modules.moduleAuthor.name}, date: ${it.modules.moduleAuthor.pubTime}" }
                            return@mapNotNull null
                        }

                        if (it.type == DynamicType.Forward.webValue) {
                            if (!availableWebDynamicTypes.contains(it.orig?.type)) {
                                logger.warn { "unknown dynamic forward type ${it.orig?.type}, up: ${it.modules.moduleAuthor.name}, date: ${it.modules.moduleAuthor.pubTime}" }
                                return@mapNotNull null
                            }
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

                    if (it.cardType == bilibili.app.dynamic.v2.DynamicType.forward) {
                        if (!availableAppDynamicTypes.contains(it.getDynamicModule()?.dynForward?.item?.cardType)) {
                            logger.warn { "unknown dynamic forward type ${it.getDynamicModule()?.dynForward?.item?.cardType}, up: ${it.getAuthorModule()?.author?.name}, date: ${it.getAuthorModule()?.ptimeLabelText}" }
                            return@mapNotNull null
                        }
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
    var commentId: Long = 0,
    var commentType: Int = 0,
    var type: DynamicType,
    val author: DynamicAuthorModule,
    var video: DynamicVideoModule? = null,
    var draw: DynamicDrawModule? = null,
    var word: DynamicWordModule? = null,
    var liveRcmd: DynamicLiveRcmdModule? = null,
    val footer: DynamicFooterModule? = null,
    var orig: DynamicItem? = null
) {
    companion object {
        fun fromDynamicItem(item: dev.aaa1115910.biliapi.http.entity.dynamic.DynamicItem): DynamicItem {
            val dynamicType = DynamicType.fromWebValue(item.type)
            val dynamicItem = DynamicItem(
                commentId = item.basic.commentIdStr.toLongOrDefault(0),
                commentType = item.basic.commentType,
                type = dynamicType,
                author = DynamicAuthorModule.fromModuleAuthor(item.modules.moduleAuthor),
                footer = DynamicFooterModule.fromModuleStat(item.modules.moduleStat)
            )
            when (dynamicType) {
                DynamicType.Av -> dynamicItem.video =
                    DynamicVideoModule.fromModuleArchive(item.modules.moduleDynamic.major!!.archive!!)

                DynamicType.UgcSeason -> TODO()
                DynamicType.Forward -> dynamicItem.apply {
                    word = DynamicWordModule.fromModuleDynamic(item.modules.moduleDynamic)
                    orig = fromDynamicItem(item.orig!!)
                }

                DynamicType.Word -> dynamicItem.word =
                    DynamicWordModule.fromModuleDynamic(item.modules.moduleDynamic)

                DynamicType.Draw -> dynamicItem.draw =
                    DynamicDrawModule.fromModuleDynamic(item.modules.moduleDynamic)

                DynamicType.LiveRcmd -> dynamicItem.liveRcmd =
                    DynamicLiveRcmdModule.fromModuleDynamic(item.modules.moduleDynamic)
            }
            return dynamicItem
        }

        fun fromDynamicItem(
            item: bilibili.app.dynamic.v2.DynamicItem,
            isForwardItem: Boolean = false
        ): DynamicItem {
            val dynamicType = DynamicType.fromAppValue(item.cardType)
            val dynamicItem = DynamicItem(
                commentId = item.extend.businessId.toLongOrDefault(0),
                commentType = item.extend.rType,
                type = dynamicType,
                author = if (isForwardItem) {
                    DynamicAuthorModule.fromExtendAndModuleAuthorForward(
                        item.extend, item.getAuthorModuleForward()!!
                    )
                } else {
                    DynamicAuthorModule.fromModuleAuthor(item.getAuthorModule()!!)
                },
                video = item.getDynamicModule()?.let {
                    DynamicVideoModule.fromModuleArchive(it.dynArchive).apply {
                        text = item.getDescModule()?.text ?: ""
                    }
                },
                footer = if (!isForwardItem) DynamicFooterModule.fromModuleStat(item.getStatModule()!!) else null
            )

            when (dynamicType) {
                DynamicType.Av -> dynamicItem.video = item.getDynamicModule()?.let {
                    DynamicVideoModule.fromModuleArchive(it.dynArchive).apply {
                        text = item.getDescModule()?.text ?: ""
                    }
                }

                DynamicType.UgcSeason -> TODO()
                DynamicType.Draw -> dynamicItem.draw =
                    DynamicDrawModule.fromModuleOpusSummaryAndModuleDynamic(
                        item.getOpusSummaryModule()!!,
                        item.getDynamicModule()!!
                    )

                DynamicType.Word -> dynamicItem.word =
                    DynamicWordModule.fromModuleOpusSummary(item.getOpusSummaryModule()!!)

                DynamicType.Forward -> dynamicItem.apply {
                    word = DynamicWordModule.fromModuleDesc(item.getDescModule()!!)
                    orig = fromDynamicItem(item.getDynamicModule()!!.dynForward.item, true)
                }

                DynamicType.LiveRcmd -> dynamicItem.liveRcmd =
                    DynamicLiveRcmdModule.fromModuleDynamic(item.getDynamicModule()!!)
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

            fun fromExtendAndModuleAuthorForward(
                extend: bilibili.app.dynamic.v2.Extend,
                moduleAuthorForward: bilibili.app.dynamic.v2.ModuleAuthorForward
            ) =
                DynamicAuthorModule(
                    author = extend.origName,
                    avatar = extend.origFace,
                    mid = extend.uid,
                    pubTime = moduleAuthorForward.ptimeLabelText,
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
            fun fromModuleStat(moduleStat: dev.aaa1115910.biliapi.http.entity.dynamic.DynamicItem.Modules.Stat?) =
                moduleStat?.let {
                    DynamicFooterModule(
                        like = moduleStat.like.count,
                        comment = moduleStat.comment.count,
                        share = moduleStat.forward.count
                    )
                }

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
        val images: List<Picture>
    ) {
        companion object {
            fun fromModuleDynamic(moduleDynamic: dev.aaa1115910.biliapi.http.entity.dynamic.DynamicItem.Modules.Dynamic) =
                DynamicDrawModule(
                    text = moduleDynamic.desc!!.text,
                    images = moduleDynamic.major!!.draw!!.items
                        .map(Picture::fromPicture)
                        .distinctBy { it.url }
                )

            fun fromModuleOpusSummaryAndModuleDynamic(
                moduleOpusSummary: bilibili.app.dynamic.v2.ModuleOpusSummary,
                moduleDynamic: bilibili.app.dynamic.v2.ModuleDynamic
            ): DynamicDrawModule {
                var text = ""
                val images = mutableListOf<Picture>()

                when (val summaryContentType = moduleOpusSummary.summary.contentCase) {
                    Paragraph.ContentCase.TEXT -> text = moduleOpusSummary.summary.text.nodesList
                        .joinToString("") { it.rawText }

                    else -> println("not implemented: ModuleOpusSummary summaryContentType: $summaryContentType")
                }

                when (val dynamicItemType = moduleDynamic.moduleItemCase) {
                    ModuleItemCase.DYN_DRAW -> images.addAll(
                        moduleDynamic.dynDraw.itemsList.map(Picture::fromPicture)
                    )

                    else -> println("not implemented: ModuleOpusSummary dynamicItemType $dynamicItemType")
                }

                return DynamicDrawModule(
                    text = text,
                    images = images.distinctBy { it.url }
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

            fun fromModuleDesc(moduleDesc: bilibili.app.dynamic.v2.ModuleDesc) =
                DynamicWordModule(
                    text = moduleDesc.text
                )
        }
    }

    data class DynamicLiveRcmdModule(
        val title: String,
        val cover: String,
        val roomId: Int
    ) {
        companion object {
            private val json = Json {
                coerceInputValues = true
                ignoreUnknownKeys = true
                prettyPrint = true
            }

            fun fromModuleDynamic(moduleDynamic: dev.aaa1115910.biliapi.http.entity.dynamic.DynamicItem.Modules.Dynamic): DynamicLiveRcmdModule {
                val liveRcmdContent =
                    json.decodeFromString<LiveRcmdContent>(moduleDynamic.major!!.liveRcmd!!.content)
                return DynamicLiveRcmdModule(
                    title = liveRcmdContent.livePlayInfo.title,
                    cover = liveRcmdContent.livePlayInfo.cover,
                    roomId = liveRcmdContent.livePlayInfo.roomId
                )
            }

            fun fromModuleDynamic(moduleDynamic: bilibili.app.dynamic.v2.ModuleDynamic): DynamicLiveRcmdModule {
                val liveRcmdContent =
                    json.decodeFromString<LiveRcmdContent>(moduleDynamic.dynLiveRcmd.content)
                return DynamicLiveRcmdModule(
                    title = liveRcmdContent.livePlayInfo.title,
                    cover = liveRcmdContent.livePlayInfo.cover,
                    roomId = liveRcmdContent.livePlayInfo.roomId
                )
            }
        }

        @Serializable
        private data class LiveRcmdContent(
            @SerialName("live_play_info")
            val livePlayInfo: LivePlayInfo,
            @SerialName("live_record_info")
            val liveRecordInfo: JsonElement? = null,
            val type: Int
        ) {
            @Serializable
            data class LivePlayInfo(
                val title: String,
                @SerialName("parent_area_name")
                val parentAreaName: String,
                val cover: String,
                val online: Int,
                @SerialName("parent_area_id")
                val parentAreaId: Int,
                @SerialName("live_start_time")
                val liveStartTime: Long,
                @SerialName("room_id")
                val roomId: Int,
                @SerialName("live_status")
                val liveStatus: Int,
                @SerialName("room_type")
                val roomType: Int,
                @SerialName("play_type")
                val playType: Int,
                val link: String,
                @SerialName("area_id")
                val areaId: Int,
                @SerialName("area_name")
                val areaName: String,
                @SerialName("watched_show")
                val watchedShow: WatchedShow,
                @SerialName("room_paid_type")
                val roomPaidType: Int,
                val uid: Long,
                @SerialName("live_screen_type")
                val liveScreenType: Int,
                @SerialName("live_id")
                val liveId: Long,
                val pendants: Pendants
            ) {
                @Serializable
                data class WatchedShow(
                    val num: Int,
                    @SerialName("text_small")
                    val textSmall: String,
                    @SerialName("text_large")
                    val textLarge: String,
                    val icon: String,
                    @SerialName("icon_location")
                    val iconLocation: String,
                    @SerialName("icon_web")
                    val iconWeb: String,
                    val switch: Boolean
                )

                @Serializable
                data class Pendants(
                    val list: JsonElement? = null
                )
            }
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
    val aid: Long,
    val bvid: String? = null,
    val cid: Long,
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
                aid = archive.aid.toLong(),
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
                        aid = archive.avid,
                        bvid = archive.bvid,
                        cid = archive.cid,
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
                        aid = pgc.aid,
                        bvid = null,
                        cid = pgc.cid,
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

enum class DynamicType(val webValue: String, val appValue: bilibili.app.dynamic.v2.DynamicType) {
    Av("DYNAMIC_TYPE_AV", bilibili.app.dynamic.v2.DynamicType.av),
    UgcSeason("DYNAMIC_TYPE_UGC_SEASON", bilibili.app.dynamic.v2.DynamicType.ugc_season),
    Forward("DYNAMIC_TYPE_FORWARD", bilibili.app.dynamic.v2.DynamicType.forward),
    Word("DYNAMIC_TYPE_WORD", bilibili.app.dynamic.v2.DynamicType.word),
    Draw("DYNAMIC_TYPE_DRAW", bilibili.app.dynamic.v2.DynamicType.draw),
    LiveRcmd("DYNAMIC_TYPE_LIVE_RCMD", bilibili.app.dynamic.v2.DynamicType.live_rcmd);

    companion object {
        fun fromWebValue(webValue: String) = entries.firstOrNull { it.webValue == webValue }
            ?: throw IllegalArgumentException("unknown type $webValue")

        fun fromAppValue(appValue: bilibili.app.dynamic.v2.DynamicType) =
            entries.firstOrNull { it.appValue == appValue }
                ?: throw IllegalArgumentException("unknown type ${appValue.name}")
    }
}

private fun Module.isAuthorModule() = moduleType == DynModuleType.module_author
private fun Module.isAuthorModuleForward() = moduleType == DynModuleType.module_author_forward
private fun Module.isDescModule() = moduleType == DynModuleType.module_desc
private fun Module.isDynamicModule() = moduleType == DynModuleType.module_dynamic
private fun Module.isModuleOpusSummary() = moduleType == DynModuleType.module_opus_summary
private fun Module.isStatModule() = moduleType == DynModuleType.module_stat

private fun bilibili.app.dynamic.v2.DynamicItem.getAuthorModule() =
    modulesList.firstOrNull { it.isAuthorModule() }?.moduleAuthor

private fun bilibili.app.dynamic.v2.DynamicItem.getAuthorModuleForward() =
    modulesList.firstOrNull { it.isAuthorModuleForward() }?.moduleAuthorForward

private fun bilibili.app.dynamic.v2.DynamicItem.getDescModule() =
    modulesList.firstOrNull { it.isDescModule() }?.moduleDesc


private fun bilibili.app.dynamic.v2.DynamicItem.getDynamicModule() =
    modulesList.firstOrNull { it.isDynamicModule() }?.moduleDynamic

private fun bilibili.app.dynamic.v2.DynamicItem.getOpusSummaryModule() =
    modulesList.firstOrNull { it.isModuleOpusSummary() }?.moduleOpusSummary

private fun bilibili.app.dynamic.v2.DynamicItem.getStatModule() =
    modulesList.firstOrNull { it.isStatModule() }?.moduleStat


