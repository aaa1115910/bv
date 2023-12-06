package dev.aaa1115910.biliapi.repositories

import dev.aaa1115910.biliapi.entity.season.IndexResultPage
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class IndexRepositoryTest {

    @Test
    fun `get indexes`() {
        runBlocking {
            println("----- get anime index -----")
            `get anime index`()
            println("----- get guochuang index -----")
            `get guochuang index`()
            println("----- get variety index -----")
            `get variety index`()
            println("----- get movie index -----")
            `get movie index`()
            println("----- get tv index -----")
            `get tv index`()
            println("----- get documentary index -----")
            `get documentary index`()
        }
    }

    @Test
    fun `get anime index`() {
        runBlocking {
            var page = IndexResultPage()
            for (i in 1..5) {
                val result = IndexRepository().getAnimeIndex(page = page)
                println(result.list.map { it.title })
                page = result.nextPage
            }
        }
    }

    @Test
    fun `get guochuang index`() {
        runBlocking {
            var page = IndexResultPage()
            for (i in 1..5) {
                val result = IndexRepository().getGuochuangIndex(page = page)
                println(result.list.map { it.title })
                page = result.nextPage
            }
        }
    }

    @Test
    fun `get variety index`() {
        runBlocking {
            var page = IndexResultPage()
            for (i in 1..5) {
                val result = IndexRepository().getVarietyIndex(page = page)
                println(result.list.map { it.title })
                page = result.nextPage
            }
        }
    }

    @Test
    fun `get movie index`() {
        runBlocking {
            var page = IndexResultPage()
            for (i in 1..5) {
                val result = IndexRepository().getMovieIndex(page = page)
                println(result.list.map { it.title })
                page = result.nextPage
            }
        }
    }

    @Test
    fun `get tv index`() {
        runBlocking {
            var page = IndexResultPage()
            for (i in 1..5) {
                val result = IndexRepository().getTvIndex(page = page)
                println(result.list.map { it.title })
                page = result.nextPage
            }
        }
    }

    @Test
    fun `get documentary index`() {
        runBlocking {
            var page = IndexResultPage()
            for (i in 1..5) {
                val result = IndexRepository().getDocumentaryIndex(page = page)
                println(result.list.map { it.title })
                page = result.nextPage
            }
        }
    }
}