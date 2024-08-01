package dev.aaa1115910.bv.screen.settings

import android.util.Base64
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebView.setWebContentsDebuggingEnabled
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import androidx.tv.material3.Text
import androidx.webkit.WebViewClientCompat
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.util.Prefs

@Composable
fun SpeedTestScreen(
    modifier: Modifier = Modifier
) {
    var loading by remember { mutableStateOf(true) }

    BoxWithConstraints {
        val width = with(LocalDensity.current) {
            this@BoxWithConstraints.maxWidth.toPx().toInt()
        }

        val webViewClient = object : WebViewClientCompat() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                //默认的 css 会无法正常显示
                val css = """
                .container {
                    width: 1920px !important;
                    height: 1080px !important;
                }
            """.trimIndent()
                val encoded: String = Base64.encodeToString(css.toByteArray(), Base64.NO_WRAP)

                view?.loadUrl(
                    "javascript:(function() {" +
                            "var parent = document.getElementsByTagName('head').item(0);" +
                            "var style = document.createElement('style');" +
                            "style.type = 'text/css';" +
                            // Tell the browser to BASE64-decode the string into your script !!!
                            "style.innerHTML = window.atob('" + encoded + "');" +
                            "parent.appendChild(style)" +
                            "})()"
                )
                //处理完css还得处理缩放
                view?.setInitialScale(((width / 1920f) * 100).toInt())
                loading = false
            }
        }

        CookieManager.getInstance().apply {
            val cookies = mapOf(
                "DedeUserID" to Prefs.uid,
                "DedeUserID__ckMd5" to Prefs.uidCkMd5,
                "SESSDATA" to Prefs.sessData,
                "bili_jct" to Prefs.biliJct,
                "sid" to Prefs.sid
            )

            cookies.forEach { (name, value) ->
                setCookie(".bilibili.com", "$name=$value")
            }
        }

        AndroidView(
            modifier = modifier.fillMaxSize(),
            factory = { ctx ->
                WebView(ctx).apply {
                    this.webViewClient = webViewClient

                    setWebContentsDebuggingEnabled(true)

                    settings.apply {
                        userAgentString =
                            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36"
                        javaScriptEnabled = true
                    }

                    loadUrl("https://www.bilibili.com/blackboard/video-diagnostics.html")
                }
            }
        )

        if (loading) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                colors = SurfaceDefaults.colors(
                    containerColor = Color.Black.copy(alpha = 0.9f)
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = stringResource(R.string.loading))
                }
            }
        }
    }
}