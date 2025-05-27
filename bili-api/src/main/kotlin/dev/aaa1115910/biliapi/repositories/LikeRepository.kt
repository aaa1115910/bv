package dev.aaa1115910.biliapi.repositories

import dev.aaa1115910.biliapi.entity.ApiType
import dev.aaa1115910.biliapi.http.BiliHttpApi

class LikeRepository(
    private val authRepository: AuthRepository
) {
    suspend fun checkVideoLike(
        aid: Long,
        preferApiType: ApiType = ApiType.Web
    ): Boolean {
        return when (preferApiType) {
            ApiType.Web -> BiliHttpApi.checkVideoLiked(
                avid = aid,
                sessData = authRepository.sessionData
            )

            ApiType.App -> BiliHttpApi.checkVideoLiked(
                avid = aid,
                accessKey = authRepository.accessToken
            )
        }
    }

    suspend fun addVideoLike(
        aid: Long,
        preferApiType: ApiType = ApiType.Web
    ) {
        when (preferApiType) {
            ApiType.Web -> BiliHttpApi.sendVideoLike(
                avid = aid,
                like = true,
                sessData = authRepository.sessionData,
                csrf = authRepository.biliJct
            )

            ApiType.App -> BiliHttpApi.sendVideoLike(
                avid = aid,
                like = true,
                accessKey = authRepository.accessToken
            )
        }
    }

    suspend fun delVideoLike(
        aid: Long,
        preferApiType: ApiType = ApiType.Web
    ) {
        // 根据preferApiType参数选择不同的API接口，只会执行一个分支，不会发起两次请求
        when (preferApiType) {
            ApiType.Web -> BiliHttpApi.sendVideoLike(
                avid = aid,
                like = false,
                sessData = authRepository.sessionData,
                csrf = authRepository.biliJct
            )

            ApiType.App -> BiliHttpApi.sendVideoLike(
                avid = aid,
                like = false,
                accessKey = authRepository.accessToken
            )
        }
    }
}
