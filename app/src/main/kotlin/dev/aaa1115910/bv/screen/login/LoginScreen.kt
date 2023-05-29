package dev.aaa1115910.bv.screen.login

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.aaa1115910.biliapi.entity.ApiType
import dev.aaa1115910.bv.util.Prefs

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier
) {
    when (Prefs.apiType) {
        ApiType.Http -> {
            QRLoginContent(modifier)
        }

        ApiType.GRPC -> {
            SmsLoginContent(modifier)
        }
    }
}