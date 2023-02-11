package dev.aaa1115910.bv.activities.anime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dev.aaa1115910.bv.screen.home.anime.AnimeFollowingScreen
import dev.aaa1115910.bv.ui.theme.BVTheme

class AnimeFollowingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BVTheme {
                AnimeFollowingScreen()
            }
        }
    }
}