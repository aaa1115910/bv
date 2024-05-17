package dev.aaa1115910.bv.screen.login

import android.app.Activity
import android.view.KeyEvent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.nativeKeyCode
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import dev.aaa1115910.biliapi.entity.login.QrLoginState
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.util.toast
import dev.aaa1115910.bv.viewmodel.login.AppQrLoginViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppQRLoginContent(
    modifier: Modifier = Modifier,
    appQrLoginViewModel: AppQrLoginViewModel = koinViewModel()
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        appQrLoginViewModel.requestQRCode()
    }

    LaunchedEffect(appQrLoginViewModel.state) {
        if (appQrLoginViewModel.state == QrLoginState.Success) {
            R.string.login_success.toast(context)
            (context as Activity).finish()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            appQrLoginViewModel.cancelCheckLoginResultTimer()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = modifier
                .focusable()
                .fillMaxSize()
                .onKeyEvent {
                    if (it.key.nativeKeyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                        if (listOf(QrLoginState.Expired, QrLoginState.Error)
                                .contains(appQrLoginViewModel.state)
                        ) {
                            appQrLoginViewModel.requestQRCode()
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
                    visible = listOf(QrLoginState.WaitingForScan, QrLoginState.WaitingForConfirm)
                        .contains(appQrLoginViewModel.state)
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
                            bitmap = appQrLoginViewModel.qrImage,
                            contentDescription = null
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = when (appQrLoginViewModel.state) {
                            QrLoginState.Ready, QrLoginState.RequestingQRCode -> stringResource(R.string.login_requesting)
                            QrLoginState.WaitingForScan -> stringResource(R.string.login_wait_for_scan)
                            QrLoginState.WaitingForConfirm -> stringResource(R.string.login_wait_for_confirm)
                            QrLoginState.Expired -> stringResource(R.string.login_expired)
                            QrLoginState.Success -> stringResource(R.string.login_success)
                            QrLoginState.Error, QrLoginState.Unknown -> stringResource(R.string.login_error)
                        },
                        style = MaterialTheme.typography.displaySmall,
                        color = Color.White
                    )
                    AnimatedVisibility(
                        visible = listOf(QrLoginState.Expired, QrLoginState.Error)
                            .contains(appQrLoginViewModel.state)
                    ) {
                        Text(
                            text = stringResource(R.string.login_retry),
                            style = MaterialTheme.typography.displaySmall,
                            color = Color.White,
                            fontSize = 26.sp
                        )
                    }
                }
            }
        }
    }
}
