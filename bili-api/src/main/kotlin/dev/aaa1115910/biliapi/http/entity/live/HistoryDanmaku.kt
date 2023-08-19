package dev.aaa1115910.biliapi.http.entity.live

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive

@Serializable
data class HistoryDanmaku(
    //val admin:List<Any>,
    val room: List<HistoryDanmakuItem>
) {
    @Serializable
    data class HistoryDanmakuItem(
        val text: String,
        @SerialName("dm_type")
        val dmType: Int,
        val uid: Long,
        val nickname: String,
        @SerialName("uname_color")
        val unameColor: String,
        val timeline: String,
        @SerialName("isadmin")
        val isAdmin: Int,
        val vip: Int,
        val svip: Int,
        @SerialName("medal")
        private val _medal: List<JsonElement>,
        @Transient
        var medal: Medal? = null,
        val title: List<String>,
        @SerialName("user_level")
        val userLevel: List<JsonElement>,
        val rank: Int,
        @SerialName("teamid")
        val teamId: Int,
        val rnd: Int,
        @SerialName("user_title")
        val userTitle: String,
        @SerialName("guard_level")
        val guardLevel: Int,
        val bubble: Int,
        @SerialName("bubble_color")
        val bubbleColor: String,
        val lpl: Int,
        @SerialName("yeah_space_url")
        val yeahSpaceUrl: String,
        @SerialName("jump_to_url")
        val jumpToUrl: String,
        @SerialName("check_info")
        val checkInfo: CheckInfo,
        @SerialName("voice_dm_info")
        val voiceDmInfo: VoiceDmInfo,
        val emoticon: Emoticon
    ) {
        init {
            medal = runCatching {
                Medal(
                    level = _medal[0].jsonPrimitive.int,
                    name = _medal[1].jsonPrimitive.content,
                    up = _medal[2].jsonPrimitive.content,
                    roomId = _medal[3].jsonPrimitive.int
                )
            }.getOrNull()
        }
    }
}

/**
 * 粉丝勋章
 *
 * 返回样例
 * ```
 *  [
 *      16,         //level
 *      "迷你鲨",    //name
 *      "hufang360",//up
 *      22739471,   //up room id
 *      12478086,
 *      "",
 *      0,
 *      12478086,   //medal_color_border（可能是）
 *      12478086,   //medal_color_end（可能是）
 *      12478086,   //medal_color_start（可能是）
 *      0,
 *      1,
 *      4328524
 *  ]
 * ```
 *
 * @param level 勋章等级
 * @param name 勋章名称
 * @param up 主播昵称
 * @param roomId 主播房间号
 */
data class Medal(
    val level: Int,
    val name: String,
    val up: String,
    val roomId: Int
)

/*
"medal": [
					16,         //level
					"迷你鲨",    //name
					"hufang360",//up
					22739471,   //up room id
					12478086,
					"",
					0,
					12478086,   //medal_color_border
					12478086,   //medal_color_end
					12478086,   //medal_color_start
					0,
					1,
					4328524
				],
 */

@Serializable
data class CheckInfo(
    val ts: Int,
    val ct: String
)

@Serializable
data class VoiceDmInfo(
    @SerialName("voice_url")
    val voiceUrl: String,
    @SerialName("file_format")
    val fileFormat: String,
    val text: String,
    @SerialName("file_duration")
    val fileDuration: Int,
    @SerialName("file_id")
    val fileId: String
)

@Serializable
data class Emoticon(
    val id: Int,
    @SerialName("emoticon_unique")
    val emoticonUnique: String,
    val text: String,
    val perm: Int,
    val url: String,
    @SerialName("in_player_area")
    val inPlayerArea: Int,
    @SerialName("bulge_display")
    val bulgeDisplay: Int,
    @SerialName("is_dynamic")
    val isDynamic: Int,
    val height: Int,
    val width: Int
)
