package dev.aaa1115910.bv.mobile.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import dev.aaa1115910.bv.mobile.screen.FollowingSeasonScreen
import dev.aaa1115910.bv.mobile.theme.BVMobileTheme

class FollowingSeasonActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val windowSize = calculateWindowSizeClass(this)
            BVMobileTheme {
                FollowingSeasonScreen(
                    windowSize = windowSize
                )
            }
        }
    }
}