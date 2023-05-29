package dev.aaa1115910.bv.screen.login

import android.annotation.SuppressLint
import android.app.Activity
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import dev.aaa1115910.biliapi.repositories.SendSmsState
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.viewmodel.login.SmsLoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun SmsLoginContent(
    modifier: Modifier = Modifier,
    smsLoginViewModel: SmsLoginViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var phoneNumberText by remember { mutableStateOf("") }
    var codeText by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column {
            OutlinedTextField(
                value = phoneNumberText,
                onValueChange = { phoneNumberText = it },
                label = { Text(text = "Phone number") },
                maxLines = 1
            )
            OutlinedTextField(
                value = codeText,
                onValueChange = { codeText = it },
                label = { Text(text = "Code") },
                maxLines = 1
            )
            Button(onClick = {
                scope.launch(Dispatchers.IO) {
                    runCatching {
                        smsLoginViewModel.sendSms(phoneNumberText.toLong())
                    }
                }
            }) {
                Text(text = "Send sms")
            }
            Button(onClick = {
                scope.launch(Dispatchers.IO) {
                    runCatching {
                        smsLoginViewModel.loginWithSms(codeText.toInt()) {
                            (context as Activity).finish()
                        }
                    }
                }
            }) {
                Text(text = "Login")
            }
        }

        if (smsLoginViewModel.sendSmsState == SendSmsState.RecaptchaRequire) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(0.2f)),
                contentAlignment = Alignment.Center
            ) {
                CaptchaWebView(
                    modifier = Modifier
                        .size(300.dp)
                        .clip(MaterialTheme.shapes.large),
                    url = smsLoginViewModel.recaptchaUrl,
                    onClose = { smsLoginViewModel.sendSmsState = SendSmsState.Ready }
                )
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun CaptchaWebView(
    modifier: Modifier = Modifier,
    smsLoginViewModel: SmsLoginViewModel = koinViewModel(),
    url: String,
    onClose: () -> Unit
) {
    var mWebView: WebView? by remember { mutableStateOf(null) }

    BackHandler {
        onClose()
    }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                mWebView = this
                loadUrl(url)
                settings.javaScriptEnabled = true
                addJavascriptInterface(smsLoginViewModel, "main")
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        //https://github.com/mos9527/bilibili-toolman/blob/23fad4a1028757fcf725923f5629a88b047f6bfc/bilibili_toolman/bilisession/client.py#LL306C11-L306C11
                        //val js = """javascript:eval("Object._assign=Object.assign;Object.assign=(...args)=>{if(args[1]['geetest_challenge']){document.body.innerHTML=JSON.stringify(args[1]);}return Object._assign(...args)}")"""
                        println("on page finished")
                        val js =
                            """javascript:eval("Object._assign=Object.assign;Object.assign=(...args)=>{if(args[1]['geetest_challenge']){window.main.parseCaptchaResult(JSON.stringify(args[1]));}return Object._assign(...args)}")"""
                        loadUrl(js)
                    }
                }
            }
        }
    )
}

@Preview
@Composable
fun AndroidWebViewPreview() {
    BVTheme {
        CaptchaWebView(
            modifier = Modifier.fillMaxSize(),
            url = "",
            onClose = {}
        )
    }
}