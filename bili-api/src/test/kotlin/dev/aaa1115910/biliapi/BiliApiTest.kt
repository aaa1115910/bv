package dev.aaa1115910.biliapi

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.Test

internal class BiliApiTest {

    companion object {
        const val SESSDATA = ""
    }

    @Test
    fun `get popular videos`() {
        assertDoesNotThrow {
            runBlocking {
                val response = BiliApi.getPopularVideoData()
                println(response)
            }
        }
    }

    @Test
    fun `get video info`() {
        assertDoesNotThrow {
            runBlocking {
                val response = BiliApi.getVideoInfo(av = 170001)
                println(response)
            }
        }
    }

    @Test
    fun `get video play url`() {
        assertDoesNotThrow {
            runBlocking {
                val response = BiliApi.getVideoPlayUrl(
                    av = 648092492,
                    cid = 903675075,
                    fnval = 4048,
                    qn = 127,
                    sessData = SESSDATA
                )
                println(response)
            }
        }
    }

    @Test
    fun `get video danmaku from xml`() {
        assertDoesNotThrow {
            runBlocking {
                val response = BiliApi.getDanmakuXml(cid = 903675075)
                println(response)
            }
        }
    }

    @Test
    fun `get dynamic list with type all`() {
        assertDoesNotThrow {
            runBlocking {
                val response = BiliApi.getDynamicList(
                    type = "article",
                    sessData = SESSDATA
                )
                println(response)
            }
        }
    }
}