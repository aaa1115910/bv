package dev.aaa1115910.bv.mobile.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dev.aaa1115910.bv.mobile.screen.MobileMainScreen
import dev.aaa1115910.bv.mobile.theme.BVMobileTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            BVMobileTheme {
                MobileMainScreen()
            }
        }
    }
}