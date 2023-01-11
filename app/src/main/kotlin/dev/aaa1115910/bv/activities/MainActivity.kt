package dev.aaa1115910.bv.activities

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
import dev.aaa1115910.bv.screen.HomeScreen
import dev.aaa1115910.bv.screen.RegionBlockScreen
import dev.aaa1115910.bv.ui.theme.BVTheme
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
            var isMainlandChina by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                scope.launch(Dispatchers.Default) {
                    isMainlandChina = NetworkUtil.isMainlandChina()
                    keepSplashScreen = false
                }
            }

            BVTheme {
                if (isMainlandChina) RegionBlockScreen() else HomeScreen()
            }
        }
    }
}

