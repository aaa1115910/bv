package dev.aaa1115910.biliapi.entity.season

import java.util.Date

enum class TimelineFilter(
    val webFilterId: Int,
    val appFilterId: Int
) {
    All(webFilterId = -1, appFilterId = 0),
    Anime(webFilterId = 1, appFilterId = 1),
    Following(webFilterId = -1, appFilterId = 2),
    GuoChuang(webFilterId = 4, appFilterId = 3);

    companion object {
        val webFilters = listOf(Anime, GuoChuang)
        val appFilters = listOf(All, Anime, Following, GuoChuang)
    }
}

data class Timeline(
    val dateString: String,
    val date: Date,
    val dayOfWeek: Int,
    val isToday: Boolean,
    val episodes: List<TimelineEp>
) {
    companion object {
        fun fromTimeline(timeline: dev.aaa1115910.biliapi.http.entity.video.Timeline) = Timeline(
            dateString = timeline.date,
            date = Date(timeline.dateTs * 1000L),
            dayOfWeek = timeline.dayOfWeek,
            isToday = timeline.isToday,
            episodes = timeline.episodes.map { TimelineEp.fromTimelineEpisode(it) }
        )
    }
}

data class TimelineEp(
    val cover: String,
    val title: String,
    val seasonId: Int,
    val publishIndex: String,
    val publishTime: String,
    val publishDate: Date
) {
    companion object {
        fun fromTimelineEpisode(episode: dev.aaa1115910.biliapi.http.entity.video.Timeline.Episode) =
            TimelineEp(
                cover = episode.cover,
                title = episode.title,
                seasonId = episode.seasonId,
                publishIndex = episode.pubIndex,
                publishTime = episode.pubTime,
                publishDate = Date(episode.pubTs * 1000L)
            )
    }
}