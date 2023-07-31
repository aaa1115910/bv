package dev.aaa1115910.biliapi.repositories

import dev.aaa1115910.biliapi.entity.ApiType
import dev.aaa1115910.biliapi.entity.season.FollowingSeasonType
import dev.aaa1115910.biliapi.entity.season.TimelineFilter
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Paths
import java.util.Properties

class SeasonRepositoryTest {

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
    private val seasonRepository = SeasonRepository(authRepository)

    init {
        channelRepository.initDefaultChannel(
            FavoriteRepositoryTest.ACCESS_TOKEN,
            FavoriteRepositoryTest.BUVID
        )

        authRepository.sessionData = SESSDATA
        authRepository.accessToken = ACCESS_TOKEN
        authRepository.biliJct = BILI_JCT
        authRepository.mid = UID
    }

    @Test
    fun `get following seasons with web api`() = runBlocking {
        val bangumiResult = seasonRepository.getFollowingSeasons(
            type = FollowingSeasonType.Bangumi,
            preferApiType = ApiType.Web
        )
        val cinemaResult = seasonRepository.getFollowingSeasons(
            type = FollowingSeasonType.Cinema,
            preferApiType = ApiType.Web
        )
        println("bangumiResult: $bangumiResult")
        println("cinemaResult: $cinemaResult")
    }

    @Test
    fun `get following seasons with app api`() = runBlocking {
        val bangumiResult = seasonRepository.getFollowingSeasons(
            type = FollowingSeasonType.Bangumi,
            preferApiType = ApiType.App
        )
        val cinemaResult = seasonRepository.getFollowingSeasons(
            type = FollowingSeasonType.Cinema,
            preferApiType = ApiType.App
        )
        println("bangumiResult: $bangumiResult")
        println("cinemaResult: $cinemaResult")
    }

    @Test
    fun `get timeline with web api`() = runBlocking {
        TimelineFilter.webFilters.forEach { filter ->
            val result = seasonRepository.getTimeline(
                filter = filter,
                preferApiType = ApiType.Web
            )
            println("filter: $filter, result: $result")
        }
    }

    @Test
    fun `get timeline with app api`() = runBlocking {
        TimelineFilter.appFilters.forEach { filter ->
            val result = seasonRepository.getTimeline(
                filter = filter,
                preferApiType = ApiType.App
            )
            println("filter: $filter, result: $result")
        }
    }
}