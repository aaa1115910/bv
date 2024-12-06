package dev.aaa1115910.biliapi.repositories

import dev.aaa1115910.biliapi.entity.ugc.UgcType
import dev.aaa1115910.biliapi.entity.ugc.region.UgcRegionData
import dev.aaa1115910.biliapi.entity.ugc.region.UgcRegionListData
import dev.aaa1115910.biliapi.http.BiliHttpApi

class UgcRepository(
    private val authRepository: AuthRepository
) {
    suspend fun getRegionData(ugcType: UgcType): UgcRegionData {
        val responseData = BiliHttpApi.getRegionDynamic(
            rid = ugcType.rid,
            accessKey = authRepository.accessToken ?: "",
        ).getResponseData()
        val data = UgcRegionData.fromRegionDynamic(responseData)
        return data
    }

    suspend fun getRegionMoreData(ugcType: UgcType): UgcRegionListData {
        val responseData = BiliHttpApi.getRegionDynamicList(
            rid = ugcType.rid,
            accessKey = authRepository.accessToken ?: "",
        ).getResponseData()
        val data = UgcRegionListData.fromRegionDynamicList(responseData)
        return data
    }
}
