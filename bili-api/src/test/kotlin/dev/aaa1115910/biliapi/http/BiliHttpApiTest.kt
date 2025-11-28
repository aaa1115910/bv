package dev.aaa1115910.biliapi.http

import dev.aaa1115910.biliapi.entity.pgc.PgcType
import dev.aaa1115910.biliapi.entity.season.FollowingSeasonStatus
import dev.aaa1115910.biliapi.entity.season.FollowingSeasonType
import dev.aaa1115910.biliapi.http.entity.user.FollowAction
import dev.aaa1115910.biliapi.http.entity.user.FollowActionSource
import dev.aaa1115910.biliapi.http.util.generateBuvid
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Paths
import java.util.Properties

internal class BiliHttpApiTest {

    companion object {
        private val localProperties = Properties().apply {
            val path = Paths.get("../local.properties").toAbsolutePath().toString()
            load(File(path).bufferedReader())
        }
        val SESSDATA: String =
            runCatching { localProperties.getProperty("test.sessdata") }.getOrNull() ?: ""
        val BILI_JCT: String =
            runCatching { localProperties.getProperty("test.bili_jct") }.getOrNull() ?: ""
        val UID: Long =
            runCatching { localProperties.getProperty("test.uid") }.getOrNull()?.toLongOrNull() ?: 2
        val ACCESS_TOKEN: String =
            runCatching { localProperties.getProperty("test.access_token") }.getOrNull() ?: ""
        val BUVID: String =
            runCatching { localProperties.getProperty("test.buvid") }.getOrNull() ?: ""
    }

    @Test
    fun `println sessdata and bili_jct`() {
        println("SESSDATA: $SESSDATA")
        println("BILI_JCT: $BILI_JCT")
    }

    @Test
    fun `get popular videos`() {
        assertDoesNotThrow {
            runBlocking {
                val response = BiliHttpApi.getPopularVideoData()
                println(response)
            }
        }
    }

    @Test
    fun `get video info`() {
        assertDoesNotThrow {
            runBlocking {
                val response = BiliHttpApi.getVideoInfo(av = 170001)
                println(response)
            }
        }
    }

    @Test
    fun `get video info which is ugc season`() {
        assertDoesNotThrow {
            runBlocking {
                val response = BiliHttpApi.getVideoInfo(av = 433139956)
                println(response)
            }
        }
    }

    @Test
    fun `get video play url`() {
        assertDoesNotThrow {
            runBlocking {
                val response = BiliHttpApi.getVideoPlayUrl(
                    av = 648092492,
                    cid = 903675075,
                    fnval = 4048,
                    qn = 127,
                    sessData = SESSDATA,
                    dedeUserID = UID
                )
                println(response)
            }
        }
    }

    @Test
    fun `get pgc video play url`() {
        runBlocking {
            println(
                BiliHttpApi.getPgcVideoPlayUrl(
                    av = 672676070,
                    cid = 331748015,
                    fnval = 4048,
                    qn = 127,
                    sessData = SESSDATA,
                    dedeUserID = UID
                )
            )
        }
    }

    @Test
    fun `get pgc video play url v2`() {
        runBlocking {
            println(
                BiliHttpApi.getPgcVideoPlayUrlV2(
                    av = 672676070,
                    cid = 331748015,
                    fnval = 4048,
                    qn = 127,
                    sessData = SESSDATA
                )
            )
        }
    }

    @Test
    fun `get video danmaku from xml`() {
        assertDoesNotThrow {
            runBlocking {
                val response = BiliHttpApi.getDanmakuXml(cid = 903675075)
                println(response)
            }
        }
    }

    @Test
    fun `get dynamic list with type all`() {
        assertDoesNotThrow {
            runBlocking {
                val response = BiliHttpApi.getDynamicList(
                    type = "article",
                    sessData = SESSDATA
                )
                println(response)
            }
        }
    }

    @Test
    fun `get user info from Mr_He`() {
        assertDoesNotThrow {
            runBlocking {
                val response = BiliHttpApi.getUserInfo(
                    uid = 163637592,
                    sessData = SESSDATA
                )
                println(response)
            }
        }
    }

    @Test
    fun `get user card info from Mr_He`() {
        assertDoesNotThrow {
            runBlocking {
                val response = BiliHttpApi.getUserCardInfo(
                    uid = 163637592,
                    photo = true,
                    sessData = SESSDATA
                )
                println(response)
            }
        }
    }

    @Test
    fun `get self user info`() {
        assertDoesNotThrow {
            runBlocking {
                val response = BiliHttpApi.getUserSelfInfo(
                    sessData = SESSDATA
                )
                println(response)
            }
        }
    }

