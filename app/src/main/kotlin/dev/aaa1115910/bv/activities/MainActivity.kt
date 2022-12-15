package dev.aaa1115910.bv.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dev.aaa1115910.bv.screen.HomeScreen
import dev.aaa1115910.bv.ui.theme.BVTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            BVTheme {
                HomeScreen()
            }
        }
    }
}

