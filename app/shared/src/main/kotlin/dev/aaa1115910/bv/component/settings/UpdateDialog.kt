package dev.aaa1115910.bv.component.settings

import android.content.Intent
import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.ProvideTextStyle
import dev.aaa1115910.bv.BuildConfig
import dev.aaa1115910.bv.network.GithubApi
import dev.aaa1115910.bv.network.entity.Release
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.util.fException
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.toMBString
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.content.ProgressListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

@Composable
fun UpdateDialog(
    modifier: Modifier = Modifier,
    show: Boolean,
    onHideDialog: () -> Unit,
    text: @Composable ((text: String) -> Unit),
    button: @Composable ((enabled: Boolean, onClick: () -> Unit, content: @Composable (RowScope.() -> Unit)) -> Unit),
    outlinedButton: @Composable ((enabled: Boolean, onClick: () -> Unit, content: @Composable (RowScope.() -> Unit)) -> Unit)
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val logger = KotlinLogging.logger("UpdateDialog")

    var updateStatus by remember { mutableStateOf(UpdateStatus.UpdatingInfo) }

    var bytesSentTotal: Long by remember { mutableLongStateOf(0L) }
    var contentLength: Long by remember { mutableLongStateOf(0L) }
    var targetProgress by remember { mutableFloatStateOf(0f) }
    val progress by animateFloatAsState(
        targetValue = targetProgress,
        label = "update progress"
    )

    var latestReleaseBuild by remember { mutableStateOf<Release?>(null) }

    var downloadJob by remember { mutableStateOf<Job?>(null) }
    DisposableEffect(show) {
        if(!show) {
            downloadJob?.cancel()
        }
        onDispose {
            downloadJob?.cancel()
        }
    }
    val checkUpdate: () -> Unit = {
        updateStatus = UpdateStatus.UpdatingInfo

        scope.launch(Dispatchers.IO) {
            runCatching {
                latestReleaseBuild = GithubApi.getLatestBuild()
                val revision = latestReleaseBuild!!
                    .assets.first { it.name.startsWith("BV") }
                    .name.split("_")[1].toInt()
                if (revision <= BuildConfig.VERSION_CODE) {
                    updateStatus = UpdateStatus.NoAvailableUpdate
                    return@launch
                }
            }.onFailure {
                logger.fException(it) { "Failed to get latest version" }
                updateStatus = UpdateStatus.CheckError
            }.onSuccess {
                logger.fInfo { "Find latest version ${latestReleaseBuild!!.name}" }
                updateStatus = UpdateStatus.Ready
            }
        }
    }

    val installUpdate: (File) -> Unit = { file ->
        updateStatus = UpdateStatus.Installing
        runCatching {
            val uri =
                FileProvider.getUriForFile(context, "${BuildConfig.APPLICATION_ID}.provider", file)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                addCategory(Intent.CATEGORY_DEFAULT)
                setDataAndType(uri, "application/vnd.android.package-archive")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(intent)
        }.onFailure {
            updateStatus = UpdateStatus.InstallError
        }
    }

    val startUpdate: () -> Unit = {
        updateStatus = UpdateStatus.Downloading
        downloadJob = scope.launch(Dispatchers.IO) {
            val tempFilename = "${UUID.randomUUID()}.apk"
            val tempDir = File(context.cacheDir, "update_downloader")
            if (!tempDir.exists()) tempDir.mkdirs()
            val tempFile = File(tempDir, tempFilename)
            tempFile.createNewFile()
            runCatching {
                GithubApi.downloadUpdate(
                    latestReleaseBuild!!,
                    tempFile,
                    object : ProgressListener {
                        override suspend fun onProgress(downloaded: Long, total: Long?) {
                            bytesSentTotal = downloaded
                            contentLength = total ?: 0
                            targetProgress =
                                runCatching { bytesSentTotal.toFloat() / contentLength }
                                    .getOrDefault(0f)
                        }
                    })
                if (show) installUpdate(tempFile)
            }.onFailure {
                logger.fException(it) { "Failed to download update" }
                updateStatus = UpdateStatus.DownloadError
            }
        }
    }

    LaunchedEffect(show) {
        if (show) {
            checkUpdate()
        } else {
            updateStatus = UpdateStatus.UpdatingInfo
        }
    }

    if (show) {
        UpdateDialogContent(
            modifier = modifier,
            updateStatus = updateStatus,
            latestReleaseBuild = latestReleaseBuild,
            progress = progress,
            bytesSentTotal = bytesSentTotal,
            contentLength = contentLength,
            onHideDialog = onHideDialog,
            checkUpdate = checkUpdate,
            startUpdate = startUpdate,
            text = text,
            button = button,
            outlinedButton = outlinedButton
        )
    }
}

