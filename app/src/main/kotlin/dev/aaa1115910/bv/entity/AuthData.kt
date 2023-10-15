package dev.aaa1115910.bv.entity

import dev.aaa1115910.bv.util.Prefs
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.Date

@Serializable
data class AuthData(
    @SerialName("DedeUserID")
    val uid: Long,
    @SerialName("DedeUserID__ckMd5")
    val uidCkMd5: String,
    val sid: String,
    @SerialName("bili_jct")
    val biliJct: String,
    @SerialName("SESSDATA")
    val sessData: String,
    @SerialName("expired_date")
    val tokenExpiredData: Long,
    @SerialName("access_token")
    val accessToken: String = "",
    @SerialName("refresh_token")
    val refreshToken: String = ""
) {
    companion object {
        fun fromJson(json: String): AuthData {
            return Json.decodeFromString(json)
        }

        fun fromPrefs(): AuthData {
            return AuthData(
                Prefs.uid,
                Prefs.uidCkMd5,
                Prefs.sid,
                Prefs.biliJct,
                Prefs.sessData,
                Prefs.tokenExpiredData.time,
                Prefs.accessToken,
                Prefs.refreshToken
            )
        }
    }

    fun toJson(): String = Json.encodeToString(this)
    fun saveToPrefs() {
        Prefs.uid = uid
        Prefs.uidCkMd5 = uidCkMd5
        Prefs.sid = sid
        Prefs.biliJct = biliJct
        Prefs.sessData = sessData
        Prefs.tokenExpiredData = Date(tokenExpiredData)
        Prefs.accessToken = accessToken
        Prefs.refreshToken = refreshToken
        Prefs.isLogin = true
    }
}
