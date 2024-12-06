package dev.aaa1115910.bv.screen.user.lock

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.entity.db.UserDB
import dev.aaa1115910.bv.repository.UserRepository
import dev.aaa1115910.bv.screen.user.UserItem
import dev.aaa1115910.bv.util.toast
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.launch
import org.koin.compose.getKoin

@Composable
fun UserLockSettingsScreen(
    modifier: Modifier = Modifier,
    userRepository: UserRepository = getKoin().get()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val logger = KotlinLogging.logger("UserLockSettingsScreen")

    var user by remember {
        mutableStateOf(
            UserDB(
                uid = -1,
                username = "None",
                avatar = "",
                auth = ""
            )
        )
    }

    LaunchedEffect(Unit) {
        val intent = (context as Activity).intent
        if (intent.hasExtra("uid")) {
            val uid = intent.getLongExtra("uid", 0)
            userRepository.findUserByUid(uid)
                ?.let { user = it }
                ?: let { context.finish() }
            logger.debug { "user $uid lock: ${user.lock}" }
        } else {
            context.finish()
        }
    }

    UserLockSettingsContent(
        modifier = modifier,
        user = user,
        onUpdateUser = {
            scope.launch {
                userRepository.updateUser(it)
                (context as Activity).finish()
            }
        },
        onExit = {
            (context as Activity).finish()
        }
    )
}

@Composable
private fun UserLockSettingsContent(
    modifier: Modifier = Modifier,
    user: UserDB,
    onUpdateUser: (UserDB) -> Unit,
    onExit: () -> Unit
) {
    val context = LocalContext.current

    val focusRequester = remember { FocusRequester() }
    var inputState by remember { mutableStateOf(InputState.InputOldPassword) }
    var inputPassword by remember { mutableStateOf("") }
    var lastInput by remember { mutableStateOf("") }
    val inputShow by remember {
        derivedStateOf {
            inputPassword
                .replace("u", "↑")
                .replace("d", "↓")
                .replace("l", "←")
                .replace("r", "→")
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()

    }
    LaunchedEffect(user) {
        inputState = if (user.lock.isNotBlank()) InputState.InputOldPassword
        else InputState.InputNewPassword
    }

    BackHandler(inputPassword.isNotEmpty()) {
    }

    Surface(
        modifier = modifier
            .clickable {}
            .focusRequester(focusRequester)
            .onPreviewKeyEvent { keyEvent ->
                if (keyEvent.nativeKeyEvent.action == android.view.KeyEvent.ACTION_DOWN) {
                    return@onPreviewKeyEvent true
                }

                when (keyEvent.key) {
                    Key.DirectionUp -> inputPassword += "u"
                    Key.DirectionDown -> inputPassword += "d"
                    Key.DirectionLeft -> inputPassword += "l"
                    Key.DirectionRight -> inputPassword += "r"

                    Key.DirectionCenter -> {
                        when (inputState) {
                            InputState.InputOldPassword -> {
                                if (inputPassword == user.lock) {
                                    inputState = InputState.InputNewPassword
                                    inputPassword = ""
                                } else {
                                    R.string.user_lock_toast_password_error.toast(context)
                                    inputPassword = ""
                                }
                            }

                            InputState.InputNewPassword -> {
                                if (inputPassword.isBlank()) {
                                    R.string.user_lock_toast_password_removed.toast(context)
                                    user.lock = ""
                                    onUpdateUser(user)
                                } else {
                                    lastInput = inputPassword
                                    inputPassword = ""
                                    inputState = InputState.ConfirmNewPassword
                                }
                            }

                            InputState.ConfirmNewPassword -> {
                                if (inputPassword == lastInput) {
                                    user.lock = inputPassword
                                    onUpdateUser(user)
                                } else {
                                    R.string.user_lock_toast_password_different.toast(context)
                                    inputPassword = ""
                                    inputState = InputState.InputNewPassword
                                }
                            }
                        }
                    }

                    Key.Back -> {
                        if (inputPassword.isNotEmpty()) {
                            inputPassword = inputPassword.dropLast(1)
                        } else {
                            onExit()
                        }
                    }
                }
                true
            },
        shape = RoundedCornerShape(0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 64.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = when (inputState) {
                        InputState.InputOldPassword -> stringResource(R.string.user_lock_title_input_old_password)
                        InputState.InputNewPassword -> stringResource(R.string.user_lock_title_input_new_password)
                        InputState.ConfirmNewPassword -> stringResource(R.string.user_lock_title_input_new_password_again)
                    },
                    style = MaterialTheme.typography.displaySmall
                )
            }

            LazyRow(
                modifier = Modifier.focusRequester(focusRequester),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                item {
                    UserItem(
                        avatar = user.avatar,
                        username = user.username
                    )
                }
            }

            Text(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 96.dp),
                text = inputShow,
                style = MaterialTheme.typography.displayLarge
            )

            Text(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 48.dp),
                text = stringResource(R.string.user_lock_input_tip),
                color = MaterialTheme.colorScheme.onSurface.copy(0.6f)
            )
        }
    }
}

private enum class InputState {
    InputOldPassword,
    InputNewPassword,
    ConfirmNewPassword
}
