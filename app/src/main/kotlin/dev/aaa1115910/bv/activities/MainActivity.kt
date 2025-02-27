package dev.aaa1115910.bv.activities

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
import dev.aaa1115910.bv.screen.MainScreen
import dev.aaa1115910.bv.screen.RegionBlockScreen
import dev.aaa1115910.bv.screen.user.lock.UnlockUserScreen
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
            var isCheckingNetwork by remember { mutableStateOf(true) }
            var isCheckingUserLock by remember { mutableStateOf(true) }
            val isChecking by remember {
                derivedStateOf { isCheckingNetwork || isCheckingUserLock }
            }
            var isMainlandChina by remember { mutableStateOf(false) }
            var userLockLocked by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                val user = userRepository.findUserByUid(userRepository.uid)
                userLockLocked = user?.lock?.isNotBlank() ?: false
                logger.info { "default user: ${user?.username}" }
                isCheckingUserLock = false
            }

            LaunchedEffect(Unit) {
                scope.launch(Dispatchers.Default) {
                    isMainlandChina = NetworkUtil.isMainlandChina()
                    isCheckingNetwork = false
                    keepSplashScreen = false
                }
            }

            BVTheme {
                if (isChecking) {
                    //避免在检查网络的期间加载屏幕内容，导致检查完毕后显示屏幕内容时出现初始焦点未成功设置的问题
                } else /*if (isMainlandChina) {
                    RegionBlockScreen()
                } else */{
                    //HomeScreen()
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

