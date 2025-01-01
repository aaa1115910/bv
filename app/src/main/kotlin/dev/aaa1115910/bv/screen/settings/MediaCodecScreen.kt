package dev.aaa1115910.bv.screen.settings

import android.os.Build
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.nativeKeyCode
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Icon
import androidx.tv.material3.ListItem
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.util.CodecInfoData
import dev.aaa1115910.bv.util.CodecMedia
import dev.aaa1115910.bv.util.CodecMode
import dev.aaa1115910.bv.util.CodecType
import dev.aaa1115910.bv.util.CodecUtil
import dev.aaa1115910.bv.util.requestFocus
import dev.aaa1115910.bv.util.swapList
import java.util.Locale

@Composable
fun MediaCodecScreen(
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        val list = CodecUtil.parseCodecs()
        println(list)
    }

    val showLargeTitle by remember { derivedStateOf { true } }
    val titleFontSize by animateFloatAsState(
        targetValue = if (showLargeTitle) 48f else 24f,
        label = "title font size"
    )

    var currentCodecInfoData by remember { mutableStateOf<CodecInfoData?>(null) }
    var focusInNav by remember { mutableStateOf(false) }

    val decoderList = remember { mutableStateListOf<CodecInfoData>() }

    LaunchedEffect(Unit) {
        val list = CodecUtil.parseCodecs().filter { it.type == CodecType.Decoder }
        decoderList.swapList(list)
        currentCodecInfoData = list[0]
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            Box(
                modifier = Modifier.padding(
                    start = 48.dp,
                    top = 24.dp,
                    bottom = 8.dp,
                    end = 48.dp
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.title_activity_media_codec),
                        fontSize = titleFontSize.sp
                    )
                    Text(
                        text = "",
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        }
    ) { innerPadding ->
        Row(
            modifier = Modifier.padding(innerPadding)
        ) {
            MediaCodecListItems(
                modifier = Modifier
                    .onFocusChanged { focusInNav = it.hasFocus }
                    .weight(3f)
                    .fillMaxHeight(),
                codecInfoDataList = decoderList,
                currentCodecInfoData = currentCodecInfoData,
                onCodecInfoDataChanged = { currentCodecInfoData = it },
                isFocusing = focusInNav
            )
            MediaCodecDetails(
                modifier = Modifier
                    .weight(5f)
                    .fillMaxSize(),
                onBackNav = { focusInNav = true },
                currentCodecInfoData = currentCodecInfoData
            )
        }
    }
}

@Composable
fun MediaCodecListItems(
    modifier: Modifier = Modifier,
    codecInfoDataList: List<CodecInfoData>,
    currentCodecInfoData: CodecInfoData?,
    onCodecInfoDataChanged: (CodecInfoData) -> Unit,
    isFocusing: Boolean
) {
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isFocusing) {
        if (isFocusing) focusRequester.requestFocus(scope)
    }

    LaunchedEffect(codecInfoDataList) {
        focusRequester.requestFocus(scope)
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items = codecInfoDataList) { codecInfoData ->
            val buttonModifier = if (currentCodecInfoData == codecInfoData) Modifier
                .focusRequester(focusRequester)
                .fillMaxWidth()
            else Modifier.fillMaxWidth()
            MediaCodecListItem(
                modifier = buttonModifier,
                codecInfoData = codecInfoData,
                onFocus = { onCodecInfoDataChanged(codecInfoData) },
                selected = currentCodecInfoData == codecInfoData
            )
        }
    }
}

@Composable
fun MediaCodecListItem(
    modifier: Modifier = Modifier,
    codecInfoData: CodecInfoData,
    onFocus: () -> Unit,
    onLoseFocus: () -> Unit = {},
    onClick: () -> Unit = {},
    selected: Boolean
) {
    ListItem(
        modifier = modifier
            .onFocusChanged { if (it.hasFocus) onFocus() else onLoseFocus() },
        selected = selected,
        onClick = onClick,
        headlineContent = {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = codecInfoData.name,
                //style = MaterialTheme.typography.titleLarge
            )
        },
        overlineContent = {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 8.dp),
                    text = codecInfoData.mimeType,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    imageVector = when (codecInfoData.media) {
                        CodecMedia.Audio -> Icons.Default.Audiotrack
                        CodecMedia.Video -> Icons.Default.Videocam
                    }, contentDescription = null
                )
            }
        }
    )
}

