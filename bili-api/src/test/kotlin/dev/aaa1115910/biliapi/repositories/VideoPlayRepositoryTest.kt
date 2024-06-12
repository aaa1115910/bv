package dev.aaa1115910.biliapi.repositories

import bilibili.rpc.Status
//import com.google.rpc.Status
import dev.aaa1115910.biliapi.entity.ApiType
import dev.aaa1115910.biliapi.entity.video.HeartbeatVideoType
import dev.aaa1115910.biliapi.grpc.utils.getDetail
import dev.aaa1115910.biliapi.http.BiliHttpProxyApi
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.io.File
import java.net.URL
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
        val HTTP_PROXY_SERVER: String =
            runCatching { localProperties.getProperty("test.http_proxy_server") }.getOrNull() ?: ""
        val GRPC_PROXY_SERVER: String =
            runCatching { localProperties.getProperty("test.grpc_proxy_server") }.getOrNull() ?: ""
    }

    private val authRepository = AuthRepository()
    private val channelRepository = ChannelRepository()
    private val videoPlayRepository = VideoPlayRepository(authRepository, channelRepository)

    init {
        channelRepository.initDefaultChannel(ACCESS_TOKEN, BUVID)
        channelRepository.initProxyChannel(ACCESS_TOKEN, BUVID, GRPC_PROXY_SERVER)
        BiliHttpProxyApi.createClient(HTTP_PROXY_SERVER)
        authRepository.sessionData = SESSDATA
        authRepository.accessToken = ACCESS_TOKEN
        authRepository.biliJct = BILI_JCT
    }

    @Test
    fun `get flac video with grpc`() {
        runBlocking {
            runCatching {
                val result = videoPlayRepository.getPlayData(
                    aid = 993403941,
                    cid = 1051761130,
                    preferApiType = ApiType.App
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
            runCatching {
                val result = videoPlayRepository.getPlayData(
                    aid = 993403941,
                    cid = 1051761130,
                    preferApiType = ApiType.Web
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
            runCatching {
                val result = videoPlayRepository.getPlayData(
                    aid = 934637444,
                    cid = 455439756,
                    preferApiType = ApiType.App
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
            runCatching {
                val result = videoPlayRepository.getPlayData(
                    aid = 934637444,
                    cid = 455439756,
                    preferApiType = ApiType.Web
                )
                println(result)
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    @Test
    fun `get multi part video with http`() = runBlocking {
        val result = videoPlayRepository.getPlayData(
            aid = 836207,
            cid = 1215693,
            preferApiType = ApiType.Web
        )
        println(result)
    }

    @Test
    fun `get multi part video with grpc`() = runBlocking {
        val result = videoPlayRepository.getPlayData(
            aid = 836207,
            cid = 1215693,
            preferApiType = ApiType.App
        )
        println(result)
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
            runCatching {
                val result = videoPlayRepository.getPgcPlayData(
                    aid = 210680503,
                    cid = 486114279,
                    epid = 469110,
                    preferApiType = ApiType.App
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
            runCatching {
                val result = videoPlayRepository.getPgcPlayData(
                    aid = 210680503,
                    cid = 486114279,
                    epid = 469110,
                    preferApiType = ApiType.Web
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
            runCatching {
                val result = videoPlayRepository.getPgcPlayData(
                    aid = 741219885,
                    cid = 1132332811,
                    epid = 750015,
                    preferApiType = ApiType.App
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
            runCatching {
                val result = videoPlayRepository.getPgcPlayData(
                    aid = 741219885,
                    cid = 1132332811,
                    epid = 750015,
                    preferApiType = ApiType.Web
                )
                println(result)
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    @Test
    fun `get subtitle with web api`() = runBlocking {
        val result = videoPlayRepository.getSubtitle(
            aid = 913498989,
            cid = 1203020250,
            preferApiType = ApiType.Web
        )
        println(result)
    }

    @Test
    fun `get subtitle with app api`() = runBlocking {
        val result = videoPlayRepository.getSubtitle(
            aid = 913498989,
            cid = 1203020250,
            preferApiType = ApiType.App
        )
        println(result)
    }

    @Test
    fun `send heartbeat with web api`() = runBlocking {
        val randomTime = (0..100).random()
        println("random time: $randomTime")
        videoPlayRepository.sendHeartbeat(
            aid = 170001,
            cid = 280468,
            time = randomTime,
            preferApiType = ApiType.Web
        )
        videoPlayRepository.sendHeartbeat(
            aid = 476982015,
            cid = 1107179650,
            type = HeartbeatVideoType.Season,
            subType = 4,
            time = randomTime,
            epid = 706666,
            seasonId = 39707,
            preferApiType = ApiType.Web
        )
    }

    @Test
    fun `send heartbeat with app api`() = runBlocking {
        val randomTime = (0..100).random()
        println("random time: $randomTime")
        videoPlayRepository.sendHeartbeat(
            aid = 170001,
            cid = 280468,
            time = randomTime,
            preferApiType = ApiType.App
        )
        videoPlayRepository.sendHeartbeat(
            aid = 476982015,
            cid = 1107179650,
            type = HeartbeatVideoType.Season,
            subType = 4,
            time = randomTime,
            epid = 706666,
            seasonId = 39707,
            preferApiType = ApiType.App
        )
    }

    @Test
    fun `get region limited pgc play data`() = runBlocking {
        val cid = 1199794768L
        val epid = 763396
        val enableProxy = true
        val proxyArea = "hk"
        val webResult = videoPlayRepository.getPgcPlayData(
            aid = 0,
            cid = cid,
            epid = epid,
            preferApiType = ApiType.Web,
            enableProxy = enableProxy,
            proxyArea = proxyArea
        )
        val appResult = videoPlayRepository.getPgcPlayData(
            aid = 0,
            cid = cid,
            epid = epid,
            preferApiType = ApiType.App,
            enableProxy = enableProxy,
            proxyArea = proxyArea
        )
        println("web result: $webResult")
        println("app result: $appResult")
    }

    @Test
    fun `get play url domain`() = runBlocking {
        val getUrlDomain: (String) -> String = {
            val url = URL(it)
            "${url.protocol}://${url.host}"
        }
        ApiType.entries.forEach { apiType ->
            val result = videoPlayRepository.getPlayData(
                aid = 934637444,
                cid = 455439756,
                preferApiType = apiType
            )
            println("api type: $apiType")

            result.dashVideos.forEach { video ->
                println("video quality: ${video.quality}")
                val videoUrls = mutableListOf<String>()
                videoUrls.add(video.baseUrl)
                videoUrls.addAll(video.backUrl)
                videoUrls.forEach { println(getUrlDomain(it)) }
            }
        }
    }

    @Test
    fun `get video shots`() = runBlocking {
        ApiType.entries.forEach { apiType ->
            val result = videoPlayRepository.getVideoShot(
                aid = 170001,
                cid = 279786,
                preferApiType = apiType
            )
            println("api type: $apiType")
            println(result)
        }
    }
}

