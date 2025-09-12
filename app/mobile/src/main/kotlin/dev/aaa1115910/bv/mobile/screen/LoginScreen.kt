package dev.aaa1115910.bv.mobile.screen

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.geetest.sdk.GT3ConfigBean
import com.geetest.sdk.GT3ErrorBean
import com.geetest.sdk.GT3GeetestUtils
import com.geetest.sdk.GT3Listener
import dev.aaa1115910.biliapi.entity.login.QrLoginState
import dev.aaa1115910.biliapi.repositories.SendSmsState
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.component.QrImage
import dev.aaa1115910.bv.mobile.theme.BVMobileTheme
import dev.aaa1115910.bv.util.calculateWindowSizeClassInPreview
import dev.aaa1115910.bv.util.toast
import dev.aaa1115910.bv.viewmodel.login.AppQrLoginViewModel
import dev.aaa1115910.bv.viewmodel.login.GeetestResult
import dev.aaa1115910.bv.viewmodel.login.SmsLoginViewModel
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.json.JSONObject
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    smsLoginViewModel: SmsLoginViewModel = koinViewModel(),
    appQrLoginViewModel: AppQrLoginViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val logger = KotlinLogging.logger { }
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val windowSize = calculateWindowSizeClass(context as Activity)

    var gt3GeetestUtils: GT3GeetestUtils? by remember { mutableStateOf(null) }
    val gt3ConfigBean by remember { mutableStateOf(GT3ConfigBean()) }
    var phone by remember { mutableLongStateOf(0) }

    val setConfig: (challenge: String, gt: String) -> Unit = { challenge, gt ->
        gt3GeetestUtils!!.startCustomFlow()
        gt3ConfigBean.api1Json = JSONObject().apply {
            put("success", 1)
            put("gt", gt)
            put("challenge", challenge)
        }
        gt3GeetestUtils!!.getGeetest()
    }

    val sendSms: (Long) -> Unit = { phoneNumber ->
        phone = phoneNumber
        keyboardController?.hide()
        scope.launch(Dispatchers.IO) {
            runCatching {
                smsLoginViewModel.sendSms(phoneNumber) { challenge: String, gt: String ->
                    scope.launch(Dispatchers.Main) {
                        setConfig(challenge, gt)
                    }
                }
            }
        }
    }

    val loginWithSms: (Long, Int) -> Unit = { phoneNumber, code ->
        phone = phoneNumber
        keyboardController?.hide()
        if (smsLoginViewModel.sendSmsState != SendSmsState.Success) {
            R.string.sms_login_toast_send_sms_first.toast(context)
        } else {
            scope.launch(Dispatchers.IO) {
                runCatching {
                    smsLoginViewModel.loginWithSms(code) {
                        scope.launch(Dispatchers.Main) {
                            R.string.login_success.toast(context)
                        }
                        (context as Activity).finish()
                    }
                }
            }
        }
    }

    DisposableEffect(Unit) {
        gt3GeetestUtils = GT3GeetestUtils(context)
        gt3ConfigBean.apply {
            pattern = 1
            isCanceledOnTouchOutside = false
            lang = null
            timeout = 10000
            webviewTimeout = 10000
            corners = 24
            listener = object : GT3Listener() {
                override fun onReceiveCaptchaCode(p0: Int) {
                    logger.info { "Geetest - onReceiveCaptchaCode: $p0" }
                }

                override fun onStatistics(p0: String?) {
                    logger.info { "Geetest - onStatistics: $p0" }
                }

                override fun onClosed(p0: Int) {
                    logger.info { "Geetest - onClosed: $p0" }
                    smsLoginViewModel.clearCaptchaData()
                }

                override fun onSuccess(p0: String?) {
                    logger.info { "Geetest - onSuccess: $p0" }
                }

                override fun onFailed(p0: GT3ErrorBean?) {
                    logger.info { "Geetest - onFailed: $p0" }
                    smsLoginViewModel.clearCaptchaData()
                }

                override fun onButtonClick() {
                    logger.info { "Geetest - onButtonClick" }
                }

                override fun onDialogResult(result: String) {
                    logger.info { "Geetest - onDialogResult: $result" }
                    runCatching {
                        val geetestResult = Json.decodeFromString<GeetestResult>(result)
                        smsLoginViewModel.geetestChallenge = geetestResult.geetestChallenge
                        smsLoginViewModel.geetestValidate = geetestResult.geetestValidate
                        smsLoginViewModel.sendSmsState = SendSmsState.Ready
                        gt3GeetestUtils?.showSuccessDialog()
                        scope.launch(Dispatchers.IO) {
                            smsLoginViewModel.sendSms(phone) { _, _ -> }
                        }
                    }.onFailure {
                        gt3GeetestUtils?.showFailedDialog()
                    }
                }
            }
        }
        gt3GeetestUtils!!.init(gt3ConfigBean)

        onDispose {
            gt3GeetestUtils?.destory()
        }
    }

    LaunchedEffect(Unit) {
        appQrLoginViewModel.requestQRCode()
    }

    LaunchedEffect(appQrLoginViewModel.state) {
        when (appQrLoginViewModel.state) {
            QrLoginState.Success -> {
                R.string.login_success.toast(context)
                context.finish()
            }

            QrLoginState.Expired -> {
                appQrLoginViewModel.requestQRCode()
            }

            else -> {}
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            appQrLoginViewModel.cancelCheckLoginResultTimer()
        }
    }

    LoginContent(
        modifier = modifier,
        windowSize = windowSize,
        qrLoginUrl = appQrLoginViewModel.loginUrl,
        onBack = { context.finish() },
        onClearCaptchaData = { smsLoginViewModel.clearCaptchaData() },
        onSendSms = sendSms,
        onLogin = loginWithSms
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginContent(
    modifier: Modifier = Modifier,
    windowSize: WindowSizeClass,
    qrLoginUrl: String,
    onBack: () -> Unit,
    onClearCaptchaData: () -> Unit,
    onSendSms: (Long) -> Unit,
    onLogin: (phoneNumber: Long, code: Int) -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.title_mobile_activity_login))
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        when (windowSize.widthSizeClass) {
            WindowWidthSizeClass.Compact, WindowWidthSizeClass.Medium -> LoginContentCompact(
                modifier = Modifier.padding(innerPadding),
                qrLoginUrl = qrLoginUrl,
                onClearCaptchaData = onClearCaptchaData,
                onSendSms = onSendSms,
                onLogin = onLogin
            )

            WindowWidthSizeClass.Expanded -> LoginContentExpanded(
                modifier = Modifier.padding(innerPadding),
                qrLoginUrl = qrLoginUrl,
                onClearCaptchaData = onClearCaptchaData,
                onSendSms = onSendSms,
                onLogin = onLogin
            )
        }
    }
}

