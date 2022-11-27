package dev.aaa1115910.biliapi

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.Test

internal class BiliApiTest {

    @Test
    fun getPopularVideoData() {
        assertDoesNotThrow {
            runBlocking {
                val response = BiliApi.getPopularVideoData()
                println(response)
            }
        }
    }

    @Test
    fun getVideoInfo() {
        assertDoesNotThrow {
            runBlocking {
                val response = BiliApi.getVideoInfo(av = 170001)
                println(response)
            }
        }
    }

    @Test
    fun getVideoPlayUrl() {
        assertDoesNotThrow {
            runBlocking {
                val response = BiliApi.getVideoPlayUrl(av = 170001, cid = 267714)
                println(response)
            }
        }
    }

    @Test
    fun getDanmakuXml() {
        assertDoesNotThrow {
            runBlocking {
                val response = BiliApi.getDanmakuXml(cid = 267714)
                println(response)
            }
        }
    }

    @Test
    fun getLiveDanmuHistory() {
        assertDoesNotThrow {
            runBlocking {
                val response = BiliApi.getLiveDanmuHistory(roomId = 22739471)
                println(response)
            }
        }
    }
}