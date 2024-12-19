package dev.aaa1115910.biliapi.repositories

import bilibili.app.interfaces.v1.HistoryGrpcKt
import bilibili.app.interfaces.v1.cursor
import bilibili.app.interfaces.v1.cursorV2Req
import dev.aaa1115910.biliapi.entity.ApiType
import dev.aaa1115910.biliapi.entity.user.ToViewData
import dev.aaa1115910.biliapi.http.BiliHttpApi

class ToViewRepository(
    private val authRepository: AuthRepository,
    private val channelRepository: ChannelRepository
) {
    private val historyStub
        get() = runCatching {
            HistoryGrpcKt.HistoryCoroutineStub(channelRepository.defaultChannel!!)
        }.getOrNull()

    suspend fun getToView(
        cursor: Long,
        preferApiType: ApiType = ApiType.Web
    ): ToViewData {
        return when (preferApiType) {
            ApiType.Web -> {
                val data = BiliHttpApi.getToView(
                    // viewAt = cursor,
                    sessData = authRepository.sessionData!!,
                ).getResponseData()
                print(data)
                ToViewData.fromToViewResponse(data)
            }

            ApiType.App -> {
                val data = BiliHttpApi.getToView(
                    // viewAt = cursor,
                    sessData = authRepository.sessionData!!,
                ).getResponseData()
                ToViewData.fromToViewResponse(data)
            }
        }
    }
}
