package dev.aaa1115910.bv.screen.settings.content

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
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
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import dev.aaa1115910.biliapi.http.ProxyHttpApi
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.activities.settings.SpeedTestActivity
import dev.aaa1115910.bv.component.settings.SettingListItem
import dev.aaa1115910.bv.component.settings.SettingSwitchListItem
import dev.aaa1115910.bv.screen.settings.SettingsMenuNavItem
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.util.Prefs

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun NetworkSetting(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var enableProxy by remember { mutableStateOf(Prefs.enableProxy) }
    var proxyServer by remember { mutableStateOf(Prefs.proxyServer) }
    var showProxyServerEditDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
    ) {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = SettingsMenuNavItem.Network.getDisplayName(context),
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
                    Column {
                        SettingSwitchListItem(
                            title = stringResource(R.string.settings_network_enable_proxy_title),
                            supportText = stringResource(R.string.settings_network_enable_proxy_text),
                            checked = Prefs.enableProxy,
                            onCheckedChange = { enable ->
                                enableProxy = enable
                                Prefs.enableProxy = enable
                                if (enable) ProxyHttpApi.createClient(Prefs.proxyServer)
                            }
                        )
                        AnimatedVisibility(visible = enableProxy) {
                            SettingListItem(
                                modifier = Modifier.padding(top = 12.dp),
                                title = stringResource(R.string.settings_network_proxy_server_title),
                                supportText = if (proxyServer.isBlank()) stringResource(R.string.settings_network_proxy_server_content_empty) else proxyServer,
                                onClick = { showProxyServerEditDialog = true }
                            )
                        }
                    }
                }

                item {
                    SettingListItem(
                        title = stringResource(R.string.settings_network_test_title),
                        supportText = stringResource(R.string.settings_network_test_text),
                        onClick = {
                            context.startActivity(Intent(context, SpeedTestActivity::class.java))
                        }
                    )
                }
            }
        }
    }

    ProxyServerEditDialog(
        show = showProxyServerEditDialog,
        onHideDialog = { showProxyServerEditDialog = false },
        proxyServer = proxyServer,
        onProxyServerChange = {
            proxyServer = it
            Prefs.proxyServer = it
            ProxyHttpApi.createClient(it)
        }
    )
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ProxyServerEditDialog(
    modifier: Modifier = Modifier,
    show: Boolean,
    onHideDialog: () -> Unit,
    proxyServer: String,
    onProxyServerChange: (String) -> Unit
) {
    var proxyServerString by remember(show) { mutableStateOf(proxyServer) }

    if (show) {
        AlertDialog(
            modifier = modifier,
            title = { Text(text = stringResource(R.string.proxy_server_edit_dialog_title)) },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = proxyServerString,
                        onValueChange = { proxyServerString = it },
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium,
                        placeholder = { Text(text = stringResource(R.string.proxy_server_edit_dialog_input_field_label)) }
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(imageVector = Icons.Outlined.Info, contentDescription = null)
                        Text(
                            text = stringResource(R.string.proxy_server_edit_dialog_warning),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            onDismissRequest = onHideDialog,
            confirmButton = {
                Button(onClick = {
                    onProxyServerChange(proxyServerString)
                    onHideDialog()
                }) {
                    Text(text = stringResource(id = R.string.common_confirm))
                }
            },
            dismissButton = {
                Button(onClick = onHideDialog) {
                    Text(text = stringResource(id = R.string.common_cancel))
                }
            }
        )
    }
}

@Preview
@Composable
fun ProxyServerEditDialogPreview() {
    BVTheme {
        ProxyServerEditDialog(
            show = true,
            onHideDialog = {},
            proxyServer = "",
            onProxyServerChange = {}
        )
    }
}