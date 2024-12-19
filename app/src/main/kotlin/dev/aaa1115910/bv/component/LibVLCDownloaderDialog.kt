package dev.aaa1115910.bv.component

import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.tv.material3.Button
import androidx.tv.material3.OutlinedButton
import androidx.tv.material3.Text
import dev.aaa1115910.bv.network.VlcLibsApi
import dev.aaa1115910.bv.player.BuildConfig
import dev.aaa1115910.bv.util.toast
import io.ktor.client.content.ProgressListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import java.util.zip.ZipInputStream

@Composable
fun LibVLCDownloaderDialog(
    modifier: Modifier = Modifier,
    show: Boolean,
    onHideDialog: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var processing by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("等待操作中...") }

    val unZipLibs: (zipFile: File) -> Unit = { zipFile ->
        val vlcLibsDir = File(context.filesDir, "vlc_libs")
        vlcLibsDir.mkdir()
        vlcLibsDir.listFiles()?.forEach { file ->
            file.delete()
        }

        ZipInputStream(zipFile.inputStream())
            .use { zipInputStream ->
                generateSequence { zipInputStream.nextEntry }
                    .map {
                        UnzippedFile(
                            filename = it.name,
                            content = zipInputStream.readBytes()
                        )
                    }.toList()
            }
            .forEach {
                println("Extracting ${it.filename}")
                val file = File(vlcLibsDir, it.filename)
                file.createNewFile()
                file.writeBytes(it.content)
            }
    }

    val startInstall: () -> Unit = {
        processing = true

        scope.launch(Dispatchers.IO) {
            runCatching {
                text = "正在获取下载地址"
                val release =
                    VlcLibsApi.getRelease(BuildConfig.libVLCVersion)
                        ?: throw IllegalStateException("Release not found")
                val tempFilename = "${UUID.randomUUID()}.zip"
                val tempDir = File(context.cacheDir, "libvlc_downloader")
                if (!tempDir.exists()) tempDir.mkdirs()
                val tempFile = File(tempDir, tempFilename)
                tempFile.createNewFile()

                VlcLibsApi.downloadFile(
                    release,
                    tempFile,
                    object : ProgressListener {
                        override suspend fun onProgress(downloaded: Long, total: Long?) {
                            text = "正在下载(${downloaded / (total?.toFloat() ?: 0f) * 100}%)"
                        }
                    })

                text = "正在解压"
                unZipLibs(tempFile)
            }.onSuccess {
                text = "安装完成"
                onHideDialog()
                withContext(Dispatchers.Main) {
                    "LibVLC 安装成功".toast(context)
                }
            }.onFailure {
                text = "安装失败"
                withContext(Dispatchers.Main) {
                    "LibVLC 安装失败: ${it.message}".toast(context)
                }
                it.printStackTrace()
            }

            processing = false
        }
    }

    if (show) {
        AlertDialog(
            modifier = modifier,
            title = { Text(text = "LibVLC 下载器") },
            text = { Text(text = text) },
            onDismissRequest = { if (!processing) onHideDialog() },
            confirmButton = {
                Button(
                    onClick = { startInstall() },
                    enabled = !processing
                ) {
                    Text(text = "下载")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { onHideDialog() },
                    enabled = !processing
                ) {
                    Text(text = "取消")
                }
            }
        )
    }
}

@Suppress("ArrayInDataClass")
private data class UnzippedFile(val filename: String, val content: ByteArray)
