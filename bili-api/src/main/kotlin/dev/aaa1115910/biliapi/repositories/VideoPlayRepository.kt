package dev.aaa1115910.biliapi.repositories

import bilibili.app.playerunite.v1.PlayerGrpcKt
import bilibili.app.playerunite.v1.playViewUniteReq
import bilibili.community.service.dm.v1.DMGrpcKt
import bilibili.community.service.dm.v1.dmViewReq
import bilibili.pgc.gateway.player.v2.playViewReq
import bilibili.playershared.videoVod
import dev.aaa1115910.biliapi.entity.ApiType
import dev.aaa1115910.biliapi.entity.CodeType
import dev.aaa1115910.biliapi.entity.PlayData
import dev.aaa1115910.biliapi.entity.danmaku.DanmakuMask
import dev.aaa1115910.biliapi.entity.danmaku.DanmakuMaskSegment
import dev.aaa1115910.biliapi.entity.danmaku.DanmakuMaskType
import dev.aaa1115910.biliapi.entity.video.HeartbeatVideoType
import dev.aaa1115910.biliapi.entity.video.Subtitle
import dev.aaa1115910.biliapi.entity.video.VideoShot
import dev.aaa1115910.biliapi.grpc.utils.handleGrpcException
import dev.aaa1115910.biliapi.http.BiliHttpApi
import dev.aaa1115910.biliapi.http.BiliHttpProxyApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single
import bilibili.pgc.gateway.player.v2.PlayURLGrpcKt as PgcPlayURLGrpcKt

