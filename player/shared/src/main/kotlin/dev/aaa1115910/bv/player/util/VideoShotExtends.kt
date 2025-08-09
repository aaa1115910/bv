package dev.aaa1115910.bv.player.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import dev.aaa1115910.biliapi.entity.video.VideoShot
import dev.aaa1115910.biliapi.repositories.VideoPlayRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

suspend fun VideoShot.getImage(time: Int): Bitmap {
    val index = findClosestValueIndex(times, time.toUShort())
    val singleImgCount = imageCountX * imageCountY
    val imagesIndex = index / singleImgCount
    val imageIndex = index % singleImgCount
    val x = imageIndex % imageCountX
    val y = imageIndex / imageCountX

    //println("get $time at $imagesIndex $x $y")

    // 新的支持调用去重（保留第一次解码）的缓存机制
    val bitmap = VideoShotImageCache.getOrDecodeImage(imagesIndex, images[imagesIndex]!!)

    val realImageWidth = bitmap.width / imageCountX
    val realImageHeight = bitmap.height / imageCountY

    return Bitmap.createBitmap(
        bitmap, x * realImageWidth, y * realImageHeight, realImageWidth, realImageHeight
    )
}

private fun findClosestValueIndex(array: List<UShort>, target: UShort): Int {
    var left = 0
    var right = array.size - 1
    while (left < right) {
        val mid = left + (right - left) / 2
        if (array[mid] < target) {
            left = mid + 1
        } else {
            right = mid
        }
    }
    return left
}

private object VideoShotImageCache {
    var hash: Int = 0
    var image: Bitmap? = null

    // 保存正在解码的任务，避免重复解码
    private val decodingTasks = mutableMapOf<Int, Deferred<Bitmap>>()

    // BitmapFactory 配置，使用 RGB_565 以减少内存占用
    val bitmapOptions = BitmapFactory.Options().apply {
        // 内存优化
        inPreferredConfig = Bitmap.Config.RGB_565 // 比 ARGB_8888 节省一半内存
        inMutable = false // 不可变，节省内存
        
        // 解码优化
        inScaled = false // 禁用缩放，避免额外计算
        
        // 内存管理
        inTempStorage = ByteArray(16 * 1024) // 16KB临时缓冲区，减少内存分配
        inSampleSize = 1 // 采样率，1表示原始大小
        
        // 其他性能优化
        inJustDecodeBounds = false // 实际解码像素数据
        inPremultiplied = false // 不进行预乘处理，节省计算
        
        // 现代化优化参数
        inBitmap = null // 不复用现有Bitmap，避免尺寸不匹配问题
        inDensity = 0 // 忽略密度设置，使用原始尺寸
        inTargetDensity = 0 // 忽略目标密度
        inScreenDensity = 0 // 忽略屏幕密度
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            inPreferredColorSpace = null // API 26+ 可使用默认色彩空间，避免转换开销
        }
    }

    suspend fun getOrDecodeImage(imagesIndex: Int, imageData: ByteArray): Bitmap = coroutineScope {
        val imageHash = imageData.hashCode()

        // 如果已经缓存了这张图片，直接返回
        if (imageHash == hash && image != null) {
            return@coroutineScope image!!
        }

        // 如果正在解码这张图片，等待解码完成
        decodingTasks[imagesIndex]?.let { existingTask ->
            return@coroutineScope existingTask.await()
        }

        val decodingTask = async {
            BitmapFactory.decodeByteArray(imageData, 0, imageData.size, bitmapOptions)
        }

        decodingTasks[imagesIndex] = decodingTask

        try {
            val result = decodingTask.await()
            hash = imageHash
            image = result
            return@coroutineScope result
        } finally {
            // 解码完成后移除任务
            decodingTasks.remove(imagesIndex)
        }
    }
}

@Composable
fun VideoShotTest(
    modifier: Modifier = Modifier,
    videoPlayRepository: VideoPlayRepository// = org.koin.compose.getKoin().get()
) {
    val aid = 170001L
    val cid = 279786L
    var videoShot: VideoShot? by remember { mutableStateOf(null) }
    LaunchedEffect(Unit) {
        videoShot = videoPlayRepository.getVideoShot(aid, cid)
    }

    if (videoShot != null) {
        LazyVerticalGrid(
            modifier = modifier,
            columns = GridCells.Fixed(10),
        ) {
            items(videoShot!!.times) { time ->
                var bitmap by remember { mutableStateOf<Bitmap?>(null) }
                
                LaunchedEffect(time) {
                    bitmap = videoShot!!.getImage(time.toInt())
                }
                
                bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = null
                    )
                }
            }
        }
    }
}