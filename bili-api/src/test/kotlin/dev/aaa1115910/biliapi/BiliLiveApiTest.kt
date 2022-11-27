package dev.aaa1115910.biliapi

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class BiliLiveApiTest {
    @Test
    fun `get history live room danmaku`() {
        Assertions.assertDoesNotThrow {
            runBlocking {
                val response = BiliLiveApi.getLiveDanmuHistory(roomId = 22739471)
                println(response)
            }
        }
    }

    @Test
    fun `get live event websocket connect url and token`() {
        Assertions.assertDoesNotThrow {
            runBlocking {
                val response = BiliLiveApi.getLiveDanmuInfo(roomId = 22739471)
                println(response)
            }
        }
    }

    @Test
    fun `get live room info`() {
        Assertions.assertDoesNotThrow {
            runBlocking {
                val response = BiliLiveApi.getLiveRoomPlayInfo(roomId = 22739471)
                println(response)
            }
        }
    }

}