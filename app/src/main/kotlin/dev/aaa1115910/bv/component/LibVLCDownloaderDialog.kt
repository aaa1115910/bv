package dev.aaa1115910.bv.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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

        scope.launch(Dispatchers.Default) {
            runCatching {
                val release =
                    VlcLibsApi.getRelease(BuildConfig.libVLCVersion)
                        ?: throw IllegalStateException("Release not found")
                val tempFilename = "${UUID.randomUUID()}.zip"
                val tempFile = File(context.cacheDir, tempFilename)
                tempFile.createNewFile()

                VlcLibsApi.downloadFile(
                    release,
                    tempFile,
                    object : ProgressListener {
                        override suspend fun invoke(downloaded: Long, total: Long) {

                        }
                    })

                unZipLibs(tempFile)
            }.onSuccess {
                withContext(Dispatchers.Main) {
                    "Install success".toast(context)
                }
            }.onFailure {
                withContext(Dispatchers.Main) {
                    "Install failed".toast(context)
                }
                println(it)
            }

            processing = false
        }
    }

    if (show) {
        AlertDialog(
            modifier = modifier,
            title = { Text(text = "LibVLC Downloader") },
            onDismissRequest = { if (!processing) onHideDialog() },
            confirmButton = {
                FilledTonalButton(
                    onClick = { startInstall() },
                    enabled = !processing
                ) {
                    Text(text = "Download")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { onHideDialog() },
                    enabled = !processing
                ) {
                    Text(text = "Cancel")
                }
            }
        )
    }
}

@Suppress("ArrayInDataClass")
private data class UnzippedFile(val filename: String, val content: ByteArray)
