package dev.aaa1115910.bv.mobile.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dev.aaa1115910.bv.mobile.screen.UserSpaceScreen
import dev.aaa1115910.bv.mobile.theme.BVMobileTheme

class UserSpaceActivity : ComponentActivity() {
    companion object {
        fun actionStart(context: Context, mid: Long, name: String) {
            context.startActivity(
                Intent(context, UserSpaceActivity::class.java).apply {
                    putExtra("mid", mid)
                    putExtra("name", name)
                }
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BVMobileTheme {
                UserSpaceScreen()
            }
        }
    }
}