package dev.aaa1115910.biliapi.repositories

import dev.aaa1115910.biliapi.entity.pgc.PgcCarouselData
import dev.aaa1115910.biliapi.entity.pgc.PgcFeedData
import dev.aaa1115910.biliapi.http.BiliHttpApi

class PgcRepository {
    suspend fun getCarousel(pgcType: PgcType): PgcCarouselData {
        val initialStateData = BiliHttpApi.getPgcWebInitialStateData(pgcType)
        val carouselData = PgcCarouselData.fromPgcWebInitialStateData(initialStateData)
        return carouselData
    }

    suspend fun getFeed(pgcType: PgcType, cursor: Int): PgcFeedData {
        val data = when (pgcType) {
            PgcType.Anime, PgcType.GuoChuang -> PgcFeedData.fromPgcFeedData(
                BiliHttpApi.getPgcFeedV3(
                    name = pgcType.name.lowercase(),
                    cursor = cursor
                ).getResponseData()
            )

            PgcType.Movie, PgcType.Tv, PgcType.Documentary, PgcType.Variety -> PgcFeedData.fromPgcFeedData(
                BiliHttpApi.getPgcFeed(
                    name = pgcType.name.lowercase(),
                    cursor = cursor
                ).getResponseData()
            )
        }
        return data
    }
}

enum class PgcType {
    Anime,
    GuoChuang,
    Movie,
    Documentary,
    Tv,
    Variety
}
