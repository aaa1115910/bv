package dev.aaa1115910.biliapi.repositories

import dev.aaa1115910.biliapi.entity.ApiType
import dev.aaa1115910.biliapi.entity.FavoriteFolderData
import dev.aaa1115910.biliapi.entity.FavoriteFolderMetadata
import dev.aaa1115910.biliapi.entity.FavoriteItemType
import dev.aaa1115910.biliapi.http.BiliHttpApi

class FavoriteRepository(
    private val authRepository: AuthRepository
) {
    suspend fun checkVideoFavoured(
        aid: Int,
        preferApiType: ApiType = ApiType.Web
    ): Boolean {
        return when (preferApiType) {
            ApiType.Web -> BiliHttpApi.checkVideoFavoured(
                avid = aid,
                sessData = authRepository.sessionData ?: ""
            )

            ApiType.App -> BiliHttpApi.checkVideoFavoured(
                avid = aid,
                accessKey = authRepository.accessToken ?: ""
            )
        }
    }

    suspend fun addVideoToFavoriteFolder(
        aid: Int,
        addMediaIds: List<Long>,
        preferApiType: ApiType = ApiType.Web
    ) {
        when (preferApiType) {
            ApiType.Web -> BiliHttpApi.setVideoToFavorite(
                avid = aid,
                type = FavoriteItemType.Video.value,
                addMediaIds = addMediaIds,
                sessData = authRepository.sessionData,
                csrf = authRepository.biliJct
            )

            ApiType.App -> BiliHttpApi.setVideoToFavorite(
                avid = aid,
                type = FavoriteItemType.Video.value,
                addMediaIds = addMediaIds,
                accessKey = authRepository.accessToken
            )
        }
    }

    suspend fun delVideoFromFavoriteFolder(
        aid: Int,
        delMediaIds: List<Long>,
        preferApiType: ApiType = ApiType.Web
    ) {
        when (preferApiType) {
            ApiType.Web -> BiliHttpApi.setVideoToFavorite(
                avid = aid,
                type = FavoriteItemType.Video.value,
                delMediaIds = delMediaIds,
                sessData = authRepository.sessionData,
                csrf = authRepository.biliJct
            )

            ApiType.App -> BiliHttpApi.setVideoToFavorite(
                avid = aid,
                type = FavoriteItemType.Video.value,
                delMediaIds = delMediaIds,
                accessKey = authRepository.accessToken
            )
        }
    }

    suspend fun updateVideoToFavoriteFolder(
        aid: Int,
        addMediaIds: List<Long>,
        delMediaIds: List<Long>,
        preferApiType: ApiType = ApiType.Web
    ) {
        when (preferApiType) {
            ApiType.Web -> BiliHttpApi.setVideoToFavorite(
                avid = aid,
                type = FavoriteItemType.Video.value,
                addMediaIds = addMediaIds,
                delMediaIds = delMediaIds,
                sessData = authRepository.sessionData,
                csrf = authRepository.biliJct
            )

            ApiType.App -> BiliHttpApi.setVideoToFavorite(
                avid = aid,
                type = FavoriteItemType.Video.value,
                addMediaIds = addMediaIds,
                delMediaIds = delMediaIds,
                accessKey = authRepository.accessToken
            )
        }
    }

    suspend fun getAllFavoriteFolderMetadataList(
        mid: Long,
        type: FavoriteItemType = FavoriteItemType.Video,
        rid: Int? = null,
        preferApiType: ApiType = ApiType.Web
    ): List<FavoriteFolderMetadata> {
        val userFavoriteFoldersData = when (preferApiType) {
            ApiType.Web -> BiliHttpApi.getAllFavoriteFoldersInfo(
                mid = mid,
                type = type.value,
                rid = rid,
                sessData = authRepository.sessionData ?: ""
            )

            ApiType.App -> BiliHttpApi.getAllFavoriteFoldersInfo(
                mid = mid,
                type = type.value,
                rid = rid,
                accessKey = authRepository.accessToken ?: ""
            )
        }.getResponseData()
        return userFavoriteFoldersData.list.map {
            FavoriteFolderMetadata.fromHttpUserFavoriteFolder(it)
        }
    }

    suspend fun getFavoriteFolderData(
        mediaId: Long,
        pageSize: Int = 20,
        pageNumber: Int = 1,
        preferApiType: ApiType = ApiType.Web
    ): FavoriteFolderData {
        val favoriteFolderListData = when (preferApiType) {
            ApiType.Web -> BiliHttpApi.getFavoriteList(
                mediaId = mediaId,
                pageSize = pageSize,
                pageNumber = pageNumber,
                sessData = authRepository.sessionData ?: ""
            )

            ApiType.App -> BiliHttpApi.getFavoriteList(
                mediaId = mediaId,
                pageSize = pageSize,
                pageNumber = pageNumber,
                accessKey = authRepository.accessToken ?: ""
            )
        }.getResponseData()
        return FavoriteFolderData.fromHttpFavoriteFolderInfoListData(favoriteFolderListData)
    }
}
