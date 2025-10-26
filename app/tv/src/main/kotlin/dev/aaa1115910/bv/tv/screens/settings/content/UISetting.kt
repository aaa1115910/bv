package dev.aaa1115910.bv.tv.screens.settings.content

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.ArrowDropUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Icon
import androidx.tv.material3.ListItem
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.RadioButton
import androidx.tv.material3.Text
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.entity.ThemeType
import dev.aaa1115910.bv.tv.component.TvAlertDialog
import dev.aaa1115910.bv.tv.component.settings.SettingListItem
import dev.aaa1115910.bv.tv.component.settings.SettingSwitchListItem
import dev.aaa1115910.bv.tv.component.HomeTopNavItem
import dev.aaa1115910.bv.tv.screens.settings.SettingsMenuNavItem
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.requestFocus
import kotlin.math.roundToInt

@Composable
fun UISetting(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var showDensityDialog by remember { mutableStateOf(false) }
    var showThemeTypeDialog by remember { mutableStateOf(false) }
    var showDefaultHomeTabDialog by remember { mutableStateOf(false) }
    val density by Prefs.densityFlow.collectAsState(context.resources.displayMetrics.widthPixels / 960f)
    val themeType by Prefs.themeTypeFlow.collectAsState(Prefs.themeType)
    var defaultHomeTab by remember { mutableStateOf(HomeTopNavItem.entries.getOrElse(Prefs.defaultHomeTab) { HomeTopNavItem.Recommend }) }

    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = SettingsMenuNavItem.UI.getDisplayName(context),
                style = MaterialTheme.typography.displaySmall
            )
            Spacer(modifier = Modifier.height(12.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    SettingListItem(
                        title = stringResource(R.string.settings_ui_density_title),
                        supportText = stringResource(R.string.settings_ui_density_text),
                        valueText = density.toString(),
                        onClick = { showDensityDialog = true }
                    )
                }
                item {
                    SettingListItem(
                        title = stringResource(R.string.settings_ui_theme_type_title),
                        supportText = stringResource(R.string.settings_ui_theme_type_text),
                        valueText = themeType.getDisplayName(context),
                        onClick = { showThemeTypeDialog = true }
                    )
                }
                item {
                    SettingListItem(
                        title = stringResource(R.string.settings_ui_default_home_tab_title),
                        supportText = stringResource(R.string.settings_ui_default_home_tab_text),
                        valueText = defaultHomeTab.getDisplayName(context),
                        onClick = { showDefaultHomeTabDialog = true }
                    )
                }
            }
        }
    }

    UIDensityDialog(
        show = showDensityDialog,
        onHideDialog = { showDensityDialog = false },
        density = density,
        onDensityChange = { Prefs.density = it }
    )

    ThemeTypeDialog(
        show = showThemeTypeDialog,
        onHideDialog = { showThemeTypeDialog = false },
        themeType = themeType,
        onThemeTypeChange = { Prefs.themeType = it }
    )

    DefaultHomeTabDialog(
        show = showDefaultHomeTabDialog,
        onHideDialog = { showDefaultHomeTabDialog = false },
        defaultHomeTab = defaultHomeTab,
        onDefaultHomeTabChange = { 
            defaultHomeTab = it
            Prefs.defaultHomeTab = it.ordinal 
        }
    )
}

@Composable
private fun UIDensityDialog(
    modifier: Modifier = Modifier,
    show: Boolean,
    onHideDialog: () -> Unit,
    density: Float,
    onDensityChange: (Float) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }
    val defaultDensity by remember { mutableFloatStateOf(context.resources.displayMetrics.widthPixels / 960f) }

    LaunchedEffect(show) {
        if (show) focusRequester.requestFocus(scope)
    }

    // 这里得采用固定的 Density，否则会导致更改 Density 时，对话框反复重新加载
    CompositionLocalProvider(
        LocalDensity provides Density(
            density = defaultDensity,
            fontScale = LocalDensity.current.fontScale
        )
    ) {
        if (show) {
            TvAlertDialog(
                modifier = modifier,
                onDismissRequest = { onHideDialog() },
                title = { Text(text = stringResource(R.string.settings_ui_density_title)) },
                text = {
                    Column(
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .focusable()
                            .fillMaxWidth()
                            .onPreviewKeyEvent {
                                if (it.key == Key.DirectionUp || it.key == Key.DirectionDown) {
                                    if (it.type == KeyEventType.KeyDown) {
                                        var newDensity = if (it.key == Key.DirectionUp)
                                            density + 0.1f else density - 0.1f
                                        newDensity = (newDensity * 10).roundToInt() / 10f
                                        if (newDensity < 0.5f) newDensity = 0.5f
                                        if (newDensity > 5f) newDensity = 5f
                                        onDensityChange(newDensity)
                                    }
                                }
                                false
                            },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Rounded.ArrowDropUp, contentDescription = null)
                        Text(text = "$density")
                        Icon(imageVector = Icons.Rounded.ArrowDropDown, contentDescription = null)
                    }
                },
                confirmButton = {}
            )
        }
    }
}

@Composable
fun ThemeTypeDialog(
    modifier: Modifier = Modifier,
    show: Boolean,
    onHideDialog: () -> Unit,
    themeType: ThemeType,
    onThemeTypeChange: (ThemeType) -> Unit
) {
    if (show) {
        TvAlertDialog(
            modifier = modifier,
            onDismissRequest = { onHideDialog() },
            title = { Text(text = stringResource(R.string.settings_ui_theme_type_title)) },
            text = {
                Column {
                    ThemeType.entries.forEach {
                        ListItem(
                            selected = themeType == it,
                            onClick = { onThemeTypeChange(it) },
                            headlineContent = {
                                Text(text = it.getDisplayName(LocalContext.current))
                            },
                            trailingContent = {
                                RadioButton(
                                    selected = themeType == it,
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

@Preview
@Composable
fun UIDensityDialogPreview() {
    val show by remember { mutableStateOf(true) }
    var density by remember { mutableFloatStateOf(1.0f) }

    BVTheme {
        UIDensityDialog(
            show = show,
            onHideDialog = {},
            density = density,
            onDensityChange = { density = it }
        )
    }
}

@Preview
@Composable
private fun ThemeTypeDialogPreview() {
    val show by remember { mutableStateOf(true) }
    val themeType by remember { mutableStateOf(ThemeType.Auto) }

    BVTheme {
        ThemeTypeDialog(
            show = show,
            onHideDialog = {},
            themeType = themeType,
            onThemeTypeChange = {}
        )
    }
}

@Composable
fun DefaultHomeTabDialog(
    modifier: Modifier = Modifier,
    show: Boolean,
    onHideDialog: () -> Unit,
    defaultHomeTab: HomeTopNavItem,
    onDefaultHomeTabChange: (HomeTopNavItem) -> Unit
) {
    if (show) {
        TvAlertDialog(
            modifier = modifier,
            onDismissRequest = { onHideDialog() },
            title = { Text(text = stringResource(R.string.settings_ui_default_home_tab_title)) },
            text = {
                Column {
                    HomeTopNavItem.entries.forEach {
                        ListItem(
                            selected = defaultHomeTab == it,
                            onClick = { onDefaultHomeTabChange(it) },
                            headlineContent = {
                                Text(text = it.getDisplayName(LocalContext.current))
                            },
                            trailingContent = {
                                RadioButton(
                                    selected = defaultHomeTab == it,
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