@Composable
fun LoginContentCompact(
    modifier: Modifier = Modifier,
    qrLoginUrl: String,
    onClearCaptchaData: () -> Unit,
    onSendSms: (Long) -> Unit,
    onLogin: (phoneNumber: Long, code: Int) -> Unit
) {
    var showQrCode by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SmsLoginInputs(
                modifier = Modifier
                    .padding(24.dp)
                    .widthIn(max = 400.dp),
                onClearCaptchaData = onClearCaptchaData,
                onSendSms = onSendSms,
                onLogin = onLogin
            )
            AnimatedVisibility(showQrCode) {
                QrImage(
                    modifier = Modifier
                        .padding(top = 36.dp)
                        .size(240.dp),
                    content = qrLoginUrl
                )
            }
        }
        if (!showQrCode) {
            TextButton(
                modifier = Modifier.align(Alignment.BottomCenter),
                onClick = { showQrCode = true }
            ) {
                Text(text = stringResource(dev.aaa1115910.bv.mobile.R.string.qr_login_button_login))
            }
        }
    }
}

@Composable
fun LoginContentExpanded(
    modifier: Modifier = Modifier,
    qrLoginUrl: String,
    onClearCaptchaData: () -> Unit,
    onSendSms: (Long) -> Unit,
    onLogin: (phoneNumber: Long, code: Int) -> Unit
) {
    Row(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            SmsLoginInputs(
                modifier = Modifier
                    .width(400.dp),
                onClearCaptchaData = onClearCaptchaData,
                onSendSms = onSendSms,
                onLogin = onLogin
            )
        }
        VerticalDivider(
            modifier = Modifier
                .fillMaxHeight(0.5f)
                .align(Alignment.CenterVertically)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            QrLogin(
                modifier = Modifier,
                qrLoginUrl = qrLoginUrl
            )

        }
    }
}

