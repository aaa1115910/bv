package dev.aaa1115910.bv.activities.pgc.anime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dev.aaa1115910.bv.screen.main.pgc.anime.AnimeTimelineScreen
import dev.aaa1115910.bv.ui.theme.BVTheme

class AnimeTimelineActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BVTheme {
                AnimeTimelineScreen()
            }
        }
    }
}