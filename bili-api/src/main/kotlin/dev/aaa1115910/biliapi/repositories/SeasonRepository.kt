package dev.aaa1115910.biliapi.repositories

import dev.aaa1115910.biliapi.entity.ApiType
import dev.aaa1115910.biliapi.entity.season.FollowingSeason
import dev.aaa1115910.biliapi.entity.season.FollowingSeasonData
import dev.aaa1115910.biliapi.entity.season.FollowingSeasonStatus
import dev.aaa1115910.biliapi.entity.season.FollowingSeasonType
import dev.aaa1115910.biliapi.http.BiliHttpApi
import dev.aaa1115910.biliapi.http.util.BiliAppConf

class SeasonRepository(
    private val authRepository: AuthRepository
) {
    /**
     * 获取追番/追剧列表
     *
     * @param type 追番/追剧类型
     * @param status 追剧状态 当 [preferApiType] == [ApiType.App] 时，不可使用 [FollowingSeasonStatus.All]
     * @param pageNumber 页码
     * @param pageSize 每页数量
     * @param preferApiType 优先使用的 API 类型
     */
    suspend fun getFollowingSeasons(
        type: FollowingSeasonType = FollowingSeasonType.Bangumi,
        status: FollowingSeasonStatus = FollowingSeasonStatus.All,
        pageNumber: Int = 1,
        pageSize: Int = 30,
        preferApiType: ApiType = ApiType.Web
    ): FollowingSeasonData {
        return when (preferApiType) {
            ApiType.Web -> BiliHttpApi.getFollowingSeasons(
                type = type.id,
                status = status.id,
                pageNumber = pageNumber,
                pageSize = pageSize,
                mid = authRepository.mid!!,
                sessData = authRepository.sessionData
            ).getResponseData()
                .let { responseData ->
                    FollowingSeasonData(
                        list = responseData.list.map { FollowingSeason.fromFollowingSeason(it) },
                        total = responseData.total
                    )
                }

            ApiType.App -> BiliHttpApi.getFollowingSeasons(
                type = type.paramName,
                status = status.id,
                pageNumber = pageNumber,
                pageSize = pageSize,
                build = BiliAppConf.APP_BUILD_CODE,
                accessKey = authRepository.accessToken!!
            ).getResponseData()
                .let { responseData ->
                    FollowingSeasonData(
                        list = responseData.followList.map { FollowingSeason.fromFollowingSeason(it) },
                        total = responseData.total
                    )
                }
        }
    }
}