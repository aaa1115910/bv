package dev.aaa1115910.biliapi.http.entity.video

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * @param code 作用尚不明确 pcg独有
 * @param isPreview 作用尚不明确 pcg独有
 * @param fnver 请求时提供的 fnver pcg独有
 * @param fnval 请求时提供的 fnval pcg独有
 * @param videoProject 作用尚不明确 pcg独有
 * @param type 视频流类型（DASH、FLV、MP4） pcg独有
 * @param bp 是否可以承包 pcg独有
 * @param vipType 当前用户大会员类型 pcg独有
 * @param vipStatus 当前用户大会员状态 pcg独有
 * @param isDrm 作用尚不明确 pcg独有
 * @param noRexcode 作用尚不明确 pcg独有
 * @param hasPaid 是否已购买 免费影片始终为 true pcg独有
 * @param status 作用尚不明确 pcg独有
 * @param from local 作用尚不明确
 * @param result suee 作用尚不明确
 * @param message 空 作用尚不明确
 * @param quality 当前的视频分辨率代码 值含义见上表
 * @param format 视频格式
 * @param timeLength 视频长度（毫秒值） 单位为毫秒 不同分辨率/格式可能有略微差异
 * @param acceptFormat 视频支持的全部格式 每项用,分隔
 * @param acceptDescription 视频支持的分辨率列表
 * @param acceptQuality 视频支持的分辨率代码列表 值含义见上表
 * @param videoCodecId 默认选择视频流的编码id 见视频编码代码
 * @param seekParam 固定值：start 作用尚不明确
 * @param seekType offset（dash、flv） second（mp4） 作用尚不明确
 * @param durl 视频分段 注：仅flv/mp4存在此项
 * @param dash dash音视频流信息 注：仅dash存在此项
 * @param supportFormats 支持格式的详细信息
 * @param high_format null
 * @param lastPlayTime 上次播放进度 毫秒值 非pgc接口独有
 * @param lastPlayCid 上次播放分p的cid 非pgc接口独有
 * @param clipInfoList 作用尚不明确 pcg独有
 * @param recordInfo 备案登记信息 pcg独有
 */
@Serializable
data class PlayUrlData(
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
    val dash: Dash? = null,
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

/**
 * 视频播放地址
 *
 * @param order 视频分段序号 某些视频会分为多个片段（从1顺序增长）
 * @param length 视频长度 单位为毫秒
 * @param size 视频大小 单位为Byte
 * @param ahead 空 作用尚不明确
 * @param vhead 空 作用尚不明确
 * @param url 视频流url 注：url内容存在转义符 有效时间为120min
 * @param backupUrl 备用视频流
 */
@Serializable
data class Durl(
    val order: Int,
    val length: Int,
    val size: Int,
    val ahead: String,
    val vhead: String,
    val url: String,
    @SerialName("backup_url")
    val backupUrl: List<String> = emptyList()
)

//TODO
@Serializable
data class Dash(
    val duration: Int,
    val minBufferTime: Float,
    val video: List<DashData> = emptyList(),
    val audio: List<DashData>? = null,
    val dolby: DashDolby = DashDolby(),
    val flac: DashFlac? = null
)

@Serializable
data class DashDolby(
    val audio: List<DashData>? = null,
    val type: Int = 2
)

@Serializable
data class DashFlac(
    val display: Boolean,
    val audio: DashData? = null
)

@Serializable
data class DashData(
    val id: Int,
    @SerialName("base_url")
    val baseUrl: String,
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

@Serializable
data class SegmentBase(
    val initialization: String,
    @SerialName("index_range")
    val indexRange: String
)

/**
 * 支持的视频格式
 *
 * @param quality 视频清晰度代码
 * @param format 视频格式
 * @param newDescription 格式描述
 * @param description 格式描述
 * @param displayDesc 格式描述
 * @param superScript (?)
 * @param codecs 可用编码格式列表
 * @param needLogin 需要登录
 * @param needVip 需要大会员
 */
@Serializable
data class SupportFormat(
    val quality: Int,
    val format: String,
    @SerialName("new_description")
    val newDescription: String,
    val description: String? = null,
    @SerialName("display_desc")
    val displayDesc: String,
    @SerialName("superscript")
    val superScript: String,
    val codecs: List<String>? = emptyList(),
    @SerialName("need_login")
    val needLogin: Boolean = false,
    @SerialName("need_vip")
    val needVip: Boolean = false
)

/**
 * 备案登记信息
 *
 * @param recordIcon
 * @param record 显示文案 登记号：10417060172092207
 */
@Serializable
data class RecordInfo(
    @SerialName("record_icon")
    val recordIcon: String,
    val record: String
)

@Serializable
enum class VideoQuality(val qn: Int, val displayName: String) {
    Q260P(6, "240P 极速"),
    Q360P(16, "360P 流畅"),
    Q480P(32, "480P 清晰"),
    Q720P(64, ""),
    Q720P60(74, ""),
    Q1080P(80, ""),
    Q1080PPlus(112, ""),
    Q1080P60(116, ""),
    Q4K(120, ""),
    HDR(125, ""),
    Dolby(126, ""),
    Q8K(127, "")
}