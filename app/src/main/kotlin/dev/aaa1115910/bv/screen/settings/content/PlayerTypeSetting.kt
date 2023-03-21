package dev.aaa1115910.bv.screen.settings.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.items
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import dev.aaa1115910.bv.component.LibVLCDownloaderDialog
import dev.aaa1115910.bv.entity.PlayerType
import dev.aaa1115910.bv.screen.settings.SettingsMenuNavItem
import dev.aaa1115910.bv.screen.settings.SettingsMenuSelectItem
import dev.aaa1115910.bv.util.LibVLCUtil
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.toast

@Composable
fun PlayerTypeSetting(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedPlayerType by remember { mutableStateOf(Prefs.playerType) }
    var showLibVLCDownloaderDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!LibVLCUtil.existLibs()) {
            "LibVLC not found".toast(context)
            selectedPlayerType = PlayerType.ExoPlayer
            Prefs.playerType = PlayerType.ExoPlayer
        }
    }

    Box(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = SettingsMenuNavItem.PlayerType.getDisplayName(context),
                style = MaterialTheme.typography.displaySmall
            )
            Spacer(modifier = Modifier.height(12.dp))
            TvLazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items = PlayerType.values()) { playerType ->
                    SettingsMenuSelectItem(
                        text = playerType.name,
                        selected = selectedPlayerType == playerType,
                        onClick = {
                            selectedPlayerType = playerType
                            Prefs.playerType = playerType

                            if (playerType == PlayerType.VLC && !LibVLCUtil.existLibs()) {
                                showLibVLCDownloaderDialog = true
                            }
                        }
                    )
                }
            }
        }
    }

    LibVLCDownloaderDialog(
        show = showLibVLCDownloaderDialog,
        onHideDialog = {
            showLibVLCDownloaderDialog = false
            if (!LibVLCUtil.existLibs()) {
                selectedPlayerType = PlayerType.ExoPlayer
                Prefs.playerType = PlayerType.ExoPlayer
            }
        }
    )
}
