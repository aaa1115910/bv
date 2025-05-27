package dev.aaa1115910.bv.tv.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dev.aaa1115910.bv.repository.UserRepository
import dev.aaa1115910.bv.tv.screens.MainScreen
import dev.aaa1115910.bv.tv.screens.RegionBlockScreen
import dev.aaa1115910.bv.tv.screens.user.lock.UnlockUserScreen
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.util.NetworkUtil
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val userRepository: UserRepository by inject()
    private val logger = KotlinLogging.logger {}

    override fun onCreate(savedInstanceState: Bundle?) {
        var keepSplashScreen = true
        installSplashScreen().apply {
            setKeepOnScreenCondition { keepSplashScreen }
        }
        super.onCreate(savedInstanceState)

        setContent {
            val scope = rememberCoroutineScope()
            var isCheckingUserLock by remember { mutableStateOf(true) }
            var userLockLocked by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                scope.launch(Dispatchers.Default) {
                    val user = userRepository.findUserByUid(userRepository.uid)
                    userLockLocked = user?.lock?.isNotBlank() ?: false
                    logger.info { "default user: ${user?.username}" }
                    isCheckingUserLock = false
                    keepSplashScreen = false
                }
            }

            BVTheme {
                if (isCheckingUserLock) {
                    // 保持空白界面直到检查完成
                } else {
                    if (!userLockLocked) {
                        MainScreen()
                    } else {
                        UnlockUserScreen(
                            onUnlockSuccess = { user ->
                                logger.info { "unlock user lock for user ${user.uid}" }
                                userLockLocked = false
                            }
                        )
                    }
                }
            }
        }
    }
}

