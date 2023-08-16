package dev.aaa1115910.bv.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationInstance
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Typography
import androidx.tv.material3.darkColorScheme
import dev.aaa1115910.bv.component.FpsMonitor
import dev.aaa1115910.bv.util.Prefs

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun BVTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable (
        onDensityChange: (Float) -> Unit
    ) -> Unit
) {
    val context = LocalContext.current
    val fontScale = LocalDensity.current.fontScale
    val view = LocalView.current

    val colorSchemeTv = darkColorScheme()
    val colorSchemeCommon = androidx.compose.material3.darkColorScheme()
    val typographyTv =
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) android6AndBelowTypographyTv else Typography()
    val typographyCommon =
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) android6AndBelowTypographyCommon else androidx.compose.material3.Typography()

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorSchemeTv.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    val density = if (view.isInEditMode)
        LocalDensity.current.density
    else
        Prefs.densityFlow.collectAsState(context.resources.displayMetrics.widthPixels / 960f).value

    val showFps by remember { mutableStateOf(if (!view.isInEditMode) Prefs.showFps else false) }

    MaterialTheme(
        colorScheme = colorSchemeTv,
        typography = typographyTv
    ) {
        androidx.compose.material3.MaterialTheme(
            colorScheme = colorSchemeCommon,
            typography = typographyCommon
        ) {
            CompositionLocalProvider(
                LocalIndication provides NoRippleIndication,
                LocalDensity provides Density(density = density, fontScale = fontScale)
            ) {
                androidx.compose.material3.Surface(color = Color.Transparent) {
                    Surface(
                        shape = RoundedCornerShape(0.dp),
                    ) {
                        if (showFps) {
                            FpsMonitor {
                                content(
                                    onDensityChange = {

                                    }
                                )
                            }
                        } else {
                            content(
                                onDensityChange = {

                                }
                            )
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
