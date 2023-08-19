package dev.aaa1115910.biliapi.http

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class BiliPassportHttpApiTest {
    @Test
    fun `get web qr login url`() {
        val response = runBlocking { BiliPassportHttpApi.getWebQRUrl() }
        println("qr url: ${response.data?.url}")
        println("qr key: ${response.data?.qrcodeKey}")
        assertEquals(0, response.code)
    }

    @Test
    fun `request web qr login result`() {
        val qrUrlResponse = runBlocking { BiliPassportHttpApi.getWebQRUrl() }
        val url = qrUrlResponse.data?.url
        val key = qrUrlResponse.data?.qrcodeKey
        println("qr url: $url")
        println("qr key: $key")
        var loop = true
        while (loop) {
            val (loginResponse, cookies) = runBlocking { BiliPassportHttpApi.loginWithWebQR(key!!) }
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

    @Test
    fun `get app qr login url`() {
        val response = runBlocking {
            BiliPassportHttpApi.getAppQRUrl(
                localId = "1",
                ts = (System.currentTimeMillis() / 1000).toInt(),
                mobiApp = "android_hd"
            )
        }
        println(response)
        println("qr url: ${response.data?.url}")
        println("qr key: ${response.data?.authCode}")
        assertEquals(0, response.code)
    }

    @Test
    fun `request app qr login result`() {
        val qrUrlResponse = runBlocking {
            BiliPassportHttpApi.getAppQRUrl(
                localId = "1",
                ts = (System.currentTimeMillis() / 1000).toInt(),
                mobiApp = "android_hd"
            )
        }
        val url = qrUrlResponse.data?.url
        val key = qrUrlResponse.data?.authCode
        println("qr url: $url")
        println("qr key: $key")
        var loop = true
        while (loop) {
            val loginResponse = runBlocking {
                BiliPassportHttpApi.loginWithAppQR(
                    authCode = key!!,
                    localId = "1",
                    ts = (System.currentTimeMillis() / 1000).toInt()
                )
            }
            println(loginResponse)
            when (val result = loginResponse.code) {
                0 -> {
                    loop = false
                    println("login success")
                }

                86039 -> println("wait to scan")
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
            }
        }
    }

    @Test
    fun `get captcha`() = runBlocking {
        println(BiliPassportHttpApi.getCaptcha())
    }

    // this is a random phone number
    val tel = 13300000001L
    val loginSessionId = "144525a8fd2811edbe560242ac120002"
    val recaptchaToken = "766877c778b2425eb2c6fbc4791e2b43"
    val geeChallenge = "5ccbb4431fa57f5476e65facd9f2b111"
    val geeValidate = "04721095469a0a08c3f124a71c7b3c49"
    val buvid = "XYaa24a8d76a1140a332c16e1e2d4d66318ff"

    @Test
    fun `send sms`() = runBlocking {
        println(
            BiliPassportHttpApi.sendSms(
                cid = 86,
                tel = tel,
                loginSessionId = loginSessionId,
                channel = "bili",
                buvid = buvid,
                statistics = """{"appId":1,"platform":3,"version":"7.27.0","abtest":""}""",
                ts = System.currentTimeMillis() / 1000
            )
        )
    }

    @Test
    fun `send sms with captcha`() = runBlocking {
        println(
            BiliPassportHttpApi.sendSms(
                cid = 86,
                tel = tel,
                loginSessionId = loginSessionId,
                recaptchaToken = recaptchaToken,
                geeChallenge = geeChallenge,
                geeValidate = geeValidate,
                geeSeccode = "$geeValidate|jordan",
                channel = "bili",
                buvid = buvid,
                statistics = """{"appId":1,"platform":3,"version":"7.27.0","abtest":""}""",
                ts = System.currentTimeMillis() / 1000
            )
        )
    }

    @Test
    fun `login with sms`() = runBlocking {
        println(
            BiliPassportHttpApi.loginWithSms(
                cid = 86,
                tel = tel,
                loginSessionId = loginSessionId,
                code = 23,
                captchaKey = ""
            )
        )
    }
}