    @Test
    fun `get histories`() {
        assertDoesNotThrow {
            runBlocking {
                val response = BiliHttpApi.getHistories(
                    viewAt = 0,
                    sessData = SESSDATA
                )
                println(response)
            }
        }
    }

    @Test
    fun `get related vidoes`() {
        assertDoesNotThrow {
            runBlocking {
                val response = BiliHttpApi.getRelatedVideos(
                    avid = 170001
                )
                println(response)
            }
        }
    }

    @Test
    fun `get favorite folder metadata from id 2333`() {
        runBlocking {
            val response = BiliHttpApi.getFavoriteFolderInfo(
                mediaId = 2333
            )
            println(response)
        }
    }

    @Test
    fun `get all favorite folders metadata`() {
        runBlocking {
            val response = BiliHttpApi.getAllFavoriteFoldersInfo(
                mid = 2333,
                sessData = SESSDATA
            )
            println(response)
        }
    }

    @Test
    fun `get all favorite item ids`() {
        runBlocking {
            val response = BiliHttpApi.getFavoriteIdList(
                mediaId = 2333,
                sessData = SESSDATA
            )
            println(response)
        }
    }

    @Test
    fun `get favorite list`() {
        runBlocking {
            val response = BiliHttpApi.getFavoriteList(
                mediaId = 2333,
                sessData = SESSDATA
            )
            println(response)
        }
    }

    @Test
    fun `send heartbeat`() {
        runBlocking {
            val response = BiliHttpApi.sendHeartbeat(
                avid = 170001,
                cid = 280468,
                playedTime = 23,
                sessData = SESSDATA
            )
            println(response)
        }
    }

    @Test
    fun `get video more info`() {
        runBlocking {
            val response = BiliHttpApi.getVideoMoreInfo(
                avid = 170001,
                cid = 279786,
                sessData = SESSDATA,
                buvid3 = generateBuvid()
            ).getResponseData()
            println("lastPlayTime: ${response.lastPlayTime}")
            println("lastPlayCid: ${response.lastPlayCid}")
        }
    }

    @Test
    fun `send video like`() {
        runBlocking {
            println(
                BiliHttpApi.sendVideoLike(
                    avid = 170001,
                    like = true,
                    csrf = BILI_JCT,
                    sessData = SESSDATA
                )
            )
        }
    }

    @Test
    fun `check video is liked`() {
        runBlocking {
            println(
                BiliHttpApi.checkVideoLiked(
                    avid = 170001,
                    sessData = SESSDATA
                )
            )
        }
    }

    @Test
    fun `send video coin`() {
        runBlocking {
            println(
                BiliHttpApi.sendVideoCoin(
                    avid = 170001,
                    csrf = BILI_JCT,
                    sessData = SESSDATA
                )
            )
        }
    }

    @Test
    fun `check video coin`() {
        runBlocking {
            println(
                BiliHttpApi.checkVideoSentCoin(
                    avid = 170001,
                    sessData = SESSDATA
                )
            )
        }
    }

    @Test
    fun `add video to favorite`() {
        runBlocking {
            println(
                BiliHttpApi.setVideoToFavorite(
                    avid = 170001,
                    addMediaIds = listOf(46912037),
                    csrf = BILI_JCT,
                    sessData = SESSDATA
                )
            )
        }
    }

    @Test
    fun `delete video from favorite`() {
        runBlocking {
            println(
                BiliHttpApi.setVideoToFavorite(
                    avid = 170001,
                    delMediaIds = listOf(46912037),
                    csrf = BILI_JCT,
                    sessData = SESSDATA
                )
            )
        }
    }

    @Test
    fun `check is video in favorite`() {
        runBlocking {
            println(
                BiliHttpApi.checkVideoFavoured(
                    avid = 170001,
                    sessData = SESSDATA
                )
            )
        }
    }

    @Test
    fun `get user space videos`() = runBlocking {
        println(
            BiliHttpApi.getWebUserSpaceVideos(
                mid = 1,
                sessData = SESSDATA
            )
        )
    }

    @Test
    fun `get web season info data`() {
        runBlocking {
            println(
                BiliHttpApi.getWebSeasonInfo(
                    epId = 705917
                )
            )
        }
    }

    @Test
    fun `get app season info data`() = runBlocking {
        println(
            BiliHttpApi.getAppSeasonInfo(
                epId = 752900,
                seasonId = 45303,
                mobiApp = "android_hd",
                accessKey = ACCESS_TOKEN
            )
        )
    }

