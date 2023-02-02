package dev.aaa1115910.bv.activities.search

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dev.aaa1115910.bv.screen.search.SearchInputScreen
import dev.aaa1115910.bv.ui.theme.BVTheme

class SearchInputActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BVTheme {
                SearchInputScreen()
            }
        }
    }
}