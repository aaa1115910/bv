package dev.aaa1115910.biliapi.entity.season

data class FollowingSeasonData(
    val list: List<FollowingSeason>,
    val total: Int
)

data class FollowingSeason(
    val seasonId: Int,
    val title: String,
    val cover: String
) {
    companion object {
        fun fromFollowingSeason(season: dev.aaa1115910.biliapi.http.entity.season.WebFollowingSeason) =
            FollowingSeason(
                seasonId = season.seasonId,
                title = season.title,
                cover = season.cover
            )

        fun fromFollowingSeason(season: dev.aaa1115910.biliapi.http.entity.season.AppFollowingSeason) =
            FollowingSeason(
                seasonId = season.seasonId,
                title = season.title,
                cover = season.cover
            )
    }
}

enum class FollowingSeasonType(val id: Int, val paramName: String) {
    Bangumi(id = 1, paramName = "bangumi"),
    Cinema(id = 2, paramName = "cinema")
}

enum class FollowingSeasonStatus(val id: Int) {
    All(id = 0), Want(id = 1), Watching(id = 2), Watched(id = 3)
}
