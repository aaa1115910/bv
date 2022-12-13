package dev.aaa1115910.bv.activities.user

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dev.aaa1115910.bv.screen.user.UserInfoScreen
import dev.aaa1115910.bv.ui.theme.BVTheme

class UserInfoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BVTheme {
                UserInfoScreen()
            }
        }
    }
}
