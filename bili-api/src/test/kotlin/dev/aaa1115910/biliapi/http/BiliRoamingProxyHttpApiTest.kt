package dev.aaa1115910.biliapi.http

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Paths
import java.util.Properties

class BiliRoamingProxyHttpApiTest {
    companion object {
        private val localProperties = Properties().apply {
            val path = Paths.get("../local.properties").toAbsolutePath().toString()
            load(File(path).bufferedReader())
        }
        val ACCESS_TOKEN: String =
            runCatching { localProperties.getProperty("test.access_token") }.getOrNull() ?: ""
        val PROXY_SERVER: String =
            runCatching { localProperties.getProperty("test.proxy_server") }.getOrNull() ?: ""
    }

    init {
        BiliRoamingProxyHttpApi.createClient(PROXY_SERVER)
    }

    @Test
    fun `get pgc play url with web api`() = runBlocking {
        val result = BiliRoamingProxyHttpApi.getWebPgcVideoPlayUrl(
            cid = 1188129412,
            epid = 759510,
            fnval = 4048,
            qn = 127,
            fnver = 0,
            fourk = 1,
            accessKey = ACCESS_TOKEN,
            area = "hk"
        )
        println(result)
    }

    @Test
    fun `get pgc play url with app api`() = runBlocking {
        val result = BiliRoamingProxyHttpApi.getAppPgcVideoPlayUrl(
            cid = 1188129412,
            epid = 759510,
            fnval = 4048,
            qn = 127,
            fnver = 0,
            fourk = 1,
            accessKey = ACCESS_TOKEN,
            area = "hk"
        )
        println(result)
    }
}