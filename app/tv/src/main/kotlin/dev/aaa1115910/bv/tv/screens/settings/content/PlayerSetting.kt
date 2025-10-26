package dev.aaa1115910.bv.tv.screens.settings.content

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ListItem
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.RadioButton
import androidx.tv.material3.Text
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.player.entity.Audio
import dev.aaa1115910.bv.player.entity.Resolution
import dev.aaa1115910.bv.player.entity.VideoCodec
import dev.aaa1115910.bv.tv.component.settings.SettingListItem
import dev.aaa1115910.bv.tv.component.settings.SettingSwitchListItem
import dev.aaa1115910.bv.tv.component.settings.SettingNumberListItem
import dev.aaa1115910.bv.tv.component.TvAlertDialog
import dev.aaa1115910.bv.tv.screens.settings.SettingsMenuNavItem
import dev.aaa1115910.bv.util.Prefs

@Composable
fun PlayerSetting(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var selectedResolution by remember { mutableStateOf(Prefs.defaultQuality) }
    var showResolutionDialog by remember { mutableStateOf(false) }
    var selectedVideoCodec by remember { mutableStateOf(Prefs.defaultVideoCodec) }
    var showVideoCodecDialog by remember { mutableStateOf(false) }
    var selectedAudio by remember { mutableStateOf(Prefs.defaultAudio) }
    var showAudioDialog by remember { mutableStateOf(false) }
    var enableFfmpegAudioRenderer by remember { mutableStateOf(Prefs.enableFfmpegAudioRenderer) }
    var showUGCVideoInfo by remember { mutableStateOf(Prefs.showUGCVideoInfo) }
    var playerShowBottomProgressBar by remember { mutableStateOf(Prefs.playerShowBottomProgressBar) }
    var playerShowDebugInfo by remember { mutableStateOf(Prefs.playerShowDebugInfo) }
    var portraitVideoQualityLimitMax1080P by remember { mutableStateOf(Prefs.portraitVideoQualityLimitMax1080P) }
    var playerAutoPlayNextVideo by remember { mutableStateOf(Prefs.playerAutoPlayNextVideo) }
    var playerExitWhenAllIsPlayed by remember { mutableStateOf(Prefs.playerExitWhenAllIsPlayed) }
    var defaultPlaybackSpeed by remember { mutableDoubleStateOf(Prefs.defaultPlaySpeed.toDouble()) }
    var playerSeekForwardStep by remember { mutableDoubleStateOf(Prefs.playerSeekForwardStep.toDouble()) }
    var playerSeekBackwardStep by remember { mutableDoubleStateOf(Prefs.playerSeekBackwardStep.toDouble()) }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = SettingsMenuNavItem.Player.getDisplayName(context),
            style = MaterialTheme.typography.displaySmall
        )
        Spacer(modifier = Modifier.height(12.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                SettingListItem(
                    title = stringResource(R.string.settings_item_resolution),
                    supportText = stringResource(R.string.settings_item_resolution),
                    valueText = selectedResolution.getDisplayName(context),
                    onClick = { showResolutionDialog = true }
                )
            }
            item {
                SettingListItem(
                    title = stringResource(R.string.settings_item_codec),
                    supportText = stringResource(R.string.settings_item_codec),
                    valueText = selectedVideoCodec.getDisplayName(context),
                    onClick = { showVideoCodecDialog = true }
                )
            }
            item {
                SettingListItem(
                    title = stringResource(R.string.settings_item_audio),
                    supportText = stringResource(R.string.settings_item_codec),
                    valueText = selectedAudio.getDisplayName(context),
                    onClick = { showAudioDialog = true }
                )
            }
            item {
                SettingSwitchListItem(
                    title = stringResource(R.string.settings_other_ffmpeg_audio_renderer_title),
                    supportText = stringResource(R.string.settings_other_ffmpeg_audio_renderer_text),
                    checked = enableFfmpegAudioRenderer,
                    onCheckedChange = {
                        enableFfmpegAudioRenderer = it
                        Prefs.enableFfmpegAudioRenderer = it
                    }
                )
            }
            item {
                SettingSwitchListItem(
                    title = stringResource(R.string.settings_show_ugc_video_info_title),
                    supportText = stringResource(R.string.settings_show_ugc_video_info_text),
                    checked = showUGCVideoInfo,
                    onCheckedChange = {
                        showUGCVideoInfo = it
                        Prefs.showUGCVideoInfo = it
                    }
                )
            }
            item {
                SettingSwitchListItem(
                    title = stringResource(R.string.settings_player_show_bottom_progress_bar_title),
                    supportText = stringResource(R.string.settings_player_show_bottom_progress_bar_text),
                    checked = playerShowBottomProgressBar,
                    onCheckedChange = {
                        playerShowBottomProgressBar = it
                        Prefs.playerShowBottomProgressBar = it
                    }
                )
            }
            item {
                SettingSwitchListItem(
                    title = stringResource(R.string.settings_player_show_debug_info_title),
                    supportText = stringResource(R.string.settings_player_show_debug_info_text),
                    checked = playerShowDebugInfo,
                    onCheckedChange = {
                        playerShowDebugInfo = it
                        Prefs.playerShowDebugInfo = it
                    }
                )
            }
            item {
                SettingSwitchListItem(
                    title = stringResource(R.string.settings_other_portrait_video_quality_title),
                    supportText = stringResource(R.string.settings_other_portrait_video_quality_text),
                    checked = portraitVideoQualityLimitMax1080P,
                    onCheckedChange = {
                        portraitVideoQualityLimitMax1080P = it
                        Prefs.portraitVideoQualityLimitMax1080P = it
                    }
                )
            }
            item {
                SettingSwitchListItem(
                    title = stringResource(R.string.settings_player_auto_play_next_video_title),
                    supportText = stringResource(R.string.settings_player_auto_play_next_video_text),
                    checked = playerAutoPlayNextVideo,
                    onCheckedChange = {
                        playerAutoPlayNextVideo = it
                        Prefs.playerAutoPlayNextVideo = it
                    }
                )
            }
            item {
                SettingSwitchListItem(
                    title = stringResource(R.string.settings_player_exit_when_all_is_played_title),
                    supportText = stringResource(R.string.settings_player_exit_when_all_is_played_text),
                    checked = playerExitWhenAllIsPlayed,
                    onCheckedChange = {
                        playerExitWhenAllIsPlayed = it
                        Prefs.playerExitWhenAllIsPlayed = it
                    }
                )
            }
            item {
                SettingNumberListItem(
                    title = stringResource(R.string.settings_player_default_playback_speed_title),
                    supportText = stringResource(R.string.settings_player_default_playback_speed_text),
                    value = defaultPlaybackSpeed,
                    minValue = 0.25,
                    maxValue = 2.5,
                    isInteger = false,
                    step = 0.25,
                    onValueChange = {
                        defaultPlaybackSpeed = it
                        Prefs.defaultPlaySpeed = it.toFloat()
                    }
                )
            }
            item {
                SettingNumberListItem(
                    title = stringResource(R.string.settings_player_seek_forward_step_title),
                    supportText = stringResource(R.string.settings_player_seek_forward_step_text),
                    value = playerSeekForwardStep,
                    minValue = 5.0,
                    maxValue = 30.0,
                    isInteger = true,
                    step = 1.0,
                    onValueChange = {
                        playerSeekForwardStep = it
                        Prefs.playerSeekForwardStep = it.toInt()
                    }
                )
            }
            item {
                SettingNumberListItem(
                    title = stringResource(R.string.settings_player_seek_backward_step_title),
                    supportText = stringResource(R.string.settings_player_seek_backward_step_text),
                    value = playerSeekBackwardStep,
                    minValue = 5.0,
                    maxValue = 30.0,
                    isInteger = true,
                    step = 1.0,
                    onValueChange = {
                        playerSeekBackwardStep = it
                        Prefs.playerSeekBackwardStep = it.toInt()
                    }
                )
            }
        }
    }

    SelectionDialog(
        show = showResolutionDialog,
        title = stringResource(R.string.settings_item_resolution),
        onHideDialog = { showResolutionDialog = false },
        options = Resolution.entries.reversed(),
        getDisplayName = { item, ctx -> item.getDisplayName(ctx) },
        value = selectedResolution,
        onChange = {
            Prefs.defaultQuality = it
            selectedResolution = it
        }
    )

    SelectionDialog(
        show = showVideoCodecDialog,
        title = stringResource(R.string.settings_item_codec),
        onHideDialog = { showVideoCodecDialog = false },
        options = VideoCodec.entries.filter { it != VideoCodec.DVH1 && it != VideoCodec.HVC1 },
        getDisplayName = { item, ctx -> item.getDisplayName(ctx) },
        value = selectedVideoCodec,
        onChange = {
            Prefs.defaultVideoCodec = it
            selectedVideoCodec = it
        }
    )

    SelectionDialog(
        show = showAudioDialog,
        title = stringResource(R.string.settings_item_audio),
        onHideDialog = { showAudioDialog = false },
        options = Audio.entries,
        getDisplayName = { item, ctx -> item.getDisplayName(ctx) },
        value = selectedAudio,
        onChange = {
            Prefs.defaultAudio = it
            selectedAudio = it
        }
    )
}


@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun <T> SelectionDialog(
    modifier: Modifier = Modifier,
    show: Boolean,
    title: String = "",
    options: List<T>,
    getDisplayName: (T, Context) -> String,
    value: T,
    onChange: (T) -> Unit,
    onHideDialog: () -> Unit
) {
    if (show) {
        val context = LocalContext.current
        val configuration = LocalConfiguration.current
        val maxHeight = (configuration.screenHeightDp * 0.5).dp
        TvAlertDialog(
            modifier = modifier,
            onDismissRequest = { onHideDialog() },
            title = { if (title.isNotEmpty()) Text(text = title) },
            text = {
                Column(
                    modifier = Modifier
                        .heightIn(max = maxHeight)
                        .verticalScroll(rememberScrollState())
                ) {
                    options.forEach {
                        ListItem(
                            selected = value == it,
                            onClick = { onChange(it) },
                            headlineContent = {
                                Text(text = getDisplayName(it, context))
                            },
                            trailingContent = {
                                RadioButton(
                                    selected = value == it,
                                    onClick = null
                                )
                            }
                        )
                    }
                }
            },
            confirmButton = {}
        )
    }
}