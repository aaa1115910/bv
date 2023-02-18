package dev.aaa1115910.bv.activities.user

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dev.aaa1115910.bv.screen.user.FollowingSeasonScreen
import dev.aaa1115910.bv.ui.theme.BVTheme

class FollowingSeasonActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BVTheme {
                FollowingSeasonScreen()
            }
        }
    }
}