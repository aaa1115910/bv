package dev.aaa1115910.biliapi.repositories

import dev.aaa1115910.biliapi.entity.ApiType
import kotlinx.coroutines.runBlocking
import java.io.File
import java.nio.file.Paths
import java.util.Properties
import kotlin.test.Test

class CommentRepositoryTest {
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
    private val commentRepository = CommentRepository(authRepository, channelRepository)

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
    fun `get comments`() = runBlocking {
        val commentId = 1018604077579239458L
        val commentType = 17L
        ApiType.entries.forEach { apiType ->
            val commentsData = commentRepository.getComments(
                id = commentId,
                type = commentType,
                preferApiType = apiType
            )
            println("ApiType: $apiType")
            println(commentsData)
        }
    }

    @Test
    fun `get comment replies`() = runBlocking {
        val commentId = 1018604077579239458L
        val commentType = 17L
        val rpid = 251515293904
        ApiType.entries.forEach { apiType ->
            val commentsData = commentRepository.getCommentReplies(
                rpid = rpid,
                commentId = commentId,
                type = commentType,
                preferApiType = apiType
            )
            println("ApiType: $apiType")
            println(commentsData)
        }
    }
}