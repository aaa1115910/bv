package dev.aaa1115910.biliapi.entity.season

data class IndexResultData(
    val list: List<IndexResultItem>,
    val nextPage: IndexResultPage
) {
    companion object {
        fun fromIndexResultData(data: dev.aaa1115910.biliapi.http.entity.index.IndexResultData) =
            IndexResultData(
                list = data.list.map { IndexResultItem.fromIndexResultItem(it) },
                nextPage = IndexResultPage(
                    nextPage = (data.num + 1).takeIf { data.hasNext == 1 } ?: -1,
                    hasNext = data.hasNext == 1
                )
            )
    }
}

data class IndexResultPage(
    val nextPage: Int = 1,
    val hasNext: Boolean = true
)

data class IndexResultItem(
    val title: String,
    val subTitle: String,
    val cover: String,
    val score: String,
    val badge: Badge?,
    val indexShow: String,
    val seasonId: Int
) {
    companion object {
        fun fromIndexResultItem(item: dev.aaa1115910.biliapi.http.entity.index.IndexResultData.IndexResultItem): IndexResultItem {
            return IndexResultItem(
                title = item.title,
                subTitle = item.subTitle,
                cover = item.cover,
                score = item.score,
                badge = Badge(
                    text = item.badgeInfo.text,
                    bgColor = item.badgeInfo.bgColor,
                    bgColorNight = item.badgeInfo.bgColorNight
                ).takeIf { item.badgeInfo.text.isNotEmpty() },
                indexShow = item.indexShow,
                seasonId = item.seasonId
            )
        }
    }

    data class Badge(
        val text: String,
        val bgColor: String,
        val bgColorNight: String
    )
}
