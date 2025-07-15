package dev.aaa1115910.bv.tv.screens.settings

import android.content.Context
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.nativeKeyCode
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ListItem
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.tv.screens.settings.content.AboutSetting
import dev.aaa1115910.bv.tv.screens.settings.content.ApiSetting
import dev.aaa1115910.bv.tv.screens.settings.content.AudioSetting
import dev.aaa1115910.bv.tv.screens.settings.content.InfoSetting
import dev.aaa1115910.bv.tv.screens.settings.content.NetworkSetting
import dev.aaa1115910.bv.tv.screens.settings.content.OtherSetting
import dev.aaa1115910.bv.tv.screens.settings.content.PlayerTypeSetting
import dev.aaa1115910.bv.tv.screens.settings.content.ResolutionSetting
import dev.aaa1115910.bv.tv.screens.settings.content.StorageSetting
import dev.aaa1115910.bv.tv.screens.settings.content.UISetting
import dev.aaa1115910.bv.tv.screens.settings.content.VideoCodecSetting
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.util.requestFocus

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier
) {
    val showLargeTitle by remember { derivedStateOf { true } }
    val titleFontSize by animateFloatAsState(
        targetValue = if (showLargeTitle) 48f else 24f,
        label = "title font size"
    )

    var currentMenu by remember { mutableStateOf(SettingsMenuNavItem.Resolution) }
    var focusInNav by remember { mutableStateOf(false) }

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
                        text = stringResource(R.string.title_activity_settings),
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
            SettingsNav(
                modifier = Modifier
                    .onFocusChanged { focusInNav = it.hasFocus }
                    .weight(3f)
                    .fillMaxHeight(),
                currentMenu = currentMenu,
                onMenuChanged = { currentMenu = it },
                isFocusing = focusInNav
            )
            SettingContent(
                modifier = Modifier
                    .weight(5f)
                    .fillMaxSize(),
                onBackNav = { focusInNav = true },
                currentMenu = currentMenu
            )
        }
    }
}

@Composable
fun SettingsNav(
    modifier: Modifier = Modifier,
    currentMenu: SettingsMenuNavItem,
    onMenuChanged: (SettingsMenuNavItem) -> Unit,
    isFocusing: Boolean
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isFocusing) {
        if (isFocusing) focusRequester.requestFocus(scope)
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus(scope)
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (item in SettingsMenuNavItem.entries - listOf(SettingsMenuNavItem.PlayerType)) {
            val buttonModifier = if (currentMenu == item) Modifier
                .focusRequester(focusRequester)
                .fillMaxWidth()
            else Modifier.fillMaxWidth()
            item {
                SettingsMenuButton(
                    modifier = buttonModifier,
                    text = item.getDisplayName(context),
                    selected = currentMenu == item,
                    onFocus = {
                        onMenuChanged(item)
                    }
                )
            }
        }
    }
}

enum class SettingsMenuNavItem(private val strRes: Int) {
    Resolution(R.string.settings_item_resolution),
    VideoCodec(R.string.settings_item_codec),
    Audio(R.string.settings_item_audio),
    PlayerType(R.string.settings_item_player_type),
    UI(R.string.settings_item_ui),
    Api(R.string.settings_item_api),
    Other(R.string.settings_item_other),
    Storage(R.string.settings_item_storage),
    Network(R.string.settings_item_network),
    Info(R.string.settings_item_info),
    About(R.string.settings_item_about);

    fun getDisplayName(context: Context) = context.getString(strRes)
}

@Composable
fun SettingContent(
    modifier: Modifier = Modifier,
    onBackNav: () -> Unit,
    currentMenu: SettingsMenuNavItem
) {
    Box(
        modifier = modifier
            .padding(24.dp)
    ) {
        SettingsDetail(
            modifier = Modifier.fillMaxSize(),
            onFocusBackMenuList = {
                onBackNav()
            }
        ) {
            when (currentMenu) {
                SettingsMenuNavItem.Resolution -> ResolutionSetting()
                SettingsMenuNavItem.Info -> InfoSetting()
                SettingsMenuNavItem.About -> AboutSetting()
                SettingsMenuNavItem.VideoCodec -> VideoCodecSetting()
                SettingsMenuNavItem.Audio -> AudioSetting()
                SettingsMenuNavItem.Other -> OtherSetting()
                SettingsMenuNavItem.Network -> NetworkSetting()
                SettingsMenuNavItem.PlayerType -> PlayerTypeSetting()
                SettingsMenuNavItem.UI -> UISetting()
                SettingsMenuNavItem.Storage -> StorageSetting()
                SettingsMenuNavItem.Api -> ApiSetting()
            }
        }
    }
}

@Composable
fun SettingsMenuButton(
    modifier: Modifier = Modifier,
    text: String,
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
                modifier = Modifier.padding(
                    horizontal = 16.dp
                ),
                text = text,
                style = MaterialTheme.typography.titleLarge
            )
        }
    )
}

@Preview
@Composable
fun SettingsMenuButtonPreview() {
    BVTheme {
        Box(
            modifier = Modifier.size(200.dp, 100.dp)
        ) {
            SettingsMenuButton(
                modifier = Modifier.align(Alignment.Center),
                text = "This is button",
                selected = true,
                onFocus = {}
            )
        }
    }
}

@Composable
fun SettingsDetail(
    modifier: Modifier = Modifier,
    onFocusBackMenuList: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .onPreviewKeyEvent {
                val result = it.key.nativeKeyCode == android.view.KeyEvent.KEYCODE_DPAD_LEFT
                if (result) onFocusBackMenuList()
                result
            }
    ) {
        content()
    }
}
