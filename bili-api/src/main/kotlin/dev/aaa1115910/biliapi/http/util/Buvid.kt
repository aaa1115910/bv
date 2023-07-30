package dev.aaa1115910.biliapi.http.util

import java.math.BigInteger
import java.security.MessageDigest

fun generateBuvid(): String {
    val mac = mutableListOf<String>()
    for (i in 0 until 6) {
        val min = 0
        val max = 0xff
        val num = (Math.random() * (max - min + 1) + min).toInt().toString(16)
        mac.add(num)
    }
    val md5 = md5(mac.joinToString(":"))
    val md5Arr = md5.split("").toTypedArray()
    return "XY${md5Arr[2]}${md5Arr[12]}${md5Arr[22]}$md5"
}

fun md5(input: String): String {
    val md = MessageDigest.getInstance("MD5")
    val messageDigest = md.digest(input.toByteArray())
    val no = BigInteger(1, messageDigest)
    var hashText = no.toString(16)
    while (hashText.length < 32) {
        hashText = "0$hashText"
    }
    return hashText
}