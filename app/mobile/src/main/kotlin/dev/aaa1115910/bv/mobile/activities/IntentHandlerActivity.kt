package dev.aaa1115910.bv.mobile.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import dev.aaa1115910.bv.entity.BvScheme
import io.github.oshai.kotlinlogging.KotlinLogging

class IntentHandlerActivity : ComponentActivity() {
    companion object {
        private val logger = KotlinLogging.logger { }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.decorView.systemUiVisibility =
            android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
            android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        val uri = intent.data
        when (uri?.host) {
            BvScheme.QrToken.HOST -> QrTokenResultActivity.launch(this, uri)
            else -> {
                logger.info { "unknown uri: $uri" }
                finish()
            }
        }
    }
}