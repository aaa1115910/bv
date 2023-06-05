package dev.aaa1115910.biliapi.repositories

import bilibili.app.playerunite.v1.PlayerGrpcKt
import bilibili.app.playerunite.v1.playViewUniteReq
import bilibili.pgc.gateway.player.v2.playViewReq
import bilibili.playershared.videoVod
import dev.aaa1115910.biliapi.entity.ApiType
import dev.aaa1115910.biliapi.entity.CodeType
import dev.aaa1115910.biliapi.entity.PlayData
import dev.aaa1115910.biliapi.grpc.utils.handleGrpcException
import dev.aaa1115910.biliapi.http.BiliHttpApi
import bilibili.pgc.gateway.player.v2.PlayURLGrpcKt as PgcPlayURLGrpcKt

class VideoPlayRepository(
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

    suspend fun getPlayData(
        aid: Int,
        cid: Int,
        preferCodec: CodeType = CodeType.NoCode,
        preferApiType: ApiType = ApiType.Http
    ): PlayData {
        return when (preferApiType) {
            ApiType.Http -> {
                val playUrlData = BiliHttpApi.getVideoPlayUrl(
                    av = aid,
                    cid = cid,
                    fnval = 4048,
                    qn = 127,
                    fnver = 0,
                    fourk = 1,
                    sessData = channelRepository.sessionData
                ).getResponseData()
                PlayData.fromPlayUrlData(playUrlData)
            }

            ApiType.GRPC -> {
                val playUniteReplay = runCatching {
                    playerStub?.playViewUnite(playViewUniteReq {
                        vod = videoVod {
                            this.aid = aid
                            this.cid = cid
                            fnval = 4048
                            qn = 127
                            fnver = 0
                            fourk = true
                            preferCodecType = preferCodec.toPlayerSharedCodeType()
                        }
                    }) ?: throw IllegalStateException("Player stub is not initialized")
                }.onFailure { handleGrpcException(it) }.getOrThrow()
                PlayData.fromPlayViewUniteReply(playUniteReplay)
            }
        }
    }

    suspend fun getPgcPlayData(
        aid: Int,
        cid: Int,
        epid: Int,
        preferCodec: CodeType = CodeType.NoCode,
        preferApiType: ApiType = ApiType.Http
    ): PlayData {
        return when (preferApiType) {
            ApiType.Http -> {
                val playUrlData = BiliHttpApi.getPgcVideoPlayUrl(
                    av = aid,
                    cid = cid,
                    fnval = 4048,
                    qn = 127,
                    fnver = 0,
                    fourk = 1,
                    sessData = channelRepository.sessionData
                ).getResponseData()
                PlayData.fromPlayUrlData(playUrlData)
            }

            ApiType.GRPC -> {
                val pgcPlayViewReply = runCatching {
                    pgcPlayUrlStub?.playView(playViewReq {
                        this.epid = epid.toLong()
                        this.cid = cid.toLong()
                        qn = 127
                        fnver = 0
                        fnval = 4048
                        fourk = true
                        forceHost = 0
                        download = 0
                        preferCodecType = preferCodec.toPgcPlayUrlCodeType()
                    }) ?: throw IllegalStateException("Pgc play url stub is not initialized")
                }.onFailure { handleGrpcException(it) }.getOrThrow()
                PlayData.fromPgcPlayViewReply(pgcPlayViewReply)
            }
        }
    }
}
