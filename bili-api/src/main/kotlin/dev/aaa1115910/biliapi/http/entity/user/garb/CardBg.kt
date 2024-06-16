package dev.aaa1115910.biliapi.http.entity.user.garb

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class CardBg(
    @SerialName("act_id")
    val actId: Int,
    val level: Int,
    @SerialName("bg_no")
    val bgNo: Int,
    val color: String,
    @SerialName("no_prefix")
    val noPrefix: String,
    @SerialName("no_color_format")
    val noColorFormat: JsonObject? = null
)
