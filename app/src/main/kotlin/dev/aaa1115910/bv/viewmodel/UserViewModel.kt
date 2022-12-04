package dev.aaa1115910.bv.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.aaa1115910.biliapi.BiliApi
import dev.aaa1115910.biliapi.entity.user.MyInfoData
import dev.aaa1115910.bv.BVApp
import dev.aaa1115910.bv.repository.UserRepository
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.toast
import kotlinx.coroutines.launch
import mu.KotlinLogging

class UserViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    private var shouldUpdateInfo = true
    private val logger = KotlinLogging.logger { }
    val isLogin get() = userRepository.isLogin
    val username get() = userRepository.username
    val face get() = userRepository.face

    var responseData: MyInfoData? by mutableStateOf(null)

    fun updateUserInfo() {
        if (!shouldUpdateInfo || !userRepository.isLogin) return
        logger.info { "Update user info" }
        viewModelScope.launch {
            runCatching {
                responseData = BiliApi.getUserSelfInfo(sessData = Prefs.sessData).getResponseData()
                logger.info { "Update user info success" }
                shouldUpdateInfo = false
                userRepository.username = responseData!!.name
                userRepository.face = responseData!!.face
            }.onFailure {
               "获取用户信息失败：${it.message}".toast(BVApp.context)
            }
        }
    }

    fun logout() {
        userRepository.logout()
    }

    fun clearUserInfo() {
        userRepository.username = ""
        userRepository.face = ""
    }
}