    @Test
    fun `get user season status data`() = runBlocking {
        println(
            BiliHttpApi.getSeasonUserStatus(
                seasonId = 44152,
                sessData = SESSDATA
            )
        )
    }

    @Test
    fun `get video tags`() {
        runBlocking {
            println(
                BiliHttpApi.getVideoTags(
                    avid = 170001
                )
            )
        }
    }

    @Test
    fun `get tag detail`() = runBlocking {
        println(
            BiliHttpApi.getTagDetail(
                tagId = 6020278,
                pageNumber = 1,
                pageSize = 20
            )
        )
    }

    @Test
    fun `get tag popular videos`() = runBlocking {
        println(
            BiliHttpApi.getTagTopVideos(
                tagId = 6020278,
                pageNumber = 1,
                pageSize = 20
            )
        )
    }

    @Test
    fun `get web timeline`() = runBlocking {
        val result = BiliHttpApi.getTimeline(
            type = 1,
            before = 7,
            after = 7
        )
        println(result)
    }

    @Test
    fun `get app timeline`() = runBlocking {
        val result = BiliHttpApi.getTimeline(
            filterType = 0
        )
        println(result)
    }

    @Test
    fun `get follow list`() {
        runBlocking {
            println(
                BiliHttpApi.getUserFollow(
                    mid = 3066511,
                    sessData = SESSDATA
                )
            )
        }
    }

    @Test
    fun `add follow`() {
        runBlocking {
            println(
                BiliHttpApi.modifyFollow(
                    mid = 3066511,
                    action = FollowAction.AddFollow,
                    actionSource = FollowActionSource.Space,
                    csrf = BILI_JCT,
                    sessData = SESSDATA
                )
            )
        }
    }

    @Test
    fun `delete follow`() {
        runBlocking {
            println(
                BiliHttpApi.modifyFollow(
                    mid = 3066511,
                    action = FollowAction.DelFollow,
                    actionSource = FollowActionSource.Space,
                    csrf = BILI_JCT,
                    sessData = SESSDATA
                )
            )
        }
    }

    @Test
    fun `get user relations`() {
        runBlocking {
            println(
                BiliHttpApi.getRelations(
                    mid = 11336264,
                    sessData = SESSDATA
                )
            )
        }
    }

    @Test
    fun `get user relation stat`() {
        runBlocking {
            println(
                BiliHttpApi.getRelationStat(
                    mid = 11336264
                )
            )
        }
    }

    @Test
    fun `get web search hot words`() = runBlocking {
        println(BiliHttpApi.getWebSearchSquare())
    }

    @Test
    fun `get app search hot words`() = runBlocking {
        println(BiliHttpApi.getAppSearchSquare())
    }

    @Test
    fun `get app search trending ranking`() = runBlocking {
        println(BiliHttpApi.getSearchTrendRank())
    }

    @Test
    fun `get search keyword suggests`() {
        runBlocking {
            println(
                BiliHttpApi.getKeywordSuggest(
                    term = "和奥托一起泡温泉",
                    buvid = BUVID
                )
            )
        }
    }

    @Test
    fun `search all`() {
        runBlocking {
            println(
                BiliHttpApi.searchAll(
                    keyword = "007"
                )
            )
        }
    }

    @Test
    fun `search type`() {
        val types =
            listOf("video", "media_bangumi", "media_ft", "article", "topic", "bili_user")
        runBlocking {
            types.forEach { type ->
                println(
                    BiliHttpApi.searchType(
                        keyword = "007",
                        type = type
                    )
                )
            }
        }
    }

    @Test
    fun `get web initial state data`() {
        runBlocking {
            PgcType.entries.forEach { pgcType ->
                println("type: ${pgcType.name}")
                println(
                    BiliHttpApi.getPgcWebInitialStateData(pgcType)
                        .toString().replace("\n", "")
                )
            }
        }
    }

    @Test
    fun `get pgc feed data`() {
        runBlocking {
            PgcType.entries.forEach { pgcType ->
                println("type: ${pgcType.name}")
                when (pgcType) {
                    PgcType.Anime, PgcType.GuoChuang ->
                        println(
                            BiliHttpApi.getPgcFeedV3(name = pgcType.name.lowercase())
                                .toString().replace("\n", "")
                        )

                    PgcType.Tv, PgcType.Movie, PgcType.Documentary, PgcType.Variety ->
                        println(
                            BiliHttpApi.getPgcFeed(name = pgcType.name.lowercase())
                                .toString().replace("\n", "")
                        )
                }
            }
        }
    }

