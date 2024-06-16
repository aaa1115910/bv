package dev.aaa1115910.bv.repository

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.aaa1115910.biliapi.http.BiliHttpApi
import dev.aaa1115910.biliapi.repositories.AuthRepository
import dev.aaa1115910.bv.BVApp
import dev.aaa1115910.bv.dao.AppDatabase
import dev.aaa1115910.bv.entity.AuthData
import dev.aaa1115910.bv.entity.db.UserDB
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.fInfo
import io.github.oshai.kotlinlogging.KotlinLogging
import java.util.Date

class UserRepository(
    private val authRepository: AuthRepository,
    private val db: AppDatabase = BVApp.getAppDatabase()
) {
    companion object {
        private val logger = KotlinLogging.logger { }
    }

    var isLogin by mutableStateOf(Prefs.isLogin)
    var uid by mutableLongStateOf(Prefs.uid)
    var uidCkMd5 by mutableStateOf(Prefs.uidCkMd5)
    var sid by mutableStateOf(Prefs.sid)
    var sessData by mutableStateOf(Prefs.sessData)
    var biliJct by mutableStateOf(Prefs.biliJct)
    var expiredDate by mutableStateOf(Prefs.tokenExpiredData)

    var accessToken by mutableStateOf(Prefs.accessToken)
    var refreshToken by mutableStateOf(Prefs.refreshToken)

    var username by mutableStateOf("")
    var avatar by mutableStateOf("")

    private fun reloadFromPrefs() {
        logger.info { "Reload auth data from prefs" }

        uid = Prefs.uid
        uidCkMd5 = Prefs.uidCkMd5
        sid = Prefs.sid
        sessData = Prefs.sessData
        biliJct = Prefs.biliJct
        isLogin = Prefs.isLogin
        expiredDate = Prefs.tokenExpiredData
        accessToken = Prefs.accessToken
        refreshToken = Prefs.refreshToken
    }

    private fun saveToPrefs(authData: AuthData) {
        logger.info { "Save auth data to prefs" }

        Prefs.uid = authData.uid
        Prefs.uidCkMd5 = authData.uidCkMd5
        Prefs.sid = authData.sid
        Prefs.sessData = authData.sessData
        Prefs.biliJct = authData.biliJct
        Prefs.isLogin = true
        Prefs.tokenExpiredData = Date(authData.tokenExpiredData)
        Prefs.accessToken = authData.accessToken
        Prefs.refreshToken = authData.refreshToken

        updateAuthRepository()
    }

    private fun saveToPrefs() {
        logger.info { "Save auth data to prefs" }

        Prefs.uid = uid
        Prefs.uidCkMd5 = uidCkMd5
        Prefs.sid = sid
        Prefs.sessData = sessData
        Prefs.biliJct = biliJct
        Prefs.isLogin = isLogin
        Prefs.tokenExpiredData = expiredDate
        Prefs.accessToken = accessToken
        Prefs.refreshToken = refreshToken

        updateAuthRepository()
    }

    suspend fun logout() {
        val user = db.userDao().findUserByUid(uid)
        user?.let {
            db.userDao().delete(it)
            logger.info { "Delete user $uid in user db" }
        } ?: let {
            logger.info { "Not found user $uid in user db" }
        }
        clearAuth()
    }

    private fun clearAuth() {
        logger.info { "Clear auth data in UserRepository" }
        uid = 0
        uidCkMd5 = ""
        sid = ""
        sessData = ""
        biliJct = ""
        isLogin = false
        expiredDate = Date(0)
        accessToken = ""
        refreshToken = ""
        saveToPrefs()
    }

    private fun updateAuthRepository() {
        authRepository.sessionData = sessData
        authRepository.biliJct = biliJct
        authRepository.accessToken = accessToken
        authRepository.mid = uid
        authRepository.buvid3 = Prefs.buvid3
    }

    suspend fun setUser(user: UserDB) {
        saveToPrefs(AuthData.fromJson(user.auth))
        reloadFromPrefs()
        BVApp.instance?.initRepository()
        BVApp.instance?.initProxy()
        updateAvatar()
    }

    suspend fun addUser(authData: AuthData) {
        val existUser = db.userDao().findUserByUid(authData.uid)
        existUser?.let {
            it.auth = authData.toJson()
            db.userDao().update(it)
        } ?: let {
            val newUser = UserDB(
                uid = authData.uid,
                username = "User ${authData.uid}",
                avatar = "https://i0.hdslb.com/bfs/article/b6b843d84b84a3ba5526b09ebf538cd4b4c8c3f3.jpg",
                auth = authData.toJson()
            )
            db.userDao().insert(newUser)
        }
        saveToPrefs(authData)
        reloadFromPrefs()
        BVApp.instance?.initRepository()
        BVApp.instance?.initProxy()
        updateAvatar()
    }

    suspend fun updateAvatar() {
        val user = db.userDao().findUserByUid(uid)
        user?.let {
            runCatching {
                val responseData =
                    BiliHttpApi.getUserSelfInfo(sessData = Prefs.sessData).getResponseData()
                logger.fInfo { "Updating user name and avatar" }
                username = responseData.name
                avatar = responseData.face
                user.username = username
                user.avatar = avatar
                db.userDao().update(user)
            }.onFailure {
                logger.info {
                    "Update user name and avatar failed: ${it.stackTraceToString()}"
                }
            }
        }
    }

    suspend fun reloadAvatar() {
        val user = db.userDao().findUserByUid(uid)
        user?.let {
            username = it.username
            avatar = it.avatar
        }
    }

    suspend fun findUserByUid(uid: Long): UserDB? {
        return db.userDao().findUserByUid(uid)
    }

    suspend fun updateUser(user: UserDB){
        db.userDao().update(user)
    }
}