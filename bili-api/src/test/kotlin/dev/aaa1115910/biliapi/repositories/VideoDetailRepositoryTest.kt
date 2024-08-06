package dev.aaa1115910.biliapi.repositories

import dev.aaa1115910.biliapi.entity.ApiType
import dev.aaa1115910.biliapi.entity.reply.CommentPage
import dev.aaa1115910.biliapi.entity.reply.CommentReplyPage
import dev.aaa1115910.biliapi.entity.reply.CommentSort
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Paths
import java.util.Base64
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
    fun `get multi part video info with http`() = runBlocking {
        val result = videoDetailRepository.getVideoDetail(
            aid = 836207,
            preferApiType = ApiType.Web
        )
        println(result)
    }

    @Test
    fun `get multi part video info with grpc`() = runBlocking {
        val result = videoDetailRepository.getVideoDetail(
            aid = 836207,
            preferApiType = ApiType.App
        )
        println(result)
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

    @Test
    fun `get video comments`() {
        runBlocking {
            runCatching {
                //val aid = 234771846
                val aid = 491985062L
                val sort = CommentSort.Time
                /*val page = CommentPage(
                    nextWebPage = """{"type":1,"direction":1,"Data":{"cursor":90}}""",
                    nextAppPage = 90
                )*/
                val page = CommentPage()
                val webResult = videoDetailRepository.getComments(
                    aid = aid,
                    sort = sort,
                    page = page,
                    preferApiType = ApiType.Web
                )
                val appResult = videoDetailRepository.getComments(
                    aid = aid,
                    sort = sort,
                    page = page,
                    preferApiType = ApiType.App
                )
                println(webResult)
                println(appResult)
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    @Test
    fun `get all comments with web api`() = `get all comments`(ApiType.Web)

    @Test
    fun `get all comments with app api`() = `get all comments`(ApiType.App)

    private fun `get all comments`(apiType: ApiType) {
        runBlocking {
            runCatching {
                var page = CommentPage()
                //val aid = 234771846
                val aid = 832519956L
                val sort = CommentSort.Hot
                var hasNext = true
                var index = 1

                while (hasNext) {
                    val data = videoDetailRepository.getComments(
                        aid = aid,
                        sort = sort,
                        page = page,
                        preferApiType = apiType
                    )
                    page = data.nextPage
                    data.comments.forEach {
                        println("${index++}\t${it.content.joinToString(separator = "")}")
                    }
                    hasNext = data.hasNext
                    delay(3000)
                }
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    @Test
    fun `get video comment reply`() {
        runBlocking {
            runCatching {
                val aid = 499133313L
                val root = 3323576644
                val page = CommentReplyPage()
                val webResult = videoDetailRepository.getCommentReplies(
                    aid = aid,
                    commentId = root,
                    page = page,
                    preferApiType = ApiType.Web
                )
                val appResult = videoDetailRepository.getCommentReplies(
                    aid = aid,
                    commentId = root,
                    page = page,
                    preferApiType = ApiType.App
                )
                println(webResult)
                println(appResult)
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    @Test
    fun `get all replies with web api`() = `get all replies`(ApiType.Web)

    @Test
    fun `get all replies with app api`() = `get all replies`(ApiType.App)

    private fun `get all replies`(apiType: ApiType) {
        runBlocking {
            runCatching {
                var page = CommentReplyPage()
                val aid = 234771846L
                val rpid = 190458721840
                //val aid = 619910840
                //val rpid = 191513725712
                var hasNext = true
                var index = 1

                while (hasNext) {
                    val data = videoDetailRepository.getCommentReplies(
                        aid = aid,
                        commentId = rpid,
                        page = page,
                        sort = CommentSort.Time,
                        preferApiType = apiType
                    )
                    page = data.nextPage
                    data.replies.forEach {
                        println("${index++}\t${it.content.joinToString(separator = "")}")
                    }
                    hasNext = data.hasNext
                    delay(3000)
                }
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    @Test
    fun foo() {
        val base64 =
            "AQAAA4IfiwgAAAAAAAAANVRNiiVEDHYQRFx5hrcUp0mlKqnEXVJJEJdzARnsFhqmp4e2FURcibj3BC7nIJ7BG7jwFuYJwuO9R/0kX76f+vjP33/768WnLy5f/XT57t3D/e3li8v3b++f725v3r15/ePd08sf7m/vHl/e3j2/vn9zAzdw+fzy7dPjw9f/n35+uHm6++bx4eHu7e1/+z9/9scnKVzrVI2SMlGwTYMo04xGsuUq0/BkrJMbZAgHnKSsuV1YlzBCiNsI1cFCPlAkMSNBDwdhycGYM0hlY1qO9L05xqg9COfJYYVjYIgpgasU27AlJUv8sOxhsvTMjLEmSC1es5eIkTayKeoVNpfBGVyZeNLnRFBLrpFqVJsqNiOe7SlzUp5MjYWOx1d64D59EudABCgzdoqTSzQXLpk1hguacAIcG9NRCAEnMU8pWj5qEQ52RVieizbobDSIsmJJ8zkUa1/5sp2h4yhRcWNYOYOPEPnqiVdoZXOS68zmNHmdmAB2JWCjKoj6HsGNW/PQ3rgXXUdxPjAW5DwqHus0VJj9LwFHbJugPrrpbI0BRFarIAJNUw3vzV4j5MZT3nI1V6zUK3PHiv7VQyjjhB3cMBlltrS2xlgl6d53Ynmx1NRsao5nLj99mCAqDHcUYiF7FZxpsJupPGptOApeCksjGvYagFdf9LWpdViVaR9DmNZfqHvvLgwqzLhiIs9VM52GHE4+bD7gFMFUXnhEhOooxtg9hUBpgPeHvIxCT5fdaDSdRNsSu+fpusbG2si3WFtyiI6ME73haUCxdel0Z9/eorYE4bD5hPb5bKdWe9od+mqjBFmmNkhO0OjoMBB0iEi3r3YchLd/DmBOtWYvRyRGsykalhHdADZFj5ktvE3maL2Cx9VuSGSMDaD9uvp4UesNp4NL2fk0VPeNPtp8OkI6nO3ESluZbQFMxaySuXWygmOAdLhbAmiV+j7LwDWy1eqhTtdTbpSddvAmb9go81PQL4Tu9m62UwzaDXa1+6EOfL8HTHiNklQL0JPIbhuEi16jzZuXJfncNU5xswK7dk+OBoMtRjcy593d+wHawV1ksUYnQHyuBu9Yjaa8rSO2O/zsPXYTs2fSOs2e43IfGqmTqlM+20NHF0cP0nmJJqi17qelq3e72O3OxoBgtAlJrMNK5R/9/ev7f355/+WHrz74F6hB/06DBQAA"

        //base64 to bytearray
        val bytes = convertBase64StringToByteArray(base64)

        val req = bilibili.main.community.reply.v1.MainListReq.parseFrom(bytes)
        println()
        println(req)
    }

    @Test
    fun foo2() {
        val base64 = "Cg42N+S6uuato+WcqOecixAeGAEqEjY35Lq65ZCM5pe25Zyo55yLfjoCNjc="
        val bytes = convertBase64StringToByteArray(base64)

        val req = bilibili.app.playeronline.v1.PlayerOnlineReply.parseFrom(bytes)
        println()
        println(req)
    }

    fun convertBase64StringToByteArray(base64String: String): ByteArray {
        return Base64.getDecoder().decode(base64String)
    }


}