package dev.aaa1115910.biliapi.repositories

import bilibili.main.community.reply.v1.Mode
import bilibili.main.community.reply.v1.ReplyGrpcKt
import bilibili.main.community.reply.v1.detailListReq
import bilibili.main.community.reply.v1.mainListReq
import bilibili.pagination.feedPagination
import dev.aaa1115910.biliapi.entity.ApiType
import dev.aaa1115910.biliapi.entity.reply.CommentPage
import dev.aaa1115910.biliapi.entity.reply.CommentRepliesData
import dev.aaa1115910.biliapi.entity.reply.CommentReplyPage
import dev.aaa1115910.biliapi.entity.reply.CommentSort
import dev.aaa1115910.biliapi.entity.reply.CommentsData
import dev.aaa1115910.biliapi.grpc.utils.handleGrpcException
import dev.aaa1115910.biliapi.http.BiliHttpApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class CommentRepository(
    private val authRepository: AuthRepository,
    private val channelRepository: ChannelRepository
) {
    private val replyStub
        get() = runCatching {
            ReplyGrpcKt.ReplyCoroutineStub(channelRepository.defaultChannel!!)
        }.getOrNull()

    suspend fun getComments(
        id: Long,
        type: Long,
        sort: CommentSort = CommentSort.Hot,
        page: CommentPage = CommentPage(),
        preferApiType: ApiType = ApiType.Web
    ): CommentsData {
        when (preferApiType) {
            ApiType.Web -> {
                val webComments = BiliHttpApi.getComments(
                    oid = id,
                    type = type,
                    mode = sort.param,
                    paginationStr = Json.encodeToString(mapOf("offset" to page.nextWebPage)),
                    sessData = authRepository.sessionData ?: "",
                    buvid3 = authRepository.buvid3 ?: ""
                ).getResponseData()
                return CommentsData.fromCommentData(webComments)
            }

            ApiType.App -> {
                runCatching {
                    val appComments = replyStub?.mainList(
                        mainListReq {
                            this.oid = id.toLong()
                            this.type = type.toLong()
                            mode = when (sort) {
                                CommentSort.Hot -> Mode.MAIN_LIST_HOT
                                CommentSort.HotAndTime -> Mode.DEFAULT
                                CommentSort.Time -> Mode.MAIN_LIST_TIME
                            }
                            pagination = feedPagination {
                                offset = page.nextAppPage
                            }
                        }
                    ) ?: throw IllegalStateException("Reply stub is not initialized")
                    return CommentsData.fromMainListReply(appComments)
                }.onFailure {
                    handleGrpcException(it)
                }.getOrThrow()
            }
        }
    }

    suspend fun getCommentReplies(
        rpid: Long,
        type: Long,
        commentId: Long,
        page: CommentReplyPage = CommentReplyPage(),
        sort: CommentSort = CommentSort.Hot,
        preferApiType: ApiType = ApiType.Web
    ): CommentRepliesData {
        when (preferApiType) {
            ApiType.Web -> {
                val webReplies = BiliHttpApi.getCommentReplies(
                    oid = commentId,
                    type = type,
                    root = rpid,
                    pageSize = 20,
                    pageNumber = page.nextWebPage,
                ).getResponseData()
                return CommentRepliesData.fromCommentReplyData(webReplies)
            }

            ApiType.App -> {
                val appReplies = replyStub?.detailList(
                    detailListReq {
                        this.oid = commentId
                        this.type = type
                        root = rpid
                        mode = when (sort) {
                            CommentSort.Hot -> Mode.MAIN_LIST_HOT
                            CommentSort.HotAndTime -> Mode.DEFAULT
                            CommentSort.Time -> Mode.MAIN_LIST_TIME
                        }
                        pagination = feedPagination {
                            offset = page.nextAppPage
                        }
                    }
                ) ?: throw IllegalStateException("Reply stub is not initialized")
                return CommentRepliesData.fromCommentReplyList(appReplies)
            }
        }
    }
}