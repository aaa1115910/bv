package dev.aaa1115910.bv.mobile.screen.settings

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val scaffoldNavigator = rememberListDetailPaneScaffoldNavigator()

    var selectedSettings by rememberSaveable { mutableStateOf<MobileSettings?>(null) }
    val singlePart = listOf(WindowWidthSizeClass.COMPACT, WindowWidthSizeClass.MEDIUM)
        .contains(currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass)

    BackHandler(scaffoldNavigator.canNavigateBack()) {
        scaffoldNavigator.navigateBack()
    }

    ListDetailPaneScaffold(
        directive = scaffoldNavigator.scaffoldDirective,
        value = scaffoldNavigator.scaffoldValue,
        listPane = {
            AnimatedPane(
                modifier = Modifier.preferredWidth(300.dp),
            ) {
                SettingsCategories(
                    selectedSettings = if (singlePart) null else selectedSettings
                        ?: MobileSettings.Play,
                    onSelectedSettings = {
                        selectedSettings = it
                        scaffoldNavigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
                    },
                    showNavBack = !scaffoldNavigator.canNavigateBack(),
                    onBack = { (context as Activity).finish() },
                    singleList = singlePart
                )
            }
        },
        detailPane = {
            AnimatedPane(modifier = Modifier) {
                SettingsDetails(
                    selectedSettings = selectedSettings ?: MobileSettings.Play,
                    showNavBack = scaffoldNavigator.canNavigateBack(),
                    onBack = { scaffoldNavigator.navigateBack() }
                )
            }
        }
    )
}

enum class MobileSettings {
    Play, About, Advance, Debug
}
