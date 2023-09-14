package dev.aaa1115910.biliapi.repositories

import bilibili.app.interfaces.v1.suggestionResult3Req
import bilibili.pagination.pagination
import bilibili.polymer.app.search.v1.SearchByTypeRequest
import bilibili.polymer.app.search.v1.searchByTypeRequest
import dev.aaa1115910.biliapi.entity.ApiType
import dev.aaa1115910.biliapi.entity.search.Hotword
import dev.aaa1115910.biliapi.grpc.utils.handleGrpcException
import dev.aaa1115910.biliapi.http.BiliHttpApi
import dev.aaa1115910.biliapi.http.BiliHttpProxyApi

class SearchRepository(
    private val authRepository: AuthRepository,
    private val channelRepository: ChannelRepository
) {
    private val searchSuggestStub
        get() = runCatching {
            bilibili.app.interfaces.v1.SearchGrpcKt.SearchCoroutineStub(channelRepository.defaultChannel!!)
        }.getOrNull()

    private val searchResultStub
        get() = runCatching {
            bilibili.polymer.app.search.v1.SearchGrpcKt.SearchCoroutineStub(channelRepository.defaultChannel!!)
        }.getOrNull()

    private val proxySearchResultStub
        get() = runCatching {
            bilibili.polymer.app.search.v1.SearchGrpcKt.SearchCoroutineStub(channelRepository.proxyChannel!!)
        }.getOrNull()

    /*private val searchStub
        get() = runCatching {
            SearchGrpcKt.SearchCoroutineStub(channelRepository.defaultChannel!!)
        }.getOrNull()

    suspend fun search(
        keyword: String,
        page: Int = 1,
        pageSize: Int = 20,
        preferApiType: ApiType = ApiType.Web
    ): SearchData {
        return when (preferApiType) {
            ApiType.Web -> {
                val data = BiliHttpApi.search(
                    keyword = keyword,
                    page = page,
                    pageSize = pageSize,
                    sessData = authRepository.sessionData!!,
                ).getResponseData()
                SearchData.fromSearchResponse(data)
            }

            ApiType.App -> {
                val reply = searchStub?.searchV2(searchV2Req {
                    this.keyword = keyword
                    this.page = page
                    this.pageSize = pageSize
                })
                SearchData.fromSearchResponse(reply!!)
            }
        }
    }*/

    suspend fun getSearchHotwords(
        limit: Int = 30,
        preferApiType: ApiType = ApiType.Web
    ): List<Hotword> {
        return when (preferApiType) {
            ApiType.Web -> BiliHttpApi.getWebSearchSquare(limit = limit)
                .getResponseData().trending.list
                .map { Hotword.fromHttpWebHotword(it) }

            /*ApiType.App -> BiliHttpApi.getAppSearchSquare(limit = limit)
                .getResponseData()
                .firstOrNull { it.type == "trending" }
                ?.data?.list
                ?.map { Hotword.fromHttpAppSquareDataItem(it) }
                ?: emptyList()*/

            ApiType.App -> BiliHttpApi.getSearchTrendRank(limit = 50)
                .getResponseData().list
                .map { Hotword.fromHttpAppSearchTrendingHotword(it) }
        }
    }

    suspend fun getSearchSuggest(
        keyword: String,
        preferApiType: ApiType = ApiType.App
    ): List<String> {
        return when (preferApiType) {
            ApiType.Web -> BiliHttpApi.getKeywordSuggest(
                term = keyword
            ).suggests.map { it.value }

            //TODO 返回的关键词提示中可能包含通过avid/bvid/专栏id等的直达跳转结果项，需要过滤掉或进行单独处理
            ApiType.App -> searchSuggestStub?.suggest3(suggestionResult3Req {
                this.keyword = keyword
            })?.listList?.map { it.keyword } ?: emptyList()
        }
    }

    /**
     * 按分类进行搜索
     *
     * app 端的接口无法对视频投稿结果进行筛选搜索
     */
    suspend fun searchType(
        keyword: String,
        type: SearchType,
        tid: Int?,
        order: SearchFilterOrderType,
        duration: SearchFilterDuration,
        page: SearchTypePage,
        preferApiType: ApiType = ApiType.App,
        enableProxy: Boolean = false
    ): SearchTypeResult {
        return when (preferApiType) {
            ApiType.Web -> {
                val response = if (enableProxy) {
                    BiliHttpProxyApi.searchType(
                        keyword = keyword,
                        type = type.httpTypeParam,
                        page = page.nextPageForWeb,
                        tid = tid,
                        order = order.httpOrderParam,
                        duration = duration.httpDurationParam,
                        buvid3 = authRepository.buvid3!!,
                    )
                } else {
                    BiliHttpApi.searchType(
                        keyword = keyword,
                        type = type.httpTypeParam,
                        page = page.nextPageForWeb,
                        tid = tid,
                        order = order.httpOrderParam,
                        duration = duration.httpDurationParam,
                        buvid3 = authRepository.buvid3!!,
                    )
                }.getResponseData()
                SearchTypeResult.fromSearchTypeResult(response)
            }

            ApiType.App -> {
                val searchTypeReply = runCatching {
                    val searchTypeRequest = searchByTypeRequest {
                        this.keyword = keyword
                        this.type = type.grpcTypeParam
                        categorySort = order.grpcOrderParam
                        userType = SearchByTypeRequest.UserType.ALL
                        userSort = SearchByTypeRequest.UserSort.USER_SORT_DEFAULT
                        pagination = pagination {
                            next = page.nextPageForApp
                        }
                    }
                    if (enableProxy) {
                        proxySearchResultStub?.searchByType(searchTypeRequest)
                            ?: throw IllegalStateException("Proxy search result stub is not initialized")
                    } else {
                        searchResultStub?.searchByType(searchTypeRequest)
                            ?: throw IllegalStateException("Search result stub is not initialized")
                    }
                }.onFailure { handleGrpcException(it) }.getOrThrow()
                SearchTypeResult.fromSearchTypeResult(searchTypeReply)
            }
        }
    }
}