@Composable
fun SmsLoginInputs(
    modifier: Modifier = Modifier,
    onClearCaptchaData: () -> Unit,
    onSendSms: (Long) -> Unit,
    onLogin: (phoneNumber: Long, code: Int) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    var phoneNumberText by remember { mutableStateOf("") }
    val phoneNumber by remember(phoneNumberText) {
        mutableLongStateOf(runCatching { phoneNumberText.toLong() }.getOrNull() ?: 0)
    }
    var codeText by remember { mutableStateOf("") }
    val code by remember(codeText) {
        mutableIntStateOf(runCatching { codeText.toInt() }.getOrNull() ?: 0)
    }

    val sendSmsButtonEnabled by remember(phoneNumber) {
        derivedStateOf { phoneNumber != 0L && phoneNumberText.length == 11 }
    }
    val loginButtonEnabled by remember(code) {
        derivedStateOf { sendSmsButtonEnabled && code != 0 && codeText.length == 6 }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = phoneNumberText,
            onValueChange = {
                phoneNumberText = it
                onClearCaptchaData()
            },
            label = { Text(text = stringResource(R.string.sms_login_phone_number)) },
            maxLines = 1,
            shape = MaterialTheme.shapes.medium,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Send
            ),
            keyboardActions = KeyboardActions(
                onSend = {
                    if (sendSmsButtonEnabled) {
                        onSendSms(phoneNumber)
                        keyboardController?.hide()
                    }
                }
            ),
            trailingIcon = {
                TextButton(
                    onClick = {
                        onSendSms(phoneNumber)
                        keyboardController?.hide()
                    },
                    enabled = sendSmsButtonEnabled
                ) {
                    Text(text = stringResource(R.string.sms_login_button_send_sms))
                }
            }
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = codeText,
            onValueChange = { codeText = it },
            label = { Text(text = stringResource(R.string.sms_login_code)) },
            maxLines = 1,
            shape = MaterialTheme.shapes.medium,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (loginButtonEnabled) {
                        onLogin(phoneNumber, code)
                        keyboardController?.hide()
                    }
                }
            )
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onLogin(phoneNumber, code)
                keyboardController?.hide()
            },
            enabled = loginButtonEnabled
        ) {
            Text(text = stringResource(R.string.sms_login_button_login))
        }
    }
}

@Composable
fun QrLogin(
    modifier: Modifier = Modifier,
    qrLoginUrl: String
) {
    Box(
        modifier = modifier
    ) {
        QrImage(
            modifier = Modifier.size(240.dp),
            content = qrLoginUrl
        )
    }
}

@Preview
@Composable
fun SmsLoginInputsPreview() {
    BVMobileTheme {
        Surface {
            SmsLoginInputs(
                modifier = Modifier.padding(24.dp),
                onClearCaptchaData = {},
                onSendSms = {},
                onLogin = { _, _ -> }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview
@Preview(device = "spec:width=1280dp,height=800dp,dpi=240")
@Preview(device = "spec:width=1280dp,height=800dp,dpi=240,orientation=portrait")
@Composable
private fun LoginScreenPreview() {
    val windowSize = calculateWindowSizeClassInPreview()

    BVMobileTheme {
        LoginContent(
            modifier = Modifier,
            windowSize = windowSize,
            qrLoginUrl = "https://www.example.com",
            onBack = {},
            onClearCaptchaData = {},
            onSendSms = { _ -> },
            onLogin = { _, _ -> }
        )
    }
}
