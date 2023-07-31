package dev.aaa1115910.bv.screen.settings.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import dev.aaa1115910.bv.BuildConfig
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.component.settings.UpdateDialog
import dev.aaa1115910.bv.network.AppCenterApi
import dev.aaa1115910.bv.screen.settings.SettingsMenuButton
import dev.aaa1115910.bv.screen.settings.SettingsMenuNavItem
import dev.aaa1115910.bv.ui.theme.BVTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AboutSetting(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var showUpdateDialog by remember { mutableStateOf(false) }
    var latestVersionName by remember { mutableStateOf("Loading...") }

    LaunchedEffect(Unit) {
        launch(Dispatchers.IO) {
            runCatching {
                val latestVersion = AppCenterApi.getLatestVersion()
                latestVersionName = latestVersion.second
            }.onFailure {
                latestVersionName = "Error"
            }
        }
    }

    Box(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = SettingsMenuNavItem.About.getDisplayName(context),
                style = MaterialTheme.typography.displaySmall
            )
            Spacer(modifier = Modifier.height(12.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(
                        R.string.settings_version_current_version,
                        BuildConfig.VERSION_NAME
                    )
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(
                            R.string.settings_version_latest_version,
                            latestVersionName
                        )
                    )
                }
            }

            var isUpdateButtonHasFocus by remember { mutableStateOf(false) }
            SettingsMenuButton(
                text = stringResource(R.string.settings_version_check_update_button),
                selected = isUpdateButtonHasFocus,
                onFocus = { isUpdateButtonHasFocus = true },
                onLoseFocus = { isUpdateButtonHasFocus = false },
                onClick = { showUpdateDialog = true }
            )
        }
        Text(
            modifier = Modifier.align(Alignment.BottomCenter),
            text = "https://github.com/aaa1115910/bv"
        )
    }

    UpdateDialog(
        show = showUpdateDialog,
        onHideDialog = { showUpdateDialog = false }
    )
}

@Preview
@Composable
private fun AboutSettingPreview() {
    BVTheme {
        AboutSetting()
    }
}