package dev.aaa1115910.bv.repository

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.aaa1115910.bv.util.Prefs
import mu.KotlinLogging
import java.util.Date

class UserRepository {
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

    var username by mutableStateOf("")
    var face by mutableStateOf("")

    fun reloadFromPrefs() {
        uid = Prefs.uid
        uidCkMd5 = Prefs.uidCkMd5
        sid = Prefs.sid
        sessData = Prefs.sessData
        biliJct = Prefs.biliJct
        isLogin = Prefs.isLogin
        expiredDate = Prefs.tokenExpiredData
    }

    fun saveToPrefs() {
        Prefs.uid = uid
        Prefs.uidCkMd5 = uidCkMd5
        Prefs.sid = sid
        Prefs.sessData = sessData
        Prefs.biliJct = biliJct
        Prefs.isLogin = isLogin
        Prefs.tokenExpiredData = expiredDate
    }

    fun setLoginData(
        uid: Long,
        uidCkMd5: String,
        sid: String,
        sessData: String,
        biliJct: String,
        expiredDate: Date
    ) {
        this.uid = uid
        this.uidCkMd5 = uidCkMd5
        this.sid = sid
        this.sessData = sessData
        this.biliJct = biliJct
        this.isLogin = true
        this.expiredDate = expiredDate
        saveToPrefs()
    }

    fun logout() {
        uid = 0
        uidCkMd5 = ""
        sid = ""
        sessData = ""
        biliJct = ""
        isLogin = false
        expiredDate = Date(0)
        saveToPrefs()
    }

}