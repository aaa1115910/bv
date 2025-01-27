package dev.aaa1115910.biliapi.entity.video

import dev.aaa1115910.biliapi.http.BiliHttpApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.DataInputStream

data class VideoShot(
    val times: List<UShort>,
    val images: List<ByteArray?>,
    val imageCountX: Int,
    val imageCountY: Int,
    val imageWidth: Int,
    val imageHeight: Int
) {
    companion object {
        suspend fun fromVideoShot(videoShot: dev.aaa1115910.biliapi.http.entity.video.VideoShot): VideoShot? =
            withContext(Dispatchers.IO) {
                val images = videoShot.image.map { imageUrl ->
                    async {
                        runCatching {
                            BiliHttpApi.download(imageUrl)
                        }.getOrNull()
                    }
                }.awaitAll()
                if (images.contains(null)) {
                    println("download video shot images failed")
                    return@withContext null
                }

                val timeBinary = runCatching {
                    BiliHttpApi.download(
                        videoShot.pvData ?: throw IllegalStateException("pvData is null")
                    )
                }.onFailure {
                    println("download video shot times binary failed: ${it.stackTraceToString()}")
                    return@withContext null
                }.getOrNull()

                val times = mutableListOf<UShort>()
                runCatching {
                    DataInputStream(ByteArrayInputStream(timeBinary)).use {
                        //if has next
                        while (it.available() > 0) {
                            times.add(it.readUnsignedShort().toUShort())
                        }
                    }
                }.onFailure {
                    println("parse video shot times binary failed: ${it.stackTraceToString()}")
                    return@withContext null
                }

                return@withContext VideoShot(
                    times = times.drop(1),
                    images = images,
                    imageCountX = videoShot.imgXLen,
                    imageCountY = videoShot.imgYLen,
                    imageWidth = videoShot.imgXSize,
                    imageHeight = videoShot.imgYSize
                )
            }
    }
}
