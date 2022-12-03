package dev.aaa1115910.bv.ui.theme

import android.app.Activity
import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationInstance
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import dev.aaa1115910.bv.component.Disclaimer
import dev.aaa1115910.bv.util.VerityUtil

private val BiliColorScheme = darkColorScheme(
    primary = Color(0xFFFB7299),
    onPrimary = Color.White/*
    secondary = Color(0xFFCCC2DC),
    tertiary = Color(0xFFEFB8C8)*/
)

@Composable
fun BVTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = BiliColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
    ) {
        CompositionLocalProvider(
            LocalIndication provides NoRippleIndication,
        ) {
            Surface {
                content()

                if (!view.isInEditMode) Disclaimer()
            }
        }
    }

    VerityUtil.hackCheck(context) {
        throw NullPointerException()
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
