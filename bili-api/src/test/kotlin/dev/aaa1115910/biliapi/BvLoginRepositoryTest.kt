package dev.aaa1115910.biliapi

import dev.aaa1115910.biliapi.http.BiliPassportHttpApi
import dev.aaa1115910.biliapi.http.util.generateBuvid
import dev.aaa1115910.biliapi.repositories.LoginRepository
import dev.aaa1115910.biliapi.repositories.SendSmsResult
import dev.aaa1115910.biliapi.repositories.SendSmsState
import kotlinx.coroutines.runBlocking
import java.net.URL

class BvLoginRepositoryTest {
    private val loginRepository = LoginRepository()
    private val phone = 16215705468L
    private val buvid = generateBuvid()
    private val loginSessionId = loginRepository.generateLoginSessionId()
    var captchaKey: String? = null

    fun `send sms`() = runBlocking {

        println(
            """
            buvid: $buvid
            loginSessionId: $loginSessionId
            phoneNumber: $phone
        """.trimIndent()
        )

        var recaptchaToken: String? = null
        var geetestChallenge: String? = null
        var geetestValidate: String? = null
        var geetestGt: String? = null

        var sendSmsResult: SendSmsResult? = null
        runCatching {
            sendSmsResult = loginRepository.requestSms(
                phone = phone,
                loginSessionId = loginSessionId,
                buvid = buvid
            )
            println("Request send sms result: $sendSmsResult")
        }.onFailure {
            it.printStackTrace()
            return@runBlocking
        }

        when (sendSmsResult!!.state) {
            SendSmsState.Error -> {
                println("error")
            }

            SendSmsState.Success -> {
                println("send sms success")
                captchaKey = sendSmsResult!!.captchaKey
                println("captcha key: $captchaKey")
            }

            SendSmsState.RecaptchaRequire -> {
                println("require recaptcha")
                println("recaptcha url: ${sendSmsResult!!.recaptchaUrl}")

                URL(sendSmsResult!!.recaptchaUrl).query.split("&").forEach {
                    val (key, value) = it.split("=")
                    when (key) {
                        "recaptcha_token" -> recaptchaToken = value
                        "gee_gt" -> geetestGt = value
                        "gee_challenge" -> geetestChallenge = value
                    }
                }

                println(
                    """
                    recaptchaToken: $recaptchaToken
                    geetestGt: $geetestGt
                    geetestChallenge: $geetestChallenge
                """.trimIndent()
                )
                print("Please input the validate: ")
                geetestValidate = readln()
                //应使用新的 challenge
                print("Please input the challenge: ")
                geetestChallenge = readln()

                //retry send sms
                runCatching {
                    sendSmsResult = loginRepository.requestSms(
                        phone = phone,
                        loginSessionId = loginSessionId,
                        buvid = buvid,
                        recaptchaToken = recaptchaToken,
                        geetestChallenge = "$geetestChallenge",
                        geetestValidate = geetestValidate,
                    )
                    println("Request send sms result: $sendSmsResult")
                }.onFailure {
                    it.printStackTrace()
                    return@runBlocking
                }

                when (sendSmsResult!!.state) {
                    SendSmsState.Success -> {
                        println("send sms success")
                        captchaKey = sendSmsResult!!.captchaKey
                        println("captcha key: $captchaKey")
                    }

                    else -> {
                        println("error")
                    }
                }
            }

            else -> {}
        }
    }

    fun `login with sms`() = runBlocking {
        println("====login with sms==== ")
        print("please input the sms code: ")
        val code = readln().toInt()
        val response = BiliPassportHttpApi.loginWithSms(
            cid = 86,
            tel = phone,
            loginSessionId = loginSessionId,
            code = code,
            captchaKey = captchaKey!!
        )
        println(response.getResponseData())
    }
}

fun main() {
    val test = BvLoginRepositoryTest()
    test.`send sms`()
    test.captchaKey?.let {
        test.`login with sms`()
    }
}