package dev.aaa1115910.biliapi.http.entity.live

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RoomPlayInfoData(
    @SerialName("room_id")
    val roomId: Int,
    @SerialName("short_id")
    val shortId: Int,
    val uid: Long,
    @SerialName("need_p2p")
    val needP2P: Int,
    @SerialName("is_hidden")
    val isHidden: Boolean,
    @SerialName("is_locked")
    val isLocked: Boolean,
    @SerialName("is_portrait")
    val isPortrait: Boolean,
    @SerialName("live_status")
    val liveStatus: Int,
    @SerialName("hidden_till")
    val hiddenTill: Int,
    @SerialName("lock_till")
    val lockTill: Int,
    val encrypted: Boolean,
    @SerialName("pwd_verified")
    val pwdVerified: Boolean,
    @SerialName("live_time")
    val liveTime: Int,
    @SerialName("room_shield")
    val roomShield: Int,
    @SerialName("is_sp")
    val isSp: Int,
    @SerialName("special_type")
    val specialType: Int,
    val playUrl: String? = null,
    @SerialName("all_special_types")
    val allSpecialTypes: List<Int> = emptyList()
)
/*
"data": {
		"play_url": null,
		"all_special_types": [
			50
		]
 */