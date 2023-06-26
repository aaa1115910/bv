package dev.aaa1115910.biliapi.repositories

import dev.aaa1115910.biliapi.entity.ApiType
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Paths
import java.util.Properties

class FavoriteRepositoryTest {
    companion object {
        private val localProperties = Properties().apply {
            val path = Paths.get("../local.properties").toAbsolutePath().toString()
            load(File(path).bufferedReader())
        }
        val SESSDATA: String =
            runCatching { localProperties.getProperty("test.sessdata") }.getOrNull() ?: ""
        val BILI_JCT: String =
            runCatching { localProperties.getProperty("test.bili_jct") }.getOrNull() ?: ""
        val UID: Long =
            runCatching { localProperties.getProperty("test.uid") }.getOrNull()?.toLongOrNull() ?: 2
        val ACCESS_TOKEN: String =
            runCatching { localProperties.getProperty("test.access_token") }.getOrNull() ?: ""
        val BUVID: String =
            runCatching { localProperties.getProperty("test.buvid") }.getOrNull() ?: ""
    }

    private val authRepository = AuthRepository()
    private val channelRepository = ChannelRepository()
    private val favoriteRepository = FavoriteRepository(authRepository)

    init {
        channelRepository.initDefaultChannel(ACCESS_TOKEN, BUVID)

        authRepository.sessionData = SESSDATA
        authRepository.accessToken = ACCESS_TOKEN
        authRepository.biliJct = BILI_JCT
    }

    @Test
    fun `check video is favoured with cookies`() = runBlocking {
        val result = favoriteRepository.checkVideoFavoured(
            aid = 170001,
            preferApiType = ApiType.Web
        )
        println(result)
    }

    @Test
    fun `check video is favoured with token`() = runBlocking {
        val result = favoriteRepository.checkVideoFavoured(
            aid = 170001,
            preferApiType = ApiType.Web
        )
        println(result)
    }

    @Test
    fun `add video to favorite folder with cookies`() = runBlocking {
        val defaultMediaId = getDefaultFavoriteFolderId(ApiType.Web)
        favoriteRepository.addVideoToFavoriteFolder(
            aid = 170001,
            addMediaIds = listOf(defaultMediaId),
            preferApiType = ApiType.Web
        )
    }

    @Test
    fun `add video to favorite folder with token`() = runBlocking {
        val defaultMediaId = getDefaultFavoriteFolderId(ApiType.App)
        favoriteRepository.addVideoToFavoriteFolder(
            aid = 170001,
            addMediaIds = listOf(defaultMediaId),
            preferApiType = ApiType.App
        )
    }

    @Test
    fun `del video from favorite folder with cookies`() = runBlocking {
        val defaultMediaId = getDefaultFavoriteFolderId(ApiType.Web)
        favoriteRepository.delVideoFromFavoriteFolder(
            aid = 170001,
            delMediaIds = listOf(defaultMediaId),
            preferApiType = ApiType.Web
        )
    }

    @Test
    fun `del video from favorite folder with token`() = runBlocking {
        val defaultMediaId = getDefaultFavoriteFolderId(ApiType.App)
        favoriteRepository.delVideoFromFavoriteFolder(
            aid = 170001,
            delMediaIds = listOf(defaultMediaId),
            preferApiType = ApiType.App
        )
    }

    @Test
    fun `update video to favorite folder with cookies`() = runBlocking {
        val defaultMediaId = getDefaultFavoriteFolderId(ApiType.App)
        favoriteRepository.updateVideoToFavoriteFolder(
            aid = 170001,
            addMediaIds = listOf(defaultMediaId),
            delMediaIds = listOf(),
            preferApiType = ApiType.Web
        )
    }

    @Test
    fun `update video to favorite folder with token`() = runBlocking {
        val defaultMediaId = getDefaultFavoriteFolderId(ApiType.App)
        favoriteRepository.updateVideoToFavoriteFolder(
            aid = 170001,
            addMediaIds = listOf(defaultMediaId),
            delMediaIds = listOf(),
            preferApiType = ApiType.App
        )
    }

    @Test
    fun `get all favorite folders metadata with cookies`() = runBlocking {
        val result = favoriteRepository.getAllFavoriteFolderMetadataList(
            mid = UID,
            rid = 170001,
            preferApiType = ApiType.Web
        )
        println(result)
    }

    @Test
    fun `get all favorite folders metadata with token`() = runBlocking {
        val result = favoriteRepository.getAllFavoriteFolderMetadataList(
            mid = UID,
            rid = 170001,
            preferApiType = ApiType.App
        )
        println(result)
    }

    @Test
    fun `get favorite folder data with cookies`() = runBlocking {
        val defaultMediaId = getDefaultFavoriteFolderId(ApiType.Web)
        val result = favoriteRepository.getFavoriteFolderData(
            mediaId = defaultMediaId,
            pageSize = 20,
            pageNumber = 1,
            preferApiType = ApiType.Web
        )
        println(result)
    }

    @Test
    fun `get favorite folder data with token`() = runBlocking {
        val defaultMediaId = getDefaultFavoriteFolderId(ApiType.App)
        val result = favoriteRepository.getFavoriteFolderData(
            mediaId = defaultMediaId,
            pageSize = 20,
            pageNumber = 1,
            preferApiType = ApiType.App
        )
        println(result)
    }

    private suspend fun getDefaultFavoriteFolderId(preferApiType: ApiType): Long {
        val foldersInfoResult = favoriteRepository.getAllFavoriteFolderMetadataList(
            mid = UID,
            preferApiType = preferApiType
        )
        val id = foldersInfoResult.find { it.title == "默认收藏夹" }?.id ?: 0
        println("default media id: $id")
        return id
    }
}