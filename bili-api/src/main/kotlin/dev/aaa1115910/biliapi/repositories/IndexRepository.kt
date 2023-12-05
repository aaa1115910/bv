package dev.aaa1115910.biliapi.repositories

import dev.aaa1115910.biliapi.entity.season.IndexResultData
import dev.aaa1115910.biliapi.entity.season.IndexResultPage
import dev.aaa1115910.biliapi.http.BiliHttpApi
import dev.aaa1115910.biliapi.http.entity.index.IndexOrder

class IndexRepository {
    suspend fun getAnimeIndex(
        sort: IndexOrder = IndexOrder.PlayCount,
        seasonVersion: Int = -1,
        spokenLanguageType: Int = -1,
        area: Int = -1,
        isFinish: Int = -1,
        copyright: Int = -1,
        seasonStatus: Int = -1,
        seasonMonth: Int = -1,
        year: String = "-1",
        styleId: Int = -1,
        desc: Boolean = true,
        page: IndexResultPage = IndexResultPage()
    ): IndexResultData {
        val biliResponse = BiliHttpApi.seasonIndexAnimeResult(
            order = sort.id,
            seasonVersion = seasonVersion,
            spokenLanguageType = spokenLanguageType,
            area = area,
            isFinish = isFinish,
            copyright = copyright,
            seasonStatus = seasonStatus,
            seasonMonth = seasonMonth,
            year = year,
            styleId = styleId,
            sort = if (desc) 0 else 1,
            page = page.nextPage,
            pagesize = 20
        )
        return IndexResultData.fromIndexResultData(biliResponse.getResponseData())
    }

    suspend fun getGuochuangIndex(
        sort: IndexOrder = IndexOrder.PlayCount,
        seasonVersion: Int = -1,
        isFinish: Int = -1,
        copyright: Int = -1,
        seasonStatus: Int = -1,
        year: String = "-1",
        styleId: Int = -1,
        desc: Boolean = true,
        page: IndexResultPage = IndexResultPage()
    ): IndexResultData {
        val biliResponse = BiliHttpApi.seasonIndexGuochuangResult(
            order = sort.id,
            seasonVersion = seasonVersion,
            isFinish = isFinish,
            copyright = copyright,
            seasonStatus = seasonStatus,
            year = year,
            styleId = styleId,
            sort = if (desc) 0 else 1,
            page = page.nextPage,
            pagesize = 20
        )
        return IndexResultData.fromIndexResultData(biliResponse.getResponseData())
    }

    suspend fun getVarietyIndex(
        sort: IndexOrder = IndexOrder.PlayCount,
        seasonStatus: Int = -1,
        styleId: Int = -1,
        desc: Boolean = true,
        page: IndexResultPage = IndexResultPage()
    ): IndexResultData {
        val biliResponse = BiliHttpApi.seasonIndexVarietyResult(
            order = sort.id,
            seasonStatus = seasonStatus,
            styleId = styleId,
            sort = if (desc) 0 else 1,
            page = page.nextPage,
            pagesize = 20
        )
        return IndexResultData.fromIndexResultData(biliResponse.getResponseData())
    }

    suspend fun getMovieIndex(
        sort: IndexOrder = IndexOrder.PlayCount,
        area: Int = -1,
        releaseDate: String = "-1",
        seasonStatus: Int = -1,
        styleId: Int = -1,
        desc: Boolean = true,
        page: IndexResultPage = IndexResultPage()
    ): IndexResultData {
        val biliResponse = BiliHttpApi.seasonIndexMovieResult(
            order = sort.id,
            area = area,
            releaseDate = releaseDate,
            seasonStatus = seasonStatus,
            styleId = styleId,
            sort = if (desc) 0 else 1,
            page = page.nextPage,
            pagesize = 20
        )
        return IndexResultData.fromIndexResultData(biliResponse.getResponseData())
    }

    suspend fun getTvIndex(
        sort: IndexOrder = IndexOrder.PlayCount,
        area: Int = -1,
        releaseDate: String = "-1",
        seasonStatus: Int = -1,
        styleId: Int = -1,
        desc: Boolean = true,
        page: IndexResultPage = IndexResultPage()
    ): IndexResultData {
        val biliResponse = BiliHttpApi.seasonIndexTvResult(
            order = sort.id,
            area = area,
            releaseDate = releaseDate,
            seasonStatus = seasonStatus,
            styleId = styleId,
            sort = if (desc) 0 else 1,
            page = page.nextPage,
            pagesize = 20
        )
        return IndexResultData.fromIndexResultData(biliResponse.getResponseData())
    }

    suspend fun getDocumentaryIndex(
        sort: IndexOrder = IndexOrder.PlayCount,
        area: Int = -1,
        releaseDate: String = "-1",
        seasonStatus: Int = -1,
        styleId: Int = -1,
        producerId: Int = -1,
        desc: Boolean = true,
        page: IndexResultPage = IndexResultPage()
    ): IndexResultData {
        val biliResponse = BiliHttpApi.seasonIndexDocumentaryResult(
            order = sort.id,
            area = area,
            releaseDate = releaseDate,
            seasonStatus = seasonStatus,
            styleId = styleId,
            producerId = producerId,
            sort = if (desc) 0 else 1,
            page = page.nextPage,
            pagesize = 20
        )
        return IndexResultData.fromIndexResultData(biliResponse.getResponseData())
    }
}