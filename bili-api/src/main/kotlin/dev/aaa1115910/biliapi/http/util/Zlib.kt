package dev.aaa1115910.biliapi.http.util

import io.ktor.utils.io.core.use
import java.io.ByteArrayOutputStream
import java.util.zip.Deflater
import java.util.zip.Inflater

fun ByteArray.zlibCompress(): ByteArray {
    val output = ByteArray(this.size * 4)
    val compressor = Deflater().apply {
        setInput(this@zlibCompress)
        finish()
    }
    val compressedDataLength: Int = compressor.deflate(output)
    return output.copyOfRange(0, compressedDataLength)
}

fun ByteArray.zlibDecompress(): ByteArray {
    val inflater = Inflater()
    val outputStream = ByteArrayOutputStream()
    return outputStream.use {
        val buffer = ByteArray(1024)
        inflater.setInput(this)
        var count = -1
        while (count != 0) {
            count = inflater.inflate(buffer)
            outputStream.write(buffer, 0, count)
        }
        inflater.end()
        outputStream.toByteArray()
    }
}