package dev.aaa1115910.bv.util

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import de.schnettler.datastore.manager.PreferenceRequest
import dev.aaa1115910.bv.BVApp
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import java.util.Date

private val prefIsLoginKey = booleanPreferencesKey("il")
private val prefUidKey = longPreferencesKey("uid")
private val prefSidKey = stringPreferencesKey("sid")
private val prefSessDataKey = stringPreferencesKey("sd")
private val prefBiliJctKey = stringPreferencesKey("bj")
private val prefUidCkMd5Key = stringPreferencesKey("ucm")
private val prefTokenExpiredDateKey = longPreferencesKey("ted")

val prefIsLoginRequest = PreferenceRequest(prefIsLoginKey, false)
val prefUidRequest = PreferenceRequest(prefUidKey, 0)
val prefSidRequest = PreferenceRequest(prefSidKey, "")
val prefSessDataRequest = PreferenceRequest(prefSessDataKey, "")
val prefBiliJctRequest = PreferenceRequest(prefBiliJctKey, "")
val prefUidCkMd5Request = PreferenceRequest(prefUidCkMd5Key, "")
val prefTokenExpiredDateRequest = PreferenceRequest(prefTokenExpiredDateKey, 0)

//SESSDATA
object Prefs {
    val dsm = BVApp.dataStoreManager
    val logger = KotlinLogging.logger { }

    var isLogin: Boolean
        get() = runBlocking { dsm.getPreferenceFlow(prefIsLoginRequest).first() }
        set(value) = runBlocking { dsm.editPreference(prefIsLoginKey, value) }

    var uid: Long
        get() = runBlocking { dsm.getPreferenceFlow(prefUidRequest).first() }
        set(value) = runBlocking { dsm.editPreference(prefUidKey, value) }

    var sid: String
        get() = runBlocking { dsm.getPreferenceFlow(prefSidRequest).first() }
        set(value) = runBlocking { dsm.editPreference(prefSidKey, value) }

    var sessData: String
        get() = runBlocking { dsm.getPreferenceFlow(prefSessDataRequest).first() }
        set(value) = runBlocking { dsm.editPreference(prefSessDataKey, value) }

    var biliJct: String
        get() = runBlocking { dsm.getPreferenceFlow(prefBiliJctRequest).first() }
        set(value) = runBlocking { dsm.editPreference(prefBiliJctKey, value) }

    var uidCkMd5: String
        get() = runBlocking { dsm.getPreferenceFlow(prefUidCkMd5Request).first() }
        set(value) = runBlocking { dsm.editPreference(prefUidCkMd5Key, value) }

    var tokenExpiredData: Date
        get() = Date(runBlocking { dsm.getPreferenceFlow(prefTokenExpiredDateRequest).first() })
        set(value) = runBlocking { dsm.editPreference(prefTokenExpiredDateKey, value.time) }

    fun logout() {
        logger.info { "Logout uid: $uid" }
        isLogin = false
        uid = 0
        sid = ""
        sessData = ""
        biliJct = ""
        uidCkMd5 = ""
        tokenExpiredData = Date(0)
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
        this.tokenExpiredData = expiredDate
    }
}