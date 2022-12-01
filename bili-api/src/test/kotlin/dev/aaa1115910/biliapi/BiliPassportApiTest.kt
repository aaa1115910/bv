package dev.aaa1115910.biliapi

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class BiliPassportApiTest {
    @Test
    fun `get qr login url`() {
        val response = runBlocking { BiliPassportApi.getQRUrl() }
        println("qr url: ${response.data?.url}")
        println("qr key: ${response.data?.qrcodeKey}")
        assertEquals(0, response.code)
    }

    @Test
    fun `request qr login result`() {
        val qrUrlResponse = runBlocking { BiliPassportApi.getQRUrl() }
        val url = qrUrlResponse.data?.url
        val key = qrUrlResponse.data?.qrcodeKey
        println("qr url: $url")
        println("qr key: $key")
        var loop = true
        while (loop) {
            val (loginResponse, cookies) = runBlocking { BiliPassportApi.loginWithQR(key!!) }
            when (val result = loginResponse.data?.code) {
                0 -> {
                    loop = false
                    println("login success")
                }

                86101 -> println("wait to scan")
                86090 -> println("wait to confirm")
                86038 -> {
                    loop = false
                    println("qr expired")
                }

                else -> {
                    loop = false
                    println("unknown code: $result")
                }
            }
            if (loop) {
                runBlocking { delay(1000) }
            } else {
                println(loginResponse)
                println(cookies)
            }
        }
    }
}