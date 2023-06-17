package dev.aaa1115910.biliapi.repositories

import bilibili.app.view.v1.ViewGrpcKt
import bilibili.app.view.v1.viewReq
import dev.aaa1115910.biliapi.entity.ApiType
import dev.aaa1115910.biliapi.entity.video.VideoDetail
import dev.aaa1115910.biliapi.grpc.utils.handleGrpcException
import dev.aaa1115910.biliapi.http.BiliHttpApi

class VideoDetailRepository(
    private val authRepository: AuthRepository,
    private val channelRepository: ChannelRepository
) {
    private val viewStub
        get() = runCatching {
            ViewGrpcKt.ViewCoroutineStub(channelRepository.defaultChannel!!)
        }.getOrNull()

    suspend fun getVideoDetail(
        aid: Int,
        preferApiType: ApiType = ApiType.Web
    ): VideoDetail {
        return when (preferApiType) {
            ApiType.Web -> {
                val videoDetail = BiliHttpApi.getVideoDetail(
                    av = aid,
                    sessData = authRepository.sessionData ?: ""
                ).getResponseData()
                VideoDetail.fromVideoDetail(videoDetail)
            }

            ApiType.App -> {
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
