package dev.aaa1115910.bv.screen.user.lock

import android.view.KeyEvent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.alpha
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
import dev.aaa1115910.bv.component.ifElse
import dev.aaa1115910.bv.entity.db.UserDB
import dev.aaa1115910.bv.screen.user.UserItem
import dev.aaa1115910.bv.screen.user.UserSwitchViewModel
import dev.aaa1115910.bv.util.toast
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun UnlockUserScreen(
    modifier: Modifier = Modifier,
    userSwitchViewModel: UserSwitchViewModel = koinViewModel(),
    onUnlockSuccess: (UserDB) -> Unit
) {
    val scope = rememberCoroutineScope()
    val userList = userSwitchViewModel.userDbList
    var selectedUser: UserDB? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        userSwitchViewModel.updateData()
    }

    UnlockUserContent(
        modifier = modifier,
        userList = userList,
        selectedUser = selectedUser,
        onSelectedUserChange = { user ->
            selectedUser = user
        },
        onUnlockSuccess = { user ->
            scope.launch {
                userSwitchViewModel.switchUser(user)
                onUnlockSuccess(user)
            }
        }
    )
}

@Composable
private fun UnlockUserContent(
    modifier: Modifier = Modifier,
    userList: List<UserDB>,
    selectedUser: UserDB?,
    onSelectedUserChange: (UserDB) -> Unit,
    onUnlockSuccess: (UserDB) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val logger = KotlinLogging.logger("UnlockUserContent")

    val inputFocusRequester = remember { FocusRequester() }
    val defaultFocusRequester = remember { FocusRequester() }
    var inputPassword by remember { mutableStateOf("") }
    val inputShow by remember {
        derivedStateOf {
            inputPassword
                .replace("u", "*")
                .replace("d", "*")
                .replace("l", "*")
                .replace("r", "*")
        }
    }
    var unlockState by remember { mutableStateOf(UnlockState.ChooseUser) }
    val unChosenUserAlpha by animateFloatAsState(
        targetValue = when (unlockState) {
            UnlockState.ChooseUser -> 1f
            UnlockState.InputPassword -> 0.4f
        },
        label = "unchosen user alpha"
    )

    LaunchedEffect(userList) {
        scope.launch {
            delay(200)
            defaultFocusRequester.requestFocus()
        }
    }

    LaunchedEffect(unlockState) {
        scope.launch {
            delay(100)
            inputFocusRequester.requestFocus()
        }
    }

    BackHandler(true) {
    }

    Surface(
        modifier = modifier
            .ifElse({ unlockState == UnlockState.InputPassword }, Modifier.clickable {})
            .focusRequester(inputFocusRequester)
            .onPreviewKeyEvent {
                when (unlockState) {
                    UnlockState.ChooseUser -> return@onPreviewKeyEvent false
                    UnlockState.InputPassword -> {
                        if (it.nativeKeyEvent.action == KeyEvent.ACTION_DOWN) return@onPreviewKeyEvent true
                        when (it.key) {
                            Key.DirectionUp -> inputPassword += "u"
                            Key.DirectionDown -> inputPassword += "d"
                            Key.DirectionLeft -> inputPassword += "l"
                            Key.DirectionRight -> inputPassword += "r"
                            Key.DirectionCenter -> {
                                if (selectedUser?.lock == inputPassword) {
                                    onUnlockSuccess(selectedUser)
                                } else {
                                    "密码错误".toast(context)
                                    inputPassword = ""
                                }
                            }

                            Key.Back -> {
                                if (inputPassword.isNotBlank()) {
                                    inputPassword = inputPassword.drop(1)
                                } else {
                                    unlockState = UnlockState.ChooseUser
                                    defaultFocusRequester.requestFocus()
                                }
                            }
                        }
                        return@onPreviewKeyEvent true
                    }
                }
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
                    text = when (unlockState) {
                        UnlockState.ChooseUser -> stringResource(R.string.user_lock_title_choose_user)
                        UnlockState.InputPassword -> stringResource(R.string.user_lock_title_input_password)
                    },
                    style = MaterialTheme.typography.displaySmall
                )
            }

            LazyRow(
                modifier = Modifier.focusRequester(defaultFocusRequester),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                items(items = userList) { user ->
                    UserItem(
                        modifier = Modifier
                            .ifElse({ user != selectedUser }, Modifier.alpha(unChosenUserAlpha)),
                        avatar = user.avatar,
                        username = user.username,
                        lockEnabled = user.lock.isNotBlank(),
                        onClick = {
                            logger.info { "Choose user ${user.uid}" }
                            if (user.lock.isNotBlank()) {
                                onSelectedUserChange(user)
                                unlockState = UnlockState.InputPassword
                            } else {
                                onSelectedUserChange(user)
                                onUnlockSuccess(user)
                            }
                        }.takeIf { unlockState == UnlockState.ChooseUser }
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

            if (unlockState == UnlockState.InputPassword) {
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
}

private enum class UnlockState {
    ChooseUser,
    InputPassword
}