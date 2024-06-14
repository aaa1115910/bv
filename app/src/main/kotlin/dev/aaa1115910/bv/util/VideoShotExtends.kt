package dev.aaa1115910.bv.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.tv.foundation.lazy.grid.TvGridCells
import androidx.tv.foundation.lazy.grid.TvLazyVerticalGrid
import androidx.tv.foundation.lazy.grid.items
import dev.aaa1115910.biliapi.entity.video.VideoShot
import dev.aaa1115910.biliapi.repositories.VideoPlayRepository
import org.koin.compose.getKoin

fun VideoShot.getImage(time: Int): Bitmap {
    val index = findClosestValueIndex(times, time.toUShort())
    val singleImgCount = imageCountX * imageCountY
    val imagesIndex = index / singleImgCount
    val imageIndex = index % singleImgCount
    val x = imageIndex % imageCountX
    val y = imageIndex / imageCountX

    //println("get $time at $imagesIndex $x $y")

    val bitmap = if (images[imagesIndex].hashCode() == VideoShotImageCache.hash) {
        VideoShotImageCache.image!!
    } else {
        BitmapFactory.decodeByteArray(images[imagesIndex], 0, images[imagesIndex]!!.size).also {
            VideoShotImageCache.hash = images[imagesIndex].hashCode()
            VideoShotImageCache.image = it
        }
    }

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
}

@Composable
fun VideoShotTest(
    modifier: Modifier = Modifier,
    videoPlayRepository: VideoPlayRepository = getKoin().get()
) {
    val aid = 170001L
    val cid = 279786L
    var videoShot: VideoShot? by remember { mutableStateOf(null) }
    LaunchedEffect(Unit) {
        videoShot = videoPlayRepository.getVideoShot(aid, cid)
    }

    if (videoShot != null) {
        TvLazyVerticalGrid(
            modifier = modifier,
            columns = TvGridCells.Fixed(10),
        ) {
            items(videoShot!!.times) { time ->
                val bitmap = videoShot!!.getImage(time.toInt())
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null
                )
            }
        }
    }
}