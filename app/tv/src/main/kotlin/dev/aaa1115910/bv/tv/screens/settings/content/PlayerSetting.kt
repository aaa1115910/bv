package dev.aaa1115910.bv.tv.screens.settings.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.player.entity.Audio
import dev.aaa1115910.bv.player.entity.PortraitVideoFixMode
import dev.aaa1115910.bv.player.entity.PlayerLoadNextAction
import dev.aaa1115910.bv.player.entity.Resolution
import dev.aaa1115910.bv.player.entity.VideoCodec
import dev.aaa1115910.bv.tv.component.settings.SettingListItemWithDialog
import dev.aaa1115910.bv.tv.component.settings.SettingSwitchListItem
import dev.aaa1115910.bv.tv.component.settings.SettingNumberListItem
import dev.aaa1115910.bv.tv.screens.settings.SettingsMenuNavItem
import dev.aaa1115910.bv.util.Prefs

@Composable
fun PlayerSetting(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var selectedResolution by remember { mutableStateOf(Prefs.defaultQuality) }
    var selectedVideoCodec by remember { mutableStateOf(Prefs.defaultVideoCodec) }
    var selectedAudio by remember { mutableStateOf(Prefs.defaultAudio) }
    var enableFfmpegAudioRenderer by remember { mutableStateOf(Prefs.enableFfmpegAudioRenderer) }
    var showUGCVideoInfo by remember { mutableStateOf(Prefs.showUGCVideoInfo) }
    var playerShowBottomProgressBar by remember { mutableStateOf(Prefs.playerShowBottomProgressBar) }
    var playerShowDebugInfo by remember { mutableStateOf(Prefs.playerShowDebugInfo) }
    var playerExitWhenAllIsPlayed by remember { mutableStateOf(Prefs.playerExitWhenAllIsPlayed) }
    var playerLoadNextAction by remember { mutableStateOf(Prefs.playerLoadNextAction) }
    var defaultPlaybackSpeed by remember { mutableDoubleStateOf(Prefs.defaultPlaySpeed.toDouble()) }
    var playerSeekForwardStep by remember { mutableDoubleStateOf(Prefs.playerSeekForwardStep.toDouble()) }
    var playerSeekBackwardStep by remember { mutableDoubleStateOf(Prefs.playerSeekBackwardStep.toDouble()) }
    var portraitVideoFixMode by remember { mutableStateOf(Prefs.portraitVideoFixMode) }

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
                SettingListItemWithDialog(
                    title = stringResource(R.string.settings_item_resolution),
                    supportText = stringResource(R.string.settings_item_resolution),
                    options = Resolution.entries.reversed(),
                    getDisplayName = { item, ctx -> item.getDisplayName(ctx) },
                    value = selectedResolution,
                    onValueChange = {
                        Prefs.defaultQuality = it
                        selectedResolution = it
                    }
                )
            }
            item {
                SettingListItemWithDialog(
                    title = stringResource(R.string.settings_item_codec),
                    supportText = stringResource(R.string.settings_item_codec),
                    options = VideoCodec.entries.filter { it != VideoCodec.DVH1 && it != VideoCodec.HVC1 },
                    getDisplayName = { item, ctx -> item.getDisplayName(ctx) },
                    value = selectedVideoCodec,
                    onValueChange = {
                        Prefs.defaultVideoCodec = it
                        selectedVideoCodec = it
                    }
                )
            }
            item {
                SettingListItemWithDialog(
                    title = stringResource(R.string.settings_item_audio),
                    supportText = stringResource(R.string.settings_item_codec),
                    options = Audio.entries,
                    getDisplayName = { item, ctx -> item.getDisplayName(ctx) },
                    value = selectedAudio,
                    onValueChange = {
                        Prefs.defaultAudio = it
                        selectedAudio = it
                    }
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
                SettingListItemWithDialog(
                    title = stringResource(R.string.settings_portrait_video_fix_mode_title),
                    supportText = stringResource(R.string.settings_portrait_video_fix_mode_text),
                    options = PortraitVideoFixMode.entries,
                    getDisplayName = { item, ctx -> item.displayName(ctx) },
                    value = portraitVideoFixMode,
                    onValueChange = {
                        portraitVideoFixMode = it
                        Prefs.portraitVideoFixMode = it
                    }
                )
            }
            item {
                SettingListItemWithDialog(
                    title = stringResource(R.string.settings_player_load_next_action_title),
                    supportText = stringResource(R.string.settings_player_load_next_action_text),
                    options = PlayerLoadNextAction.entries,
                    getDisplayName = { item, ctx -> item.displayName(ctx) },
                    value = playerLoadNextAction,
                    onValueChange = {
                        playerLoadNextAction = it
                        Prefs.playerLoadNextAction = it
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

}