data class SearchTypePage(
    val nextPageForWeb: Int = 1,
    val nextPageForApp: String = ""
)

enum class SearchType(
    val httpTypeParam: String,
    val grpcTypeParam: Int
) {
    Video(httpTypeParam = "video", grpcTypeParam = 10),
    MediaBangumi(httpTypeParam = "media_bangumi", grpcTypeParam = 7),
    MediaFt(httpTypeParam = "media_ft", grpcTypeParam = 8),
    BiliUser(httpTypeParam = "bili_user", grpcTypeParam = 2),
    //Live grpcTypeParam = 4/5
    //Article grpcTypeParam = 6
}

enum class SearchFilterOrderType(
    val httpOrderParam: String?,
    val grpcOrderParam: SearchByTypeRequest.CategorySort
) {
    ComprehensiveSort(
        httpOrderParam = null,
        grpcOrderParam = SearchByTypeRequest.CategorySort.CATEGORY_SORT_DEFAULT
    ),
    MostClicks(
        httpOrderParam = "click",
        grpcOrderParam = SearchByTypeRequest.CategorySort.CATEGORY_SORT_CLICK_COUNT
    ),
    LatestPublish(
        httpOrderParam = "pubdate",
        grpcOrderParam = SearchByTypeRequest.CategorySort.CATEGORY_SORT_PUBLISH_TIME
    ),
    MostDanmaku(
        httpOrderParam = "dm",
        grpcOrderParam = SearchByTypeRequest.CategorySort.UNRECOGNIZED
    ),
    MostFavorites(
        httpOrderParam = "stow",
        grpcOrderParam = SearchByTypeRequest.CategorySort.UNRECOGNIZED
    ),
    MostComment(
        httpOrderParam = null,
        grpcOrderParam = SearchByTypeRequest.CategorySort.CATEGORY_SORT_COMMENT_COUNT
    ),
    MostLikes(
        httpOrderParam = null,
        grpcOrderParam = SearchByTypeRequest.CategorySort.CATEGORY_SORT_LIKE_COUNT
    );

    companion object {
        val webFilters =
            listOf(ComprehensiveSort, MostClicks, LatestPublish, MostDanmaku, MostFavorites)
        val allFilters =
            listOf(ComprehensiveSort, MostClicks, LatestPublish, MostComment, MostLikes)
    }
}

enum class SearchFilterDuration(
    val httpDurationParam: Int?,
    //val grpcOrderParam: SearchByTypeRequest.
) {
    All(null),
    LessThan10Minutes(1),
    Between10And30Minutes(2),
    Between30And60Minutes(3),
    MoreThan60Minutes(4);
}

