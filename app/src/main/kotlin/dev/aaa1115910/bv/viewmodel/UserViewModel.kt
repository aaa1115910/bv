package dev.aaa1115910.bv.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.aaa1115910.biliapi.BiliApi
import dev.aaa1115910.bv.repository.UserRepository
import dev.aaa1115910.bv.util.Prefs
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

    fun updateUserInfo() {
        if (!shouldUpdateInfo || !userRepository.isLogin) return
        logger.info { "Update user info" }
        viewModelScope.launch {
            val responseData = BiliApi.getUserSelfInfo(sessData = Prefs.sessData).getResponseData()
            logger.info { "Update user info success" }
            shouldUpdateInfo = false
            userRepository.username = responseData.name
            userRepository.face = responseData.face
        }
    }

    fun clearUserInfo() {
        userRepository.username = ""
        userRepository.face = ""
    }
}