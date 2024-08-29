package dev.aaa1115910.bv.screen.settings.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.OutlinedButton
import androidx.tv.material3.Text
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.component.settings.SettingListItem
import dev.aaa1115910.bv.screen.settings.SettingsMenuNavItem
import dev.aaa1115910.bv.util.LogCatcherUtil
import dev.aaa1115910.bv.util.fInfo
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun StorageSetting(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val logger = KotlinLogging.logger { }

    var loading by remember { mutableStateOf(false) }
    var imageCacheSize by remember { mutableLongStateOf(0L) }
    var updateCacheSize by remember { mutableLongStateOf(0L) }
    var crashLogsSize by remember { mutableLongStateOf(0L) }
    //var libVLCCacheSize by remember { mutableLongStateOf(0L) }
    //var libVLCFileSize by remember { mutableLongStateOf(0L) }

    var showConfirmDialog by remember { mutableStateOf(false) }
    var clearFun: (() -> Unit)? by remember { mutableStateOf(null) }
    var content by remember { mutableStateOf("") }
    var size by remember { mutableLongStateOf(0L) }

    val calSize = {
        val imageCacheDir = File(context.cacheDir, "image_cache")
        val updateCacheDir = File(context.cacheDir, "update_downloader")
        val crashLogsDir = File(context.filesDir, LogCatcherUtil.LOG_DIR)
        //val libVLCCacheDir = File(context.cacheDir, "libvlc_downloader")
        //val libVLCFileDir = File(context.filesDir, "vlc_libs")

        imageCacheSize = getFolderSize(imageCacheDir)
        updateCacheSize = getFolderSize(updateCacheDir)
        crashLogsSize = getFolderSize(crashLogsDir)
        //libVLCCacheSize = getFolderSize(libVLCCacheDir)
        //libVLCFileSize = getFolderSize(libVLCFileDir)
    }

    val clearImageCaches: () -> Unit = {
        logger.fInfo { "clearImageCaches" }
        val imageCacheDir = File(context.cacheDir, "image_cache")
        imageCacheDir.deleteRecursively()
    }

    val clearCrashLogs: () -> Unit = {
        logger.fInfo { "clearCrashLogs" }
        val crashLogsDir = File(context.filesDir, LogCatcherUtil.LOG_DIR)
        crashLogsDir.deleteRecursively()
    }

    val clearOthersCaches: () -> Unit = {
        logger.fInfo { "clearOthersCaches" }
        val updateCacheDir = File(context.cacheDir, "update_downloader")
        //val libVLCCacheDir = File(context.cacheDir, "libvlc_downloader")
        updateCacheDir.deleteRecursively()
        //libVLCCacheDir.deleteRecursively()
    }

    //val clearLibVLCFiles: () -> Unit = {
    //    logger.fInfo { "clearLibVLCFiles" }
    //    val libVLCFileDir = File(context.filesDir, "vlc_libs")
    //    libVLCFileDir.deleteRecursively()
    //}

    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.IO) {
            loading = true
            calSize()
            loading = false
        }
    }

    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = SettingsMenuNavItem.Storage.getDisplayName(context),
                style = MaterialTheme.typography.displaySmall
            )
            Spacer(modifier = Modifier.height(12.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    SettingListItem(
                        title = stringResource(R.string.settings_storage_image_cache),
                        supportText = if (loading) stringResource(R.string.settings_storage_calculating)
                        else "${imageCacheSize / 1024 / 1024} MB",
                        onClick = {
                            clearFun = clearImageCaches
                            content = context.getString(R.string.settings_storage_image_cache)
                            size = imageCacheSize
                            showConfirmDialog = true
                        }
                    )
                }
                item {
                    SettingListItem(
                        title = stringResource(R.string.settings_storage_others_cache),
                        supportText = if (loading) stringResource(R.string.settings_storage_calculating)
                        //else "${updateCacheSize + libVLCCacheSize / 1024 / 1024} MB",
                        else "${updateCacheSize / 1024 / 1024} MB",
                        onClick = {
                            clearFun = clearOthersCaches
                            content = context.getString(R.string.settings_storage_others_cache)
                            size = updateCacheSize// + libVLCCacheSize
                            showConfirmDialog = true
                        }
                    )
                }
                //item {
                //    SettingListItem(
                //        title = stringResource(R.string.settings_storage_libvlc_files),
                //        supportText = if (loading) stringResource(R.string.settings_storage_calculating)
                //        else "${libVLCFileSize / 1024 / 1024} MB",
                //        onClick = {
                //            clearFun = clearLibVLCFiles
                //            content = context.getString(R.string.settings_storage_libvlc_files)
                //            size = libVLCFileSize
                //            showConfirmDialog = true
                //        }
                //    )
                //}

                item {
                    SettingListItem(
                        title = stringResource(R.string.settings_storage_crash_logs),
                        supportText = if (loading) stringResource(R.string.settings_storage_calculating)
                        else "${crashLogsSize / 1024 / 1024} MB",
                        onClick = {
                            clearFun = clearCrashLogs
                            content = context.getString(R.string.settings_storage_crash_logs)
                            size = crashLogsSize
                            showConfirmDialog = true
                        }
                    )
                }
            }
        }
    }

    ConfirmDeleteDialog(
        show = showConfirmDialog,
        onHideDialog = { showConfirmDialog = false },
        content = content,
        size = size,
        clearFiles = {
            clearFun?.invoke()
            calSize()
        }
    )
}

private fun getFolderSize(f: File): Long {
    var size: Long = 0
    if (f.isDirectory) {
        for (file in f.listFiles()!!) {
            size += getFolderSize(file)
        }
    } else {
        size = f.length()
    }
    return size
}

@Composable
private fun ConfirmDeleteDialog(
    modifier: Modifier = Modifier,
    show: Boolean,
    onHideDialog: () -> Unit,
    content: String,
    size: Long,
    clearFiles: () -> Unit
) {
    if (show) {
        AlertDialog(
            modifier = modifier,
            onDismissRequest = onHideDialog,
            title = { Text(text = "清除$content") },
            text = { Text(text = "${size / 1024 / 1024} MB") },
            confirmButton = {
                Button(onClick = {
                    clearFiles()
                    onHideDialog()
                }) {
                    Text(text = "确定")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = onHideDialog) {
                    Text(text = "取消")
                }
            }
        )
    }
}