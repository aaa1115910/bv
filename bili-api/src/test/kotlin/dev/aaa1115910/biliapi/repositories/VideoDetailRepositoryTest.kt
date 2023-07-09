package dev.aaa1115910.biliapi.repositories

import dev.aaa1115910.biliapi.entity.ApiType
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Paths
import java.util.Properties

class VideoDetailRepositoryTest {
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

    private val channelRepository = ChannelRepository()
    private val authRepository = AuthRepository()
    private val favoriteRepository = FavoriteRepository(authRepository)
    private val videoDetailRepository =
        VideoDetailRepository(authRepository, channelRepository, favoriteRepository)

    init {
        channelRepository.initDefaultChannel(
            VideoPlayRepositoryTest.ACCESS_TOKEN,
            VideoPlayRepositoryTest.BUVID
        )
        authRepository.sessionData = VideoPlayRepositoryTest.SESSDATA
    }

    @Test
    fun `get video info with http`() {
        runBlocking {
            runCatching {
                val result = videoDetailRepository.getVideoDetail(
                    aid = 170001,
                    preferApiType = ApiType.Web
                )
                println(result)
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    @Test
    fun `get video info with grpc`() {
        runBlocking {
            runCatching {
                val result = videoDetailRepository.getVideoDetail(
                    aid = 170001,
                    preferApiType = ApiType.App
                )
                println(result)
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    @Test
    fun `get ugc season video info with http`() {
        runBlocking {
            runCatching {
                val result = videoDetailRepository.getVideoDetail(
                    aid = 954251211,
                    preferApiType = ApiType.Web
                )
                println(result)
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    @Test
    fun `get ugc season video info with grpc`() {
        runBlocking {
            runCatching {
                val result = videoDetailRepository.getVideoDetail(
                    aid = 954251211,
                    preferApiType = ApiType.App
                )
                println(result)
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    @Test
    fun `get anime video info with http`() {
        runBlocking {
            runCatching {
                val result = videoDetailRepository.getVideoDetail(
                    aid = 314583081,
                    preferApiType = ApiType.Web
                )
                println(result)
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    @Test
    fun `get anime video info with grpc`() {
        runBlocking {
            runCatching {
                val result = videoDetailRepository.getVideoDetail(
                    aid = 314583081,
                    preferApiType = ApiType.App
                )
                println(result)
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    @Test
    fun `get argue video info with http`() {
        runBlocking {
            runCatching {
                val result = videoDetailRepository.getVideoDetail(
                    aid = 996965888,
                    preferApiType = ApiType.Web
                )
                println(result)
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    @Test
    fun `get argue video info with grpc`() {
        runBlocking {
            runCatching {
                val result = videoDetailRepository.getVideoDetail(
                    aid = 996965888,
                    preferApiType = ApiType.App
                )
                println(result)
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    @Test
    fun `get pgc season video prefer web api`() {
        runBlocking {
            runCatching {
                val result = videoDetailRepository.getPgcVideoDetail(
                    epid = 752900,
                    preferApiType = ApiType.Web
                )
                println(result)
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    @Test
    fun `get pgc season video prefer app api`() {
        runBlocking {
            runCatching {
                val result = videoDetailRepository.getPgcVideoDetail(
                    epid = 752900,
                    preferApiType = ApiType.App
                )
                println(result)
            }.onFailure {
                it.printStackTrace()
            }
        }
    }
}