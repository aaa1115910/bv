package dev.aaa1115910.biliapi.entity.video.season

import dev.aaa1115910.biliapi.entity.video.VideoDetail.PlayerIcon
import dev.aaa1115910.biliapi.http.entity.season.AppSeasonData
import dev.aaa1115910.biliapi.http.entity.season.WebSeasonData

/**
 * 剧集详细信息
 *
 * @param title 剧集标题
 * @param originTitle 剧集原始标题，仅出现在具有外文名时，且仅 App 端有此字段
 * @param styles 剧集风格
 * @param cover 封面
 * @param description 剧集简介
 * @param subType 剧集类型，需要用到上报播放记录
 * @param seasonId 剧集id
 * @param userStatus 用户信息，例如购买记录，追剧情况，播放记录等
 * @param publish 发布状态
 * @param newEpDesc 最新一集的描述,
 * @param seasons 同系列剧集列表,
 * @param episodes 剧集视频列表
 * @param sections 相关视频列表
 */
data class SeasonDetail(
    val title: String,
    val originTitle: String? = null,
    val styles: List<String>,
    val cover: String,
    val description: String,
    val subType: Int,
    val seasonId: Int,
    val userStatus: UserStatus,
    val publish: Publish,
    val newEpDesc: String,
    val seasons: List<PgcSeason> = emptyList(),
    val episodes: List<Episode> = emptyList(),
    val sections: List<Section> = emptyList(),
    var playerIcon: PlayerIcon? = null
) {
    companion object {
        fun fromSeasonData(seasonData: WebSeasonData): SeasonDetail {
            return SeasonDetail(
                title = seasonData.title,
                originTitle = null,
                styles = seasonData.styles,
                cover = seasonData.cover,
                description = seasonData.evaluate,
                subType = seasonData.type,
                seasonId = seasonData.seasonId,
                userStatus = UserStatus.fromUserStatus(seasonData.userStatus),
                publish = Publish.fromPublish(seasonData.publish),
                newEpDesc = seasonData.newEp.desc,
                seasons = seasonData.seasons.map { PgcSeason.fromSeason(it) },
                episodes = seasonData.episodes.map { Episode.fromEpisode(it) }
            )
        }

        fun fromSeasonData(seasonData: AppSeasonData): SeasonDetail {
            return SeasonDetail(
                title = seasonData.title,
                originTitle = seasonData.originName,
                styles = seasonData.styles.map { it.name },
                cover = seasonData.cover,
                description = seasonData.evaluate,
                subType = seasonData.type,
                seasonId = seasonData.seasonId,
                userStatus = UserStatus.fromUserStatus(seasonData.userStatus),
                publish = Publish.fromPublish(seasonData.publish),
                newEpDesc = seasonData.newEp.desc,
                seasons = seasonData.modules
                    .firstOrNull { it.style == "season" }
                    ?.data?.seasons
                    ?.map { PgcSeason.fromSeason(it) }
                    ?: emptyList(),
                episodes = seasonData.modules
                    .firstOrNull { it.style == "positive" }
                    ?.data?.episodes
                    ?.map { Episode.fromEpisode(it) }
                    ?: emptyList(),
                sections = seasonData.modules
                    .filter { it.style == "section" }
                    .map { Section.fromModule(it) },
                playerIcon = PlayerIcon.fromPlayerIcon(seasonData.playerIcon)
            )
        }
    }

    /**
     * 用户信息
     *
     * @param follow 已追剧
     * @param pay 已购买
     * @param progress 观看记录
     */
    data class UserStatus(
        val follow: Boolean,
        val pay: Boolean,
        val progress: Progress? = null
    ) {
        companion object {
            fun fromUserStatus(userStatus: WebSeasonData.UserStatus): UserStatus {
                return UserStatus(
                    follow = userStatus.follow == 1,
                    pay = userStatus.pay == 1,
                    progress = userStatus.progress?.let {
                        Progress(
                            lastEpId = it.lastEpId,
                            lastEpIndex = it.lastEpIndex,
                            lastTime = it.lastTime
                        )
                    }
                )
            }

            fun fromUserStatus(userStatus: AppSeasonData.UserStatus): UserStatus {
                return UserStatus(
                    follow = userStatus.follow == 1,
                    pay = userStatus.pay == 1,
                    progress = userStatus.progress?.let {
                        Progress(
                            lastEpId = it.lastEpId,
                            lastEpIndex = it.lastEpIndex,
                            lastTime = it.lastTime
                        )
                    }
                )
            }
        }

        /**
         * 观看记录
         *
         * @param lastEpId 最后观看的epid
         * @param lastEpIndex 最后观看的ep标题
         * @param lastTime 最后观看的时间（秒）
         */
        data class Progress(
            val lastEpId: Int,
            val lastEpIndex: String,
            val lastTime: Int
        )
    }

    data class Publish(
        val isPublished: Boolean,
        val publishDate: String
    ) {
        companion object {
            fun fromPublish(publish: dev.aaa1115910.biliapi.http.entity.season.Publish): Publish {
                return Publish(
                    isPublished = publish.isStarted,
                    publishDate = publish.pubTimeShow
                )
            }
        }
    }
}
