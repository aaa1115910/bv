package dev.aaa1115910.biliapi.repositories

import dev.aaa1115910.biliapi.entity.ugc.UgcType
import kotlinx.coroutines.runBlocking
import java.io.File
import java.nio.file.Paths
import java.util.Properties
import kotlin.test.Test

class UgcRepositoryTest {
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
    private val ugcRepository: UgcRepository = UgcRepository(authRepository)

    init {
        authRepository.sessionData = SESSDATA
        authRepository.accessToken = ACCESS_TOKEN
        authRepository.biliJct = BILI_JCT
    }

    @Test
    fun `get region data`() = runBlocking {
        UgcType.entries
            .filter { it.locId != -1 }
            .forEach { ugcType ->
                println("ugcType: $ugcType")
                val result = ugcRepository.getRegionData(ugcType)
                println(result)
            }
    }

    @Test
    fun `get region more data`() = runBlocking {
        UgcType.entries
            .filter { it.locId != -1 }
            .forEach { ugcType ->
                println("ugcType: $ugcType")
                val result = ugcRepository.getRegionMoreData(ugcType)
                println(result)
            }
    }
}