data class SearchTypeResult(
    val videos: List<Video> = emptyList(),
    val pgcs: List<Pgc> = emptyList(),
    val users: List<User> = emptyList(),
    val page: SearchTypePage
) {
    companion object {
        fun fromSearchTypeResult(result: dev.aaa1115910.biliapi.http.entity.search.SearchResultData): SearchTypeResult {
            return when (result.searchTypeResults.first()) {
                is dev.aaa1115910.biliapi.http.entity.search.SearchVideoResult -> {
                    SearchTypeResult(
                        videos = result.searchTypeResults.map { Video.fromSearchVideoResult(it as dev.aaa1115910.biliapi.http.entity.search.SearchVideoResult) },
                        page = SearchTypePage(nextPageForWeb = result.page + 1)
                    )
                }

                is dev.aaa1115910.biliapi.http.entity.search.SearchMediaResult -> {
                    SearchTypeResult(
                        pgcs = result.searchTypeResults.map { Pgc.fromSearchPgcResult(it as dev.aaa1115910.biliapi.http.entity.search.SearchMediaResult) },
                        page = SearchTypePage(nextPageForWeb = result.page + 1)
                    )
                }

                is dev.aaa1115910.biliapi.http.entity.search.SearchBiliUserResult -> {
                    SearchTypeResult(
                        users = result.searchTypeResults.map { User.fromSearchUserResult(it as dev.aaa1115910.biliapi.http.entity.search.SearchBiliUserResult) },
                        page = SearchTypePage(nextPageForWeb = result.page + 1)
                    )
                }

                else -> {
                    SearchTypeResult(page = SearchTypePage(nextPageForWeb = result.page + 1))
                }
            }
        }

        fun fromSearchTypeResult(result: bilibili.polymer.app.search.v1.SearchByTypeResponse): SearchTypeResult {
            return when (result.itemsList.firstOrNull()?.cardItemCase) {
                bilibili.polymer.app.search.v1.Item.CardItemCase.AV -> {
                    SearchTypeResult(
                        videos = result.itemsList.map { Video.fromSearchVideoCard(it) },
                        page = SearchTypePage(nextPageForApp = result.pagination.next)
                    )
                }

                bilibili.polymer.app.search.v1.Item.CardItemCase.BANGUMI -> {
                    SearchTypeResult(
                        pgcs = result.itemsList.map { Pgc.fromSearchPgcCard(it) },
                        page = SearchTypePage(nextPageForApp = result.pagination.next)
                    )
                }

                bilibili.polymer.app.search.v1.Item.CardItemCase.AUTHOR -> {
                    SearchTypeResult(
                        users = result.itemsList.map { User.fromSearchUserCard(it) },
                        page = SearchTypePage(nextPageForApp = result.pagination.next)
                    )
                }

                else -> {
                    SearchTypeResult(page = SearchTypePage(nextPageForApp = result.pagination.next))
                }
            }
        }
    }

    interface SearchTypeResultItem

    data class Video(
        val aid: Int,
        val bvid: String,
        val title: String,
        val cover: String,
        val author: String,
        val duration: Int,
        val play: Int,
        val danmaku: Int
    ) : SearchTypeResultItem {
        companion object {
            fun fromSearchVideoResult(video: dev.aaa1115910.biliapi.http.entity.search.SearchVideoResult) =
                Video(
                    aid = video.aid,
                    bvid = video.bvid,
                    title = video.title,
                    cover = "https:${video.pic}",
                    author = video.author,
                    duration = convertStringTimeToSeconds(video.duration),
                    play = video.play,
                    danmaku = video.danmaku
                )

            fun fromSearchVideoCard(video: bilibili.polymer.app.search.v1.Item) =
                Video(
                    aid = video.param.toInt(),
                    bvid = video.av.share.video.bvid,
                    title = video.av.title,
                    cover = video.av.cover,
                    author = video.av.author,
                    duration = convertStringTimeToSeconds(video.av.duration),
                    play = video.av.play,
                    danmaku = video.av.danmaku
                )
        }
    }

    data class Pgc(
        val title: String,
        val cover: String,
        val star: Float,
        val seasonId: Int
    ) : SearchTypeResultItem {
        companion object {
            fun fromSearchPgcResult(pgc: dev.aaa1115910.biliapi.http.entity.search.SearchMediaResult) =
                Pgc(
                    title = pgc.title,
                    cover = pgc.cover,
                    star = pgc.mediaScore.score,
                    seasonId = pgc.seasonId
                )

            fun fromSearchPgcCard(pgc: bilibili.polymer.app.search.v1.Item) =
                Pgc(
                    title = pgc.bangumi.title,
                    cover = pgc.bangumi.cover,
                    star = pgc.bangumi.rating.toFloat(),
                    seasonId = pgc.bangumi.seasonId.toInt()
                )
        }
    }

    data class User(
        val mid: Long,
        val name: String,
        val avatar: String,
        val sign: String
    ) : SearchTypeResultItem {
        companion object {
            fun fromSearchUserResult(user: dev.aaa1115910.biliapi.http.entity.search.SearchBiliUserResult) =
                User(
                    mid = user.mid,
                    name = user.uname,
                    avatar = "https:${user.upic}",
                    sign = user.usign
                )

            fun fromSearchUserCard(user: bilibili.polymer.app.search.v1.Item) =
                User(
                    mid = user.param.toLong(),
                    name = user.author.title,
                    avatar = user.author.cover,
                    sign = user.author.sign
                )
        }
    }
}

private fun convertStringTimeToSeconds(time: String): Int {
    val parts = time.split(":")
    val hours = if (parts.size == 3) parts[0].toInt() else 0
    val minutes = parts[parts.size - 2].toInt()
    val seconds = parts[parts.size - 1].toInt()
    return (hours * 3600) + (minutes * 60) + seconds
}