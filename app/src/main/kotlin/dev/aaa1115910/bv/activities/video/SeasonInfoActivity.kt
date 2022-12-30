package dev.aaa1115910.bv.activities.video

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dev.aaa1115910.bv.screen.SeasonInfoScreen
import dev.aaa1115910.bv.ui.theme.BVTheme

class SeasonInfoActivity : ComponentActivity() {
    companion object {
        fun actionStart(context: Context, epid: Int) {
            context.startActivity(
                Intent(context, SeasonInfoActivity::class.java).apply {
                    putExtra("epid", epid)
                }
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BVTheme {
                SeasonInfoScreen()
            }
        }
    }
}
