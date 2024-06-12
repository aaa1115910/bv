package dev.aaa1115910.biliapi.http.entity.user.garb

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class Equip(
    val item: Item? = null,
    val index: Int,
    val fan: JsonObject? = null,
    @SerialName("is_diy")
    val isDiy: Int,
    @SerialName("card_bg")
    val cardBg: CardBg? = null,
    @SerialName("previous_item")
    val previousItem: Item? = null,
    @SerialName("previous_index")
    val previousIndex: Int,
    @SerialName("previous_fan")
    val previousFan: JsonObject? = null,
    @SerialName("previous_is_diy")
    val previousIsDiy: Int,
    @SerialName("previous_card_bg")
    val previousCardBg: CardBg? = null,
)

enum class EquipPart(val value: String) {
    Card("card"),
    CardBg("card_bg"),
    Loading("loading"),
    PlayerIcon("play_icon"),
    Pendant("pendant"),
    Thumbup("thumbup"),
}
