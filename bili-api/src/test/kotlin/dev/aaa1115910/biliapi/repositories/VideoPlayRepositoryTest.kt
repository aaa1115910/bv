package dev.aaa1115910.biliapi.repositories

import com.google.rpc.Status
import dev.aaa1115910.biliapi.entity.ApiType
import dev.aaa1115910.biliapi.grpc.utils.getDetail
import io.ktor.http.ContentType.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Paths
import java.util.Properties
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class VideoPlayRepositoryTest {
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

    init {
        channelRepository.initDefaultChannel(ACCESS_TOKEN, BUVID)
        channelRepository.sessionData = SESSDATA
    }

    @Test
    fun `get flac video with grpc`() {
        runBlocking {
            val repository = VideoPlayRepository(channelRepository)
            runCatching {
                val result = repository.getPlayData(
                    aid = 993403941,
                    cid = 1051761130,
                    preferApiType = ApiType.GRPC
                )
                println(result)
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    @Test
    fun `get flac video with http`() {
        runBlocking {
            val repository = VideoPlayRepository(channelRepository)
            runCatching {
                val result = repository.getPlayData(
                    aid = 993403941,
                    cid = 1051761130,
                    preferApiType = ApiType.Http
                )
                println(result)
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    @Test
    fun `get 8k video with grpc`() {
        runBlocking {
            val repository = VideoPlayRepository(channelRepository)
            runCatching {
                val result = repository.getPlayData(
                    aid = 934637444,
                    cid = 455439756,
                    preferApiType = ApiType.GRPC
                )
                println(result)
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    @Test
    fun `get 8k video with http`() {
        runBlocking {
            val repository = VideoPlayRepository(channelRepository)
            runCatching {
                val result = repository.getPlayData(
                    aid = 934637444,
                    cid = 455439756,
                    preferApiType = ApiType.Http
                )
                println(result)
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    @Test
    fun `parse error status`() {
        val errorBin =
            "CAISBC00MDQaRAondHlwZS5nb29nbGVhcGlzLmNvbS9iaWxpYmlsaS5ycGMuU3RhdHVzEhkI7Pz/////////ARIM5ZWl6YO95pyo5pyJ"
        val errorData = Base64.decode(errorBin)
        val status = Status.parseFrom(errorData).getDetail()
        println(status)
    }

    @Test
    fun `get pgc video with grpc`() {
        runBlocking {
            val repository = VideoPlayRepository(channelRepository)
            runCatching {
                val result = repository.getPgcPlayData(
                    aid = 210680503,
                    cid = 486114279,
                    epid = 469110,
                    preferApiType = ApiType.GRPC
                )
                println(result)
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    @Test
    fun `get pgc video with http`() {
        runBlocking {
            val repository = VideoPlayRepository(channelRepository)
            runCatching {
                val result = repository.getPgcPlayData(
                    aid = 210680503,
                    cid = 486114279,
                    epid = 469110,
                    preferApiType = ApiType.Http
                )
                println(result)
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    @Test
    fun `get paid pgc video with grpc`() {
        runBlocking {
            val repository = VideoPlayRepository(channelRepository)
            runCatching {
                val result = repository.getPgcPlayData(
                    aid = 741219885,
                    cid = 1132332811,
                    epid = 750015,
                    preferApiType = ApiType.GRPC
                )
                println(result)
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    @Test
    fun `get paid pgc video with http`() {
        runBlocking {
            val repository = VideoPlayRepository(channelRepository)
            runCatching {
                val result = repository.getPgcPlayData(
                    aid = 741219885,
                    cid = 1132332811,
                    epid = 750015,
                    preferApiType = ApiType.Http
                )
                println(result)
            }.onFailure {
                it.printStackTrace()
            }
        }
    }
}

