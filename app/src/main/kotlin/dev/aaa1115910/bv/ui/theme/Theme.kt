package dev.aaa1115910.bv.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationInstance
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Typography
import androidx.tv.material3.darkColorScheme
import dev.aaa1115910.bv.component.FpsMonitor
import dev.aaa1115910.bv.component.SurfaceWithoutClickable
import dev.aaa1115910.bv.util.Prefs

@OptIn(ExperimentalTvMaterial3Api::class)
private val bvColorSchemeTv = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    //secondary = md_theme_dark_secondary,
    //onSecondary = md_theme_dark_onSecondary,
    //secondaryContainer = md_theme_dark_secondaryContainer,
    //onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    background = Color(0xFF121212)
)

private val bvColorSchemeCommon = androidx.compose.material3.darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    //secondary = md_theme_dark_secondary,
    //onSecondary = md_theme_dark_onSecondary,
    //secondaryContainer = md_theme_dark_secondaryContainer,
    //onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    background = Color(0xFF121212)
)

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun BVTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = bvColorSchemeTv
    val typographyTv =
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) android6AndBelowTypographyTv else Typography()
    val typographyCommon =
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) android6AndBelowTypographyCommon else androidx.compose.material3.Typography()

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    val showFps by remember { mutableStateOf(if (!view.isInEditMode) Prefs.showFps else false) }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typographyTv
    ) {
        androidx.compose.material3.MaterialTheme(
            colorScheme = bvColorSchemeCommon,
            typography = typographyCommon
        ) {
            CompositionLocalProvider(
                LocalIndication provides NoRippleIndication,
            ) {
                androidx.compose.material3.Surface(color = Color.Transparent) {
                    SurfaceWithoutClickable {
                        if (showFps) {
                            FpsMonitor {
                                content()
                            }
                        } else {
                            content()
                        }
                    }
                }
            }
        }
    }
}

object NoRippleIndication : Indication {
    private object NoIndicationInstance : IndicationInstance {
        override fun ContentDrawScope.drawIndication() {
            drawContent()
        }
    }

    @Composable
    override fun rememberUpdatedInstance(interactionSource: InteractionSource): IndicationInstance {
        return NoIndicationInstance
    }
}
