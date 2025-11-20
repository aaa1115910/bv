package dev.aaa1115910.bv.player.tv.controller

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import dev.aaa1115910.biliapi.entity.video.Subtitle
import dev.aaa1115910.bv.player.entity.Audio
import dev.aaa1115910.bv.player.entity.DanmakuType
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerConfigData
import dev.aaa1115910.bv.player.entity.PlayMode
import dev.aaa1115910.bv.player.entity.Resolution
import dev.aaa1115910.bv.player.entity.VideoAspectRatio
import dev.aaa1115910.bv.player.entity.VideoCodec
import dev.aaa1115910.bv.util.requestFocus
import kotlinx.coroutines.delay

enum class BottomMenuItem(val label: String) {
    RESOLUTION("清晰度"),
    CODEC("视频编码"),
    ASPECT_RATIO("画面比例"),
    PLAY_SPEED("播放速度"),
    AUDIO("音频品质"),
    DANMAKU_SWITCH("弹幕开关"),
    DANMAKU_SIZE("弹幕大小"),
    DANMAKU_OPACITY("弹幕透明度"),
    DANMAKU_AREA("弹幕区域"),
    SUBTITLE("字幕"),
    SUBTITLE_SIZE("字幕大小"),
    PLAY_MODE("播放模式")
}

@Composable
fun BottomMenuController(
    modifier: Modifier = Modifier,
    show: Boolean,
    onResolutionChange: (Resolution) -> Unit = {},
    onCodecChange: (VideoCodec) -> Unit = {},
    onAspectRatioChange: (VideoAspectRatio) -> Unit = {},
    onPlaySpeedChange: (Float) -> Unit = {},
    onAudioChange: (Audio) -> Unit = {},
    onDanmakuSwitchChange: (List<DanmakuType>) -> Unit = {},
    onDanmakuSizeChange: (Float) -> Unit = {},
    onDanmakuOpacityChange: (Float) -> Unit = {},
    onDanmakuAreaChange: (Float) -> Unit = {},
    onSubtitleChange: (Subtitle) -> Unit = {},
    onSubtitleSizeChange: (TextUnit) -> Unit = {},
    onPlayModeChange: (PlayMode) -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(show) {
        if (show) {
            delay(100)
            focusRequester.requestFocus(scope)
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedVisibility(
            visible = show,
            enter = expandVertically(expandFrom = Alignment.Bottom),
            exit = shrinkVertically(shrinkTowards = Alignment.Bottom)
        ) {
            BottomMenuContent(
                focusRequester = focusRequester,
                onResolutionChange = onResolutionChange,
                onCodecChange = onCodecChange,
                onAspectRatioChange = onAspectRatioChange,
                onPlaySpeedChange = onPlaySpeedChange,
                onAudioChange = onAudioChange,
                onDanmakuSwitchChange = onDanmakuSwitchChange,
                onDanmakuSizeChange = onDanmakuSizeChange,
                onDanmakuOpacityChange = onDanmakuOpacityChange,
                onDanmakuAreaChange = onDanmakuAreaChange,
                onSubtitleChange = onSubtitleChange,
                onSubtitleSizeChange = onSubtitleSizeChange,
                onPlayModeChange = onPlayModeChange
            )
        }
    }
}

@Composable
private fun BottomMenuContent(
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester,
    onResolutionChange: (Resolution) -> Unit,
    onCodecChange: (VideoCodec) -> Unit,
    onAspectRatioChange: (VideoAspectRatio) -> Unit,
    onPlaySpeedChange: (Float) -> Unit,
    onAudioChange: (Audio) -> Unit,
    onDanmakuSwitchChange: (List<DanmakuType>) -> Unit,
    onDanmakuSizeChange: (Float) -> Unit,
    onDanmakuOpacityChange: (Float) -> Unit,
    onDanmakuAreaChange: (Float) -> Unit,
    onSubtitleChange: (Subtitle) -> Unit,
    onSubtitleSizeChange: (TextUnit) -> Unit,
    onPlayModeChange: (PlayMode) -> Unit
) {
    val context = LocalContext.current
    val configData = LocalVideoPlayerConfigData.current

    val menuItems = remember {
        listOf(
            BottomMenuItem.PLAY_SPEED,
            BottomMenuItem.DANMAKU_SWITCH,
            BottomMenuItem.SUBTITLE,
            BottomMenuItem.RESOLUTION,
            BottomMenuItem.CODEC,
            BottomMenuItem.ASPECT_RATIO,
            BottomMenuItem.AUDIO,
            BottomMenuItem.DANMAKU_SIZE,
            BottomMenuItem.DANMAKU_OPACITY,
            BottomMenuItem.DANMAKU_AREA,
            BottomMenuItem.SUBTITLE_SIZE,
            BottomMenuItem.PLAY_MODE
        )
    }

    var selectedIndex by remember { mutableIntStateOf(0) }
    var hasFocus by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    LaunchedEffect(selectedIndex) {
        listState.animateScrollToItem(selectedIndex)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(0.33f)
            .background(Color.Black.copy(alpha = 0.6f))
            .focusRequester(focusRequester)
            .onFocusChanged { hasFocus = it.hasFocus }
            .focusable()
            .onPreviewKeyEvent { event ->
                when (event.key) {
                    Key.DirectionUp -> {
                        if (event.type == KeyEventType.KeyUp) {
                            selectedIndex = (selectedIndex - 1 + menuItems.size) % menuItems.size
                        }
                        true
                    }

                    Key.DirectionDown -> {
                        if (event.type == KeyEventType.KeyUp) {
                            selectedIndex = (selectedIndex + 1) % menuItems.size
                        }
                        true
                    }

                    Key.DirectionLeft -> {
                        if (event.type == KeyEventType.KeyUp) {
                            handleValueChange(
                                menuItems[selectedIndex],
                                -1,
                                configData,
                                onResolutionChange,
                                onCodecChange,
                                onAspectRatioChange,
                                onPlaySpeedChange,
                                onAudioChange,
                                onDanmakuSwitchChange,
                                onDanmakuSizeChange,
                                onDanmakuOpacityChange,
                                onDanmakuAreaChange,
                                onSubtitleChange,
                                onSubtitleSizeChange,
                                onPlayModeChange
                            )
                        }
                        true
                    }

                    Key.DirectionRight -> {
                        if (event.type == KeyEventType.KeyUp) {
                            handleValueChange(
                                menuItems[selectedIndex],
                                1,
                                configData,
                                onResolutionChange,
                                onCodecChange,
                                onAspectRatioChange,
                                onPlaySpeedChange,
                                onAudioChange,
                                onDanmakuSwitchChange,
                                onDanmakuSizeChange,
                                onDanmakuOpacityChange,
                                onDanmakuAreaChange,
                                onSubtitleChange,
                                onSubtitleSizeChange,
                                onPlayModeChange
                            )
                        }
                        true
                    }

                    else -> false
                }
            }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(menuItems) { index, item ->
                BottomMenuItemRow(
                    item = item,
                    isSelected = index == selectedIndex && hasFocus,
                    configData = configData
                )
            }
        }
    }
}

