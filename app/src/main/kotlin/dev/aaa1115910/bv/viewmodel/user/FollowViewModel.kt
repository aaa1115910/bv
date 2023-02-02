package dev.aaa1115910.bv.viewmodel.user

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.aaa1115910.biliapi.BiliApi
import dev.aaa1115910.biliapi.entity.user.UserFollowData
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.swapList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mu.KotlinLogging
import java.text.Collator
import java.util.Locale
import kotlin.math.ceil

class FollowViewModel : ViewModel() {
    companion object {
        private val logger = KotlinLogging.logger { }
    }

    var followedUsers = mutableStateListOf<UserFollowData.FollowedUser>()
    var updating by mutableStateOf(true)
    private val resultMap = mutableMapOf<Int, Boolean>()

    init {
        viewModelScope.launch(Dispatchers.Default) {
            initFollowedUsers()
        }
    }

    private suspend fun initFollowedUsers() {
        runCatching {
            logger.fInfo { "Init followed users" }
            val response = BiliApi.getUserFollow(
                mid = Prefs.uid,
                sessData = Prefs.sessData
            ).getResponseData()
            followedUsers.addAll(response.list)
            val totalSize = response.total
            logger.fInfo { "Followed user count: $totalSize" }
            val r = ceil((totalSize.toFloat() / 50)).toInt()
            resultMap[0] = true
            for (i in 1 until r) {
                resultMap[i] = false
            }

            var retry = 0
            while (retry < 10 && resultMap.containsValue(false)) {
                updateData()
                retry++
            }

            followedUsers.swapList(sortUsers())
            println("Load followed user finish")
        }
        updating = false
    }

    private suspend fun updateData() {
        resultMap.forEach { (index, done) ->
            runCatching {
                logger.info { "Loading followed users, page number: ${index + 1}" }
                if (!done) {
                    val response = BiliApi.getUserFollow(
                        mid = Prefs.uid,
                        pageNumber = index + 1,
                        sessData = Prefs.sessData
                    ).getResponseData()
                    followedUsers.addAll(response.list)
                    resultMap[index] = true
                }
                println(resultMap)
            }.onFailure {
                logger.fInfo { "Loading index failed: ${it.printStackTrace()}" }
            }
        }
    }

    private fun sortUsers(): List<UserFollowData.FollowedUser> {
        val sortedList = mutableStateListOf<UserFollowData.FollowedUser>()
        val usersStartWithoutChinese =
            followedUsers.filter { Regex("^[A-Za-z0-9_-]").containsMatchIn(it.uname) }
                .toMutableList()
        val usersStartWithChinese =
            (followedUsers - usersStartWithoutChinese.toSet()).toMutableList()

        usersStartWithoutChinese.sortWith { o1, o2 ->
            Collator.getInstance(Locale.CHINA).compare(o1.uname, o2.uname)
        }
        usersStartWithChinese.sortWith { o1, o2 ->
            Collator.getInstance(Locale.CHINA).compare(o1.uname, o2.uname)
        }

        println(usersStartWithoutChinese.map { it.uname })
        println(usersStartWithChinese.map { it.uname })

        sortedList.addAll(usersStartWithoutChinese)
        sortedList.addAll(usersStartWithChinese)

        return sortedList
    }
}