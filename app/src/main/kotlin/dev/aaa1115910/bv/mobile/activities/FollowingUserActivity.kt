package dev.aaa1115910.bv.mobile.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dev.aaa1115910.bv.mobile.screen.FollowingUserScreen
import dev.aaa1115910.bv.mobile.theme.BVMobileTheme

class FollowingUserActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BVMobileTheme {
                FollowingUserScreen()
            }
        }
    }
}