package dev.aaa1115910.bv.viewmodel.user

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.aaa1115910.biliapi.BiliApi
import dev.aaa1115910.biliapi.entity.user.favorite.UserFavoriteFoldersData
import dev.aaa1115910.bv.entity.carddata.VideoCardData
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.fWarn
import dev.aaa1115910.bv.util.swapList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mu.KotlinLogging

class FavoriteViewModel : ViewModel() {
    companion object {
        private val logger = KotlinLogging.logger { }
    }

    var favoriteFolders = mutableStateListOf<UserFavoriteFoldersData.UserFavoriteFolder>()
    var favorites = mutableStateListOf<VideoCardData>()

    var currentFavoriteFolder: UserFavoriteFoldersData.UserFavoriteFolder? by mutableStateOf(null)

    private var pageSize = 20
    private var pageNumber = 1
    private var hasMore = true

    private var updatingFolders = false
    private var updatingFolderItems = false

    init {
        updateFoldersInfo()
    }

    private fun updateFoldersInfo() {
        if (updatingFolders) return
        updatingFolders = true
        logger.fInfo { "Updating favorite folders" }
        viewModelScope.launch(Dispatchers.Default) {
            runCatching {
                val responseData = BiliApi.getAllFavoriteFoldersInfo(
                    mid = Prefs.uid,
                    sessData = Prefs.sessData
                ).getResponseData()
                favoriteFolders.swapList(responseData.list)
                currentFavoriteFolder = responseData.list.first()
                logger.fInfo { "Update favorite folders success: ${favoriteFolders.map { it.id }}" }
            }.onFailure {
                logger.fWarn { "Update favorite folders failed: ${it.stackTraceToString()}" }
                //这里返回的数据并不会有用户认证失败的错误返回，没必要做身份验证失败提示
            }.onSuccess {
                updateFolderItems()
            }
            updatingFolders = false
        }
    }

    fun updateFolderItems() {
        if (updatingFolderItems || !hasMore) return
        updatingFolderItems = true
        logger.fInfo { "Updating favorite folder items with media id: ${currentFavoriteFolder?.id}" }
        viewModelScope.launch(Dispatchers.Default) {
            runCatching {
                val responseData = BiliApi.getFavoriteList(
                    mediaId = currentFavoriteFolder!!.id,
                    pageSize = pageSize,
                    pageNumber = pageNumber,
                    sessData = Prefs.sessData
                ).getResponseData()
                responseData.medias.forEach { favoriteItem ->
                    if (favoriteItem.type != 2) return@forEach
                    favorites.add(
                        VideoCardData(
                            avid = favoriteItem.id.toInt(),
                            title = favoriteItem.title,
                            cover = favoriteItem.cover,
                            upName = favoriteItem.upper.name,
                            time = favoriteItem.duration * 1000L
                        )
                    )
                }
                hasMore = responseData.hasMore
                logger.fInfo { "Update favorite items success" }
            }.onFailure {
                logger.fInfo { "Update favorite items failed: ${it.stackTraceToString()}" }
            }.onSuccess {
                pageNumber++
            }
            updatingFolderItems = false
        }
    }

    fun resetPageNumber() {
        pageNumber = 1
        hasMore = true
    }
}