package dev.aaa1115910.biliapi.repositories

import dev.aaa1115910.biliapi.entity.ApiType
import dev.aaa1115910.biliapi.entity.user.SpaceVideoOrder
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Paths
import java.util.Properties

class UserRepositoryTest {
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
    private val userRepository = UserRepository(authRepository, channelRepository)

    init {
        channelRepository.initDefaultChannel(
            FavoriteRepositoryTest.ACCESS_TOKEN,
            FavoriteRepositoryTest.BUVID
        )

        authRepository.sessionData = FavoriteRepositoryTest.SESSDATA
        authRepository.accessToken = FavoriteRepositoryTest.ACCESS_TOKEN
        authRepository.biliJct = FavoriteRepositoryTest.BILI_JCT
    }

    @Test
    fun `get user space videos with web api`() = runBlocking {
        val result = userRepository.getSpaceVideos(
            mid = 2,
            order = SpaceVideoOrder.PubDate,
            pageNumber = 1,
            pageSize = 10,
            preferApiType = ApiType.Web
        )
        println(result)
    }

    @Test
    fun `get user space videos with app api`() = runBlocking {
        val result = userRepository.getSpaceVideos(
            mid = 2,
            order = SpaceVideoOrder.PubDate,
            pageNumber = 1,
            pageSize = 10,
            preferApiType = ApiType.App
        )
        println(result)
    }

    @Test
    fun `get dynamic videos with web api`() = runBlocking {
        val result = userRepository.getDynamicVideos(
            page = 1,
            offset = "",
            updateBaseline = "",
            preferApiType = ApiType.Web
        )
        println(result)
    }

    @Test
    fun `get dynamic videos with grpc api`() = runBlocking {
        val result = userRepository.getDynamicVideos(
            page = 1,
            offset = "",
            updateBaseline = "",
            preferApiType = ApiType.App
        )
        println(result)
    }

    @Test
    fun `get following users with web api`() = runBlocking {
        val result = userRepository.getFollowedUsers(
            mid = UID,
            preferApiType = ApiType.Web
        )
        println(result)
    }

    @Test
    fun `get following users with app api`() = runBlocking {
        val result = userRepository.getFollowedUsers(
            mid = UID,
            preferApiType = ApiType.App
        )
        println(result)
    }
}