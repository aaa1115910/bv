package dev.aaa1115910.bv.screen.settings

import android.content.Context
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ListItem
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.network.HttpServer
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.util.LogCatcherUtil
import dev.aaa1115910.bv.util.swapList
import dev.aaa1115910.bv.util.toast
import io.github.g0dkar.qrcode.QRCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.Inet4Address
import java.net.NetworkInterface

@Composable
fun LogsScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var host by remember { mutableStateOf("x.x.x.x") }
    var port by remember { mutableIntStateOf(0) }

    val logs = remember { mutableStateListOf<File>() }
    var currentSelectFile by remember { mutableStateOf<File?>(null) }

    var qrImage by remember { mutableStateOf<ImageBitmap?>(null) }

    val generateQRCode = {
        scope.launch(Dispatchers.IO) {
            qrImage = null
            val output = ByteArrayOutputStream()
            val url = "http://$host:$port/api/logs/${currentSelectFile?.name}"
            QRCode(url).render().writeImage(output)
            val input = ByteArrayInputStream(output.toByteArray())
            qrImage = BitmapFactory.decodeStream(input).asImageBitmap()
        }
    }

    @Suppress("DEPRECATION")
    val getIpAddress: () -> String = {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val isWifi: Boolean = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val s = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            s?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo
            networkInfo?.type == ConnectivityManager.TYPE_WIFI
        }

        var ip = ""
        if (isWifi) {
            val wifiManager =
                context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiInfo = wifiManager.connectionInfo
            val ipNum = wifiInfo.ipAddress
            ip =
                "${ipNum and 0xFF}.${ipNum shr 8 and 0xFF}.${ipNum shr 16 and 0xFF}.${ipNum shr 24 and 0xFF}"
        } else {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                val enumIpAddr = intf.inetAddresses
                while (ip == "" && enumIpAddr.hasMoreElements()) {
                    val inetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                        ip = inetAddress.getHostAddress() ?: ""
                        break
                    }
                }
            }
        }
        ip
    }

    val updateLogs = {
        LogCatcherUtil.updateLogFiles()
        val newLogs = (LogCatcherUtil.manualFiles + LogCatcherUtil.crashFiles)
            .sortedByDescending { it.lastModified() }
        logs.swapList(newLogs)
    }

    LaunchedEffect(Unit) {
        host = getIpAddress()
        port = HttpServer.server?.engine?.resolvedConnectors()?.first()?.port ?: 0

        updateLogs()
    }

    LogsScreenContent(
        modifier = modifier,
        qrImage = qrImage,
        clearQrImage = { qrImage = null },
        logs = logs,
        onFocusLogFile = { file ->
            currentSelectFile = file
            generateQRCode()
        },
        onClickCreateLog = {
            LogCatcherUtil.logLogcat(manual = true)
            "Log created".toast(context)
            updateLogs()
        }
    )
}

@Composable
fun LogsScreenContent(
    modifier: Modifier = Modifier,
    qrImage: ImageBitmap?,
    clearQrImage: () -> Unit,
    logs: List<File>,
    onFocusLogFile: (File) -> Unit,
    onClickCreateLog: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            Box(
                modifier = Modifier.padding(start = 48.dp, top = 24.dp, bottom = 8.dp, end = 48.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(id = R.string.title_activity_logs),
                        fontSize = 48.sp
                    )
                }
            }
        }
    ) { innerPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(
                modifier = Modifier.weight(1f)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        horizontal = 36.dp,
                        vertical = 12.dp
                    )
                ) {
                    item {
                        CreateLogItem(
                            modifier = Modifier.focusRequester(focusRequester),
                            onFocus = clearQrImage,
                            onClick = onClickCreateLog
                        )
                    }
                    items(items = logs) { logFile ->
                        LogItem(
                            filename = logFile.name,
                            size = logFile.length(),
                            onFocus = { onFocusLogFile(logFile) }
                        )
                    }
                    if (logs.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = stringResource(R.string.log_list_empty))
                            }
                        }
                    }
                }
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (qrImage != null) {
                    Box(
                        modifier = Modifier
                            .size(240.dp)
                            .clip(MaterialTheme.shapes.large)
                            .background(Color.White),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            modifier = Modifier.size(200.dp),
                            bitmap = qrImage,
                            contentDescription = null
                        )
                    }
                } else {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun LogItem(
    modifier: Modifier = Modifier,
    filename: String,
    size: Long,
    onFocus: () -> Unit
) {
    ListItem(
        modifier = modifier
            .onFocusChanged {
                if (it.hasFocus) onFocus()
            },
        selected = false,
        onClick = { /*TODO*/ },
        headlineContent = {
            Text(text = filename)
        },
        supportingContent = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (filename.startsWith("logs_manual"))
                        stringResource(R.string.log_type_manual)
                    else
                        stringResource(R.string.log_type_crash)
                )
                Text(
                    text = "${size / 1024} KB"
                )
            }
        }
    )
}

@Composable
fun CreateLogItem(
    modifier: Modifier = Modifier,
    onFocus: () -> Unit,
    onClick: () -> Unit,
) {
    ListItem(
        modifier = modifier
            .onFocusChanged {
                if (it.hasFocus) onFocus()
            },
        selected = false,
        onClick = onClick,
        headlineContent = {
            Text(text = stringResource(R.string.log_save_now_button))
        }
    )
}

@Preview
@Composable
fun LogItemPreview() {
    BVTheme {
        LogItem(
            filename = "logs_manual_3202-11-11_08:16:23.log",
            size = 2145,
            onFocus = {}
        )
    }
}
