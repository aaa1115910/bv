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

    @Test
    fun `get user info from Mr_He`() {
        assertDoesNotThrow {
            runBlocking {
                val response = BiliApi.getUserInfo(
                    uid = 163637592,
                    sessData = SESSDATA
                )
                println(response)
            }
        }
    }

    @Test
    fun `get user card info from Mr_He`() {
        assertDoesNotThrow {
            runBlocking {
                val response = BiliApi.getUserCardInfo(
                    uid = 163637592,
                    photo = true,
                    sessData = SESSDATA
                )
                println(response)
            }
        }
    }

    @Test
    fun `get self user info`() {
        assertDoesNotThrow {
            runBlocking {
                val response = BiliApi.getUserSelfInfo(
                    sessData = SESSDATA
                )
                println(response)
            }
        }
    }

    @Test
    fun `get histories`() {
        assertDoesNotThrow {
            runBlocking {
                val response = BiliApi.getHistories(
                    sessData = SESSDATA
                )
                println(response)
            }
        }
    }

    @Test
    fun `get related vidoes`() {
        assertDoesNotThrow {
            runBlocking {
                val response = BiliApi.getRelatedVideos(
                    avid = 170001
                )
                println(response)
            }
        }
    }

    @Test
    fun `get favorite folder metadata from id 2333`(){
        runBlocking {
            val response = BiliApi.getFavoriteFolderInfo(
                mediaId = 2333
            )
            println(response)
        }
    }

    @Test
    fun `get all favorite folders metadata`(){
        runBlocking {
            val response = BiliApi.getAllFavoriteFoldersInfo(
                mid = 2333,
                sessData= SESSDATA
            )
            println(response)
        }
    }

    @Test
    fun `get all favorite item ids`(){
        runBlocking {
            val response = BiliApi.getFavoriteIdList(
                mediaId = 2333,
                sessData= SESSDATA
            )
            println(response)
        }
    }

    @Test
    fun `get favorite list`(){
        runBlocking {
            val response = BiliApi.getFavoriteList(
                mediaId = 2333,
                sessData= SESSDATA
            )
            println(response)
        }
    }
}