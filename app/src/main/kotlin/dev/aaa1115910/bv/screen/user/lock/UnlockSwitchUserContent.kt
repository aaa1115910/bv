package dev.aaa1115910.bv.screen.user.lock

import android.view.KeyEvent
import androidx.activity.compose.BackHandler
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
import androidx.compose.runtime.mutableFloatStateOf
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
import dev.aaa1115910.bv.util.toast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun UnlockSwitchUserContent(
    modifier: Modifier = Modifier,
    userList: List<UserDB>,
    unlockUser: UserDB?,
    onUnlockSuccess: (UserDB) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

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
    val unselectedUserAlpha by remember { mutableFloatStateOf(0.4f) }

    LaunchedEffect(Unit) {
        scope.launch {
            delay(200)
            println("request default focus")
            defaultFocusRequester.requestFocus()
        }
    }

    BackHandler(true) {
    }

    Surface(
        modifier = modifier
            .clickable {}
            .focusRequester(defaultFocusRequester)
            .onPreviewKeyEvent {
                if (it.nativeKeyEvent.action == KeyEvent.ACTION_DOWN) return@onPreviewKeyEvent true
                when (it.key) {
                    Key.DirectionUp -> inputPassword += "u"
                    Key.DirectionDown -> inputPassword += "d"
                    Key.DirectionLeft -> inputPassword += "l"
                    Key.DirectionRight -> inputPassword += "r"
                    Key.DirectionCenter -> {
                        if (unlockUser?.lock == inputPassword) {
                            onUnlockSuccess(unlockUser)
                        } else {
                            R.string.user_lock_toast_password_error.toast(context)
                            inputPassword = ""
                        }
                    }

                    Key.Back -> {
                        if (inputPassword.isNotBlank()) {
                            inputPassword = inputPassword.drop(1)
                        } else {
                            onCancel()
                        }
                    }
                }
                return@onPreviewKeyEvent true
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
                    text = stringResource(R.string.user_lock_title_input_password),
                    style = MaterialTheme.typography.displaySmall
                )
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                items(items = userList) { user ->
                    UserItem(
                        modifier = Modifier
                            .ifElse({ user != unlockUser }, Modifier.alpha(unselectedUserAlpha)),
                        avatar = user.avatar,
                        username = user.username,
                        lockEnabled = user.lock.isNotBlank(),
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