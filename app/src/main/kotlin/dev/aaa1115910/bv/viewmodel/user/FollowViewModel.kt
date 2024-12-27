package dev.aaa1115910.bv.viewmodel.user

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.aaa1115910.biliapi.entity.user.FollowedUser
import dev.aaa1115910.biliapi.repositories.UserRepository
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.swapListWithMainContext
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.Collator
import java.util.Locale

class FollowViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    companion object {
        private val logger = KotlinLogging.logger { }
    }

    var followedUsers = mutableStateListOf<FollowedUser>()
    var updating by mutableStateOf(true)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            initFollowedUsers()
        }
    }

    private suspend fun initFollowedUsers() {
        runCatching {
            logger.fInfo { "Init followed users" }
            val followedUserList = userRepository.getFollowedUsers(
                mid = Prefs.uid,
                preferApiType = Prefs.apiType
            )
            logger.fInfo { "Followed user count: ${followedUserList.size}" }
            val sortedUserList = sortUsers(followedUserList)
            followedUsers.swapListWithMainContext(sortedUserList)
            logger.fInfo { "Load followed user finish" }
        }
        updating = false
    }

    private fun sortUsers(users: List<FollowedUser>): List<FollowedUser> {
        val sortedList = mutableStateListOf<FollowedUser>()
        val usersStartWithoutChinese =
            users.filter { Regex("^[A-Za-z0-9_-]").containsMatchIn(it.name) }
                .toMutableList()
        val usersStartWithChinese =
            (users - usersStartWithoutChinese.toSet()).toMutableList()

        usersStartWithoutChinese.sortWith { o1, o2 ->
            Collator.getInstance(Locale.CHINA).compare(o1.name, o2.name)
        }
        usersStartWithChinese.sortWith { o1, o2 ->
            Collator.getInstance(Locale.CHINA).compare(o1.name, o2.name)
        }

        logger.info { "sorted user which start without chinese: ${usersStartWithoutChinese.map { it.name }}" }
        logger.info { "sorted user which start with chinese: ${usersStartWithChinese.map { it.name }}" }

        sortedList.addAll(usersStartWithoutChinese)
        sortedList.addAll(usersStartWithChinese)

        return sortedList
    }
}