@Composable
private fun BottomMenuItemRow(
    item: BottomMenuItem,
    isSelected: Boolean,
    configData: dev.aaa1115910.bv.player.entity.VideoPlayerConfigData
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isSelected) Color.White.copy(alpha = 0.2f)
                else Color.Transparent
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.label,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f),
            modifier = Modifier.width(120.dp)
        )

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            val options = getAvailableOptions(item, configData)
            val currentValue = getCurrentValue(item, configData)
            val currentIndex = options.indexOf(currentValue).coerceAtLeast(0)

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 显示前面最多5个选项
                for (i in 5 downTo 1) {
                    val index = currentIndex - i
                    if (index >= 0) {
                        Text(
                            text = options[index],
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.4f),
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                }

                // 当前选中的选项（居中，放大，高亮）
                Text(
                    text = currentValue,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF00A0FF),
                    fontSize = 18.sp
                )

                // 显示后面最多5个选项
                for (i in 1..5) {
                    val index = currentIndex + i
                    if (index < options.size) {
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = options[index],
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.4f),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

private fun getCurrentValue(
    item: BottomMenuItem,
    configData: dev.aaa1115910.bv.player.entity.VideoPlayerConfigData
): String {
    return when (item) {
        BottomMenuItem.RESOLUTION -> configData.currentResolution.name
        BottomMenuItem.CODEC -> configData.currentVideoCodec.name
        BottomMenuItem.ASPECT_RATIO -> when (configData.currentVideoAspectRatio) {
            VideoAspectRatio.Default -> "默认"
            VideoAspectRatio.FourToThree -> "4:3"
            VideoAspectRatio.SixteenToNine -> "16:9"
        }

        BottomMenuItem.PLAY_SPEED -> "${configData.currentVideoSpeed}x"
        BottomMenuItem.AUDIO -> configData.currentAudio.name
        BottomMenuItem.DANMAKU_SWITCH -> if (configData.currentDanmakuEnabledList.contains(
                DanmakuType.All
            ) || configData.currentDanmakuEnabledList.isNotEmpty()
        ) "开启" else "关闭"

        BottomMenuItem.DANMAKU_SIZE -> "${(configData.currentDanmakuScale * 100).toInt()}%"
        BottomMenuItem.DANMAKU_OPACITY -> "${(configData.currentDanmakuOpacity * 100).toInt()}%"
        BottomMenuItem.DANMAKU_AREA -> "${(configData.currentDanmakuArea * 100).toInt()}%"
        BottomMenuItem.SUBTITLE -> {
            val subtitle =
                configData.availableSubtitleTracks.find { it.id == configData.currentSubtitleId }
            subtitle?.langDoc ?: "无字幕"
        }

        BottomMenuItem.SUBTITLE_SIZE -> "${configData.currentSubtitleFontSize.value.toInt()}sp"
        BottomMenuItem.PLAY_MODE -> when (configData.currentPlayMode) {
            PlayMode.Single -> "单集播放"
            PlayMode.Sequential -> "顺序播放"
            PlayMode.SingleLoop -> "单集循环"
            PlayMode.ListLoop -> "列表循环"
        }
    }
}

private fun getAvailableOptions(
    item: BottomMenuItem,
    configData: dev.aaa1115910.bv.player.entity.VideoPlayerConfigData
): List<String> {
    return when (item) {
        BottomMenuItem.RESOLUTION -> configData.availableResolutions.map { it.name }
        BottomMenuItem.CODEC -> configData.availableVideoCodec.map { it.name }
        BottomMenuItem.ASPECT_RATIO -> listOf("默认", "4:3", "16:9")
        BottomMenuItem.PLAY_SPEED -> listOf("0.5x", "0.75x", "1.0x", "1.25x", "1.5x", "2.0x")
        BottomMenuItem.AUDIO -> configData.availableAudio.map { it.name }
        BottomMenuItem.DANMAKU_SWITCH -> listOf("关闭", "开启")
        BottomMenuItem.DANMAKU_SIZE -> listOf(
            "50%",
            "60%",
            "70%",
            "80%",
            "90%",
            "100%",
            "110%",
            "120%",
            "130%",
            "140%",
            "150%",
            "160%",
            "170%",
            "180%",
            "190%",
            "200%"
        )

        BottomMenuItem.DANMAKU_OPACITY -> listOf(
            "0%",
            "10%",
            "20%",
            "30%",
            "40%",
            "50%",
            "60%",
            "70%",
            "80%",
            "90%",
            "100%"
        )

        BottomMenuItem.DANMAKU_AREA -> listOf(
            "25%",
            "35%",
            "45%",
            "55%",
            "65%",
            "75%",
            "85%",
            "95%",
            "100%"
        )

        BottomMenuItem.SUBTITLE -> configData.availableSubtitleTracks.map { it.langDoc }
        BottomMenuItem.SUBTITLE_SIZE -> listOf("16sp", "20sp", "24sp", "28sp", "32sp", "36sp")
        BottomMenuItem.PLAY_MODE -> listOf("单集播放", "顺序播放", "单集循环", "列表循环")
    }
}

private fun handleValueChange(
    item: BottomMenuItem,
    direction: Int,
    configData: dev.aaa1115910.bv.player.entity.VideoPlayerConfigData,
    onResolutionChange: (Resolution) -> Unit,
    onCodecChange: (VideoCodec) -> Unit,
    onAspectRatioChange: (VideoAspectRatio) -> Unit,
    onPlaySpeedChange: (Float) -> Unit,
    onAudioChange: (Audio) -> Unit,
    onDanmakuSwitchChange: (List<DanmakuType>) -> Unit,
    onDanmakuSizeChange: (Float) -> Unit,
    onDanmakuOpacityChange: (Float) -> Unit,
    onDanmakuAreaChange: (Float) -> Unit,
    onSubtitleChange: (Subtitle) -> Unit,
    onSubtitleSizeChange: (TextUnit) -> Unit,
    onPlayModeChange: (PlayMode) -> Unit
) {
    when (item) {
        BottomMenuItem.PLAY_SPEED -> {
            val speeds = listOf(0.5f, 0.75f, 1f, 1.25f, 1.5f, 2f)
            val currentIndex = speeds.indexOf(configData.currentVideoSpeed)
            val newIndex = (currentIndex + direction).coerceIn(0, speeds.size - 1)
            onPlaySpeedChange(speeds[newIndex])
        }

        BottomMenuItem.DANMAKU_SWITCH -> {
            val isEnabled =
                configData.currentDanmakuEnabledList.contains(DanmakuType.All) || configData.currentDanmakuEnabledList.isNotEmpty()
            if (isEnabled) {
                onDanmakuSwitchChange(listOf())
            } else {
                onDanmakuSwitchChange(listOf(DanmakuType.All))
            }
        }

        BottomMenuItem.SUBTITLE -> {
            val subtitles = configData.availableSubtitleTracks
            if (subtitles.isNotEmpty()) {
                val currentIndex = subtitles.indexOfFirst { it.id == configData.currentSubtitleId }
                val newIndex = (currentIndex + direction + subtitles.size) % subtitles.size
                onSubtitleChange(subtitles[newIndex])
            }
        }

        BottomMenuItem.RESOLUTION -> {
            val resolutions = configData.availableResolutions
            val currentIndex = resolutions.indexOf(configData.currentResolution)
            val newIndex = (currentIndex + direction + resolutions.size) % resolutions.size
            onResolutionChange(resolutions[newIndex])
        }

        BottomMenuItem.CODEC -> {
            val codecs = configData.availableVideoCodec
            val currentIndex = codecs.indexOf(configData.currentVideoCodec)
            val newIndex = (currentIndex + direction + codecs.size) % codecs.size
            onCodecChange(codecs[newIndex])
        }

        BottomMenuItem.ASPECT_RATIO -> {
            val ratios = VideoAspectRatio.entries
            val currentIndex = ratios.indexOf(configData.currentVideoAspectRatio)
            val newIndex = (currentIndex + direction + ratios.size) % ratios.size
            onAspectRatioChange(ratios[newIndex])
        }

        BottomMenuItem.AUDIO -> {
            val audios = configData.availableAudio
            val currentIndex = audios.indexOf(configData.currentAudio)
            val newIndex = (currentIndex + direction + audios.size) % audios.size
            onAudioChange(audios[newIndex])
        }

        BottomMenuItem.DANMAKU_SIZE -> {
            val currentPercentage = (configData.currentDanmakuScale * 100).toInt()
            val options =
                listOf(50, 60, 70, 80, 90, 100, 110, 120, 130, 140, 150, 160, 170, 180, 190, 200)
            val currentIndex = options.indexOfFirst { it == currentPercentage }
                .let { if (it == -1) options.indexOfFirst { it >= currentPercentage } else it }
            val newIndex = (currentIndex + direction).coerceIn(0, options.size - 1)
            onDanmakuSizeChange(options[newIndex] / 100f)
        }

        BottomMenuItem.DANMAKU_OPACITY -> {
            val currentPercentage = (configData.currentDanmakuOpacity * 100).toInt()
            val options = listOf(0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100)
            val currentIndex = options.indexOfFirst { it == currentPercentage }
                .let { if (it == -1) options.indexOfFirst { it >= currentPercentage } else it }
            val newIndex = (currentIndex + direction).coerceIn(0, options.size - 1)
            onDanmakuOpacityChange(options[newIndex] / 100f)
        }

        BottomMenuItem.DANMAKU_AREA -> {
            val currentPercentage = (configData.currentDanmakuArea * 100).toInt()
            val options = listOf(25, 35, 45, 55, 65, 75, 85, 95, 100)
            val currentIndex = options.indexOfFirst { it == currentPercentage }
                .let { if (it == -1) options.indexOfFirst { it >= currentPercentage } else it }
            val newIndex = (currentIndex + direction).coerceIn(0, options.size - 1)
            onDanmakuAreaChange(options[newIndex] / 100f)
        }

        BottomMenuItem.SUBTITLE_SIZE -> {
            val sizes = listOf(16.sp, 20.sp, 24.sp, 28.sp, 32.sp, 36.sp)
            val currentIndex =
                sizes.indexOfFirst { it.value == configData.currentSubtitleFontSize.value }
            val newIndex = (currentIndex + direction).coerceIn(0, sizes.size - 1)
            onSubtitleSizeChange(sizes[newIndex])
        }

        BottomMenuItem.PLAY_MODE -> {
            val modes = PlayMode.entries
            val currentIndex = modes.indexOf(configData.currentPlayMode)
            val newIndex = (currentIndex + direction + modes.size) % modes.size
            onPlayModeChange(modes[newIndex])
        }
    }
}
