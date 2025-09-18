package dev.aaa1115910.bv.mobile.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dev.aaa1115910.bv.mobile.screen.MobileMainScreen
import dev.aaa1115910.bv.mobile.screen.RegionBlockScreen
import dev.aaa1115910.bv.mobile.theme.BVMobileTheme
import dev.aaa1115910.bv.util.NetworkUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        var keepSplashScreen = true
        installSplashScreen().apply {
            setKeepOnScreenCondition { keepSplashScreen }
        }
        super.onCreate(savedInstanceState)

        setContent {
            val scope = rememberCoroutineScope()
            var isCheckingNetwork by remember { mutableStateOf(true) }
            var isMainlandChina by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                scope.launch(Dispatchers.IO) {
                    isMainlandChina = false // NetworkUtil.isMainlandChina()
                    isCheckingNetwork = false
                    keepSplashScreen = false
                }
            }

            BVMobileTheme {
                if (isCheckingNetwork) {
                    // 避免提前加载内容
//                } else if (isMainlandChina) {
//                    RegionBlockScreen()
                } else {
                    MobileMainScreen()
                }
            }
        }
    }
}