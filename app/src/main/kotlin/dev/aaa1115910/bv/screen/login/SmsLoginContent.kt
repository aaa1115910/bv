package dev.aaa1115910.bv.screen.login

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.geetest.sdk.GT3ConfigBean
import com.geetest.sdk.GT3ErrorBean
import com.geetest.sdk.GT3GeetestUtils
import com.geetest.sdk.GT3Listener
import dev.aaa1115910.biliapi.repositories.SendSmsState
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.util.toast
import dev.aaa1115910.bv.viewmodel.login.GeetestResult
import dev.aaa1115910.bv.viewmodel.login.SmsLoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import org.json.JSONObject
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun SmsLoginContent(
    modifier: Modifier = Modifier,
    smsLoginViewModel: SmsLoginViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val logger = KotlinLogging.logger { }
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    var gt3GeetestUtils: GT3GeetestUtils? by remember { mutableStateOf(null) }
    val gt3ConfigBean by remember { mutableStateOf(GT3ConfigBean()) }
    var phoneNumberText by remember { mutableStateOf("") }
    var codeText by remember { mutableStateOf("") }

    val setConfig: (challenge: String, gt: String) -> Unit = { challenge, gt ->
        gt3GeetestUtils!!.startCustomFlow()
        gt3ConfigBean.api1Json = JSONObject().apply {
            put("success", 1)
            put("gt", gt)
            put("challenge", challenge)
        }
        gt3GeetestUtils!!.getGeetest()
    }

    val sendSms = {
        keyboardController?.hide()
        scope.launch(Dispatchers.IO) {
            runCatching {
                smsLoginViewModel.sendSms(phoneNumberText.toLong()) { challenge: String, gt: String ->
                    scope.launch(Dispatchers.Main) {
                        setConfig(challenge, gt)
                    }
                }
            }
        }
    }

    val loginWithSms = {
        keyboardController?.hide()
        if (smsLoginViewModel.sendSmsState != SendSmsState.Success) {
            R.string.sms_login_toast_send_sms_first.toast(context)
        } else {
            scope.launch(Dispatchers.IO) {
                runCatching {
                    smsLoginViewModel.loginWithSms(codeText.toInt()) {
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
                            smsLoginViewModel.sendSms(phoneNumberText.toLong()) { _, _ -> }
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

    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = phoneNumberText,
                    onValueChange = {
                        phoneNumberText = it
                        // Clear captcha data when phone number changed
                        smsLoginViewModel.clearCaptchaData()
                    },
                    label = { Text(text = stringResource(R.string.sms_login_phone_number)) },
                    maxLines = 1,
                    shape = MaterialTheme.shapes.large,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = { sendSms() }
                    )
                )
                Button(onClick = { sendSms() }) {
                    Text(text = stringResource(R.string.sms_login_button_send_sms))
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = codeText,
                    onValueChange = { codeText = it },
                    label = { Text(text = stringResource(R.string.sms_login_code)) },
                    maxLines = 1,
                    shape = MaterialTheme.shapes.large,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            loginWithSms()
                        }
                    )
                )
                Button(onClick = { loginWithSms() }) {
                    Text(text = stringResource(R.string.sms_login_button_login))
                }
            }
        }
    }
}