    @Test
    fun `get web following season data`() {
        runBlocking {
            for (followingSeasonType in FollowingSeasonType.values()) {
                for (followingSeasonStatus in FollowingSeasonStatus.values()) {
                    println("type: $followingSeasonType, status: $followingSeasonStatus: ")
                    println(
                        BiliHttpApi.getFollowingSeasons(
                            type = followingSeasonType.id,
                            status = followingSeasonStatus.id,
                            pageNumber = 1,
                            pageSize = 1,
                            mid = UID,
                            sessData = SESSDATA
                        )
                    )
                }
            }
        }
    }

    @Test
    fun `get app following season data`() {
        runBlocking {
            for (followingSeasonType in FollowingSeasonType.values()) {
                for (followingSeasonStatus in FollowingSeasonStatus.values()) {
                    println("type: $followingSeasonType, status: $followingSeasonStatus: ")
                    println(
                        BiliHttpApi.getFollowingSeasons(
                            type = followingSeasonType.paramName,
                            status = followingSeasonStatus.id,
                            pageNumber = 1,
                            pageSize = 1,
                            build = 6830300,
                            accessKey = ACCESS_TOKEN
                        )
                    )
                }
            }
        }
    }

    @Test
    fun `get web homepage recommend items`() = runBlocking {
        val result = BiliHttpApi.getFeedRcmd()
        println(result)
    }

    @Test
    fun `get app homepage recommend items`() = runBlocking {
        val result = BiliHttpApi.getFeedIndex()
        println(result)
    }

    @Test
    fun `get anime index`() = runBlocking {
        val result = BiliHttpApi.seasonIndexAnimeResult()
        println(result.data?.list?.map { it.title })
    }

    @Test
    fun `get guochuang index`() = runBlocking {
        val result = BiliHttpApi.seasonIndexGuochuangResult()
        println(result.data?.list?.map { it.title })
    }

    @Test
    fun `get movie index`() = runBlocking {
        val result = BiliHttpApi.seasonIndexMovieResult()
        println(result.data?.list?.map { it.title })
    }

    @Test
    fun `get tv index`() = runBlocking {
        val result = BiliHttpApi.seasonIndexTvResult()
        println(result.data?.list?.map { it.title })
    }

    @Test
    fun `get variety season index`() = runBlocking {
        val result = BiliHttpApi.seasonIndexVarietyResult()
        println(result.data?.list?.map { it.title })
    }

    @Test
    fun `get documentary season index`() = runBlocking {
        val result = BiliHttpApi.seasonIndexDocumentaryResult()
        println(result.data?.list?.map { it.title })
    }

    @Test
    fun `get web video shot`() = runBlocking {
        val result = BiliHttpApi.getWebVideoShot(aid = 170001)
        println(result)
    }

    @Test
    fun `get app video shot`() = runBlocking {
        val result = BiliHttpApi.getAppVideoShot(aid = 170001, cid = 279786)
        println(result)
    }

    @Test
    fun `get app region dynamic`() = runBlocking {
        val rids = listOf(
            1, 13, 167, 3, 129, 4, 36, 188, 234, 223, 160,
            211, 217, 119, 155, 202, 5, 181, 177, 23, 11
        )
        rids
            .shuffled()
            .forEach { rid ->
                println("rid $rid:")
                val result = BiliHttpApi.getRegionDynamic(
                    rid = rid,
                    accessKey = ACCESS_TOKEN
                )
                println(result)
                delay((800L..2000L).random())
            }
    }


    @Test
    fun `get app region dynamic list`() = runBlocking {
        val rids = listOf(
            1, 13, 167, 3, 129, 4, 36, 188, 234, 223, 160,
            211, 217, 119, 155, 202, 5, 181, 177, 23, 11
        )
        rids
            .shuffled()
            .forEach { rid ->
                println("rid $rid:")
                val result = BiliHttpApi.getRegionDynamicList(
                    rid = rid,
                    accessKey = ACCESS_TOKEN
                )
                println(result)
                delay((800L..2000L).random())
            }
    }

    @Test
    fun `get locs`() = runBlocking {
        val locIds = listOf(
            4973, 4991, 5004, 4979, 4985, 5008, 5007, 4997,
            4998, 5005, 5002, 5001, 5000, 5006, 4999, 5003
        )

        locIds.chunked(3).forEach { locs ->
            println("${locs.joinToString(",")}:")
            val result = BiliHttpApi.getLocs(
                ids = locs
            )
            println(result)
            delay((800L..2000L).random())
        }
    }
}