@Single
class VideoPlayRepository(
    private val authRepository: AuthRepository,
    private val channelRepository: ChannelRepository
) {
    private val playerStub
        get() = runCatching {
            PlayerGrpcKt.PlayerCoroutineStub(channelRepository.defaultChannel!!)
        }.getOrNull()
    private val pgcPlayUrlStub
        get() = runCatching {
            PgcPlayURLGrpcKt.PlayURLCoroutineStub(channelRepository.defaultChannel!!)
        }.getOrNull()
    private val danmakuStub
        get() = runCatching {
            DMGrpcKt.DMCoroutineStub(channelRepository.defaultChannel!!)
        }.getOrNull()

    private val proxyPgcPlayUrlStub
        get() = runCatching {
            PgcPlayURLGrpcKt.PlayURLCoroutineStub(channelRepository.proxyChannel!!)
        }.getOrNull()


    suspend fun getPlayData(
        aid: Long,
        cid: Long,
        preferApiType: ApiType = ApiType.Web
    ): PlayData {
        return when (preferApiType) {
            ApiType.Web -> {
                val playUrlData = BiliHttpApi.getVideoPlayUrl(
                    av = aid,
                    cid = cid,
                    fnval = 4048,
                    qn = 127,
                    fnver = 0,
                    fourk = 1,
                    sessData = authRepository.sessionData
                ).getResponseData()
                PlayData.fromPlayUrlData(playUrlData)
            }

            ApiType.App -> {
                withContext(Dispatchers.IO) {
                    val codecTypes = listOf(
                        CodeType.Code264,
                        CodeType.Code265,
                        CodeType.CodeAv1
                    )
                    val replies = codecTypes.map { codecType ->
                        async {
                            val playUniteReply = runCatching {
                                playerStub?.playViewUnite(playViewUniteReq {
                                    vod = videoVod {
                                        this.aid = aid
                                        this.cid = cid
                                        fnval = 4048
                                        qn = 127
                                        fnver = 0
                                        fourk = true
                                        preferCodecType = codecType.toPlayerSharedCodeType()
                                    }
                                }) ?: throw IllegalStateException("Player stub is not initialized")
                            }.onFailure {
                                // dont throw
                                runCatching { handleGrpcException(it) }
                                    .onFailure {
                                        println("get play data failed: [aid=$aid, cid=$cid, preferCodec=$codecType, preferApiType=$preferApiType]")
                                        it.printStackTrace()
                                    }
                            }.getOrNull()
                            playUniteReply
                        }
                    }.awaitAll()
                    val result = replies.map {
                        it?.let { PlayData.fromPlayViewUniteReply(it) }
                    }.reduce { acc, playData ->
                        acc?.let { playData?.let { acc + playData } ?: acc } ?: playData
                    } ?: throw IllegalStateException("All codec types are failed to get play data")
                    result
                }
            }
        }
    }

    suspend fun getPgcPlayData(
        aid: Long?,
        cid: Long?,
        epid: Int,
        preferCodec: CodeType = CodeType.NoCode,
        preferApiType: ApiType = ApiType.Web,
        enableProxy: Boolean = false,
        proxyArea: String = ""
    ): PlayData {
        println("get pgc play data: [aid=$aid, cid=$cid, epid=$epid, preferCodec=$preferCodec, preferApiType=$preferApiType, enableProxy=$enableProxy, proxyArea=$proxyArea]")
        return when (preferApiType) {
            ApiType.Web -> {
                val playUrlData = if (enableProxy) {
                    BiliHttpProxyApi.getPgcVideoPlayUrl(
                        av = aid,
                        cid = cid,
                        epid = epid,
                        fnval = 4048,
                        qn = 127,
                        fnver = 0,
                        fourk = 1,
                        sessData = authRepository.sessionData
                    )
                } else {
                    BiliHttpApi.getPgcVideoPlayUrl(
                        av = aid,
                        cid = cid,
                        fnval = 4048,
                        qn = 127,
                        fnver = 0,
                        fourk = 1,
                        sessData = authRepository.sessionData
                    )
                }.getResponseData()

                PlayData.fromPlayUrlData(playUrlData)
            }

            ApiType.App -> {
                withContext(Dispatchers.IO) {
                    val codecTypes = listOf(
                        CodeType.Code264,
                        CodeType.Code265,
                        CodeType.CodeAv1
                    )
                    val replies = codecTypes.map { codecType ->
                        val req = playViewReq {
                            this.epid = epid.toLong()
                            cid?.let { this.cid = it }
                            qn = 127
                            fnver = 0
                            fnval = 4048
                            fourk = true
                            forceHost = 0
                            download = 0
                            preferCodecType = codecType.toPgcPlayUrlCodeType()
                        }
                        async {
                            val playReply = runCatching {
                                if (enableProxy) {
                                    proxyPgcPlayUrlStub?.playView(req)
                                        ?: throw IllegalStateException("Proxy pgc play url stub is not initialized")
                                } else {
                                    pgcPlayUrlStub?.playView(req)
                                        ?: throw IllegalStateException("Pgc play url stub is not initialized")
                                }
                            }.onFailure {
                                // dont throw
                                runCatching { handleGrpcException(it) }
                                    .onFailure {
                                        println("get pgc play data failed: [aid=$aid, cid=$cid, epid=$epid, preferCodec=$codecType, preferApiType=$preferApiType]")
                                        it.printStackTrace()
                                    }
                            }.getOrNull()
                            playReply
                        }
                    }.awaitAll()
                    val result = replies.map {
                        it?.let { PlayData.fromPgcPlayViewReply(it) }
                    }.reduce { acc, playData ->
                        acc?.let { playData?.let { acc + playData } ?: acc } ?: playData
                    } ?: throw IllegalStateException("All codec types are failed to get play data")
                    result
                }
            }
        }
    }

    suspend fun getSubtitle(
        aid: Long,
        cid: Long,
        preferApiType: ApiType = ApiType.Web
    ): List<Subtitle> {
        return when (preferApiType) {
            ApiType.Web -> {
                val response = BiliHttpApi.getVideoMoreInfo(
                    avid = aid,
                    cid = cid,
                    sessData = authRepository.sessionData ?: ""
                ).getResponseData()
                response.subtitle?.subtitles
                    ?.map { Subtitle.fromSubtitleItem(it) }
                    ?: emptyList()
            }

            ApiType.App -> {
                val dmViewReply = runCatching {
                    danmakuStub?.dmView(dmViewReq {
                        pid = aid.toLong()
                        oid = cid.toLong()
                        type = 1
                    })
                }.onFailure { handleGrpcException(it) }.getOrThrow()
                dmViewReply?.subtitle?.subtitlesList
                    ?.map { Subtitle.fromSubtitleItem(it) }
                    ?: emptyList()
            }
        }
    }

    suspend fun sendHeartbeat(
        aid: Long,
        cid: Long,
        time: Int,
        type: HeartbeatVideoType = HeartbeatVideoType.Video,
        subType: Int? = null,
        epid: Int? = null,
        seasonId: Int? = null,
        preferApiType: ApiType = ApiType.Web
    ) {
        val result = when (preferApiType) {
            ApiType.Web -> BiliHttpApi.sendHeartbeat(
                avid = aid.toLong(),
                cid = cid,
                playedTime = time,
                type = type.value,
                subType = subType,
                epid = epid,
                sid = seasonId,
                csrf = authRepository.biliJct,
                sessData = authRepository.sessionData ?: ""
            )

            ApiType.App -> BiliHttpApi.sendHeartbeat(
                avid = aid.toLong(),
                cid = cid,
                playedTime = time,
                type = type.value,
                subType = subType,
                epid = epid,
                sid = seasonId,
                accessKey = authRepository.accessToken ?: ""
            )
        }
        println("send heartbeat result: $result")
    }

    suspend fun getDanmakuMask(
        aid: Long,
        cid: Long,
        preferApiType: ApiType = ApiType.Web
    ): List<DanmakuMaskSegment> {
        val danmakuMaskUrl = when (preferApiType) {
            ApiType.Web -> {
                val response = BiliHttpApi.getVideoMoreInfo(
                    avid = aid,
                    cid = cid,
                    sessData = authRepository.sessionData ?: ""
                ).getResponseData()
                response.dmMask?.maskUrl
            }

            ApiType.App -> {
                val dmViewReply = runCatching {
                    danmakuStub?.dmView(dmViewReq {
                        pid = aid
                        oid = cid
                        type = 1
                    })
                }.onFailure { handleGrpcException(it) }.getOrThrow()
                dmViewReply?.mask?.maskUrl
            }
        } ?: return emptyList()

        val maskBinary = BiliHttpApi.download(danmakuMaskUrl.apply {
            when (preferApiType) {
                ApiType.Web -> replace("mobmask", "webmask")
                ApiType.App -> replace("webmask", "mobmask")
            }
        })
        val danmakuMaskType = when (preferApiType) {
            ApiType.Web -> DanmakuMaskType.WebMask
            ApiType.App -> DanmakuMaskType.MobMask
        }
        return DanmakuMask.fromBinary(maskBinary, danmakuMaskType).segments
    }

    suspend fun getVideoShot(
        aid: Long,
        cid: Long,
        preferApiType: ApiType = ApiType.Web
    ): VideoShot? {
        val videoShortResponse = when (preferApiType) {
            ApiType.Web -> BiliHttpApi.getWebVideoShot(aid = aid, cid = cid)
            ApiType.App -> BiliHttpApi.getAppVideoShot(aid = aid, cid = cid)
        }
        val videoShot = VideoShot.fromVideoShot(videoShortResponse.getResponseData())
        return videoShot
    }
}
