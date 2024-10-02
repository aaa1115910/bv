package dev.aaa1115910.bv.activities.anime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dev.aaa1115910.bv.screen.main.pgc.anime.AnimeIndexScreen
import dev.aaa1115910.bv.ui.theme.BVTheme

class AnimeIndexActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BVTheme {
                AnimeIndexScreen()
            }
        }
    }
}