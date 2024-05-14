package dev.aaa1115910.biliapi.http.entity.proxy

import dev.aaa1115910.biliapi.http.entity.video.DashData
import dev.aaa1115910.biliapi.http.entity.video.DashFlac
import dev.aaa1115910.biliapi.http.entity.video.Durl
import dev.aaa1115910.biliapi.http.entity.video.RecordInfo
import dev.aaa1115910.biliapi.http.entity.video.SegmentBase
import dev.aaa1115910.biliapi.http.entity.video.SupportFormat
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class ProxyWebPlayUrlData(
    val code: Int = 0,
    @SerialName("is_preview")
    val isPreview: Int = 0,
    val fnver: Int = 0,
    val fnval: Int = 0,
    @SerialName("video_project")
    val videoProject: Boolean = false,
    val type: String = "",
    val bp: Int = 0,
    @SerialName("vip_type")
    val vipType: Int = 0,
    @SerialName("vip_status")
    val vipStatus: Int = 0,
    @SerialName("is_drm")
    val isDrm: Boolean = false,
    @SerialName("no_rexcode")
    val noRexcode: Int = 0,
    @SerialName("has_paid")
    val hasPaid: Boolean = false,
    val status: Int = 0,
    val from: String,
    val result: String,
    val message: String,
    val quality: Int,
    val format: String,
    @SerialName("timelength")
    val timeLength: Int,
    @SerialName("accept_format")
    val acceptFormat: String,
    @SerialName("accept_description")
    val acceptDescription: List<String> = emptyList(),
    @SerialName("accept_quality")
    val acceptQuality: List<Int> = emptyList(),
    @SerialName("video_codecid")
    val videoCodecId: Int,
    @SerialName("seek_param")
    val seekParam: String,
    @SerialName("seek_type")
    val seekType: String,
    val durl: List<Durl> = emptyList(),
    val dash: ProxyWebDash? = null,
    @SerialName("support_formats")
    val supportFormats: List<SupportFormat> = emptyList(),
    @SerialName("last_play_time")
    val lastPlayTime: Int = 0,
    @SerialName("last_play_cid")
    val lastPlaycid: Long = 0,
    @SerialName("clip_info_list")
    val clipInfoList: List<JsonElement> = emptyList(),
    @SerialName("record_info")
    val recordInfo: RecordInfo? = null
)

@Serializable
data class ProxyAppPlayUrlData(
    val code: Int = 0,
    @SerialName("is_preview")
    val isPreview: Int = 0,
    val fnver: Int = 0,
    val fnval: Int = 0,
    @SerialName("video_project")
    val videoProject: Boolean = false,
    val type: String = "",
    val bp: Int = 0,
    @SerialName("vip_type")
    val vipType: Int = 0,
    @SerialName("vip_status")
    val vipStatus: Int = 0,
    @SerialName("is_drm")
    val isDrm: Boolean = false,
    @SerialName("no_rexcode")
    val noRexcode: Int = 0,
    @SerialName("has_paid")
    val hasPaid: Boolean = false,
    val status: Int = 0,
    val from: String,
    val result: String,
    val message: String,
    val quality: Int,
    val format: String,
    @SerialName("timelength")
    val timeLength: Int,
    @SerialName("accept_format")
    val acceptFormat: String,
    @SerialName("accept_description")
    val acceptDescription: List<String> = emptyList(),
    @SerialName("accept_quality")
    val acceptQuality: List<Int> = emptyList(),
    @SerialName("video_codecid")
    val videoCodecId: Int,
    @SerialName("seek_param")
    val seekParam: String,
    @SerialName("seek_type")
    val seekType: String,
    val durl: List<Durl> = emptyList(),
    val dash: ProxyAppDash? = null,
    @SerialName("support_formats")
    val supportFormats: List<SupportFormat> = emptyList(),
    @SerialName("last_play_time")
    val lastPlayTime: Int = 0,
    @SerialName("last_play_cid")
    val lastPlaycid: Long = 0,
    @SerialName("clip_info_list")
    val clipInfoList: List<JsonElement> = emptyList(),
    @SerialName("record_info")
    val recordInfo: RecordInfo? = null
)

@Serializable
data class ProxyWebDash(
    val duration: Int,
    val minBufferTime: Float,
    val video: List<DashData> = emptyList(),
    val audio: List<DashData>? = null,
    val dolby: ProxyWebDashDolby = ProxyWebDashDolby(),
    val flac: DashFlac? = null
)

@Serializable
data class ProxyWebDashDolby(
    val audio: List<DashData>? = null,
    val type: Int = 2
)

@Serializable
data class ProxyAppDash(
    val duration: Int,
    @SerialName("min_buffer_time")
    val minBufferTime: Float,
    val video: List<ProxyAppDashData> = emptyList(),
    val audio: List<ProxyAppDashData>? = null,
    val dolby: ProxyAppDashDolby = ProxyAppDashDolby(),
    val flac: DashFlac? = null
)

@Serializable
data class ProxyAppDashDolby(
    val audio: List<DashData>? = null,
    val type: String = ""
)

@Serializable
data class ProxyAppDashData(
    val id: Int,
    @SerialName("base_url")
    val baseUrl: String,
    @SerialName("backup_url")
    val backupUrl: List<String> = emptyList(),
    val bandwidth: Int,
    @SerialName("mime_type")
    val mimeType: String,
    val codecs: String,
    val width: Int,
    val height: Int,
    @SerialName("frame_rate")
    val frameRate: String,
    val sar: String,
    @SerialName("start_with_sap")
    val startWithSap: Int,
    @SerialName("segment_base")
    val segmentBase: SegmentBase,
    @SerialName("codecid")
    val codecId: Int
)