@Composable
private fun UpdateDialogContent(
    modifier: Modifier = Modifier,
    updateStatus: UpdateStatus,
    latestReleaseBuild: Release?,
    progress: Float,
    bytesSentTotal: Long,
    contentLength: Long,
    onHideDialog: () -> Unit,
    checkUpdate: () -> Unit,
    startUpdate: () -> Unit,
    text: @Composable ((text: String) -> Unit),
    button: @Composable ((enabled: Boolean, onClick: () -> Unit, content: @Composable (RowScope.() -> Unit)) -> Unit),
    outlinedButton: @Composable ((enabled: Boolean, onClick: () -> Unit, content: @Composable (RowScope.() -> Unit)) -> Unit)
) {
    val configuration = LocalConfiguration.current
    val maxHeight = (configuration.screenHeightDp * 0.8).dp
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val confirmButtonFocusRequester = remember { FocusRequester() }
    
    AlertDialog(
        modifier = modifier
            .width(450.dp)
            .heightIn(max = maxHeight),
        onDismissRequest = { onHideDialog() },
        title = {
            ProvideTextStyle(
                value = MaterialTheme.typography.headlineSmall
            ) {
                text(
                    when (updateStatus) {
                        UpdateStatus.UpdatingInfo -> "获取更新信息中"
                        UpdateStatus.Ready -> latestReleaseBuild!!.name
                        UpdateStatus.Downloading -> "下载中"
                        UpdateStatus.Installing -> "安装中"
                        UpdateStatus.NoAvailableUpdate -> "无可用更新"
                        UpdateStatus.CheckError -> "检查更新失败"
                        UpdateStatus.DownloadError -> "下载失败"
                        UpdateStatus.InstallError -> "安装失败"
                    }
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .focusable()
                    .onKeyEvent { keyEvent ->
                        when (keyEvent.key) {
                            Key.DirectionUp -> {
                                scope.launch {
                                    scrollState.animateScrollBy(-100f)
                                }
                                true
                            }
                            Key.DirectionDown -> {
                                scope.launch {
                                    val canScrollDown = scrollState.value < scrollState.maxValue
                                    if (canScrollDown) {
                                        scrollState.animateScrollBy(100f)
                                    } else {
                                        // 已经滚动到底部，将焦点转移到确认按钮
                                        confirmButtonFocusRequester.requestFocus()
                                    }
                                }
                                true
                            }
                            else -> false
                        }
                    }
            ) {
                when (updateStatus) {
                    UpdateStatus.UpdatingInfo -> text("检查更新中...")
                    UpdateStatus.Ready -> text(latestReleaseBuild?.body ?: "Empty content")
                    UpdateStatus.Downloading -> Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            text("${bytesSentTotal.toMBString()}/${contentLength.toMBString()}")
                        }
                    }

                    UpdateStatus.Installing -> text("请坐和放宽")
                    UpdateStatus.DownloadError -> text("下载失败")
                    UpdateStatus.InstallError -> text("安装失败")
                    UpdateStatus.CheckError -> text("获取更新信息失败")
                    UpdateStatus.NoAvailableUpdate -> text("真没更新，骗你是小狗！")
                }
            }
        },
        confirmButton = {
            when (updateStatus) {
                UpdateStatus.UpdatingInfo, UpdateStatus.NoAvailableUpdate, UpdateStatus.Downloading, UpdateStatus.Installing -> {}

                UpdateStatus.Ready -> {
                    Box(modifier = Modifier.focusRequester(confirmButtonFocusRequester)) {
                        button(true, startUpdate) {
                            text("立即更新")
                        }
                    }
                }

                UpdateStatus.InstallError, UpdateStatus.DownloadError, UpdateStatus.CheckError -> {
                    Box(modifier = Modifier.focusRequester(confirmButtonFocusRequester)) {
                        button(true, checkUpdate) {
                            text("再试一次")
                        }
                    }
                }
            }
        },
        dismissButton = {
            Box(modifier = Modifier.focusRequester(confirmButtonFocusRequester)) {
                outlinedButton(
                    !(updateStatus == UpdateStatus.Downloading || updateStatus == UpdateStatus.Installing),
                    onHideDialog
                ) {
                    text(
                        when (updateStatus) {
                            UpdateStatus.UpdatingInfo -> "我点错了"
                            UpdateStatus.Ready -> "打死不更"
                            UpdateStatus.NoAvailableUpdate -> "走了走了"
                            UpdateStatus.CheckError, UpdateStatus.DownloadError, UpdateStatus.InstallError -> "算了算了"
                            UpdateStatus.Downloading, UpdateStatus.Installing -> "你已经无路可逃！"
                        }
                    )
                }
            }
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    )
}

enum class UpdateStatus {
    UpdatingInfo, Ready, Downloading, Installing,
    NoAvailableUpdate, CheckError, DownloadError, InstallError
}

private class UpdateStatusProvider : PreviewParameterProvider<UpdateStatus> {
    override val values = UpdateStatus.entries.asSequence()
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun UpdateDialogPreview(
    @PreviewParameter(UpdateStatusProvider::class) updateStatus: UpdateStatus
) {
    BVTheme {
        UpdateDialogContent(
            updateStatus = updateStatus,
            latestReleaseBuild = Release(
                name = "BV-1.0.0",
                body = "测试更新",
                assets = emptyList(),
                tagName = "v1.0.0",
                publishedAt = "2023-10-01T00:00:00Z",
                url = "",
                uploadUrl = "",
                assetsUrl = "",
                author = Release.User(
                    login = "",
                    id = 1,
                    nodeId = "",
                    avatarUrl = "",
                    gravatarId = "",
                    url = "",
                    htmlUrl = "",
                    followersUrl = "",
                    followingUrl = "",
                    gistsUrl = "",
                    starredUrl = "",
                    subscriptionsUrl = "",
                    organizationsUrl = "",
                    reposUrl = "",
                    eventsUrl = "",
                    receivedEventsUrl = "",
                    type = "",
                    siteAdmin = false
                ),
                draft = false,
                htmlUrl = "",
                createdAt = "2023-10-01T00:00:00Z",
                nodeId = "",
                id = 0,
                prerelease = false,
                reactions = null,
                tarballUrl = "",
                targetCommitish = "",
                zipballUrl = "",
            ),
            progress = 0.5f,
            bytesSentTotal = 100L,
            contentLength = 200L,
            onHideDialog = {},
            checkUpdate = {},
            startUpdate = {},
            text = { Text(text = it) },
            button = { enabled, onClick, content ->
                Button(
                    enabled = enabled,
                    onClick = onClick,
                    content = content
                )
            },
            outlinedButton = { enabled, onClick, content ->
                OutlinedButton(
                    enabled = enabled,
                    onClick = onClick,
                    content = content
                )
            }
        )
    }
}