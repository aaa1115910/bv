package dev.aaa1115910.biliapi.websocket

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

internal class LiveDataWebSocketTest {

    @Test
    fun connectLiveEvent() {
        runBlocking {
            LiveDataWebSocket.connectLiveEvent(5555) {
                println(it)
            }
            for (i in 1..10) {
                delay(1_000)
            }
        }
    }
}