package dev.aaa1115910.bv.activities.video

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dev.aaa1115910.bv.screen.TagScreen
import dev.aaa1115910.bv.ui.theme.BVTheme

class TagActivity : ComponentActivity() {
    companion object {
        fun actionStart(context: Context, tagId: Int, tagName: String) {
            context.startActivity(
                Intent(context, TagActivity::class.java).apply {
                    putExtra("tagId", tagId)
                    putExtra("tagName", tagName)
                }
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BVTheme {
                TagScreen()
            }
        }
    }
}