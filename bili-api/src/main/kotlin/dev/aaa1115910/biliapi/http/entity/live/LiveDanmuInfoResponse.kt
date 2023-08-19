package dev.aaa1115910.biliapi.http.entity.live

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DanmuInfoData(
    val group: String,
    @SerialName("business_id")
    val businessId: Int,
    @SerialName("refresh_row_factor")
    val refreshRowFactor: Float,
    @SerialName("refresh_rate")
    val refreshRate: Int,
    @SerialName("max_delay")
    val maxDelay: Int,
    val token: String,
    @SerialName("host_list")
    val hostList: List<HostListItem> = emptyList()
)

@Serializable
data class HostListItem(
    val host: String,
    val port: Int,
    @SerialName("wss_port")
    val wssPort: Int,
    @SerialName("ws_port")
    val wsPort: Int
)