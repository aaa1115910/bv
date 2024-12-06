package dev.aaa1115910.biliapi.repositories

import dev.aaa1115910.biliapi.entity.CarouselData
import dev.aaa1115910.biliapi.entity.pgc.PgcFeedData
import dev.aaa1115910.biliapi.entity.pgc.PgcType
import dev.aaa1115910.biliapi.entity.pgc.index.Area
import dev.aaa1115910.biliapi.entity.pgc.index.Copyright
import dev.aaa1115910.biliapi.entity.pgc.index.IndexOrder
import dev.aaa1115910.biliapi.entity.pgc.index.IndexOrderType
import dev.aaa1115910.biliapi.entity.pgc.index.IsFinish
import dev.aaa1115910.biliapi.entity.pgc.index.PgcIndexData
import dev.aaa1115910.biliapi.entity.pgc.index.Producer
import dev.aaa1115910.biliapi.entity.pgc.index.ReleaseDate
import dev.aaa1115910.biliapi.entity.pgc.index.SeasonMonth
import dev.aaa1115910.biliapi.entity.pgc.index.SeasonStatus
import dev.aaa1115910.biliapi.entity.pgc.index.SeasonVersion
import dev.aaa1115910.biliapi.entity.pgc.index.SpokenLanguage
import dev.aaa1115910.biliapi.entity.pgc.index.Style
import dev.aaa1115910.biliapi.entity.pgc.index.Year
import dev.aaa1115910.biliapi.http.BiliHttpApi

class PgcRepository {
    suspend fun getCarousel(pgcType: PgcType): CarouselData {
        val initialStateData = BiliHttpApi.getPgcWebInitialStateData(pgcType)
        val carouselData = CarouselData.fromPgcWebInitialStateData(initialStateData)
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

    suspend fun getPgcIndex(
        pgcType: PgcType,
        indexOrder: IndexOrder,
        indexOrderType: IndexOrderType,
        seasonVersion: SeasonVersion,
        spokenLanguage: SpokenLanguage,
        area: Area,
        isFinish: IsFinish,
        copyright: Copyright,
        seasonStatus: SeasonStatus,
        seasonMonth: SeasonMonth,
        producer: Producer,
        year: Year,
        releaseDate: ReleaseDate,
        style: Style,
        page: PgcIndexData.PgcIndexPage,
    ): PgcIndexData {
        val data = PgcIndexData.fromIndexResultData(
            when (pgcType) {
                PgcType.Anime -> BiliHttpApi.seasonIndexAnimeResult(
                    order = indexOrder.id,
                    sort = indexOrderType.id,
                    seasonVersion = seasonVersion.id,
                    spokenLanguageType = spokenLanguage.id,
                    area = area.id,
                    isFinish = isFinish.id,
                    copyright = copyright.id,
                    seasonStatus = seasonStatus.id,
                    seasonMonth = seasonMonth.id,
                    year = year.str,
                    styleId = style.id,
                    page = page.nextPage,
                    pagesize = page.pageSize
                )

                PgcType.GuoChuang -> BiliHttpApi.seasonIndexGuochuangResult(
                    order = indexOrder.id,
                    sort = indexOrderType.id,
                    seasonVersion = seasonVersion.id,
                    isFinish = isFinish.id,
                    copyright = copyright.id,
                    seasonStatus = seasonStatus.id,
                    year = year.str,
                    styleId = style.id,
                    page = page.nextPage,
                    pagesize = page.pageSize
                )

                PgcType.Movie -> BiliHttpApi.seasonIndexMovieResult(
                    order = indexOrder.id,
                    sort = indexOrderType.id,
                    area = area.id,
                    seasonStatus = seasonStatus.id,
                    releaseDate = releaseDate.str,
                    styleId = style.id,
                    page = page.nextPage,
                    pagesize = page.pageSize
                )

                PgcType.Documentary -> BiliHttpApi.seasonIndexDocumentaryResult(
                    order = indexOrder.id,
                    sort = indexOrderType.id,
                    area = area.id,
                    seasonStatus = seasonStatus.id,
                    producerId = producer.id,
                    releaseDate = releaseDate.str,
                    styleId = style.id,
                    page = page.nextPage,
                    pagesize = page.pageSize
                )

                PgcType.Tv -> BiliHttpApi.seasonIndexTvResult(
                    order = indexOrder.id,
                    sort = indexOrderType.id,
                    area = area.id,
                    seasonStatus = seasonStatus.id,
                    releaseDate = releaseDate.str,
                    styleId = style.id,
                    page = page.nextPage,
                    pagesize = page.pageSize
                )

                PgcType.Variety -> BiliHttpApi.seasonIndexVarietyResult(
                    order = indexOrder.id,
                    sort = indexOrderType.id,
                    seasonStatus = seasonStatus.id,
                    styleId = style.id,
                    page = page.nextPage,
                    pagesize = page.pageSize
                )
            }.getResponseData()
        )
        return data
    }
}
