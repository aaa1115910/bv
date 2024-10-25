package dev.aaa1115910.biliapi.entity.pgc

import dev.aaa1115910.biliapi.http.SeasonIndexType

data class PgcFeedData(
    var hasNext: Boolean,
    var cursor: Int,
    var items: List<FeedItem> = emptyList(),
    var ranks: List<FeedRank> = emptyList()
) {
    companion object {
        fun fromPgcFeedData(data: dev.aaa1115910.biliapi.http.entity.pgc.PgcFeedData): PgcFeedData {
            return PgcFeedData(
                hasNext = data.hasNext,
                cursor = data.coursor,
                items = data.items.map { FeedItem.fromFeedSubItem(it) },
                ranks = emptyList()
            )
        }

        fun fromPgcFeedData(data: dev.aaa1115910.biliapi.http.entity.pgc.PgcFeedV3Data): PgcFeedData {
            val itemsList = data.items.find { it.subItems.first().cardStyle == "v_card" }
            val ranksList = data.items.find { it.subItems.first().cardStyle == "rank" }
            return PgcFeedData(
                hasNext = data.hasNext,
                cursor = data.coursor,
                items = itemsList?.subItems?.map { FeedItem.fromFeedSubItem(it) } ?: emptyList(),
                ranks = ranksList?.subItems?.map { FeedRank.fromFeedSubItem(it) } ?: emptyList()
            )
        }
    }

    data class FeedItem(
        var cover: String,
        var title: String,
        var subTitle: String,
        var seasonId: Int,
        var episodeId: Int,
        var seasonType: SeasonIndexType,
        var rating: String
    ) {
        companion object {
            fun fromFeedSubItem(feedSubItem: dev.aaa1115910.biliapi.http.entity.pgc.PgcFeedData.FeedSubItem): FeedItem {
                return FeedItem(
                    cover = feedSubItem.cover,
                    title = feedSubItem.title,
                    subTitle = feedSubItem.subTitle,
                    seasonId = feedSubItem.seasonId!!,
                    episodeId = feedSubItem.episodeId,
                    seasonType = SeasonIndexType.fromId(feedSubItem.seasonType!!),
                    rating = feedSubItem.rating ?: "0"
                )
            }

            fun fromFeedSubItem(feedSubItem: dev.aaa1115910.biliapi.http.entity.pgc.PgcFeedV3Data.FeedItem.FeedSubItem): FeedItem {
                return FeedItem(
                    cover = feedSubItem.cover,
                    title = feedSubItem.title,
                    subTitle = feedSubItem.subTitle,
                    seasonId = feedSubItem.seasonId!!,
                    episodeId = feedSubItem.episodeId ?: feedSubItem.inline!!.epId,
                    seasonType = SeasonIndexType.fromId(feedSubItem.seasonType!!),
                    rating = feedSubItem.rating ?: "0"
                )
            }
        }
    }

    data class FeedRank(
        var cover: String,
        var title: String,
        var subTitle: String,
        var items: List<FeedItem>
    ) {
        companion object {
            fun fromFeedSubItem(feedSubItem: dev.aaa1115910.biliapi.http.entity.pgc.PgcFeedV3Data.FeedItem.FeedSubItem): FeedRank {
                return FeedRank(
                    cover = feedSubItem.cover,
                    title = feedSubItem.title,
                    subTitle = feedSubItem.subTitle,
                    items = feedSubItem.subItems?.map { FeedItem.fromFeedSubItem(it) }
                        ?: emptyList()
                )
            }
        }
    }
}

