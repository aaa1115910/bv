package dev.aaa1115910.bv.component.settings

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import dev.aaa1115910.bv.entity.AuthData
import dev.aaa1115910.bv.util.toast
import io.github.g0dkar.qrcode.QRCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

@Composable
fun CookiesDialog(
    modifier: Modifier = Modifier,
    show: Boolean,
    onHideDialog: () -> Unit
) {
    val context = LocalContext.current
    var qrImage by remember { mutableStateOf(ImageBitmap(1, 1, ImageBitmapConfig.Argb8888)) }
    val defaultCookies by remember { mutableStateOf(AuthData.fromPrefs()) }

    var json by remember { mutableStateOf(Json.encodeToString(defaultCookies)) }

    val createQr: () -> Unit = {
        val jsonString = Json.encodeToString(defaultCookies)
        val output = ByteArrayOutputStream()
        QRCode(jsonString).render().writeImage(output)
        val input = ByteArrayInputStream(output.toByteArray())
        qrImage = BitmapFactory.decodeStream(input).asImageBitmap()
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.Default) {
            createQr()
        }
    }

    if (show) {
        AlertDialog(
            modifier = modifier,
            onDismissRequest = { onHideDialog() },
            title = { Text(text = "Cookies") },
            text = {
                Row {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = json,
                            onValueChange = { json = it },
                            maxLines = 5
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .size(140.dp)
                            .clip(MaterialTheme.shapes.large)
                            .background(Color.White),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            modifier = Modifier.size(120.dp),
                            bitmap = qrImage,
                            contentDescription = null
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    runCatching {
                        val authData = AuthData.fromJson(json)
                        authData.saveToPrefs()
                    }.onFailure {
                        println(it.stackTraceToString())
                        "无法解析".toast(context)
                    }.onSuccess {
                        onHideDialog()
                    }
                }) {
                    Text(text = "Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { onHideDialog() }) {
                    Text(text = "Dismiss")
                }
            }
        )
    }
}
