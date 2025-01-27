package dev.aaa1115910.biliapi.repositories

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
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class PgcRepositoryTest {
    private val pgcRepository: PgcRepository = PgcRepository()

    @Test
    fun `get pgc carousel data`() {
        runBlocking {
            PgcType.entries.forEach { pgcType ->
                println("pgcType: $pgcType")
                val data = pgcRepository.getCarousel(pgcType)
                println(data)
            }
        }
    }

    @Test
    fun `get pgc feed data`() {
        runBlocking {
            PgcType.entries.forEach { pgcType ->
                println("pgcType: $pgcType")
                val data = pgcRepository.getFeed(
                    pgcType = pgcType,
                    cursor = 0
                )
                println(data)
            }
        }
    }

    @Test
    fun `get pgc index`() {
        runBlocking {
            PgcType.entries.forEach { pgcType ->
                println("pgcType: $pgcType")
                val data = pgcRepository.getPgcIndex(
                    pgcType = pgcType,
                    indexOrder = IndexOrder.PlayCount,
                    indexOrderType = IndexOrderType.Desc,
                    seasonVersion = SeasonVersion.All,
                    spokenLanguage = SpokenLanguage.All,
                    area = Area.All,
                    isFinish = IsFinish.All,
                    copyright = Copyright.All,
                    seasonStatus = SeasonStatus.All,
                    seasonMonth = SeasonMonth.All,
                    producer = Producer.All,
                    year = Year.All,
                    releaseDate = ReleaseDate.All,
                    style = Style.All,
                    page = PgcIndexData.PgcIndexPage()
                )
                println(data)
            }
        }
    }
}