package dev.aaa1115910.bv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.nativeKeyCode
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.viewmodel.LoginState
import dev.aaa1115910.bv.viewmodel.LoginViewModel
import org.koin.androidx.compose.koinViewModel

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BVTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFE9487F)
                ) {
                    QRLoginScreen()
                }
            }
        }
    }
}

@Composable
fun QRLoginScreen(
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel = koinViewModel()
) {
    LaunchedEffect(Unit) {
        loginViewModel.requestQRCode()
    }
    Box(
        modifier = modifier
            .focusable()
            .fillMaxSize()
            .onKeyEvent {
                if (it.key.nativeKeyCode == android.view.KeyEvent.KEYCODE_DPAD_CENTER) {
                    if (listOf(LoginState.QRExpired, LoginState.Error)
                            .contains(loginViewModel.state)
                    ) {
                        loginViewModel.requestQRCode()
                    }
                    return@onKeyEvent true
                }
                false
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(36.dp)
        ) {
            AnimatedVisibility(
                visible = listOf(LoginState.WaitToScan, LoginState.WaitToConfirm)
                    .contains(loginViewModel.state)
            ) {
                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .clip(MaterialTheme.shapes.large)
                        .background(Color.White),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        modifier = Modifier.size(200.dp),
                        bitmap = loginViewModel.qrImage,
                        contentDescription = null
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = when (loginViewModel.state) {
                        LoginState.Ready, LoginState.RequestingQRCode -> "请求二维码中"
                        LoginState.WaitToScan -> "请扫描二维码"
                        LoginState.WaitToConfirm -> "请确认登录"
                        LoginState.QRExpired -> "二维码已过期"
                        LoginState.LoginSuccess -> "登录成功"
                        LoginState.Error -> "出现错误"
                    },
                    style = MaterialTheme.typography.displaySmall,
                    color = Color.White
                )
                var hasFocus by remember { mutableStateOf(false) }
                AnimatedVisibility(
                    visible = listOf(LoginState.QRExpired, LoginState.Error)
                        .contains(loginViewModel.state)
                ) {
                    Text(
                        text = "按下 确认键 重试",
                        style = MaterialTheme.typography.displaySmall,
                        color = Color.White,
                        fontSize = 26.sp
                    )
                }
            }
        }
    }
}
