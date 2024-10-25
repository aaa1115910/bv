package dev.aaa1115910.biliapi.entity.pgc

import dev.aaa1115910.biliapi.http.entity.pgc.PgcWebInitialStateData
import io.ktor.http.Url

data class PgcCarouselData(
    val items: List<CarouselItem>
) {
    companion object {
        fun fromPgcWebInitialStateData(data: PgcWebInitialStateData): PgcCarouselData {
            val result = mutableListOf<CarouselItem>()
            var isMovie = false
            // 电影板块里的轮播图数据里没有直接包含 episodeId 和 seasonId
            if (data.modules.banner.moduleId == 1668) isMovie = true
            data.modules.banner.items.filter {
                it.episodeId != null || (isMovie && it.link.contains("bangumi/play/ep"))
            }.forEach {
                var cover = it.bigCover ?: it.cover
                if (cover.startsWith("//")) cover = "https:$cover"
                result.add(
                    CarouselItem(
                        cover = cover,
                        title = it.title,
                        seasonId = it.seasonId ?: -1,
                        episodeId = it.episodeId
                            ?: Url(it.link).pathSegments.last().substring(2).toInt()
                    )
                )
            }
            return PgcCarouselData(result)
        }
    }

    data class CarouselItem(
        val cover: String,
        val title: String,
        val seasonId: Int,
        val episodeId: Int
    )
}
