package dev.aaa1115910.bv.util

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import de.schnettler.datastore.manager.PreferenceRequest
import dev.aaa1115910.bv.BVApp
import dev.aaa1115910.bv.entity.Resolution
import dev.aaa1115910.bv.entity.VideoCodec
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
private val prefDefaultQualityKey = intPreferencesKey("dq")
private val prefDefaultDanmakuSizeKey = intPreferencesKey("dds")
private val prefDefaultDanmakuTransparencyKey = intPreferencesKey("ddt")
private val prefDefaultDanmakuEnabledKey = booleanPreferencesKey("dde")
private val prefDefaultDanmakuAreaKey = floatPreferencesKey("dda")
private val prefDefaultVideoCodecKey = intPreferencesKey("dvc")

val prefIsLoginRequest = PreferenceRequest(prefIsLoginKey, false)
val prefUidRequest = PreferenceRequest(prefUidKey, 0)
val prefSidRequest = PreferenceRequest(prefSidKey, "")
val prefSessDataRequest = PreferenceRequest(prefSessDataKey, "")
val prefBiliJctRequest = PreferenceRequest(prefBiliJctKey, "")
val prefUidCkMd5Request = PreferenceRequest(prefUidCkMd5Key, "")
val prefTokenExpiredDateRequest = PreferenceRequest(prefTokenExpiredDateKey, 0)
val prefDefaultQualityRequest = PreferenceRequest(prefDefaultQualityKey, Resolution.R1080P.code)
val prefDefaultDanmakuSizeRequest = PreferenceRequest(prefDefaultDanmakuSizeKey, 6)
val prefDefaultDanmakuTransparencyRequest = PreferenceRequest(prefDefaultDanmakuTransparencyKey, 0)
val prefDefaultDanmakuEnabledRequest = PreferenceRequest(prefDefaultDanmakuEnabledKey, true)
val prefDefaultDanmakuAreaRequest = PreferenceRequest(prefDefaultDanmakuAreaKey, 1f)
val prefDefaultVideoCodecRequest =
    PreferenceRequest(prefDefaultVideoCodecKey, VideoCodec.AVC.ordinal)

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

    var defaultQuality: Int
        get() = runBlocking { dsm.getPreferenceFlow(prefDefaultQualityRequest).first() }
        set(value) = runBlocking { dsm.editPreference(prefDefaultQualityKey, value) }

    var defaultDanmakuSize: Int
        get() = runBlocking { dsm.getPreferenceFlow(prefDefaultDanmakuSizeRequest).first() }
        set(value) = runBlocking { dsm.editPreference(prefDefaultDanmakuSizeKey, value) }

    var defaultDanmakuTransparency: Int
        get() = runBlocking { dsm.getPreferenceFlow(prefDefaultDanmakuTransparencyRequest).first() }
        set(value) = runBlocking { dsm.editPreference(prefDefaultDanmakuTransparencyKey, value) }

    var defaultDanmakuEnabled: Boolean
        get() = runBlocking { dsm.getPreferenceFlow(prefDefaultDanmakuEnabledRequest).first() }
        set(value) = runBlocking { dsm.editPreference(prefDefaultDanmakuEnabledKey, value) }

    var defaultDanmakuArea: Float
        get() = runBlocking { dsm.getPreferenceFlow(prefDefaultDanmakuAreaRequest).first() }
        set(value) = runBlocking { dsm.editPreference(prefDefaultDanmakuAreaKey, value) }

    var defaultVideoCodec: VideoCodec
        get() = VideoCodec.fromCode(
            runBlocking { dsm.getPreferenceFlow(prefDefaultVideoCodecRequest).first() }
        )
        set(value) = runBlocking { dsm.editPreference(prefDefaultDanmakuSizeKey, value.ordinal) }

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