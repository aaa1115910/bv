package dev.aaa1115910.biliapi.repositories

import bilibili.app.view.v1.ViewGrpcKt
import bilibili.app.view.v1.viewReq
import dev.aaa1115910.biliapi.entity.ApiType
import dev.aaa1115910.biliapi.entity.video.RelatedVideo
import dev.aaa1115910.biliapi.entity.video.VideoDetail
import dev.aaa1115910.biliapi.grpc.utils.handleGrpcException
import dev.aaa1115910.biliapi.http.BiliHttpApi

class VideoDetailRepository(
    private val channelRepository: ChannelRepository
) {
    private val viewStub
        get() = runCatching {
            ViewGrpcKt.ViewCoroutineStub(channelRepository.defaultChannel!!)
        }.getOrNull()

    suspend fun getVideoDetail(
        aid: Int,
        preferApiType: ApiType = ApiType.Http
    ): VideoDetail {
        return when (preferApiType) {
            ApiType.Http -> {
                val videoInfoResponse = BiliHttpApi.getVideoInfo(
                    av = aid,
                    sessData = channelRepository.sessionData
                ).getResponseData()
                val relatedVideos = runCatching {
                    BiliHttpApi.getRelatedVideos(avid = aid.toLong()).data
                }.onFailure { it.printStackTrace() }
                    .getOrNull() ?: emptyList()
                VideoDetail.fromVideoInfo(videoInfoResponse).apply {
                    this.relatedVideos.addAll(relatedVideos.map { RelatedVideo.fromRelate(it) })
                }
            }

            ApiType.GRPC -> {
                val viewReply = runCatching {
                    viewStub?.view(viewReq {
                        this.aid = aid.toLong()
                    }) ?: throw IllegalStateException("Player stub is not initialized")
                }.onFailure { handleGrpcException(it) }.getOrThrow()
                VideoDetail.fromViewReply(viewReply)
            }
        }
    }
}
