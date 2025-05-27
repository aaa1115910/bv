package dev.aaa1115910.biliapi.repositories

import dev.aaa1115910.biliapi.entity.ApiType
import dev.aaa1115910.biliapi.http.BiliHttpApi

class CoinRepository(
    private val authRepository: AuthRepository
) {
    suspend fun checkVideoCoin(
        aid: Long,
        preferApiType: ApiType = ApiType.Web
    ): Boolean {
        return when (preferApiType) {
            ApiType.Web -> BiliHttpApi.checkVideoSentCoin(
                avid = aid,
                sessData = authRepository.sessionData
            )

            ApiType.App -> BiliHttpApi.checkVideoSentCoin(
                avid = aid,
                accessKey = authRepository.accessToken
            )
        }
    }

    suspend fun addVideoCoin(
        aid: Long,
        preferApiType: ApiType = ApiType.Web
    ) {
        when (preferApiType) {
            ApiType.Web -> BiliHttpApi.sendVideoCoin(
                avid = aid,
                like = false,
                csrf = authRepository.biliJct?: "",
                sessData = authRepository.sessionData!!,
                buvid3 = authRepository.buvid3!!,
            )

            ApiType.App -> BiliHttpApi.sendVideoCoin(
                avid = aid,
                like = false,
                accessKey = authRepository.accessToken
            )
        }
    }
}
