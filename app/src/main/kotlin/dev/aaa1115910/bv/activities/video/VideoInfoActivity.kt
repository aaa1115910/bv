package dev.aaa1115910.bv.activities.video

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dev.aaa1115910.bv.screen.VideoInfoScreen
import dev.aaa1115910.bv.ui.theme.BVTheme

class VideoInfoActivity : ComponentActivity() {
    companion object {
        fun actionStart(context: Context, aid: Int, fromSeason: Boolean = false) {
            context.startActivity(
                Intent(context, VideoInfoActivity::class.java).apply {
                    putExtra("aid", aid)
                    putExtra("fromSeason", fromSeason)
                }
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BVTheme {
                VideoInfoScreen()
            }
        }
    }
}