@Composable
fun MediaCodecDetails(
    modifier: Modifier = Modifier,
    onBackNav: () -> Unit,
    currentCodecInfoData: CodecInfoData?
) {
    val context = LocalContext.current

    if (currentCodecInfoData != null) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .onPreviewKeyEvent {
                    val result = it.key.nativeKeyCode == android.view.KeyEvent.KEYCODE_DPAD_LEFT
                    if (result) onBackNav()
                    result
                },
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(
                horizontal = 48.dp,
                vertical = 24.dp
            )
        ) {
            item {
                MediaCodecDetailItem(
                    title = stringResource(R.string.codec_detail_hs_title),
                    text = when (currentCodecInfoData.mode) {
                        CodecMode.Hardware -> stringResource(R.string.codec_detail_hs_hardware)
                        CodecMode.Software -> stringResource(R.string.codec_detail_hs_software)
                    }
                )
            }
            item {
                MediaCodecDetailItem(
                    title = stringResource(R.string.codec_detail_max_supported_instances_title),
                    text = currentCodecInfoData.maxSupportedInstances.toString()
                )
            }
            if (currentCodecInfoData.media == CodecMedia.Video) {
                item {
                    MediaCodecDetailItem(
                        title = stringResource(R.string.codec_detail_color_formats_title),
                        text = currentCodecInfoData.colorFormats.joinToString()
                    )
                }
            }
            if (currentCodecInfoData.media == CodecMedia.Audio) {
                item {
                    MediaCodecDetailItem(
                        title = stringResource(R.string.codec_detail_audio_bitrate_range_title),
                        text = "${currentCodecInfoData.audioBitrateRange?.first?.toBps()} - ${currentCodecInfoData.audioBitrateRange?.last?.toBps()}"
                    )
                }
            }
            if (currentCodecInfoData.media == CodecMedia.Video) {
                item {
                    MediaCodecDetailItem(
                        title = stringResource(R.string.codec_detail_video_max_bitrate_title),
                        text = currentCodecInfoData.videoBitrateRange?.last?.toBps() ?: "Unknown"
                    )
                }
            }
            if (currentCodecInfoData.media == CodecMedia.Video) {
                item {
                    MediaCodecDetailItem(
                        title = stringResource(R.string.codec_detail_video_frame_range_title),
                        text = "${currentCodecInfoData.videoFrame?.first}fps - ${currentCodecInfoData.videoFrame?.last}fps"
                    )
                }
            }
            if (currentCodecInfoData.media == CodecMedia.Video) {
                item {
                    MediaCodecDetailItem(
                        title = stringResource(R.string.codec_detail_video_frame_supported_title),
                        text = currentCodecInfoData.supportedFrameRates.joinToString("\n") { supportedFrameRate ->
                            when (supportedFrameRate.resolution.second) {
                                360 -> context.getString(R.string.codec_detail_video_resolution_360p)
                                480 -> context.getString(R.string.codec_detail_video_resolution_480p)
                                720 -> context.getString(R.string.codec_detail_video_resolution_720p)
                                1080 -> context.getString(R.string.codec_detail_video_resolution_1080p)
                                1440 -> context.getString(R.string.codec_detail_video_resolution_1440p)
                                2160 -> context.getString(R.string.codec_detail_video_resolution_2160p)
                                4320 -> context.getString(R.string.codec_detail_video_resolution_4320p)
                                else -> context.getString(R.string.codec_detail_video_resolution_unknown)
                            } + ": " +
                                    ("${
                                        String.format(
                                            Locale.getDefault(),
                                            "%.1f",
                                            supportedFrameRate.frameRate.upper
                                        )
                                    }fps"
                                        .takeUnless { supportedFrameRate.unsupported }
                                        ?: context.getString(R.string.codec_detail_video_frame_unsupported))
                        }
                    )
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && currentCodecInfoData.media == CodecMedia.Video) {
                item {
                    MediaCodecDetailItem(
                        title = stringResource(R.string.codec_detail_video_frame_achievable_title),
                        text = currentCodecInfoData.achievableFrameRates.joinToString("\n") { achievableFrameRates ->
                            when (achievableFrameRates.resolution.second) {
                                360 -> context.getString(R.string.codec_detail_video_resolution_360p)
                                480 -> context.getString(R.string.codec_detail_video_resolution_480p)
                                720 -> context.getString(R.string.codec_detail_video_resolution_720p)
                                1080 -> context.getString(R.string.codec_detail_video_resolution_1080p)
                                1440 -> context.getString(R.string.codec_detail_video_resolution_1440p)
                                2160 -> context.getString(R.string.codec_detail_video_resolution_2160p)
                                4320 -> context.getString(R.string.codec_detail_video_resolution_4320p)
                                else -> context.getString(R.string.codec_detail_video_resolution_unknown)
                            } + ": " +
                                    ("${
                                        String.format(
                                            Locale.getDefault(),
                                            "%.1f",
                                            achievableFrameRates.frameRate.upper
                                        )
                                    }fps"
                                        .takeUnless { achievableFrameRates.unsupported }
                                        ?: context.getString(R.string.codec_detail_video_frame_unsupported))
                        }
                    )
                }
            }
        }
    } else {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Empty")
        }
    }
}

@Composable
fun MediaCodecDetailItem(
    modifier: Modifier = Modifier,
    title: String,
    text: String
) {
    var hasFocus by remember { mutableStateOf(false) }

    ListItem(
        modifier = modifier
            .onFocusChanged { hasFocus = it.hasFocus },
        selected = hasFocus,
        onClick = {},
        headlineContent = { Text(text = title) },
        supportingContent = { Text(text = text) }
    )
}

private val previewCodecInfoData = CodecInfoData(
    name = "c2.android.avc.decoder",
    type = CodecType.Decoder,
    mode = CodecMode.Hardware,
    media = CodecMedia.Video,
    mimeType = "video/avc",
    maxSupportedInstances = 1,
    colorFormats = listOf(21, 19, 20),
    audioBitrateRange = 0..0,
    videoBitrateRange = 0..0,
    videoFrame = 0..0,
    supportedFrameRates = emptyList(),
    achievableFrameRates = emptyList()
)

@Preview(device = "id:tv_1080p")
@Composable
private fun MediaCodecListItemPreview() {
    BVTheme {
        MediaCodecListItem(
            codecInfoData = previewCodecInfoData,
            onFocus = {},
            selected = false
        )
    }
}

@Preview(device = "id:tv_1080p")
@Composable
private fun MediaCodecDetailsPreview() {
    BVTheme {
        MediaCodecDetails(
            currentCodecInfoData = previewCodecInfoData,
            onBackNav = {}
        )
    }
}

private fun Int.toBps(): String {
    return when {
        this >= 1000000 -> "${this / 1000000} Mbps"
        this >= 1000 -> "${this / 1000} Kbps"
        else -> "$this bps"
    }
}