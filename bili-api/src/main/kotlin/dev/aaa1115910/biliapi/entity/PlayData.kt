package dev.aaa1115910.biliapi.entity

import bilibili.app.playerunite.v1.PlayViewUniteReply
import bilibili.pgc.gateway.player.v2.dashVideoOrNull
import bilibili.pgc.gateway.player.v2.dolbyOrNull
import bilibili.playershared.dashVideoOrNull
import bilibili.playershared.dolbyOrNull
import bilibili.playershared.lossLessItemOrNull

data class PlayData(
    val dashVideos: List<DashVideo>,
    val dashAudios: List<DashAudio>,
    val dolby: DashAudio? = null,
    val flac: DashAudio? = null,
    val codec: Map<Int, List<String>> = emptyMap(),
    val needPay: Boolean = false,
) {
    companion object {
        fun fromPlayViewUniteReply(playViewUniteReply: PlayViewUniteReply): PlayData {
            val streamList =
                playViewUniteReply.vodInfo.streamListList.filter { it.dashVideoOrNull != null }
            val audioList = playViewUniteReply.vodInfo.dashAudioList
            val dolbyItem = playViewUniteReply.vodInfo.dolbyOrNull?.audioList?.firstOrNull()
            val lossLessItem =
                playViewUniteReply.vodInfo.lossLessItemOrNull?.audio.takeIf { it?.id != 0 }

            val dashVideos = streamList.map {
                DashVideo(
                    quality = it.streamInfo.quality,
                    baseUrl = it.dashVideo.baseUrl,
                    bandwidth = it.dashVideo.bandwidth,
                    codecId = it.dashVideo.codecid,
                    width = it.dashVideo.width,
                    height = it.dashVideo.height,
                    frameRate = it.dashVideo.frameRate,
                    backUrl = it.dashVideo.backupUrlList,
                    codecs = CodeType.fromCodecId(it.dashVideo.codecid).str
                )
            }
            val dashAudios = audioList.map {
                DashAudio(
                    baseUrl = it.baseUrl,
                    bandwidth = it.bandwidth,
                    codecId = it.id,
                    backUrl = it.backupUrlList
                )
            }
            val dolby = dolbyItem?.let {
                DashAudio(
                    baseUrl = it.baseUrl,
                    bandwidth = it.bandwidth,
                    codecId = it.id,
                    backUrl = it.backupUrlList
                )
            }
            val flac = lossLessItem?.let {
                DashAudio(
                    baseUrl = it.baseUrl,
                    bandwidth = it.bandwidth,
                    codecId = it.id,
                    backUrl = it.backupUrlList
                )
            }

            val codecs = playViewUniteReply.vodInfo.streamListList.associate {
                it.streamInfo.quality to listOf(CodeType.fromCodecId(it.dashVideo.codecid).str)
            }

            return PlayData(
                dashVideos = dashVideos,
                dashAudios = dashAudios,
                dolby = dolby,
                flac = flac,
                codec = codecs,
                needPay = false
            )
        }

        fun fromPgcPlayViewReply(pgcPlayViewReply: bilibili.pgc.gateway.player.v2.PlayViewReply): PlayData {
            val streamList =
                pgcPlayViewReply.videoInfo.streamListList.filter { it.dashVideoOrNull != null }
            val audioList = pgcPlayViewReply.videoInfo.dashAudioList
            val dolbyItem = pgcPlayViewReply.videoInfo.dolbyOrNull?.audio
            val codecs = pgcPlayViewReply.videoInfo.streamListList.associate {
                it.info.quality to listOf(CodeType.fromCodecId(it.dashVideo.codecid).str)
            }
            val needPay = pgcPlayViewReply.business.isPreview

            val dashVideos = streamList.map {
                DashVideo(
                    quality = it.info.quality,
                    baseUrl = it.dashVideo.baseUrl,
                    bandwidth = it.dashVideo.bandwidth,
                    codecId = it.dashVideo.codecid,
                    width = it.dashVideo.width,
                    height = it.dashVideo.height,
                    frameRate = it.dashVideo.frameRate,
                    backUrl = it.dashVideo.backupUrlList,
                    codecs = CodeType.fromCodecId(it.dashVideo.codecid).str
                )
            }
            val dashAudios = audioList.map {
                DashAudio(
                    baseUrl = it.baseUrl,
                    bandwidth = it.bandwidth,
                    codecId = it.id,
                    backUrl = it.backupUrlList
                )
            }
            val dolby = dolbyItem?.let {
                DashAudio(
                    baseUrl = it.baseUrl,
                    bandwidth = it.bandwidth,
                    codecId = it.codecid,
                    backUrl = it.backupUrlList
                )
            }

            return PlayData(
                dashVideos = dashVideos,
                dashAudios = dashAudios,
                dolby = dolby,
                flac = null,
                codec = codecs,
                needPay = needPay
            )
        }

        fun fromPlayUrlData(playUrlData: dev.aaa1115910.biliapi.http.entity.video.PlayUrlData): PlayData {
            val videos = playUrlData.dash?.video ?: emptyList()
            val audios = playUrlData.dash?.audio
            val dolbyItem = playUrlData.dash?.dolby?.audio?.firstOrNull()
            val flacItem = playUrlData.dash?.flac?.audio
            val codec = playUrlData.supportFormats.associate {
                it.quality to it.codecs!!
            }
            val needPay = playUrlData.isPreview == 1

            val dashVideos = videos.map {
                DashVideo(
                    quality = it.id,
                    baseUrl = it.baseUrl,
                    bandwidth = it.bandwidth,
                    codecId = it.id,
                    width = it.width,
                    height = it.height,
                    frameRate = it.frameRate,
                    backUrl = it.backupUrl,
                    codecs = it.codecs
                )
            }
            val dashAudios = audios?.map {
                DashAudio(
                    baseUrl = it.baseUrl,
                    bandwidth = it.bandwidth,
                    codecId = it.id,
                    backUrl = it.backupUrl
                )
            } ?: emptyList()
            val dolby = dolbyItem?.let {
                DashAudio(
                    baseUrl = it.baseUrl,
                    bandwidth = it.bandwidth,
                    codecId = it.id,
                    backUrl = it.backupUrl
                )
            }
            val flac = flacItem?.let {
                DashAudio(
                    baseUrl = it.baseUrl,
                    bandwidth = it.bandwidth,
                    codecId = it.id,
                    backUrl = it.backupUrl
                )
            }

            return PlayData(
                dashVideos = dashVideos,
                dashAudios = dashAudios,
                dolby = dolby,
                flac = flac,
                codec = codec,
                needPay = needPay
            )
        }

        fun fromPlayUrlData(playUrlData: dev.aaa1115910.biliapi.http.entity.proxy.ProxyWebPlayUrlData): PlayData {
            val videos = playUrlData.dash?.video ?: emptyList()
            val audios = playUrlData.dash?.audio
            val dolbyItem = playUrlData.dash?.dolby?.audio?.firstOrNull()
            val flacItem = playUrlData.dash?.flac?.audio
            val codec = playUrlData.supportFormats.associate {
                it.quality to it.codecs!!
            }
            val needPay = playUrlData.isPreview == 1

            val dashVideos = videos.map {
                DashVideo(
                    quality = it.id,
                    baseUrl = it.baseUrl,
                    bandwidth = it.bandwidth,
                    codecId = it.id,
                    width = it.width,
                    height = it.height,
                    frameRate = it.frameRate,
                    backUrl = it.backupUrl,
                    codecs = it.codecs
                )
            }
            val dashAudios = audios?.map {
                DashAudio(
                    baseUrl = it.baseUrl,
                    bandwidth = it.bandwidth,
                    codecId = it.id,
                    backUrl = it.backupUrl
                )
            } ?: emptyList()
            val dolby = dolbyItem?.let {
                DashAudio(
                    baseUrl = it.baseUrl,
                    bandwidth = it.bandwidth,
                    codecId = it.id,
                    backUrl = it.backupUrl
                )
            }
            val flac = flacItem?.let {
                DashAudio(
                    baseUrl = it.baseUrl,
                    bandwidth = it.bandwidth,
                    codecId = it.id,
                    backUrl = it.backupUrl
                )
            }

            return PlayData(
                dashVideos = dashVideos,
                dashAudios = dashAudios,
                dolby = dolby,
                flac = flac,
                codec = codec,
                needPay = needPay
            )
        }

        fun fromPlayUrlData(playUrlData: dev.aaa1115910.biliapi.http.entity.proxy.ProxyAppPlayUrlData): PlayData {
            val videos = playUrlData.dash?.video ?: emptyList()
            val audios = playUrlData.dash?.audio
            val dolbyItem = playUrlData.dash?.dolby?.audio?.firstOrNull()
            val flacItem = playUrlData.dash?.flac?.audio
            val codec = playUrlData.supportFormats.associate {
                it.quality to it.codecs!!
            }
            val needPay = playUrlData.isPreview == 1

            val dashVideos = videos.map {
                DashVideo(
                    quality = it.id,
                    baseUrl = it.baseUrl,
                    bandwidth = it.bandwidth,
                    codecId = it.id,
                    width = it.width,
                    height = it.height,
                    frameRate = it.frameRate,
                    backUrl = it.backupUrl,
                    codecs = it.codecs
                )
            }
            val dashAudios = audios?.map {
                DashAudio(
                    baseUrl = it.baseUrl,
                    bandwidth = it.bandwidth,
                    codecId = it.id,
                    backUrl = it.backupUrl
                )
            } ?: emptyList()
            val dolby = dolbyItem?.let {
                DashAudio(
                    baseUrl = it.baseUrl,
                    bandwidth = it.bandwidth,
                    codecId = it.id,
                    backUrl = it.backupUrl
                )
            }
            val flac = flacItem?.let {
                DashAudio(
                    baseUrl = it.baseUrl,
                    bandwidth = it.bandwidth,
                    codecId = it.id,
                    backUrl = it.backupUrl
                )
            }

            return PlayData(
                dashVideos = dashVideos,
                dashAudios = dashAudios,
                dolby = dolby,
                flac = flac,
                codec = codec,
                needPay = needPay
            )
        }
    }

    operator fun plus(other: PlayData): PlayData {
        return PlayData(
            dashVideos = (dashVideos + other.dashVideos)
                .distinctBy { "${it.codecId}_${it.quality}" }
                .sortedByDescending { it.quality },
            dashAudios = (dashAudios + other.dashAudios)
                .distinctBy { it.codecId }
                .sortedByDescending { it.codecId },
            dolby = dolby ?: other.dolby,
            flac = flac ?: other.flac,
            codec = codec.map {
                it.key to (it.value + other.codec[it.key].orEmpty())
                    .distinct()
                    .filter { it != "none" }
            }.toMap(),
            needPay = needPay || other.needPay
        )
    }
}

/**
 * @param quality 视频分辨率
 * @param baseUrl 主线流
 * @param bandwidth 码率
 * @param codecId 编码ID
 * @param width 视频宽度
 * @param height 视频高度
 * @param frameRate 帧率
 * @param backUrl 备用流
 * @param codecs 编码格式 仅 Web 接口有该值
 */
data class DashVideo(
    val quality: Int,
    val baseUrl: String,
    val bandwidth: Int,
    val codecId: Int,
    val width: Int,
    val height: Int,
    val frameRate: String,
    val backUrl: List<String>,
    val codecs: String? = null
)

/**
 * @param baseUrl 主线流
 * @param bandwidth 码率
 * @param codecId 编码ID
 * @param backUrl 备用流
 */
data class DashAudio(
    val baseUrl: String,
    val bandwidth: Int,
    val codecId: Int,
    val backUrl: List<String>
)
