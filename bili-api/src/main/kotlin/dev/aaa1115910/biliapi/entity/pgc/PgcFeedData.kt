package dev.aaa1115910.biliapi.entity.pgc

data class PgcFeedData(
    var hasNext: Boolean,
    var cursor: Int,
    var items: List<PgcItem> = emptyList(),
    var ranks: List<FeedRank> = emptyList()
) {
    companion object {
        fun fromPgcFeedData(data: dev.aaa1115910.biliapi.http.entity.pgc.PgcFeedData): PgcFeedData {
            return PgcFeedData(
                hasNext = data.hasNext,
                cursor = data.coursor,
                items = data.items.map { PgcItem.fromFeedSubItem(it) },
                ranks = emptyList()
            )
        }

        fun fromPgcFeedData(data: dev.aaa1115910.biliapi.http.entity.pgc.PgcFeedV3Data): PgcFeedData {
            val itemsList = data.items.find { it.subItems.first().cardStyle == "v_card" }
            val ranksList = data.items.find { it.subItems.first().cardStyle == "rank" }
            return PgcFeedData(
                hasNext = data.hasNext,
                cursor = data.coursor,
                items = itemsList?.subItems?.map { PgcItem.fromFeedSubItem(it) } ?: emptyList(),
                ranks = ranksList?.subItems?.map { FeedRank.fromFeedSubItem(it) } ?: emptyList()
            )
        }
    }

    data class FeedRank(
        var cover: String,
        var title: String,
        var subTitle: String,
        var items: List<PgcItem>
    ) {
        companion object {
            fun fromFeedSubItem(feedSubItem: dev.aaa1115910.biliapi.http.entity.pgc.PgcFeedV3Data.FeedItem.FeedSubItem): FeedRank {
                return FeedRank(
                    cover = feedSubItem.cover,
                    title = feedSubItem.title,
                    subTitle = feedSubItem.subTitle,
                    items = feedSubItem.subItems?.map { PgcItem.fromFeedSubItem(it) }
                        ?: emptyList()
                )
            }
        }
    }
}

