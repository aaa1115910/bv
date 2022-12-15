package dev.aaa1115910.bv.activities.user

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dev.aaa1115910.bv.screen.QRLoginScreen
import dev.aaa1115910.bv.ui.theme.BVTheme

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BVTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFE9487F)
                ) {
                    QRLoginScreen()
                }
            }
        }
    }
}
