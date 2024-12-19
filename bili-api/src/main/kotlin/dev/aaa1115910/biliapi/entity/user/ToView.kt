package dev.aaa1115910.biliapi.entity.user

import bilibili.app.interfaces.v1.CursorItem

//TODO 暂时仅解析 UGC 和 PGC
data class ToViewData(
    val cursor: Long,
    val data: List<ToViewItem>
) {
    companion object {
        fun fromToViewResponse(data: dev.aaa1115910.biliapi.http.entity.toview.ToViewData) =
            ToViewData(
//               cursor = data.cursor.viewAt,
                cursor = 0,
                data = data.list
//                    .filter { it.history.business == "archive" || it.history.business == "pgc" }
                    .map { ToViewItem.fromToViewItem(it) }
            )

        // fun fromToViewResponse(data: bilibili.app.interfaces.v1.CursorV2Reply) = ToViewData(
        //     cursor = data.cursor.max,
        //     data = data.itemsList
        //         .filter { it.cardItemCase == CursorItem.CardItemCase.CARD_UGC || it.cardItemCase == CursorItem.CardItemCase.CARD_OGV }
        //         .map { ToViewItem.fromToViewItem(it) }
        // )
    }
}

data class ToViewItem(
    val oid: Long,
    val bvid: String,
    val cid: Long,
    val kid: Int,
    val epid: Int?,
    val seasonId: Int?,
    val title: String,
    val cover: String,
    val author: String,
    val duration: Int,
    val progress: Int,
    val type: ToViewItemType
) {
    companion object {
        fun fromToViewItem(item: dev.aaa1115910.biliapi.http.entity.toview.ToViewItem) =
            ToViewItem(
                oid = item.aid,
                bvid = item.bvid,
                cid = item.cid,
                kid = 0,
                epid = 0,
                seasonId = null,
                title = item.title,
                cover = item.pic,
                author = item.owner.name,
                duration = item.duration,
                progress = item.progress,
                type = ToViewItemType.Archive
                // type = when (item.history.business) {
                //     "archive" -> HistoryItemType.Archive
                //     "pgc" -> HistoryItemType.Pgc
                //     else -> HistoryItemType.Unknown
                // }
            )

        @Suppress("RemoveRedundantQualifierName")
        fun fromToViewItem(item: bilibili.app.interfaces.v1.CursorItem) = ToViewItem(
            oid = item.oid,
            bvid = when (item.cardItemCase) {
                CursorItem.CardItemCase.CARD_UGC -> item.cardUgc.bvid
                CursorItem.CardItemCase.CARD_OGV -> ""
                else -> ""
            },
            cid = when (item.cardItemCase) {
                CursorItem.CardItemCase.CARD_UGC -> item.cardUgc.cid
                CursorItem.CardItemCase.CARD_OGV -> 0
                else -> 0
            },
            kid = item.kid.toInt(),
            epid = null,
            seasonId = when (item.cardItemCase) {
                CursorItem.CardItemCase.CARD_OGV -> item.kid.toInt()
                else -> null
            },
            title = item.title,
            cover = when (item.cardItemCase) {
                CursorItem.CardItemCase.CARD_UGC -> item.cardUgc.cover
                CursorItem.CardItemCase.CARD_OGV -> item.cardOgv.cover
                else -> ""
            },
            author = when (item.cardItemCase) {
                CursorItem.CardItemCase.CARD_UGC -> item.cardUgc.name
                CursorItem.CardItemCase.CARD_OGV -> ""
                else -> ""
            },
            duration = when (item.cardItemCase) {
                CursorItem.CardItemCase.CARD_UGC -> item.cardUgc.duration.toInt()
                CursorItem.CardItemCase.CARD_OGV -> item.cardOgv.duration.toInt()
                else -> 0
            },
            progress = when (item.cardItemCase) {
                CursorItem.CardItemCase.CARD_UGC -> item.cardUgc.progress.toInt()
                CursorItem.CardItemCase.CARD_OGV -> item.cardOgv.progress.toInt()
                else -> 0
            },
            type = when (item.cardItemCase) {
                CursorItem.CardItemCase.CARD_UGC -> ToViewItemType.Archive
                CursorItem.CardItemCase.CARD_OGV -> ToViewItemType.Pgc
                else -> ToViewItemType.Unknown
            }
        )
    }
}

enum class ToViewItemType {
    Unknown, Archive, Pgc
}